package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.annotation.NotThreadSafe;





































@NotThreadSafe
public class ByteArrayEntity
  extends AbstractHttpEntity
  implements Cloneable
{
  @Deprecated
  protected final byte[] content;
  private final byte[] b;
  private final int off;
  private final int len;
  
  public ByteArrayEntity(byte[] b, ContentType contentType)
  {
    if (b == null) {
      throw new IllegalArgumentException("Source byte array may not be null");
    }
    this.content = b;
    this.b = b;
    this.off = 0;
    this.len = this.b.length;
    if (contentType != null) {
      setContentType(contentType.toString());
    }
  }
  



  public ByteArrayEntity(byte[] b, int off, int len, ContentType contentType)
  {
    if (b == null) {
      throw new IllegalArgumentException("Source byte array may not be null");
    }
    if ((off < 0) || (off > b.length) || (len < 0) || (off + len < 0) || (off + len > b.length))
    {
      throw new IndexOutOfBoundsException("off: " + off + " len: " + len + " b.length: " + b.length);
    }
    this.content = b;
    this.b = b;
    this.off = off;
    this.len = len;
    if (contentType != null) {
      setContentType(contentType.toString());
    }
  }
  
  public ByteArrayEntity(byte[] b) {
    this(b, null);
  }
  
  public ByteArrayEntity(byte[] b, int off, int len) {
    this(b, off, len, null);
  }
  
  public boolean isRepeatable() {
    return true;
  }
  
  public long getContentLength() {
    return this.len;
  }
  
  public InputStream getContent() {
    return new ByteArrayInputStream(this.b, this.off, this.len);
  }
  
  public void writeTo(OutputStream outstream) throws IOException {
    if (outstream == null) {
      throw new IllegalArgumentException("Output stream may not be null");
    }
    outstream.write(this.b, this.off, this.len);
    outstream.flush();
  }
  





  public boolean isStreaming()
  {
    return false;
  }
  
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }
}
