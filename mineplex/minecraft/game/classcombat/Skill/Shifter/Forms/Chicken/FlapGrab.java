package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Chicken;

import java.util.HashMap;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;









public class FlapGrab
{
  public Flap Host;
  private HashMap<Player, LivingEntity> _clutch = new HashMap();
  
  public FlapGrab(Flap host)
  {
    this.Host = host;
  }
  
















  public void Grab(Player paramPlayer, LivingEntity paramLivingEntity)
  {
    throw new Error("Unresolved compilation problem: \n\tThe method SetIndicatorVisibility(LivingEntity, boolean) is undefined for the type ConditionManager\n");
  }
  























  public void Release(Player paramPlayer)
  {
    throw new Error("Unresolved compilation problem: \n\tThe method SetIndicatorVisibility(LivingEntity, boolean) is undefined for the type ConditionManager\n");
  }
  







  public void DamageRelease(CustomDamageEvent event)
  {
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    Release(damagee);
  }
  




  public void Reset(Player paramPlayer)
  {
    throw new Error("Unresolved compilation problem: \n\tThe method SetIndicatorVisibility(LivingEntity, boolean) is undefined for the type ConditionManager\n");
  }
}
