package org.apache.http.impl.client.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

























class WarningValue
{
  private int offs;
  private int init_offs;
  private String src;
  private int warnCode;
  private String warnAgent;
  private String warnText;
  private Date warnDate;
  private static final String TOPLABEL = "\\p{Alpha}([\\p{Alnum}-]*\\p{Alnum})?";
  private static final String DOMAINLABEL = "\\p{Alnum}([\\p{Alnum}-]*\\p{Alnum})?";
  private static final String HOSTNAME = "(\\p{Alnum}([\\p{Alnum}-]*\\p{Alnum})?\\.)*\\p{Alpha}([\\p{Alnum}-]*\\p{Alnum})?\\.?";
  private static final String IPV4ADDRESS = "\\d+\\.\\d+\\.\\d+\\.\\d+";
  private static final String HOST = "((\\p{Alnum}([\\p{Alnum}-]*\\p{Alnum})?\\.)*\\p{Alpha}([\\p{Alnum}-]*\\p{Alnum})?\\.?)|(\\d+\\.\\d+\\.\\d+\\.\\d+)";
  private static final String PORT = "\\d*";
  private static final String HOSTPORT = "(((\\p{Alnum}([\\p{Alnum}-]*\\p{Alnum})?\\.)*\\p{Alpha}([\\p{Alnum}-]*\\p{Alnum})?\\.?)|(\\d+\\.\\d+\\.\\d+\\.\\d+))(\\:\\d*)?";
  
  WarningValue(String s)
  {
    this(s, 0);
  }
  
  WarningValue(String s, int offs) {
    this.offs = (this.init_offs = offs);
    this.src = s;
    consumeWarnValue();
  }
  







  public static WarningValue[] getWarningValues(Header h)
  {
    List<WarningValue> out = new ArrayList();
    String src = h.getValue();
    int offs = 0;
    for (;;) { if (offs < src.length())
        try {
          WarningValue wv = new WarningValue(src, offs);
          out.add(wv);
          offs = wv.offs;
        } catch (IllegalArgumentException e) {
          int nextComma = src.indexOf(',', offs);
          if (nextComma != -1)
            offs = nextComma + 1;
        }
    }
    WarningValue[] wvs = new WarningValue[0];
    return (WarningValue[])out.toArray(wvs);
  }
  



  protected void consumeLinearWhitespace()
  {
    while (this.offs < this.src.length()) {
      switch (this.src.charAt(this.offs)) {
      case '\r': 
        if ((this.offs + 2 >= this.src.length()) || (this.src.charAt(this.offs + 1) != '\n') || ((this.src.charAt(this.offs + 2) != ' ') && (this.src.charAt(this.offs + 2) != '\t')))
        {


          return;
        }
        this.offs += 2;
        break;
      case '\t': 
      case ' ': 
        break;
      default: 
        return;
      }
      this.offs += 1;
    }
  }
  


  private boolean isChar(char c)
  {
    int i = c;
    return (i >= 0) && (i <= 127);
  }
  



  private boolean isControl(char c)
  {
    int i = c;
    return (i == 127) || ((i >= 0) && (i <= 31));
  }
  





  private boolean isSeparator(char c)
  {
    return (c == '(') || (c == ')') || (c == '<') || (c == '>') || (c == '@') || (c == ',') || (c == ';') || (c == ':') || (c == '\\') || (c == '"') || (c == '/') || (c == '[') || (c == ']') || (c == '?') || (c == '=') || (c == '{') || (c == '}') || (c == ' ') || (c == '\t');
  }
  






  protected void consumeToken()
  {
    if (!isTokenChar(this.src.charAt(this.offs))) parseError();
    while ((this.offs < this.src.length()) && 
      (isTokenChar(this.src.charAt(this.offs)))) {
      this.offs += 1;
    }
  }
  
  private boolean isTokenChar(char c) {
    return (isChar(c)) && (!isControl(c)) && (!isSeparator(c));
  }
  







  private static final Pattern HOSTPORT_PATTERN = Pattern.compile("(((\\p{Alnum}([\\p{Alnum}-]*\\p{Alnum})?\\.)*\\p{Alpha}([\\p{Alnum}-]*\\p{Alnum})?\\.?)|(\\d+\\.\\d+\\.\\d+\\.\\d+))(\\:\\d*)?");
  private static final String MONTH = "Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec";
  
  protected void consumeHostPort() { Matcher m = HOSTPORT_PATTERN.matcher(this.src.substring(this.offs));
    if (!m.find()) parseError();
    if (m.start() != 0) parseError();
    this.offs += m.end();
  }
  
