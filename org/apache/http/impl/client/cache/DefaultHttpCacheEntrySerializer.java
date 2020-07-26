package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheEntrySerializationException;
import org.apache.http.client.cache.HttpCacheEntrySerializer;

































@Immutable
public class DefaultHttpCacheEntrySerializer
  implements HttpCacheEntrySerializer
{
  public void writeTo(HttpCacheEntry cacheEntry, OutputStream os)
    throws IOException
  {
    ObjectOutputStream oos = new ObjectOutputStream(os);
    try {
      oos.writeObject(cacheEntry);
    } finally {
      oos.close();
    }
  }
  
  public HttpCacheEntry readFrom(InputStream is) throws IOException {
    ObjectInputStream ois = new ObjectInputStream(is);
    try {
      return (HttpCacheEntry)ois.readObject();
    } catch (ClassNotFoundException ex) {
      throw new HttpCacheEntrySerializationException("Class not found: " + ex.getMessage(), ex);
    } finally {
      ois.close();
    }
  }
}
