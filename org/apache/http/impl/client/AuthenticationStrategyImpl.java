package org.apache.http.impl.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthOption;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeRegistry;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.client.AuthCache;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharArrayBuffer;




























@Immutable
class AuthenticationStrategyImpl
  implements AuthenticationStrategy
{
  private final Log log = LogFactory.getLog(getClass());
  
  private static final List<String> DEFAULT_SCHEME_PRIORITY = Collections.unmodifiableList(Arrays.asList(new String[] { "negotiate", "Kerberos", "NTLM", "Digest", "Basic" }));
  

  private final int challengeCode;
  

  private final String headerName;
  

  private final String prefParamName;
  


  AuthenticationStrategyImpl(int challengeCode, String headerName, String prefParamName)
  {
    this.challengeCode = challengeCode;
    this.headerName = headerName;
    this.prefParamName = prefParamName;
  }
  


  public boolean isAuthenticationRequested(HttpHost authhost, HttpResponse response, HttpContext context)
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    int status = response.getStatusLine().getStatusCode();
    return status == this.challengeCode;
  }
  

  public Map<String, Header> getChallenges(HttpHost authhost, HttpResponse response, HttpContext context)
    throws MalformedChallengeException
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    Header[] headers = response.getHeaders(this.headerName);
    Map<String, Header> map = new HashMap(headers.length);
    for (Header header : headers) { int pos;
      CharArrayBuffer buffer;
      int pos;
      if ((header instanceof FormattedHeader)) {
        CharArrayBuffer buffer = ((FormattedHeader)header).getBuffer();
        pos = ((FormattedHeader)header).getValuePos();
      } else {
        String s = header.getValue();
        if (s == null) {
          throw new MalformedChallengeException("Header value is null");
        }
        buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        pos = 0;
      }
      while ((pos < buffer.length()) && (HTTP.isWhitespace(buffer.charAt(pos)))) {
        pos++;
      }
      int beginIndex = pos;
      while ((pos < buffer.length()) && (!HTTP.isWhitespace(buffer.charAt(pos)))) {
        pos++;
      }
      int endIndex = pos;
      String s = buffer.substring(beginIndex, endIndex);
      map.put(s.toLowerCase(Locale.US), header);
    }
    return map;
  }
  


  public Queue<AuthOption> select(Map<String, Header> challenges, HttpHost authhost, HttpResponse response, HttpContext context)
    throws MalformedChallengeException
  {
    if (challenges == null) {
      throw new IllegalArgumentException("Map of auth challenges may not be null");
    }
    if (authhost == null) {
      throw new IllegalArgumentException("Host may not be null");
    }
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    
    Queue<AuthOption> options = new LinkedList();
    AuthSchemeRegistry registry = (AuthSchemeRegistry)context.getAttribute("http.authscheme-registry");
    
    if (registry == null) {
      this.log.debug("Auth scheme registry not set in the context");
      return options;
    }
    CredentialsProvider credsProvider = (CredentialsProvider)context.getAttribute("http.auth.credentials-provider");
    
    if (credsProvider == null) {
      this.log.debug("Credentials provider not set in the context");
      return options;
    }
    

    List<String> authPrefs = (List)response.getParams().getParameter(this.prefParamName);
    if (authPrefs == null) {
      authPrefs = DEFAULT_SCHEME_PRIORITY;
    }
    if (this.log.isDebugEnabled()) {
      this.log.debug("Authentication schemes in the order of preference: " + authPrefs);
    }
    
    for (String id : authPrefs) {
      Header challenge = (Header)challenges.get(id.toLowerCase(Locale.US));
      if (challenge != null) {
        try {
          AuthScheme authScheme = registry.getAuthScheme(id, response.getParams());
          authScheme.processChallenge(challenge);
          
          AuthScope authScope = new AuthScope(authhost.getHostName(), authhost.getPort(), authScheme.getRealm(), authScheme.getSchemeName());
          




          Credentials credentials = credsProvider.getCredentials(authScope);
          if (credentials != null) {
            options.add(new AuthOption(authScheme, credentials));
          }
        } catch (IllegalStateException e) {
          if (this.log.isWarnEnabled()) {
            this.log.warn("Authentication scheme " + id + " not supported");
          }
          
        }
        
      } else if (this.log.isDebugEnabled()) {
        this.log.debug("Challenge for " + id + " authentication scheme not available");
      }
    }
    

    return options;
  }
  
  public void authSucceeded(HttpHost authhost, AuthScheme authScheme, HttpContext context)
  {
    if (authhost == null) {
      throw new IllegalArgumentException("Host may not be null");
    }
    if (authScheme == null) {
      throw new IllegalArgumentException("Auth scheme may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    if (isCachable(authScheme)) {
      AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
      if (authCache == null) {
        authCache = new BasicAuthCache();
        context.setAttribute("http.auth.auth-cache", authCache);
      }
      if (this.log.isDebugEnabled()) {
        this.log.debug("Caching '" + authScheme.getSchemeName() + "' auth scheme for " + authhost);
      }
      
      authCache.put(authhost, authScheme);
    }
  }
  
  protected boolean isCachable(AuthScheme authScheme) {
    if ((authScheme == null) || (!authScheme.isComplete())) {
      return false;
    }
    String schemeName = authScheme.getSchemeName();
    return (schemeName.equalsIgnoreCase("Basic")) || (schemeName.equalsIgnoreCase("Digest"));
  }
  

  public void authFailed(HttpHost authhost, AuthScheme authScheme, HttpContext context)
  {
    if (authhost == null) {
      throw new IllegalArgumentException("Host may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    AuthCache authCache = (AuthCache)context.getAttribute("http.auth.auth-cache");
    if (authCache != null) {
      if (this.log.isDebugEnabled()) {
        this.log.debug("Clearing cached auth scheme for " + authhost);
      }
      authCache.remove(authhost);
    }
  }
}
