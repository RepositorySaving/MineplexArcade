package org.apache.http.impl.client.cache.memcached;

import org.apache.http.client.cache.HttpCacheEntry;




























public class MemcachedCacheEntryFactoryImpl
  implements MemcachedCacheEntryFactory
{
  public MemcachedCacheEntry getMemcachedCacheEntry(String key, HttpCacheEntry entry)
  {
    return new MemcachedCacheEntryImpl(key, entry);
  }
  
  public MemcachedCacheEntry getUnsetCacheEntry() {
    return new MemcachedCacheEntryImpl(null, null);
  }
}
