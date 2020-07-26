package mineplex.minecraft.game.classcombat.item.weapon;

import mineplex.minecraft.game.classcombat.item.Item;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import org.bukkit.Material;

public class StandardBow
  extends Item
{
  public StandardBow(ItemFactory factory, int gemCost, int tokenCost)
  {
    super(factory, "Standard Bow", new String[] { "Pretty standard." }, Material.BOW, 1, true, gemCost, tokenCost);
    
    setFree(true);
  }
}
