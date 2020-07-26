package org.apache.http.pool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.apache.http.annotation.NotThreadSafe;




























@NotThreadSafe
abstract class RouteSpecificPool<T, C, E extends PoolEntry<T, C>>
{
  private final T route;
  private final Set<E> leased;
  private final LinkedList<E> available;
  private final LinkedList<PoolEntryFuture<E>> pending;
  
  RouteSpecificPool(T route)
  {
    this.route = route;
    this.leased = new HashSet();
    this.available = new LinkedList();
    this.pending = new LinkedList();
  }
  
  protected abstract E createEntry(C paramC);
  
  public final T getRoute() {
    return this.route;
  }
  
  public int getLeasedCount() {
    return this.leased.size();
  }
  
  public int getPendingCount() {
    return this.pending.size();
  }
  
  public int getAvailableCount() {
    return this.available.size();
  }
  
  public int getAllocatedCount() {
    return this.available.size() + this.leased.size();
  }
  
  public E getFree(Object state) {
    if (!this.available.isEmpty()) {
      if (state != null) {
        Iterator<E> it = this.available.iterator();
        while (it.hasNext()) {
          E entry = (PoolEntry)it.next();
          if (state.equals(entry.getState())) {
            it.remove();
            this.leased.add(entry);
            return entry;
          }
        }
      }
      Iterator<E> it = this.available.iterator();
      while (it.hasNext()) {
        E entry = (PoolEntry)it.next();
        if (entry.getState() == null) {
          it.remove();
          this.leased.add(entry);
          return entry;
        }
      }
    }
    return null;
  }
  
  public E getLastUsed() {
    if (!this.available.isEmpty()) {
      return (PoolEntry)this.available.getFirst();
    }
    return null;
  }
  
  public boolean remove(E entry)
  {
    if (entry == null) {
      throw new IllegalArgumentException("Pool entry may not be null");
    }
    if ((!this.available.remove(entry)) && 
      (!this.leased.remove(entry))) {
      return false;
    }
    
    return true;
  }
  
  public void free(E entry, boolean reusable) {
    if (entry == null) {
      throw new IllegalArgumentException("Pool entry may not be null");
    }
    boolean found = this.leased.remove(entry);
    if (!found) {
      throw new IllegalStateException("Entry " + entry + " has not been leased from this pool");
    }
    
    if (reusable) {
      this.available.add(entry);
    }
  }
  
  public E add(C conn) {
    E entry = createEntry(conn);
    this.leased.add(entry);
    return entry;
  }
  
  public void queue(PoolEntryFuture<E> future) {
    if (future == null) {
      return;
    }
    this.pending.add(future);
  }
  
  public PoolEntryFuture<E> nextPending() {
    return (PoolEntryFuture)this.pending.poll();
  }
  
  public void unqueue(PoolEntryFuture<E> future) {
    if (future == null) {
      return;
    }
    this.pending.remove(future);
  }
  
  public void shutdown() {
    for (PoolEntryFuture<E> future : this.pending) {
      future.cancel(true);
    }
    this.pending.clear();
    for (E entry : this.available) {
      entry.close();
    }
    this.available.clear();
    for (E entry : this.leased) {
      entry.close();
    }
    this.leased.clear();
  }
  
  public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    buffer.append("[route: ");
    buffer.append(this.route);
    buffer.append("][leased: ");
    buffer.append(this.leased.size());
    buffer.append("][available: ");
    buffer.append(this.available.size());
    buffer.append("][pending: ");
    buffer.append(this.pending.size());
    buffer.append("]");
    return buffer.toString();
  }
}
