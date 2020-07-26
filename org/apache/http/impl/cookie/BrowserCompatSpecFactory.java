package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.params.HttpParams;









































@Immutable
public class BrowserCompatSpecFactory
  implements CookieSpecFactory
{
  public CookieSpec newInstance(HttpParams params)
  {
    if (params != null)
    {
      String[] patterns = null;
      Collection<?> param = (Collection)params.getParameter("http.protocol.cookie-datepatterns");
      
      if (param != null) {
        patterns = new String[param.size()];
        patterns = (String[])param.toArray(patterns);
      }
      return new BrowserCompatSpec(patterns);
    }
    return new BrowserCompatSpec();
  }
}
