package org.apache.http.client.cache;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public abstract interface Resource
  extends Serializable
{
  public abstract InputStream getInputStream()
    throws IOException;
  
  public abstract long length();
  
  public abstract void dispose();
}
