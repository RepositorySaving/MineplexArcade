package org.apache.http.impl.auth;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.http.HeaderElement;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.HeaderValueParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;











































@NotThreadSafe
public abstract class RFC2617Scheme
  extends AuthSchemeBase
{
  private final Map<String, String> params;
  
  public RFC2617Scheme(ChallengeState challengeState)
  {
    super(challengeState);
    this.params = new HashMap();
  }
  
  public RFC2617Scheme() {
    this(null);
  }
  
  protected void parseChallenge(CharArrayBuffer buffer, int pos, int len)
    throws MalformedChallengeException
  {
    HeaderValueParser parser = BasicHeaderValueParser.DEFAULT;
    ParserCursor cursor = new ParserCursor(pos, buffer.length());
    HeaderElement[] elements = parser.parseElements(buffer, cursor);
    if (elements.length == 0) {
      throw new MalformedChallengeException("Authentication challenge is empty");
    }
    this.params.clear();
    for (HeaderElement element : elements) {
      this.params.put(element.getName(), element.getValue());
    }
  }
  




  protected Map<String, String> getParameters()
  {
    return this.params;
  }
  






  public String getParameter(String name)
  {
    if (name == null) {
      return null;
    }
    return (String)this.params.get(name.toLowerCase(Locale.ENGLISH));
  }
  




  public String getRealm()
  {
    return getParameter("realm");
  }
}
