package org.apache.http.client.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Stack;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Immutable;


































































@Immutable
public class URIUtils
{
  public static URI createURI(String scheme, String host, int port, String path, String query, String fragment)
    throws URISyntaxException
  {
    StringBuilder buffer = new StringBuilder();
    if (host != null) {
      if (scheme != null) {
        buffer.append(scheme);
        buffer.append("://");
      }
      buffer.append(host);
      if (port > 0) {
        buffer.append(':');
        buffer.append(port);
      }
    }
    if ((path == null) || (!path.startsWith("/"))) {
      buffer.append('/');
    }
    if (path != null) {
      buffer.append(path);
    }
    if (query != null) {
      buffer.append('?');
      buffer.append(query);
    }
    if (fragment != null) {
      buffer.append('#');
      buffer.append(fragment);
    }
    return new URI(buffer.toString());
  }
  

















  public static URI rewriteURI(URI uri, HttpHost target, boolean dropFragment)
    throws URISyntaxException
  {
    if (uri == null) {
      throw new IllegalArgumentException("URI may not be null");
    }
    if (target != null) {
      return createURI(target.getSchemeName(), target.getHostName(), target.getPort(), normalizePath(uri.getRawPath()), uri.getRawQuery(), dropFragment ? null : uri.getRawFragment());
    }
    





    return createURI(null, null, -1, normalizePath(uri.getRawPath()), uri.getRawQuery(), dropFragment ? null : uri.getRawFragment());
  }
  






  private static String normalizePath(String path)
  {
    if (path == null) {
      return null;
    }
    for (int n = 0; 
        n < path.length(); n++) {
      if (path.charAt(n) != '/') {
        break;
      }
    }
    if (n > 1) {
      path = path.substring(n - 1);
    }
    return path;
  }
  





  public static URI rewriteURI(URI uri, HttpHost target)
    throws URISyntaxException
  {
    return rewriteURI(uri, target, false);
  }
  








  public static URI rewriteURI(URI uri)
    throws URISyntaxException
  {
    if (uri == null) {
      throw new IllegalArgumentException("URI may not be null");
    }
    if (uri.getFragment() != null) {
      return createURI(uri.getScheme(), uri.getHost(), uri.getPort(), uri.getRawPath(), uri.getRawQuery(), null);
    }
    





    return uri;
  }
  








  public static URI resolve(URI baseURI, String reference)
  {
    return resolve(baseURI, URI.create(reference));
  }
  







  public static URI resolve(URI baseURI, URI reference)
  {
    if (baseURI == null) {
      throw new IllegalArgumentException("Base URI may nor be null");
    }
    if (reference == null) {
      throw new IllegalArgumentException("Reference URI may nor be null");
    }
    String s = reference.toString();
    if (s.startsWith("?")) {
      return resolveReferenceStartingWithQueryString(baseURI, reference);
    }
    boolean emptyReference = s.length() == 0;
    if (emptyReference) {
      reference = URI.create("#");
    }
    URI resolved = baseURI.resolve(reference);
    if (emptyReference) {
      String resolvedString = resolved.toString();
      resolved = URI.create(resolvedString.substring(0, resolvedString.indexOf('#')));
    }
    
    return removeDotSegments(resolved);
  }
  







  private static URI resolveReferenceStartingWithQueryString(URI baseURI, URI reference)
  {
    String baseUri = baseURI.toString();
    baseUri = baseUri.indexOf('?') > -1 ? baseUri.substring(0, baseUri.indexOf('?')) : baseUri;
    
    return URI.create(baseUri + reference.toString());
  }
  





  private static URI removeDotSegments(URI uri)
  {
    String path = uri.getPath();
    if ((path == null) || (path.indexOf("/.") == -1))
    {
      return uri;
    }
    String[] inputSegments = path.split("/");
    Stack<String> outputSegments = new Stack();
    for (int i = 0; i < inputSegments.length; i++) {
      if ((inputSegments[i].length() != 0) && (!".".equals(inputSegments[i])))
      {

        if ("..".equals(inputSegments[i])) {
          if (!outputSegments.isEmpty()) {
            outputSegments.pop();
          }
        } else
          outputSegments.push(inputSegments[i]);
      }
    }
    StringBuilder outputBuffer = new StringBuilder();
    for (String outputSegment : outputSegments) {
      outputBuffer.append('/').append(outputSegment);
    }
    try {
      return new URI(uri.getScheme(), uri.getAuthority(), outputBuffer.toString(), uri.getQuery(), uri.getFragment());
    }
    catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }
  








  public static HttpHost extractHost(URI uri)
  {
    if (uri == null) {
      return null;
    }
    HttpHost target = null;
    if (uri.isAbsolute()) {
      int port = uri.getPort();
      String host = uri.getHost();
      if (host == null)
      {
        host = uri.getAuthority();
        if (host != null)
        {
          int at = host.indexOf('@');
          if (at >= 0) {
            if (host.length() > at + 1) {
              host = host.substring(at + 1);
            } else {
              host = null;
            }
          }
          
          if (host != null) {
            int colon = host.indexOf(':');
            if (colon >= 0) {
              int pos = colon + 1;
              int len = 0;
              for (int i = pos; i < host.length(); i++) {
                if (!Character.isDigit(host.charAt(i))) break;
                len++;
              }
              


              if (len > 0) {
                try {
                  port = Integer.parseInt(host.substring(pos, pos + len));
                }
                catch (NumberFormatException ex) {}
              }
              host = host.substring(0, colon);
            }
          }
        }
      }
      String scheme = uri.getScheme();
      if (host != null) {
        target = new HttpHost(host, port, scheme);
      }
    }
    return target;
  }
}
