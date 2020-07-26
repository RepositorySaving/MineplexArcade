package org.apache.http.impl.client;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;





















































public class DecompressingHttpClient
  implements HttpClient
{
  private HttpClient backend;
  private HttpRequestInterceptor acceptEncodingInterceptor;
  private HttpResponseInterceptor contentEncodingInterceptor;
  
  public DecompressingHttpClient(HttpClient backend)
  {
    this(backend, new RequestAcceptEncoding(), new ResponseContentEncoding());
  }
  

  DecompressingHttpClient(HttpClient backend, HttpRequestInterceptor requestInterceptor, HttpResponseInterceptor responseInterceptor)
  {
    this.backend = backend;
    this.acceptEncodingInterceptor = requestInterceptor;
    this.contentEncodingInterceptor = responseInterceptor;
  }
  
  public HttpParams getParams() {
    return this.backend.getParams();
  }
  
  public ClientConnectionManager getConnectionManager() {
    return this.backend.getConnectionManager();
  }
  
  public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException
  {
    return execute(getHttpHost(request), request, (HttpContext)null);
  }
  
  HttpHost getHttpHost(HttpUriRequest request) {
    URI uri = request.getURI();
    return new HttpHost(uri.getAuthority());
  }
  
  public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException
  {
    return execute(getHttpHost(request), request, context);
  }
  
  public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException
  {
    return execute(target, request, (HttpContext)null);
  }
  
  public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException
  {
    try {
      if (context == null) context = new BasicHttpContext();
      HttpRequest wrapped = new RequestWrapper(request);
      this.acceptEncodingInterceptor.process(wrapped, context);
      HttpResponse response = this.backend.execute(target, wrapped, context);
      this.contentEncodingInterceptor.process(response, context);
      if (Boolean.TRUE.equals(context.getAttribute("http.client.response.uncompressed"))) {
        response.removeHeaders("Content-Length");
        response.removeHeaders("Content-Encoding");
        response.removeHeaders("Content-MD5");
      }
      return response;
    } catch (HttpException e) {
      throw new RuntimeException(e);
    }
  }
  
  public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler)
    throws IOException, ClientProtocolException
  {
    return execute(getHttpHost(request), request, responseHandler);
  }
  
  public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
    throws IOException, ClientProtocolException
  {
    return execute(getHttpHost(request), request, responseHandler, context);
  }
  
  public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler)
    throws IOException, ClientProtocolException
  {
    return execute(target, request, responseHandler, null);
  }
  
  public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context)
    throws IOException, ClientProtocolException
  {
    HttpResponse response = execute(target, request, context);
    try { HttpEntity entity;
      return responseHandler.handleResponse(response);
    } finally {
      HttpEntity entity = response.getEntity();
      if (entity != null) EntityUtils.consume(entity);
    }
  }
}
