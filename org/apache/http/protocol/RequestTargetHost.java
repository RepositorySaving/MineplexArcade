package org.apache.http.protocol;

import java.io.IOException;
import java.net.InetAddress;
import org.apache.http.HttpConnection;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Immutable;




































@Immutable
public class RequestTargetHost
  implements HttpRequestInterceptor
{
  public void process(HttpRequest request, HttpContext context)
    throws HttpException, IOException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    
    ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
    String method = request.getRequestLine().getMethod();
    if ((method.equalsIgnoreCase("CONNECT")) && (ver.lessEquals(HttpVersion.HTTP_1_0))) {
      return;
    }
    
    if (!request.containsHeader("Host")) {
      HttpHost targethost = (HttpHost)context.getAttribute("http.target_host");
      
      if (targethost == null) {
        HttpConnection conn = (HttpConnection)context.getAttribute("http.connection");
        
        if ((conn instanceof HttpInetConnection))
        {

          InetAddress address = ((HttpInetConnection)conn).getRemoteAddress();
          int port = ((HttpInetConnection)conn).getRemotePort();
          if (address != null) {
            targethost = new HttpHost(address.getHostName(), port);
          }
        }
        if (targethost == null) {
          if (ver.lessEquals(HttpVersion.HTTP_1_0)) {
            return;
          }
          throw new ProtocolException("Target host missing");
        }
      }
      
      request.addHeader("Host", targethost.toHostString());
    }
  }
}
