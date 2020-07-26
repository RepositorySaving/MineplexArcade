package org.apache.http.impl.auth;

import org.apache.http.annotation.Immutable;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.params.HttpParams;
































@Immutable
public class SPNegoSchemeFactory
  implements AuthSchemeFactory
{
  private final boolean stripPort;
  
  public SPNegoSchemeFactory(boolean stripPort)
  {
    this.stripPort = stripPort;
  }
  
  public SPNegoSchemeFactory() {
    this(false);
  }
  
  public AuthScheme newInstance(HttpParams params) {
    return new SPNegoScheme(this.stripPort);
  }
  
  public boolean isStripPort() {
    return this.stripPort;
  }
}
