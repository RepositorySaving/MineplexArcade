package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.annotation.ThreadSafe;



































@ThreadSafe
public class RequestDate
  implements HttpRequestInterceptor
{
  private static final HttpDateGenerator DATE_GENERATOR = new HttpDateGenerator();
  



  public void process(HttpRequest request, HttpContext context)
    throws HttpException, IOException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null.");
    }
    
    if (((request instanceof HttpEntityEnclosingRequest)) && (!request.containsHeader("Date")))
    {
      String httpdate = DATE_GENERATOR.getCurrentDate();
      request.setHeader("Date", httpdate);
    }
  }
}
