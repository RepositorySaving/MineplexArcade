package org.apache.http.impl.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;




































@ThreadSafe
public class BasicCredentialsProvider
  implements CredentialsProvider
{
  private final ConcurrentHashMap<AuthScope, Credentials> credMap;
  
  public BasicCredentialsProvider()
  {
    this.credMap = new ConcurrentHashMap();
  }
  

  public void setCredentials(AuthScope authscope, Credentials credentials)
  {
    if (authscope == null) {
      throw new IllegalArgumentException("Authentication scope may not be null");
    }
    this.credMap.put(authscope, credentials);
  }
  










  private static Credentials matchCredentials(Map<AuthScope, Credentials> map, AuthScope authscope)
  {
    Credentials creds = (Credentials)map.get(authscope);
    if (creds == null)
    {

      int bestMatchFactor = -1;
      AuthScope bestMatch = null;
      for (AuthScope current : map.keySet()) {
        int factor = authscope.match(current);
        if (factor > bestMatchFactor) {
          bestMatchFactor = factor;
          bestMatch = current;
        }
      }
      if (bestMatch != null) {
        creds = (Credentials)map.get(bestMatch);
      }
    }
    return creds;
  }
  
  public Credentials getCredentials(AuthScope authscope) {
    if (authscope == null) {
      throw new IllegalArgumentException("Authentication scope may not be null");
    }
    return matchCredentials(this.credMap, authscope);
  }
  
  public void clear() {
    this.credMap.clear();
  }
  
  public String toString()
  {
    return this.credMap.toString();
  }
}
