package org.apache.http.client.cache;
















public class HttpCacheUpdateException
  extends Exception
{
  private static final long serialVersionUID = 823573584868632876L;
  















  public HttpCacheUpdateException(String message)
  {
    super(message);
  }
  
  public HttpCacheUpdateException(String message, Throwable cause) {
    super(message);
    initCause(cause);
  }
}
