package org.apache.http.impl.client;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.CircularRedirectException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;







































@Deprecated
@Immutable
public class DefaultRedirectHandler
  implements RedirectHandler
{
  private final Log log = LogFactory.getLog(getClass());
  


  private static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
  



  public boolean isRedirectRequested(HttpResponse response, HttpContext context)
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    
    int statusCode = response.getStatusLine().getStatusCode();
    switch (statusCode) {
    case 301: 
    case 302: 
    case 307: 
      HttpRequest request = (HttpRequest)context.getAttribute("http.request");
      
      String method = request.getRequestLine().getMethod();
      return (method.equalsIgnoreCase("GET")) || (method.equalsIgnoreCase("HEAD"));
    
    case 303: 
      return true;
    }
    return false;
  }
  

  public URI getLocationURI(HttpResponse response, HttpContext context)
    throws ProtocolException
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    
    Header locationHeader = response.getFirstHeader("location");
    if (locationHeader == null)
    {
      throw new ProtocolException("Received redirect response " + response.getStatusLine() + " but no location header");
    }
    

    String location = locationHeader.getValue();
    if (this.log.isDebugEnabled()) {
      this.log.debug("Redirect requested to location '" + location + "'");
    }
    URI uri;
    try
    {
      uri = new URI(location);
    } catch (URISyntaxException ex) {
      throw new ProtocolException("Invalid redirect URI: " + location, ex);
    }
    
    HttpParams params = response.getParams();
    

    if (!uri.isAbsolute()) {
      if (params.isParameterTrue("http.protocol.reject-relative-redirect")) {
        throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
      }
      

      HttpHost target = (HttpHost)context.getAttribute("http.target_host");
      
      if (target == null) {
        throw new IllegalStateException("Target host not available in the HTTP context");
      }
      

      HttpRequest request = (HttpRequest)context.getAttribute("http.request");
      
      try
      {
        URI requestURI = new URI(request.getRequestLine().getUri());
        URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, true);
        uri = URIUtils.resolve(absoluteRequestURI, uri);
      } catch (URISyntaxException ex) {
        throw new ProtocolException(ex.getMessage(), ex);
      }
    }
    
    if (params.isParameterFalse("http.protocol.allow-circular-redirects"))
    {
      RedirectLocations redirectLocations = (RedirectLocations)context.getAttribute("http.protocol.redirect-locations");
      

      if (redirectLocations == null) {
        redirectLocations = new RedirectLocations();
        context.setAttribute("http.protocol.redirect-locations", redirectLocations);
      }
      
      URI redirectURI;
      if (uri.getFragment() != null) {
        try {
          HttpHost target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
          


          redirectURI = URIUtils.rewriteURI(uri, target, true);
        } catch (URISyntaxException ex) {
          throw new ProtocolException(ex.getMessage(), ex);
        }
      } else {
        redirectURI = uri;
      }
      
      if (redirectLocations.contains(redirectURI)) {
        throw new CircularRedirectException("Circular redirect to '" + redirectURI + "'");
      }
      
      redirectLocations.add(redirectURI);
    }
    

    return uri;
  }
}
