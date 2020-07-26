package org.apache.http.impl.auth;

import java.security.Principal;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.auth.params.AuthParams;
import org.apache.http.message.BufferedHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EncodingUtils;














































@NotThreadSafe
public class BasicScheme
  extends RFC2617Scheme
{
  private boolean complete;
  
  public BasicScheme(ChallengeState challengeState)
  {
    super(challengeState);
    this.complete = false;
  }
  
  public BasicScheme() {
    this(null);
  }
  




  public String getSchemeName()
  {
    return "basic";
  }
  








  public void processChallenge(Header header)
    throws MalformedChallengeException
  {
    super.processChallenge(header);
    this.complete = true;
  }
  





  public boolean isComplete()
  {
    return this.complete;
  }
  




  public boolean isConnectionBased()
  {
    return false;
  }
  


  @Deprecated
  public Header authenticate(Credentials credentials, HttpRequest request)
    throws AuthenticationException
  {
    return authenticate(credentials, request, new BasicHttpContext());
  }
  















  public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context)
    throws AuthenticationException
  {
    if (credentials == null) {
      throw new IllegalArgumentException("Credentials may not be null");
    }
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    
    String charset = AuthParams.getCredentialCharset(request.getParams());
    return authenticate(credentials, charset, isProxy());
  }
  











  public static Header authenticate(Credentials credentials, String charset, boolean proxy)
  {
    if (credentials == null) {
      throw new IllegalArgumentException("Credentials may not be null");
    }
    if (charset == null) {
      throw new IllegalArgumentException("charset may not be null");
    }
    
    StringBuilder tmp = new StringBuilder();
    tmp.append(credentials.getUserPrincipal().getName());
    tmp.append(":");
    tmp.append(credentials.getPassword() == null ? "null" : credentials.getPassword());
    
    byte[] base64password = Base64.encodeBase64(EncodingUtils.getBytes(tmp.toString(), charset));
    

    CharArrayBuffer buffer = new CharArrayBuffer(32);
    if (proxy) {
      buffer.append("Proxy-Authorization");
    } else {
      buffer.append("Authorization");
    }
    buffer.append(": Basic ");
    buffer.append(base64password, 0, base64password.length);
    
    return new BufferedHeader(buffer);
  }
}
