package org.apache.commons.codec.net;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;












































public class QCodec
  extends RFC1522Codec
  implements StringEncoder, StringDecoder
{
  private final String charset;
  private static final BitSet PRINTABLE_CHARS = new BitSet(256);
  private static final byte BLANK = 32;
  private static final byte UNDERSCORE = 95;
  
  static { PRINTABLE_CHARS.set(32);
    PRINTABLE_CHARS.set(33);
    PRINTABLE_CHARS.set(34);
    PRINTABLE_CHARS.set(35);
    PRINTABLE_CHARS.set(36);
    PRINTABLE_CHARS.set(37);
    PRINTABLE_CHARS.set(38);
    PRINTABLE_CHARS.set(39);
    PRINTABLE_CHARS.set(40);
    PRINTABLE_CHARS.set(41);
    PRINTABLE_CHARS.set(42);
    PRINTABLE_CHARS.set(43);
    PRINTABLE_CHARS.set(44);
    PRINTABLE_CHARS.set(45);
    PRINTABLE_CHARS.set(46);
    PRINTABLE_CHARS.set(47);
    for (int i = 48; i <= 57; i++) {
      PRINTABLE_CHARS.set(i);
    }
    PRINTABLE_CHARS.set(58);
    PRINTABLE_CHARS.set(59);
    PRINTABLE_CHARS.set(60);
    PRINTABLE_CHARS.set(62);
    PRINTABLE_CHARS.set(64);
    for (int i = 65; i <= 90; i++) {
      PRINTABLE_CHARS.set(i);
    }
    PRINTABLE_CHARS.set(91);
    PRINTABLE_CHARS.set(92);
    PRINTABLE_CHARS.set(93);
    PRINTABLE_CHARS.set(94);
    PRINTABLE_CHARS.set(96);
    for (int i = 97; i <= 122; i++) {
      PRINTABLE_CHARS.set(i);
    }
    PRINTABLE_CHARS.set(123);
    PRINTABLE_CHARS.set(124);
    PRINTABLE_CHARS.set(125);
    PRINTABLE_CHARS.set(126);
  }
  




  private boolean encodeBlanks = false;
  


  public QCodec()
  {
    this("UTF-8");
  }
  








  public QCodec(String charset)
  {
    this.charset = charset;
  }
  
  protected String getEncoding()
  {
    return "Q";
  }
  
  protected byte[] doEncoding(byte[] bytes)
  {
    if (bytes == null) {
      return null;
    }
    byte[] data = QuotedPrintableCodec.encodeQuotedPrintable(PRINTABLE_CHARS, bytes);
    if (this.encodeBlanks) {
      for (int i = 0; i < data.length; i++) {
        if (data[i] == 32) {
          data[i] = 95;
        }
      }
    }
    return data;
  }
  
  protected byte[] doDecoding(byte[] bytes) throws DecoderException
  {
    if (bytes == null) {
      return null;
    }
    boolean hasUnderscores = false;
    for (byte b : bytes) {
      if (b == 95) {
        hasUnderscores = true;
        break;
      }
    }
    if (hasUnderscores) {
      byte[] tmp = new byte[bytes.length];
      for (int i = 0; i < bytes.length; i++) {
        byte b = bytes[i];
        if (b != 95) {
          tmp[i] = b;
        } else {
          tmp[i] = 32;
        }
      }
      return QuotedPrintableCodec.decodeQuotedPrintable(tmp);
    }
    return QuotedPrintableCodec.decodeQuotedPrintable(bytes);
  }
  










  public String encode(String pString, String charset)
    throws EncoderException
  {
    if (pString == null) {
      return null;
    }
    try {
      return encodeText(pString, charset);
    } catch (UnsupportedEncodingException e) {
      throw new EncoderException(e.getMessage(), e);
    }
  }
  








  public String encode(String pString)
    throws EncoderException
  {
    if (pString == null) {
      return null;
    }
    return encode(pString, getDefaultCharset());
  }
  










  public String decode(String pString)
    throws DecoderException
  {
    if (pString == null) {
      return null;
    }
    try {
      return decodeText(pString);
    } catch (UnsupportedEncodingException e) {
      throw new DecoderException(e.getMessage(), e);
    }
  }
  








  public Object encode(Object pObject)
    throws EncoderException
  {
    if (pObject == null)
      return null;
    if ((pObject instanceof String)) {
      return encode((String)pObject);
    }
    throw new EncoderException("Objects of type " + pObject.getClass().getName() + " cannot be encoded using Q codec");
  }
  














  public Object decode(Object pObject)
    throws DecoderException
  {
    if (pObject == null)
      return null;
    if ((pObject instanceof String)) {
      return decode((String)pObject);
    }
    throw new DecoderException("Objects of type " + pObject.getClass().getName() + " cannot be decoded using Q codec");
  }
  







  public String getDefaultCharset()
  {
    return this.charset;
  }
  




  public boolean isEncodeBlanks()
  {
    return this.encodeBlanks;
  }
  





  public void setEncodeBlanks(boolean b)
  {
    this.encodeBlanks = b;
  }
}
