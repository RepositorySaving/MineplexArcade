package org.apache.http.impl.client;

import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;












































@Deprecated
@Immutable
public class DefaultTargetAuthenticationHandler
  extends AbstractAuthenticationHandler
{
  public boolean isAuthenticationRequested(HttpResponse response, HttpContext context)
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    int status = response.getStatusLine().getStatusCode();
    return status == 401;
  }
  
  public Map<String, Header> getChallenges(HttpResponse response, HttpContext context)
    throws MalformedChallengeException
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    Header[] headers = response.getHeaders("WWW-Authenticate");
    return parseChallenges(headers);
  }
  



  protected List<String> getAuthPreferences(HttpResponse response, HttpContext context)
  {
    List<String> authpref = (List)response.getParams().getParameter("http.auth.target-scheme-pref");
    
    if (authpref != null) {
      return authpref;
    }
    return super.getAuthPreferences(response, context);
  }
}
