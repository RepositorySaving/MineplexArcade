package org.apache.http.impl.client.cache;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.apache.http.client.cache.HttpCacheEntry;



























final class CacheMap
  extends LinkedHashMap<String, HttpCacheEntry>
{
  private static final long serialVersionUID = -7750025207539768511L;
  private final int maxEntries;
  
  CacheMap(int maxEntries)
  {
    super(20, 0.75F, true);
    this.maxEntries = maxEntries;
  }
  
  protected boolean removeEldestEntry(Map.Entry<String, HttpCacheEntry> eldest)
  {
    return size() > this.maxEntries;
  }
}
