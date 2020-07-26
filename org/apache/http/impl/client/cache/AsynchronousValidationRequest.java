package org.apache.http.impl.client.cache;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.ProtocolException;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.protocol.HttpContext;






























class AsynchronousValidationRequest
  implements Runnable
{
  private final AsynchronousValidator parent;
  private final CachingHttpClient cachingClient;
  private final HttpHost target;
  private final HttpRequest request;
  private final HttpContext context;
  private final HttpCacheEntry cacheEntry;
  private final String identifier;
  private final Log log = LogFactory.getLog(getClass());
  














  AsynchronousValidationRequest(AsynchronousValidator parent, CachingHttpClient cachingClient, HttpHost target, HttpRequest request, HttpContext context, HttpCacheEntry cacheEntry, String identifier)
  {
    this.parent = parent;
    this.cachingClient = cachingClient;
    this.target = target;
    this.request = request;
    this.context = context;
    this.cacheEntry = cacheEntry;
    this.identifier = identifier;
  }
  
  public void run() {
    try {
      this.cachingClient.revalidateCacheEntry(this.target, this.request, this.context, this.cacheEntry);
    } catch (IOException ioe) {
      this.log.debug("Asynchronous revalidation failed due to exception: " + ioe);
    } catch (ProtocolException pe) {
      this.log.error("ProtocolException thrown during asynchronous revalidation: " + pe);
    } finally {
      this.parent.markComplete(this.identifier);
    }
  }
  
  String getIdentifier() {
    return this.identifier;
  }
}
