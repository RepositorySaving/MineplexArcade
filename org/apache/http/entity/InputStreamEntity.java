package org.apache.http.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.annotation.NotThreadSafe;


































@NotThreadSafe
public class InputStreamEntity
  extends AbstractHttpEntity
{
  private static final int BUFFER_SIZE = 2048;
  private final InputStream content;
  private final long length;
  
  public InputStreamEntity(InputStream instream, long length)
  {
    this(instream, length, null);
  }
  



  public InputStreamEntity(InputStream instream, long length, ContentType contentType)
  {
    if (instream == null) {
      throw new IllegalArgumentException("Source input stream may not be null");
    }
    this.content = instream;
    this.length = length;
    if (contentType != null) {
      setContentType(contentType.toString());
    }
  }
  
  public boolean isRepeatable() {
    return false;
  }
  
  public long getContentLength() {
    return this.length;
  }
  
  public InputStream getContent() throws IOException {
    return this.content;
  }
  
  public void writeTo(OutputStream outstream) throws IOException {
    if (outstream == null) {
      throw new IllegalArgumentException("Output stream may not be null");
    }
    InputStream instream = this.content;
    try {
      byte[] buffer = new byte[2048];
      
      if (this.length < 0L) {
        int l;
        while ((l = instream.read(buffer)) != -1) {
          outstream.write(buffer, 0, l);
        }
      }
      
      long remaining = this.length;
      while (remaining > 0L) {
        int l = instream.read(buffer, 0, (int)Math.min(2048L, remaining));
        if (l == -1) {
          break;
        }
        outstream.write(buffer, 0, l);
        remaining -= l;
      }
    }
    finally {
      instream.close();
    }
  }
  
  public boolean isStreaming() {
    return true;
  }
  





  @Deprecated
  public void consumeContent()
    throws IOException
  {
    this.content.close();
  }
}
