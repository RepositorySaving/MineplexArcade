package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.cache.HttpCacheEntry;

abstract interface HttpCache
{
  public abstract void flushCacheEntriesFor(HttpHost paramHttpHost, HttpRequest paramHttpRequest)
    throws IOException;
  
  public abstract void flushInvalidatedCacheEntriesFor(HttpHost paramHttpHost, HttpRequest paramHttpRequest)
    throws IOException;
  
  public abstract void flushInvalidatedCacheEntriesFor(HttpHost paramHttpHost, HttpRequest paramHttpRequest, HttpResponse paramHttpResponse);
  
  public abstract HttpCacheEntry getCacheEntry(HttpHost paramHttpHost, HttpRequest paramHttpRequest)
    throws IOException;
  
  public abstract Map<String, Variant> getVariantCacheEntriesWithEtags(HttpHost paramHttpHost, HttpRequest paramHttpRequest)
    throws IOException;
  
  public abstract HttpResponse cacheAndReturnResponse(HttpHost paramHttpHost, HttpRequest paramHttpRequest, HttpResponse paramHttpResponse, Date paramDate1, Date paramDate2)
    throws IOException;
  
  public abstract HttpCacheEntry updateCacheEntry(HttpHost paramHttpHost, HttpRequest paramHttpRequest, HttpCacheEntry paramHttpCacheEntry, HttpResponse paramHttpResponse, Date paramDate1, Date paramDate2)
    throws IOException;
  
  public abstract HttpCacheEntry updateVariantCacheEntry(HttpHost paramHttpHost, HttpRequest paramHttpRequest, HttpCacheEntry paramHttpCacheEntry, HttpResponse paramHttpResponse, Date paramDate1, Date paramDate2, String paramString)
    throws IOException;
  
  public abstract void reuseVariantEntryFor(HttpHost paramHttpHost, HttpRequest paramHttpRequest, Variant paramVariant)
    throws IOException;
}
