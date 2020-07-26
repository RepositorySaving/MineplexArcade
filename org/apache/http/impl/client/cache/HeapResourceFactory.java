package org.apache.http.impl.client.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.cache.InputLimit;
import org.apache.http.client.cache.Resource;
import org.apache.http.client.cache.ResourceFactory;

































@Immutable
public class HeapResourceFactory
  implements ResourceFactory
{
  public Resource generate(String requestId, InputStream instream, InputLimit limit)
    throws IOException
  {
    ByteArrayOutputStream outstream = new ByteArrayOutputStream();
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
    
    return new HeapResource(outstream.toByteArray());
  }
  
  public Resource copy(String requestId, Resource resource) throws IOException
  {
    byte[] body;
    byte[] body;
    if ((resource instanceof HeapResource)) {
      body = ((HeapResource)resource).getByteArray();
    } else {
      ByteArrayOutputStream outstream = new ByteArrayOutputStream();
      IOUtils.copyAndClose(resource.getInputStream(), outstream);
      body = outstream.toByteArray();
    }
    return new HeapResource(body);
  }
}
