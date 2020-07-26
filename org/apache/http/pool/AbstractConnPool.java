package org.apache.http.pool;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.concurrent.FutureCallback;















































@ThreadSafe
public abstract class AbstractConnPool<T, C, E extends PoolEntry<T, C>>
  implements ConnPool<T, E>, ConnPoolControl<T>
{
  private final Lock lock;
  private final ConnFactory<T, C> connFactory;
  private final Map<T, RouteSpecificPool<T, C, E>> routeToPool;
  private final Set<E> leased;
  private final LinkedList<E> available;
  private final LinkedList<PoolEntryFuture<E>> pending;
  private final Map<T, Integer> maxPerRoute;
  private volatile boolean isShutDown;
  private volatile int defaultMaxPerRoute;
  private volatile int maxTotal;
  
  public AbstractConnPool(ConnFactory<T, C> connFactory, int defaultMaxPerRoute, int maxTotal)
  {
    if (connFactory == null) {
      throw new IllegalArgumentException("Connection factory may not null");
    }
    if (defaultMaxPerRoute <= 0) {
      throw new IllegalArgumentException("Max per route value may not be negative or zero");
    }
    if (maxTotal <= 0) {
      throw new IllegalArgumentException("Max total value may not be negative or zero");
    }
    this.lock = new ReentrantLock();
    this.connFactory = connFactory;
    this.routeToPool = new HashMap();
    this.leased = new HashSet();
    this.available = new LinkedList();
    this.pending = new LinkedList();
    this.maxPerRoute = new HashMap();
    this.defaultMaxPerRoute = defaultMaxPerRoute;
    this.maxTotal = maxTotal;
  }
  

  protected abstract E createEntry(T paramT, C paramC);
  

  public boolean isShutdown()
  {
    return this.isShutDown;
  }
  

  public void shutdown()
    throws IOException
  {
    if (this.isShutDown) {
      return;
    }
    this.isShutDown = true;
    this.lock.lock();
    try {
      for (E entry : this.available) {
        entry.close();
      }
      for (E entry : this.leased) {
        entry.close();
      }
      for (RouteSpecificPool<T, C, E> pool : this.routeToPool.values()) {
        pool.shutdown();
      }
      this.routeToPool.clear();
      this.leased.clear();
      this.available.clear();
    } finally {
      this.lock.unlock();
    }
  }
  
  private RouteSpecificPool<T, C, E> getPool(final T route) {
    RouteSpecificPool<T, C, E> pool = (RouteSpecificPool)this.routeToPool.get(route);
    if (pool == null) {
      pool = new RouteSpecificPool(route)
      {
        protected E createEntry(C conn)
        {
          return AbstractConnPool.this.createEntry(route, conn);
        }
        
      };
      this.routeToPool.put(route, pool);
    }
    return pool;
  }
  







  public Future<E> lease(final T route, final Object state, FutureCallback<E> callback)
  {
    if (route == null) {
      throw new IllegalArgumentException("Route may not be null");
    }
    if (this.isShutDown) {
      throw new IllegalStateException("Connection pool shut down");
    }
    new PoolEntryFuture(this.lock, callback)
    {

      public E getPoolEntry(long timeout, TimeUnit tunit)
        throws InterruptedException, TimeoutException, IOException
      {

        return AbstractConnPool.this.getPoolEntryBlocking(route, state, timeout, tunit, this);
      }
    };
  }
  
















  public Future<E> lease(T route, Object state)
  {
    return lease(route, state, null);
  }
  



  private E getPoolEntryBlocking(T route, Object state, long timeout, TimeUnit tunit, PoolEntryFuture<E> future)
    throws IOException, InterruptedException, TimeoutException
  {
    Date deadline = null;
    if (timeout > 0L) {
      deadline = new Date(System.currentTimeMillis() + tunit.toMillis(timeout));
    }
    

    this.lock.lock();
    try {
      RouteSpecificPool<T, C, E> pool = getPool(route);
      E entry = null;
      while (entry == null) {
        if (this.isShutDown) {
          throw new IllegalStateException("Connection pool shut down");
        }
        for (;;) {
          entry = pool.getFree(state);
          if (entry == null) {
            break;
          }
          if ((!entry.isClosed()) && (!entry.isExpired(System.currentTimeMillis()))) break;
          entry.close();
          this.available.remove(entry);
          pool.free(entry, false);
        }
        


        if (entry != null) {
          this.available.remove(entry);
          this.leased.add(entry);
          return entry;
        }
        

        int maxPerRoute = getMax(route);
        
        int excess = Math.max(0, pool.getAllocatedCount() + 1 - maxPerRoute);
        if (excess > 0) {
          for (int i = 0; i < excess; i++) {
            E lastUsed = pool.getLastUsed();
            if (lastUsed == null) {
              break;
            }
            lastUsed.close();
            this.available.remove(lastUsed);
            pool.remove(lastUsed);
          }
        }
        
        if (pool.getAllocatedCount() < maxPerRoute) {
          int totalUsed = this.leased.size();
          int freeCapacity = Math.max(this.maxTotal - totalUsed, 0);
          if (freeCapacity > 0) {
            int totalAvailable = this.available.size();
            RouteSpecificPool<T, C, E> otherpool; if ((totalAvailable > freeCapacity - 1) && 
              (!this.available.isEmpty())) {
              E lastUsed = (PoolEntry)this.available.removeFirst();
              lastUsed.close();
              otherpool = getPool(lastUsed.getRoute());
              otherpool.remove(lastUsed);
            }
            
            C conn = this.connFactory.create(route);
            entry = pool.add(conn);
            this.leased.add(entry);
            return entry;
          }
        }
        
        boolean success = false;
        try {
          pool.queue(future);
          this.pending.add(future);
          success = future.await(deadline);

        }
        finally
        {

          pool.unqueue(future);
          this.pending.remove(future);
        }
        
        if ((!success) && (deadline != null) && (deadline.getTime() <= System.currentTimeMillis())) {
          break;
        }
      }
      
      throw new TimeoutException("Timeout waiting for connection");
    } finally {
      this.lock.unlock();
    }
  }
  
  private void notifyPending(RouteSpecificPool<T, C, E> pool) {
    PoolEntryFuture<E> future = pool.nextPending();
    if (future != null) {
      this.pending.remove(future);
    } else {
      future = (PoolEntryFuture)this.pending.poll();
    }
    if (future != null) {
      future.wakeup();
    }
  }
  
  public void release(E entry, boolean reusable) {
    this.lock.lock();
    try {
      if (this.leased.remove(entry)) {
        RouteSpecificPool<T, C, E> pool = getPool(entry.getRoute());
        pool.free(entry, reusable);
        if ((reusable) && (!this.isShutDown)) {
          this.available.add(entry);
        } else {
          entry.close();
        }
        notifyPending(pool);
      }
    } finally {
      this.lock.unlock();
    }
  }
  
  private int getMax(T route) {
    Integer v = (Integer)this.maxPerRoute.get(route);
    if (v != null) {
      return v.intValue();
    }
    return this.defaultMaxPerRoute;
  }
  
  public void setMaxTotal(int max)
  {
    if (max <= 0) {
      throw new IllegalArgumentException("Max value may not be negative or zero");
    }
    this.lock.lock();
    try {
      this.maxTotal = max;
    } finally {
      this.lock.unlock();
    }
  }
  
  public int getMaxTotal() {
    this.lock.lock();
    try {
      return this.maxTotal;
    } finally {
      this.lock.unlock();
    }
  }
  
  public void setDefaultMaxPerRoute(int max) {
    if (max <= 0) {
      throw new IllegalArgumentException("Max value may not be negative or zero");
    }
    this.lock.lock();
    try {
      this.defaultMaxPerRoute = max;
    } finally {
      this.lock.unlock();
    }
  }
  
  public int getDefaultMaxPerRoute() {
    this.lock.lock();
    try {
      return this.defaultMaxPerRoute;
    } finally {
      this.lock.unlock();
    }
  }
  
  public void setMaxPerRoute(T route, int max) {
    if (route == null) {
      throw new IllegalArgumentException("Route may not be null");
    }
    if (max <= 0) {
      throw new IllegalArgumentException("Max value may not be negative or zero");
    }
    this.lock.lock();
    try {
      this.maxPerRoute.put(route, Integer.valueOf(max));
    } finally {
      this.lock.unlock();
    }
  }
  
  public int getMaxPerRoute(T route) {
    if (route == null) {
      throw new IllegalArgumentException("Route may not be null");
    }
    this.lock.lock();
    try {
      return getMax(route);
    } finally {
      this.lock.unlock();
    }
  }
  
  public PoolStats getTotalStats() {
    this.lock.lock();
    try {
      return new PoolStats(this.leased.size(), this.pending.size(), this.available.size(), this.maxTotal);

    }
    finally
    {

      this.lock.unlock();
    }
  }
  
  public PoolStats getStats(T route) {
    if (route == null) {
      throw new IllegalArgumentException("Route may not be null");
    }
    this.lock.lock();
    try {
      RouteSpecificPool<T, C, E> pool = getPool(route);
      return new PoolStats(pool.getLeasedCount(), pool.getPendingCount(), pool.getAvailableCount(), getMax(route));

    }
    finally
    {

      this.lock.unlock();
    }
  }
  






  public void closeIdle(long idletime, TimeUnit tunit)
  {
    if (tunit == null) {
      throw new IllegalArgumentException("Time unit must not be null.");
    }
    long time = tunit.toMillis(idletime);
    if (time < 0L) {
      time = 0L;
    }
    long deadline = System.currentTimeMillis() - time;
    this.lock.lock();
    try {
      Iterator<E> it = this.available.iterator();
      while (it.hasNext()) {
        E entry = (PoolEntry)it.next();
        if (entry.getUpdated() <= deadline) {
          entry.close();
          RouteSpecificPool<T, C, E> pool = getPool(entry.getRoute());
          pool.remove(entry);
          it.remove();
          notifyPending(pool);
        }
      }
    } finally {
      this.lock.unlock();
    }
  }
  


  public void closeExpired()
  {
    long now = System.currentTimeMillis();
    this.lock.lock();
    try {
      Iterator<E> it = this.available.iterator();
      while (it.hasNext()) {
        E entry = (PoolEntry)it.next();
        if (entry.isExpired(now)) {
          entry.close();
          RouteSpecificPool<T, C, E> pool = getPool(entry.getRoute());
          pool.remove(entry);
          it.remove();
          notifyPending(pool);
        }
      }
    } finally {
      this.lock.unlock();
    }
  }
  
  public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    buffer.append("[leased: ");
    buffer.append(this.leased);
    buffer.append("][available: ");
    buffer.append(this.available);
    buffer.append("][pending: ");
    buffer.append(this.pending);
    buffer.append("]");
    return buffer.toString();
  }
}
