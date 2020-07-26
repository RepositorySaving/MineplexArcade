package org.apache.http.impl.conn;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HttpContext;





























































@NotThreadSafe
public class ProxySelectorRoutePlanner
  implements HttpRoutePlanner
{
  protected final SchemeRegistry schemeRegistry;
  protected ProxySelector proxySelector;
  
  public ProxySelectorRoutePlanner(SchemeRegistry schreg, ProxySelector prosel)
  {
    if (schreg == null) {
      throw new IllegalArgumentException("SchemeRegistry must not be null.");
    }
    
    this.schemeRegistry = schreg;
    this.proxySelector = prosel;
  }
  




  public ProxySelector getProxySelector()
  {
    return this.proxySelector;
  }
  





  public void setProxySelector(ProxySelector prosel)
  {
    this.proxySelector = prosel;
  }
  


  public HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context)
    throws HttpException
  {
    if (request == null) {
      throw new IllegalStateException("Request must not be null.");
    }
    


    HttpRoute route = ConnRouteParams.getForcedRoute(request.getParams());
    
    if (route != null) {
      return route;
    }
    


    if (target == null) {
      throw new IllegalStateException("Target host must not be null.");
    }
    

    InetAddress local = ConnRouteParams.getLocalAddress(request.getParams());
    
    HttpHost proxy = determineProxy(target, request, context);
    
    Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
    


    boolean secure = schm.isLayered();
    
    if (proxy == null) {
      route = new HttpRoute(target, local, secure);
    } else {
      route = new HttpRoute(target, local, proxy, secure);
    }
    return route;
  }
  















  protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context)
    throws HttpException
  {
    ProxySelector psel = this.proxySelector;
    if (psel == null)
      psel = ProxySelector.getDefault();
    if (psel == null) {
      return null;
    }
    URI targetURI = null;
    try {
      targetURI = new URI(target.toURI());
    } catch (URISyntaxException usx) {
      throw new HttpException("Cannot convert host to URI: " + target, usx);
    }
    
    List<Proxy> proxies = psel.select(targetURI);
    
    Proxy p = chooseProxy(proxies, target, request, context);
    
    HttpHost result = null;
    if (p.type() == Proxy.Type.HTTP)
    {
      if (!(p.address() instanceof InetSocketAddress)) {
        throw new HttpException("Unable to handle non-Inet proxy address: " + p.address());
      }
      
      InetSocketAddress isa = (InetSocketAddress)p.address();
      
      result = new HttpHost(getHost(isa), isa.getPort());
    }
    
    return result;
  }
  













  protected String getHost(InetSocketAddress isa)
  {
    return isa.isUnresolved() ? isa.getHostName() : isa.getAddress().getHostAddress();
  }
  





















  protected Proxy chooseProxy(List<Proxy> proxies, HttpHost target, HttpRequest request, HttpContext context)
  {
    if ((proxies == null) || (proxies.isEmpty())) {
      throw new IllegalArgumentException("Proxy list must not be empty.");
    }
    

    Proxy result = null;
    

    for (int i = 0; (result == null) && (i < proxies.size()); i++)
    {
      Proxy p = (Proxy)proxies.get(i);
      switch (1.$SwitchMap$java$net$Proxy$Type[p.type().ordinal()])
      {
      case 1: 
      case 2: 
        result = p;
      }
      
    }
    





    if (result == null)
    {


      result = Proxy.NO_PROXY;
    }
    
    return result;
  }
}