  private static final String WEEKDAY = "Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday";
  private static final String WKDAY = "Mon|Tue|Wed|Thu|Fri|Sat|Sun";
  private static final String TIME = "\\d{2}:\\d{2}:\\d{2}";
  private static final String DATE3 = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) ( |\\d)\\d";
  private static final String DATE2 = "\\d{2}-(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)-\\d{2}";
  protected void consumeWarnAgent() {
    int curr_offs = this.offs;
    try {
      consumeHostPort();
      this.warnAgent = this.src.substring(curr_offs, this.offs);
      consumeCharacter(' ');
      return;
    } catch (IllegalArgumentException e) {
      this.offs = curr_offs;
      
      consumeToken();
      this.warnAgent = this.src.substring(curr_offs, this.offs);
      consumeCharacter(' ');
    }
  }
  


  protected void consumeQuotedString()
  {
    if (this.src.charAt(this.offs) != '"') parseError();
    this.offs += 1;
    boolean foundEnd = false;
    while ((this.offs < this.src.length()) && (!foundEnd)) {
      char c = this.src.charAt(this.offs);
      if ((this.offs + 1 < this.src.length()) && (c == '\\') && (isChar(this.src.charAt(this.offs + 1))))
      {
        this.offs += 2;
      } else if (c == '"') {
        foundEnd = true;
        this.offs += 1;
      } else if ((c != '"') && (!isControl(c))) {
        this.offs += 1;
      } else {
        parseError();
      }
    }
    if (!foundEnd) { parseError();
    }
  }
  

  protected void consumeWarnText()
  {
    int curr = this.offs;
    consumeQuotedString();
    this.warnText = this.src.substring(curr, this.offs);
  }
  

  private static final String DATE1 = "\\d{2} (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \\d{4}";
  
  private static final String ASCTIME_DATE = "(Mon|Tue|Wed|Thu|Fri|Sat|Sun) ((Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) ( |\\d)\\d) (\\d{2}:\\d{2}:\\d{2}) \\d{4}";
  
  private static final String RFC850_DATE = "(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday), (\\d{2}-(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)-\\d{2}) (\\d{2}:\\d{2}:\\d{2}) GMT";
  
  private static final String RFC1123_DATE = "(Mon|Tue|Wed|Thu|Fri|Sat|Sun), (\\d{2} (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \\d{4}) (\\d{2}:\\d{2}:\\d{2}) GMT";
  
  private static final String HTTP_DATE = "((Mon|Tue|Wed|Thu|Fri|Sat|Sun), (\\d{2} (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \\d{4}) (\\d{2}:\\d{2}:\\d{2}) GMT)|((Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday), (\\d{2}-(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)-\\d{2}) (\\d{2}:\\d{2}:\\d{2}) GMT)|((Mon|Tue|Wed|Thu|Fri|Sat|Sun) ((Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) ( |\\d)\\d) (\\d{2}:\\d{2}:\\d{2}) \\d{4})";
  
  private static final String WARN_DATE = "\"(((Mon|Tue|Wed|Thu|Fri|Sat|Sun), (\\d{2} (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \\d{4}) (\\d{2}:\\d{2}:\\d{2}) GMT)|((Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday), (\\d{2}-(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)-\\d{2}) (\\d{2}:\\d{2}:\\d{2}) GMT)|((Mon|Tue|Wed|Thu|Fri|Sat|Sun) ((Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) ( |\\d)\\d) (\\d{2}:\\d{2}:\\d{2}) \\d{4}))\"";
  private static final Pattern WARN_DATE_PATTERN = Pattern.compile("\"(((Mon|Tue|Wed|Thu|Fri|Sat|Sun), (\\d{2} (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \\d{4}) (\\d{2}:\\d{2}:\\d{2}) GMT)|((Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday), (\\d{2}-(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)-\\d{2}) (\\d{2}:\\d{2}:\\d{2}) GMT)|((Mon|Tue|Wed|Thu|Fri|Sat|Sun) ((Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) ( |\\d)\\d) (\\d{2}:\\d{2}:\\d{2}) \\d{4}))\"");
  


  protected void consumeWarnDate()
  {
    int curr = this.offs;
    Matcher m = WARN_DATE_PATTERN.matcher(this.src.substring(this.offs));
    if (!m.lookingAt()) parseError();
    this.offs += m.end();
    try {
      this.warnDate = DateUtils.parseDate(this.src.substring(curr + 1, this.offs - 1));
    } catch (DateParseException e) {
      throw new IllegalStateException("couldn't parse a parseable date");
    }
  }
  


  protected void consumeWarnValue()
  {
    consumeLinearWhitespace();
    consumeWarnCode();
    consumeWarnAgent();
    consumeWarnText();
    if ((this.offs + 1 < this.src.length()) && (this.src.charAt(this.offs) == ' ') && (this.src.charAt(this.offs + 1) == '"')) {
      consumeCharacter(' ');
      consumeWarnDate();
    }
    consumeLinearWhitespace();
    if (this.offs != this.src.length()) {
      consumeCharacter(',');
    }
  }
  
  protected void consumeCharacter(char c) {
    if ((this.offs + 1 > this.src.length()) || (c != this.src.charAt(this.offs)))
    {
      parseError();
    }
    this.offs += 1;
  }
  


  protected void consumeWarnCode()
  {
    if ((this.offs + 4 > this.src.length()) || (!Character.isDigit(this.src.charAt(this.offs))) || (!Character.isDigit(this.src.charAt(this.offs + 1))) || (!Character.isDigit(this.src.charAt(this.offs + 2))) || (this.src.charAt(this.offs + 3) != ' '))
    {



      parseError();
    }
    this.warnCode = Integer.parseInt(this.src.substring(this.offs, this.offs + 3));
    this.offs += 4;
  }
  
  private void parseError() {
    String s = this.src.substring(this.init_offs);
    throw new IllegalArgumentException("Bad warn code \"" + s + "\"");
  }
  

  public int getWarnCode()
  {
    return this.warnCode;
  }
  


  public String getWarnAgent()
  {
    return this.warnAgent;
  }
  








  public String getWarnText()
  {
    return this.warnText;
  }
  


  public Date getWarnDate()
  {
    return this.warnDate;
  }
  








  public String toString()
  {
    if (this.warnDate != null) {
      return String.format("%d %s %s \"%s\"", new Object[] { Integer.valueOf(this.warnCode), this.warnAgent, this.warnText, DateUtils.formatDate(this.warnDate) });
    }
    
    return String.format("%d %s %s", new Object[] { Integer.valueOf(this.warnCode), this.warnAgent, this.warnText });
  }
}
