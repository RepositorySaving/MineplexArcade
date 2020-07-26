package org.apache.http.impl.client.cache.memcached;

import org.apache.http.client.cache.HttpCacheEntry;

public abstract interface MemcachedCacheEntryFactory
{
  public abstract MemcachedCacheEntry getMemcachedCacheEntry(String paramString, HttpCacheEntry paramHttpCacheEntry);
  
  public abstract MemcachedCacheEntry getUnsetCacheEntry();
}
