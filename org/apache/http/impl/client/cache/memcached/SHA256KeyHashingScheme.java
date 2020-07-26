package org.apache.http.impl.client.cache.memcached;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


































public class SHA256KeyHashingScheme
  implements KeyHashingScheme
{
  private static final Log log = LogFactory.getLog(SHA256KeyHashingScheme.class);
  
  public String hash(String key) {
    MessageDigest md = getDigest();
    md.update(key.getBytes());
    return Hex.encodeHexString(md.digest());
  }
  
  private MessageDigest getDigest() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException nsae) {
      log.error("can't find SHA-256 implementation for cache key hashing");
      throw new MemcachedKeyHashingException(nsae);
    }
  }
}
