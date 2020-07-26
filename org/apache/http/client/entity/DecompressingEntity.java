package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;












































abstract class DecompressingEntity
  extends HttpEntityWrapper
{
  private static final int BUFFER_SIZE = 2048;
  private InputStream content;
  
  public DecompressingEntity(HttpEntity wrapped)
  {
    super(wrapped);
  }
  

  abstract InputStream getDecompressingInputStream(InputStream paramInputStream)
    throws IOException;
  
  public InputStream getContent()
    throws IOException
  {
    if (this.wrappedEntity.isStreaming()) {
      if (this.content == null) {
        this.content = getDecompressingInputStream(this.wrappedEntity.getContent());
      }
      return this.content;
    }
    return getDecompressingInputStream(this.wrappedEntity.getContent());
  }
  



  public void writeTo(OutputStream outstream)
    throws IOException
  {
    if (outstream == null) {
      throw new IllegalArgumentException("Output stream may not be null");
    }
    InputStream instream = getContent();
    try {
      byte[] buffer = new byte[2048];
      
      int l;
      
      while ((l = instream.read(buffer)) != -1) {
        outstream.write(buffer, 0, l);
      }
    } finally {
      instream.close();
    }
  }
}
