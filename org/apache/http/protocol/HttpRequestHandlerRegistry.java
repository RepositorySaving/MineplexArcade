package org.apache.http.protocol;

import java.util.Map;
import org.apache.http.annotation.ThreadSafe;












































@ThreadSafe
public class HttpRequestHandlerRegistry
  implements HttpRequestHandlerResolver
{
  private final UriPatternMatcher<HttpRequestHandler> matcher;
  
  public HttpRequestHandlerRegistry()
  {
    this.matcher = new UriPatternMatcher();
  }
  






  public void register(String pattern, HttpRequestHandler handler)
  {
    if (pattern == null) {
      throw new IllegalArgumentException("URI request pattern may not be null");
    }
    if (handler == null) {
      throw new IllegalArgumentException("Request handler may not be null");
    }
    this.matcher.register(pattern, handler);
  }
  




  public void unregister(String pattern)
  {
    this.matcher.unregister(pattern);
  }
  



  public void setHandlers(Map<String, HttpRequestHandler> map)
  {
    this.matcher.setObjects(map);
  }
  





  public Map<String, HttpRequestHandler> getHandlers()
  {
    return this.matcher.getObjects();
  }
  
  public HttpRequestHandler lookup(String requestURI) {
    return (HttpRequestHandler)this.matcher.lookup(requestURI);
  }
}
