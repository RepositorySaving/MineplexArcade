package org.apache.http.client.cache;

import java.io.IOException;

public abstract interface HttpCacheStorage
{
  public abstract void putEntry(String paramString, HttpCacheEntry paramHttpCacheEntry)
    throws IOException;
  
  public abstract HttpCacheEntry getEntry(String paramString)
    throws IOException;
  
  public abstract void removeEntry(String paramString)
    throws IOException;
  
  public abstract void updateEntry(String paramString, HttpCacheUpdateCallback paramHttpCacheUpdateCallback)
    throws IOException, HttpCacheUpdateException;
}
