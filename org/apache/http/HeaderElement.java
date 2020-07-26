package org.apache.http;

public abstract interface HeaderElement
{
  public abstract String getName();
  
  public abstract String getValue();
  
  public abstract NameValuePair[] getParameters();
  
  public abstract NameValuePair getParameterByName(String paramString);
  
  public abstract int getParameterCount();
  
  public abstract NameValuePair getParameter(int paramInt);
}
