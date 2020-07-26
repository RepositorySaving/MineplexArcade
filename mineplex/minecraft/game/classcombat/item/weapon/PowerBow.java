package mineplex.minecraft.game.classcombat.item.weapon;

import mineplex.minecraft.game.classcombat.item.Item;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import org.bukkit.Material;

public class PowerBow
  extends Item
{
  public PowerBow(ItemFactory factory, int gemCost, int tokenCost)
  {
    super(factory, "Power Bow", new String[] { "Increases Bow damage by 1." }, Material.BOW, 1, true, gemCost, tokenCost);
  }
}
