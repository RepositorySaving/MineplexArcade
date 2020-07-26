package nautilus.game.arcade.addons;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class CompassAddon extends MiniPlugin
{
  public ArcadeManager Manager;
  
  public CompassAddon(JavaPlugin plugin, ArcadeManager manager)
  {
    super("Compass Addon", plugin);
    
    this.Manager = manager;
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.SEC) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    if (!this.Manager.GetGame().IsLive()) {
      return;
    }
    for (Player player : mineplex.core.common.util.UtilServer.getPlayers())
    {
      if ((this.Manager.GetGame().CompassEnabled) || (!this.Manager.GetGame().IsAlive(player)))
      {

        GameTeam team = this.Manager.GetGame().GetTeam(player);
        
        Player target = null;
        GameTeam targetTeam = null;
        double bestDist = 0.0D;
        
        for (Player other : this.Manager.GetGame().GetPlayers(true))
        {
          if (!other.equals(player))
          {

            GameTeam otherTeam = this.Manager.GetGame().GetTeam(other);
            

            if ((this.Manager.GetGame().GetTeamList().size() <= 1) || (team == null) || (!team.equals(otherTeam)) || (!this.Manager.GetGame().IsAlive(player)))
            {

              double dist = UtilMath.offset(player, other);
              
              if ((target == null) || (dist < bestDist))
              {
                target = other;
                targetTeam = otherTeam;
                bestDist = dist;
              }
            }
          } }
        if (target != null)
        {
          if (!player.getInventory().contains(Material.COMPASS)) {
            player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COMPASS) });
          }
          player.setCompassTarget(target.getLocation());
          
          for (??? = player.getInventory().all(Material.COMPASS).keySet().iterator(); ???.hasNext();) { int i = ((Integer)???.next()).intValue();
            
            ItemStack stack = player.getInventory().getItem(i);
            
            double heightDiff = target.getLocation().getY() - player.getLocation().getY();
            
            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(
              "    " + C.cWhite + C.Bold + "Nearest Player: " + targetTeam.GetColor() + target.getName() + 
              "    " + C.cWhite + C.Bold + "Distance: " + targetTeam.GetColor() + UtilMath.trim(1, bestDist) + 
              "    " + C.cWhite + C.Bold + "Height: " + targetTeam.GetColor() + UtilMath.trim(1, heightDiff));
            stack.setItemMeta(itemMeta);
            
            player.getInventory().setItem(i, stack);
          }
        }
      }
    }
  }
  
  @EventHandler
  public void DropItem(PlayerDropItemEvent event) {
    if ((this.Manager.GetGame() == null) || (!this.Manager.GetGame().CompassEnabled)) {
      return;
    }
    if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.COMPASS, (byte)0)) {
      return;
    }
    
    event.setCancelled(true);
    

    mineplex.core.common.util.UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot drop " + F.item("Target Compass") + "."));
  }
  
  @EventHandler
  public void DeathRemove(PlayerDeathEvent event)
  {
    if ((this.Manager.GetGame() == null) || (!this.Manager.GetGame().CompassEnabled)) {
      return;
    }
    HashSet<ItemStack> remove = new HashSet();
    
    for (ItemStack item : event.getDrops()) {
      if (UtilInv.IsItem(item, Material.COMPASS, (byte)0))
        remove.add(item);
    }
    for (ItemStack item : remove) {
      event.getDrops().remove(item);
    }
  }
  

  @EventHandler
  public void InventoryClick(InventoryClickEvent event) {}
  

  @EventHandler
  public void PlayerInteract(PlayerInteractEvent event)
  {
    if ((this.Manager.GetGame() == null) || (!this.Manager.GetGame().CompassEnabled)) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!mineplex.core.common.util.UtilGear.isMat(player.getItemInHand(), Material.COMPASS)) {
      return;
    }
    if (this.Manager.GetGame().IsAlive(player)) {
      return;
    }
    GameTeam team = this.Manager.GetGame().GetTeam(player);
    
    Player target = null;
    double bestDist = 0.0D;
    
    for (Player other : this.Manager.GetGame().GetPlayers(true))
    {
      GameTeam otherTeam = this.Manager.GetGame().GetTeam(other);
      

      if ((this.Manager.GetGame().GetTeamList().size() <= 1) || (team == null) || (!team.equals(otherTeam)) || (!this.Manager.GetGame().IsAlive(player)))
      {

        double dist = UtilMath.offset(player, other);
        
        if ((target == null) || (dist < bestDist))
        {
          target = other;
          bestDist = dist;
        }
      }
    }
    if (target != null)
    {
      player.teleport(target.getLocation().add(0.0D, 1.0D, 0.0D));
    }
  }
}
