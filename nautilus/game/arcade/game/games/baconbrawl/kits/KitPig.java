package nautilus.game.arcade.game.games.baconbrawl.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguisePig;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkBodySlam;
import nautilus.game.arcade.kit.perks.PerkJump;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitPig
  extends Kit
{
  public KitPig(ArcadeManager manager)
  {
    super(manager, "El Muchacho Pigo", KitAvailability.Free, new String[] {"Such a fat pig. Oink." }, new Perk[] {new PerkBodySlam(6, 2.0D), new PerkJump(1) }, EntityType.PIG, new ItemStack(Material.PORK));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    

    DisguisePig disguise = new DisguisePig(player);
    disguise.SetName(C.cYellow + player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
}
