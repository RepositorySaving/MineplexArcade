package org.apache.http.impl.pool;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.params.HttpParams;
import org.apache.http.pool.AbstractConnPool;
import org.apache.http.pool.ConnFactory;

















































@ThreadSafe
public class BasicConnPool
  extends AbstractConnPool<HttpHost, HttpClientConnection, BasicPoolEntry>
{
  private static AtomicLong COUNTER = new AtomicLong();
  
  public BasicConnPool(ConnFactory<HttpHost, HttpClientConnection> connFactory) {
    super(connFactory, 2, 20);
  }
  
  public BasicConnPool(HttpParams params) {
    super(new BasicConnFactory(params), 2, 20);
  }
  


  protected BasicPoolEntry createEntry(HttpHost host, HttpClientConnection conn)
  {
    return new BasicPoolEntry(Long.toString(COUNTER.getAndIncrement()), host, conn);
  }
}
