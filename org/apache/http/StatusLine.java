package org.apache.http;

public abstract interface StatusLine
{
  public abstract ProtocolVersion getProtocolVersion();
  
  public abstract int getStatusCode();
  
  public abstract String getReasonPhrase();
}
