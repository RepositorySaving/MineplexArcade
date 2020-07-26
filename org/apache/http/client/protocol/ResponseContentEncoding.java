package org.apache.http.client.protocol;

import java.io.IOException;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.protocol.HttpContext;


















































@Immutable
public class ResponseContentEncoding
  implements HttpResponseInterceptor
{
  public static final String UNCOMPRESSED = "http.client.response.uncompressed";
  
  public void process(HttpResponse response, HttpContext context)
    throws HttpException, IOException
  {
    HttpEntity entity = response.getEntity();
    

    if (entity != null) {
      Header ceheader = entity.getContentEncoding();
      if (ceheader != null) {
        HeaderElement[] codecs = ceheader.getElements();
        HeaderElement[] arr$ = codecs;int len$ = arr$.length;int i$ = 0; if (i$ < len$) { HeaderElement codec = arr$[i$];
          String codecname = codec.getName().toLowerCase(Locale.US);
          if (("gzip".equals(codecname)) || ("x-gzip".equals(codecname))) {
            response.setEntity(new GzipDecompressingEntity(response.getEntity()));
            if (context != null) context.setAttribute("http.client.response.uncompressed", Boolean.valueOf(true));
            return; }
          if ("deflate".equals(codecname)) {
            response.setEntity(new DeflateDecompressingEntity(response.getEntity()));
            if (context != null) context.setAttribute("http.client.response.uncompressed", Boolean.valueOf(true));
            return; }
          if ("identity".equals(codecname))
          {

            return;
          }
          throw new HttpException("Unsupported Content-Coding: " + codec.getName());
        }
      }
    }
  }
}
