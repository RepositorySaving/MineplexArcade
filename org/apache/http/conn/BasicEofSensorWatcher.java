package org.apache.http.conn;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.NotThreadSafe;











































@NotThreadSafe
public class BasicEofSensorWatcher
  implements EofSensorWatcher
{
  protected final ManagedClientConnection managedConn;
  protected final boolean attemptReuse;
  
  public BasicEofSensorWatcher(ManagedClientConnection conn, boolean reuse)
  {
    if (conn == null) {
      throw new IllegalArgumentException("Connection may not be null.");
    }
    
    this.managedConn = conn;
    this.attemptReuse = reuse;
  }
  
  public boolean eofDetected(InputStream wrapped) throws IOException
  {
    try
    {
      if (this.attemptReuse)
      {

        wrapped.close();
        this.managedConn.markReusable();
      }
    } finally {
      this.managedConn.releaseConnection();
    }
    return false;
  }
  
  public boolean streamClosed(InputStream wrapped) throws IOException
  {
    try
    {
      if (this.attemptReuse)
      {

        wrapped.close();
        this.managedConn.markReusable();
      }
    } finally {
      this.managedConn.releaseConnection();
    }
    return false;
  }
  
  public boolean streamAbort(InputStream wrapped)
    throws IOException
  {
    this.managedConn.abortConnection();
    return false;
  }
}
