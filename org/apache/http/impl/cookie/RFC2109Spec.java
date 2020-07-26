package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookiePathComparator;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.message.BufferedHeader;
import org.apache.http.util.CharArrayBuffer;








































@NotThreadSafe
public class RFC2109Spec
  extends CookieSpecBase
{
  private static final CookiePathComparator PATH_COMPARATOR = new CookiePathComparator();
  
  private static final String[] DATE_PATTERNS = { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy" };
  

  private final String[] datepatterns;
  

  private final boolean oneHeader;
  


  public RFC2109Spec(String[] datepatterns, boolean oneHeader)
  {
    if (datepatterns != null) {
      this.datepatterns = ((String[])datepatterns.clone());
    } else {
      this.datepatterns = DATE_PATTERNS;
    }
    this.oneHeader = oneHeader;
    registerAttribHandler("version", new RFC2109VersionHandler());
    registerAttribHandler("path", new BasicPathHandler());
    registerAttribHandler("domain", new RFC2109DomainHandler());
    registerAttribHandler("max-age", new BasicMaxAgeHandler());
    registerAttribHandler("secure", new BasicSecureHandler());
    registerAttribHandler("comment", new BasicCommentHandler());
    registerAttribHandler("expires", new BasicExpiresHandler(this.datepatterns));
  }
  

  public RFC2109Spec()
  {
    this(null, false);
  }
  
  public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException
  {
    if (header == null) {
      throw new IllegalArgumentException("Header may not be null");
    }
    if (origin == null) {
      throw new IllegalArgumentException("Cookie origin may not be null");
    }
    if (!header.getName().equalsIgnoreCase("Set-Cookie")) {
      throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
    }
    
    HeaderElement[] elems = header.getElements();
    return parse(elems, origin);
  }
  
  public void validate(Cookie cookie, CookieOrigin origin)
    throws MalformedCookieException
  {
    if (cookie == null) {
      throw new IllegalArgumentException("Cookie may not be null");
    }
    String name = cookie.getName();
    if (name.indexOf(' ') != -1) {
      throw new CookieRestrictionViolationException("Cookie name may not contain blanks");
    }
    if (name.startsWith("$")) {
      throw new CookieRestrictionViolationException("Cookie name may not start with $");
    }
    super.validate(cookie, origin);
  }
  
  public List<Header> formatCookies(List<Cookie> cookies) {
    if (cookies == null) {
      throw new IllegalArgumentException("List of cookies may not be null");
    }
    if (cookies.isEmpty()) {
      throw new IllegalArgumentException("List of cookies may not be empty");
    }
    if (cookies.size() > 1)
    {
      cookies = new ArrayList(cookies);
      Collections.sort(cookies, PATH_COMPARATOR);
    }
    if (this.oneHeader) {
      return doFormatOneHeader(cookies);
    }
    return doFormatManyHeaders(cookies);
  }
  
  private List<Header> doFormatOneHeader(List<Cookie> cookies)
  {
    int version = 2147483647;
    
    for (Cookie cookie : cookies) {
      if (cookie.getVersion() < version) {
        version = cookie.getVersion();
      }
    }
    CharArrayBuffer buffer = new CharArrayBuffer(40 * cookies.size());
    buffer.append("Cookie");
    buffer.append(": ");
    buffer.append("$Version=");
    buffer.append(Integer.toString(version));
    for (Cookie cooky : cookies) {
      buffer.append("; ");
      Cookie cookie = cooky;
      formatCookieAsVer(buffer, cookie, version);
    }
    List<Header> headers = new ArrayList(1);
    headers.add(new BufferedHeader(buffer));
    return headers;
  }
  
  private List<Header> doFormatManyHeaders(List<Cookie> cookies) {
    List<Header> headers = new ArrayList(cookies.size());
    for (Cookie cookie : cookies) {
      int version = cookie.getVersion();
      CharArrayBuffer buffer = new CharArrayBuffer(40);
      buffer.append("Cookie: ");
      buffer.append("$Version=");
      buffer.append(Integer.toString(version));
      buffer.append("; ");
      formatCookieAsVer(buffer, cookie, version);
      headers.add(new BufferedHeader(buffer));
    }
    return headers;
  }
  









  protected void formatParamAsVer(CharArrayBuffer buffer, String name, String value, int version)
  {
    buffer.append(name);
    buffer.append("=");
    if (value != null) {
      if (version > 0) {
        buffer.append('"');
        buffer.append(value);
        buffer.append('"');
      } else {
        buffer.append(value);
      }
    }
  }
  







  protected void formatCookieAsVer(CharArrayBuffer buffer, Cookie cookie, int version)
  {
    formatParamAsVer(buffer, cookie.getName(), cookie.getValue(), version);
    if ((cookie.getPath() != null) && 
      ((cookie instanceof ClientCookie)) && (((ClientCookie)cookie).containsAttribute("path")))
    {
      buffer.append("; ");
      formatParamAsVer(buffer, "$Path", cookie.getPath(), version);
    }
    
    if ((cookie.getDomain() != null) && 
      ((cookie instanceof ClientCookie)) && (((ClientCookie)cookie).containsAttribute("domain")))
    {
      buffer.append("; ");
      formatParamAsVer(buffer, "$Domain", cookie.getDomain(), version);
    }
  }
  
  public int getVersion()
  {
    return 1;
  }
  
  public Header getVersionHeader() {
    return null;
  }
  
  public String toString()
  {
    return "rfc2109";
  }
}
