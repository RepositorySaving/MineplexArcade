package org.apache.http.impl.client.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.cache.Resource;


































@ThreadSafe
public class FileResource
  implements Resource
{
  private static final long serialVersionUID = 4132244415919043397L;
  private final File file;
  private volatile boolean disposed;
  
  public FileResource(File file)
  {
    this.file = file;
    this.disposed = false;
  }
  
  synchronized File getFile() {
    return this.file;
  }
  
  public synchronized InputStream getInputStream() throws IOException {
    return new FileInputStream(this.file);
  }
  
  public synchronized long length() {
    return this.file.length();
  }
  
  public synchronized void dispose() {
    if (this.disposed) {
      return;
    }
    this.disposed = true;
    this.file.delete();
  }
}
