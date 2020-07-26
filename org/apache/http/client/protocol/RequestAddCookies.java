package org.apache.http.client.protocol;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.ProtocolException;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecRegistry;
import org.apache.http.cookie.SetCookie2;
import org.apache.http.protocol.HttpContext;












































@Immutable
public class RequestAddCookies
  implements HttpRequestInterceptor
{
  private final Log log = LogFactory.getLog(getClass());
  



  public void process(HttpRequest request, HttpContext context)
    throws HttpException, IOException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    
    String method = request.getRequestLine().getMethod();
    if (method.equalsIgnoreCase("CONNECT")) {
      return;
    }
    

    CookieStore cookieStore = (CookieStore)context.getAttribute("http.cookie-store");
    
    if (cookieStore == null) {
      this.log.debug("Cookie store not specified in HTTP context");
      return;
    }
    

    CookieSpecRegistry registry = (CookieSpecRegistry)context.getAttribute("http.cookiespec-registry");
    
    if (registry == null) {
      this.log.debug("CookieSpec registry not specified in HTTP context");
      return;
    }
    

    HttpHost targetHost = (HttpHost)context.getAttribute("http.target_host");
    
    if (targetHost == null) {
      this.log.debug("Target host not set in the context");
      return;
    }
    

    HttpRoutedConnection conn = (HttpRoutedConnection)context.getAttribute("http.connection");
    
    if (conn == null) {
      this.log.debug("HTTP connection not set in the context");
      return;
    }
    
    String policy = HttpClientParams.getCookiePolicy(request.getParams());
    if (this.log.isDebugEnabled()) {
      this.log.debug("CookieSpec selected: " + policy);
    }
    URI requestURI;
    URI requestURI;
    if ((request instanceof HttpUriRequest)) {
      requestURI = ((HttpUriRequest)request).getURI();
    } else {
      try {
        requestURI = new URI(request.getRequestLine().getUri());
      } catch (URISyntaxException ex) {
        throw new ProtocolException("Invalid request URI: " + request.getRequestLine().getUri(), ex);
      }
    }
    

    String hostName = targetHost.getHostName();
    int port = targetHost.getPort();
    if (port < 0) {
      HttpRoute route = conn.getRoute();
      if (route.getHopCount() == 1) {
        port = conn.getRemotePort();
      }
      else
      {
        String scheme = targetHost.getSchemeName();
        if (scheme.equalsIgnoreCase("http")) {
          port = 80;
        } else if (scheme.equalsIgnoreCase("https")) {
          port = 443;
        } else {
          port = 0;
        }
      }
    }
    
    CookieOrigin cookieOrigin = new CookieOrigin(hostName, port, requestURI.getPath(), conn.isSecure());
    





    CookieSpec cookieSpec = registry.getCookieSpec(policy, request.getParams());
    
    List<Cookie> cookies = new ArrayList(cookieStore.getCookies());
    
    List<Cookie> matchedCookies = new ArrayList();
    Date now = new Date();
    for (Cookie cookie : cookies) {
      if (!cookie.isExpired(now)) {
        if (cookieSpec.match(cookie, cookieOrigin)) {
          if (this.log.isDebugEnabled()) {
            this.log.debug("Cookie " + cookie + " match " + cookieOrigin);
          }
          matchedCookies.add(cookie);
        }
      }
      else if (this.log.isDebugEnabled()) {
        this.log.debug("Cookie " + cookie + " expired");
      }
    }
    

    if (!matchedCookies.isEmpty()) {
      List<Header> headers = cookieSpec.formatCookies(matchedCookies);
      for (Header header : headers) {
        request.addHeader(header);
      }
    }
    
    int ver = cookieSpec.getVersion();
    if (ver > 0) {
      boolean needVersionHeader = false;
      for (Cookie cookie : matchedCookies) {
        if ((ver != cookie.getVersion()) || (!(cookie instanceof SetCookie2))) {
          needVersionHeader = true;
        }
      }
      
      if (needVersionHeader) {
        Header header = cookieSpec.getVersionHeader();
        if (header != null)
        {
          request.addHeader(header);
        }
      }
    }
    


    context.setAttribute("http.cookie-spec", cookieSpec);
    context.setAttribute("http.cookie-origin", cookieOrigin);
  }
}
