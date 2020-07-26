package org.apache.http.impl.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.pool.AbstractConnPool;
import org.apache.http.pool.ConnFactory;






























class HttpConnPool
  extends AbstractConnPool<HttpRoute, OperatedClientConnection, HttpPoolEntry>
{
  private static AtomicLong COUNTER = new AtomicLong();
  
  private final Log log;
  
  private final long timeToLive;
  private final TimeUnit tunit;
  
  public HttpConnPool(Log log, int defaultMaxPerRoute, int maxTotal, long timeToLive, TimeUnit tunit)
  {
    super(new InternalConnFactory(), defaultMaxPerRoute, maxTotal);
    this.log = log;
    this.timeToLive = timeToLive;
    this.tunit = tunit;
  }
  
  protected HttpPoolEntry createEntry(HttpRoute route, OperatedClientConnection conn)
  {
    String id = Long.toString(COUNTER.getAndIncrement());
    return new HttpPoolEntry(this.log, id, route, conn, this.timeToLive, this.tunit);
  }
  
  static class InternalConnFactory implements ConnFactory<HttpRoute, OperatedClientConnection>
  {
    public OperatedClientConnection create(HttpRoute route) throws IOException {
      return new DefaultClientConnection();
    }
  }
}
