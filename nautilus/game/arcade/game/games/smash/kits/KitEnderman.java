package nautilus.game.arcade.game.games.smash.kits;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseEnderman;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkBlink;
import nautilus.game.arcade.kit.perks.PerkBlockToss;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import nautilus.game.arcade.kit.perks.event.PerkBlockGrabEvent;
import nautilus.game.arcade.kit.perks.event.PerkBlockThrowEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class KitEnderman extends SmashKit
{
  public HashMap<Player, DisguiseEnderman> _disguises = new HashMap();
  














  public KitEnderman(ArcadeManager manager)
  {
    super(manager, "Enderman", nautilus.game.arcade.kit.KitAvailability.Green, new String[0], new nautilus.game.arcade.kit.Perk[] {new PerkSmashStats(7.0D, 1.3D, 0.25D, 6.0D), new PerkDoubleJump("Double Jump", 0.9D, 0.9D, false), new PerkBlink("Blink", 12.0D, 6000L), new PerkBlockToss() }, org.bukkit.entity.EntityType.ENDERMAN, new ItemStack(Material.ENDER_PEARL));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, 0, 1, 
      C.cYellow + C.Bold + "Hold/Release Block" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Block Toss", 
      
      new String[] {
      ChatColor.RESET + "Picks up a block from the ground, and", 
      ChatColor.RESET + "then hurls it at opponents, causing huge", 
      ChatColor.RESET + "damage and knockback if it hits.", 
      ChatColor.RESET, 
      ChatColor.RESET + "The longer you hold the block, the harder", 
      ChatColor.RESET + "you throw it. You will hear a 'tick' sound", 
      ChatColor.RESET + "when it is fully charged." }) });
    

    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Blink", 
      
      new String[] {
      ChatColor.RESET + "Instantly teleport in the direction", 
      ChatColor.RESET + "you are looking.", 
      ChatColor.RESET, 
      ChatColor.RESET + "You cannot pass through blocks." }) });
    

    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
    

    DisguiseEnderman disguise = new DisguiseEnderman(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    disguise.a(false);
    this.Manager.GetDisguise().disguise(disguise);
    
    this._disguises.put(player, disguise);
  }
  
  @EventHandler
  public void BlockGrab(PerkBlockGrabEvent event)
  {
    SetBlock((DisguiseEnderman)this._disguises.get(event.GetPlayer()), event.GetId(), event.GetData());
  }
  
  @EventHandler
  public void BlockThrow(PerkBlockThrowEvent event)
  {
    SetBlock((DisguiseEnderman)this._disguises.get(event.GetPlayer()), 0, (byte)0);
  }
  
  @EventHandler
  public void Death(PlayerDeathEvent event)
  {
    SetBlock((DisguiseEnderman)this._disguises.get(event.getEntity()), 0, (byte)0);
  }
  
  public void SetBlock(DisguiseEnderman disguise, int id, byte data)
  {
    if (disguise == null) {
      return;
    }
    disguise.SetCarriedId(id);
    disguise.SetCarriedData(data);
    
    this.Manager.GetDisguise().updateDisguise(disguise);
  }
  
  @EventHandler
  public void cleanDisguises(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    for (Iterator<Map.Entry<Player, DisguiseEnderman>> iterator = this._disguises.entrySet().iterator(); iterator.hasNext();)
    {
      Map.Entry<Player, DisguiseEnderman> current = (Map.Entry)iterator.next();
      
      if (!this.Manager.GetDisguise().isDisguised((LivingEntity)current.getKey()))
      {
        iterator.remove();
      }
      else if (this.Manager.GetDisguise().getDisguise((LivingEntity)current.getKey()) != current.getValue())
      {
        iterator.remove();
      }
    }
  }
  

  public int GetCost()
  {
    return 4000;
  }
}
