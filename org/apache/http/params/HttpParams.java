package org.apache.http.params;

public abstract interface HttpParams
{
  public abstract Object getParameter(String paramString);
  
  public abstract HttpParams setParameter(String paramString, Object paramObject);
  
  @Deprecated
  public abstract HttpParams copy();
  
  public abstract boolean removeParameter(String paramString);
  
  public abstract long getLongParameter(String paramString, long paramLong);
  
  public abstract HttpParams setLongParameter(String paramString, long paramLong);
  
  public abstract int getIntParameter(String paramString, int paramInt);
  
  public abstract HttpParams setIntParameter(String paramString, int paramInt);
  
  public abstract double getDoubleParameter(String paramString, double paramDouble);
  
  public abstract HttpParams setDoubleParameter(String paramString, double paramDouble);
  
  public abstract boolean getBooleanParameter(String paramString, boolean paramBoolean);
  
  public abstract HttpParams setBooleanParameter(String paramString, boolean paramBoolean);
  
  public abstract boolean isParameterTrue(String paramString);
  
  public abstract boolean isParameterFalse(String paramString);
}
