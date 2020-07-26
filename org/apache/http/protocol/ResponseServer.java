package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Immutable;
import org.apache.http.params.HttpParams;











































@Immutable
public class ResponseServer
  implements HttpResponseInterceptor
{
  public void process(HttpResponse response, HttpContext context)
    throws HttpException, IOException
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (!response.containsHeader("Server")) {
      String s = (String)response.getParams().getParameter("http.origin-server");
      
      if (s != null) {
        response.addHeader("Server", s);
      }
    }
  }
}
