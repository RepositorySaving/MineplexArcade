package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseChicken;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkChickenRocket;
import nautilus.game.arcade.kit.perks.PerkEggGun;
import nautilus.game.arcade.kit.perks.PerkFlap;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitChicken
  extends SmashKit
{
  public KitChicken(ArcadeManager manager)
  {
    super(manager, "Chicken", KitAvailability.Blue, new String[0], new Perk[] {new PerkSmashStats(4.0D, 2.0D, 0.1D, 1.5D), new PerkFlap(0.8D, 0.8D, false), new PerkEggGun(), new PerkChickenRocket() }, EntityType.CHICKEN, new ItemStack(Material.EGG));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, 0, 1, 
      C.cYellow + C.Bold + "Hold Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Egg Blaster", 
      
      new String[] {
      ChatColor.RESET + "Unleash a barrage of your precious eggs.", 
      ChatColor.RESET + "They won't deal any knockback, but if", 
      ChatColor.RESET + "they they can deal some serious damage." }) });
    

    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Chicken Missile", 
      
      new String[] {
      ChatColor.RESET + "Launch one of your newborn babies.", 
      ChatColor.RESET + "It will fly forwards and explode if it", 
      ChatColor.RESET + "collides with anything, giving large", 
      ChatColor.RESET + "damage and knockback to players." }) });
    

    if (this.Manager.GetGame().GetState() == Game.GameState.Recruit) {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.FEATHER, 0, 1, 
        C.cYellow + C.Bold + "Passive" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Flap", 
        
        new String[] {
        ChatColor.RESET + "You are able to use your double jump", 
        ChatColor.RESET + "up to 6 times in a row. However, with", 
        ChatColor.RESET + "each flap, it loses some potency.", 
        ChatColor.RESET, 
        ChatColor.RESET + C.cAqua + "Flap uses Energy (Experience Bar)" }) });
    }
    
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.LEATHER_CHESTPLATE));
    

    DisguiseChicken disguise = new DisguiseChicken(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
}
