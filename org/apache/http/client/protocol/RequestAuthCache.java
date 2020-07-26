package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthProtocolState;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.protocol.HttpContext;




































@Immutable
public class RequestAuthCache
  implements HttpRequestInterceptor
{
  private final Log log = LogFactory.getLog(getClass());
  



  public void process(HttpRequest request, HttpContext context)
    throws HttpException, IOException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    
    AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
    if (authCache == null) {
      this.log.debug("Auth cache not set in the context");
      return;
    }
    
    CredentialsProvider credsProvider = (CredentialsProvider)context.getAttribute("http.auth.credentials-provider");
    
    if (credsProvider == null) {
      this.log.debug("Credentials provider not set in the context");
      return;
    }
    
    HttpHost target = (HttpHost)context.getAttribute("http.target_host");
    if (target.getPort() < 0) {
      SchemeRegistry schemeRegistry = (SchemeRegistry)context.getAttribute("http.scheme-registry");
      
      Scheme scheme = schemeRegistry.getScheme(target);
      target = new HttpHost(target.getHostName(), scheme.resolvePort(target.getPort()), target.getSchemeName());
    }
    

    AuthState targetState = (AuthState)context.getAttribute("http.auth.target-scope");
    if ((target != null) && (targetState != null) && (targetState.getState() == AuthProtocolState.UNCHALLENGED)) {
      AuthScheme authScheme = authCache.get(target);
      if (authScheme != null) {
        doPreemptiveAuth(target, authScheme, targetState, credsProvider);
      }
    }
    
    HttpHost proxy = (HttpHost)context.getAttribute("http.proxy_host");
    AuthState proxyState = (AuthState)context.getAttribute("http.auth.proxy-scope");
    if ((proxy != null) && (proxyState != null) && (proxyState.getState() == AuthProtocolState.UNCHALLENGED)) {
      AuthScheme authScheme = authCache.get(proxy);
      if (authScheme != null) {
        doPreemptiveAuth(proxy, authScheme, proxyState, credsProvider);
      }
    }
  }
  



  private void doPreemptiveAuth(HttpHost host, AuthScheme authScheme, AuthState authState, CredentialsProvider credsProvider)
  {
    String schemeName = authScheme.getSchemeName();
    if (this.log.isDebugEnabled()) {
      this.log.debug("Re-using cached '" + schemeName + "' auth scheme for " + host);
    }
    
    AuthScope authScope = new AuthScope(host, AuthScope.ANY_REALM, schemeName);
    Credentials creds = credsProvider.getCredentials(authScope);
    
    if (creds != null) {
      authState.setState(AuthProtocolState.SUCCESS);
      authState.update(authScheme, creds);
    } else {
      this.log.debug("No credentials for preemptive authentication");
    }
  }
}
