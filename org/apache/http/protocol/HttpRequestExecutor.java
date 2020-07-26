package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.params.HttpParams;


































































@Immutable
public class HttpRequestExecutor
{
  protected boolean canResponseHaveBody(HttpRequest request, HttpResponse response)
  {
    if ("HEAD".equalsIgnoreCase(request.getRequestLine().getMethod())) {
      return false;
    }
    int status = response.getStatusLine().getStatusCode();
    return (status >= 200) && (status != 204) && (status != 304) && (status != 205);
  }
  

















  public HttpResponse execute(HttpRequest request, HttpClientConnection conn, HttpContext context)
    throws IOException, HttpException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (conn == null) {
      throw new IllegalArgumentException("Client connection may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    try
    {
      HttpResponse response = doSendRequest(request, conn, context);
      if (response == null) {}
      return doReceiveResponse(request, conn, context);
    }
    catch (IOException ex)
    {
      closeConnection(conn);
      throw ex;
    } catch (HttpException ex) {
      closeConnection(conn);
      throw ex;
    } catch (RuntimeException ex) {
      closeConnection(conn);
      throw ex;
    }
  }
  
  private static final void closeConnection(HttpClientConnection conn) {
    try {
      conn.close();
    }
    catch (IOException ignore) {}
  }
  














  public void preProcess(HttpRequest request, HttpProcessor processor, HttpContext context)
    throws HttpException, IOException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (processor == null) {
      throw new IllegalArgumentException("HTTP processor may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    context.setAttribute("http.request", request);
    processor.process(request, context);
  }
  
























  protected HttpResponse doSendRequest(HttpRequest request, HttpClientConnection conn, HttpContext context)
    throws IOException, HttpException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (conn == null) {
      throw new IllegalArgumentException("HTTP connection may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    
    HttpResponse response = null;
    
    context.setAttribute("http.connection", conn);
    context.setAttribute("http.request_sent", Boolean.FALSE);
    
    conn.sendRequestHeader(request);
    if ((request instanceof HttpEntityEnclosingRequest))
    {


      boolean sendentity = true;
      ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
      
      if ((((HttpEntityEnclosingRequest)request).expectContinue()) && (!ver.lessEquals(HttpVersion.HTTP_1_0)))
      {

        conn.flush();
        

        int tms = request.getParams().getIntParameter("http.protocol.wait-for-continue", 2000);
        

        if (conn.isResponseAvailable(tms)) {
          response = conn.receiveResponseHeader();
          if (canResponseHaveBody(request, response)) {
            conn.receiveResponseEntity(response);
          }
          int status = response.getStatusLine().getStatusCode();
          if (status < 200) {
            if (status != 100) {
              throw new ProtocolException("Unexpected response: " + response.getStatusLine());
            }
            

            response = null;
          } else {
            sendentity = false;
          }
        }
      }
      if (sendentity) {
        conn.sendRequestEntity((HttpEntityEnclosingRequest)request);
      }
    }
    conn.flush();
    context.setAttribute("http.request_sent", Boolean.TRUE);
    return response;
  }
  

















  protected HttpResponse doReceiveResponse(HttpRequest request, HttpClientConnection conn, HttpContext context)
    throws HttpException, IOException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    if (conn == null) {
      throw new IllegalArgumentException("HTTP connection may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    
    HttpResponse response = null;
    int statuscode = 0;
    
    while ((response == null) || (statuscode < 200))
    {
      response = conn.receiveResponseHeader();
      if (canResponseHaveBody(request, response)) {
        conn.receiveResponseEntity(response);
      }
      statuscode = response.getStatusLine().getStatusCode();
    }
    

    return response;
  }
  





















  public void postProcess(HttpResponse response, HttpProcessor processor, HttpContext context)
    throws HttpException, IOException
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    if (processor == null) {
      throw new IllegalArgumentException("HTTP processor may not be null");
    }
    if (context == null) {
      throw new IllegalArgumentException("HTTP context may not be null");
    }
    context.setAttribute("http.response", response);
    processor.process(response, context);
  }
}
