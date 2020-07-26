package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkNeedler extends Perk
{
  private HashMap<Player, Integer> _active = new HashMap();
  private HashSet<Arrow> _arrows = new HashSet();
  


  public PerkNeedler()
  {
    super("Needler", new String[] {C.cYellow + "Hold Block" + C.cGray + " to use " + C.cGreen + "Needler" });
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
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SWORD")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 1800L, true, true)) {
      return;
    }
    this._active.put(player, Integer.valueOf(8));
    
    mineplex.core.common.util.UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : mineplex.core.common.util.UtilServer.getPlayers())
    {
      if (this._active.containsKey(cur))
      {

        if (!cur.isBlocking())
        {
          this._active.remove(cur);
        }
        else
        {
          int count = ((Integer)this._active.get(cur)).intValue() - 1;
          

          if (count <= 0)
          {
            this._active.remove(cur);

          }
          else
          {
            this._active.put(cur, Integer.valueOf(count));
            

            Arrow arrow = cur.getWorld().spawnArrow(cur.getEyeLocation().add(cur.getLocation().getDirection()), 
              cur.getLocation().getDirection(), 1.2F, 6.0F);
            arrow.setShooter(cur);
            this._arrows.add(arrow);
            

            cur.getWorld().playSound(cur.getLocation(), org.bukkit.Sound.SPIDER_IDLE, 0.8F, 2.0F);
          }
        } } }
  }
  
  @EventHandler
  public void ArrowDamamge(CustomDamageEvent event) {
    if (event.GetProjectile() == null) {
      return;
    }
    if (event.GetDamagerPlayer(true) == null) {
      return;
    }
    if (!(event.GetProjectile() instanceof Arrow)) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    event.SetCancelled("Needler Cancel");
    
    event.GetProjectile().remove();
    

    this.Manager.GetDamage().NewDamageEvent(event.GetDamageeEntity(), damager, null, 
      org.bukkit.event.entity.EntityDamageEvent.DamageCause.THORNS, 1.1D, true, true, false, 
      damager.getName(), GetName());
    
    this.Manager.GetCondition().Factory().Poison(GetName(), event.GetDamageeEntity(), damager, 2.0D, 0, false, false, false);
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Arrow> arrowIterator = this._arrows.iterator(); arrowIterator.hasNext();)
    {
      Arrow arrow = (Arrow)arrowIterator.next();
      
      if ((arrow.isOnGround()) || (!arrow.isValid()) || (arrow.getTicksLived() > 300))
      {
        arrowIterator.remove();
        arrow.remove();
      }
    }
  }
}
