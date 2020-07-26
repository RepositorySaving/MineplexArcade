package mineplex.core.common.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class NautHashMap<KeyType, ValueType>
{
  private HashMap<KeyType, ValueType> _wrappedHashMap = new HashMap();
  
  public boolean containsKey(KeyType key)
  {
    return this._wrappedHashMap.containsKey(key);
  }
  
  public boolean containsValue(ValueType key)
  {
    return this._wrappedHashMap.containsValue(key);
  }
  
  public Set<Map.Entry<KeyType, ValueType>> entrySet()
  {
    return this._wrappedHashMap.entrySet();
  }
  
  public Set<KeyType> keySet()
  {
    return this._wrappedHashMap.keySet();
  }
  
  public Collection<ValueType> values()
  {
    return this._wrappedHashMap.values();
  }
  
  public ValueType get(KeyType key)
  {
    return this._wrappedHashMap.get(key);
  }
  
  public ValueType remove(KeyType key)
  {
    return this._wrappedHashMap.remove(key);
  }
  
  public ValueType put(KeyType key, ValueType value)
  {
    return this._wrappedHashMap.put(key, value);
  }
  
  public void clear()
  {
    this._wrappedHashMap.clear();
  }
  
  public int size()
  {
    return this._wrappedHashMap.size();
  }
  
  public boolean isEmpty()
  {
    return this._wrappedHashMap.isEmpty();
  }
}
