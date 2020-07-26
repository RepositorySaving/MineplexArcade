package org.apache.commons.codec.binary;

import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;




































public class Hex
  implements BinaryEncoder, BinaryDecoder
{
  public static final String DEFAULT_CHARSET_NAME = "UTF-8";
  private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
  



  private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  




  private final String charsetName;
  




  public static byte[] decodeHex(char[] data)
    throws DecoderException
  {
    int len = data.length;
    
    if ((len & 0x1) != 0) {
      throw new DecoderException("Odd number of characters.");
    }
    
    byte[] out = new byte[len >> 1];
    

    int i = 0; for (int j = 0; j < len; i++) {
      int f = toDigit(data[j], j) << 4;
      j++;
      f |= toDigit(data[j], j);
      j++;
      out[i] = ((byte)(f & 0xFF));
    }
    
    return out;
  }
  








  public static char[] encodeHex(byte[] data)
  {
    return encodeHex(data, true);
  }
  











  public static char[] encodeHex(byte[] data, boolean toLowerCase)
  {
    return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
  }
  











  protected static char[] encodeHex(byte[] data, char[] toDigits)
  {
    int l = data.length;
    char[] out = new char[l << 1];
    
    int i = 0; for (int j = 0; i < l; i++) {
      out[(j++)] = toDigits[((0xF0 & data[i]) >>> 4)];
      out[(j++)] = toDigits[(0xF & data[i])];
    }
    return out;
  }
  








  public static String encodeHexString(byte[] data)
  {
    return new String(encodeHex(data));
  }
  









  protected static int toDigit(char ch, int index)
    throws DecoderException
  {
    int digit = Character.digit(ch, 16);
    if (digit == -1) {
      throw new DecoderException("Illegal hexadecimal character " + ch + " at index " + index);
    }
    return digit;
  }
  





  public Hex()
  {
    this.charsetName = "UTF-8";
  }
  






  public Hex(String csName)
  {
    this.charsetName = csName;
  }
  









  public byte[] decode(byte[] array)
    throws DecoderException
  {
    try
    {
      return decodeHex(new String(array, getCharsetName()).toCharArray());
    } catch (UnsupportedEncodingException e) {
      throw new DecoderException(e.getMessage(), e);
    }
  }
  










  public Object decode(Object object)
    throws DecoderException
  {
    try
    {
      char[] charArray = (object instanceof String) ? ((String)object).toCharArray() : (char[])object;
      return decodeHex(charArray);
    } catch (ClassCastException e) {
      throw new DecoderException(e.getMessage(), e);
    }
  }
  
















  public byte[] encode(byte[] array)
  {
    return StringUtils.getBytesUnchecked(encodeHexString(array), getCharsetName());
  }
  













  public Object encode(Object object)
    throws EncoderException
  {
    try
    {
      byte[] byteArray = (object instanceof String) ? ((String)object).getBytes(getCharsetName()) : (byte[])object;
      return encodeHex(byteArray);
    } catch (ClassCastException e) {
      throw new EncoderException(e.getMessage(), e);
    } catch (UnsupportedEncodingException e) {
      throw new EncoderException(e.getMessage(), e);
    }
  }
  





  public String getCharsetName()
  {
    return this.charsetName;
  }
  





  public String toString()
  {
    return super.toString() + "[charsetName=" + this.charsetName + "]";
  }
}
