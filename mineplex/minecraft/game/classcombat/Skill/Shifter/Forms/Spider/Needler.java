package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.energy.Energy;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;


public class Needler
  extends SkillActive
{
  private HashMap<Player, Integer> _stored = new HashMap();
  private HashMap<Player, Long> _fired = new HashMap();
  
  private HashSet<Arrow> _arrows = new HashSet();
  
  private boolean _tick = false;
  










  public Needler(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
  }
  

  public boolean CustomCheck(Player player, int level)
  {
    return true;
  }
  


  public void Skill(Player player, int level) {}
  


  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    this._tick = (!this._tick);
    
    if (this._tick) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (cur.isBlocking())
      {


        int level = getLevel(cur);
        if (level != 0)
        {

          if (cur.getLocation().getBlock().isLiquid())
          {
            UtilPlayer.message(cur, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in liquids."));



          }
          else if (UseCharge(cur))
          {


            this.Factory.Energy().Use(cur, GetName(), 3.0D - 0.2D * level, true, false);
            
            Arrow arrow = cur.getWorld().spawnArrow(cur.getEyeLocation().add(cur.getLocation().getDirection()), 
              cur.getLocation().getDirection(), 1.6F + level * 0.4F, 2.0F);
            arrow.setShooter(cur);
            this._arrows.add(arrow);
            

            this._fired.put(cur, Long.valueOf(System.currentTimeMillis()));
            

            cur.getWorld().playSound(cur.getLocation(), Sound.SPIDER_IDLE, 0.8F, 2.0F);
          }
        }
      }
    }
  }
  



















  @EventHandler(priority=EventPriority.LOW)
  public void Damage(CustomDamageEvent paramCustomDamageEvent)
  {
    throw new Error("Unresolved compilation problem: \n\tThe method Poison(String, LivingEntity, LivingEntity, double, int, boolean, boolean, boolean) in the type ConditionFactory is not applicable for the arguments (String, LivingEntity, Player, int, int, boolean, boolean)\n");
  }
  
  public boolean UseCharge(Player player)
  {
    if (!this._stored.containsKey(player)) {
      return false;
    }
    int charges = ((Integer)this._stored.get(player)).intValue();
    
    if (charges <= 0) {
      return false;
    }
    this._stored.put(player, Integer.valueOf(charges - 1));
    player.setLevel(charges - 1);
    
    return true;
  }
  

















  @EventHandler
  public void Recharge(UpdateEvent paramUpdateEvent)
  {
    throw new Error("Unresolved compilation problem: \n\tThe method use(Player, String, long, boolean, boolean) in the type Recharge is not applicable for the arguments (Player, String, int, boolean)\n");
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
      
      if ((arrow.isDead()) || (!arrow.isValid()) || (arrow.getTicksLived() > 300))
      {
        arrowIterator.remove();
        arrow.remove();
      }
    }
  }
  

  public void Reset(Player player)
  {
    this._stored.remove(player);
    this._fired.remove(player);
  }
}
