package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.Resource;











































@ThreadSafe
public class ManagedHttpCacheStorage
  implements HttpCacheStorage
{
  private final CacheMap entries;
  private final ReferenceQueue<HttpCacheEntry> morque;
  private final Set<ResourceReference> resources;
  private volatile boolean shutdown;
  
  public ManagedHttpCacheStorage(CacheConfig config)
  {
    this.entries = new CacheMap(config.getMaxCacheEntries());
    this.morque = new ReferenceQueue();
    this.resources = new HashSet();
  }
  
  private void ensureValidState() throws IllegalStateException {
    if (this.shutdown) {
      throw new IllegalStateException("Cache has been shut down");
    }
  }
  
  private void keepResourceReference(HttpCacheEntry entry) {
    Resource resource = entry.getResource();
    if (resource != null)
    {
      ResourceReference ref = new ResourceReference(entry, this.morque);
      this.resources.add(ref);
    }
  }
  
  public void putEntry(String url, HttpCacheEntry entry) throws IOException {
    if (url == null) {
      throw new IllegalArgumentException("URL may not be null");
    }
    if (entry == null) {
      throw new IllegalArgumentException("Cache entry may not be null");
    }
    ensureValidState();
    synchronized (this) {
      this.entries.put(url, entry);
      keepResourceReference(entry);
    }
  }
  
  public HttpCacheEntry getEntry(String url) throws IOException {
    if (url == null) {
      throw new IllegalArgumentException("URL may not be null");
    }
    ensureValidState();
    synchronized (this) {
      return (HttpCacheEntry)this.entries.get(url);
    }
  }
  
  public void removeEntry(String url) throws IOException {
    if (url == null) {
      throw new IllegalArgumentException("URL may not be null");
    }
    ensureValidState();
    synchronized (this)
    {

      this.entries.remove(url);
    }
  }
  
  public void updateEntry(String url, HttpCacheUpdateCallback callback)
    throws IOException
  {
    if (url == null) {
      throw new IllegalArgumentException("URL may not be null");
    }
    if (callback == null) {
      throw new IllegalArgumentException("Callback may not be null");
    }
    ensureValidState();
    synchronized (this) {
      HttpCacheEntry existing = (HttpCacheEntry)this.entries.get(url);
      HttpCacheEntry updated = callback.update(existing);
      this.entries.put(url, updated);
      if (existing != updated) {
        keepResourceReference(updated);
      }
    }
  }
  
  public void cleanResources() {
    if (this.shutdown) {
      return;
    }
    ResourceReference ref;
    while ((ref = (ResourceReference)this.morque.poll()) != null) {
      synchronized (this) {
        this.resources.remove(ref);
      }
      ref.getResource().dispose();
    }
  }
  
  public void shutdown() {
    if (this.shutdown) {
      return;
    }
    this.shutdown = true;
    synchronized (this) {
      this.entries.clear();
      for (ResourceReference ref : this.resources) {
        ref.getResource().dispose();
      }
      this.resources.clear();
      while (this.morque.poll() != null) {}
    }
  }
}
