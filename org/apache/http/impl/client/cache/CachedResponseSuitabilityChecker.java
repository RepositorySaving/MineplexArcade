package org.apache.http.impl.client.cache;

import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;



































@Immutable
class CachedResponseSuitabilityChecker
{
  private final Log log = LogFactory.getLog(getClass());
  
  private final boolean sharedCache;
  
  private final boolean useHeuristicCaching;
  private final float heuristicCoefficient;
  private final long heuristicDefaultLifetime;
  private final CacheValidityPolicy validityStrategy;
  
  CachedResponseSuitabilityChecker(CacheValidityPolicy validityStrategy, CacheConfig config)
  {
    this.validityStrategy = validityStrategy;
    this.sharedCache = config.isSharedCache();
    this.useHeuristicCaching = config.isHeuristicCachingEnabled();
    this.heuristicCoefficient = config.getHeuristicCoefficient();
    this.heuristicDefaultLifetime = config.getHeuristicDefaultLifetime();
  }
  
  CachedResponseSuitabilityChecker(CacheConfig config) {
    this(new CacheValidityPolicy(), config);
  }
  
  private boolean isFreshEnough(HttpCacheEntry entry, HttpRequest request, Date now) {
    if (this.validityStrategy.isResponseFresh(entry, now)) return true;
    if ((this.useHeuristicCaching) && (this.validityStrategy.isResponseHeuristicallyFresh(entry, now, this.heuristicCoefficient, this.heuristicDefaultLifetime)))
    {
      return true; }
    if (originInsistsOnFreshness(entry)) return false;
    long maxstale = getMaxStale(request);
    if (maxstale == -1L) return false;
    return maxstale > this.validityStrategy.getStalenessSecs(entry, now);
  }
  
  private boolean originInsistsOnFreshness(HttpCacheEntry entry) {
    if (this.validityStrategy.mustRevalidate(entry)) return true;
    if (!this.sharedCache) return false;
    return (this.validityStrategy.proxyRevalidate(entry)) || (this.validityStrategy.hasCacheControlDirective(entry, "s-maxage"));
  }
  
  private long getMaxStale(HttpRequest request)
  {
    long maxstale = -1L;
    for (Header h : request.getHeaders("Cache-Control")) {
      for (HeaderElement elt : h.getElements()) {
        if ("max-stale".equals(elt.getName())) {
          if (((elt.getValue() == null) || ("".equals(elt.getValue().trim()))) && (maxstale == -1L))
          {
            maxstale = 9223372036854775807L;
          } else {
            try {
              long val = Long.parseLong(elt.getValue());
              if (val < 0L) val = 0L;
              if ((maxstale == -1L) || (val < maxstale)) {
                maxstale = val;
              }
            }
            catch (NumberFormatException nfe) {
              maxstale = 0L;
            }
          }
        }
      }
    }
    return maxstale;
  }
  














  public boolean canCachedResponseBeUsed(HttpHost host, HttpRequest request, HttpCacheEntry entry, Date now)
  {
    if (!isFreshEnough(entry, request, now)) {
      this.log.trace("Cache entry was not fresh enough");
      return false;
    }
    
    if (!this.validityStrategy.contentLengthHeaderMatchesActualLength(entry)) {
      this.log.debug("Cache entry Content-Length and header information do not match");
      return false;
    }
    
    if (hasUnsupportedConditionalHeaders(request)) {
      this.log.debug("Request contained conditional headers we don't handle");
      return false;
    }
    
    if ((isConditional(request)) && (!allConditionalsMatch(request, entry, now))) {
      return false;
    }
    
    for (Header ccHdr : request.getHeaders("Cache-Control")) {
      for (HeaderElement elt : ccHdr.getElements()) {
        if ("no-cache".equals(elt.getName())) {
          this.log.trace("Response contained NO CACHE directive, cache was not suitable");
          return false;
        }
        
        if ("no-store".equals(elt.getName())) {
          this.log.trace("Response contained NO STORE directive, cache was not suitable");
          return false;
        }
        
        if ("max-age".equals(elt.getName())) {
          try {
            int maxage = Integer.parseInt(elt.getValue());
            if (this.validityStrategy.getCurrentAgeSecs(entry, now) > maxage) {
              this.log.trace("Response from cache was NOT suitable due to max age");
              return false;
            }
          }
          catch (NumberFormatException ex) {
            this.log.debug("Response from cache was malformed" + ex.getMessage());
            return false;
          }
        }
        
        if ("max-stale".equals(elt.getName())) {
          try {
            int maxstale = Integer.parseInt(elt.getValue());
            if (this.validityStrategy.getFreshnessLifetimeSecs(entry) > maxstale) {
              this.log.trace("Response from cache was not suitable due to Max stale freshness");
              return false;
            }
          }
          catch (NumberFormatException ex) {
            this.log.debug("Response from cache was malformed: " + ex.getMessage());
            return false;
          }
        }
        
        if ("min-fresh".equals(elt.getName())) {
          try {
            long minfresh = Long.parseLong(elt.getValue());
            if (minfresh < 0L) return false;
            long age = this.validityStrategy.getCurrentAgeSecs(entry, now);
            long freshness = this.validityStrategy.getFreshnessLifetimeSecs(entry);
            if (freshness - age < minfresh) {
              this.log.trace("Response from cache was not suitable due to min fresh freshness requirement");
              
              return false;
            }
          }
          catch (NumberFormatException ex) {
            this.log.debug("Response from cache was malformed: " + ex.getMessage());
            return false;
          }
        }
      }
    }
    
    this.log.trace("Response from cache was suitable");
    return true;
  }
  




