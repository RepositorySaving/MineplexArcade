package org.apache.http.message;

import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.util.LangUtils;











































@NotThreadSafe
public class BasicHeaderElement
  implements HeaderElement, Cloneable
{
  private final String name;
  private final String value;
  private final NameValuePair[] parameters;
  
  public BasicHeaderElement(String name, String value, NameValuePair[] parameters)
  {
    if (name == null) {
      throw new IllegalArgumentException("Name may not be null");
    }
    this.name = name;
    this.value = value;
    if (parameters != null) {
      this.parameters = parameters;
    } else {
      this.parameters = new NameValuePair[0];
    }
  }
  





  public BasicHeaderElement(String name, String value)
  {
    this(name, value, null);
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public NameValuePair[] getParameters() {
    return (NameValuePair[])this.parameters.clone();
  }
  
  public int getParameterCount() {
    return this.parameters.length;
  }
  
  public NameValuePair getParameter(int index)
  {
    return this.parameters[index];
  }
  
  public NameValuePair getParameterByName(String name) {
    if (name == null) {
      throw new IllegalArgumentException("Name may not be null");
    }
    NameValuePair found = null;
    for (int i = 0; i < this.parameters.length; i++) {
      NameValuePair current = this.parameters[i];
      if (current.getName().equalsIgnoreCase(name)) {
        found = current;
        break;
      }
    }
    return found;
  }
  
  public boolean equals(Object object)
  {
    if (this == object) return true;
    if ((object instanceof HeaderElement)) {
      BasicHeaderElement that = (BasicHeaderElement)object;
      return (this.name.equals(that.name)) && (LangUtils.equals(this.value, that.value)) && (LangUtils.equals(this.parameters, that.parameters));
    }
    

    return false;
  }
  

  public int hashCode()
  {
    int hash = 17;
    hash = LangUtils.hashCode(hash, this.name);
    hash = LangUtils.hashCode(hash, this.value);
    for (int i = 0; i < this.parameters.length; i++) {
      hash = LangUtils.hashCode(hash, this.parameters[i]);
    }
    return hash;
  }
  
  public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    buffer.append(this.name);
    if (this.value != null) {
      buffer.append("=");
      buffer.append(this.value);
    }
    for (int i = 0; i < this.parameters.length; i++) {
      buffer.append("; ");
      buffer.append(this.parameters[i]);
    }
    return buffer.toString();
  }
  

  public Object clone()
    throws CloneNotSupportedException
  {
    return super.clone();
  }
}
