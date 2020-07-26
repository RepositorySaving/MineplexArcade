package org.apache.http;

public abstract interface Header
{
  public abstract String getName();
  
  public abstract String getValue();
  
  public abstract HeaderElement[] getElements()
    throws ParseException;
}
