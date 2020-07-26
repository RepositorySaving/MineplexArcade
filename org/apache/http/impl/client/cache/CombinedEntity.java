package org.apache.http.impl.client.cache;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.cache.Resource;
import org.apache.http.entity.AbstractHttpEntity;


























@NotThreadSafe
class CombinedEntity
  extends AbstractHttpEntity
{
  private final Resource resource;
  private final InputStream combinedStream;
  
  CombinedEntity(Resource resource, InputStream instream)
    throws IOException
  {
    this.resource = resource;
    this.combinedStream = new SequenceInputStream(new ResourceStream(resource.getInputStream()), instream);
  }
  
  public long getContentLength()
  {
    return -1L;
  }
  
  public boolean isRepeatable() {
    return false;
  }
  
  public boolean isStreaming() {
    return true;
  }
  
  public InputStream getContent() throws IOException, IllegalStateException {
    return this.combinedStream;
  }
  
  public void writeTo(OutputStream outstream) throws IOException {
    if (outstream == null) {
      throw new IllegalArgumentException("Output stream may not be null");
    }
    InputStream instream = getContent();
    try
    {
      byte[] tmp = new byte[2048];
      int l; while ((l = instream.read(tmp)) != -1) {
        outstream.write(tmp, 0, l);
      }
    } finally {
      instream.close();
    }
  }
  
  private void dispose() {
    this.resource.dispose();
  }
  
  class ResourceStream extends FilterInputStream
  {
    protected ResourceStream(InputStream in) {
      super();
    }
    
    public void close() throws IOException
    {
      try {
        super.close();
      } finally {
        CombinedEntity.this.dispose();
      }
    }
  }
}
