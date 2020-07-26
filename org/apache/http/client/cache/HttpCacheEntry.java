package org.apache.http.client.cache;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.message.HeaderGroup;





























































@Immutable
public class HttpCacheEntry
  implements Serializable
{
  private static final long serialVersionUID = -6300496422359477413L;
  private final Date requestDate;
  private final Date responseDate;
  private final StatusLine statusLine;
  private final HeaderGroup responseHeaders;
  private final Resource resource;
  private final Map<String, String> variantMap;
  
  public HttpCacheEntry(Date requestDate, Date responseDate, StatusLine statusLine, Header[] responseHeaders, Resource resource, Map<String, String> variantMap)
  {
    if (requestDate == null) {
      throw new IllegalArgumentException("Request date may not be null");
    }
    if (responseDate == null) {
      throw new IllegalArgumentException("Response date may not be null");
    }
    if (statusLine == null) {
      throw new IllegalArgumentException("Status line may not be null");
    }
    if (responseHeaders == null) {
      throw new IllegalArgumentException("Response headers may not be null");
    }
    if (resource == null) {
      throw new IllegalArgumentException("Resource may not be null");
    }
    this.requestDate = requestDate;
    this.responseDate = responseDate;
    this.statusLine = statusLine;
    this.responseHeaders = new HeaderGroup();
    this.responseHeaders.setHeaders(responseHeaders);
    this.resource = resource;
    this.variantMap = (variantMap != null ? new HashMap(variantMap) : null);
  }
  

















  public HttpCacheEntry(Date requestDate, Date responseDate, StatusLine statusLine, Header[] responseHeaders, Resource resource)
  {
    this(requestDate, responseDate, statusLine, responseHeaders, resource, new HashMap());
  }
  



  public StatusLine getStatusLine()
  {
    return this.statusLine;
  }
  


  public ProtocolVersion getProtocolVersion()
  {
    return this.statusLine.getProtocolVersion();
  }
  



  public String getReasonPhrase()
  {
    return this.statusLine.getReasonPhrase();
  }
  


  public int getStatusCode()
  {
    return this.statusLine.getStatusCode();
  }
  




  public Date getRequestDate()
  {
    return this.requestDate;
  }
  



  public Date getResponseDate()
  {
    return this.responseDate;
  }
  


  public Header[] getAllHeaders()
  {
    return this.responseHeaders.getAllHeaders();
  }
  



  public Header getFirstHeader(String name)
  {
    return this.responseHeaders.getFirstHeader(name);
  }
  



  public Header[] getHeaders(String name)
  {
    return this.responseHeaders.getHeaders(name);
  }
  


  public Resource getResource()
  {
    return this.resource;
  }
  





  public boolean hasVariants()
  {
    return getFirstHeader("Vary") != null;
  }
  








  public Map<String, String> getVariantMap()
  {
    return Collections.unmodifiableMap(this.variantMap);
  }
  




  public String toString()
  {
    return "[request date=" + this.requestDate + "; response date=" + this.responseDate + "; statusLine=" + this.statusLine + "]";
  }
}
