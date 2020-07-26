package org.apache.http.auth;

import java.security.Principal;

public abstract interface Credentials
{
  public abstract Principal getUserPrincipal();
  
  public abstract String getPassword();
}
