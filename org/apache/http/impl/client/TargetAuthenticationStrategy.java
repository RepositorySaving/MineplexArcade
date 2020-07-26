package org.apache.http.impl.client;

import org.apache.http.annotation.Immutable;



































@Immutable
public class TargetAuthenticationStrategy
  extends AuthenticationStrategyImpl
{
  public TargetAuthenticationStrategy()
  {
    super(401, "WWW-Authenticate", "http.auth.target-scheme-pref");
  }
}
