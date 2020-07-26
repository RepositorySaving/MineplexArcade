package org.apache.http.client.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

































public class URIBuilder
{
  private String scheme;
  private String schemeSpecificPart;
  private String authority;
  private String userInfo;
  private String host;
  private int port;
  private String path;
  private List<NameValuePair> queryParams;
  private String fragment;
  
  public URIBuilder()
  {
    this.port = -1;
  }
  
  public URIBuilder(String string) throws URISyntaxException
  {
    digestURI(new URI(string));
  }
  
  public URIBuilder(URI uri)
  {
    digestURI(uri);
  }
  
  private List<NameValuePair> parseQuery(String query, Charset charset) {
    if ((query != null) && (query.length() > 0)) {
      return URLEncodedUtils.parse(query, charset);
    }
    return null;
  }
  
  private String formatQuery(List<NameValuePair> parameters, Charset charset) {
    if (parameters == null) {
      return null;
    }
    return URLEncodedUtils.format(parameters, charset);
  }
  

  public URI build()
    throws URISyntaxException
  {
    if (this.schemeSpecificPart != null)
      return new URI(this.scheme, this.schemeSpecificPart, this.fragment);
    if (this.authority != null) {
      return new URI(this.scheme, this.authority, this.path, formatQuery(this.queryParams, Consts.UTF_8), this.fragment);
    }
    

    return new URI(this.scheme, this.userInfo, this.host, this.port, this.path, formatQuery(this.queryParams, Consts.UTF_8), this.fragment);
  }
  

  private void digestURI(URI uri)
  {
    this.scheme = uri.getScheme();
    this.schemeSpecificPart = uri.getSchemeSpecificPart();
    this.authority = uri.getAuthority();
    this.host = uri.getHost();
    this.port = uri.getPort();
    this.userInfo = uri.getUserInfo();
    this.path = uri.getPath();
    this.queryParams = parseQuery(uri.getRawQuery(), Consts.UTF_8);
    this.fragment = uri.getFragment();
  }
  
  public URIBuilder setScheme(String scheme) {
    this.scheme = scheme;
    return this;
  }
  
  public URIBuilder setUserInfo(String userInfo) {
    this.userInfo = userInfo;
    this.schemeSpecificPart = null;
    this.authority = null;
    return this;
  }
  
  public URIBuilder setUserInfo(String username, String password) {
    return setUserInfo(username + ':' + password);
  }
  
  public URIBuilder setHost(String host) {
    this.host = host;
    this.schemeSpecificPart = null;
    this.authority = null;
    return this;
  }
  
  public URIBuilder setPort(int port) {
    this.port = (port < 0 ? -1 : port);
    this.schemeSpecificPart = null;
    this.authority = null;
    return this;
  }
  
  public URIBuilder setPath(String path) {
    this.path = path;
    this.schemeSpecificPart = null;
    return this;
  }
  
  public URIBuilder removeQuery() {
    this.queryParams = null;
    this.schemeSpecificPart = null;
    return this;
  }
  
  public URIBuilder setQuery(String query) {
    this.queryParams = parseQuery(query, Consts.UTF_8);
    this.schemeSpecificPart = null;
    return this;
  }
  
  public URIBuilder addParameter(String param, String value) {
    if (this.queryParams == null) {
      this.queryParams = new ArrayList();
    }
    this.queryParams.add(new BasicNameValuePair(param, value));
    this.schemeSpecificPart = null;
    return this;
  }
  
  public URIBuilder setParameter(String param, String value) {
    if (this.queryParams == null)
      this.queryParams = new ArrayList();
    Iterator<NameValuePair> it;
    if (!this.queryParams.isEmpty()) {
      for (it = this.queryParams.iterator(); it.hasNext();) {
        NameValuePair nvp = (NameValuePair)it.next();
        if (nvp.getName().equals(param)) {
          it.remove();
        }
      }
    }
    this.queryParams.add(new BasicNameValuePair(param, value));
    this.schemeSpecificPart = null;
    return this;
  }
  
  public URIBuilder setFragment(String fragment) {
    this.fragment = fragment;
    return this;
  }
  
  public String getScheme() {
    return this.scheme;
  }
  
  public String getUserInfo() {
    return this.userInfo;
  }
  
  public String getHost() {
    return this.host;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public String getPath() {
    return this.path;
  }
  
  public List<NameValuePair> getQueryParams() {
    if (this.queryParams != null) {
      return new ArrayList(this.queryParams);
    }
    return new ArrayList();
  }
  
  public String getFragment()
  {
    return this.fragment;
  }
  
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("URI [scheme=").append(this.scheme).append(", userInfo=").append(this.userInfo).append(", host=").append(this.host).append(", port=").append(this.port).append(", path=").append(this.path).append(", queryParams=").append(this.queryParams).append(", fragment=").append(this.fragment).append("]");
    



    return builder.toString();
  }
}
