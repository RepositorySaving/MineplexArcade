package com.google.gson.stream;






















final class StringPool
{
  private final String[] pool = new String[512];
  



  public String get(char[] array, int start, int length)
  {
    int hashCode = 0;
    for (int i = start; i < start + length; i++) {
      hashCode = hashCode * 31 + array[i];
    }
    

    hashCode ^= hashCode >>> 20 ^ hashCode >>> 12;
    hashCode ^= hashCode >>> 7 ^ hashCode >>> 4;
    int index = hashCode & this.pool.length - 1;
    
    String pooled = this.pool[index];
    if ((pooled == null) || (pooled.length() != length)) {
      String result = new String(array, start, length);
      this.pool[index] = result;
      return result;
    }
    
    for (int i = 0; i < length; i++) {
      if (pooled.charAt(i) != array[(start + i)]) {
        String result = new String(array, start, length);
        this.pool[index] = result;
        return result;
      }
    }
    
    return pooled;
  }
}
