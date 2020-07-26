package nautilus.game.arcade.game.games.evolution.mobs;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseCreeper;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkExplode;
import nautilus.game.arcade.kit.perks.PerkFood;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;








public class KitCreeper
  extends Kit
{
  public KitCreeper(ArcadeManager manager)
  {
    super(manager, "Creeper", KitAvailability.Hide, new String[] {"" }, new Perk[] {new PerkFood(1), new PerkExplode("Detonate", 1.0D, 6000L) }, EntityType.SLIME, null);
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.TNT) });
    
    UtilPlayer.message(player, C.Line);
    UtilPlayer.message(player, C.Bold + "You evolved into " + F.elem(new StringBuilder(String.valueOf(C.cGreen)).append(C.Bold).append(GetName()).toString()) + "!");
    UtilPlayer.message(player, F.elem("Attack") + " to use " + F.elem("Detonate"));
    UtilPlayer.message(player, C.Line);
    
    player.getWorld().playSound(player.getLocation(), Sound.CREEPER_HISS, 4.0F, 1.0F);
    

    DisguiseCreeper disguise = new DisguiseCreeper(player);
    disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
}
