package org.apache.http.client.cache;

public enum CacheResponseStatus
{
  CACHE_MODULE_RESPONSE,  CACHE_HIT,  CACHE_MISS,  VALIDATED;
  
  private CacheResponseStatus() {}
}
