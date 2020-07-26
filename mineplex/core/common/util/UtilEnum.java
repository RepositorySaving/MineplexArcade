package mineplex.core.common.util;

public class UtilEnum
{
  public static <T extends Enum<T>> T fromString(Class<T> t, String text)
  {
    for (T value : (Enum[])t.getEnumConstants())
    {
      if (text.equalsIgnoreCase(value.name()))
      {
        return value;
      }
    }
    
    throw new IllegalArgumentException("There is no value with name '" + text + " in Enum " + t.getClass().getName());
  }
}
