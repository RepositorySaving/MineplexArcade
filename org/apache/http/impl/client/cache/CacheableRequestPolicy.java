package org.apache.http.impl.client.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Immutable;
































@Immutable
class CacheableRequestPolicy
{
  private final Log log = LogFactory.getLog(getClass());
  






  public boolean isServableFromCache(HttpRequest request)
  {
    String method = request.getRequestLine().getMethod();
    
    ProtocolVersion pv = request.getRequestLine().getProtocolVersion();
    if (HttpVersion.HTTP_1_1.compareToVersion(pv) != 0) {
      this.log.trace("non-HTTP/1.1 request was not serveable from cache");
      return false;
    }
    
    if (!method.equals("GET")) {
      this.log.trace("non-GET request was not serveable from cache");
      return false;
    }
    
    if (request.getHeaders("Pragma").length > 0) {
      this.log.trace("request with Pragma header was not serveable from cache");
      return false;
    }
    
    Header[] cacheControlHeaders = request.getHeaders("Cache-Control");
    for (Header cacheControl : cacheControlHeaders) {
      for (HeaderElement cacheControlElement : cacheControl.getElements()) {
        if ("no-store".equalsIgnoreCase(cacheControlElement.getName()))
        {
          this.log.trace("Request with no-store was not serveable from cache");
          return false;
        }
        
        if ("no-cache".equalsIgnoreCase(cacheControlElement.getName()))
        {
          this.log.trace("Request with no-cache was not serveable from cache");
          return false;
        }
      }
    }
    
    this.log.trace("Request was serveable from cache");
    return true;
  }
}
