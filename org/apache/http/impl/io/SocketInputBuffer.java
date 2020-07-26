package org.apache.http.impl.io;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.EofSensor;
import org.apache.http.params.HttpParams;






















































@NotThreadSafe
public class SocketInputBuffer
  extends AbstractSessionInputBuffer
  implements EofSensor
{
  private final Socket socket;
  private boolean eof;
  
  public SocketInputBuffer(Socket socket, int buffersize, HttpParams params)
    throws IOException
  {
    if (socket == null) {
      throw new IllegalArgumentException("Socket may not be null");
    }
    this.socket = socket;
    this.eof = false;
    if (buffersize < 0) {
      buffersize = socket.getReceiveBufferSize();
    }
    if (buffersize < 1024) {
      buffersize = 1024;
    }
    init(socket.getInputStream(), buffersize, params);
  }
  
  protected int fillBuffer() throws IOException
  {
    int i = super.fillBuffer();
    this.eof = (i == -1);
    return i;
  }
  
  public boolean isDataAvailable(int timeout) throws IOException {
    boolean result = hasBufferedData();
    if (!result) {
      int oldtimeout = this.socket.getSoTimeout();
      try {
        this.socket.setSoTimeout(timeout);
        fillBuffer();
        result = hasBufferedData();
      } catch (SocketTimeoutException ex) {
        throw ex;
      } finally {
        this.socket.setSoTimeout(oldtimeout);
      }
    }
    return result;
  }
  
  public boolean isEof() {
    return this.eof;
  }
}
