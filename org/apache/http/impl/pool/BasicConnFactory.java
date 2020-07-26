package org.apache.http.impl.pool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.net.ssl.SSLSocketFactory;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Immutable;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.pool.ConnFactory;














































@Immutable
public class BasicConnFactory
  implements ConnFactory<HttpHost, HttpClientConnection>
{
  private final SSLSocketFactory sslfactory;
  private final HttpParams params;
  
  public BasicConnFactory(SSLSocketFactory sslfactory, HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP params may not be null");
    }
    this.sslfactory = sslfactory;
    this.params = params;
  }
  
  public BasicConnFactory(HttpParams params) {
    this(null, params);
  }
  
  protected HttpClientConnection create(Socket socket, HttpParams params) throws IOException {
    DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
    conn.bind(socket, params);
    return conn;
  }
  
  public HttpClientConnection create(HttpHost host) throws IOException {
    String scheme = host.getSchemeName();
    Socket socket = null;
    if ("http".equalsIgnoreCase(scheme))
      socket = new Socket();
    if (("https".equalsIgnoreCase(scheme)) && 
      (this.sslfactory != null)) {
      socket = this.sslfactory.createSocket();
    }
    
    if (socket == null) {
      throw new IOException(scheme + " scheme is not supported");
    }
    int connectTimeout = HttpConnectionParams.getConnectionTimeout(this.params);
    int soTimeout = HttpConnectionParams.getSoTimeout(this.params);
    
    socket.setSoTimeout(soTimeout);
    socket.connect(new InetSocketAddress(host.getHostName(), host.getPort()), connectTimeout);
    return create(socket, this.params);
  }
}
