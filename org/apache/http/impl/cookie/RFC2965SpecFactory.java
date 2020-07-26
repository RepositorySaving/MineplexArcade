package org.apache.http.impl.cookie;

import java.util.Collection;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.params.HttpParams;










































@Immutable
public class RFC2965SpecFactory
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
      boolean singleHeader = params.getBooleanParameter("http.protocol.single-cookie-header", false);
      

      return new RFC2965Spec(patterns, singleHeader);
    }
    return new RFC2965Spec();
  }
}
