package org.apache.http.cookie;

public abstract interface CookieAttributeHandler
{
  public abstract void parse(SetCookie paramSetCookie, String paramString)
    throws MalformedCookieException;
  
  public abstract void validate(Cookie paramCookie, CookieOrigin paramCookieOrigin)
    throws MalformedCookieException;
  
  public abstract boolean match(Cookie paramCookie, CookieOrigin paramCookieOrigin);
}
