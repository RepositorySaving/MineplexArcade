package org.apache.http.impl.conn;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.HttpInetSocketAddress;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;




























































@ThreadSafe
public class DefaultClientConnectionOperator
  implements ClientConnectionOperator
{
  private final Log log = LogFactory.getLog(getClass());
  



  protected final SchemeRegistry schemeRegistry;
  


  protected final DnsResolver dnsResolver;
  



  public DefaultClientConnectionOperator(SchemeRegistry schemes)
  {
    if (schemes == null) {
      throw new IllegalArgumentException("Scheme registry amy not be null");
    }
    this.schemeRegistry = schemes;
    this.dnsResolver = new SystemDefaultDnsResolver();
  }
  








  public DefaultClientConnectionOperator(SchemeRegistry schemes, DnsResolver dnsResolver)
  {
    if (schemes == null) {
      throw new IllegalArgumentException("Scheme registry may not be null");
    }
    

    if (dnsResolver == null) {
      throw new IllegalArgumentException("DNS resolver may not be null");
    }
    
    this.schemeRegistry = schemes;
    this.dnsResolver = dnsResolver;
  }
  
  public OperatedClientConnection createConnection() {
    return new DefaultClientConnection();
  }
  



  public void openConnection(OperatedClientConnection conn, HttpHost target, InetAddress local, HttpContext context, HttpParams params)
    throws IOException
  {
    if (conn == null) {
      throw new IllegalArgumentException("Connection may not be null");
    }
    if (target == null) {
      throw new IllegalArgumentException("Target host may not be null");
    }
    if (params == null) {
      throw new IllegalArgumentException("Parameters may not be null");
    }
    if (conn.isOpen()) {
      throw new IllegalStateException("Connection must not be open");
    }
    
    Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
    SchemeSocketFactory sf = schm.getSchemeSocketFactory();
    
    InetAddress[] addresses = resolveHostname(target.getHostName());
    int port = schm.resolvePort(target.getPort());
    for (int i = 0; i < addresses.length; i++) {
      InetAddress address = addresses[i];
      boolean last = i == addresses.length - 1;
      
      Socket sock = sf.createSocket(params);
      conn.opening(sock, target);
      
      InetSocketAddress remoteAddress = new HttpInetSocketAddress(target, address, port);
      InetSocketAddress localAddress = null;
      if (local != null) {
        localAddress = new InetSocketAddress(local, 0);
      }
      if (this.log.isDebugEnabled()) {
        this.log.debug("Connecting to " + remoteAddress);
      }
      try {
        Socket connsock = sf.connectSocket(sock, remoteAddress, localAddress, params);
        if (sock != connsock) {
          sock = connsock;
          conn.opening(sock, target);
        }
        prepareSocket(sock, context, params);
        conn.openCompleted(sf.isSecure(sock), params);
        return;
      } catch (ConnectException ex) {
        if (last) {
          throw new HttpHostConnectException(target, ex);
        }
      } catch (ConnectTimeoutException ex) {
        if (last) {
          throw ex;
        }
      }
      if (this.log.isDebugEnabled()) {
        this.log.debug("Connect to " + remoteAddress + " timed out. " + "Connection will be retried using another IP address");
      }
    }
  }
  



  public void updateSecureConnection(OperatedClientConnection conn, HttpHost target, HttpContext context, HttpParams params)
    throws IOException
  {
    if (conn == null) {
      throw new IllegalArgumentException("Connection may not be null");
    }
    if (target == null) {
      throw new IllegalArgumentException("Target host may not be null");
    }
    if (params == null) {
      throw new IllegalArgumentException("Parameters may not be null");
    }
    if (!conn.isOpen()) {
      throw new IllegalStateException("Connection must be open");
    }
    
    Scheme schm = this.schemeRegistry.getScheme(target.getSchemeName());
    if (!(schm.getSchemeSocketFactory() instanceof SchemeLayeredSocketFactory)) {
      throw new IllegalArgumentException("Target scheme (" + schm.getName() + ") must have layered socket factory.");
    }
    


    SchemeLayeredSocketFactory lsf = (SchemeLayeredSocketFactory)schm.getSchemeSocketFactory();
    Socket sock;
    try {
      sock = lsf.createLayeredSocket(conn.getSocket(), target.getHostName(), target.getPort(), params);
    }
    catch (ConnectException ex) {
      throw new HttpHostConnectException(target, ex);
    }
    prepareSocket(sock, context, params);
    conn.update(sock, target, lsf.isSecure(sock), params);
  }
  










  protected void prepareSocket(Socket sock, HttpContext context, HttpParams params)
    throws IOException
  {
    sock.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
    sock.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
    
    int linger = HttpConnectionParams.getLinger(params);
    if (linger >= 0) {
      sock.setSoLinger(linger > 0, linger);
    }
  }
  












  protected InetAddress[] resolveHostname(String host)
    throws UnknownHostException
  {
    return this.dnsResolver.resolve(host);
  }
}
