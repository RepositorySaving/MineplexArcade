package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthState;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;






































@Immutable
public class RequestProxyAuthentication
  extends RequestAuthenticationBase
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
    
    if (request.containsHeader("Proxy-Authorization")) {
      return;
    }
    
    HttpRoutedConnection conn = (HttpRoutedConnection)context.getAttribute("http.connection");
    
    if (conn == null) {
      this.log.debug("HTTP connection not set in the context");
      return;
    }
    HttpRoute route = conn.getRoute();
    if (route.isTunnelled()) {
      return;
    }
    

    AuthState authState = (AuthState)context.getAttribute("http.auth.proxy-scope");
    
    if (authState == null) {
      this.log.debug("Proxy auth state not set in the context");
      return;
    }
    if (this.log.isDebugEnabled()) {
      this.log.debug("Proxy auth state: " + authState.getState());
    }
    process(authState, request, context);
  }
}
