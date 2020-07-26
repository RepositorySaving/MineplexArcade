package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.ThreadSafe;


































@ThreadSafe
public final class ImmutableHttpProcessor
  implements HttpProcessor
{
  private final HttpRequestInterceptor[] requestInterceptors;
  private final HttpResponseInterceptor[] responseInterceptors;
  
  public ImmutableHttpProcessor(HttpRequestInterceptor[] requestInterceptors, HttpResponseInterceptor[] responseInterceptors)
  {
    if (requestInterceptors != null) {
      int count = requestInterceptors.length;
      this.requestInterceptors = new HttpRequestInterceptor[count];
      for (int i = 0; i < count; i++) {
        this.requestInterceptors[i] = requestInterceptors[i];
      }
    } else {
      this.requestInterceptors = new HttpRequestInterceptor[0];
    }
    if (responseInterceptors != null) {
      int count = responseInterceptors.length;
      this.responseInterceptors = new HttpResponseInterceptor[count];
      for (int i = 0; i < count; i++) {
        this.responseInterceptors[i] = responseInterceptors[i];
      }
    } else {
      this.responseInterceptors = new HttpResponseInterceptor[0];
    }
  }
  


  public ImmutableHttpProcessor(HttpRequestInterceptorList requestInterceptors, HttpResponseInterceptorList responseInterceptors)
  {
    if (requestInterceptors != null) {
      int count = requestInterceptors.getRequestInterceptorCount();
      this.requestInterceptors = new HttpRequestInterceptor[count];
      for (int i = 0; i < count; i++) {
        this.requestInterceptors[i] = requestInterceptors.getRequestInterceptor(i);
      }
    } else {
      this.requestInterceptors = new HttpRequestInterceptor[0];
    }
    if (responseInterceptors != null) {
      int count = responseInterceptors.getResponseInterceptorCount();
      this.responseInterceptors = new HttpResponseInterceptor[count];
      for (int i = 0; i < count; i++) {
        this.responseInterceptors[i] = responseInterceptors.getResponseInterceptor(i);
      }
    } else {
      this.responseInterceptors = new HttpResponseInterceptor[0];
    }
  }
  
  public ImmutableHttpProcessor(HttpRequestInterceptor[] requestInterceptors) {
    this(requestInterceptors, null);
  }
  
  public ImmutableHttpProcessor(HttpResponseInterceptor[] responseInterceptors) {
    this(null, responseInterceptors);
  }
  
  public void process(HttpRequest request, HttpContext context)
    throws IOException, HttpException
  {
    for (int i = 0; i < this.requestInterceptors.length; i++) {
      this.requestInterceptors[i].process(request, context);
    }
  }
  
  public void process(HttpResponse response, HttpContext context)
    throws IOException, HttpException
  {
    for (int i = 0; i < this.responseInterceptors.length; i++) {
      this.responseInterceptors[i].process(response, context);
    }
  }
}
