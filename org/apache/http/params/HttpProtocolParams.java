package org.apache.http.params;

import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.protocol.HTTP;












































public final class HttpProtocolParams
  implements CoreProtocolPNames
{
  public static String getHttpElementCharset(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    String charset = (String)params.getParameter("http.protocol.element-charset");
    
    if (charset == null) {
      charset = HTTP.DEF_PROTOCOL_CHARSET.name();
    }
    return charset;
  }
  





  public static void setHttpElementCharset(HttpParams params, String charset)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setParameter("http.protocol.element-charset", charset);
  }
  






  public static String getContentCharset(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    String charset = (String)params.getParameter("http.protocol.content-charset");
    
    if (charset == null) {
      charset = HTTP.DEF_CONTENT_CHARSET.name();
    }
    return charset;
  }
  





  public static void setContentCharset(HttpParams params, String charset)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setParameter("http.protocol.content-charset", charset);
  }
  






  public static ProtocolVersion getVersion(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    Object param = params.getParameter("http.protocol.version");
    
    if (param == null) {
      return HttpVersion.HTTP_1_1;
    }
    return (ProtocolVersion)param;
  }
  





  public static void setVersion(HttpParams params, ProtocolVersion version)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setParameter("http.protocol.version", version);
  }
  






  public static String getUserAgent(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    return (String)params.getParameter("http.useragent");
  }
  





  public static void setUserAgent(HttpParams params, String useragent)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setParameter("http.useragent", useragent);
  }
  






  public static boolean useExpectContinue(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    return params.getBooleanParameter("http.protocol.expect-continue", false);
  }
  






  public static void setUseExpectContinue(HttpParams params, boolean b)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setBooleanParameter("http.protocol.expect-continue", b);
  }
  






  public static CodingErrorAction getMalformedInputAction(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    Object param = params.getParameter("http.malformed.input.action");
    if (param == null)
    {
      return CodingErrorAction.REPORT;
    }
    return (CodingErrorAction)param;
  }
  






  public static void setMalformedInputAction(HttpParams params, CodingErrorAction action)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    params.setParameter("http.malformed.input.action", action);
  }
  






  public static CodingErrorAction getUnmappableInputAction(HttpParams params)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    Object param = params.getParameter("http.unmappable.input.action");
    if (param == null)
    {
      return CodingErrorAction.REPORT;
    }
    return (CodingErrorAction)param;
  }
  






  public static void setUnmappableInputAction(HttpParams params, CodingErrorAction action)
  {
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may no be null");
    }
    params.setParameter("http.unmappable.input.action", action);
  }
}
