package org.apache.http.message;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.annotation.Immutable;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;










































@Immutable
public class BasicHeaderValueParser
  implements HeaderValueParser
{
  public static final BasicHeaderValueParser DEFAULT = new BasicHeaderValueParser();
  
  private static final char PARAM_DELIMITER = ';';
  private static final char ELEM_DELIMITER = ',';
  private static final char[] ALL_DELIMITERS = { ';', ',' };
  
















  public static final HeaderElement[] parseElements(String value, HeaderValueParser parser)
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
    ParserCursor cursor = new ParserCursor(0, value.length());
    return parser.parseElements(buffer, cursor);
  }
  



  public HeaderElement[] parseElements(CharArrayBuffer buffer, ParserCursor cursor)
  {
    if (buffer == null) {
      throw new IllegalArgumentException("Char array buffer may not be null");
    }
    if (cursor == null) {
      throw new IllegalArgumentException("Parser cursor may not be null");
    }
    
    List<HeaderElement> elements = new ArrayList();
    while (!cursor.atEnd()) {
      HeaderElement element = parseHeaderElement(buffer, cursor);
      if ((element.getName().length() != 0) || (element.getValue() != null)) {
        elements.add(element);
      }
    }
    return (HeaderElement[])elements.toArray(new HeaderElement[elements.size()]);
  }
  











  public static final HeaderElement parseHeaderElement(String value, HeaderValueParser parser)
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
    ParserCursor cursor = new ParserCursor(0, value.length());
    return parser.parseHeaderElement(buffer, cursor);
  }
  



  public HeaderElement parseHeaderElement(CharArrayBuffer buffer, ParserCursor cursor)
  {
    if (buffer == null) {
      throw new IllegalArgumentException("Char array buffer may not be null");
    }
    if (cursor == null) {
      throw new IllegalArgumentException("Parser cursor may not be null");
    }
    
    NameValuePair nvp = parseNameValuePair(buffer, cursor);
    NameValuePair[] params = null;
    if (!cursor.atEnd()) {
      char ch = buffer.charAt(cursor.getPos() - 1);
      if (ch != ',') {
        params = parseParameters(buffer, cursor);
      }
    }
    return createHeaderElement(nvp.getName(), nvp.getValue(), params);
  }
  









  protected HeaderElement createHeaderElement(String name, String value, NameValuePair[] params)
  {
    return new BasicHeaderElement(name, value, params);
  }
  











  public static final NameValuePair[] parseParameters(String value, HeaderValueParser parser)
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
    ParserCursor cursor = new ParserCursor(0, value.length());
    return parser.parseParameters(buffer, cursor);
  }
  




  public NameValuePair[] parseParameters(CharArrayBuffer buffer, ParserCursor cursor)
  {
    if (buffer == null) {
      throw new IllegalArgumentException("Char array buffer may not be null");
    }
    if (cursor == null) {
      throw new IllegalArgumentException("Parser cursor may not be null");
    }
    
    int pos = cursor.getPos();
    int indexTo = cursor.getUpperBound();
    
    while (pos < indexTo) {
      char ch = buffer.charAt(pos);
      if (!HTTP.isWhitespace(ch)) break;
      pos++;
    }
    


    cursor.updatePos(pos);
    if (cursor.atEnd()) {
      return new NameValuePair[0];
    }
    
    List<NameValuePair> params = new ArrayList();
    while (!cursor.atEnd()) {
      NameValuePair param = parseNameValuePair(buffer, cursor);
      params.add(param);
      char ch = buffer.charAt(cursor.getPos() - 1);
      if (ch == ',') {
        break;
      }
    }
    
    return (NameValuePair[])params.toArray(new NameValuePair[params.size()]);
  }
  










  public static final NameValuePair parseNameValuePair(String value, HeaderValueParser parser)
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
    ParserCursor cursor = new ParserCursor(0, value.length());
    return parser.parseNameValuePair(buffer, cursor);
  }
  


  public NameValuePair parseNameValuePair(CharArrayBuffer buffer, ParserCursor cursor)
  {
    return parseNameValuePair(buffer, cursor, ALL_DELIMITERS);
  }
  
  private static boolean isOneOf(char ch, char[] chs) {
    if (chs != null) {
      for (int i = 0; i < chs.length; i++) {
        if (ch == chs[i]) {
          return true;
        }
      }
    }
    return false;
  }
  


  public NameValuePair parseNameValuePair(CharArrayBuffer buffer, ParserCursor cursor, char[] delimiters)
  {
    if (buffer == null) {
      throw new IllegalArgumentException("Char array buffer may not be null");
    }
    if (cursor == null) {
      throw new IllegalArgumentException("Parser cursor may not be null");
    }
    
    boolean terminated = false;
    
    int pos = cursor.getPos();
    int indexFrom = cursor.getPos();
    int indexTo = cursor.getUpperBound();
    

    String name = null;
    while (pos < indexTo) {
      char ch = buffer.charAt(pos);
      if (ch == '=') {
        break;
      }
      if (isOneOf(ch, delimiters)) {
        terminated = true;
        break;
      }
      pos++;
    }
    
    if (pos == indexTo) {
      terminated = true;
      name = buffer.substringTrimmed(indexFrom, indexTo);
    } else {
      name = buffer.substringTrimmed(indexFrom, pos);
      pos++;
    }
    
    if (terminated) {
      cursor.updatePos(pos);
      return createNameValuePair(name, null);
    }
    

    String value = null;
    int i1 = pos;
    
    boolean qouted = false;
    boolean escaped = false;
    while (pos < indexTo) {
      char ch = buffer.charAt(pos);
      if ((ch == '"') && (!escaped)) {
        qouted = !qouted;
      }
      if ((!qouted) && (!escaped) && (isOneOf(ch, delimiters))) {
        terminated = true;
        break;
      }
      if (escaped) {
        escaped = false;
      } else {
        escaped = (qouted) && (ch == '\\');
      }
      pos++;
    }
    
    int i2 = pos;
    
    while ((i1 < i2) && (HTTP.isWhitespace(buffer.charAt(i1)))) {
      i1++;
    }
    
    while ((i2 > i1) && (HTTP.isWhitespace(buffer.charAt(i2 - 1)))) {
      i2--;
    }
    
    if ((i2 - i1 >= 2) && (buffer.charAt(i1) == '"') && (buffer.charAt(i2 - 1) == '"'))
    {

      i1++;
      i2--;
    }
    value = buffer.substring(i1, i2);
    if (terminated) {
      pos++;
    }
    cursor.updatePos(pos);
    return createNameValuePair(name, value);
  }
  








  protected NameValuePair createNameValuePair(String name, String value)
  {
    return new BasicNameValuePair(name, value);
  }
}
