package org.apache.http.conn.params;

import org.apache.http.annotation.Immutable;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.params.HttpParams;












































@Deprecated
@Immutable
public final class ConnManagerParams
  implements ConnManagerPNames
{
  public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;
  
  /**
   * @deprecated
   */
  public static long getTimeout(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    return params.getLongParameter("http.conn-manager.timeout", 0L);
  }
  





  /**
   * @deprecated
   */
  public static void setTimeout(HttpParams params, long timeout)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setLongParameter("http.conn-manager.timeout", timeout);
  }
  

  private static final ConnPerRoute DEFAULT_CONN_PER_ROUTE = new ConnPerRoute()
  {
    public int getMaxForRoute(HttpRoute route) {
      return 2;
    }
  };
  








  public static void setMaxConnectionsPerRoute(HttpParams params, ConnPerRoute connPerRoute)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters must not be null.");
    }
    
    params.setParameter("http.conn-manager.max-per-route", connPerRoute);
  }
  






  public static ConnPerRoute getMaxConnectionsPerRoute(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters must not be null.");
    }
    
    ConnPerRoute connPerRoute = (ConnPerRoute)params.getParameter("http.conn-manager.max-per-route");
    if (connPerRoute == null) {
      connPerRoute = DEFAULT_CONN_PER_ROUTE;
    }
    return connPerRoute;
  }
  







  public static void setMaxTotalConnections(HttpParams params, int maxTotalConnections)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters must not be null.");
    }
    
    params.setIntParameter("http.conn-manager.max-total", maxTotalConnections);
  }
  







  public static int getMaxTotalConnections(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters must not be null.");
    }
    
    return params.getIntParameter("http.conn-manager.max-total", 20);
  }
}
