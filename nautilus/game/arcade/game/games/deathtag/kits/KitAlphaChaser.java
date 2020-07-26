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
import nautilus.game.arcade.kit.perks.PerkKnockbackMultiplier;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitAlphaChaser
  extends SmashKit
{
  public KitAlphaChaser(ArcadeManager manager)
  {
    super(manager, "Alpha Chaser", KitAvailability.Free, new String[0], new Perk[] {new PerkDamageSet(6.0D), new PerkKnockbackMultiplier(0.5D), new PerkIronSkin(4.0D) }, EntityType.SKELETON, new ItemStack(Material.IRON_AXE));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    

    DisguiseSkeleton disguise = new DisguiseSkeleton(player);
    disguise.SetName(C.cRed + player.getName());
    disguise.SetCustomNameVisible(true);
    disguise.hideArmor();
    disguise.SetSkeletonType(Skeleton.SkeletonType.WITHER);
    this.Manager.GetDisguise().disguise(disguise);
  }
}
