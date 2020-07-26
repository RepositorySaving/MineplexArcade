package org.apache.http.cookie;

import org.apache.http.params.HttpParams;

public abstract interface CookieSpecFactory
{
  public abstract CookieSpec newInstance(HttpParams paramHttpParams);
}
