package org.apache.http.conn.routing;

import java.net.InetAddress;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Immutable;
import org.apache.http.util.LangUtils;




































@Immutable
public final class HttpRoute
  implements RouteInfo, Cloneable
{
  private static final HttpHost[] EMPTY_HTTP_HOST_ARRAY = new HttpHost[0];
  





  private final HttpHost targetHost;
  





  private final InetAddress localAddress;
  





  private final HttpHost[] proxyChain;
  




  private final RouteInfo.TunnelType tunnelled;
  




  private final RouteInfo.LayerType layered;
  




  private final boolean secure;
  





  private HttpRoute(InetAddress local, HttpHost target, HttpHost[] proxies, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered)
  {
    if (target == null) {
      throw new IllegalArgumentException("Target host may not be null.");
    }
    
    if (proxies == null) {
      throw new IllegalArgumentException("Proxies may not be null.");
    }
    
    if ((tunnelled == RouteInfo.TunnelType.TUNNELLED) && (proxies.length == 0)) {
      throw new IllegalArgumentException("Proxy required if tunnelled.");
    }
    


    if (tunnelled == null)
      tunnelled = RouteInfo.TunnelType.PLAIN;
    if (layered == null) {
      layered = RouteInfo.LayerType.PLAIN;
    }
    this.targetHost = target;
    this.localAddress = local;
    this.proxyChain = proxies;
    this.secure = secure;
    this.tunnelled = tunnelled;
    this.layered = layered;
  }
  














  public HttpRoute(HttpHost target, InetAddress local, HttpHost[] proxies, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered)
  {
    this(local, target, toChain(proxies), secure, tunnelled, layered);
  }
  


















  public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure, RouteInfo.TunnelType tunnelled, RouteInfo.LayerType layered)
  {
    this(local, target, toChain(proxy), secure, tunnelled, layered);
  }
  










  public HttpRoute(HttpHost target, InetAddress local, boolean secure)
  {
    this(local, target, EMPTY_HTTP_HOST_ARRAY, secure, RouteInfo.TunnelType.PLAIN, RouteInfo.LayerType.PLAIN);
  }
  





  public HttpRoute(HttpHost target)
  {
    this(null, target, EMPTY_HTTP_HOST_ARRAY, false, RouteInfo.TunnelType.PLAIN, RouteInfo.LayerType.PLAIN);
  }
  














  public HttpRoute(HttpHost target, InetAddress local, HttpHost proxy, boolean secure)
  {
    this(local, target, toChain(proxy), secure, secure ? RouteInfo.TunnelType.TUNNELLED : RouteInfo.TunnelType.PLAIN, secure ? RouteInfo.LayerType.LAYERED : RouteInfo.LayerType.PLAIN);
    

    if (proxy == null) {
      throw new IllegalArgumentException("Proxy host may not be null.");
    }
  }
  








  private static HttpHost[] toChain(HttpHost proxy)
  {
    if (proxy == null) {
      return EMPTY_HTTP_HOST_ARRAY;
    }
    return new HttpHost[] { proxy };
  }
  








  private static HttpHost[] toChain(HttpHost[] proxies)
  {
    if ((proxies == null) || (proxies.length < 1)) {
      return EMPTY_HTTP_HOST_ARRAY;
    }
    for (HttpHost proxy : proxies) {
      if (proxy == null) {
        throw new IllegalArgumentException("Proxy chain may not contain null elements.");
      }
    }
    

    HttpHost[] result = new HttpHost[proxies.length];
    System.arraycopy(proxies, 0, result, 0, proxies.length);
    
    return result;
  }
  


  public final HttpHost getTargetHost()
  {
    return this.targetHost;
  }
  

  public final InetAddress getLocalAddress()
  {
    return this.localAddress;
  }
  
  public final int getHopCount()
  {
    return this.proxyChain.length + 1;
  }
  
  public final HttpHost getHopTarget(int hop)
  {
    if (hop < 0) {
      throw new IllegalArgumentException("Hop index must not be negative: " + hop);
    }
    int hopcount = getHopCount();
    if (hop >= hopcount) {
      throw new IllegalArgumentException("Hop index " + hop + " exceeds route length " + hopcount);
    }
    

    HttpHost result = null;
    if (hop < hopcount - 1) {
      result = this.proxyChain[hop];
    } else {
      result = this.targetHost;
    }
    return result;
  }
  
  public final HttpHost getProxyHost()
  {
    return this.proxyChain.length == 0 ? null : this.proxyChain[0];
  }
  
  public final RouteInfo.TunnelType getTunnelType()
  {
    return this.tunnelled;
  }
  
  public final boolean isTunnelled()
  {
    return this.tunnelled == RouteInfo.TunnelType.TUNNELLED;
  }
  
  public final RouteInfo.LayerType getLayerType()
  {
    return this.layered;
  }
  
  public final boolean isLayered()
  {
    return this.layered == RouteInfo.LayerType.LAYERED;
  }
  
  public final boolean isSecure()
  {
    return this.secure;
  }
  









  public final boolean equals(Object obj)
  {
    if (this == obj) return true;
    if ((obj instanceof HttpRoute)) {
      HttpRoute that = (HttpRoute)obj;
      return (this.secure == that.secure) && (this.tunnelled == that.tunnelled) && (this.layered == that.layered) && (LangUtils.equals(this.targetHost, that.targetHost)) && (LangUtils.equals(this.localAddress, that.localAddress)) && (LangUtils.equals(this.proxyChain, that.proxyChain));
    }
    






    return false;
  }
  







  public final int hashCode()
  {
    int hash = 17;
    hash = LangUtils.hashCode(hash, this.targetHost);
    hash = LangUtils.hashCode(hash, this.localAddress);
    for (int i = 0; i < this.proxyChain.length; i++) {
      hash = LangUtils.hashCode(hash, this.proxyChain[i]);
    }
    hash = LangUtils.hashCode(hash, this.secure);
    hash = LangUtils.hashCode(hash, this.tunnelled);
    hash = LangUtils.hashCode(hash, this.layered);
    return hash;
  }
  






  public final String toString()
  {
    StringBuilder cab = new StringBuilder(50 + getHopCount() * 30);
    if (this.localAddress != null) {
      cab.append(this.localAddress);
      cab.append("->");
    }
    cab.append('{');
    if (this.tunnelled == RouteInfo.TunnelType.TUNNELLED)
      cab.append('t');
    if (this.layered == RouteInfo.LayerType.LAYERED)
      cab.append('l');
    if (this.secure)
      cab.append('s');
    cab.append("}->");
    for (HttpHost aProxyChain : this.proxyChain) {
      cab.append(aProxyChain);
      cab.append("->");
    }
    cab.append(this.targetHost);
    return cab.toString();
  }
  

  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }
}
