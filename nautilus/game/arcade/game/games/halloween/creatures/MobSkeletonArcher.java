package nautilus.game.arcade.game.games.halloween.creatures;

import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class MobSkeletonArcher extends CreatureBase<Skeleton> implements InterfaceMove
{
  public MobSkeletonArcher(Game game, Location loc)
  {
    super(game, null, Skeleton.class, loc);
  }
  

  public void SpawnCustom(Skeleton ent)
  {
    ent.getEquipment().setItemInHand(new ItemStack(Material.BOW));
    ent.setCustomName("Skeleton Archer");
    
    this.Host.Manager.GetCondition().Factory().Speed("Speed", ent, ent, 99999.0D, 0, false, false, false);
  }
  




  public void Damage(CustomDamageEvent event) {}
  



  public void Target(EntityTargetEvent event) {}
  



  public void Update(UpdateEvent event) {}
  



  public void Move()
  {
    if ((GetTarget() == null) || 
      (UtilMath.offset(((Skeleton)GetEntity()).getLocation(), GetTarget()) < 10.0D) || 
      (UtilMath.offset2d(((Skeleton)GetEntity()).getLocation(), GetTarget()) < 6.0D) || 
      (mineplex.core.common.util.UtilTime.elapsed(GetTargetTime(), 10000L)))
    {
      SetTarget(GetRoamTarget());
      return;
    }
    

    if (((Skeleton)GetEntity()).getTarget() != null)
    {
      if ((UtilMath.offset2d(GetEntity(), ((Skeleton)GetEntity()).getTarget()) > 10.0D) || (
        ((((Skeleton)GetEntity()).getTarget() instanceof Player)) && (this.Host.IsAlive((Player)((Skeleton)GetEntity()).getTarget()))))
      {
        ((Skeleton)GetEntity()).setTarget(null);
      }
      

    }
    else {
      DefaultMove();
    }
  }
}
