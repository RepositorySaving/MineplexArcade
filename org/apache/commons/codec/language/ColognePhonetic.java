package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

















































































































































































public class ColognePhonetic
  implements StringEncoder
{
  private abstract class CologneBuffer
  {
    protected final char[] data;
    protected int length = 0;
    
    public CologneBuffer(char[] data) {
      this.data = data;
      this.length = data.length;
    }
    
    public CologneBuffer(int buffSize) {
      this.data = new char[buffSize];
      this.length = 0;
    }
    
    protected abstract char[] copyData(int paramInt1, int paramInt2);
    
    public int length() {
      return this.length;
    }
    
    public String toString()
    {
      return new String(copyData(0, this.length));
    }
  }
  
  private class CologneOutputBuffer extends ColognePhonetic.CologneBuffer
  {
    public CologneOutputBuffer(int buffSize) {
      super(buffSize);
    }
    
    public void addRight(char chr) {
      this.data[this.length] = chr;
      this.length += 1;
    }
    
    protected char[] copyData(int start, int length)
    {
      char[] newData = new char[length];
      System.arraycopy(this.data, start, newData, 0, length);
      return newData;
    }
  }
  
  private class CologneInputBuffer extends ColognePhonetic.CologneBuffer
  {
    public CologneInputBuffer(char[] data) {
      super(data);
    }
    
    public void addLeft(char ch) {
      this.length += 1;
      this.data[getNextPos()] = ch;
    }
    
    protected char[] copyData(int start, int length)
    {
      char[] newData = new char[length];
      System.arraycopy(this.data, this.data.length - this.length + start, newData, 0, length);
      return newData;
    }
    
    public char getNextChar() {
      return this.data[getNextPos()];
    }
    
    protected int getNextPos() {
      return this.data.length - this.length;
    }
    
    public char removeNext() {
      char ch = getNextChar();
      this.length -= 1;
      return ch;
    }
  }
  









  private static final char[][] PREPROCESS_MAP = { { 'Ä', 'A' }, { 'Ü', 'U' }, { 'Ö', 'O' }, { 'ß', 'S' } };
  







  private static boolean arrayContains(char[] arr, char key)
  {
    for (char element : arr) {
      if (element == key) {
        return true;
      }
    }
    return false;
  }
  










  public String colognePhonetic(String text)
  {
    if (text == null) {
      return null;
    }
    
    text = preprocess(text);
    
    CologneOutputBuffer output = new CologneOutputBuffer(text.length() * 2);
    CologneInputBuffer input = new CologneInputBuffer(text.toCharArray());
    


    char lastChar = '-';
    char lastCode = '/';
    


    int rightLength = input.length();
    
    while (rightLength > 0) {
      char chr = input.removeNext();
      char nextChar;
      char nextChar; if ((rightLength = input.length()) > 0) {
        nextChar = input.getNextChar();
      } else
        nextChar = '-';
      char code;
      char code;
      if (arrayContains(new char[] { 'A', 'E', 'I', 'J', 'O', 'U', 'Y' }, chr)) {
        code = '0';
      } else if ((chr == 'H') || (chr < 'A') || (chr > 'Z')) {
        if (lastCode == '/') {
          continue;
        }
        char code = '-'; } else { char code;
        if ((chr == 'B') || ((chr == 'P') && (nextChar != 'H'))) {
          code = '1';
        } else { if ((chr == 'D') || (chr == 'T')) if (!arrayContains(new char[] { 'S', 'C', 'Z' }, nextChar)) {
              char code = '2';
              break label654; } char code; if (arrayContains(new char[] { 'W', 'F', 'P', 'V' }, chr)) {
            code = '3'; } else { char code;
            if (arrayContains(new char[] { 'G', 'K', 'Q' }, chr)) {
              code = '4';
            } else { if (chr == 'X') if (!arrayContains(new char[] { 'C', 'K', 'Q' }, lastChar)) {
                  char code = '4';
                  input.addLeft('S');
                  rightLength++;
                  break label654; } char code; if ((chr == 'S') || (chr == 'Z')) {
                code = '8'; } else { char code;
                if (chr == 'C') { char code;
                  if (lastCode == '/') { char code;
                    if (arrayContains(new char[] { 'A', 'H', 'K', 'L', 'O', 'Q', 'R', 'U', 'X' }, nextChar)) {
                      code = '4';
                    } else {
                      code = '8';
                    }
                  } else {
                    if (!arrayContains(new char[] { 'S', 'Z' }, lastChar)) { if (arrayContains(new char[] { 'A', 'H', 'O', 'U', 'K', 'Q', 'X' }, nextChar)) {}
                    } else {
                      char code = '8';
                      break label654; }
                    code = '4';
                  }
                } else { char code;
                  if (arrayContains(new char[] { 'T', 'D', 'X' }, chr)) {
                    code = '8'; } else { char code;
                    if (chr == 'R') {
                      code = '7'; } else { char code;
                      if (chr == 'L') {
                        code = '5'; } else { char code;
                        if ((chr == 'M') || (chr == 'N')) {
                          code = '6';
                        } else
                          code = chr;
                      } } } } } } } } }
      label654:
      if ((code != '-') && (((lastCode != code) && ((code != '0') || (lastCode == '/'))) || (code < '0') || (code > '8'))) {
        output.addRight(code);
      }
      
      lastChar = chr;
      lastCode = code;
    }
    return output.toString();
  }
  
  public Object encode(Object object) throws EncoderException {
    if (!(object instanceof String)) {
      throw new EncoderException("This method's parameter was expected to be of the type " + String.class.getName() + ". But actually it was of the type " + object.getClass().getName() + ".");
    }
    



    return encode((String)object);
  }
  
  public String encode(String text) {
    return colognePhonetic(text);
  }
  
  public boolean isEncodeEqual(String text1, String text2) {
    return colognePhonetic(text1).equals(colognePhonetic(text2));
  }
  


  private String preprocess(String text)
  {
    text = text.toUpperCase(Locale.GERMAN);
    
    char[] chrs = text.toCharArray();
    
    for (int index = 0; index < chrs.length; index++) {
      if (chrs[index] > 'Z') {
        for (char[] element : PREPROCESS_MAP) {
          if (chrs[index] == element[0]) {
            chrs[index] = element[1];
            break;
          }
        }
      }
    }
    return new String(chrs);
  }
}
