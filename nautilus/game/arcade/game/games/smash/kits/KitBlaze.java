package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseBlaze;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkFirefly;
import nautilus.game.arcade.kit.perks.PerkInferno;
import nautilus.game.arcade.kit.perks.PerkKnockbackFire;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import nautilus.game.arcade.kit.perks.PerkSpeed;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;











public class KitBlaze
  extends SmashKit
{
  public KitBlaze(ArcadeManager manager)
  {
    super(manager, "Blaze", KitAvailability.Blue, new String[0], new Perk[] {new PerkSmashStats(6.0D, 1.5D, 0.15D, 5.0D), new PerkDoubleJump("Double Jump", 1.0D, 1.0D, false), new PerkKnockbackFire(1.5D), new PerkSpeed(0), new PerkInferno(), new PerkFirefly() }, EntityType.BLAZE, new ItemStack(Material.BLAZE_ROD));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, 0, 1, 
      C.cYellow + C.Bold + "Hold Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Inferno", 
      
      new String[] {
      ChatColor.RESET + "Releases a deadly torrent of flames,", 
      ChatColor.RESET + "which ignite and damage opponents." }) });
    


    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Firefly", 
      
      new String[] {
      ChatColor.RESET + "After a short startup time, you fly", 
      ChatColor.RESET + "forward with great power, destroying", 
      ChatColor.RESET + "anyone you touch.", 
      ChatColor.RESET, 
      ChatColor.RESET + "If hit are hit by a projectile during", 
      ChatColor.RESET + "startup time, the skill is cancelled." }) });
    


    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
    

    DisguiseBlaze disguise = new DisguiseBlaze(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
  

  @EventHandler
  public void FireItemResist(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (HasKit(player))
      {

        this.Manager.GetCondition().Factory().FireItemImmunity(GetName(), player, player, 1.9D, false);
      }
    }
  }
  
  public int GetCost()
  {
    return 6000;
  }
}
