package org.apache.http.conn.routing;

import java.net.InetAddress;
import org.apache.http.HttpHost;




















public abstract interface RouteInfo
{
  public abstract HttpHost getTargetHost();
  
  public abstract InetAddress getLocalAddress();
  
  public abstract int getHopCount();
  
  public abstract HttpHost getHopTarget(int paramInt);
  
  public abstract HttpHost getProxyHost();
  
  public abstract TunnelType getTunnelType();
  
  public abstract boolean isTunnelled();
  
  public abstract LayerType getLayerType();
  
  public abstract boolean isLayered();
  
  public abstract boolean isSecure();
  
  public static enum TunnelType
  {
    PLAIN,  TUNNELLED;
    




    private TunnelType() {}
  }
  




  public static enum LayerType
  {
    PLAIN,  LAYERED;
    
    private LayerType() {}
  }
}
