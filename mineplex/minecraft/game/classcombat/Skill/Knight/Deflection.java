package mineplex.minecraft.game.classcombat.Skill.Knight;

import java.util.HashMap;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Deflection
  extends Skill
{
  private HashMap<Player, Integer> _charges = new HashMap();
  
  public Deflection(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Prepare to deflect incoming attacks.", 
      "You gain 1 Charge every #5#-1 seconds.", 
      "You can store a maximum of #2#1 Charges.", 
      "", 
      "When you are attacked, the damage is", 
      "reduced by the number of your Charges,", 
      "and your Charges are reset to 0." });
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void DecreaseDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this._charges.containsKey(damagee)) {
      return;
    }
    event.AddMod(damagee.getName(), GetName(), -((Integer)this._charges.remove(damagee)).intValue(), false);
    

    int level = getLevel(damagee);
    Recharge.Instance.useForce(damagee, GetName(), 5000 - 1000 * level);
  }
  
  @EventHandler
  public void Charge(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      int level = getLevel(cur);
      
      if (Recharge.Instance.use(cur, GetName(), 5000 - 1000 * level, false, false))
      {

        int charge = 1;
        if (this._charges.containsKey(cur)) {
          charge += ((Integer)this._charges.get(cur)).intValue();
        }
        charge = Math.min(2 + 1 * level, charge);
        
        this._charges.put(cur, Integer.valueOf(charge));
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._charges.remove(player);
  }
}
