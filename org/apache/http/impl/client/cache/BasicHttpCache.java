package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.cache.ResourceFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHttpResponse;



























class BasicHttpCache
  implements HttpCache
{
  private final CacheKeyGenerator uriExtractor;
  private final ResourceFactory resourceFactory;
  private final long maxObjectSizeBytes;
  private final CacheEntryUpdater cacheEntryUpdater;
  private final CachedHttpResponseGenerator responseGenerator;
  private final CacheInvalidator cacheInvalidator;
  private final HttpCacheStorage storage;
  private final Log log = LogFactory.getLog(getClass());
  
  public BasicHttpCache(ResourceFactory resourceFactory, HttpCacheStorage storage, CacheConfig config) {
    this.resourceFactory = resourceFactory;
    this.uriExtractor = new CacheKeyGenerator();
    this.cacheEntryUpdater = new CacheEntryUpdater(resourceFactory);
    this.maxObjectSizeBytes = config.getMaxObjectSize();
    this.responseGenerator = new CachedHttpResponseGenerator();
    this.storage = storage;
    this.cacheInvalidator = new CacheInvalidator(this.uriExtractor, this.storage);
  }
  
  public BasicHttpCache(CacheConfig config) {
    this(new HeapResourceFactory(), new BasicHttpCacheStorage(config), config);
  }
  
  public BasicHttpCache() {
    this(new CacheConfig());
  }
  
  public void flushCacheEntriesFor(HttpHost host, HttpRequest request) throws IOException
  {
    String uri = this.uriExtractor.getURI(host, request);
    this.storage.removeEntry(uri);
  }
  
  public void flushInvalidatedCacheEntriesFor(HttpHost host, HttpRequest request, HttpResponse response) {
    this.cacheInvalidator.flushInvalidatedCacheEntries(host, request, response);
  }
  
  void storeInCache(HttpHost target, HttpRequest request, HttpCacheEntry entry) throws IOException
  {
    if (entry.hasVariants()) {
      storeVariantEntry(target, request, entry);
    } else {
      storeNonVariantEntry(target, request, entry);
    }
  }
  
  void storeNonVariantEntry(HttpHost target, HttpRequest req, HttpCacheEntry entry) throws IOException
  {
    String uri = this.uriExtractor.getURI(target, req);
    this.storage.putEntry(uri, entry);
  }
  

  void storeVariantEntry(HttpHost target, final HttpRequest req, final HttpCacheEntry entry)
    throws IOException
  {
    String parentURI = this.uriExtractor.getURI(target, req);
    final String variantURI = this.uriExtractor.getVariantURI(target, req, entry);
    this.storage.putEntry(variantURI, entry);
    
    HttpCacheUpdateCallback callback = new HttpCacheUpdateCallback()
    {
      public HttpCacheEntry update(HttpCacheEntry existing) throws IOException {
        return BasicHttpCache.this.doGetUpdatedParentEntry(req.getRequestLine().getUri(), existing, entry, BasicHttpCache.this.uriExtractor.getVariantKey(req, entry), variantURI);
      }
    };
    



    try
    {
      this.storage.updateEntry(parentURI, callback);
    } catch (HttpCacheUpdateException e) {
      this.log.warn("Could not update key [" + parentURI + "]", e);
    }
  }
  
  public void reuseVariantEntryFor(HttpHost target, final HttpRequest req, Variant variant) throws IOException
  {
    String parentCacheKey = this.uriExtractor.getURI(target, req);
    final HttpCacheEntry entry = variant.getEntry();
    final String variantKey = this.uriExtractor.getVariantKey(req, entry);
    final String variantCacheKey = variant.getCacheKey();
    
    HttpCacheUpdateCallback callback = new HttpCacheUpdateCallback()
    {
      public HttpCacheEntry update(HttpCacheEntry existing) throws IOException {
        return BasicHttpCache.this.doGetUpdatedParentEntry(req.getRequestLine().getUri(), existing, entry, variantKey, variantCacheKey);
      }
    };
    
    try
    {
      this.storage.updateEntry(parentCacheKey, callback);
    } catch (HttpCacheUpdateException e) {
      this.log.warn("Could not update key [" + parentCacheKey + "]", e);
    }
  }
  
  boolean isIncompleteResponse(HttpResponse resp, Resource resource) {
    int status = resp.getStatusLine().getStatusCode();
    if ((status != 200) && (status != 206))
    {
      return false;
    }
    Header hdr = resp.getFirstHeader("Content-Length");
    if (hdr == null) return false;
    int contentLength;
    try {
      contentLength = Integer.parseInt(hdr.getValue());
    } catch (NumberFormatException nfe) {
      return false;
    }
    return resource.length() < contentLength;
  }
  
  HttpResponse generateIncompleteResponseError(HttpResponse response, Resource resource)
  {
    int contentLength = Integer.parseInt(response.getFirstHeader("Content-Length").getValue());
    HttpResponse error = new BasicHttpResponse(HttpVersion.HTTP_1_1, 502, "Bad Gateway");
    
    error.setHeader("Content-Type", "text/plain;charset=UTF-8");
    String msg = String.format("Received incomplete response with Content-Length %d but actual body length %d", new Object[] { Integer.valueOf(contentLength), Long.valueOf(resource.length()) });
    

    byte[] msgBytes = msg.getBytes();
    error.setHeader("Content-Length", Integer.toString(msgBytes.length));
    error.setEntity(new ByteArrayEntity(msgBytes));
    return error;
  }
  



  HttpCacheEntry doGetUpdatedParentEntry(String requestId, HttpCacheEntry existing, HttpCacheEntry entry, String variantKey, String variantCacheKey)
    throws IOException
  {
    HttpCacheEntry src = existing;
    if (src == null) {
      src = entry;
    }
    
    Resource resource = this.resourceFactory.copy(requestId, src.getResource());
    Map<String, String> variantMap = new HashMap(src.getVariantMap());
    variantMap.put(variantKey, variantCacheKey);
    return new HttpCacheEntry(src.getRequestDate(), src.getResponseDate(), src.getStatusLine(), src.getAllHeaders(), resource, variantMap);
  }
  






  public HttpCacheEntry updateCacheEntry(HttpHost target, HttpRequest request, HttpCacheEntry stale, HttpResponse originResponse, Date requestSent, Date responseReceived)
    throws IOException
  {
    HttpCacheEntry updatedEntry = this.cacheEntryUpdater.updateCacheEntry(request.getRequestLine().getUri(), stale, requestSent, responseReceived, originResponse);
    




    storeInCache(target, request, updatedEntry);
    return updatedEntry;
  }
  
  public HttpCacheEntry updateVariantCacheEntry(HttpHost target, HttpRequest request, HttpCacheEntry stale, HttpResponse originResponse, Date requestSent, Date responseReceived, String cacheKey)
    throws IOException
  {
    HttpCacheEntry updatedEntry = this.cacheEntryUpdater.updateCacheEntry(request.getRequestLine().getUri(), stale, requestSent, responseReceived, originResponse);
    




    this.storage.putEntry(cacheKey, updatedEntry);
    return updatedEntry;
  }
  

  public HttpResponse cacheAndReturnResponse(HttpHost host, HttpRequest request, HttpResponse originResponse, Date requestSent, Date responseReceived)
    throws IOException
  {
    SizeLimitedResponseReader responseReader = getResponseReader(request, originResponse);
    responseReader.readResponse();
    
    if (responseReader.isLimitReached()) {
      return responseReader.getReconstructedResponse();
    }
    
    Resource resource = responseReader.getResource();
    if (isIncompleteResponse(originResponse, resource)) {
      return generateIncompleteResponseError(originResponse, resource);
    }
    
    HttpCacheEntry entry = new HttpCacheEntry(requestSent, responseReceived, originResponse.getStatusLine(), originResponse.getAllHeaders(), resource);
    




    storeInCache(host, request, entry);
    return this.responseGenerator.generateResponse(entry);
  }
  
  SizeLimitedResponseReader getResponseReader(HttpRequest request, HttpResponse backEndResponse) {
    return new SizeLimitedResponseReader(this.resourceFactory, this.maxObjectSizeBytes, request, backEndResponse);
  }
  
  public HttpCacheEntry getCacheEntry(HttpHost host, HttpRequest request) throws IOException
  {
    HttpCacheEntry root = this.storage.getEntry(this.uriExtractor.getURI(host, request));
    if (root == null) return null;
    if (!root.hasVariants()) return root;
    String variantCacheKey = (String)root.getVariantMap().get(this.uriExtractor.getVariantKey(request, root));
    if (variantCacheKey == null) return null;
    return this.storage.getEntry(variantCacheKey);
  }
  
  public void flushInvalidatedCacheEntriesFor(HttpHost host, HttpRequest request) throws IOException
  {
    this.cacheInvalidator.flushInvalidatedCacheEntries(host, request);
  }
  
  public Map<String, Variant> getVariantCacheEntriesWithEtags(HttpHost host, HttpRequest request) throws IOException
  {
    Map<String, Variant> variants = new HashMap();
    HttpCacheEntry root = this.storage.getEntry(this.uriExtractor.getURI(host, request));
    if ((root == null) || (!root.hasVariants())) return variants;
    for (Map.Entry<String, String> variant : root.getVariantMap().entrySet()) {
      String variantKey = (String)variant.getKey();
      String variantCacheKey = (String)variant.getValue();
      addVariantWithEtag(variantKey, variantCacheKey, variants);
    }
    return variants;
  }
  
  private void addVariantWithEtag(String variantKey, String variantCacheKey, Map<String, Variant> variants)
    throws IOException
  {
    HttpCacheEntry entry = this.storage.getEntry(variantCacheKey);
    if (entry == null) return;
    Header etagHeader = entry.getFirstHeader("ETag");
    if (etagHeader == null) return;
    variants.put(etagHeader.getValue(), new Variant(variantKey, variantCacheKey, entry));
  }
}
