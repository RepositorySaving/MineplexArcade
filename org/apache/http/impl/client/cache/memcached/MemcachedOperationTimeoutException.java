package org.apache.http.impl.client.cache.memcached;

import java.io.IOException;




























class MemcachedOperationTimeoutException
  extends IOException
{
  private static final long serialVersionUID = 1608334789051537010L;
  
  public MemcachedOperationTimeoutException(Throwable cause)
  {
    super(cause.getMessage());
    initCause(cause);
  }
}
