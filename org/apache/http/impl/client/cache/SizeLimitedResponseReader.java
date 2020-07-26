package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.cache.InputLimit;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.cache.ResourceFactory;
import org.apache.http.message.BasicHttpResponse;







































@NotThreadSafe
class SizeLimitedResponseReader
{
  private final ResourceFactory resourceFactory;
  private final long maxResponseSizeBytes;
  private final HttpRequest request;
  private final HttpResponse response;
  private InputStream instream;
  private InputLimit limit;
  private Resource resource;
  private boolean consumed;
  
  public SizeLimitedResponseReader(ResourceFactory resourceFactory, long maxResponseSizeBytes, HttpRequest request, HttpResponse response)
  {
    this.resourceFactory = resourceFactory;
    this.maxResponseSizeBytes = maxResponseSizeBytes;
    this.request = request;
    this.response = response;
  }
  
  protected void readResponse() throws IOException {
    if (!this.consumed) {
      doConsume();
    }
  }
  
  private void ensureNotConsumed() {
    if (this.consumed) {
      throw new IllegalStateException("Response has already been consumed");
    }
  }
  
  private void ensureConsumed() {
    if (!this.consumed) {
      throw new IllegalStateException("Response has not been consumed");
    }
  }
  
  private void doConsume() throws IOException {
    ensureNotConsumed();
    this.consumed = true;
    
    this.limit = new InputLimit(this.maxResponseSizeBytes);
    
    HttpEntity entity = this.response.getEntity();
    if (entity == null) {
      return;
    }
    String uri = this.request.getRequestLine().getUri();
    this.instream = entity.getContent();
    this.resource = this.resourceFactory.generate(uri, this.instream, this.limit);
  }
  
  boolean isLimitReached() {
    ensureConsumed();
    return this.limit.isReached();
  }
  
  Resource getResource() {
    ensureConsumed();
    return this.resource;
  }
  
  HttpResponse getReconstructedResponse() throws IOException {
    ensureConsumed();
    HttpResponse reconstructed = new BasicHttpResponse(this.response.getStatusLine());
    reconstructed.setHeaders(this.response.getAllHeaders());
    
    CombinedEntity combinedEntity = new CombinedEntity(this.resource, this.instream);
    HttpEntity entity = this.response.getEntity();
    if (entity != null) {
      combinedEntity.setContentType(entity.getContentType());
      combinedEntity.setContentEncoding(entity.getContentEncoding());
      combinedEntity.setChunked(entity.isChunked());
    }
    reconstructed.setEntity(combinedEntity);
    return reconstructed;
  }
}
