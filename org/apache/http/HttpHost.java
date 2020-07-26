package org.apache.http;

import java.io.Serializable;
import java.util.Locale;
import org.apache.http.annotation.Immutable;
import org.apache.http.util.LangUtils;


























































@Immutable
public final class HttpHost
  implements Cloneable, Serializable
{
  private static final long serialVersionUID = -7529410654042457626L;
  public static final String DEFAULT_SCHEME_NAME = "http";
  protected final String hostname;
  protected final String lcHostname;
  protected final int port;
  protected final String schemeName;
  
  public HttpHost(String hostname, int port, String scheme)
  {
    if (hostname == null) {
      throw new IllegalArgumentException("Host name may not be null");
    }
    this.hostname = hostname;
    this.lcHostname = hostname.toLowerCase(Locale.ENGLISH);
    if (scheme != null) {
      this.schemeName = scheme.toLowerCase(Locale.ENGLISH);
    } else {
      this.schemeName = "http";
    }
    this.port = port;
  }
  






  public HttpHost(String hostname, int port)
  {
    this(hostname, port, null);
  }
  




  public HttpHost(String hostname)
  {
    this(hostname, -1, null);
  }
  




  public HttpHost(HttpHost httphost)
  {
    this(httphost.hostname, httphost.port, httphost.schemeName);
  }
  




  public String getHostName()
  {
    return this.hostname;
  }
  




  public int getPort()
  {
    return this.port;
  }
  




  public String getSchemeName()
  {
    return this.schemeName;
  }
  




  public String toURI()
  {
    StringBuilder buffer = new StringBuilder();
    buffer.append(this.schemeName);
    buffer.append("://");
    buffer.append(this.hostname);
    if (this.port != -1) {
      buffer.append(':');
      buffer.append(Integer.toString(this.port));
    }
    return buffer.toString();
  }
  





  public String toHostString()
  {
    if (this.port != -1)
    {
      StringBuilder buffer = new StringBuilder(this.hostname.length() + 6);
      buffer.append(this.hostname);
      buffer.append(":");
      buffer.append(Integer.toString(this.port));
      return buffer.toString();
    }
    return this.hostname;
  }
  


  public String toString()
  {
    return toURI();
  }
  

  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj instanceof HttpHost)) {
      HttpHost that = (HttpHost)obj;
      return (this.lcHostname.equals(that.lcHostname)) && (this.port == that.port) && (this.schemeName.equals(that.schemeName));
    }
    

    return false;
  }
  




  public int hashCode()
  {
    int hash = 17;
    hash = LangUtils.hashCode(hash, this.lcHostname);
    hash = LangUtils.hashCode(hash, this.port);
    hash = LangUtils.hashCode(hash, this.schemeName);
    return hash;
  }
  
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }
}
