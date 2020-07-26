package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashSet;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.IRelation;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public class Magnetize extends SkillActive
{
  private HashSet<Player> _active = new HashSet();
  










  public Magnetize(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "SKILL NEEDS REPLACING" });
  }
  


  public String GetEnergyString()
  {
    return "Energy: 16 per Second";
  }
  

  public boolean CustomCheck(Player player, int level)
  {
    UtilPlayer.message(player, mineplex.core.common.util.F.main("Skill", "This skill is currently being re-worked."));
    return false;
  }
  

  public void Skill(Player player, int level)
  {
    this._active.add(player);
  }
  

  @EventHandler
  public void Energy(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {

        if (!cur.isBlocking())
        {
          this._active.remove(cur);

        }
        else
        {
          int level = getLevel(cur);
          if (level == 0)
          {
            this._active.remove(cur);


          }
          else if (!this.Factory.Energy().Use(cur, GetName(), 2.0D, true, true))
          {
            this._active.remove(cur);

          }
          else
          {
            cur.getWorld().playEffect(cur.getLocation(), Effect.STEP_SOUND, 42);
            

            for (int i = 0; i <= 5 + level; i++)
              Pull(cur, cur.getEyeLocation().add(cur.getLocation().getDirection().multiply(i)));
          }
        } } }
  }
  
  public void Pull(Player player, Location loc) {
    for (Player other : UtilPlayer.getNearby(loc, 2.0D))
    {
      if (!player.equals(other))
      {

        if (this.Factory.Relation().CanHurt(player, other))
        {

          if (UtilMath.offset(player, other) >= 2.0D)
          {

            UtilAction.velocity(other, UtilAlg.getTrajectory2d(other, player), 
              0.2D, false, 0.0D, 0.0D, 1.0D, false); }
        }
      }
    }
  }
  
  public void Reset(Player player) {
    this._active.remove(player);
  }
}
