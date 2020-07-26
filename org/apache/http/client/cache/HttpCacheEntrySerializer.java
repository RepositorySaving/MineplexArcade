package org.apache.http.client.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface HttpCacheEntrySerializer
{
  public abstract void writeTo(HttpCacheEntry paramHttpCacheEntry, OutputStream paramOutputStream)
    throws IOException;
  
  public abstract HttpCacheEntry readFrom(InputStream paramInputStream)
    throws IOException;
}
