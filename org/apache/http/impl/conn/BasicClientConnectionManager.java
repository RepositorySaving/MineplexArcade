package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.RouteTracker;
import org.apache.http.conn.scheme.SchemeRegistry;











































@ThreadSafe
public class BasicClientConnectionManager
  implements ClientConnectionManager
{
  private final Log log = LogFactory.getLog(getClass());
  
  private static final AtomicLong COUNTER = new AtomicLong();
  


  public static final String MISUSE_MESSAGE = "Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.";
  


  private final SchemeRegistry schemeRegistry;
  


  private final ClientConnectionOperator connOperator;
  


  @GuardedBy("this")
  private HttpPoolEntry poolEntry;
  

  @GuardedBy("this")
  private ManagedClientConnectionImpl conn;
  

  @GuardedBy("this")
  private volatile boolean shutdown;
  


  public BasicClientConnectionManager(SchemeRegistry schreg)
  {
    if (schreg == null) {
      throw new IllegalArgumentException("Scheme registry may not be null");
    }
    this.schemeRegistry = schreg;
    this.connOperator = createConnectionOperator(schreg);
  }
  
  public BasicClientConnectionManager() {
    this(SchemeRegistryFactory.createDefault());
  }
  
  protected void finalize() throws Throwable
  {
    try {
      shutdown();
    } finally {
      super.finalize();
    }
  }
  
  public SchemeRegistry getSchemeRegistry() {
    return this.schemeRegistry;
  }
  
  protected ClientConnectionOperator createConnectionOperator(SchemeRegistry schreg) {
    return new DefaultClientConnectionOperator(schreg);
  }
  


  public final ClientConnectionRequest requestConnection(final HttpRoute route, final Object state)
  {
    new ClientConnectionRequest()
    {
      public void abortRequest() {}
      


      public ManagedClientConnection getConnection(long timeout, TimeUnit tunit)
      {
        return BasicClientConnectionManager.this.getConnection(route, state);
      }
    };
  }
  

  private void assertNotShutdown()
  {
    if (this.shutdown) {
      throw new IllegalStateException("Connection manager has been shut down");
    }
  }
  
  ManagedClientConnection getConnection(HttpRoute route, Object state) {
    if (route == null) {
      throw new IllegalArgumentException("Route may not be null.");
    }
    assertNotShutdown();
    if (this.log.isDebugEnabled()) {
      this.log.debug("Get connection for route " + route);
    }
    synchronized (this) {
      if (this.conn != null) {
        throw new IllegalStateException("Invalid use of BasicClientConnManager: connection still allocated.\nMake sure to release the connection before allocating another one.");
      }
      if ((this.poolEntry != null) && (!this.poolEntry.getPlannedRoute().equals(route))) {
        this.poolEntry.close();
        this.poolEntry.getTracker().reset();
      }
      if (this.poolEntry == null) {
        String id = Long.toString(COUNTER.getAndIncrement());
        OperatedClientConnection conn = this.connOperator.createConnection();
        this.poolEntry = new HttpPoolEntry(this.log, id, route, conn, 0L, TimeUnit.MILLISECONDS);
      }
      long now = System.currentTimeMillis();
      if (this.poolEntry.isExpired(now)) {
        this.poolEntry.close();
        this.poolEntry.getTracker().reset();
      }
      this.conn = new ManagedClientConnectionImpl(this, this.connOperator, this.poolEntry);
      return this.conn;
    }
  }
  
  public void releaseConnection(ManagedClientConnection conn, long keepalive, TimeUnit tunit) {
    assertNotShutdown();
    if (!(conn instanceof ManagedClientConnectionImpl)) {
      throw new IllegalArgumentException("Connection class mismatch, connection not obtained from this manager");
    }
    
    if (this.log.isDebugEnabled()) {
      this.log.debug("Releasing connection " + conn);
    }
    ManagedClientConnectionImpl managedConn = (ManagedClientConnectionImpl)conn;
    synchronized (managedConn) {
      if (managedConn.getPoolEntry() == null) {
        return;
      }
      ClientConnectionManager manager = managedConn.getManager();
      if ((manager != null) && (manager != this)) {
        throw new IllegalStateException("Connection not obtained from this manager");
      }
      synchronized (this) {
        try {
          if ((managedConn.isOpen()) && (!managedConn.isMarkedReusable())) {
            try {
              managedConn.shutdown();
            } catch (IOException iox) {
              if (this.log.isDebugEnabled()) {
                this.log.debug("I/O exception shutting down released connection", iox);
              }
            }
          }
          this.poolEntry.updateExpiry(keepalive, tunit != null ? tunit : TimeUnit.MILLISECONDS);
          if (this.log.isDebugEnabled()) { String s;
            String s;
            if (keepalive > 0L) {
              s = "for " + keepalive + " " + tunit;
            } else {
              s = "indefinitely";
            }
            this.log.debug("Connection can be kept alive " + s);
          }
        } finally {
          managedConn.detach();
          this.conn = null;
          if (this.poolEntry.isClosed()) {
            this.poolEntry = null;
          }
        }
      }
    }
  }
  
  public void closeExpiredConnections() {
    assertNotShutdown();
    synchronized (this) {
      long now = System.currentTimeMillis();
      if ((this.poolEntry != null) && (this.poolEntry.isExpired(now))) {
        this.poolEntry.close();
        this.poolEntry.getTracker().reset();
      }
    }
  }
  
  public void closeIdleConnections(long idletime, TimeUnit tunit) {
    if (tunit == null) {
      throw new IllegalArgumentException("Time unit must not be null.");
    }
    assertNotShutdown();
    synchronized (this) {
      long time = tunit.toMillis(idletime);
      if (time < 0L) {
        time = 0L;
      }
      long deadline = System.currentTimeMillis() - time;
      if ((this.poolEntry != null) && (this.poolEntry.getUpdated() <= deadline)) {
        this.poolEntry.close();
        this.poolEntry.getTracker().reset();
      }
    }
  }
  
  public void shutdown() {
    this.shutdown = true;
    synchronized (this) {
      try {
        if (this.poolEntry != null) {
          this.poolEntry.close();
        }
      } finally {
        this.poolEntry = null;
        this.conn = null;
      }
    }
  }
}
