package org.apache.http.impl.client.cache;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.Resource;


























@Immutable
class ResourceReference
  extends PhantomReference<HttpCacheEntry>
{
  private final Resource resource;
  
  public ResourceReference(HttpCacheEntry entry, ReferenceQueue<HttpCacheEntry> q)
  {
    super(entry, q);
    if (entry.getResource() == null) {
      throw new IllegalArgumentException("Resource may not be null");
    }
    this.resource = entry.getResource();
  }
  
  public Resource getResource() {
    return this.resource;
  }
  
  public int hashCode()
  {
    return this.resource.hashCode();
  }
  
  public boolean equals(Object obj)
  {
    return this.resource.equals(obj);
  }
}
