package org.apache.http.auth;

import java.util.Queue;
import org.apache.http.annotation.NotThreadSafe;










































@NotThreadSafe
public class AuthState
{
  private AuthProtocolState state;
  private AuthScheme authScheme;
  private AuthScope authScope;
  private Credentials credentials;
  private Queue<AuthOption> authOptions;
  
  public AuthState()
  {
    this.state = AuthProtocolState.UNCHALLENGED;
  }
  




  public void reset()
  {
    this.state = AuthProtocolState.UNCHALLENGED;
    this.authOptions = null;
    this.authScheme = null;
    this.authScope = null;
    this.credentials = null;
  }
  


  public AuthProtocolState getState()
  {
    return this.state;
  }
  


  public void setState(AuthProtocolState state)
  {
    this.state = (state != null ? state : AuthProtocolState.UNCHALLENGED);
  }
  


  public AuthScheme getAuthScheme()
  {
    return this.authScheme;
  }
  


  public Credentials getCredentials()
  {
    return this.credentials;
  }
  







  public void update(AuthScheme authScheme, Credentials credentials)
  {
    if (authScheme == null) {
      throw new IllegalArgumentException("Auth scheme may not be null or empty");
    }
    if (credentials == null) {
      throw new IllegalArgumentException("Credentials may not be null or empty");
    }
    this.authScheme = authScheme;
    this.credentials = credentials;
    this.authOptions = null;
  }
  




  public Queue<AuthOption> getAuthOptions()
  {
    return this.authOptions;
  }
  





  public boolean hasAuthOptions()
  {
    return (this.authOptions != null) && (!this.authOptions.isEmpty());
  }
  






  public void update(Queue<AuthOption> authOptions)
  {
    if ((authOptions == null) || (authOptions.isEmpty())) {
      throw new IllegalArgumentException("Queue of auth options may not be null or empty");
    }
    this.authOptions = authOptions;
    this.authScheme = null;
    this.credentials = null;
  }
  




  @Deprecated
  public void invalidate()
  {
    reset();
  }
  


  @Deprecated
  public boolean isValid()
  {
    return this.authScheme != null;
  }
  






  @Deprecated
  public void setAuthScheme(AuthScheme authScheme)
  {
    if (authScheme == null) {
      reset();
      return;
    }
    this.authScheme = authScheme;
  }
  






  @Deprecated
  public void setCredentials(Credentials credentials)
  {
    this.credentials = credentials;
  }
  






  @Deprecated
  public AuthScope getAuthScope()
  {
    return this.authScope;
  }
  






  @Deprecated
  public void setAuthScope(AuthScope authScope)
  {
    this.authScope = authScope;
  }
  
  public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    buffer.append("state:").append(this.state).append(";");
    if (this.authScheme != null) {
      buffer.append("auth scheme:").append(this.authScheme.getSchemeName()).append(";");
    }
    if (this.credentials != null) {
      buffer.append("credentials present");
    }
    return buffer.toString();
  }
}
