package org.apache.http.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public abstract interface ManagedClientConnection
  extends HttpClientConnection, HttpRoutedConnection, ConnectionReleaseTrigger
{
  public abstract boolean isSecure();
  
  public abstract HttpRoute getRoute();
  
  public abstract SSLSession getSSLSession();
  
  public abstract void open(HttpRoute paramHttpRoute, HttpContext paramHttpContext, HttpParams paramHttpParams)
    throws IOException;
  
  public abstract void tunnelTarget(boolean paramBoolean, HttpParams paramHttpParams)
    throws IOException;
  
  public abstract void tunnelProxy(HttpHost paramHttpHost, boolean paramBoolean, HttpParams paramHttpParams)
    throws IOException;
  
  public abstract void layerProtocol(HttpContext paramHttpContext, HttpParams paramHttpParams)
    throws IOException;
  
  public abstract void markReusable();
  
  public abstract void unmarkReusable();
  
  public abstract boolean isMarkedReusable();
  
  public abstract void setState(Object paramObject);
  
  public abstract Object getState();
  
  public abstract void setIdleDuration(long paramLong, TimeUnit paramTimeUnit);
}
