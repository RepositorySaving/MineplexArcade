package org.apache.http.impl.client.cache;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.protocol.HttpContext;
































class AsynchronousValidator
{
  private final CachingHttpClient cachingClient;
  private final ExecutorService executor;
  private final Set<String> queued;
  private final CacheKeyGenerator cacheKeyGenerator;
  private final Log log = LogFactory.getLog(getClass());
  












  public AsynchronousValidator(CachingHttpClient cachingClient, CacheConfig config)
  {
    this(cachingClient, new ThreadPoolExecutor(config.getAsynchronousWorkersCore(), config.getAsynchronousWorkersMax(), config.getAsynchronousWorkerIdleLifetimeSecs(), TimeUnit.SECONDS, new ArrayBlockingQueue(config.getRevalidationQueueSize())));
  }
  













  AsynchronousValidator(CachingHttpClient cachingClient, ExecutorService executor)
  {
    this.cachingClient = cachingClient;
    this.executor = executor;
    this.queued = new HashSet();
    this.cacheKeyGenerator = new CacheKeyGenerator();
  }
  









  public synchronized void revalidateCacheEntry(HttpHost target, HttpRequest request, HttpContext context, HttpCacheEntry entry)
  {
    String uri = this.cacheKeyGenerator.getVariantURI(target, request, entry);
    
    if (!this.queued.contains(uri)) {
      AsynchronousValidationRequest revalidationRequest = new AsynchronousValidationRequest(this, this.cachingClient, target, request, context, entry, uri);
      

      try
      {
        this.executor.execute(revalidationRequest);
        this.queued.add(uri);
      } catch (RejectedExecutionException ree) {
        this.log.debug("Revalidation for [" + uri + "] not scheduled: " + ree);
      }
    }
  }
  






  synchronized void markComplete(String identifier)
  {
    this.queued.remove(identifier);
  }
  
  Set<String> getScheduledIdentifiers() {
    return Collections.unmodifiableSet(this.queued);
  }
  
  ExecutorService getExecutor() {
    return this.executor;
  }
}
