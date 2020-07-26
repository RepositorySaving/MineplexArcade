package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem;

import java.util.HashSet;
import mineplex.core.common.util.F;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public class MagneticPull extends SkillActive
{
  private HashSet<Player> _active = new HashSet();
  










  public MagneticPull(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
  }
  

  public String GetEnergyString()
  {
    return "Energy: 16 per Second";
  }
  

  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    return true;
  }
  

  public void Skill(Player player, int level)
  {
    this._active.add(player);
    
    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.IRONGOLEM_DEATH, 0.6F, 0.6F);
  }
  

  @org.bukkit.event.EventHandler
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
          else if (!this.Factory.Energy().Use(cur, GetName(), 2.0D - level * 0.1D, true, true))
          {
            this._active.remove(cur);

          }
          else
          {
            cur.getWorld().playEffect(cur.getLocation(), org.bukkit.Effect.STEP_SOUND, 42);
            

            for (int i = 0; i <= 4 + 2 * level; i++)
              Pull(cur, cur.getEyeLocation().add(cur.getLocation().getDirection().multiply(i)));
          }
        } } }
  }
  
  public void Pull(Player player, Location loc) {
    for (Entity other : player.getWorld().getEntities())
    {
      if (((other instanceof LivingEntity)) || ((other instanceof Item)))
      {

        if (!player.equals(other))
        {

          if ((UtilMath.offset(player, other) >= 2.0D) && (UtilMath.offset(loc, other.getLocation()) <= 2.0D + getLevel(player) * 0.5D))
          {

            if ((!(other instanceof Player)) || 
            
              (this.Factory.Relation().CanHurt(player, (Player)other)))
            {


              UtilAction.velocity(other, UtilAlg.getTrajectory2d(other, player), 
                0.2D, false, 0.0D, 0.0D, 1.0D, true); } }
        }
      }
    }
  }
  
  public void Reset(Player player) {
    this._active.remove(player);
  }
}
