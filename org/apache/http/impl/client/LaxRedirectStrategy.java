package org.apache.http.impl.client;

import org.apache.http.annotation.Immutable;









































@Immutable
public class LaxRedirectStrategy
  extends DefaultRedirectStrategy
{
  private static final String[] REDIRECT_METHODS = { "GET", "POST", "HEAD" };
  




  protected boolean isRedirectable(String method)
  {
    for (String m : REDIRECT_METHODS) {
      if (m.equalsIgnoreCase(method)) {
        return true;
      }
    }
    return false;
  }
}
