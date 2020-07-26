package org.apache.http.impl.client.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
































@Immutable
class RequestProtocolCompliance
{
  private static final List<String> disallowedWithNoCache = Arrays.asList(new String[] { "min-fresh", "max-stale", "max-age" });
  







  public List<RequestProtocolError> requestIsFatallyNonCompliant(HttpRequest request)
  {
    List<RequestProtocolError> theErrors = new ArrayList();
    
    RequestProtocolError anError = requestHasWeakETagAndRange(request);
    if (anError != null) {
      theErrors.add(anError);
    }
    
    anError = requestHasWeekETagForPUTOrDELETEIfMatch(request);
    if (anError != null) {
      theErrors.add(anError);
    }
    
    anError = requestContainsNoCacheDirectiveWithFieldName(request);
    if (anError != null) {
      theErrors.add(anError);
    }
    
    return theErrors;
  }
  








  public HttpRequest makeRequestCompliant(HttpRequest request)
    throws ClientProtocolException
  {
    if (requestMustNotHaveEntity(request)) {
      ((HttpEntityEnclosingRequest)request).setEntity(null);
    }
    
    verifyRequestWithExpectContinueFlagHas100continueHeader(request);
    verifyOPTIONSRequestWithBodyHasContentType(request);
    decrementOPTIONSMaxForwardsIfGreaterThen0(request);
    stripOtherFreshnessDirectivesWithNoCache(request);
    
    if (requestVersionIsTooLow(request)) {
      return upgradeRequestTo(request, HttpVersion.HTTP_1_1);
    }
    
    if (requestMinorVersionIsTooHighMajorVersionsMatch(request)) {
      return downgradeRequestTo(request, HttpVersion.HTTP_1_1);
    }
    
    return request;
  }
  
  private void stripOtherFreshnessDirectivesWithNoCache(HttpRequest request) {
    List<HeaderElement> outElts = new ArrayList();
    boolean shouldStrip = false;
    for (Header h : request.getHeaders("Cache-Control")) {
      for (HeaderElement elt : h.getElements()) {
        if (!disallowedWithNoCache.contains(elt.getName())) {
          outElts.add(elt);
        }
        if ("no-cache".equals(elt.getName())) {
          shouldStrip = true;
        }
      }
    }
    if (!shouldStrip) return;
    request.removeHeaders("Cache-Control");
    request.setHeader("Cache-Control", buildHeaderFromElements(outElts));
  }
  
  private String buildHeaderFromElements(List<HeaderElement> outElts) {
    StringBuilder newHdr = new StringBuilder("");
    boolean first = true;
    for (HeaderElement elt : outElts) {
      if (!first) {
        newHdr.append(",");
      } else {
        first = false;
      }
      newHdr.append(elt.toString());
    }
    return newHdr.toString();
  }
  
  private boolean requestMustNotHaveEntity(HttpRequest request) {
    return ("TRACE".equals(request.getRequestLine().getMethod())) && ((request instanceof HttpEntityEnclosingRequest));
  }
  
  private void decrementOPTIONSMaxForwardsIfGreaterThen0(HttpRequest request)
  {
    if (!"OPTIONS".equals(request.getRequestLine().getMethod())) {
      return;
    }
    
    Header maxForwards = request.getFirstHeader("Max-Forwards");
    if (maxForwards == null) {
      return;
    }
    
    request.removeHeaders("Max-Forwards");
    int currentMaxForwards = Integer.parseInt(maxForwards.getValue());
    
    request.setHeader("Max-Forwards", Integer.toString(currentMaxForwards - 1));
  }
  
  private void verifyOPTIONSRequestWithBodyHasContentType(HttpRequest request) {
    if (!"OPTIONS".equals(request.getRequestLine().getMethod())) {
      return;
    }
    
    if (!(request instanceof HttpEntityEnclosingRequest)) {
      return;
    }
    
    addContentTypeHeaderIfMissing((HttpEntityEnclosingRequest)request);
  }
  
