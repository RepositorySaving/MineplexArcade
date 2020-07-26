package nautilus.game.arcade.game.games.zombiesurvival.kits;

import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSkeleton;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkIronSkin;
import nautilus.game.arcade.kit.perks.PerkLeap;
import nautilus.game.arcade.kit.perks.PerkRegeneration;
import nautilus.game.arcade.kit.perks.PerkStrength;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;









public class KitUndeadAlpha
  extends Kit
{
  public KitUndeadAlpha(ArcadeManager manager)
  {
    super(manager, "Alpha Undead", KitAvailability.Free, new String[] {"Leap at those undead" }, new Perk[] {new PerkLeap("Leap", 1.0D, 1.0D, 8000L), new PerkStrength(2), new PerkIronSkin(2.0D), new PerkRegeneration(1) }, EntityType.SKELETON, new ItemStack(Material.STONE_AXE));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.STONE_AXE) });
    
    DisguiseSkeleton disguise = new DisguiseSkeleton(player);
    disguise.SetSkeletonType(Skeleton.SkeletonType.WITHER);
    disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    if ((ent instanceof Skeleton))
    {
      Skeleton skel = (Skeleton)ent;
      skel.setSkeletonType(Skeleton.SkeletonType.WITHER);
    }
  }
}
