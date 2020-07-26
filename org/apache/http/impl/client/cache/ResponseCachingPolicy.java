package org.apache.http.impl.client.cache;

import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

































@Immutable
class ResponseCachingPolicy
{
  private final long maxObjectSizeBytes;
  private final boolean sharedCache;
  private final Log log = LogFactory.getLog(getClass());
  







  public ResponseCachingPolicy(long maxObjectSizeBytes, boolean sharedCache)
  {
    this.maxObjectSizeBytes = maxObjectSizeBytes;
    this.sharedCache = sharedCache;
  }
  






  public boolean isResponseCacheable(String httpMethod, HttpResponse response)
  {
    boolean cacheable = false;
    
    if (!"GET".equals(httpMethod)) {
      this.log.debug("Response was not cacheable.");
      return false;
    }
    
    switch (response.getStatusLine().getStatusCode())
    {
    case 200: 
    case 203: 
    case 300: 
    case 301: 
    case 410: 
      cacheable = true;
      this.log.debug("Response was cacheable");
      break;
    

    case 206: 
      this.log.debug("Response was not cacheable (Partial Content)");
      return cacheable;
    


    default: 
      this.log.debug("Response was not cacheable (Unknown Status code)");
      return cacheable;
    }
    
    Header contentLength = response.getFirstHeader("Content-Length");
    if (contentLength != null) {
      int contentLengthValue = Integer.parseInt(contentLength.getValue());
      if (contentLengthValue > this.maxObjectSizeBytes) {
        return false;
      }
    }
    Header[] ageHeaders = response.getHeaders("Age");
    
    if (ageHeaders.length > 1) {
      return false;
    }
    Header[] expiresHeaders = response.getHeaders("Expires");
    
    if (expiresHeaders.length > 1) {
      return false;
    }
    Header[] dateHeaders = response.getHeaders("Date");
    
    if (dateHeaders.length != 1) {
      return false;
    }
    try {
      DateUtils.parseDate(dateHeaders[0].getValue());
    } catch (DateParseException dpe) {
      return false;
    }
    
    for (Header varyHdr : response.getHeaders("Vary")) {
      for (HeaderElement elem : varyHdr.getElements()) {
        if ("*".equals(elem.getName())) {
          return false;
        }
      }
    }
    
    if (isExplicitlyNonCacheable(response)) {
      return false;
    }
    return (cacheable) || (isExplicitlyCacheable(response));
  }
  
  protected boolean isExplicitlyNonCacheable(HttpResponse response) {
    Header[] cacheControlHeaders = response.getHeaders("Cache-Control");
    for (Header header : cacheControlHeaders) {
      for (HeaderElement elem : header.getElements()) {
        if (("no-store".equals(elem.getName())) || ("no-cache".equals(elem.getName())) || ((this.sharedCache) && ("private".equals(elem.getName()))))
        {

          return true;
        }
      }
    }
    return false;
  }
  
  protected boolean hasCacheControlParameterFrom(HttpMessage msg, String[] params) {
    Header[] cacheControlHeaders = msg.getHeaders("Cache-Control");
    for (Header header : cacheControlHeaders) {
      for (HeaderElement elem : header.getElements()) {
        for (String param : params) {
          if (param.equalsIgnoreCase(elem.getName())) {
            return true;
          }
        }
      }
    }
    return false;
  }
  
  protected boolean isExplicitlyCacheable(HttpResponse response) {
    if (response.getFirstHeader("Expires") != null)
      return true;
    String[] cacheableParams = { "max-age", "s-maxage", "must-revalidate", "proxy-revalidate", "public" };
    



    return hasCacheControlParameterFrom(response, cacheableParams);
  }
  







  public boolean isResponseCacheable(HttpRequest request, HttpResponse response)
  {
    if (requestProtocolGreaterThanAccepted(request)) {
      this.log.debug("Response was not cacheable.");
      return false;
    }
    
    String[] uncacheableRequestDirectives = { "no-store" };
    if (hasCacheControlParameterFrom(request, uncacheableRequestDirectives)) {
      return false;
    }
    
    if ((request.getRequestLine().getUri().contains("?")) && ((!isExplicitlyCacheable(response)) || (from1_0Origin(response))))
    {
      this.log.debug("Response was not cacheable.");
      return false;
    }
    
    if (expiresHeaderLessOrEqualToDateHeaderAndNoCacheControl(response)) {
      return false;
    }
    
    if (this.sharedCache) {
      Header[] authNHeaders = request.getHeaders("Authorization");
      if ((authNHeaders != null) && (authNHeaders.length > 0)) {
        String[] authCacheableParams = { "s-maxage", "must-revalidate", "public" };
        

        return hasCacheControlParameterFrom(response, authCacheableParams);
      }
    }
    
    String method = request.getRequestLine().getMethod();
    return isResponseCacheable(method, response);
  }
  
  private boolean expiresHeaderLessOrEqualToDateHeaderAndNoCacheControl(HttpResponse response)
  {
    if (response.getFirstHeader("Cache-Control") != null) return false;
    Header expiresHdr = response.getFirstHeader("Expires");
    Header dateHdr = response.getFirstHeader("Date");
    if ((expiresHdr == null) || (dateHdr == null)) return false;
    try {
      Date expires = DateUtils.parseDate(expiresHdr.getValue());
      Date date = DateUtils.parseDate(dateHdr.getValue());
      return (expires.equals(date)) || (expires.before(date));
    } catch (DateParseException dpe) {}
    return false;
  }
  
  private boolean from1_0Origin(HttpResponse response)
  {
    Header via = response.getFirstHeader("Via");
    if (via != null) {
      HeaderElement[] arr$ = via.getElements();int len$ = arr$.length;int i$ = 0; if (i$ < len$) { HeaderElement elt = arr$[i$];
        String proto = elt.toString().split("\\s")[0];
        if (proto.contains("/")) {
          return proto.equals("HTTP/1.0");
        }
        return proto.equals("1.0");
      }
    }
    
    return HttpVersion.HTTP_1_0.equals(response.getProtocolVersion());
  }
  
  private boolean requestProtocolGreaterThanAccepted(HttpRequest req) {
    return req.getProtocolVersion().compareToVersion(HttpVersion.HTTP_1_1) > 0;
  }
}