  private void addContentTypeHeaderIfMissing(HttpEntityEnclosingRequest request) {
    if (request.getEntity().getContentType() == null) {
      ((AbstractHttpEntity)request.getEntity()).setContentType(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
    }
  }
  
  private void verifyRequestWithExpectContinueFlagHas100continueHeader(HttpRequest request)
  {
    if ((request instanceof HttpEntityEnclosingRequest))
    {
      if ((((HttpEntityEnclosingRequest)request).expectContinue()) && (((HttpEntityEnclosingRequest)request).getEntity() != null))
      {
        add100ContinueHeaderIfMissing(request);
      } else {
        remove100ContinueHeaderIfExists(request);
      }
    } else {
      remove100ContinueHeaderIfExists(request);
    }
  }
  
  private void remove100ContinueHeaderIfExists(HttpRequest request) {
    boolean hasHeader = false;
    
    Header[] expectHeaders = request.getHeaders("Expect");
    List<HeaderElement> expectElementsThatAreNot100Continue = new ArrayList();
    
    for (Header h : expectHeaders) {
      for (HeaderElement elt : h.getElements()) {
        if (!"100-continue".equalsIgnoreCase(elt.getName())) {
          expectElementsThatAreNot100Continue.add(elt);
        } else {
          hasHeader = true;
        }
      }
      
      if (hasHeader) {
        request.removeHeader(h);
        for (HeaderElement elt : expectElementsThatAreNot100Continue) {
          BasicHeader newHeader = new BasicHeader("Expect", elt.getName());
          request.addHeader(newHeader);
        }
        return;
      }
      expectElementsThatAreNot100Continue = new ArrayList();
    }
  }
  
  private void add100ContinueHeaderIfMissing(HttpRequest request)
  {
    boolean hasHeader = false;
    
    for (Header h : request.getHeaders("Expect")) {
      for (HeaderElement elt : h.getElements()) {
        if ("100-continue".equalsIgnoreCase(elt.getName())) {
          hasHeader = true;
        }
      }
    }
    
    if (!hasHeader) {
      request.addHeader("Expect", "100-continue");
    }
  }
  
  private HttpRequest upgradeRequestTo(HttpRequest request, ProtocolVersion version) throws ClientProtocolException
  {
    RequestWrapper newRequest;
    try {
      newRequest = new RequestWrapper(request);
    } catch (ProtocolException pe) {
      throw new ClientProtocolException(pe);
    }
    newRequest.setProtocolVersion(version);
    
    return newRequest;
  }
  
  private HttpRequest downgradeRequestTo(HttpRequest request, ProtocolVersion version) throws ClientProtocolException
  {
    RequestWrapper newRequest;
    try {
      newRequest = new RequestWrapper(request);
    } catch (ProtocolException pe) {
      throw new ClientProtocolException(pe);
    }
    newRequest.setProtocolVersion(version);
    
    return newRequest;
  }
  
  protected boolean requestMinorVersionIsTooHighMajorVersionsMatch(HttpRequest request) {
    ProtocolVersion requestProtocol = request.getProtocolVersion();
    if (requestProtocol.getMajor() != HttpVersion.HTTP_1_1.getMajor()) {
      return false;
    }
    
    if (requestProtocol.getMinor() > HttpVersion.HTTP_1_1.getMinor()) {
      return true;
    }
    
    return false;
  }
  
  protected boolean requestVersionIsTooLow(HttpRequest request) {
    return request.getProtocolVersion().compareToVersion(HttpVersion.HTTP_1_1) < 0;
  }
  






  public HttpResponse getErrorForRequest(RequestProtocolError errorCheck)
  {
    switch (1.$SwitchMap$org$apache$http$impl$client$cache$RequestProtocolError[errorCheck.ordinal()]) {
    case 1: 
      return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 411, ""));
    

    case 2: 
      return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 400, "Weak eTag not compatible with byte range"));
    

    case 3: 
      return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 400, "Weak eTag not compatible with PUT or DELETE requests"));
    


    case 4: 
      return new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 400, "No-Cache directive MUST NOT include a field name"));
    }
    
    

    throw new IllegalStateException("The request was compliant, therefore no error can be generated for it.");
  }
  



  private RequestProtocolError requestHasWeakETagAndRange(HttpRequest request)
  {
    String method = request.getRequestLine().getMethod();
    if (!"GET".equals(method)) {
      return null;
    }
    
    Header range = request.getFirstHeader("Range");
    if (range == null) {
      return null;
    }
    Header ifRange = request.getFirstHeader("If-Range");
    if (ifRange == null) {
      return null;
    }
    String val = ifRange.getValue();
    if (val.startsWith("W/")) {
      return RequestProtocolError.WEAK_ETAG_AND_RANGE_ERROR;
    }
    
    return null;
  }
  

  private RequestProtocolError requestHasWeekETagForPUTOrDELETEIfMatch(HttpRequest request)
  {
    String method = request.getRequestLine().getMethod();
    if ((!"PUT".equals(method)) && (!"DELETE".equals(method)))
    {
      return null;
    }
    
    Header ifMatch = request.getFirstHeader("If-Match");
    if (ifMatch != null) {
      String val = ifMatch.getValue();
      if (val.startsWith("W/")) {
        return RequestProtocolError.WEAK_ETAG_ON_PUTDELETE_METHOD_ERROR;
      }
    } else {
      Header ifNoneMatch = request.getFirstHeader("If-None-Match");
      if (ifNoneMatch == null) {
        return null;
      }
      String val2 = ifNoneMatch.getValue();
      if (val2.startsWith("W/")) {
        return RequestProtocolError.WEAK_ETAG_ON_PUTDELETE_METHOD_ERROR;
      }
    }
    
    return null;
  }
  
  private RequestProtocolError requestContainsNoCacheDirectiveWithFieldName(HttpRequest request) {
    for (Header h : request.getHeaders("Cache-Control")) {
      for (HeaderElement elt : h.getElements()) {
        if (("no-cache".equalsIgnoreCase(elt.getName())) && (elt.getValue() != null))
        {
          return RequestProtocolError.NO_CACHE_DIRECTIVE_WITH_FIELD_NAME;
        }
      }
    }
    return null;
  }
}
