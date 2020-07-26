package org.apache.http.client.cache;

import java.io.IOException;
import java.io.InputStream;

public abstract interface ResourceFactory
{
  public abstract Resource generate(String paramString, InputStream paramInputStream, InputLimit paramInputLimit)
    throws IOException;
  
  public abstract Resource copy(String paramString, Resource paramResource)
    throws IOException;
}
