package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.StatusLine;
import org.apache.http.annotation.ThreadSafe;



































@ThreadSafe
public class ResponseDate
  implements HttpResponseInterceptor
{
  private static final HttpDateGenerator DATE_GENERATOR = new HttpDateGenerator();
  



  public void process(HttpResponse response, HttpContext context)
    throws HttpException, IOException
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null.");
    }
    
    int status = response.getStatusLine().getStatusCode();
    if ((status >= 200) && (!response.containsHeader("Date")))
    {
      String httpdate = DATE_GENERATOR.getCurrentDate();
      response.setHeader("Date", httpdate);
    }
  }
}
