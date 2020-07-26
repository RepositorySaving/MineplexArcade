package org.apache.http.impl.client;

import java.security.Principal;
import javax.net.ssl.SSLSession;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.conn.HttpRoutedConnection;
import org.apache.http.protocol.HttpContext;













































@Immutable
public class DefaultUserTokenHandler
  implements UserTokenHandler
{
  public Object getUserToken(HttpContext context)
  {
    Principal userPrincipal = null;
    
    AuthState targetAuthState = (AuthState)context.getAttribute("http.auth.target-scope");
    
    if (targetAuthState != null) {
      userPrincipal = getAuthPrincipal(targetAuthState);
      if (userPrincipal == null) {
        AuthState proxyAuthState = (AuthState)context.getAttribute("http.auth.proxy-scope");
        
        userPrincipal = getAuthPrincipal(proxyAuthState);
      }
    }
    
    if (userPrincipal == null) {
      HttpRoutedConnection conn = (HttpRoutedConnection)context.getAttribute("http.connection");
      
      if (conn.isOpen()) {
        SSLSession sslsession = conn.getSSLSession();
        if (sslsession != null) {
          userPrincipal = sslsession.getLocalPrincipal();
        }
      }
    }
    
    return userPrincipal;
  }
  
  private static Principal getAuthPrincipal(AuthState authState) {
    AuthScheme scheme = authState.getAuthScheme();
    if ((scheme != null) && (scheme.isComplete()) && (scheme.isConnectionBased())) {
      Credentials creds = authState.getCredentials();
      if (creds != null) {
        return creds.getUserPrincipal();
      }
    }
    return null;
  }
}
