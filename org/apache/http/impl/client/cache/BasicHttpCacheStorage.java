package org.apache.http.impl.client.cache;

import java.io.IOException;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;






































@ThreadSafe
public class BasicHttpCacheStorage
  implements HttpCacheStorage
{
  private final CacheMap entries;
  
  public BasicHttpCacheStorage(CacheConfig config)
  {
    this.entries = new CacheMap(config.getMaxCacheEntries());
  }
  






  public synchronized void putEntry(String url, HttpCacheEntry entry)
    throws IOException
  {
    this.entries.put(url, entry);
  }
  





  public synchronized HttpCacheEntry getEntry(String url)
    throws IOException
  {
    return (HttpCacheEntry)this.entries.get(url);
  }
  




  public synchronized void removeEntry(String url)
    throws IOException
  {
    this.entries.remove(url);
  }
  
  public synchronized void updateEntry(String url, HttpCacheUpdateCallback callback)
    throws IOException
  {
    HttpCacheEntry existingEntry = (HttpCacheEntry)this.entries.get(url);
    this.entries.put(url, callback.update(existingEntry));
  }
}
