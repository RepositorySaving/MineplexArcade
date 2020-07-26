package org.apache.http.protocol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;











































@ThreadSafe
public class UriPatternMatcher<T>
{
  @GuardedBy("this")
  private final Map<String, T> map;
  
  public UriPatternMatcher()
  {
    this.map = new HashMap();
  }
  





  public synchronized void register(String pattern, T obj)
  {
    if (pattern == null) {
      throw new IllegalArgumentException("URI request pattern may not be null");
    }
    this.map.put(pattern, obj);
  }
  




  public synchronized void unregister(String pattern)
  {
    if (pattern == null) {
      return;
    }
    this.map.remove(pattern);
  }
  


  @Deprecated
  public synchronized void setHandlers(Map<String, T> map)
  {
    if (map == null) {
      throw new IllegalArgumentException("Map of handlers may not be null");
    }
    this.map.clear();
    this.map.putAll(map);
  }
  



  public synchronized void setObjects(Map<String, T> map)
  {
    if (map == null) {
      throw new IllegalArgumentException("Map of handlers may not be null");
    }
    this.map.clear();
    this.map.putAll(map);
  }
  





  public synchronized Map<String, T> getObjects()
  {
    return this.map;
  }
  





  public synchronized T lookup(String requestURI)
  {
    if (requestURI == null) {
      throw new IllegalArgumentException("Request URI may not be null");
    }
    
    int index = requestURI.indexOf("?");
    if (index != -1) {
      requestURI = requestURI.substring(0, index);
    }
    

    T obj = this.map.get(requestURI);
    String bestMatch; Iterator<String> it; if (obj == null)
    {
      bestMatch = null;
      for (it = this.map.keySet().iterator(); it.hasNext();) {
        String pattern = (String)it.next();
        if (matchUriRequestPattern(pattern, requestURI))
        {
          if ((bestMatch == null) || (bestMatch.length() < pattern.length()) || ((bestMatch.length() == pattern.length()) && (pattern.endsWith("*"))))
          {

            obj = this.map.get(pattern);
            bestMatch = pattern;
          }
        }
      }
    }
    return obj;
  }
  







  protected boolean matchUriRequestPattern(String pattern, String requestUri)
  {
    if (pattern.equals("*")) {
      return true;
    }
    return ((pattern.endsWith("*")) && (requestUri.startsWith(pattern.substring(0, pattern.length() - 1)))) || ((pattern.startsWith("*")) && (requestUri.endsWith(pattern.substring(1, pattern.length()))));
  }
}
