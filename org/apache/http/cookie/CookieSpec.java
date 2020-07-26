package org.apache.http.cookie;

import java.util.List;
import org.apache.http.Header;

public abstract interface CookieSpec
{
  public abstract int getVersion();
  
  public abstract List<Cookie> parse(Header paramHeader, CookieOrigin paramCookieOrigin)
    throws MalformedCookieException;
  
  public abstract void validate(Cookie paramCookie, CookieOrigin paramCookieOrigin)
    throws MalformedCookieException;
  
  public abstract boolean match(Cookie paramCookie, CookieOrigin paramCookieOrigin);
  
  public abstract List<Header> formatCookies(List<Cookie> paramList);
  
  public abstract Header getVersionHeader();
}
