package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;

public class Propel extends SkillActive
{
  private HashMap<Player, Long> _active = new HashMap();
  










  public Propel(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "" });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if (!player.getLocation().getBlock().isLiquid())
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " out of water."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    UtilAction.velocity(player, 0.6D + 0.2D * level, 0.2D, 2.0D, false);
    

    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
    

    player.getWorld().playSound(player.getLocation(), Sound.SPLASH2, 1.5F, 1.5F);
  }
  
  @EventHandler
  public void Reuse(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.containsKey(cur))
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
          else if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._active.get(cur)).longValue(), 400L))
          {

            if (cur.getLocation().getBlock().isLiquid())
            {


              UtilAction.velocity(cur, 0.3D + 0.1D * level, 0.1D, 2.0D, false);
              

              this._active.put(cur, Long.valueOf(System.currentTimeMillis()));
              

              cur.getWorld().playSound(cur.getLocation(), Sound.SPLASH2, 0.5F, 1.0F);
            } }
        } }
    }
  }
  
  public void Reset(Player player) {
    this._active.remove(player);
  }
}
