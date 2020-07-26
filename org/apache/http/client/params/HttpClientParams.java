package org.apache.http.client.params;

import org.apache.http.annotation.Immutable;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;




































@Immutable
public class HttpClientParams
{
  public static boolean isRedirecting(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    return params.getBooleanParameter("http.protocol.handle-redirects", true);
  }
  
  public static void setRedirecting(HttpParams params, boolean value)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setBooleanParameter("http.protocol.handle-redirects", value);
  }
  
  public static boolean isAuthenticating(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    return params.getBooleanParameter("http.protocol.handle-authentication", true);
  }
  
  public static void setAuthenticating(HttpParams params, boolean value)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setBooleanParameter("http.protocol.handle-authentication", value);
  }
  
  public static String getCookiePolicy(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    String cookiePolicy = (String)params.getParameter("http.protocol.cookie-policy");
    
    if (cookiePolicy == null) {
      return "best-match";
    }
    return cookiePolicy;
  }
  
  public static void setCookiePolicy(HttpParams params, String cookiePolicy) {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setParameter("http.protocol.cookie-policy", cookiePolicy);
  }
  




  public static void setConnectionManagerTimeout(HttpParams params, long timeout)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setLongParameter("http.conn-manager.timeout", timeout);
  }
  








  public static long getConnectionManagerTimeout(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    Long timeout = (Long)params.getParameter("http.conn-manager.timeout");
    if (timeout != null) {
      return timeout.longValue();
    }
    return HttpConnectionParams.getConnectionTimeout(params);
  }
}
