package org.apache.http.impl.conn.tsccm;

import java.lang.ref.ReferenceQueue;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.AbstractPoolEntry;




































@Deprecated
public class BasicPoolEntry
  extends AbstractPoolEntry
{
  private final long created;
  private long updated;
  private long validUntil;
  private long expiry;
  
  public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route, ReferenceQueue<Object> queue)
  {
    super(op, route);
    if (route == null) {
      throw new IllegalArgumentException("HTTP route may not be null");
    }
    this.created = System.currentTimeMillis();
    this.validUntil = 9223372036854775807L;
    this.expiry = this.validUntil;
  }
  






  public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route)
  {
    this(op, route, -1L, TimeUnit.MILLISECONDS);
  }
  










  public BasicPoolEntry(ClientConnectionOperator op, HttpRoute route, long connTTL, TimeUnit timeunit)
  {
    super(op, route);
    if (route == null) {
      throw new IllegalArgumentException("HTTP route may not be null");
    }
    this.created = System.currentTimeMillis();
    if (connTTL > 0L) {
      this.validUntil = (this.created + timeunit.toMillis(connTTL));
    } else {
      this.validUntil = 9223372036854775807L;
    }
    this.expiry = this.validUntil;
  }
  
  protected final OperatedClientConnection getConnection() {
    return this.connection;
  }
  
  protected final HttpRoute getPlannedRoute() {
    return this.route;
  }
  
  protected final BasicPoolEntryRef getWeakRef() {
    return null;
  }
  
  protected void shutdownEntry()
  {
    super.shutdownEntry();
  }
  


  public long getCreated()
  {
    return this.created;
  }
  


  public long getUpdated()
  {
    return this.updated;
  }
  


  public long getExpiry()
  {
    return this.expiry;
  }
  
  public long getValidUntil() {
    return this.validUntil;
  }
  


  public void updateExpiry(long time, TimeUnit timeunit)
  {
    this.updated = System.currentTimeMillis();
    long newExpiry;
    long newExpiry; if (time > 0L) {
      newExpiry = this.updated + timeunit.toMillis(time);
    } else {
      newExpiry = 9223372036854775807L;
    }
    this.expiry = Math.min(this.validUntil, newExpiry);
  }
  


  public boolean isExpired(long now)
  {
    return now >= this.expiry;
  }
}
