package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;





















































































































































































public class JsonReader
  implements Closeable
{
  private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
  
  private static final String TRUE = "true";
  
  private static final String FALSE = "false";
  private final StringPool stringPool = new StringPool();
  

  private final Reader in;
  

  private boolean lenient = false;
  






  private final char[] buffer = new char[1024];
  private int pos = 0;
  private int limit = 0;
  



  private int bufferStartLine = 1;
  private int bufferStartColumn = 1;
  



  private JsonScope[] stack = new JsonScope[32];
  private int stackSize = 0;
  
  public JsonReader(Reader in) { push(JsonScope.EMPTY_DOCUMENT);
    



















    this.skipping = false;
    




    if (in == null) {
      throw new NullPointerException("in == null");
    }
    this.in = in;
  }
  




  private JsonToken token;
  


  private String name;
  


  private String value;
  


  private int valuePos;
  


  private int valueLength;
  


  private boolean skipping;
  


  public final void setLenient(boolean lenient)
  {
    this.lenient = lenient;
  }
  


  public final boolean isLenient()
  {
    return this.lenient;
  }
  


  public void beginArray()
    throws IOException
  {
    expect(JsonToken.BEGIN_ARRAY);
  }
  


  public void endArray()
    throws IOException
  {
    expect(JsonToken.END_ARRAY);
  }
  


  public void beginObject()
    throws IOException
  {
    expect(JsonToken.BEGIN_OBJECT);
  }
  


  public void endObject()
    throws IOException
  {
    expect(JsonToken.END_OBJECT);
  }
  

  private void expect(JsonToken expected)
    throws IOException
  {
    peek();
    if (this.token != expected) {
      throw new IllegalStateException("Expected " + expected + " but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    
    advance();
  }
  

  public boolean hasNext()
    throws IOException
  {
    peek();
    return (this.token != JsonToken.END_OBJECT) && (this.token != JsonToken.END_ARRAY);
  }
  

  public JsonToken peek()
    throws IOException
  {
    if (this.token != null) {
      return this.token;
    }
    
    switch (2.$SwitchMap$com$google$gson$stream$JsonScope[this.stack[(this.stackSize - 1)].ordinal()]) {
    case 1: 
      if (this.lenient) {
        consumeNonExecutePrefix();
      }
      this.stack[(this.stackSize - 1)] = JsonScope.NONEMPTY_DOCUMENT;
      JsonToken firstToken = nextValue();
      if ((!this.lenient) && (this.token != JsonToken.BEGIN_ARRAY) && (this.token != JsonToken.BEGIN_OBJECT)) {
        throw new IOException("Expected JSON document to start with '[' or '{' but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
      }
      
      return firstToken;
    case 2: 
      return nextInArray(true);
    case 3: 
      return nextInArray(false);
    case 4: 
      return nextInObject(true);
    case 5: 
      return objectValue();
    case 6: 
      return nextInObject(false);
    case 7: 
      int c = nextNonWhitespace(false);
      if (c == -1) {
        return JsonToken.END_DOCUMENT;
      }
      this.pos -= 1;
      if (!this.lenient) {
        throw syntaxError("Expected EOF");
      }
      return nextValue();
    case 8: 
      throw new IllegalStateException("JsonReader is closed");
    }
    throw new AssertionError();
  }
  



  private void consumeNonExecutePrefix()
    throws IOException
  {
    nextNonWhitespace(true);
    this.pos -= 1;
    
    if ((this.pos + NON_EXECUTE_PREFIX.length > this.limit) && (!fillBuffer(NON_EXECUTE_PREFIX.length))) {
      return;
    }
    
    for (int i = 0; i < NON_EXECUTE_PREFIX.length; i++) {
      if (this.buffer[(this.pos + i)] != NON_EXECUTE_PREFIX[i]) {
        return;
      }
    }
    

    this.pos += NON_EXECUTE_PREFIX.length;
  }
  

  private JsonToken advance()
    throws IOException
  {
    peek();
    
    JsonToken result = this.token;
    this.token = null;
    this.value = null;
    this.name = null;
    return result;
  }
  





  public String nextName()
    throws IOException
  {
    peek();
    if (this.token != JsonToken.NAME) {
      throw new IllegalStateException("Expected a name but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    
    String result = this.name;
    advance();
    return result;
  }
  






  public String nextString()
    throws IOException
  {
    peek();
    if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER)) {
      throw new IllegalStateException("Expected a string but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    

    String result = this.value;
    advance();
    return result;
  }
  





  public boolean nextBoolean()
    throws IOException
  {
    peek();
    if (this.token != JsonToken.BOOLEAN) {
      throw new IllegalStateException("Expected a boolean but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    

    boolean result = this.value == "true";
    advance();
    return result;
  }
  





  public void nextNull()
    throws IOException
  {
    peek();
    if (this.token != JsonToken.NULL) {
      throw new IllegalStateException("Expected null but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    

    advance();
  }
  







  public double nextDouble()
    throws IOException
  {
    peek();
    if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER)) {
      throw new IllegalStateException("Expected a double but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    

    double result = Double.parseDouble(this.value);
    
    if ((result >= 1.0D) && (this.value.startsWith("0"))) {
      throw new MalformedJsonException("JSON forbids octal prefixes: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    
    if ((!this.lenient) && ((Double.isNaN(result)) || (Double.isInfinite(result)))) {
      throw new MalformedJsonException("JSON forbids NaN and infinities: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    

    advance();
    return result;
  }
  








  public long nextLong()
    throws IOException
  {
    peek();
    if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER)) {
      throw new IllegalStateException("Expected a long but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    
    long result;
    try
    {
      result = Long.parseLong(this.value);
    } catch (NumberFormatException ignored) {
      double asDouble = Double.parseDouble(this.value);
      result = asDouble;
      if (result != asDouble) {
        throw new NumberFormatException("Expected a long but was " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
      }
    }
    

    if ((result >= 1L) && (this.value.startsWith("0"))) {
      throw new MalformedJsonException("JSON forbids octal prefixes: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    

    advance();
    return result;
  }
  








  public int nextInt()
    throws IOException
  {
    peek();
    if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER)) {
      throw new IllegalStateException("Expected an int but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    
    int result;
    try
    {
      result = Integer.parseInt(this.value);
    } catch (NumberFormatException ignored) {
      double asDouble = Double.parseDouble(this.value);
      result = (int)asDouble;
      if (result != asDouble) {
        throw new NumberFormatException("Expected an int but was " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
      }
    }
    

    if ((result >= 1L) && (this.value.startsWith("0"))) {
      throw new MalformedJsonException("JSON forbids octal prefixes: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
    }
    

    advance();
    return result;
  }
  

  public void close()
    throws IOException
  {
    this.value = null;
    this.token = null;
    this.stack[0] = JsonScope.CLOSED;
    this.stackSize = 1;
    this.in.close();
  }
  



  public void skipValue()
    throws IOException
  {
    this.skipping = true;
    try {
      int count = 0;
      do {
        JsonToken token = advance();
        if ((token == JsonToken.BEGIN_ARRAY) || (token == JsonToken.BEGIN_OBJECT)) {
          count++;
        } else if ((token == JsonToken.END_ARRAY) || (token == JsonToken.END_OBJECT)) {
          count--;
        }
      } while (count != 0);
    } finally {
      this.skipping = false;
    }
  }
  
  private void push(JsonScope newTop) {
    if (this.stackSize == this.stack.length) {
      JsonScope[] newStack = new JsonScope[this.stackSize * 2];
      System.arraycopy(this.stack, 0, newStack, 0, this.stackSize);
      this.stack = newStack;
    }
    this.stack[(this.stackSize++)] = newTop;
  }
  
  private JsonToken nextInArray(boolean firstElement) throws IOException
  {
    if (firstElement) {
      this.stack[(this.stackSize - 1)] = JsonScope.NONEMPTY_ARRAY;
    }
    else {
      switch (nextNonWhitespace(true)) {
      case 93: 
        this.stackSize -= 1;
        return this.token = JsonToken.END_ARRAY;
      case 59: 
        checkLenient();
      case 44: 
        break;
      default: 
        throw syntaxError("Unterminated array");
      }
      
    }
    switch (nextNonWhitespace(true)) {
    case 93: 
      if (firstElement) {
        this.stackSize -= 1;
        return this.token = JsonToken.END_ARRAY;
      }
    

    case 44: 
    case 59: 
      checkLenient();
      this.pos -= 1;
      this.value = "null";
      return this.token = JsonToken.NULL;
    }
    this.pos -= 1;
    return nextValue();
  }
  





  private JsonToken nextInObject(boolean firstElement)
    throws IOException
  {
    if (firstElement)
    {
      switch (nextNonWhitespace(true)) {
      case 125: 
        this.stackSize -= 1;
        return this.token = JsonToken.END_OBJECT;
      }
      this.pos -= 1;
    }
    else {
      switch (nextNonWhitespace(true)) {
      case 125: 
        this.stackSize -= 1;
        return this.token = JsonToken.END_OBJECT;
      case 44: 
      case 59: 
        break;
      default: 
        throw syntaxError("Unterminated object");
      }
      
    }
    
    int quote = nextNonWhitespace(true);
    switch (quote) {
    case 39: 
      checkLenient();
    case 34: 
      this.name = nextString((char)quote);
      break;
    default: 
      checkLenient();
      this.pos -= 1;
      this.name = nextLiteral(false);
      if (this.name.length() == 0) {
        throw syntaxError("Expected name");
      }
      break;
    }
    this.stack[(this.stackSize - 1)] = JsonScope.DANGLING_NAME;
    return this.token = JsonToken.NAME;
  }
  


  private JsonToken objectValue()
    throws IOException
  {
    switch (nextNonWhitespace(true)) {
    case 58: 
      break;
    case 61: 
      checkLenient();
      if (((this.pos < this.limit) || (fillBuffer(1))) && (this.buffer[this.pos] == '>')) {
        this.pos += 1;
      }
      break;
    default: 
      throw syntaxError("Expected ':'");
    }
    
    this.stack[(this.stackSize - 1)] = JsonScope.NONEMPTY_OBJECT;
    return nextValue();
  }
  
  private JsonToken nextValue() throws IOException
  {
    int c = nextNonWhitespace(true);
    switch (c) {
    case 123: 
      push(JsonScope.EMPTY_OBJECT);
      return this.token = JsonToken.BEGIN_OBJECT;
    
    case 91: 
      push(JsonScope.EMPTY_ARRAY);
      return this.token = JsonToken.BEGIN_ARRAY;
    
    case 39: 
      checkLenient();
    case 34: 
      this.value = nextString((char)c);
      return this.token = JsonToken.STRING;
    }
    
    this.pos -= 1;
    return readLiteral();
  }
  




  private boolean fillBuffer(int minimum)
    throws IOException
  {
    char[] buffer = this.buffer;
    


    int line = this.bufferStartLine;
    int column = this.bufferStartColumn;
    int i = 0; for (int p = this.pos; i < p; i++) {
      if (buffer[i] == '\n') {
        line++;
        column = 1;
      } else {
        column++;
      }
    }
    this.bufferStartLine = line;
    this.bufferStartColumn = column;
    
    if (this.limit != this.pos) {
      this.limit -= this.pos;
      System.arraycopy(buffer, this.pos, buffer, 0, this.limit);
    } else {
      this.limit = 0;
    }
    
    this.pos = 0;
    int total;
    while ((total = this.in.read(buffer, this.limit, buffer.length - this.limit)) != -1) {
      this.limit += total;
      

      if ((this.bufferStartLine == 1) && (this.bufferStartColumn == 1) && (this.limit > 0) && (buffer[0] == 65279)) {
        this.pos += 1;
        this.bufferStartColumn -= 1;
      }
      
      if (this.limit >= minimum) {
        return true;
      }
    }
    return false;
  }
  
  private int getLineNumber() {
    int result = this.bufferStartLine;
    for (int i = 0; i < this.pos; i++) {
      if (this.buffer[i] == '\n') {
        result++;
      }
    }
    return result;
  }
  
  private int getColumnNumber() {
    int result = this.bufferStartColumn;
    for (int i = 0; i < this.pos; i++) {
      if (this.buffer[i] == '\n') {
        result = 1;
      } else {
        result++;
      }
    }
    return result;
  }
  












  private int nextNonWhitespace(boolean throwOnEof)
    throws IOException
  {
    char[] buffer = this.buffer;
    int p = this.pos;
    int l = this.limit;
    int c;
    for (;;) { if (p == l) {
        this.pos = p;
        if (!fillBuffer(1)) {
          break;
        }
        p = this.pos;
        l = this.limit;
      }
      
      c = buffer[(p++)];
      switch (c)
      {
      case 9: 
      case 10: 
      case 13: 
      case 32: 
        break;
      case 47: 
        this.pos = p;
        if (p == l) {
          this.pos -= 1;
          boolean charsLoaded = fillBuffer(2);
          this.pos += 1;
          if (!charsLoaded) {
            return c;
          }
        }
        
        checkLenient();
        char peek = buffer[this.pos];
        switch (peek)
        {
        case '*': 
          this.pos += 1;
          if (!skipTo("*/")) {
            throw syntaxError("Unterminated comment");
          }
          p = this.pos + 2;
          l = this.limit;
          break;
        

        case '/': 
          this.pos += 1;
          skipToEndOfLine();
          p = this.pos;
          l = this.limit;
          break;
        
        default: 
          return c;
        }
        break;
      case 35: 
        this.pos = p;
        




        checkLenient();
        skipToEndOfLine();
        p = this.pos;
        l = this.limit;
      }
      
    }
    this.pos = p;
    return c;
    

    if (throwOnEof) {
      throw new EOFException("End of input at line " + getLineNumber() + " column " + getColumnNumber());
    }
    
    return -1;
  }
  
  private void checkLenient() throws IOException
  {
    if (!this.lenient) {
      throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
    }
  }
  



  private void skipToEndOfLine()
    throws IOException
  {
    while ((this.pos < this.limit) || (fillBuffer(1))) {
      char c = this.buffer[(this.pos++)];
      if ((c == '\r') || (c == '\n')) {
        break;
      }
    }
  }
  
  private boolean skipTo(String toFind) throws IOException {
    label67:
    for (; (this.pos + toFind.length() <= this.limit) || (fillBuffer(toFind.length())); this.pos += 1) {
      for (int c = 0; c < toFind.length(); c++) {
        if (this.buffer[(this.pos + c)] != toFind.charAt(c)) {
          break label67;
        }
      }
      return true;
    }
    return false;
  }
  









  private String nextString(char quote)
    throws IOException
  {
    char[] buffer = this.buffer;
    StringBuilder builder = null;
    for (;;) {
      int p = this.pos;
      int l = this.limit;
      
      int start = p;
      while (p < l) {
        int c = buffer[(p++)];
        
        if (c == quote) {
          this.pos = p;
          if (this.skipping)
            return "skipped!";
          if (builder == null) {
            return this.stringPool.get(buffer, start, p - start - 1);
          }
          builder.append(buffer, start, p - start - 1);
          return builder.toString();
        }
        
        if (c == 92) {
          this.pos = p;
          if (builder == null) {
            builder = new StringBuilder();
          }
          builder.append(buffer, start, p - start - 1);
          builder.append(readEscapeCharacter());
          p = this.pos;
          l = this.limit;
          start = p;
        }
      }
      
      if (builder == null) {
        builder = new StringBuilder();
      }
      builder.append(buffer, start, p - start);
      this.pos = p;
      if (!fillBuffer(1)) {
        throw syntaxError("Unterminated string");
      }
    }
  }
  







  private String nextLiteral(boolean assignOffsetsOnly)
    throws IOException
  {
    StringBuilder builder = null;
    this.valuePos = -1;
    this.valueLength = 0;
    int i = 0;
    
    for (;;)
    {
      if (this.pos + i < this.limit) {
        switch (this.buffer[(this.pos + i)]) {
        case '#': 
        case '/': 
        case ';': 
        case '=': 
        case '\\': 
          checkLenient();
        case '\t': 
        case '\n': 
        case '\f': 
        case '\r': 
        case ' ': 
        case ',': 
        case ':': 
        case '[': 
        case ']': 
        case '{': 
        case '}': 
          break;
        default: 
          i++; break;
        























        }
        
      }
      else if (i < this.buffer.length) {
        if (!fillBuffer(i + 1))
        {

          this.buffer[this.limit] = '\000';
        }
        
      }
      else
      {
        if (builder == null) {
          builder = new StringBuilder();
        }
        builder.append(this.buffer, this.pos, i);
        this.valueLength += i;
        this.pos += i;
        i = 0;
        if (!fillBuffer(1))
          break;
      }
    }
    String result;
    String result;
    if ((assignOffsetsOnly) && (builder == null)) {
      this.valuePos = this.pos;
      result = null; } else { String result;
      if (this.skipping) {
        result = "skipped!"; } else { String result;
        if (builder == null) {
          result = this.stringPool.get(this.buffer, this.pos, i);
        } else {
          builder.append(this.buffer, this.pos, i);
          result = builder.toString();
        } } }
    this.valueLength += i;
    this.pos += i;
    return result;
  }
  
  public String toString() {
    return getClass().getSimpleName() + " at line " + getLineNumber() + " column " + getColumnNumber();
  }
  








  private char readEscapeCharacter()
    throws IOException
  {
    if ((this.pos == this.limit) && (!fillBuffer(1))) {
      throw syntaxError("Unterminated escape sequence");
    }
    
    char escaped = this.buffer[(this.pos++)];
    switch (escaped) {
    case 'u': 
      if ((this.pos + 4 > this.limit) && (!fillBuffer(4))) {
        throw syntaxError("Unterminated escape sequence");
      }
      
      char result = '\000';
      int i = this.pos; for (int end = i + 4; i < end; i++) {
        char c = this.buffer[i];
        result = (char)(result << '\004');
        if ((c >= '0') && (c <= '9')) {
          result = (char)(result + (c - '0'));
        } else if ((c >= 'a') && (c <= 'f')) {
          result = (char)(result + (c - 'a' + 10));
        } else if ((c >= 'A') && (c <= 'F')) {
          result = (char)(result + (c - 'A' + 10));
        } else {
          throw new NumberFormatException("\\u" + this.stringPool.get(this.buffer, this.pos, 4));
        }
      }
      this.pos += 4;
      return result;
    
    case 't': 
      return '\t';
    
    case 'b': 
      return '\b';
    
    case 'n': 
      return '\n';
    
    case 'r': 
      return '\r';
    
    case 'f': 
      return '\f';
    }
    
    


    return escaped;
  }
  


  private JsonToken readLiteral()
    throws IOException
  {
    this.value = nextLiteral(true);
    if (this.valueLength == 0) {
      throw syntaxError("Expected literal value");
    }
    this.token = decodeLiteral();
    if (this.token == JsonToken.STRING) {
      checkLenient();
    }
    return this.token;
  }
  

  private JsonToken decodeLiteral()
    throws IOException
  {
    if (this.valuePos == -1)
    {
      return JsonToken.STRING; }
    if ((this.valueLength == 4) && (('n' == this.buffer[this.valuePos]) || ('N' == this.buffer[this.valuePos])) && (('u' == this.buffer[(this.valuePos + 1)]) || ('U' == this.buffer[(this.valuePos + 1)])) && (('l' == this.buffer[(this.valuePos + 2)]) || ('L' == this.buffer[(this.valuePos + 2)])) && (('l' == this.buffer[(this.valuePos + 3)]) || ('L' == this.buffer[(this.valuePos + 3)])))
    {



      this.value = "null";
      return JsonToken.NULL; }
    if ((this.valueLength == 4) && (('t' == this.buffer[this.valuePos]) || ('T' == this.buffer[this.valuePos])) && (('r' == this.buffer[(this.valuePos + 1)]) || ('R' == this.buffer[(this.valuePos + 1)])) && (('u' == this.buffer[(this.valuePos + 2)]) || ('U' == this.buffer[(this.valuePos + 2)])) && (('e' == this.buffer[(this.valuePos + 3)]) || ('E' == this.buffer[(this.valuePos + 3)])))
    {



      this.value = "true";
      return JsonToken.BOOLEAN; }
    if ((this.valueLength == 5) && (('f' == this.buffer[this.valuePos]) || ('F' == this.buffer[this.valuePos])) && (('a' == this.buffer[(this.valuePos + 1)]) || ('A' == this.buffer[(this.valuePos + 1)])) && (('l' == this.buffer[(this.valuePos + 2)]) || ('L' == this.buffer[(this.valuePos + 2)])) && (('s' == this.buffer[(this.valuePos + 3)]) || ('S' == this.buffer[(this.valuePos + 3)])) && (('e' == this.buffer[(this.valuePos + 4)]) || ('E' == this.buffer[(this.valuePos + 4)])))
    {




      this.value = "false";
      return JsonToken.BOOLEAN;
    }
    this.value = this.stringPool.get(this.buffer, this.valuePos, this.valueLength);
    return decodeNumber(this.buffer, this.valuePos, this.valueLength);
  }
  






  private JsonToken decodeNumber(char[] chars, int offset, int length)
  {
    int i = offset;
    int c = chars[i];
    
    if (c == 45) {
      c = chars[(++i)];
    }
    
    if (c == 48) {
      c = chars[(++i)];
    } else { if ((c >= 49) && (c <= 57))
        c = chars[(++i)];
      while ((c >= 48) && (c <= 57)) {
        c = chars[(++i)]; continue;
        

        return JsonToken.STRING;
      }
    }
    if (c == 46) {
      c = chars[(++i)];
      while ((c >= 48) && (c <= 57)) {
        c = chars[(++i)];
      }
    }
    
    if ((c == 101) || (c == 69)) {
      c = chars[(++i)];
      if ((c == 43) || (c == 45)) {
        c = chars[(++i)];
      }
      if ((c >= 48) && (c <= 57))
        c = chars[(++i)];
      while ((c >= 48) && (c <= 57)) {
        c = chars[(++i)]; continue;
        

        return JsonToken.STRING;
      }
    }
    
    if (i == offset + length) {
      return JsonToken.NUMBER;
    }
    return JsonToken.STRING;
  }
  



  private IOException syntaxError(String message)
    throws IOException
  {
    throw new MalformedJsonException(message + " at line " + getLineNumber() + " column " + getColumnNumber());
  }
  
  static
  {
    JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
      public void promoteNameToValue(JsonReader reader) throws IOException {
        if ((reader instanceof JsonTreeReader)) {
          ((JsonTreeReader)reader).promoteNameToValue();
          return;
        }
        reader.peek();
        if (reader.token != JsonToken.NAME) {
          throw new IllegalStateException("Expected a name but was " + reader.peek() + " " + " at line " + reader.getLineNumber() + " column " + reader.getColumnNumber());
        }
        
        reader.value = reader.name;
        reader.name = null;
        reader.token = JsonToken.STRING;
      }
    };
  }
}
