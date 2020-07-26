package com.google.gson.stream;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;































































































































public class JsonWriter
  implements Closeable
{
  private static final String[] REPLACEMENT_CHARS = new String[''];
  static { for (int i = 0; i <= 31; i++) {
      REPLACEMENT_CHARS[i] = String.format("\\u%04x", new Object[] { Integer.valueOf(i) });
    }
    REPLACEMENT_CHARS[34] = "\\\"";
    REPLACEMENT_CHARS[92] = "\\\\";
    REPLACEMENT_CHARS[9] = "\\t";
    REPLACEMENT_CHARS[8] = "\\b";
    REPLACEMENT_CHARS[10] = "\\n";
    REPLACEMENT_CHARS[13] = "\\r";
    REPLACEMENT_CHARS[12] = "\\f";
    HTML_SAFE_REPLACEMENT_CHARS = (String[])REPLACEMENT_CHARS.clone();
    HTML_SAFE_REPLACEMENT_CHARS[60] = "\\u003c";
    HTML_SAFE_REPLACEMENT_CHARS[62] = "\\u003e";
    HTML_SAFE_REPLACEMENT_CHARS[38] = "\\u0026";
    HTML_SAFE_REPLACEMENT_CHARS[61] = "\\u003d";
    HTML_SAFE_REPLACEMENT_CHARS[39] = "\\u0027";
  }
  

  private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
  private final Writer out;
  private final List<JsonScope> stack = new ArrayList();
  
  public JsonWriter(Writer out) { this.stack.add(JsonScope.EMPTY_DOCUMENT);
    










    this.separator = ":";
    






    this.serializeNulls = true;
    






    if (out == null) {
      throw new NullPointerException("out == null");
    }
    this.out = out;
  }
  

  private String indent;
  
  private String separator;
  
  private boolean lenient;
  
  public final void setIndent(String indent)
  {
    if (indent.length() == 0) {
      this.indent = null;
      this.separator = ":";
    } else {
      this.indent = indent;
      this.separator = ": ";
    }
  }
  


  private boolean htmlSafe;
  

  private String deferredName;
  

  private boolean serializeNulls;
  

  public final void setLenient(boolean lenient)
  {
    this.lenient = lenient;
  }
  


  public boolean isLenient()
  {
    return this.lenient;
  }
  






  public final void setHtmlSafe(boolean htmlSafe)
  {
    this.htmlSafe = htmlSafe;
  }
  



  public final boolean isHtmlSafe()
  {
    return this.htmlSafe;
  }
  



  public final void setSerializeNulls(boolean serializeNulls)
  {
    this.serializeNulls = serializeNulls;
  }
  



  public final boolean getSerializeNulls()
  {
    return this.serializeNulls;
  }
  




  public JsonWriter beginArray()
    throws IOException
  {
    writeDeferredName();
    return open(JsonScope.EMPTY_ARRAY, "[");
  }
  



  public JsonWriter endArray()
    throws IOException
  {
    return close(JsonScope.EMPTY_ARRAY, JsonScope.NONEMPTY_ARRAY, "]");
  }
  




  public JsonWriter beginObject()
    throws IOException
  {
    writeDeferredName();
    return open(JsonScope.EMPTY_OBJECT, "{");
  }
  



  public JsonWriter endObject()
    throws IOException
  {
    return close(JsonScope.EMPTY_OBJECT, JsonScope.NONEMPTY_OBJECT, "}");
  }
  


  private JsonWriter open(JsonScope empty, String openBracket)
    throws IOException
  {
    beforeValue(true);
    this.stack.add(empty);
    this.out.write(openBracket);
    return this;
  }
  



  private JsonWriter close(JsonScope empty, JsonScope nonempty, String closeBracket)
    throws IOException
  {
    JsonScope context = peek();
    if ((context != nonempty) && (context != empty)) {
      throw new IllegalStateException("Nesting problem: " + this.stack);
    }
    if (this.deferredName != null) {
      throw new IllegalStateException("Dangling name: " + this.deferredName);
    }
    
    this.stack.remove(this.stack.size() - 1);
    if (context == nonempty) {
      newline();
    }
    this.out.write(closeBracket);
    return this;
  }
  


  private JsonScope peek()
  {
    int size = this.stack.size();
    if (size == 0) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    return (JsonScope)this.stack.get(size - 1);
  }
  


  private void replaceTop(JsonScope topOfStack)
  {
    this.stack.set(this.stack.size() - 1, topOfStack);
  }
  




  public JsonWriter name(String name)
    throws IOException
  {
    if (name == null) {
      throw new NullPointerException("name == null");
    }
    if (this.deferredName != null) {
      throw new IllegalStateException();
    }
    if (this.stack.isEmpty()) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    this.deferredName = name;
    return this;
  }
  
  private void writeDeferredName() throws IOException {
    if (this.deferredName != null) {
      beforeName();
      string(this.deferredName);
      this.deferredName = null;
    }
  }
  




  public JsonWriter value(String value)
    throws IOException
  {
    if (value == null) {
      return nullValue();
    }
    writeDeferredName();
    beforeValue(false);
    string(value);
    return this;
  }
  



  public JsonWriter nullValue()
    throws IOException
  {
    if (this.deferredName != null) {
      if (this.serializeNulls) {
        writeDeferredName();
      } else {
        this.deferredName = null;
        return this;
      }
    }
    beforeValue(false);
    this.out.write("null");
    return this;
  }
  



  public JsonWriter value(boolean value)
    throws IOException
  {
    writeDeferredName();
    beforeValue(false);
    this.out.write(value ? "true" : "false");
    return this;
  }
  





  public JsonWriter value(double value)
    throws IOException
  {
    if ((Double.isNaN(value)) || (Double.isInfinite(value))) {
      throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
    }
    writeDeferredName();
    beforeValue(false);
    this.out.append(Double.toString(value));
    return this;
  }
  



  public JsonWriter value(long value)
    throws IOException
  {
    writeDeferredName();
    beforeValue(false);
    this.out.write(Long.toString(value));
    return this;
  }
  





  public JsonWriter value(Number value)
    throws IOException
  {
    if (value == null) {
      return nullValue();
    }
    
    writeDeferredName();
    String string = value.toString();
    if ((!this.lenient) && ((string.equals("-Infinity")) || (string.equals("Infinity")) || (string.equals("NaN"))))
    {
      throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
    }
    beforeValue(false);
    this.out.append(string);
    return this;
  }
  


  public void flush()
    throws IOException
  {
    if (this.stack.isEmpty()) {
      throw new IllegalStateException("JsonWriter is closed.");
    }
    this.out.flush();
  }
  



  public void close()
    throws IOException
  {
    this.out.close();
    
    int size = this.stack.size();
    if ((size > 1) || ((size == 1) && (this.stack.get(size - 1) != JsonScope.NONEMPTY_DOCUMENT))) {
      throw new IOException("Incomplete document");
    }
    this.stack.clear();
  }
  
  private void string(String value) throws IOException {
    String[] replacements = this.htmlSafe ? HTML_SAFE_REPLACEMENT_CHARS : REPLACEMENT_CHARS;
    this.out.write("\"");
    int last = 0;
    int length = value.length();
    for (int i = 0; i < length; i++) {
      char c = value.charAt(i);
      String replacement;
      if (c < '') {
        String replacement = replacements[c];
        if (replacement == null)
          continue;
      } else { String replacement;
        if (c == ' ') {
          replacement = "\\u2028";
        } else { if (c != ' ') continue;
          replacement = "\\u2029";
        }
      }
      
      if (last < i) {
        this.out.write(value, last, i - last);
      }
      this.out.write(replacement);
      last = i + 1;
    }
    if (last < length) {
      this.out.write(value, last, length - last);
    }
    this.out.write("\"");
  }
  
  private void newline() throws IOException {
    if (this.indent == null) {
      return;
    }
    
    this.out.write("\n");
    for (int i = 1; i < this.stack.size(); i++) {
      this.out.write(this.indent);
    }
  }
  


  private void beforeName()
    throws IOException
  {
    JsonScope context = peek();
    if (context == JsonScope.NONEMPTY_OBJECT) {
      this.out.write(44);
    } else if (context != JsonScope.EMPTY_OBJECT) {
      throw new IllegalStateException("Nesting problem: " + this.stack);
    }
    newline();
    replaceTop(JsonScope.DANGLING_NAME);
  }
  







  private void beforeValue(boolean root)
    throws IOException
  {
    switch (1.$SwitchMap$com$google$gson$stream$JsonScope[peek().ordinal()]) {
    case 1: 
      if (!this.lenient) {
        throw new IllegalStateException("JSON must have only one top-level value.");
      }
    

    case 2: 
      if ((!this.lenient) && (!root)) {
        throw new IllegalStateException("JSON must start with an array or an object.");
      }
      
      replaceTop(JsonScope.NONEMPTY_DOCUMENT);
      break;
    
    case 3: 
      replaceTop(JsonScope.NONEMPTY_ARRAY);
      newline();
      break;
    
    case 4: 
      this.out.append(',');
      newline();
      break;
    
    case 5: 
      this.out.append(this.separator);
      replaceTop(JsonScope.NONEMPTY_OBJECT);
      break;
    }
    
    throw new IllegalStateException("Nesting problem: " + this.stack);
  }
}
