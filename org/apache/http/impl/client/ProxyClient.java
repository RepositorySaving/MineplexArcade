package org.apache.http.impl.client;

import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLSession;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.protocol.RequestProxyAuthentication;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
































public class ProxyClient
{
  private final HttpProcessor httpProcessor;
  private final HttpRequestExecutor requestExec;
  private final ProxyAuthenticationStrategy proxyAuthStrategy;
  private final HttpAuthenticator authenticator;
  private final AuthState proxyAuthState;
  private final AuthSchemeRegistry authSchemeRegistry;
  private final ConnectionReuseStrategy reuseStrategy;
  private final HttpParams params;
  
  public ProxyClient(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    this.httpProcessor = new ImmutableHttpProcessor(new HttpRequestInterceptor[] { new RequestContent(), new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent(), new RequestProxyAuthentication() });
    





    this.requestExec = new HttpRequestExecutor();
    this.proxyAuthStrategy = new ProxyAuthenticationStrategy();
    this.authenticator = new HttpAuthenticator();
    this.proxyAuthState = new AuthState();
    this.authSchemeRegistry = new AuthSchemeRegistry();
    this.authSchemeRegistry.register("Basic", new BasicSchemeFactory());
    this.authSchemeRegistry.register("Digest", new DigestSchemeFactory());
    this.authSchemeRegistry.register("NTLM", new NTLMSchemeFactory());
    this.authSchemeRegistry.register("negotiate", new SPNegoSchemeFactory());
    this.authSchemeRegistry.register("Kerberos", new KerberosSchemeFactory());
    this.reuseStrategy = new DefaultConnectionReuseStrategy();
    this.params = params;
  }
  
  public ProxyClient() {
    this(new BasicHttpParams());
  }
  
  public HttpParams getParams() {
    return this.params;
  }
  
  public AuthSchemeRegistry getAuthSchemeRegistry() {
    return this.authSchemeRegistry;
  }
  

  public Socket tunnel(HttpHost proxy, HttpHost target, Credentials credentials)
    throws IOException, HttpException
  {
    ProxyConnection conn = new ProxyConnection(new HttpRoute(proxy));
    HttpContext context = new BasicHttpContext();
    HttpResponse response = null;
    for (;;)
    {
      if (!conn.isOpen()) {
        Socket socket = new Socket(proxy.getHostName(), proxy.getPort());
        conn.bind(socket, this.params);
      }
      String host = target.getHostName();
      int port = target.getPort();
      if (port < 0) {
        port = 80;
      }
      
      StringBuilder buffer = new StringBuilder(host.length() + 6);
      buffer.append(host);
      buffer.append(':');
      buffer.append(Integer.toString(port));
      
      String authority = buffer.toString();
      ProtocolVersion ver = HttpProtocolParams.getVersion(this.params);
      HttpRequest connect = new BasicHttpRequest("CONNECT", authority, ver);
      connect.setParams(this.params);
      
      BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
      credsProvider.setCredentials(new AuthScope(proxy), credentials);
      

      context.setAttribute("http.target_host", target);
      context.setAttribute("http.proxy_host", proxy);
      context.setAttribute("http.connection", conn);
      context.setAttribute("http.request", connect);
      context.setAttribute("http.auth.proxy-scope", this.proxyAuthState);
      context.setAttribute("http.auth.credentials-provider", credsProvider);
      context.setAttribute("http.authscheme-registry", this.authSchemeRegistry);
      
      this.requestExec.preProcess(connect, this.httpProcessor, context);
      
      response = this.requestExec.execute(connect, conn, context);
      
      response.setParams(this.params);
      this.requestExec.postProcess(response, this.httpProcessor, context);
      
      int status = response.getStatusLine().getStatusCode();
      if (status < 200) {
        throw new HttpException("Unexpected response to CONNECT request: " + response.getStatusLine());
      }
      

      if (HttpClientParams.isAuthenticating(this.params)) {
        if (!this.authenticator.isAuthenticationRequested(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context))
          break;
        if (!this.authenticator.authenticate(proxy, response, this.proxyAuthStrategy, this.proxyAuthState, context)) {
          break;
        }
        if (this.reuseStrategy.keepAlive(response, context))
        {
          HttpEntity entity = response.getEntity();
          EntityUtils.consume(entity);
        } else {
          conn.close();
        }
      }
    }
    






    int status = response.getStatusLine().getStatusCode();
    
    if (status > 299)
    {

      HttpEntity entity = response.getEntity();
      if (entity != null) {
        response.setEntity(new BufferedHttpEntity(entity));
      }
      
      conn.close();
      throw new TunnelRefusedException("CONNECT refused by proxy: " + response.getStatusLine(), response);
    }
    
    return conn.getSocket();
  }
  
  static class ProxyConnection extends DefaultHttpClientConnection implements HttpRoutedConnection
  {
    private final HttpRoute route;
    
    ProxyConnection(HttpRoute route)
    {
      this.route = route;
    }
    
    public HttpRoute getRoute() {
      return this.route;
    }
    
    public boolean isSecure() {
      return false;
    }
    
    public SSLSession getSSLSession() {
      return null;
    }
    
    public Socket getSocket()
    {
      return super.getSocket();
    }
  }
}
