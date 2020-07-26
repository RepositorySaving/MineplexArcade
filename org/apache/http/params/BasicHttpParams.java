package org.apache.http.params;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.http.annotation.NotThreadSafe;






































@NotThreadSafe
public class BasicHttpParams
  extends AbstractHttpParams
  implements Serializable, Cloneable
{
  private static final long serialVersionUID = -7086398485908701455L;
  private final HashMap<String, Object> parameters = new HashMap();
  



  public Object getParameter(String name)
  {
    return this.parameters.get(name);
  }
  
  public HttpParams setParameter(String name, Object value) {
    this.parameters.put(name, value);
    return this;
  }
  
  public boolean removeParameter(String name)
  {
    if (this.parameters.containsKey(name)) {
      this.parameters.remove(name);
      return true;
    }
    return false;
  }
  






  public void setParameters(String[] names, Object value)
  {
    for (int i = 0; i < names.length; i++) {
      setParameter(names[i], value);
    }
  }
  










  public boolean isParameterSet(String name)
  {
    return getParameter(name) != null;
  }
  









  public boolean isParameterSetLocally(String name)
  {
    return this.parameters.get(name) != null;
  }
  


  public void clear()
  {
    this.parameters.clear();
  }
  








  @Deprecated
  public HttpParams copy()
  {
    try
    {
      return (HttpParams)clone();
    } catch (CloneNotSupportedException ex) {
      throw new UnsupportedOperationException("Cloning not supported");
    }
  }
  



  public Object clone()
    throws CloneNotSupportedException
  {
    BasicHttpParams clone = (BasicHttpParams)super.clone();
    copyParams(clone);
    return clone;
  }
  






  public void copyParams(HttpParams target)
  {
    Iterator<Map.Entry<String, Object>> iter = this.parameters.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, Object> me = (Map.Entry)iter.next();
      if ((me.getKey() instanceof String)) {
        target.setParameter((String)me.getKey(), me.getValue());
      }
    }
  }
  








  public Set<String> getNames()
  {
    return new HashSet(this.parameters.keySet());
  }
}
