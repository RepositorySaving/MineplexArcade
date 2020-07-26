package org.apache.http.conn;

import javax.net.ssl.SSLSession;
import org.apache.http.HttpInetConnection;
import org.apache.http.conn.routing.HttpRoute;

public abstract interface HttpRoutedConnection
  extends HttpInetConnection
{
  public abstract boolean isSecure();
  
  public abstract HttpRoute getRoute();
  
  public abstract SSLSession getSSLSession();
}
