package org.apache.http.client.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Immutable;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;















































@Immutable
public class URLEncodedUtils
{
  public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
  private static final String PARAMETER_SEPARATOR = "&";
  private static final String NAME_VALUE_SEPARATOR = "=";
  
  public static List<NameValuePair> parse(URI uri, String encoding)
  {
    String query = uri.getRawQuery();
    if ((query != null) && (query.length() > 0)) {
      List<NameValuePair> result = new ArrayList();
      Scanner scanner = new Scanner(query);
      parse(result, scanner, encoding);
      return result;
    }
    return Collections.emptyList();
  }
  












  public static List<NameValuePair> parse(HttpEntity entity)
    throws IOException
  {
    ContentType contentType = ContentType.get(entity);
    if ((contentType != null) && (contentType.getMimeType().equalsIgnoreCase("application/x-www-form-urlencoded"))) {
      String content = EntityUtils.toString(entity, Consts.ASCII);
      if ((content != null) && (content.length() > 0)) {
        Charset charset = contentType != null ? contentType.getCharset() : null;
        if (charset == null) {
          charset = HTTP.DEF_CONTENT_CHARSET;
        }
        return parse(content, charset);
      }
    }
    return Collections.emptyList();
  }
  



  public static boolean isEncoded(HttpEntity entity)
  {
    Header h = entity.getContentType();
    if (h != null) {
      HeaderElement[] elems = h.getElements();
      if (elems.length > 0) {
        String contentType = elems[0].getName();
        return contentType.equalsIgnoreCase("application/x-www-form-urlencoded");
      }
      return false;
    }
    
    return false;
  }
  

















  public static void parse(List<NameValuePair> parameters, Scanner scanner, String charset)
  {
    scanner.useDelimiter("&");
    while (scanner.hasNext()) {
      String name = null;
      String value = null;
      String token = scanner.next();
      int i = token.indexOf("=");
      if (i != -1) {
        name = decode(token.substring(0, i).trim(), charset);
        value = decode(token.substring(i + 1).trim(), charset);
      } else {
        name = decode(token.trim(), charset);
      }
      parameters.add(new BasicNameValuePair(name, value));
    }
  }
  
  private static final char[] DELIM = { '&' };
  










  public static List<NameValuePair> parse(String s, Charset charset)
  {
    if (s == null) {
      return Collections.emptyList();
    }
    BasicHeaderValueParser parser = BasicHeaderValueParser.DEFAULT;
    CharArrayBuffer buffer = new CharArrayBuffer(s.length());
    buffer.append(s);
    ParserCursor cursor = new ParserCursor(0, buffer.length());
    List<NameValuePair> list = new ArrayList();
    while (!cursor.atEnd()) {
      NameValuePair nvp = parser.parseNameValuePair(buffer, cursor, DELIM);
      if (nvp.getName().length() > 0) {
        list.add(new BasicNameValuePair(decode(nvp.getName(), charset), decode(nvp.getValue(), charset)));
      }
    }
    

    return list;
  }
  








  public static String format(List<? extends NameValuePair> parameters, String encoding)
  {
    StringBuilder result = new StringBuilder();
    for (NameValuePair parameter : parameters) {
      String encodedName = encode(parameter.getName(), encoding);
      String encodedValue = encode(parameter.getValue(), encoding);
      if (result.length() > 0) {
        result.append("&");
      }
      result.append(encodedName);
      if (encodedValue != null) {
        result.append("=");
        result.append(encodedValue);
      }
    }
    return result.toString();
  }
  










  public static String format(Iterable<? extends NameValuePair> parameters, Charset charset)
  {
    StringBuilder result = new StringBuilder();
    for (NameValuePair parameter : parameters) {
      String encodedName = encode(parameter.getName(), charset);
      String encodedValue = encode(parameter.getValue(), charset);
      if (result.length() > 0) {
        result.append("&");
      }
      result.append(encodedName);
      if (encodedValue != null) {
        result.append("=");
        result.append(encodedValue);
      }
    }
    return result.toString();
  }
  
  private static String decode(String content, String charset) {
    if (content == null) {
      return null;
    }
    try {
      return URLDecoder.decode(content, charset != null ? charset : HTTP.DEF_CONTENT_CHARSET.name());
    }
    catch (UnsupportedEncodingException ex) {
      throw new IllegalArgumentException(ex);
    }
  }
  
  private static String decode(String content, Charset charset) {
    if (content == null) {
      return null;
    }
    return decode(content, charset != null ? charset.name() : null);
  }
  
  private static String encode(String content, String charset) {
    if (content == null) {
      return null;
    }
    try {
      return URLEncoder.encode(content, charset != null ? charset : HTTP.DEF_CONTENT_CHARSET.name());
    }
    catch (UnsupportedEncodingException ex) {
      throw new IllegalArgumentException(ex);
    }
  }
  
  private static String encode(String content, Charset charset) {
    if (content == null) {
      return null;
    }
    return encode(content, charset != null ? charset.name() : null);
  }
}
