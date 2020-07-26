package mineplex.minecraft.game.classcombat.Skill;

import java.util.WeakHashMap;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public abstract class SkillChargeSword
  extends SkillCharge implements Listener
{
  protected boolean _canChargeInWater;
  protected boolean _canChargeInAir;
  
  public SkillChargeSword(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int maxLevel, float base, float boost, boolean inWater, boolean inAir)
  {
    super(skills, name, classType, skillType, cost, maxLevel, base, boost);
    
    this._canChargeInWater = inWater;
    this._canChargeInAir = inAir;
  }
  
  @EventHandler
  public void ChargeBlock(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {

      if (cur.isBlocking())
      {

        if ((this._canChargeInAir) || (UtilEnt.isGrounded(cur)))
        {

          if ((this._canChargeInWater) || (!cur.getLocation().getBlock().isLiquid()))
          {


            if ((this._charge.containsKey(cur)) || 
              (Recharge.Instance.use(cur, GetName(), 2000L, false, false)))
            {


              Charge(cur); }
          }
        }
      } else if (this._charge.containsKey(cur))
      {

        float charge = ((Float)this._charge.remove(cur)).floatValue();
        
        DoSkill(cur, charge);
      }
    }
  }
  
  public void DoSkill(Player player, float charge)
  {
    player.setExp(0.0F);
    
    DoSkillCustom(player, charge);
  }
  

  public abstract void DoSkillCustom(Player paramPlayer, float paramFloat);
  
  public void Reset(Player player)
  {
    this._charge.remove(player);
  }
}
