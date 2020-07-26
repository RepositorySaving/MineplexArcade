package org.apache.http.impl.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;






















































@NotThreadSafe
public abstract class AbstractMessageParser<T extends HttpMessage>
  implements HttpMessageParser<T>
{
  private static final int HEAD_LINE = 0;
  private static final int HEADERS = 1;
  private final SessionInputBuffer sessionBuffer;
  private final int maxHeaderCount;
  private final int maxLineLen;
  private final List<CharArrayBuffer> headerLines;
  protected final LineParser lineParser;
  private int state;
  private T message;
  
  public AbstractMessageParser(SessionInputBuffer buffer, LineParser parser, HttpParams params)
  {
    if (buffer == null) {
      throw new IllegalArgumentException("Session input buffer may not be null");
    }
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    this.sessionBuffer = buffer;
    this.maxHeaderCount = params.getIntParameter("http.connection.max-header-count", -1);
    
    this.maxLineLen = params.getIntParameter("http.connection.max-line-length", -1);
    
    this.lineParser = (parser != null ? parser : BasicLineParser.DEFAULT);
    this.headerLines = new ArrayList();
    this.state = 0;
  }
  






















  public static Header[] parseHeaders(SessionInputBuffer inbuffer, int maxHeaderCount, int maxLineLen, LineParser parser)
    throws HttpException, IOException
  {
    if (parser == null) {
      parser = BasicLineParser.DEFAULT;
    }
    List<CharArrayBuffer> headerLines = new ArrayList();
    return parseHeaders(inbuffer, maxHeaderCount, maxLineLen, parser, headerLines);
  }
  





























  public static Header[] parseHeaders(SessionInputBuffer inbuffer, int maxHeaderCount, int maxLineLen, LineParser parser, List<CharArrayBuffer> headerLines)
    throws HttpException, IOException
  {
    if (inbuffer == null) {
      throw new IllegalArgumentException("Session input buffer may not be null");
    }
    if (parser == null) {
      throw new IllegalArgumentException("Line parser may not be null");
    }
    if (headerLines == null) {
      throw new IllegalArgumentException("Header line list may not be null");
    }
    
    CharArrayBuffer current = null;
    CharArrayBuffer previous = null;
    for (;;) {
      if (current == null) {
        current = new CharArrayBuffer(64);
      } else {
        current.clear();
      }
      int l = inbuffer.readLine(current);
      if ((l == -1) || (current.length() < 1)) {
        break;
      }
      



      if (((current.charAt(0) == ' ') || (current.charAt(0) == '\t')) && (previous != null))
      {

        int i = 0;
        while (i < current.length()) {
          char ch = current.charAt(i);
          if ((ch != ' ') && (ch != '\t')) {
            break;
          }
          i++;
        }
        if ((maxLineLen > 0) && (previous.length() + 1 + current.length() - i > maxLineLen))
        {
          throw new IOException("Maximum line length limit exceeded");
        }
        previous.append(' ');
        previous.append(current, i, current.length() - i);
      } else {
        headerLines.add(current);
        previous = current;
        current = null;
      }
      if ((maxHeaderCount > 0) && (headerLines.size() >= maxHeaderCount)) {
        throw new IOException("Maximum header count exceeded");
      }
    }
    Header[] headers = new Header[headerLines.size()];
    for (int i = 0; i < headerLines.size(); i++) {
      CharArrayBuffer buffer = (CharArrayBuffer)headerLines.get(i);
      try {
        headers[i] = parser.parseHeader(buffer);
      } catch (ParseException ex) {
        throw new ProtocolException(ex.getMessage());
      }
    }
    return headers;
  }
  






  protected abstract T parseHead(SessionInputBuffer paramSessionInputBuffer)
    throws IOException, HttpException, ParseException;
  






  public T parse()
    throws IOException, HttpException
  {
    int st = this.state;
    switch (st) {
    case 0: 
      try {
        this.message = parseHead(this.sessionBuffer);
      } catch (ParseException px) {
        throw new ProtocolException(px.getMessage(), px);
      }
      this.state = 1;
    
    case 1: 
      Header[] headers = parseHeaders(this.sessionBuffer, this.maxHeaderCount, this.maxLineLen, this.lineParser, this.headerLines);
      




      this.message.setHeaders(headers);
      T result = this.message;
      this.message = null;
      this.headerLines.clear();
      this.state = 0;
      return result;
    }
    throw new IllegalStateException("Inconsistent parser state");
  }
}
