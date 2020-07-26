package org.apache.http.impl.client.cache;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.cache.HttpCacheEntry;





































@Immutable
class CacheKeyGenerator
{
  public String getURI(HttpHost host, HttpRequest req)
  {
    if (isRelativeRequest(req)) {
      return canonicalizeUri(String.format("%s%s", new Object[] { host.toString(), req.getRequestLine().getUri() }));
    }
    return canonicalizeUri(req.getRequestLine().getUri());
  }
  
  public String canonicalizeUri(String uri) {
    try {
      URL u = new URL(uri);
      String protocol = u.getProtocol().toLowerCase();
      String hostname = u.getHost().toLowerCase();
      int port = canonicalizePort(u.getPort(), protocol);
      String path = canonicalizePath(u.getPath());
      if ("".equals(path)) path = "/";
      String query = u.getQuery();
      String file = query != null ? path + "?" + query : path;
      URL out = new URL(protocol, hostname, port, file);
      return out.toString();
    } catch (MalformedURLException e) {}
    return uri;
  }
  
  private String canonicalizePath(String path)
  {
    try {
      String decoded = URLDecoder.decode(path, "UTF-8");
      return new URI(decoded).getPath();
    }
    catch (UnsupportedEncodingException e) {}catch (URISyntaxException e) {}
    
    return path;
  }
  
  private int canonicalizePort(int port, String protocol) {
    if ((port == -1) && ("http".equalsIgnoreCase(protocol)))
      return 80;
    if ((port == -1) && ("https".equalsIgnoreCase(protocol))) {
      return 443;
    }
    return port;
  }
  
  private boolean isRelativeRequest(HttpRequest req) {
    String requestUri = req.getRequestLine().getUri();
    return ("*".equals(requestUri)) || (requestUri.startsWith("/"));
  }
  
  protected String getFullHeaderValue(Header[] headers) {
    if (headers == null) {
      return "";
    }
    StringBuilder buf = new StringBuilder("");
    boolean first = true;
    for (Header hdr : headers) {
      if (!first) {
        buf.append(", ");
      }
      buf.append(hdr.getValue().trim());
      first = false;
    }
    
    return buf.toString();
  }
  









  public String getVariantURI(HttpHost host, HttpRequest req, HttpCacheEntry entry)
  {
    if (!entry.hasVariants()) return getURI(host, req);
    return getVariantKey(req, entry) + getURI(host, req);
  }
  








  public String getVariantKey(HttpRequest req, HttpCacheEntry entry)
  {
    List<String> variantHeaderNames = new ArrayList();
    for (Header varyHdr : entry.getHeaders("Vary")) {
      for (HeaderElement elt : varyHdr.getElements()) {
        variantHeaderNames.add(elt.getName());
      }
    }
    Collections.sort(variantHeaderNames);
    StringBuilder buf;
    try
    {
      buf = new StringBuilder("{");
      boolean first = true;
      for (String headerName : variantHeaderNames) {
        if (!first) {
          buf.append("&");
        }
        buf.append(URLEncoder.encode(headerName, Consts.UTF_8.name()));
        buf.append("=");
        buf.append(URLEncoder.encode(getFullHeaderValue(req.getHeaders(headerName)), Consts.UTF_8.name()));
        
        first = false;
      }
      buf.append("}");
    } catch (UnsupportedEncodingException uee) {
      throw new RuntimeException("couldn't encode to UTF-8", uee);
    }
    return buf.toString();
  }
}
