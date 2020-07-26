package org.apache.http.impl.client.cache;

import java.util.Date;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;




































@Immutable
class CachedHttpResponseGenerator
{
  private final CacheValidityPolicy validityStrategy;
  
  CachedHttpResponseGenerator(CacheValidityPolicy validityStrategy)
  {
    this.validityStrategy = validityStrategy;
  }
  
  CachedHttpResponseGenerator() {
    this(new CacheValidityPolicy());
  }
  







  HttpResponse generateResponse(HttpCacheEntry entry)
  {
    Date now = new Date();
    HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, entry.getStatusCode(), entry.getReasonPhrase());
    

    HttpEntity entity = new CacheEntity(entry);
    response.setHeaders(entry.getAllHeaders());
    addMissingContentLengthHeader(response, entity);
    response.setEntity(entity);
    

    long age = this.validityStrategy.getCurrentAgeSecs(entry, now);
    if (age > 0L) {
      if (age >= 2147483647L) {
        response.setHeader("Age", "2147483648");
      } else {
        response.setHeader("Age", "" + (int)age);
      }
    }
    
    return response;
  }
  







  HttpResponse generateNotModifiedResponse(HttpCacheEntry entry)
  {
    HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, 304, "Not Modified");
    





    Header dateHeader = entry.getFirstHeader("Date");
    if (dateHeader == null) {
      dateHeader = new BasicHeader("Date", DateUtils.formatDate(new Date()));
    }
    response.addHeader(dateHeader);
    


    Header etagHeader = entry.getFirstHeader("ETag");
    if (etagHeader != null) {
      response.addHeader(etagHeader);
    }
    
    Header contentLocationHeader = entry.getFirstHeader("Content-Location");
    if (contentLocationHeader != null) {
      response.addHeader(contentLocationHeader);
    }
    



    Header expiresHeader = entry.getFirstHeader("Expires");
    if (expiresHeader != null) {
      response.addHeader(expiresHeader);
    }
    
    Header cacheControlHeader = entry.getFirstHeader("Cache-Control");
    if (cacheControlHeader != null) {
      response.addHeader(cacheControlHeader);
    }
    
    Header varyHeader = entry.getFirstHeader("Vary");
    if (varyHeader != null) {
      response.addHeader(varyHeader);
    }
    
    return response;
  }
  
  private void addMissingContentLengthHeader(HttpResponse response, HttpEntity entity) {
    if (transferEncodingIsPresent(response)) {
      return;
    }
    Header contentLength = response.getFirstHeader("Content-Length");
    if (contentLength == null) {
      contentLength = new BasicHeader("Content-Length", Long.toString(entity.getContentLength()));
      
      response.setHeader(contentLength);
    }
  }
  
  private boolean transferEncodingIsPresent(HttpResponse response) {
    Header hdr = response.getFirstHeader("Transfer-Encoding");
    return hdr != null;
  }
}
