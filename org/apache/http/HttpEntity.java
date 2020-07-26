package org.apache.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract interface HttpEntity
{
  public abstract boolean isRepeatable();
  
  public abstract boolean isChunked();
  
  public abstract long getContentLength();
  
  public abstract Header getContentType();
  
  public abstract Header getContentEncoding();
  
  public abstract InputStream getContent()
    throws IOException, IllegalStateException;
  
  public abstract void writeTo(OutputStream paramOutputStream)
    throws IOException;
  
  public abstract boolean isStreaming();
  
  @Deprecated
  public abstract void consumeContent()
    throws IOException;
}
