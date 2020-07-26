package org.apache.http.conn.params;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Immutable;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;







































@Immutable
public class ConnRouteParams
  implements ConnRoutePNames
{
  public static final HttpHost NO_HOST = new HttpHost("127.0.0.255", 0, "no-host");
  





  public static final HttpRoute NO_ROUTE = new HttpRoute(NO_HOST);
  















  public static HttpHost getDefaultProxy(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("Parameters must not be null.");
    }
    HttpHost proxy = (HttpHost)params.getParameter("http.route.default-proxy");
    
    if ((proxy != null) && (NO_HOST.equals(proxy)))
    {
      proxy = null;
    }
    return proxy;
  }
  










  public static void setDefaultProxy(HttpParams params, HttpHost proxy)
  {
    if (params == null) {
      throw new IllegalArgumentException("Parameters must not be null.");
    }
    params.setParameter("http.route.default-proxy", proxy);
  }
  










  public static HttpRoute getForcedRoute(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("Parameters must not be null.");
    }
    HttpRoute route = (HttpRoute)params.getParameter("http.route.forced-route");
    
    if ((route != null) && (NO_ROUTE.equals(route)))
    {
      route = null;
    }
    return route;
  }
  










  public static void setForcedRoute(HttpParams params, HttpRoute route)
  {
    if (params == null) {
      throw new IllegalArgumentException("Parameters must not be null.");
    }
    params.setParameter("http.route.forced-route", route);
  }
  











  public static InetAddress getLocalAddress(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("Parameters must not be null.");
    }
    InetAddress local = (InetAddress)params.getParameter("http.route.local-address");
    

    return local;
  }
  







  public static void setLocalAddress(HttpParams params, InetAddress local)
  {
    if (params == null) {
      throw new IllegalArgumentException("Parameters must not be null.");
    }
    params.setParameter("http.route.local-address", local);
  }
}
