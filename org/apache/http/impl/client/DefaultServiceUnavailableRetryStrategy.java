package org.apache.http.impl.client;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;











































@Immutable
public class DefaultServiceUnavailableRetryStrategy
  implements ServiceUnavailableRetryStrategy
{
  private final int maxRetries;
  private final long retryInterval;
  
  public DefaultServiceUnavailableRetryStrategy(int maxRetries, int retryInterval)
  {
    if (maxRetries < 1) {
      throw new IllegalArgumentException("MaxRetries must be greater than 1");
    }
    if (retryInterval < 1) {
      throw new IllegalArgumentException("Retry interval must be greater than 1");
    }
    this.maxRetries = maxRetries;
    this.retryInterval = retryInterval;
  }
  
  public DefaultServiceUnavailableRetryStrategy() {
    this(1, 1000);
  }
  
  public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
    return (executionCount <= this.maxRetries) && (response.getStatusLine().getStatusCode() == 503);
  }
  
  public long getRetryInterval()
  {
    return this.retryInterval;
  }
}
