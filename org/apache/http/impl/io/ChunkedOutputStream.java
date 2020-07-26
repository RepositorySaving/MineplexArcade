package org.apache.http.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.SessionOutputBuffer;











































@NotThreadSafe
public class ChunkedOutputStream
  extends OutputStream
{
  private final SessionOutputBuffer out;
  private byte[] cache;
  private int cachePosition = 0;
  
  private boolean wroteLastChunk = false;
  

  private boolean closed = false;
  








  public ChunkedOutputStream(SessionOutputBuffer out, int bufferSize)
    throws IOException
  {
    this.cache = new byte[bufferSize];
    this.out = out;
  }
  






  public ChunkedOutputStream(SessionOutputBuffer out)
    throws IOException
  {
    this(out, 2048);
  }
  


  protected void flushCache()
    throws IOException
  {
    if (this.cachePosition > 0) {
      this.out.writeLine(Integer.toHexString(this.cachePosition));
      this.out.write(this.cache, 0, this.cachePosition);
      this.out.writeLine("");
      this.cachePosition = 0;
    }
  }
  


  protected void flushCacheWithAppend(byte[] bufferToAppend, int off, int len)
    throws IOException
  {
    this.out.writeLine(Integer.toHexString(this.cachePosition + len));
    this.out.write(this.cache, 0, this.cachePosition);
    this.out.write(bufferToAppend, off, len);
    this.out.writeLine("");
    this.cachePosition = 0;
  }
  
  protected void writeClosingChunk() throws IOException
  {
    this.out.writeLine("0");
    this.out.writeLine("");
  }
  




  public void finish()
    throws IOException
  {
    if (!this.wroteLastChunk) {
      flushCache();
      writeClosingChunk();
      this.wroteLastChunk = true;
    }
  }
  
  public void write(int b)
    throws IOException
  {
    if (this.closed) {
      throw new IOException("Attempted write to closed stream.");
    }
    this.cache[this.cachePosition] = ((byte)b);
    this.cachePosition += 1;
    if (this.cachePosition == this.cache.length) { flushCache();
    }
  }
  


  public void write(byte[] b)
    throws IOException
  {
    write(b, 0, b.length);
  }
  



  public void write(byte[] src, int off, int len)
    throws IOException
  {
    if (this.closed) {
      throw new IOException("Attempted write to closed stream.");
    }
    if (len >= this.cache.length - this.cachePosition) {
      flushCacheWithAppend(src, off, len);
    } else {
      System.arraycopy(src, off, this.cache, this.cachePosition, len);
      this.cachePosition += len;
    }
  }
  


  public void flush()
    throws IOException
  {
    flushCache();
    this.out.flush();
  }
  


  public void close()
    throws IOException
  {
    if (!this.closed) {
      this.closed = true;
      finish();
      this.out.flush();
    }
  }
}
