package org.apache.http.impl.client.cache;

import java.util.Date;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.Resource;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;



































@Immutable
class CacheValidityPolicy
{
  public static final long MAX_AGE = 2147483648L;
  
  public long getCurrentAgeSecs(HttpCacheEntry entry, Date now)
  {
    return getCorrectedInitialAgeSecs(entry) + getResidentTimeSecs(entry, now);
  }
  
  public long getFreshnessLifetimeSecs(HttpCacheEntry entry) {
    long maxage = getMaxAge(entry);
    if (maxage > -1L) {
      return maxage;
    }
    Date dateValue = getDateValue(entry);
    if (dateValue == null) {
      return 0L;
    }
    Date expiry = getExpirationDate(entry);
    if (expiry == null)
      return 0L;
    long diff = expiry.getTime() - dateValue.getTime();
    return diff / 1000L;
  }
  
  public boolean isResponseFresh(HttpCacheEntry entry, Date now) {
    return getCurrentAgeSecs(entry, now) < getFreshnessLifetimeSecs(entry);
  }
  













  public boolean isResponseHeuristicallyFresh(HttpCacheEntry entry, Date now, float coefficient, long defaultLifetime)
  {
    return getCurrentAgeSecs(entry, now) < getHeuristicFreshnessLifetimeSecs(entry, coefficient, defaultLifetime);
  }
  
  public long getHeuristicFreshnessLifetimeSecs(HttpCacheEntry entry, float coefficient, long defaultLifetime)
  {
    Date dateValue = getDateValue(entry);
    Date lastModifiedValue = getLastModifiedValue(entry);
    
    if ((dateValue != null) && (lastModifiedValue != null)) {
      long diff = dateValue.getTime() - lastModifiedValue.getTime();
      if (diff < 0L)
        return 0L;
      return (coefficient * (float)(diff / 1000L));
    }
    
    return defaultLifetime;
  }
  
  public boolean isRevalidatable(HttpCacheEntry entry) {
    return (entry.getFirstHeader("ETag") != null) || (entry.getFirstHeader("Last-Modified") != null);
  }
  
  public boolean mustRevalidate(HttpCacheEntry entry)
  {
    return hasCacheControlDirective(entry, "must-revalidate");
  }
  
  public boolean proxyRevalidate(HttpCacheEntry entry) {
    return hasCacheControlDirective(entry, "proxy-revalidate");
  }
  
  public boolean mayReturnStaleWhileRevalidating(HttpCacheEntry entry, Date now) {
    for (Header h : entry.getHeaders("Cache-Control")) {
      for (HeaderElement elt : h.getElements()) {
        if ("stale-while-revalidate".equalsIgnoreCase(elt.getName())) {
          try {
            int allowedStalenessLifetime = Integer.parseInt(elt.getValue());
            if (getStalenessSecs(entry, now) <= allowedStalenessLifetime) {
              return true;
            }
          }
          catch (NumberFormatException nfe) {}
        }
      }
    }
    

    return false;
  }
  
  public boolean mayReturnStaleIfError(HttpRequest request, HttpCacheEntry entry, Date now)
  {
    long stalenessSecs = getStalenessSecs(entry, now);
    return (mayReturnStaleIfError(request.getHeaders("Cache-Control"), stalenessSecs)) || (mayReturnStaleIfError(entry.getHeaders("Cache-Control"), stalenessSecs));
  }
  


  private boolean mayReturnStaleIfError(Header[] headers, long stalenessSecs)
  {
    boolean result = false;
    for (Header h : headers) {
      for (HeaderElement elt : h.getElements()) {
        if ("stale-if-error".equals(elt.getName())) {
          try {
            int staleIfErrorSecs = Integer.parseInt(elt.getValue());
            if (stalenessSecs <= staleIfErrorSecs) {
              result = true;
              break;
            }
          }
          catch (NumberFormatException nfe) {}
        }
      }
    }
    
    return result;
  }
  
  protected Date getDateValue(HttpCacheEntry entry) {
    Header dateHdr = entry.getFirstHeader("Date");
    if (dateHdr == null)
      return null;
    try {
      return DateUtils.parseDate(dateHdr.getValue());
    }
    catch (DateParseException dpe) {}
    
    return null;
  }
  
