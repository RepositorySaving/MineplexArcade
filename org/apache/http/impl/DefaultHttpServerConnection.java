package org.apache.http.impl;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


















































@NotThreadSafe
public class DefaultHttpServerConnection
  extends SocketHttpServerConnection
{
  public void bind(Socket socket, HttpParams params)
    throws IOException
  {
    if (socket == null) {
      throw new IllegalArgumentException("Socket may not be null");
    }
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    assertNotOpen();
    socket.setTcpNoDelay(HttpConnectionParams.getTcpNoDelay(params));
    socket.setSoTimeout(HttpConnectionParams.getSoTimeout(params));
    socket.setKeepAlive(HttpConnectionParams.getSoKeepalive(params));
    
    int linger = HttpConnectionParams.getLinger(params);
    if (linger >= 0) {
      socket.setSoLinger(linger > 0, linger);
    }
    super.bind(socket, params);
  }
}
