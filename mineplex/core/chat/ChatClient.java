package mineplex.core.chat;

public class ChatClient
{
  private boolean _filterChat = false;
  
  public void SetFilterChat(boolean filterChat)
  {
    this._filterChat = filterChat;
  }
  
  public boolean GetFilterChat()
  {
    return this._filterChat;
  }
}
