package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSpider;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkNeedler;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import nautilus.game.arcade.kit.perks.PerkSpiderLeap;
import nautilus.game.arcade.kit.perks.PerkWebShot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;











public class KitSpider
  extends SmashKit
{
  public KitSpider(ArcadeManager manager)
  {
    super(manager, "Spider", KitAvailability.Free, new String[0], new Perk[] {new PerkSmashStats(6.0D, 1.75D, 0.25D, 5.5D), new PerkSpiderLeap(), new PerkNeedler(), new PerkWebShot() }, EntityType.SPIDER, new ItemStack(Material.WEB));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, 0, 1, 
      C.cYellow + C.Bold + "Hold/Release Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Needler", 
      
      new String[] {
      ChatColor.RESET + "Quickly spray up to 5 needles from ", 
      ChatColor.RESET + "your mouth, dealing damage and small", 
      ChatColor.RESET + "knockback to opponents." }) });
    

    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Web Shot", 
      
      new String[] {
      ChatColor.RESET + "Launch a web forwards. Upon collision,", 
      ChatColor.RESET + "it creates a temporary web that traps.", 
      ChatColor.RESET + "opponents." }) });
    

    if (this.Manager.GetGame().GetState() == Game.GameState.Recruit) {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.SPIDER_EYE, 0, 1, 
        C.cYellow + C.Bold + "Double Jump" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Spider Leap", 
        
        new String[] {
        ChatColor.RESET + "Your double jump is special. It goes", 
        ChatColor.RESET + "exactly in the direction you are looking.", 
        ChatColor.RESET, 
        ChatColor.RESET + C.cAqua + "Spider Leap uses Energy (Experience Bar)" }) });
    }
    
    if (this.Manager.GetGame().GetState() == Game.GameState.Recruit) {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.FERMENTED_SPIDER_EYE, 0, 1, 
        C.cYellow + C.Bold + "Crouch" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Wall Grab", 
        
        new String[] {
        ChatColor.RESET + "While crouching, you stick to walls.", 
        ChatColor.RESET, 
        ChatColor.RESET + "Grasping onto a wall allows you to", 
        ChatColor.RESET + "use Spider Leap again.", 
        ChatColor.RESET, 
        ChatColor.RESET + C.cAqua + "Wall Grab uses Energy (Experience Bar)" }) });
    }
    
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.LEATHER_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
    

    DisguiseSpider disguise = new DisguiseSpider(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
  

  public int GetCost()
  {
    return 2000;
  }
}
