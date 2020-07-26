package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;



















































@Immutable
public class BasicLineParser
  implements LineParser
{
  public static final BasicLineParser DEFAULT = new BasicLineParser();
  






  protected final ProtocolVersion protocol;
  






  public BasicLineParser(ProtocolVersion proto)
  {
    if (proto == null) {
      proto = HttpVersion.HTTP_1_1;
    }
    this.protocol = proto;
  }
  



  public BasicLineParser()
  {
    this(null);
  }
  



  public static final ProtocolVersion parseProtocolVersion(String value, LineParser parser)
    throws ParseException
  {
    if (value == null) {
      throw new IllegalArgumentException("Value to parse may not be null.");
    }
    

    if (parser == null) {
      parser = DEFAULT;
    }
    CharArrayBuffer buffer = new CharArrayBuffer(value.length());
    buffer.append(value);
    ParserCursor cursor = new ParserCursor(0, value.length());
    return parser.parseProtocolVersion(buffer, cursor);
  }
  



  public ProtocolVersion parseProtocolVersion(CharArrayBuffer buffer, ParserCursor cursor)
    throws ParseException
  {
    if (buffer == null) {
      throw new IllegalArgumentException("Char array buffer may not be null");
    }
    if (cursor == null) {
      throw new IllegalArgumentException("Parser cursor may not be null");
    }
    
    String protoname = this.protocol.getProtocol();
    int protolength = protoname.length();
    
    int indexFrom = cursor.getPos();
    int indexTo = cursor.getUpperBound();
    
    skipWhitespace(buffer, cursor);
    
    int i = cursor.getPos();
    

    if (i + protolength + 4 > indexTo) {
      throw new ParseException("Not a valid protocol version: " + buffer.substring(indexFrom, indexTo));
    }
    



    boolean ok = true;
    for (int j = 0; (ok) && (j < protolength); j++) {
      ok = buffer.charAt(i + j) == protoname.charAt(j);
    }
    if (ok) {
      ok = buffer.charAt(i + protolength) == '/';
    }
    if (!ok) {
      throw new ParseException("Not a valid protocol version: " + buffer.substring(indexFrom, indexTo));
    }
    


    i += protolength + 1;
    
    int period = buffer.indexOf(46, i, indexTo);
    if (period == -1) {
      throw new ParseException("Invalid protocol version number: " + buffer.substring(indexFrom, indexTo));
    }
    
    int major;
    try
    {
      major = Integer.parseInt(buffer.substringTrimmed(i, period));
    } catch (NumberFormatException e) {
      throw new ParseException("Invalid protocol major version number: " + buffer.substring(indexFrom, indexTo));
    }
    

    i = period + 1;
    
    int blank = buffer.indexOf(32, i, indexTo);
    if (blank == -1) {
      blank = indexTo;
    }
    int minor;
    try {
      minor = Integer.parseInt(buffer.substringTrimmed(i, blank));
    } catch (NumberFormatException e) {
      throw new ParseException("Invalid protocol minor version number: " + buffer.substring(indexFrom, indexTo));
    }
    


    cursor.updatePos(blank);
    
    return createProtocolVersion(major, minor);
  }
  










  protected ProtocolVersion createProtocolVersion(int major, int minor)
  {
    return this.protocol.forVersion(major, minor);
  }
  




  public boolean hasProtocolVersion(CharArrayBuffer buffer, ParserCursor cursor)
  {
    if (buffer == null) {
      throw new IllegalArgumentException("Char array buffer may not be null");
    }
    if (cursor == null) {
      throw new IllegalArgumentException("Parser cursor may not be null");
    }
    int index = cursor.getPos();
    
    String protoname = this.protocol.getProtocol();
    int protolength = protoname.length();
    
    if (buffer.length() < protolength + 4) {
      return false;
    }
    if (index < 0)
    {

      index = buffer.length() - 4 - protolength;
    } else if (index == 0)
    {
      while ((index < buffer.length()) && (HTTP.isWhitespace(buffer.charAt(index))))
      {
        index++;
      }
    }
    

    if (index + protolength + 4 > buffer.length()) {
      return false;
    }
    

    boolean ok = true;
    for (int j = 0; (ok) && (j < protolength); j++) {
      ok = buffer.charAt(index + j) == protoname.charAt(j);
    }
    if (ok) {
      ok = buffer.charAt(index + protolength) == '/';
    }
    
    return ok;
  }
  




  public static final RequestLine parseRequestLine(String value, LineParser parser)
    throws ParseException
  {
    if (value == null) {
      throw new IllegalArgumentException("Value to parse may not be null.");
    }
    

    if (parser == null) {
      parser = DEFAULT;
    }
    CharArrayBuffer buffer = new CharArrayBuffer(value.length());
    buffer.append(value);
    ParserCursor cursor = new ParserCursor(0, value.length());
    return parser.parseRequestLine(buffer, cursor);
  }
  











  public RequestLine parseRequestLine(CharArrayBuffer buffer, ParserCursor cursor)
    throws ParseException
  {
    if (buffer == null) {
      throw new IllegalArgumentException("Char array buffer may not be null");
    }
    if (cursor == null) {
      throw new IllegalArgumentException("Parser cursor may not be null");
    }
    
    int indexFrom = cursor.getPos();
    int indexTo = cursor.getUpperBound();
    try
    {
      skipWhitespace(buffer, cursor);
      int i = cursor.getPos();
      
      int blank = buffer.indexOf(32, i, indexTo);
      if (blank < 0) {
        throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
      }
      
      String method = buffer.substringTrimmed(i, blank);
      cursor.updatePos(blank);
      
      skipWhitespace(buffer, cursor);
      i = cursor.getPos();
      
      blank = buffer.indexOf(32, i, indexTo);
      if (blank < 0) {
        throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
      }
      
      String uri = buffer.substringTrimmed(i, blank);
      cursor.updatePos(blank);
      
      ProtocolVersion ver = parseProtocolVersion(buffer, cursor);
      
      skipWhitespace(buffer, cursor);
      if (!cursor.atEnd()) {
        throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
      }
      

      return createRequestLine(method, uri, ver);
    } catch (IndexOutOfBoundsException e) {
      throw new ParseException("Invalid request line: " + buffer.substring(indexFrom, indexTo));
    }
  }
  













  protected RequestLine createRequestLine(String method, String uri, ProtocolVersion ver)
  {
    return new BasicRequestLine(method, uri, ver);
  }
  




  public static final StatusLine parseStatusLine(String value, LineParser parser)
    throws ParseException
  {
    if (value == null) {
      throw new IllegalArgumentException("Value to parse may not be null.");
    }
    

    if (parser == null) {
      parser = DEFAULT;
    }
    CharArrayBuffer buffer = new CharArrayBuffer(value.length());
    buffer.append(value);
    ParserCursor cursor = new ParserCursor(0, value.length());
    return parser.parseStatusLine(buffer, cursor);
  }
  



  public StatusLine parseStatusLine(CharArrayBuffer buffer, ParserCursor cursor)
    throws ParseException
  {
    if (buffer == null) {
      throw new IllegalArgumentException("Char array buffer may not be null");
    }
    if (cursor == null) {
      throw new IllegalArgumentException("Parser cursor may not be null");
    }
    
    int indexFrom = cursor.getPos();
    int indexTo = cursor.getUpperBound();
    
    try
    {
      ProtocolVersion ver = parseProtocolVersion(buffer, cursor);
      

      skipWhitespace(buffer, cursor);
      int i = cursor.getPos();
      
      int blank = buffer.indexOf(32, i, indexTo);
      if (blank < 0) {
        blank = indexTo;
      }
      int statusCode = 0;
      String s = buffer.substringTrimmed(i, blank);
      for (int j = 0; j < s.length(); j++) {
        if (!Character.isDigit(s.charAt(j))) {
          throw new ParseException("Status line contains invalid status code: " + buffer.substring(indexFrom, indexTo));
        }
      }
      
      try
      {
        statusCode = Integer.parseInt(s);
      } catch (NumberFormatException e) {
        throw new ParseException("Status line contains invalid status code: " + buffer.substring(indexFrom, indexTo));
      }
      


      i = blank;
      String reasonPhrase = null;
      if (i < indexTo) {
        reasonPhrase = buffer.substringTrimmed(i, indexTo);
      } else {
        reasonPhrase = "";
      }
      return createStatusLine(ver, statusCode, reasonPhrase);
    }
    catch (IndexOutOfBoundsException e) {
      throw new ParseException("Invalid status line: " + buffer.substring(indexFrom, indexTo));
    }
  }
  













  protected StatusLine createStatusLine(ProtocolVersion ver, int status, String reason)
  {
    return new BasicStatusLine(ver, status, reason);
  }
  




  public static final Header parseHeader(String value, LineParser parser)
    throws ParseException
  {
    if (value == null) {
      throw new IllegalArgumentException("Value to parse may not be null");
    }
    

    if (parser == null) {
      parser = DEFAULT;
    }
    CharArrayBuffer buffer = new CharArrayBuffer(value.length());
    buffer.append(value);
    return parser.parseHeader(buffer);
  }
  



  public Header parseHeader(CharArrayBuffer buffer)
    throws ParseException
  {
    return new BufferedHeader(buffer);
  }
  



  protected void skipWhitespace(CharArrayBuffer buffer, ParserCursor cursor)
  {
    int pos = cursor.getPos();
    int indexTo = cursor.getUpperBound();
    while ((pos < indexTo) && (HTTP.isWhitespace(buffer.charAt(pos))))
    {
      pos++;
    }
    cursor.updatePos(pos);
  }
}
