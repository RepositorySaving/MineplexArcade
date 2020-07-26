package org.apache.http.impl.entity;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Immutable;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.params.HttpParams;




















































@Immutable
public class LaxContentLengthStrategy
  implements ContentLengthStrategy
{
  private final int implicitLen;
  
  public LaxContentLengthStrategy(int implicitLen)
  {
    this.implicitLen = implicitLen;
  }
  



  public LaxContentLengthStrategy()
  {
    this(-1);
  }
  
  public long determineLength(HttpMessage message) throws HttpException {
    if (message == null) {
      throw new IllegalArgumentException("HTTP message may not be null");
    }
    
    HttpParams params = message.getParams();
    boolean strict = params.isParameterTrue("http.protocol.strict-transfer-encoding");
    
    Header transferEncodingHeader = message.getFirstHeader("Transfer-Encoding");
    

    if (transferEncodingHeader != null) {
      HeaderElement[] encodings = null;
      try {
        encodings = transferEncodingHeader.getElements();
      } catch (ParseException px) {
        throw new ProtocolException("Invalid Transfer-Encoding header value: " + transferEncodingHeader, px);
      }
      

      if (strict)
      {
        for (int i = 0; i < encodings.length; i++) {
          String encoding = encodings[i].getName();
          if ((encoding != null) && (encoding.length() > 0) && (!encoding.equalsIgnoreCase("chunked")) && (!encoding.equalsIgnoreCase("identity")))
          {

            throw new ProtocolException("Unsupported transfer encoding: " + encoding);
          }
        }
      }
      
      int len = encodings.length;
      if ("identity".equalsIgnoreCase(transferEncodingHeader.getValue()))
        return -1L;
      if ((len > 0) && ("chunked".equalsIgnoreCase(encodings[(len - 1)].getName())))
      {
        return -2L;
      }
      if (strict) {
        throw new ProtocolException("Chunk-encoding must be the last one applied");
      }
      return -1L;
    }
    
    Header contentLengthHeader = message.getFirstHeader("Content-Length");
    if (contentLengthHeader != null) {
      long contentlen = -1L;
      Header[] headers = message.getHeaders("Content-Length");
      if ((strict) && (headers.length > 1)) {
        throw new ProtocolException("Multiple content length headers");
      }
      for (int i = headers.length - 1; i >= 0; i--) {
        Header header = headers[i];
        try {
          contentlen = Long.parseLong(header.getValue());
        }
        catch (NumberFormatException e) {
          if (strict) {
            throw new ProtocolException("Invalid content length: " + header.getValue());
          }
        }
      }
      
      if (contentlen >= 0L) {
        return contentlen;
      }
      return -1L;
    }
    
    return this.implicitLen;
  }
}
