package mineplex.minecraft.game.classcombat.item.repository;

import java.util.List;
import mineplex.core.server.remotecall.JsonWebCall;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;



public class ItemRepository
{
  private String _webAddress;
  
  public ItemRepository(String webAddress)
  {
    this._webAddress = webAddress;
  }
  
  public List<ItemToken> GetItems(List<ItemToken> items)
  {
    (List)new JsonWebCall(this._webAddress + "Dominate/GetItems").Execute(new TypeToken() {}.getType(), items);
  }
}
