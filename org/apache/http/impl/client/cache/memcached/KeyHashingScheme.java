package org.apache.http.impl.client.cache.memcached;

public abstract interface KeyHashingScheme
{
  public abstract String hash(String paramString);
}
