package org.apache.http.impl.client.cache.memcached;

import org.apache.http.client.cache.HttpCacheEntry;

public abstract interface MemcachedCacheEntry
{
  public abstract byte[] toByteArray();
  
  public abstract String getStorageKey();
  
  public abstract HttpCacheEntry getHttpCacheEntry();
  
  public abstract void set(byte[] paramArrayOfByte);
}