  protected Date getLastModifiedValue(HttpCacheEntry entry) {
    Header dateHdr = entry.getFirstHeader("Last-Modified");
    if (dateHdr == null)
      return null;
    try {
      return DateUtils.parseDate(dateHdr.getValue());
    }
    catch (DateParseException dpe) {}
    
    return null;
  }
  
  protected long getContentLengthValue(HttpCacheEntry entry) {
    Header cl = entry.getFirstHeader("Content-Length");
    if (cl == null) {
      return -1L;
    }
    try {
      return Long.parseLong(cl.getValue());
    } catch (NumberFormatException ex) {}
    return -1L;
  }
  
  protected boolean hasContentLengthHeader(HttpCacheEntry entry)
  {
    return null != entry.getFirstHeader("Content-Length");
  }
  






  protected boolean contentLengthHeaderMatchesActualLength(HttpCacheEntry entry)
  {
    return (!hasContentLengthHeader(entry)) || (getContentLengthValue(entry) == entry.getResource().length());
  }
  
  protected long getApparentAgeSecs(HttpCacheEntry entry) {
    Date dateValue = getDateValue(entry);
    if (dateValue == null)
      return 2147483648L;
    long diff = entry.getResponseDate().getTime() - dateValue.getTime();
    if (diff < 0L)
      return 0L;
    return diff / 1000L;
  }
  
  protected long getAgeValue(HttpCacheEntry entry) {
    long ageValue = 0L;
    for (Header hdr : entry.getHeaders("Age")) {
      long hdrAge;
      try {
        hdrAge = Long.parseLong(hdr.getValue());
        if (hdrAge < 0L) {
          hdrAge = 2147483648L;
        }
      } catch (NumberFormatException nfe) {
        hdrAge = 2147483648L;
      }
      ageValue = hdrAge > ageValue ? hdrAge : ageValue;
    }
    return ageValue;
  }
  
  protected long getCorrectedReceivedAgeSecs(HttpCacheEntry entry) {
    long apparentAge = getApparentAgeSecs(entry);
    long ageValue = getAgeValue(entry);
    return apparentAge > ageValue ? apparentAge : ageValue;
  }
  
  protected long getResponseDelaySecs(HttpCacheEntry entry) {
    long diff = entry.getResponseDate().getTime() - entry.getRequestDate().getTime();
    return diff / 1000L;
  }
  
  protected long getCorrectedInitialAgeSecs(HttpCacheEntry entry) {
    return getCorrectedReceivedAgeSecs(entry) + getResponseDelaySecs(entry);
  }
  
  protected long getResidentTimeSecs(HttpCacheEntry entry, Date now) {
    long diff = now.getTime() - entry.getResponseDate().getTime();
    return diff / 1000L;
  }
  
  protected long getMaxAge(HttpCacheEntry entry) {
    long maxage = -1L;
    for (Header hdr : entry.getHeaders("Cache-Control")) {
      for (HeaderElement elt : hdr.getElements()) {
        if (("max-age".equals(elt.getName())) || ("s-maxage".equals(elt.getName()))) {
          try
          {
            long currMaxAge = Long.parseLong(elt.getValue());
            if ((maxage == -1L) || (currMaxAge < maxage)) {
              maxage = currMaxAge;
            }
          }
          catch (NumberFormatException nfe) {
            maxage = 0L;
          }
        }
      }
    }
    return maxage;
  }
  
  protected Date getExpirationDate(HttpCacheEntry entry) {
    Header expiresHeader = entry.getFirstHeader("Expires");
    if (expiresHeader == null)
      return null;
    try {
      return DateUtils.parseDate(expiresHeader.getValue());
    }
    catch (DateParseException dpe) {}
    
    return null;
  }
  
  public boolean hasCacheControlDirective(HttpCacheEntry entry, String directive)
  {
    for (Header h : entry.getHeaders("Cache-Control")) {
      for (HeaderElement elt : h.getElements()) {
        if (directive.equalsIgnoreCase(elt.getName())) {
          return true;
        }
      }
    }
    return false;
  }
  
  public long getStalenessSecs(HttpCacheEntry entry, Date now) {
    long age = getCurrentAgeSecs(entry, now);
    long freshness = getFreshnessLifetimeSecs(entry);
    if (age <= freshness) return 0L;
    return age - freshness;
  }
}
