package org.apache.http.util;














public final class LangUtils
{
  public static final int HASH_SEED = 17;
  











  public static final int HASH_OFFSET = 37;
  












  public static int hashCode(int seed, int hashcode)
  {
    return seed * 37 + hashcode;
  }
  
  public static int hashCode(int seed, boolean b) {
    return hashCode(seed, b ? 1 : 0);
  }
  
  public static int hashCode(int seed, Object obj) {
    return hashCode(seed, obj != null ? obj.hashCode() : 0);
  }
  






  public static boolean equals(Object obj1, Object obj2)
  {
    return obj1 == null ? false : obj2 == null ? true : obj1.equals(obj2);
  }
  














  public static boolean equals(Object[] a1, Object[] a2)
  {
    if (a1 == null) {
      if (a2 == null) {
        return true;
      }
      return false;
    }
    
    if ((a2 != null) && (a1.length == a2.length)) {
      for (int i = 0; i < a1.length; i++) {
        if (!equals(a1[i], a2[i])) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
