package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSnowman;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkArcticAura;
import nautilus.game.arcade.kit.perks.PerkBlizzard;
import nautilus.game.arcade.kit.perks.PerkDamageSnow;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkIcePath;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;













public class KitSnowman
  extends SmashKit
{
  public KitSnowman(ArcadeManager manager)
  {
    super(manager, "Snowman", KitAvailability.Green, new String[0], new Perk[] {new PerkSmashStats(5.0D, 1.4D, 0.4D, 6.0D), new PerkDoubleJump("Double Jump", 0.9D, 0.9D, false), new PerkDamageSnow(2, 1.25D), new PerkArcticAura(), new PerkBlizzard(), new PerkIcePath() }, EntityType.SNOWMAN, new ItemStack(Material.SNOW_BALL));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, 0, 1, 
      C.cYellow + C.Bold + "Hold Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Blizzard", 
      
      new String[] {
      ChatColor.RESET + "Release a windy torrent of snow, able", 
      ChatColor.RESET + "to blow opponents off the stage.", 
      ChatColor.RESET, 
      ChatColor.RESET + C.cAqua + "Blizzard uses Energy (Experience Bar)" }) });
    

    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Ice Path", 
      
      new String[] {
      ChatColor.RESET + "Create a temporary icy path in the", 
      ChatColor.RESET + "direction you are looking." }) });
    

    if (this.Manager.GetGame().GetState() == Game.GameState.Recruit) {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.SNOW_BLOCK, 0, 1, 
        C.cYellow + C.Bold + "Passive" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Arctic Aura", 
        
        new String[] {
        ChatColor.RESET + "Creates a field of snow around you", 
        ChatColor.RESET + "granting 150% damage to opponents", 
        ChatColor.RESET + "who are standing on it.", 
        ChatColor.RESET, 
        ChatColor.RESET + "Your aura shrinks on low energy." }) });
    }
    
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
    

    DisguiseSnowman disguise = new DisguiseSnowman(player);
    
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
    return 5000;
  }
}