  public boolean isConditional(HttpRequest request)
  {
    return (hasSupportedEtagValidator(request)) || (hasSupportedLastModifiedValidator(request));
  }
  






  public boolean allConditionalsMatch(HttpRequest request, HttpCacheEntry entry, Date now)
  {
    boolean hasEtagValidator = hasSupportedEtagValidator(request);
    boolean hasLastModifiedValidator = hasSupportedLastModifiedValidator(request);
    
    boolean etagValidatorMatches = (hasEtagValidator) && (etagValidatorMatches(request, entry));
    boolean lastModifiedValidatorMatches = (hasLastModifiedValidator) && (lastModifiedValidatorMatches(request, entry, now));
    
    if ((hasEtagValidator) && (hasLastModifiedValidator) && ((!etagValidatorMatches) || (!lastModifiedValidatorMatches)))
    {
      return false; }
    if ((hasEtagValidator) && (!etagValidatorMatches)) {
      return false;
    }
    
    if ((hasLastModifiedValidator) && (!lastModifiedValidatorMatches)) {
      return false;
    }
    return true;
  }
  
  private boolean hasUnsupportedConditionalHeaders(HttpRequest request) {
    return (request.getFirstHeader("If-Range") != null) || (request.getFirstHeader("If-Match") != null) || (hasValidDateField(request, "If-Unmodified-Since"));
  }
  

  private boolean hasSupportedEtagValidator(HttpRequest request)
  {
    return request.containsHeader("If-None-Match");
  }
  
  private boolean hasSupportedLastModifiedValidator(HttpRequest request) {
    return hasValidDateField(request, "If-Modified-Since");
  }
  





  private boolean etagValidatorMatches(HttpRequest request, HttpCacheEntry entry)
  {
    Header etagHeader = entry.getFirstHeader("ETag");
    String etag = etagHeader != null ? etagHeader.getValue() : null;
    Header[] ifNoneMatch = request.getHeaders("If-None-Match");
    if (ifNoneMatch != null) {
      for (Header h : ifNoneMatch) {
        for (HeaderElement elt : h.getElements()) {
          String reqEtag = elt.toString();
          if ((("*".equals(reqEtag)) && (etag != null)) || (reqEtag.equals(etag)))
          {
            return true;
          }
        }
      }
    }
    return false;
  }
  







  private boolean lastModifiedValidatorMatches(HttpRequest request, HttpCacheEntry entry, Date now)
  {
    Header lastModifiedHeader = entry.getFirstHeader("Last-Modified");
    Date lastModified = null;
    try {
      if (lastModifiedHeader != null) {
        lastModified = DateUtils.parseDate(lastModifiedHeader.getValue());
      }
    }
    catch (DateParseException dpe) {}
    

    if (lastModified == null) {
      return false;
    }
    
    for (Header h : request.getHeaders("If-Modified-Since")) {
      try {
        Date ifModifiedSince = DateUtils.parseDate(h.getValue());
        if ((ifModifiedSince.after(now)) || (lastModified.after(ifModifiedSince))) {
          return false;
        }
      }
      catch (DateParseException dpe) {}
    }
    
    return true;
  }
  
  private boolean hasValidDateField(HttpRequest request, String headerName) {
    for (Header h : request.getHeaders(headerName)) {
      try {
        DateUtils.parseDate(h.getValue());
        return true;
      }
      catch (DateParseException dpe) {}
    }
    
    return false;
  }
}
