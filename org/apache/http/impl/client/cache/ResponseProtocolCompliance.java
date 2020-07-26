package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;









































@Immutable
class ResponseProtocolCompliance
{
  private static final String UNEXPECTED_100_CONTINUE = "The incoming request did not contain a 100-continue header, but the response was a Status 100, continue.";
  private static final String UNEXPECTED_PARTIAL_CONTENT = "partial content was returned for a request that did not ask for it";
  
  public void ensureProtocolCompliance(HttpRequest request, HttpResponse response)
    throws IOException
  {
    if (backendResponseMustNotHaveBody(request, response)) {
      consumeBody(response);
      response.setEntity(null);
    }
    
    requestDidNotExpect100ContinueButResponseIsOne(request, response);
    
    transferEncodingIsNotReturnedTo1_0Client(request, response);
    
    ensurePartialContentIsNotSentToAClientThatDidNotRequestIt(request, response);
    
    ensure200ForOPTIONSRequestWithNoBodyHasContentLengthZero(request, response);
    
    ensure206ContainsDateHeader(response);
    
    ensure304DoesNotContainExtraEntityHeaders(response);
    
    identityIsNotUsedInContentEncoding(response);
    
    warningsWithNonMatchingWarnDatesAreRemoved(response);
  }
  
  private void consumeBody(HttpResponse response) throws IOException {
    HttpEntity body = response.getEntity();
    if (body != null) EntityUtils.consume(body);
  }
  
  private void warningsWithNonMatchingWarnDatesAreRemoved(HttpResponse response)
  {
    Date responseDate = null;
    try {
      responseDate = DateUtils.parseDate(response.getFirstHeader("Date").getValue());
    }
    catch (DateParseException e) {}
    

    if (responseDate == null) { return;
    }
    Header[] warningHeaders = response.getHeaders("Warning");
    
    if ((warningHeaders == null) || (warningHeaders.length == 0)) { return;
    }
    List<Header> newWarningHeaders = new ArrayList();
    boolean modified = false;
    for (Header h : warningHeaders) {
      for (WarningValue wv : WarningValue.getWarningValues(h)) {
        Date warnDate = wv.getWarnDate();
        if ((warnDate == null) || (warnDate.equals(responseDate))) {
          newWarningHeaders.add(new BasicHeader("Warning", wv.toString()));
        } else {
          modified = true;
        }
      }
    }
    if (modified) {
      response.removeHeaders("Warning");
      for (Header h : newWarningHeaders) {
        response.addHeader(h);
      }
    }
  }
  
  private void identityIsNotUsedInContentEncoding(HttpResponse response) {
    Header[] hdrs = response.getHeaders("Content-Encoding");
    if ((hdrs == null) || (hdrs.length == 0)) return;
    List<Header> newHeaders = new ArrayList();
    boolean modified = false;
    for (Header h : hdrs) {
      StringBuilder buf = new StringBuilder();
      boolean first = true;
      for (HeaderElement elt : h.getElements()) {
        if ("identity".equalsIgnoreCase(elt.getName())) {
          modified = true;
        } else {
          if (!first) buf.append(",");
          buf.append(elt.toString());
          first = false;
        }
      }
      String newHeaderValue = buf.toString();
      if (!"".equals(newHeaderValue)) {
        newHeaders.add(new BasicHeader("Content-Encoding", newHeaderValue));
      }
    }
    if (!modified) return;
    response.removeHeaders("Content-Encoding");
    for (Header h : newHeaders) {
      response.addHeader(h);
    }
  }
  
  private void ensure206ContainsDateHeader(HttpResponse response) {
    if (response.getFirstHeader("Date") == null) {
      response.addHeader("Date", DateUtils.formatDate(new Date()));
    }
  }
  
  private void ensurePartialContentIsNotSentToAClientThatDidNotRequestIt(HttpRequest request, HttpResponse response)
    throws IOException
  {
    if ((request.getFirstHeader("Range") != null) || (response.getStatusLine().getStatusCode() != 206))
    {
      return;
    }
    consumeBody(response);
    throw new ClientProtocolException("partial content was returned for a request that did not ask for it");
  }
  
  private void ensure200ForOPTIONSRequestWithNoBodyHasContentLengthZero(HttpRequest request, HttpResponse response)
  {
    if (!request.getRequestLine().getMethod().equalsIgnoreCase("OPTIONS")) {
      return;
    }
    
    if (response.getStatusLine().getStatusCode() != 200) {
      return;
    }
    
    if (response.getFirstHeader("Content-Length") == null) {
      response.addHeader("Content-Length", "0");
    }
  }
  
  private void ensure304DoesNotContainExtraEntityHeaders(HttpResponse response) {
    String[] disallowedEntityHeaders = { "Allow", "Content-Encoding", "Content-Language", "Content-Length", "Content-MD5", "Content-Range", "Content-Type", "Last-Modified" };
    


    if (response.getStatusLine().getStatusCode() == 304) {
      for (String hdr : disallowedEntityHeaders) {
        response.removeHeaders(hdr);
      }
    }
  }
  
  private boolean backendResponseMustNotHaveBody(HttpRequest request, HttpResponse backendResponse) {
    return ("HEAD".equals(request.getRequestLine().getMethod())) || (backendResponse.getStatusLine().getStatusCode() == 204) || (backendResponse.getStatusLine().getStatusCode() == 205) || (backendResponse.getStatusLine().getStatusCode() == 304);
  }
  


  private void requestDidNotExpect100ContinueButResponseIsOne(HttpRequest request, HttpResponse response)
    throws IOException
  {
    if (response.getStatusLine().getStatusCode() != 100) {
      return;
    }
    
    HttpRequest originalRequest = requestWasWrapped(request) ? ((RequestWrapper)request).getOriginal() : request;
    
    if (((originalRequest instanceof HttpEntityEnclosingRequest)) && 
      (((HttpEntityEnclosingRequest)originalRequest).expectContinue())) { return;
    }
    consumeBody(response);
    throw new ClientProtocolException("The incoming request did not contain a 100-continue header, but the response was a Status 100, continue.");
  }
  
  private void transferEncodingIsNotReturnedTo1_0Client(HttpRequest request, HttpResponse response) {
    if (!requestWasWrapped(request)) {
      return;
    }
    
    ProtocolVersion originalProtocol = getOriginalRequestProtocol((RequestWrapper)request);
    
    if (originalProtocol.compareToVersion(HttpVersion.HTTP_1_1) >= 0) {
      return;
    }
    
    removeResponseTransferEncoding(response);
  }
  
  private void removeResponseTransferEncoding(HttpResponse response) {
    response.removeHeaders("TE");
    response.removeHeaders("Transfer-Encoding");
  }
  
  private ProtocolVersion getOriginalRequestProtocol(RequestWrapper request) {
    return request.getOriginal().getProtocolVersion();
  }
  
  private boolean requestWasWrapped(HttpRequest request) {
    return request instanceof RequestWrapper;
  }
}
