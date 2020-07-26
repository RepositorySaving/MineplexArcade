package org.apache.http.impl.cookie;

import java.util.Locale;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieAttributeHandler;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;









































@Immutable
public class RFC2965DomainAttributeHandler
  implements CookieAttributeHandler
{
  public void parse(SetCookie cookie, String domain)
    throws MalformedCookieException
  {
    if (cookie == null) {
      throw new IllegalArgumentException("Cookie may not be null");
    }
    if (domain == null) {
      throw new MalformedCookieException("Missing value for domain attribute");
    }
    
    if (domain.trim().length() == 0) {
      throw new MalformedCookieException("Blank value for domain attribute");
    }
    
    domain = domain.toLowerCase(Locale.ENGLISH);
    if (!domain.startsWith("."))
    {




      domain = '.' + domain;
    }
    cookie.setDomain(domain);
  }
  














  public boolean domainMatch(String host, String domain)
  {
    boolean match = (host.equals(domain)) || ((domain.startsWith(".")) && (host.endsWith(domain)));
    

    return match;
  }
  


  public void validate(Cookie cookie, CookieOrigin origin)
    throws MalformedCookieException
  {
    if (cookie == null) {
      throw new IllegalArgumentException("Cookie may not be null");
    }
    if (origin == null) {
      throw new IllegalArgumentException("Cookie origin may not be null");
    }
    String host = origin.getHost().toLowerCase(Locale.ENGLISH);
    if (cookie.getDomain() == null) {
      throw new CookieRestrictionViolationException("Invalid cookie state: domain not specified");
    }
    
    String cookieDomain = cookie.getDomain().toLowerCase(Locale.ENGLISH);
    
    if (((cookie instanceof ClientCookie)) && (((ClientCookie)cookie).containsAttribute("domain")))
    {

      if (!cookieDomain.startsWith(".")) {
        throw new CookieRestrictionViolationException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2109: domain must start with a dot");
      }
      



      int dotIndex = cookieDomain.indexOf('.', 1);
      if (((dotIndex < 0) || (dotIndex == cookieDomain.length() - 1)) && (!cookieDomain.equals(".local")))
      {
        throw new CookieRestrictionViolationException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: the value contains no embedded dots " + "and the value is not .local");
      }
      




      if (!domainMatch(host, cookieDomain)) {
        throw new CookieRestrictionViolationException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: effective host name does not " + "domain-match domain attribute.");
      }
      




      String effectiveHostWithoutDomain = host.substring(0, host.length() - cookieDomain.length());
      
      if (effectiveHostWithoutDomain.indexOf('.') != -1) {
        throw new CookieRestrictionViolationException("Domain attribute \"" + cookie.getDomain() + "\" violates RFC 2965: " + "effective host minus domain may not contain any dots");

      }
      


    }
    else if (!cookie.getDomain().equals(host)) {
      throw new CookieRestrictionViolationException("Illegal domain attribute: \"" + cookie.getDomain() + "\"." + "Domain of origin: \"" + host + "\"");
    }
  }
  






  public boolean match(Cookie cookie, CookieOrigin origin)
  {
    if (cookie == null) {
      throw new IllegalArgumentException("Cookie may not be null");
    }
    if (origin == null) {
      throw new IllegalArgumentException("Cookie origin may not be null");
    }
    String host = origin.getHost().toLowerCase(Locale.ENGLISH);
    String cookieDomain = cookie.getDomain();
    


    if (!domainMatch(host, cookieDomain)) {
      return false;
    }
    
    String effectiveHostWithoutDomain = host.substring(0, host.length() - cookieDomain.length());
    
    return effectiveHostWithoutDomain.indexOf('.') == -1;
  }
}
