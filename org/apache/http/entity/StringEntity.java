package org.apache.http.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import org.apache.http.annotation.NotThreadSafe;













































@NotThreadSafe
public class StringEntity
  extends AbstractHttpEntity
  implements Cloneable
{
  protected final byte[] content;
  
  public StringEntity(String string, ContentType contentType)
  {
    if (string == null) {
      throw new IllegalArgumentException("Source string may not be null");
    }
    Charset charset = contentType != null ? contentType.getCharset() : null;
    try {
      this.content = string.getBytes(charset.name());
    }
    catch (UnsupportedEncodingException ex) {
      throw new UnsupportedCharsetException(charset.name());
    }
    if (contentType != null) {
      setContentType(contentType.toString());
    }
  }
  














  @Deprecated
  public StringEntity(String string, String mimeType, String charset)
    throws UnsupportedEncodingException
  {
    if (string == null) {
      throw new IllegalArgumentException("Source string may not be null");
    }
    if (mimeType == null) {
      mimeType = "text/plain";
    }
    if (charset == null) {
      charset = "ISO-8859-1";
    }
    this.content = string.getBytes(charset);
    setContentType(mimeType + "; charset=" + charset);
  }
  










  public StringEntity(String string, String charset)
    throws UnsupportedEncodingException
  {
    this(string, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
  }
  











  public StringEntity(String string, Charset charset)
  {
    this(string, ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), charset));
  }
  








  public StringEntity(String string)
    throws UnsupportedEncodingException
  {
    this(string, ContentType.DEFAULT_TEXT);
  }
  
  public boolean isRepeatable() {
    return true;
  }
  
  public long getContentLength() {
    return this.content.length;
  }
  
  public InputStream getContent() throws IOException {
    return new ByteArrayInputStream(this.content);
  }
  
  public void writeTo(OutputStream outstream) throws IOException {
    if (outstream == null) {
      throw new IllegalArgumentException("Output stream may not be null");
    }
    outstream.write(this.content);
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
