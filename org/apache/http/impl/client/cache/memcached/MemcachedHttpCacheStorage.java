package org.apache.http.impl.client.cache.memcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.OperationTimeoutException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheEntrySerializer;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;
import org.apache.http.impl.client.cache.CacheConfig;




































































public class MemcachedHttpCacheStorage
  implements HttpCacheStorage
{
  private static final Log log = LogFactory.getLog(MemcachedHttpCacheStorage.class);
  

  private final MemcachedClientIF client;
  

  private final KeyHashingScheme keyHashingScheme;
  
  private final MemcachedCacheEntryFactory memcachedCacheEntryFactory;
  
  private final int maxUpdateRetries;
  

  public MemcachedHttpCacheStorage(InetSocketAddress address)
    throws IOException
  {
    this(new MemcachedClient(new InetSocketAddress[] { address }));
  }
  




  public MemcachedHttpCacheStorage(MemcachedClientIF cache)
  {
    this(cache, new CacheConfig(), new MemcachedCacheEntryFactoryImpl(), new SHA256KeyHashingScheme());
  }
  


















  @Deprecated
  public MemcachedHttpCacheStorage(MemcachedClientIF client, CacheConfig config, HttpCacheEntrySerializer serializer)
  {
    this(client, config, new MemcachedCacheEntryFactoryImpl(), new SHA256KeyHashingScheme());
  }
  













  public MemcachedHttpCacheStorage(MemcachedClientIF client, CacheConfig config, MemcachedCacheEntryFactory memcachedCacheEntryFactory, KeyHashingScheme keyHashingScheme)
  {
    this.client = client;
    this.maxUpdateRetries = config.getMaxUpdateRetries();
    this.memcachedCacheEntryFactory = memcachedCacheEntryFactory;
    this.keyHashingScheme = keyHashingScheme;
  }
  
  public void putEntry(String url, HttpCacheEntry entry) throws IOException {
    byte[] bytes = serializeEntry(url, entry);
    String key = getCacheKey(url);
    if (key == null) return;
    try {
      this.client.set(key, 0, bytes);
    } catch (OperationTimeoutException ex) {
      throw new MemcachedOperationTimeoutException(ex);
    }
  }
  
  private String getCacheKey(String url) {
    try {
      return this.keyHashingScheme.hash(url);
    } catch (MemcachedKeyHashingException mkhe) {}
    return null;
  }
  
  private byte[] serializeEntry(String url, HttpCacheEntry hce) throws IOException
  {
    MemcachedCacheEntry mce = this.memcachedCacheEntryFactory.getMemcachedCacheEntry(url, hce);
    try {
      return mce.toByteArray();
    } catch (MemcachedSerializationException mse) {
      IOException ioe = new IOException();
      ioe.initCause(mse);
      throw ioe;
    }
  }
  
  private byte[] convertToByteArray(Object o) {
    if (o == null) return null;
    if (!(o instanceof byte[])) {
      log.warn("got a non-bytearray back from memcached: " + o);
      return null;
    }
    return (byte[])o;
  }
  
  private MemcachedCacheEntry reconstituteEntry(Object o) throws IOException {
    byte[] bytes = convertToByteArray(o);
    if (bytes == null) return null;
    MemcachedCacheEntry mce = this.memcachedCacheEntryFactory.getUnsetCacheEntry();
    try {
      mce.set(bytes);
    } catch (MemcachedSerializationException mse) {
      return null;
    }
    return mce;
  }
  
  public HttpCacheEntry getEntry(String url) throws IOException {
    String key = getCacheKey(url);
    if (key == null) return null;
    try {
      MemcachedCacheEntry mce = reconstituteEntry(this.client.get(key));
      if ((mce == null) || (!url.equals(mce.getStorageKey()))) return null;
      return mce.getHttpCacheEntry();
    } catch (OperationTimeoutException ex) {
      throw new MemcachedOperationTimeoutException(ex);
    }
  }
  
  public void removeEntry(String url) throws IOException {
    String key = getCacheKey(url);
    if (key == null) return;
    try {
      this.client.delete(key);
    } catch (OperationTimeoutException ex) {
      throw new MemcachedOperationTimeoutException(ex);
    }
  }
  
  public void updateEntry(String url, HttpCacheUpdateCallback callback) throws HttpCacheUpdateException, IOException
  {
    int numRetries = 0;
    String key = getCacheKey(url);
    if (key == null) {
      throw new HttpCacheUpdateException("couldn't generate cache key");
    }
    do {
      try {
        CASValue<Object> v = this.client.gets(key);
        MemcachedCacheEntry mce = v == null ? null : reconstituteEntry(v.getValue());
        
        if ((mce != null) && (!url.equals(mce.getStorageKey()))) {
          mce = null;
        }
        HttpCacheEntry existingEntry = mce == null ? null : mce.getHttpCacheEntry();
        
        HttpCacheEntry updatedEntry = callback.update(existingEntry);
        
        if (existingEntry == null) {
          putEntry(url, updatedEntry);
          return;
        }
        
        byte[] updatedBytes = serializeEntry(url, updatedEntry);
        CASResponse casResult = this.client.cas(key, v.getCas(), updatedBytes);
        
        if (casResult != CASResponse.OK)
          numRetries++; else {
          return;
        }
      } catch (OperationTimeoutException ex) {
        throw new MemcachedOperationTimeoutException(ex);
      }
    } while (numRetries <= this.maxUpdateRetries);
    
    throw new HttpCacheUpdateException("Failed to update");
  }
}
