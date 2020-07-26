package org.apache.http.impl.conn.tsccm;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.GuardedBy;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.IdleConnectionHandler;
























































@Deprecated
public abstract class AbstractConnPool
{
  private final Log log;
  protected final Lock poolLock;
  @GuardedBy("poolLock")
  protected Set<BasicPoolEntry> leasedConnections;
  @GuardedBy("poolLock")
  protected int numConnections;
  protected volatile boolean isShutDown;
  protected Set<BasicPoolEntryRef> issuedConnections;
  protected ReferenceQueue<Object> refQueue;
  protected IdleConnectionHandler idleConnHandler;
  
  protected AbstractConnPool()
  {
    this.log = LogFactory.getLog(getClass());
    this.leasedConnections = new HashSet();
    this.idleConnHandler = new IdleConnectionHandler();
    this.poolLock = new ReentrantLock();
  }
  










  public void enableConnectionGC()
    throws IllegalStateException
  {}
  









  public final BasicPoolEntry getEntry(HttpRoute route, Object state, long timeout, TimeUnit tunit)
    throws ConnectionPoolTimeoutException, InterruptedException
  {
    return requestPoolEntry(route, state).getPoolEntry(timeout, tunit);
  }
  






  public abstract PoolEntryRequest requestPoolEntry(HttpRoute paramHttpRoute, Object paramObject);
  






  public abstract void freeEntry(BasicPoolEntry paramBasicPoolEntry, boolean paramBoolean, long paramLong, TimeUnit paramTimeUnit);
  





  public void handleReference(Reference<?> ref) {}
  





  protected abstract void handleLostEntry(HttpRoute paramHttpRoute);
  





  public void closeIdleConnections(long idletime, TimeUnit tunit)
  {
    if (tunit == null) {
      throw new IllegalArgumentException("Time unit must not be null.");
    }
    
    this.poolLock.lock();
    try {
      this.idleConnHandler.closeIdleConnections(tunit.toMillis(idletime));
    } finally {
      this.poolLock.unlock();
    }
  }
  
  public void closeExpiredConnections() {
    this.poolLock.lock();
    try {
      this.idleConnHandler.closeExpiredConnections();
    } finally {
      this.poolLock.unlock();
    }
  }
  




  public abstract void deleteClosedConnections();
  




  public void shutdown()
  {
    this.poolLock.lock();
    try
    {
      if (this.isShutDown) {
        return;
      }
      
      Iterator<BasicPoolEntry> iter = this.leasedConnections.iterator();
      while (iter.hasNext()) {
        BasicPoolEntry entry = (BasicPoolEntry)iter.next();
        iter.remove();
        closeConnection(entry.getConnection());
      }
      this.idleConnHandler.removeAll();
      
      this.isShutDown = true;
    }
    finally {
      this.poolLock.unlock();
    }
  }
  





  protected void closeConnection(OperatedClientConnection conn)
  {
    if (conn != null) {
      try {
        conn.close();
      } catch (IOException ex) {
        this.log.debug("I/O error closing connection", ex);
      }
    }
  }
}
