package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.FormattedHeader;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.message.BufferedHeader;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;







































@NotThreadSafe
public class BrowserCompatSpec
  extends CookieSpecBase
{
  private static final String[] DEFAULT_DATE_PATTERNS = { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z" };
  








  private final String[] datepatterns;
  








  public BrowserCompatSpec(String[] datepatterns)
  {
    if (datepatterns != null) {
      this.datepatterns = ((String[])datepatterns.clone());
    } else {
      this.datepatterns = DEFAULT_DATE_PATTERNS;
    }
    registerAttribHandler("path", new BasicPathHandler());
    registerAttribHandler("domain", new BasicDomainHandler());
    registerAttribHandler("max-age", new BasicMaxAgeHandler());
    registerAttribHandler("secure", new BasicSecureHandler());
    registerAttribHandler("comment", new BasicCommentHandler());
    registerAttribHandler("expires", new BasicExpiresHandler(this.datepatterns));
  }
  

  public BrowserCompatSpec()
  {
    this(null);
  }
  
  public List<Cookie> parse(Header header, CookieOrigin origin) throws MalformedCookieException
  {
    if (header == null) {
      throw new IllegalArgumentException("Header may not be null");
    }
    if (origin == null) {
      throw new IllegalArgumentException("Cookie origin may not be null");
    }
    String headername = header.getName();
    if (!headername.equalsIgnoreCase("Set-Cookie")) {
      throw new MalformedCookieException("Unrecognized cookie header '" + header.toString() + "'");
    }
    
    HeaderElement[] helems = header.getElements();
    boolean versioned = false;
    boolean netscape = false;
    for (HeaderElement helem : helems) {
      if (helem.getParameterByName("version") != null) {
        versioned = true;
      }
      if (helem.getParameterByName("expires") != null) {
        netscape = true;
      }
    }
    if ((netscape) || (!versioned))
    {

      NetscapeDraftHeaderParser parser = NetscapeDraftHeaderParser.DEFAULT;
      ParserCursor cursor;
      CharArrayBuffer buffer;
      ParserCursor cursor; if ((header instanceof FormattedHeader)) {
        CharArrayBuffer buffer = ((FormattedHeader)header).getBuffer();
        cursor = new ParserCursor(((FormattedHeader)header).getValuePos(), buffer.length());
      }
      else
      {
        String s = header.getValue();
        if (s == null) {
          throw new MalformedCookieException("Header value is null");
        }
        buffer = new CharArrayBuffer(s.length());
        buffer.append(s);
        cursor = new ParserCursor(0, buffer.length());
      }
      helems = new HeaderElement[] { parser.parseHeader(buffer, cursor) };
    }
    return parse(helems, origin);
  }
  
  public List<Header> formatCookies(List<Cookie> cookies) {
    if (cookies == null) {
      throw new IllegalArgumentException("List of cookies may not be null");
    }
    if (cookies.isEmpty()) {
      throw new IllegalArgumentException("List of cookies may not be empty");
    }
    CharArrayBuffer buffer = new CharArrayBuffer(20 * cookies.size());
    buffer.append("Cookie");
    buffer.append(": ");
    for (int i = 0; i < cookies.size(); i++) {
      Cookie cookie = (Cookie)cookies.get(i);
      if (i > 0) {
        buffer.append("; ");
      }
      buffer.append(cookie.getName());
      buffer.append("=");
      String s = cookie.getValue();
      if (s != null) {
        buffer.append(s);
      }
    }
    List<Header> headers = new ArrayList(1);
    headers.add(new BufferedHeader(buffer));
    return headers;
  }
  
  public int getVersion() {
    return 0;
  }
  
  public Header getVersionHeader() {
    return null;
  }
  
  public String toString()
  {
    return "compatibility";
  }
}
