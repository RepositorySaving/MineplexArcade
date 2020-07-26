package org.apache.http.impl.client.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.cache.InputLimit;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.cache.ResourceFactory;
































@Immutable
public class FileResourceFactory
  implements ResourceFactory
{
  private final File cacheDir;
  private final BasicIdGenerator idgen;
  
  public FileResourceFactory(File cacheDir)
  {
    this.cacheDir = cacheDir;
    this.idgen = new BasicIdGenerator();
  }
  
  private File generateUniqueCacheFile(String requestId) {
    StringBuilder buffer = new StringBuilder();
    this.idgen.generate(buffer);
    buffer.append('.');
    int len = Math.min(requestId.length(), 100);
    for (int i = 0; i < len; i++) {
      char ch = requestId.charAt(i);
      if ((Character.isLetterOrDigit(ch)) || (ch == '.')) {
        buffer.append(ch);
      } else {
        buffer.append('-');
      }
    }
    return new File(this.cacheDir, buffer.toString());
  }
  

  public Resource generate(String requestId, InputStream instream, InputLimit limit)
    throws IOException
  {
    File file = generateUniqueCacheFile(requestId);
    FileOutputStream outstream = new FileOutputStream(file);
    try {
      byte[] buf = new byte[2048];
      long total = 0L;
      int l;
      while ((l = instream.read(buf)) != -1) {
        outstream.write(buf, 0, l);
        total += l;
        if ((limit != null) && (total > limit.getValue())) {
          limit.reached();
        }
      }
    }
    finally {
      outstream.close();
    }
    return new FileResource(file);
  }
  
  public Resource copy(String requestId, Resource resource)
    throws IOException
  {
    File file = generateUniqueCacheFile(requestId);
    
    if ((resource instanceof FileResource)) {
      File src = ((FileResource)resource).getFile();
      IOUtils.copyFile(src, file);
    } else {
      FileOutputStream out = new FileOutputStream(file);
      IOUtils.copyAndClose(resource.getInputStream(), out);
    }
    return new FileResource(file);
  }
}
