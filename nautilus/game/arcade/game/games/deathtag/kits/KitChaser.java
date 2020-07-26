package nautilus.game.arcade.game.games.deathtag.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSkeleton;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDamageSet;
import nautilus.game.arcade.kit.perks.PerkIronSkin;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;











public class KitChaser
  extends SmashKit
{
  public KitChaser(ArcadeManager manager)
  {
    super(manager, "Chaser", KitAvailability.Hide, new String[0], new Perk[] {new PerkDamageSet(4.0D), new PerkIronSkin(2.0D) }, EntityType.SKELETON, new ItemStack(Material.IRON_AXE));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    

    DisguiseSkeleton disguise = new DisguiseSkeleton(player);
    disguise.SetName(C.cRed + player.getName());
    disguise.SetCustomNameVisible(true);
    disguise.hideArmor();
    this.Manager.GetDisguise().disguise(disguise);
  }
}
