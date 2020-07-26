package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PerkTNTArrow extends Perk
{
  private HashSet<Player> _active = new HashSet();
  private HashMap<Entity, Player> _tntMap = new HashMap();
  


  public PerkTNTArrow()
  {
    super("Explosive Arrow", new String[] {C.cYellow + "Left-Click" + C.cGray + " with Bow to prepare " + C.cGreen + "Explosive Arrow" });
  }
  

  @EventHandler
  public void Fire(PlayerInteractEvent event)
  {
    if (!this.Manager.GetGame().IsLive()) {
      return;
    }
    if ((event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand().getType() != Material.BOW) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    event.setCancelled(true);
    
    if (!player.getInventory().contains(Material.TNT))
    {
      UtilPlayer.message(player, F.main("Game", "You have no " + F.item("TNT") + "."));
      return;
    }
    
    if (this._active.contains(player)) {
      return;
    }
    
    UtilInv.remove(player, Material.TNT, (byte)0, 1);
    UtilInv.Update(player);
    

    this._active.add(player);
    

    player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 2.5F, 2.0F);
    

    UtilPlayer.message(player, F.main("Game", "You prepared " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void ShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!(event.getProjectile() instanceof org.bukkit.entity.Arrow)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this._active.remove(player)) {
      return;
    }
    
    UtilPlayer.message(player, F.main("Game", "You fired " + F.skill(GetName()) + "."));
    

    TNTPrimed tnt = (TNTPrimed)player.getWorld().spawn(player.getLocation(), TNTPrimed.class);
    ((CraftTNTPrimed)tnt).getHandle().spectating = true;
    event.getProjectile().setPassenger(tnt);
    this._tntMap.put(tnt, player);
  }
}
