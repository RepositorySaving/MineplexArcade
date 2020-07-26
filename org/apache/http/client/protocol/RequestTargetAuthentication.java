package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthState;
import org.apache.http.protocol.HttpContext;




































@Immutable
public class RequestTargetAuthentication
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
    
    String method = request.getRequestLine().getMethod();
    if (method.equalsIgnoreCase("CONNECT")) {
      return;
    }
    
    if (request.containsHeader("Authorization")) {
      return;
    }
    

    AuthState authState = (AuthState)context.getAttribute("http.auth.target-scope");
    
    if (authState == null) {
      this.log.debug("Target auth state not set in the context");
      return;
    }
    if (this.log.isDebugEnabled()) {
      this.log.debug("Target auth state: " + authState.getState());
    }
    process(authState, request, context);
  }
}
