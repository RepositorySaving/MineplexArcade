package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;














































public class DeflateDecompressingEntity
  extends DecompressingEntity
{
  public DeflateDecompressingEntity(HttpEntity entity)
  {
    super(entity);
  }
  
































  InputStream getDecompressingInputStream(InputStream wrapped)
    throws IOException
  {
    byte[] peeked = new byte[6];
    
    PushbackInputStream pushback = new PushbackInputStream(wrapped, peeked.length);
    
    int headerLength = pushback.read(peeked);
    
    if (headerLength == -1) {
      throw new IOException("Unable to read the response");
    }
    

    byte[] dummy = new byte[1];
    
    Inflater inf = new Inflater();
    try
    {
      int n;
      while ((n = inf.inflate(dummy)) == 0) {
        if (inf.finished())
        {

          throw new IOException("Unable to read the response");
        }
        
        if (inf.needsDictionary()) {
          break;
        }
        


        if (inf.needsInput()) {
          inf.setInput(peeked);
        }
      }
      
      if (n == -1) {
        throw new IOException("Unable to read the response");
      }
      




      pushback.unread(peeked, 0, headerLength);
      return new InflaterInputStream(pushback);

    }
    catch (DataFormatException e)
    {
      pushback.unread(peeked, 0, headerLength); }
    return new InflaterInputStream(pushback, new Inflater(true));
  }
  






  public Header getContentEncoding()
  {
    return null;
  }
  





  public long getContentLength()
  {
    return -1L;
  }
}
