package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSlime;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSlimeRocket;
import nautilus.game.arcade.kit.perks.PerkSlimeSlam;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;











public class KitSlime
  extends SmashKit
{
  public KitSlime(ArcadeManager manager)
  {
    super(manager, "Slime", KitAvailability.Free, new String[0], new Perk[] {new PerkSmashStats(6.0D, 1.75D, 0.4D, 3.0D), new PerkDoubleJump("Double Jump", 1.2D, 1.0D, false), new PerkSlimeSlam(), new PerkSlimeRocket() }, EntityType.SLIME, new ItemStack(Material.SLIME_BALL));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, 0, 1, 
      C.cYellow + C.Bold + "Hold/Release Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Slime Rocket", 
      
      new String[] {
      ChatColor.RESET + "Slowly transfer your slimey goodness into", 
      ChatColor.RESET + "a new slime. When you release block, the", 
      ChatColor.RESET + "new slime is propelled forward.", 
      ChatColor.RESET, 
      ChatColor.RESET + "The more you charge the ability, the stronger", 
      ChatColor.RESET + "the new slime is projected forwards.", 
      ChatColor.RESET, 
      ChatColor.RESET + C.cAqua + "Slime Rocket uses Energy (Experience Bar)" }) });
    

    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Slime Slam", 
      
      new String[] {
      ChatColor.RESET + "Throw your slimey body forwards. If you hit", 
      ChatColor.RESET + "another player before you land, you deal", 
      ChatColor.RESET + "large damage and knockback to them.", 
      ChatColor.RESET, 
      ChatColor.RESET + "However, you take 50% of the damage and", 
      ChatColor.RESET + "knockback in the opposite direction." }) });
    

    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    

    DisguiseSlime disguise = new DisguiseSlime(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
    
    disguise.SetSize(3);
  }
}
