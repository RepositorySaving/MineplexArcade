package org.apache.http.impl;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;
import org.apache.http.protocol.HttpContext;



































@Immutable
public class NoConnectionReuseStrategy
  implements ConnectionReuseStrategy
{
  public boolean keepAlive(HttpResponse response, HttpContext context)
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    
    return false;
  }
}
