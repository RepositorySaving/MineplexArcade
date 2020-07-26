package org.apache.http.client.protocol;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.protocol.HttpContext;




































@Immutable
public class ResponseProcessCookies
  implements HttpResponseInterceptor
{
  private final Log log = LogFactory.getLog(getClass());
  



  public void process(HttpResponse response, HttpContext context)
    throws HttpException, IOException
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    

    CookieSpec cookieSpec = (CookieSpec)context.getAttribute("http.cookie-spec");
    
    if (cookieSpec == null) {
      this.log.debug("Cookie spec not specified in HTTP context");
      return;
    }
    
    CookieStore cookieStore = (CookieStore)context.getAttribute("http.cookie-store");
    
    if (cookieStore == null) {
      this.log.debug("Cookie store not specified in HTTP context");
      return;
    }
    
    CookieOrigin cookieOrigin = (CookieOrigin)context.getAttribute("http.cookie-origin");
    
    if (cookieOrigin == null) {
      this.log.debug("Cookie origin not specified in HTTP context");
      return;
    }
    HeaderIterator it = response.headerIterator("Set-Cookie");
    processCookies(it, cookieSpec, cookieOrigin, cookieStore);
    

    if (cookieSpec.getVersion() > 0)
    {

      it = response.headerIterator("Set-Cookie2");
      processCookies(it, cookieSpec, cookieOrigin, cookieStore);
    }
  }
  



  private void processCookies(HeaderIterator iterator, CookieSpec cookieSpec, CookieOrigin cookieOrigin, CookieStore cookieStore)
  {
    while (iterator.hasNext()) {
      Header header = iterator.nextHeader();
      try {
        List<Cookie> cookies = cookieSpec.parse(header, cookieOrigin);
        for (Cookie cookie : cookies) {
          try {
            cookieSpec.validate(cookie, cookieOrigin);
            cookieStore.addCookie(cookie);
            
            if (this.log.isDebugEnabled()) {
              this.log.debug("Cookie accepted: \"" + cookie + "\". ");
            }
          }
          catch (MalformedCookieException ex) {
            if (this.log.isWarnEnabled()) {
              this.log.warn("Cookie rejected: \"" + cookie + "\". " + ex.getMessage());
            }
          }
        }
      }
      catch (MalformedCookieException ex) {
        if (this.log.isWarnEnabled()) {
          this.log.warn("Invalid cookie header: \"" + header + "\". " + ex.getMessage());
        }
      }
    }
  }
}
