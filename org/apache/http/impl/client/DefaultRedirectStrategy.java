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
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;












































@Immutable
public class DefaultRedirectStrategy
  implements RedirectStrategy
{
  private final Log log = LogFactory.getLog(getClass());
  


  public static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
  

  private static final String[] REDIRECT_METHODS = { "GET", "HEAD" };
  








  public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
    throws ProtocolException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    
    int statusCode = response.getStatusLine().getStatusCode();
    String method = request.getRequestLine().getMethod();
    Header locationHeader = response.getFirstHeader("location");
    switch (statusCode) {
    case 302: 
      return (isRedirectable(method)) && (locationHeader != null);
    case 301: 
    case 307: 
      return isRedirectable(method);
    case 303: 
      return true;
    }
    return false;
  }
  


  public URI getLocationURI(HttpRequest request, HttpResponse response, HttpContext context)
    throws ProtocolException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
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
    
    URI uri = createLocationURI(location);
    
    HttpParams params = request.getParams();
    

    try
    {
      uri = URIUtils.rewriteURI(uri);
      if (!uri.isAbsolute()) {
        if (params.isParameterTrue("http.protocol.reject-relative-redirect")) {
          throw new ProtocolException("Relative redirect location '" + uri + "' not allowed");
        }
        

        HttpHost target = (HttpHost)context.getAttribute("http.target_host");
        if (target == null) {
          throw new IllegalStateException("Target host not available in the HTTP context");
        }
        
        URI requestURI = new URI(request.getRequestLine().getUri());
        URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, true);
        uri = URIUtils.resolve(absoluteRequestURI, uri);
      }
    } catch (URISyntaxException ex) {
      throw new ProtocolException(ex.getMessage(), ex);
    }
    
    RedirectLocations redirectLocations = (RedirectLocations)context.getAttribute("http.protocol.redirect-locations");
    
    if (redirectLocations == null) {
      redirectLocations = new RedirectLocations();
      context.setAttribute("http.protocol.redirect-locations", redirectLocations);
    }
    if ((params.isParameterFalse("http.protocol.allow-circular-redirects")) && 
      (redirectLocations.contains(uri))) {
      throw new CircularRedirectException("Circular redirect to '" + uri + "'");
    }
    
    redirectLocations.add(uri);
    return uri;
  }
  
  protected URI createLocationURI(String location)
    throws ProtocolException
  {
    try
    {
      return new URI(location);
    } catch (URISyntaxException ex) {
      throw new ProtocolException("Invalid redirect URI: " + location, ex);
    }
  }
  


  protected boolean isRedirectable(String method)
  {
    for (String m : REDIRECT_METHODS) {
      if (m.equalsIgnoreCase(method)) {
        return true;
      }
    }
    return false;
  }
  

  public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
    throws ProtocolException
  {
    URI uri = getLocationURI(request, response, context);
    String method = request.getRequestLine().getMethod();
    if (method.equalsIgnoreCase("HEAD")) {
      return new HttpHead(uri);
    }
    return new HttpGet(uri);
  }
}
