package mineplex.core.punish;

public enum Category
{
  ChatOffense, 
  Advertisement, 
  Exploiting, 
  Hacking, 
  Warning, 
  PermMute, 
  Other;
  
  public static boolean contains(String s)
  {
    try
    {
      valueOf(s);
      return true;
    }
    catch (Exception e) {}
    
    return false;
  }
}
