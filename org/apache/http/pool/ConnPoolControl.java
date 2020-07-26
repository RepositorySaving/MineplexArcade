package org.apache.http.pool;

public abstract interface ConnPoolControl<T>
{
  public abstract void setMaxTotal(int paramInt);
  
  public abstract int getMaxTotal();
  
  public abstract void setDefaultMaxPerRoute(int paramInt);
  
  public abstract int getDefaultMaxPerRoute();
  
  public abstract void setMaxPerRoute(T paramT, int paramInt);
  
  public abstract int getMaxPerRoute(T paramT);
  
  public abstract PoolStats getTotalStats();
  
  public abstract PoolStats getStats(T paramT);
}
