package org.apache.http.conn.params;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.routing.HttpRoute;










































@Deprecated
@ThreadSafe
public final class ConnPerRouteBean
  implements ConnPerRoute
{
  public static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 2;
  private final ConcurrentHashMap<HttpRoute, Integer> maxPerHostMap;
  private volatile int defaultMax;
  
  public ConnPerRouteBean(int defaultMax)
  {
    this.maxPerHostMap = new ConcurrentHashMap();
    setDefaultMaxPerRoute(defaultMax);
  }
  
  public ConnPerRouteBean() {
    this(2);
  }
  
  public int getDefaultMax() {
    return this.defaultMax;
  }
  


  public int getDefaultMaxPerRoute()
  {
    return this.defaultMax;
  }
  
  public void setDefaultMaxPerRoute(int max) {
    if (max < 1) {
      throw new IllegalArgumentException("The maximum must be greater than 0.");
    }
    
    this.defaultMax = max;
  }
  
  public void setMaxForRoute(HttpRoute route, int max) {
    if (route == null) {
      throw new IllegalArgumentException("HTTP route may not be null.");
    }
    
    if (max < 1) {
      throw new IllegalArgumentException("The maximum must be greater than 0.");
    }
    
    this.maxPerHostMap.put(route, Integer.valueOf(max));
  }
  
  public int getMaxForRoute(HttpRoute route) {
    if (route == null) {
      throw new IllegalArgumentException("HTTP route may not be null.");
    }
    
    Integer max = (Integer)this.maxPerHostMap.get(route);
    if (max != null) {
      return max.intValue();
    }
    return this.defaultMax;
  }
  
  public void setMaxForRoutes(Map<HttpRoute, Integer> map)
  {
    if (map == null) {
      return;
    }
    this.maxPerHostMap.clear();
    this.maxPerHostMap.putAll(map);
  }
  
  public String toString()
  {
    return this.maxPerHostMap.toString();
  }
}
