package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpVersion;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ProtocolException;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.UnsupportedHttpVersionException;
import org.apache.http.annotation.Immutable;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;


















































@Immutable
public class HttpService
{
  private volatile HttpParams params = null;
  private volatile HttpProcessor processor = null;
  private volatile HttpRequestHandlerResolver handlerResolver = null;
  private volatile ConnectionReuseStrategy connStrategy = null;
  private volatile HttpResponseFactory responseFactory = null;
  private volatile HttpExpectationVerifier expectationVerifier = null;
  


















  public HttpService(HttpProcessor processor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerResolver handlerResolver, HttpExpectationVerifier expectationVerifier, HttpParams params)
  {
    if (processor == null) {
      throw new IllegalArgumentException("HTTP processor may not be null");
    }
    if (connStrategy == null) {
      throw new IllegalArgumentException("Connection reuse strategy may not be null");
    }
    if (responseFactory == null) {
      throw new IllegalArgumentException("Response factory may not be null");
    }
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    this.processor = processor;
    this.connStrategy = connStrategy;
    this.responseFactory = responseFactory;
    this.handlerResolver = handlerResolver;
    this.expectationVerifier = expectationVerifier;
    this.params = params;
  }
  















  public HttpService(HttpProcessor processor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerResolver handlerResolver, HttpParams params)
  {
    this(processor, connStrategy, responseFactory, handlerResolver, null, params);
  }
  













  @Deprecated
  public HttpService(HttpProcessor proc, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory)
  {
    setHttpProcessor(proc);
    setConnReuseStrategy(connStrategy);
    setResponseFactory(responseFactory);
  }
  


  @Deprecated
  public void setHttpProcessor(HttpProcessor processor)
  {
    if (processor == null) {
      throw new IllegalArgumentException("HTTP processor may not be null");
    }
    this.processor = processor;
  }
  


  @Deprecated
  public void setConnReuseStrategy(ConnectionReuseStrategy connStrategy)
  {
    if (connStrategy == null) {
      throw new IllegalArgumentException("Connection reuse strategy may not be null");
    }
    this.connStrategy = connStrategy;
  }
  


  @Deprecated
  public void setResponseFactory(HttpResponseFactory responseFactory)
  {
    if (responseFactory == null) {
      throw new IllegalArgumentException("Response factory may not be null");
    }
    this.responseFactory = responseFactory;
  }
  


  @Deprecated
  public void setParams(HttpParams params)
  {
    this.params = params;
  }
  


  @Deprecated
  public void setHandlerResolver(HttpRequestHandlerResolver handlerResolver)
  {
    this.handlerResolver = handlerResolver;
  }
  


  @Deprecated
  public void setExpectationVerifier(HttpExpectationVerifier expectationVerifier)
  {
    this.expectationVerifier = expectationVerifier;
  }
  
  public HttpParams getParams() {
    return this.params;
  }
  











  public void handleRequest(HttpServerConnection conn, HttpContext context)
    throws IOException, HttpException
  {
    context.setAttribute("http.connection", conn);
    
    HttpResponse response = null;
    
    try
    {
      HttpRequest request = conn.receiveRequestHeader();
      request.setParams(new DefaultedHttpParams(request.getParams(), this.params));
      

      if ((request instanceof HttpEntityEnclosingRequest))
      {
        if (((HttpEntityEnclosingRequest)request).expectContinue()) {
          response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_1, 100, context);
          
          response.setParams(new DefaultedHttpParams(response.getParams(), this.params));
          

          if (this.expectationVerifier != null) {
            try {
              this.expectationVerifier.verify(request, response, context);
            } catch (HttpException ex) {
              response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_0, 500, context);
              
              response.setParams(new DefaultedHttpParams(response.getParams(), this.params));
              
              handleException(ex, response);
            }
          }
          if (response.getStatusLine().getStatusCode() < 200)
          {

            conn.sendResponseHeader(response);
            conn.flush();
            response = null;
            conn.receiveRequestEntity((HttpEntityEnclosingRequest)request);
          }
        } else {
          conn.receiveRequestEntity((HttpEntityEnclosingRequest)request);
        }
      }
      
      context.setAttribute("http.request", request);
      
      if (response == null) {
        response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_1, 200, context);
        
        response.setParams(new DefaultedHttpParams(response.getParams(), this.params));
        
        this.processor.process(request, context);
        doService(request, response, context);
      }
      

      if ((request instanceof HttpEntityEnclosingRequest)) {
        HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
        EntityUtils.consume(entity);
      }
    }
    catch (HttpException ex) {
      response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_0, 500, context);
      

      response.setParams(new DefaultedHttpParams(response.getParams(), this.params));
      
      handleException(ex, response);
    }
    
    context.setAttribute("http.response", response);
    
    this.processor.process(response, context);
    conn.sendResponseHeader(response);
    conn.sendResponseEntity(response);
    conn.flush();
    
    if (!this.connStrategy.keepAlive(response, context)) {
      conn.close();
    }
  }
  







  protected void handleException(HttpException ex, HttpResponse response)
  {
    if ((ex instanceof MethodNotSupportedException)) {
      response.setStatusCode(501);
    } else if ((ex instanceof UnsupportedHttpVersionException)) {
      response.setStatusCode(505);
    } else if ((ex instanceof ProtocolException)) {
      response.setStatusCode(400);
    } else {
      response.setStatusCode(500);
    }
    String message = ex.getMessage();
    if (message == null) {
      message = ex.toString();
    }
    byte[] msg = EncodingUtils.getAsciiBytes(message);
    ByteArrayEntity entity = new ByteArrayEntity(msg);
    entity.setContentType("text/plain; charset=US-ASCII");
    response.setEntity(entity);
  }
  


















  protected void doService(HttpRequest request, HttpResponse response, HttpContext context)
    throws HttpException, IOException
  {
    HttpRequestHandler handler = null;
    if (this.handlerResolver != null) {
      String requestURI = request.getRequestLine().getUri();
      handler = this.handlerResolver.lookup(requestURI);
    }
    if (handler != null) {
      handler.handle(request, response, context);
    } else {
      response.setStatusCode(501);
    }
  }
}
