package org.apache.http.io;

public abstract interface BufferInfo
{
  public abstract int length();
  
  public abstract int capacity();
  
  public abstract int available();
}
