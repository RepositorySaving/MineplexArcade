package org.apache.http.client.cache;

import java.io.IOException;

public abstract interface HttpCacheUpdateCallback
{
  public abstract HttpCacheEntry update(HttpCacheEntry paramHttpCacheEntry)
    throws IOException;
}
