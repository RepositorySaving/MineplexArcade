package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseWolf;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import nautilus.game.arcade.kit.perks.PerkWolf;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;










public class KitWolf
  extends SmashKit
{
  public KitWolf(ArcadeManager manager)
  {
    super(manager, "Wolf", KitAvailability.Green, new String[0], new Perk[] {new PerkSmashStats(5.0D, 1.6D, 0.3D, 4.5D), new PerkDoubleJump("Wolf Jump", 1.0D, 1.0D, true), new PerkWolf() }, EntityType.WOLF, new ItemStack(Material.BONE));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Cub Tackle", 
      
      new String[] {
      ChatColor.RESET + "Launch a wolf cub at an opponent.", 
      ChatColor.RESET + "If it hits, the cub latches onto the", 
      ChatColor.RESET + "opponent, preventing them from moving", 
      ChatColor.RESET + "for up to 5 seconds." }) });
    

    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SPADE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Wolf Strike", 
      
      new String[] {
      ChatColor.RESET + "Leap forward with great power.", 
      ChatColor.RESET + "If you collide with an enemy, you deal", 
      ChatColor.RESET + "damage to them. If they are being tacked", 
      ChatColor.RESET + "by a cub, it deals 300% Knockback." }) });
    

    if (this.Manager.GetGame().GetState() == Game.GameState.Recruit) {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BONE, 0, 1, 
        C.cYellow + C.Bold + "Passive" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Ravage", 
        
        new String[] {
        ChatColor.RESET + "When you attack someone, you receive", 
        ChatColor.RESET + "+1 Damage for 3 seconds. Bonus damage", 
        ChatColor.RESET + "stacks from multiple hits.." }) });
    }
    
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    

    DisguiseWolf disguise = new DisguiseWolf(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
}
