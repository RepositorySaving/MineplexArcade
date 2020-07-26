package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillChargeBow;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

public class Barrage extends SkillChargeBow
{
  private WeakHashMap<Player, Integer> _chargeArrows = new WeakHashMap();
  private HashSet<Projectile> _arrows = new HashSet();
  


  public Barrage(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int maxLevel)
  {
    super(skills, name, classType, skillType, cost, maxLevel, 0.01F, 0.005F, false, true);
    
    SetDesc(
      new String[] {
      "Charge your bow fire bonus arrows.", 
      "", 
      GetChargeString(), 
      "", 
      "Fires up to #2#2 additional arrows." });
  }
  


  public void DoSkillCustom(Player player, float charge, Arrow arrow)
  {
    this._chargeArrows.put(player, Integer.valueOf((int)(charge * (2 + 2 * getLevel(player)))));
  }
  
  @EventHandler
  public void Skill(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._chargeArrows.containsKey(cur))
      {

        if (!UtilGear.isBow(cur.getItemInHand()))
        {
          this._chargeArrows.remove(cur);
        }
        else
        {
          int arrows = ((Integer)this._chargeArrows.get(cur)).intValue();
          if (arrows <= 0)
          {
            this._chargeArrows.remove(cur);
          }
          else
          {
            this._chargeArrows.put(cur, Integer.valueOf(arrows - 1));
            

            Vector random = new Vector((Math.random() - 0.5D) / 10.0D, (Math.random() - 0.5D) / 10.0D, (Math.random() - 0.5D) / 10.0D);
            Projectile arrow = cur.launchProjectile(Arrow.class);
            arrow.setVelocity(cur.getLocation().getDirection().add(random).multiply(3));
            this._arrows.add(arrow);
            cur.getWorld().playSound(cur.getLocation(), Sound.SHOOT_ARROW, 1.0F, 1.0F);
            
            mineplex.core.common.util.UtilParticle.PlayParticle(UtilParticle.ParticleType.CRIT, arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
          }
        } } }
  }
  
  @EventHandler
  public void ProjectileHit(ProjectileHitEvent event) {
    if (this._arrows.remove(event.getEntity())) {
      event.getEntity().remove();
    }
  }
  
  @EventHandler
  public void Clean(UpdateEvent event) {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Projectile> arrowIterator = this._arrows.iterator(); arrowIterator.hasNext();)
    {
      Projectile arrow = (Projectile)arrowIterator.next();
      
      if ((arrow.isDead()) || (!arrow.isValid())) {
        arrowIterator.remove();
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._charge.remove(player);
    this._chargeArrows.remove(player);
  }
}
