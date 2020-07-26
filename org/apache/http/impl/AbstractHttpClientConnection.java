package org.apache.http.impl;

import java.io.IOException;
import java.net.SocketTimeoutException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.StatusLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.entity.EntityDeserializer;
import org.apache.http.impl.entity.EntitySerializer;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.HttpRequestWriter;
import org.apache.http.io.EofSensor;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpParams;














































@NotThreadSafe
public abstract class AbstractHttpClientConnection
  implements HttpClientConnection
{
  private final EntitySerializer entityserializer;
  private final EntityDeserializer entitydeserializer;
  private SessionInputBuffer inbuffer = null;
  private SessionOutputBuffer outbuffer = null;
  private EofSensor eofSensor = null;
  private HttpMessageParser<HttpResponse> responseParser = null;
  private HttpMessageWriter<HttpRequest> requestWriter = null;
  private HttpConnectionMetricsImpl metrics = null;
  








  public AbstractHttpClientConnection()
  {
    this.entityserializer = createEntitySerializer();
    this.entitydeserializer = createEntityDeserializer();
  }
  







  protected abstract void assertOpen()
    throws IllegalStateException;
  







  protected EntityDeserializer createEntityDeserializer()
  {
    return new EntityDeserializer(new LaxContentLengthStrategy());
  }
  










  protected EntitySerializer createEntitySerializer()
  {
    return new EntitySerializer(new StrictContentLengthStrategy());
  }
  









  protected HttpResponseFactory createHttpResponseFactory()
  {
    return new DefaultHttpResponseFactory();
  }
  
















  protected HttpMessageParser<HttpResponse> createResponseParser(SessionInputBuffer buffer, HttpResponseFactory responseFactory, HttpParams params)
  {
    return new DefaultHttpResponseParser(buffer, null, responseFactory, params);
  }
  














  protected HttpMessageWriter<HttpRequest> createRequestWriter(SessionOutputBuffer buffer, HttpParams params)
  {
    return new HttpRequestWriter(buffer, null, params);
  }
  




  protected HttpConnectionMetricsImpl createConnectionMetrics(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric)
  {
    return new HttpConnectionMetricsImpl(inTransportMetric, outTransportMetric);
  }
  


















  protected void init(SessionInputBuffer inbuffer, SessionOutputBuffer outbuffer, HttpParams params)
  {
    if (inbuffer == null) {
      throw new IllegalArgumentException("Input session buffer may not be null");
    }
    if (outbuffer == null) {
      throw new IllegalArgumentException("Output session buffer may not be null");
    }
    this.inbuffer = inbuffer;
    this.outbuffer = outbuffer;
    if ((inbuffer instanceof EofSensor)) {
      this.eofSensor = ((EofSensor)inbuffer);
    }
    this.responseParser = createResponseParser(inbuffer, createHttpResponseFactory(), params);
    


    this.requestWriter = createRequestWriter(outbuffer, params);
    
    this.metrics = createConnectionMetrics(inbuffer.getMetrics(), outbuffer.getMetrics());
  }
  
  public boolean isResponseAvailable(int timeout)
    throws IOException
  {
    assertOpen();
    try {
      return this.inbuffer.isDataAvailable(timeout);
    } catch (SocketTimeoutException ex) {}
    return false;
  }
  
  public void sendRequestHeader(HttpRequest request)
    throws HttpException, IOException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    assertOpen();
    this.requestWriter.write(request);
    this.metrics.incrementRequestCount();
  }
  
  public void sendRequestEntity(HttpEntityEnclosingRequest request) throws HttpException, IOException
  {
    if (request == null) {
      throw new IllegalArgumentException("HTTP request may not be null");
    }
    assertOpen();
    if (request.getEntity() == null) {
      return;
    }
    this.entityserializer.serialize(this.outbuffer, request, request.getEntity());
  }
  

  protected void doFlush()
    throws IOException
  {
    this.outbuffer.flush();
  }
  
  public void flush() throws IOException {
    assertOpen();
    doFlush();
  }
  
  public HttpResponse receiveResponseHeader() throws HttpException, IOException
  {
    assertOpen();
    HttpResponse response = (HttpResponse)this.responseParser.parse();
    if (response.getStatusLine().getStatusCode() >= 200) {
      this.metrics.incrementResponseCount();
    }
    return response;
  }
  
  public void receiveResponseEntity(HttpResponse response) throws HttpException, IOException
  {
    if (response == null) {
      throw new IllegalArgumentException("HTTP response may not be null");
    }
    assertOpen();
    HttpEntity entity = this.entitydeserializer.deserialize(this.inbuffer, response);
    response.setEntity(entity);
  }
  
  protected boolean isEof() {
    return (this.eofSensor != null) && (this.eofSensor.isEof());
  }
  
  public boolean isStale() {
    if (!isOpen()) {
      return true;
    }
    if (isEof()) {
      return true;
    }
    try {
      this.inbuffer.isDataAvailable(1);
      return isEof();
    } catch (SocketTimeoutException ex) {
      return false;
    } catch (IOException ex) {}
    return true;
  }
  
  public HttpConnectionMetrics getMetrics()
  {
    return this.metrics;
  }
}
