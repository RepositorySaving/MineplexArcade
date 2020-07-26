package org.apache.http.impl.cookie;

import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
































@Immutable
public class BasicExpiresHandler
  extends AbstractCookieAttributeHandler
{
  private final String[] datepatterns;
  
  public BasicExpiresHandler(String[] datepatterns)
  {
    if (datepatterns == null) {
      throw new IllegalArgumentException("Array of date patterns may not be null");
    }
    this.datepatterns = datepatterns;
  }
  
  public void parse(SetCookie cookie, String value) throws MalformedCookieException
  {
    if (cookie == null) {
      throw new IllegalArgumentException("Cookie may not be null");
    }
    if (value == null) {
      throw new MalformedCookieException("Missing value for expires attribute");
    }
    try {
      cookie.setExpiryDate(DateUtils.parseDate(value, this.datepatterns));
    } catch (DateParseException dpe) {
      throw new MalformedCookieException("Unable to parse expires attribute: " + value);
    }
  }
}
