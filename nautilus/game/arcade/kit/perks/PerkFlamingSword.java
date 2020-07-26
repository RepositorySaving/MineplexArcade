package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PerkFlamingSword extends Perk
{
  private HashMap<Player, Long> _active = new HashMap();
  



  public PerkFlamingSword()
  {
    super("Flaming Sword", new String[] {"Attacks ignite opponents for 4 seconds.", C.cYellow + "Hold Block" + C.cGray + " to use " + C.cGreen + "Inferno" });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void IgniteTarget(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    this.Manager.GetCondition().Factory().Ignite("Flaming Sword", event.GetDamageeEntity(), damager, 4.0D, false, false);
  }
  
  @EventHandler
  public void Activate(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (mineplex.core.common.util.UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SWORD")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, "Inferno", 4000L, true, true)) {
      return;
    }
    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
    
    mineplex.core.common.util.UtilPlayer.message(player, F.main("Skill", "You used " + F.skill("Inferno") + "."));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (this._active.containsKey(cur))
      {

        if (!cur.isBlocking())
        {
          this._active.remove(cur);


        }
        else if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._active.get(cur)).longValue(), 1500L))
        {
          this._active.remove(cur);

        }
        else
        {
          Item fire = cur.getWorld().dropItem(cur.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.FIRE));
          this.Manager.GetFire().Add(fire, cur, 0.7D, 0.0D, 0.5D, 1, "Inferno");
          
          fire.teleport(cur.getEyeLocation());
          double x = 0.07000000000000001D - UtilMath.r(14) / 100.0D;
          double y = 0.07000000000000001D - UtilMath.r(14) / 100.0D;
          double z = 0.07000000000000001D - UtilMath.r(14) / 100.0D;
          fire.setVelocity(cur.getLocation().getDirection().add(new Vector(x, y, z)).multiply(1.6D));
          

          cur.getWorld().playSound(cur.getLocation(), org.bukkit.Sound.GHAST_FIREBALL, 0.1F, 1.0F);
        }
      }
    }
  }
}
