package org.apache.http.impl.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.CharArrayBuffer;
















































@NotThreadSafe
public abstract class AbstractSessionOutputBuffer
  implements SessionOutputBuffer, BufferInfo
{
  private static final Charset ASCII = Charset.forName("US-ASCII");
  private static final byte[] CRLF = { 13, 10 };
  
  private OutputStream outstream;
  
  private ByteArrayBuffer buffer;
  private Charset charset;
  private CharsetEncoder encoder;
  private ByteBuffer bbuf;
  private boolean ascii = true;
  private int minChunkLimit = 512;
  

  private HttpTransportMetricsImpl metrics;
  

  private CodingErrorAction onMalformedInputAction;
  

  private CodingErrorAction onUnMappableInputAction;
  


  protected void init(OutputStream outstream, int buffersize, HttpParams params)
  {
    if (outstream == null) {
      throw new IllegalArgumentException("Input stream may not be null");
    }
    if (buffersize <= 0) {
      throw new IllegalArgumentException("Buffer size may not be negative or zero");
    }
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    this.outstream = outstream;
    this.buffer = new ByteArrayBuffer(buffersize);
    this.charset = Charset.forName(HttpProtocolParams.getHttpElementCharset(params));
    this.ascii = this.charset.equals(ASCII);
    this.encoder = null;
    this.minChunkLimit = params.getIntParameter("http.connection.min-chunk-limit", 512);
    this.metrics = createTransportMetrics();
    this.onMalformedInputAction = HttpProtocolParams.getMalformedInputAction(params);
    this.onUnMappableInputAction = HttpProtocolParams.getUnmappableInputAction(params);
  }
  


  protected HttpTransportMetricsImpl createTransportMetrics()
  {
    return new HttpTransportMetricsImpl();
  }
  


  public int capacity()
  {
    return this.buffer.capacity();
  }
  


  public int length()
  {
    return this.buffer.length();
  }
  


  public int available()
  {
    return capacity() - length();
  }
  
  protected void flushBuffer() throws IOException {
    int len = this.buffer.length();
    if (len > 0) {
      this.outstream.write(this.buffer.buffer(), 0, len);
      this.buffer.clear();
      this.metrics.incrementBytesTransferred(len);
    }
  }
  
  public void flush() throws IOException {
    flushBuffer();
    this.outstream.flush();
  }
  
  public void write(byte[] b, int off, int len) throws IOException {
    if (b == null) {
      return;
    }
    


    if ((len > this.minChunkLimit) || (len > this.buffer.capacity()))
    {
      flushBuffer();
      
      this.outstream.write(b, off, len);
      this.metrics.incrementBytesTransferred(len);
    }
    else {
      int freecapacity = this.buffer.capacity() - this.buffer.length();
      if (len > freecapacity)
      {
        flushBuffer();
      }
      
      this.buffer.append(b, off, len);
    }
  }
  
  public void write(byte[] b) throws IOException {
    if (b == null) {
      return;
    }
    write(b, 0, b.length);
  }
  
  public void write(int b) throws IOException {
    if (this.buffer.isFull()) {
      flushBuffer();
    }
    this.buffer.append(b);
  }
  







  public void writeLine(String s)
    throws IOException
  {
    if (s == null) {
      return;
    }
    if (s.length() > 0) {
      if (this.ascii) {
        for (int i = 0; i < s.length(); i++) {
          write(s.charAt(i));
        }
      } else {
        CharBuffer cbuf = CharBuffer.wrap(s);
        writeEncoded(cbuf);
      }
    }
    write(CRLF);
  }
  







  public void writeLine(CharArrayBuffer charbuffer)
    throws IOException
  {
    if (charbuffer == null) {
      return;
    }
    if (this.ascii) {
      int off = 0;
      int remaining = charbuffer.length();
      while (remaining > 0) {
        int chunk = this.buffer.capacity() - this.buffer.length();
        chunk = Math.min(chunk, remaining);
        if (chunk > 0) {
          this.buffer.append(charbuffer, off, chunk);
        }
        if (this.buffer.isFull()) {
          flushBuffer();
        }
        off += chunk;
        remaining -= chunk;
      }
    } else {
      CharBuffer cbuf = CharBuffer.wrap(charbuffer.buffer(), 0, charbuffer.length());
      writeEncoded(cbuf);
    }
    write(CRLF);
  }
  
  private void writeEncoded(CharBuffer cbuf) throws IOException {
    if (!cbuf.hasRemaining()) {
      return;
    }
    if (this.encoder == null) {
      this.encoder = this.charset.newEncoder();
      this.encoder.onMalformedInput(this.onMalformedInputAction);
      this.encoder.onUnmappableCharacter(this.onUnMappableInputAction);
    }
    if (this.bbuf == null) {
      this.bbuf = ByteBuffer.allocate(1024);
    }
    this.encoder.reset();
    while (cbuf.hasRemaining()) {
      CoderResult result = this.encoder.encode(cbuf, this.bbuf, true);
      handleEncodingResult(result);
    }
    CoderResult result = this.encoder.flush(this.bbuf);
    handleEncodingResult(result);
    this.bbuf.clear();
  }
  
  private void handleEncodingResult(CoderResult result) throws IOException {
    if (result.isError()) {
      result.throwException();
    }
    this.bbuf.flip();
    while (this.bbuf.hasRemaining()) {
      write(this.bbuf.get());
    }
    this.bbuf.compact();
  }
  
  public HttpTransportMetrics getMetrics() {
    return this.metrics;
  }
}
