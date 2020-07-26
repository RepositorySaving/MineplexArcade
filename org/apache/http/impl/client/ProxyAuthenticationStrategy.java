package org.apache.http.impl.client;

import org.apache.http.annotation.Immutable;



































@Immutable
public class ProxyAuthenticationStrategy
  extends AuthenticationStrategyImpl
{
  public ProxyAuthenticationStrategy()
  {
    super(407, "Proxy-Authenticate", "http.auth.proxy-scheme-pref");
  }
}
