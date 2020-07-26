package org.apache.http.impl.client.cache.memcached;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.http.client.cache.HttpCacheEntry;






























public class MemcachedCacheEntryImpl
  implements MemcachedCacheEntry
{
  private String key;
  private HttpCacheEntry httpCacheEntry;
  
  public MemcachedCacheEntryImpl(String key, HttpCacheEntry httpCacheEntry)
  {
    this.key = key;
    this.httpCacheEntry = httpCacheEntry;
  }
  


  public MemcachedCacheEntryImpl() {}
  

  public synchronized byte[] toByteArray()
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    try
    {
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(this.key);
      oos.writeObject(this.httpCacheEntry);
      oos.close();
    } catch (IOException ioe) {
      throw new MemcachedSerializationException(ioe);
    }
    return bos.toByteArray();
  }
  


  public synchronized String getStorageKey()
  {
    return this.key;
  }
  


  public synchronized HttpCacheEntry getHttpCacheEntry()
  {
    return this.httpCacheEntry;
  }
  


  public synchronized void set(byte[] bytes)
  {
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    String s;
    HttpCacheEntry entry;
    try
    {
      ObjectInputStream ois = new ObjectInputStream(bis);
      s = (String)ois.readObject();
      entry = (HttpCacheEntry)ois.readObject();
      ois.close();
      bis.close();
    } catch (IOException ioe) {
      throw new MemcachedSerializationException(ioe);
    } catch (ClassNotFoundException cnfe) {
      throw new MemcachedSerializationException(cnfe);
    }
    this.key = s;
    this.httpCacheEntry = entry;
  }
}
