package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseMagmaCube;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkFlameDash;
import nautilus.game.arcade.kit.perks.PerkMagmaBlast;
import nautilus.game.arcade.kit.perks.PerkMagmaBoost;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;










public class KitMagmaCube
  extends SmashKit
{
  public KitMagmaCube(ArcadeManager manager)
  {
    super(manager, "Magma Cube", KitAvailability.Blue, new String[0], new Perk[] {new PerkSmashStats(5.0D, 1.75D, 0.4D, 3.0D), new PerkDoubleJump("Double Jump", 1.2D, 1.0D, false), new PerkMagmaBoost(), new PerkMagmaBlast(), new PerkFlameDash() }, EntityType.MAGMA_CUBE, new ItemStack(Material.MAGMA_CREAM));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Hold/Release Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Magma Blast", 
      
      new String[] {
      ChatColor.RESET + "Release a powerful ball of magma which explodes", 
      ChatColor.RESET + "on impact, dealing damage and knockback.", 
      ChatColor.RESET, 
      ChatColor.RESET + "You receive strong knockback when you shoot it.", 
      ChatColor.RESET + "Use this knockback to get back onto the map!" }) });
    

    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SPADE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Flame Dash", 
      
      new String[] {
      ChatColor.RESET + "Disappear in flames, and fly horizontally", 
      ChatColor.RESET + "in the direction you are looking. You explode", 
      ChatColor.RESET + "when you re-appear, dealing damage to enemies.", 
      ChatColor.RESET, 
      ChatColor.RESET + "Damage increases with distance travelled.", 
      ChatColor.RESET, 
      ChatColor.RESET + "Right-Click again to end Flame Dash early." }) });
    

    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    

    DisguiseMagmaCube disguise = new DisguiseMagmaCube(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
    
    disguise.SetSize(1);
  }
}
