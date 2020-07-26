package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PerkWeb extends Perk implements mineplex.core.projectile.IThrown
{
  private int _spawnRate;
  private int _max;
  
  public PerkWeb(int spawnRate, int max)
  {
    super("Bomber", new String[] {C.cGray + "Receive 1 Web every " + spawnRate + " seconds. Maximum of " + max + ".", C.cYellow + "Click" + C.cGray + " with Web to " + C.cGreen + "Throw Web" });
    

    this._spawnRate = spawnRate;
    this._max = max;
  }
  
  @EventHandler
  public void Spawn(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (this.Kit.HasKit(cur))
      {

        if (this.Manager.GetGame().IsAlive(cur))
        {

          if (Recharge.Instance.use(cur, GetName(), this._spawnRate * 1000, false, false))
          {

            if (!UtilInv.contains(cur, Material.WEB, (byte)0, this._max))
            {


              cur.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.WEB) }); } } }
      }
    }
  }
  
  @EventHandler
  public void Throw(PlayerInteractEvent event) {
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand().getType() == Material.WEB)
    {
      if ((event.getAction() == Action.LEFT_CLICK_AIR) || (event.getAction() == Action.LEFT_CLICK_BLOCK) || 
        (event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {}

    }
    else if (event.getPlayer().getItemInHand().getType().toString().contains("_AXE"))
    {
      if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {}

    }
    else
    {
      return;
    }
    
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    event.setCancelled(true);
    
    UtilInv.remove(player, Material.WEB, (byte)0, 1);
    UtilInv.Update(player);
    
    org.bukkit.entity.Item ent = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.WEB));
    mineplex.core.common.util.UtilAction.velocity(ent, player.getLocation().getDirection(), 0.8D, false, 0.0D, 0.2D, 10.0D, false);
    this.Manager.GetProjectile().AddThrow(ent, player, this, -1L, true, true, true, false, 1.0D);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target != null)
    {
      if ((target instanceof Player))
      {
        if (!this.Manager.GetGame().IsAlive((Player)target))
        {
          return;
        }
      }
    }
    
    Web(data);
  }
  

  public void Idle(ProjectileUser data)
  {
    Web(data);
  }
  

  public void Expire(ProjectileUser data)
  {
    Web(data);
  }
  
  public void Web(ProjectileUser data)
  {
    Location loc = data.GetThrown().getLocation();
    data.GetThrown().remove();
    
    this.Manager.GetBlockRestore().Add(loc.getBlock(), 30, (byte)0, 4000L);
  }
}
