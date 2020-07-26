package org.apache.http.impl.client.cache;












public class CacheConfig
{
  public static final int DEFAULT_MAX_OBJECT_SIZE_BYTES = 8192;
  










  public static final int DEFAULT_MAX_CACHE_ENTRIES = 1000;
  










  public static final int DEFAULT_MAX_UPDATE_RETRIES = 1;
  










  public static final boolean DEFAULT_HEURISTIC_CACHING_ENABLED = false;
  










  public static final float DEFAULT_HEURISTIC_COEFFICIENT = 0.1F;
  










  public static final long DEFAULT_HEURISTIC_LIFETIME = 0L;
  










  public static final int DEFAULT_ASYNCHRONOUS_WORKERS_MAX = 1;
  










  public static final int DEFAULT_ASYNCHRONOUS_WORKERS_CORE = 1;
  










  public static final int DEFAULT_ASYNCHRONOUS_WORKER_IDLE_LIFETIME_SECS = 60;
  









  public static final int DEFAULT_REVALIDATION_QUEUE_SIZE = 100;
  









  private long maxObjectSize = 8192L;
  private int maxCacheEntries = 1000;
  private int maxUpdateRetries = 1;
  private boolean heuristicCachingEnabled = false;
  private float heuristicCoefficient = 0.1F;
  private long heuristicDefaultLifetime = 0L;
  private boolean isSharedCache = true;
  private int asynchronousWorkersMax = 1;
  private int asynchronousWorkersCore = 1;
  private int asynchronousWorkerIdleLifetimeSecs = 60;
  private int revalidationQueueSize = 100;
  





  @Deprecated
  public int getMaxObjectSizeBytes()
  {
    return this.maxObjectSize > 2147483647L ? 2147483647 : (int)this.maxObjectSize;
  }
  





  @Deprecated
  public void setMaxObjectSizeBytes(int maxObjectSizeBytes)
  {
    if (maxObjectSizeBytes > 2147483647) {
      this.maxObjectSize = 2147483647L;
    } else {
      this.maxObjectSize = maxObjectSizeBytes;
    }
  }
  





  public long getMaxObjectSize()
  {
    return this.maxObjectSize;
  }
  





  public void setMaxObjectSize(long maxObjectSize)
  {
    this.maxObjectSize = maxObjectSize;
  }
  




  public boolean isSharedCache()
  {
    return this.isSharedCache;
  }
  





  public void setSharedCache(boolean isSharedCache)
  {
    this.isSharedCache = isSharedCache;
  }
  


  public int getMaxCacheEntries()
  {
    return this.maxCacheEntries;
  }
  


  public void setMaxCacheEntries(int maxCacheEntries)
  {
    this.maxCacheEntries = maxCacheEntries;
  }
  


  public int getMaxUpdateRetries()
  {
    return this.maxUpdateRetries;
  }
  


  public void setMaxUpdateRetries(int maxUpdateRetries)
  {
    this.maxUpdateRetries = maxUpdateRetries;
  }
  



  public boolean isHeuristicCachingEnabled()
  {
    return this.heuristicCachingEnabled;
  }
  




  public void setHeuristicCachingEnabled(boolean heuristicCachingEnabled)
  {
    this.heuristicCachingEnabled = heuristicCachingEnabled;
  }
  


  public float getHeuristicCoefficient()
  {
    return this.heuristicCoefficient;
  }
  







  public void setHeuristicCoefficient(float heuristicCoefficient)
  {
    this.heuristicCoefficient = heuristicCoefficient;
  }
  



  public long getHeuristicDefaultLifetime()
  {
    return this.heuristicDefaultLifetime;
  }
  










  public void setHeuristicDefaultLifetime(long heuristicDefaultLifetimeSecs)
  {
    this.heuristicDefaultLifetime = heuristicDefaultLifetimeSecs;
  }
  




  public int getAsynchronousWorkersMax()
  {
    return this.asynchronousWorkersMax;
  }
  





  public void setAsynchronousWorkersMax(int max)
  {
    this.asynchronousWorkersMax = max;
  }
  



  public int getAsynchronousWorkersCore()
  {
    return this.asynchronousWorkersCore;
  }
  





  public void setAsynchronousWorkersCore(int min)
  {
    this.asynchronousWorkersCore = min;
  }
  





  public int getAsynchronousWorkerIdleLifetimeSecs()
  {
    return this.asynchronousWorkerIdleLifetimeSecs;
  }
  






  public void setAsynchronousWorkerIdleLifetimeSecs(int secs)
  {
    this.asynchronousWorkerIdleLifetimeSecs = secs;
  }
  


  public int getRevalidationQueueSize()
  {
    return this.revalidationQueueSize;
  }
  


  public void setRevalidationQueueSize(int size)
  {
    this.revalidationQueueSize = size;
  }
}
