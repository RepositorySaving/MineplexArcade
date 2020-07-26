package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;











































@Immutable
public class DefaultHttpRequestRetryHandler
  implements HttpRequestRetryHandler
{
  private final int retryCount;
  private final boolean requestSentRetryEnabled;
  
  public DefaultHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled)
  {
    this.retryCount = retryCount;
    this.requestSentRetryEnabled = requestSentRetryEnabled;
  }
  


  public DefaultHttpRequestRetryHandler()
  {
    this(3, false);
  }
  





  public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
  {
    if (exception == null) {
      throw new IllegalArgumentException("Exception parameter may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    if (executionCount > this.retryCount)
    {
      return false;
    }
    if ((exception instanceof InterruptedIOException))
    {
      return false;
    }
    if ((exception instanceof UnknownHostException))
    {
      return false;
    }
    if ((exception instanceof ConnectException))
    {
      return false;
    }
    if ((exception instanceof SSLException))
    {
      return false;
    }
    
    HttpRequest request = (HttpRequest)context.getAttribute("http.request");
    

    if (requestIsAborted(request)) {
      return false;
    }
    
    if (handleAsIdempotent(request))
    {
      return true;
    }
    
    Boolean b = (Boolean)context.getAttribute("http.request_sent");
    
    boolean sent = (b != null) && (b.booleanValue());
    
    if ((!sent) || (this.requestSentRetryEnabled))
    {

      return true;
    }
    
    return false;
  }
  



  public boolean isRequestSentRetryEnabled()
  {
    return this.requestSentRetryEnabled;
  }
  


  public int getRetryCount()
  {
    return this.retryCount;
  }
  


  protected boolean handleAsIdempotent(HttpRequest request)
  {
    return !(request instanceof HttpEntityEnclosingRequest);
  }
  


  protected boolean requestIsAborted(HttpRequest request)
  {
    HttpRequest req = request;
    if ((request instanceof RequestWrapper)) {
      req = ((RequestWrapper)request).getOriginal();
    }
    return ((req instanceof HttpUriRequest)) && (((HttpUriRequest)req).isAborted());
  }
}
