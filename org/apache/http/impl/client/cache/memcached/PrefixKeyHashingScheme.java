package org.apache.http.impl.client.cache.memcached;















public class PrefixKeyHashingScheme
  implements KeyHashingScheme
{
  private String prefix;
  












  private KeyHashingScheme backingScheme;
  













  public PrefixKeyHashingScheme(String prefix, KeyHashingScheme backingScheme)
  {
    this.prefix = prefix;
    this.backingScheme = backingScheme;
  }
  
  public String hash(String storageKey) {
    return this.prefix + this.backingScheme.hash(storageKey);
  }
}
