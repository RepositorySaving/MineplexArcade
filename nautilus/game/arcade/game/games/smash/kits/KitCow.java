package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseCow;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;










public class KitCow
  extends SmashKit
{
  public KitCow(ArcadeManager manager)
  {
    super(manager, "Mad Cow", KitAvailability.Blue, new String[0], new Perk[] {new PerkSmashStats(7.0D, 1.0D, 0.25D, 8.0D), new PerkDoubleJump("Double Jump", 0.9D, 0.9D, false) }, EntityType.COW, new ItemStack(Material.MILK_BUCKET));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Stampede", 
      
      new String[] {
      ChatColor.RESET }) });
    


    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SPADE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Body Slam", 
      
      new String[] {
      ChatColor.RESET }) });
    

    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.IRON_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.IRON_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.IRON_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.DIAMOND_BOOTS));
    

    DisguiseCow disguise = new DisguiseCow(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
}
