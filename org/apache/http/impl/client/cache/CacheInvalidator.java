package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheStorage;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;


































@ThreadSafe
class CacheInvalidator
{
  private final HttpCacheStorage storage;
  private final CacheKeyGenerator cacheKeyGenerator;
  private final Log log = LogFactory.getLog(getClass());
  








  public CacheInvalidator(CacheKeyGenerator uriExtractor, HttpCacheStorage storage)
  {
    this.cacheKeyGenerator = uriExtractor;
    this.storage = storage;
  }
  






  public void flushInvalidatedCacheEntries(HttpHost host, HttpRequest req)
  {
    if (requestShouldNotBeCached(req)) {
      this.log.debug("Request should not be cached");
      
      String theUri = this.cacheKeyGenerator.getURI(host, req);
      
      HttpCacheEntry parent = getEntry(theUri);
      
      this.log.debug("parent entry: " + parent);
      
      if (parent != null) {
        for (String variantURI : parent.getVariantMap().values()) {
          flushEntry(variantURI);
        }
        flushEntry(theUri);
      }
      URL reqURL = getAbsoluteURL(theUri);
      if (reqURL == null) {
        this.log.error("Couldn't transform request into valid URL");
        return;
      }
      Header clHdr = req.getFirstHeader("Content-Location");
      if (clHdr != null) {
        String contentLocation = clHdr.getValue();
        if (!flushAbsoluteUriFromSameHost(reqURL, contentLocation)) {
          flushRelativeUriFromSameHost(reqURL, contentLocation);
        }
      }
      Header lHdr = req.getFirstHeader("Location");
      if (lHdr != null) {
        flushAbsoluteUriFromSameHost(reqURL, lHdr.getValue());
      }
    }
  }
  
  private void flushEntry(String uri) {
    try {
      this.storage.removeEntry(uri);
    } catch (IOException ioe) {
      this.log.warn("unable to flush cache entry", ioe);
    }
  }
  
  private HttpCacheEntry getEntry(String theUri) {
    try {
      return this.storage.getEntry(theUri);
    } catch (IOException ioe) {
      this.log.warn("could not retrieve entry from storage", ioe);
    }
    return null;
  }
  
  protected void flushUriIfSameHost(URL requestURL, URL targetURL) {
    URL canonicalTarget = getAbsoluteURL(this.cacheKeyGenerator.canonicalizeUri(targetURL.toString()));
    if (canonicalTarget == null) return;
    if (canonicalTarget.getAuthority().equalsIgnoreCase(requestURL.getAuthority())) {
      flushEntry(canonicalTarget.toString());
    }
  }
  
  protected void flushRelativeUriFromSameHost(URL reqURL, String relUri) {
    URL relURL = getRelativeURL(reqURL, relUri);
    if (relURL == null) return;
    flushUriIfSameHost(reqURL, relURL);
  }
  
  protected boolean flushAbsoluteUriFromSameHost(URL reqURL, String uri)
  {
    URL absURL = getAbsoluteURL(uri);
    if (absURL == null) return false;
    flushUriIfSameHost(reqURL, absURL);
    return true;
  }
  
  private URL getAbsoluteURL(String uri) {
    URL absURL = null;
    try {
      absURL = new URL(uri);
    }
    catch (MalformedURLException mue) {}
    
    return absURL;
  }
  
  private URL getRelativeURL(URL reqURL, String relUri) {
    URL relURL = null;
    try {
      relURL = new URL(reqURL, relUri);
    }
    catch (MalformedURLException e) {}
    
    return relURL;
  }
  
  protected boolean requestShouldNotBeCached(HttpRequest req) {
    String method = req.getRequestLine().getMethod();
    return notGetOrHeadRequest(method);
  }
  
  private boolean notGetOrHeadRequest(String method) {
    return (!"GET".equals(method)) && (!"HEAD".equals(method));
  }
  




  public void flushInvalidatedCacheEntries(HttpHost host, HttpRequest request, HttpResponse response)
  {
    int status = response.getStatusLine().getStatusCode();
    if ((status < 200) || (status > 299)) return;
    URL reqURL = getAbsoluteURL(this.cacheKeyGenerator.getURI(host, request));
    if (reqURL == null) return;
    URL canonURL = getContentLocationURL(reqURL, response);
    if (canonURL == null) return;
    String cacheKey = this.cacheKeyGenerator.canonicalizeUri(canonURL.toString());
    HttpCacheEntry entry = getEntry(cacheKey);
    if (entry == null) { return;
    }
    if (!responseDateNewerThanEntryDate(response, entry)) return;
    if (!responseAndEntryEtagsDiffer(response, entry)) { return;
    }
    flushUriIfSameHost(reqURL, canonURL);
  }
  
  private URL getContentLocationURL(URL reqURL, HttpResponse response) {
    Header clHeader = response.getFirstHeader("Content-Location");
    if (clHeader == null) return null;
    String contentLocation = clHeader.getValue();
    URL canonURL = getAbsoluteURL(contentLocation);
    if (canonURL != null) return canonURL;
    return getRelativeURL(reqURL, contentLocation);
  }
  
  private boolean responseAndEntryEtagsDiffer(HttpResponse response, HttpCacheEntry entry)
  {
    Header entryEtag = entry.getFirstHeader("ETag");
    Header responseEtag = response.getFirstHeader("ETag");
    if ((entryEtag == null) || (responseEtag == null)) return false;
    return !entryEtag.getValue().equals(responseEtag.getValue());
  }
  
  private boolean responseDateNewerThanEntryDate(HttpResponse response, HttpCacheEntry entry)
  {
    Header entryDateHeader = entry.getFirstHeader("Date");
    Header responseDateHeader = response.getFirstHeader("Date");
    if ((entryDateHeader == null) || (responseDateHeader == null)) {
      return false;
    }
    try {
      Date entryDate = DateUtils.parseDate(entryDateHeader.getValue());
      Date responseDate = DateUtils.parseDate(responseDateHeader.getValue());
      return responseDate.after(entryDate);
    } catch (DateParseException e) {}
    return false;
  }
}
