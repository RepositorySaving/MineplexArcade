package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.fire.Fire;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.util.Vector;

public class Inferno extends SkillActive
{
  private HashSet<Player> _active = new HashSet();
  










  public Inferno(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Hold Block to use Inferno;", 
      "You spray fire at #1.05#0.15 velocity,", 
      "igniting enemies for #0.3#0.1 ." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: #30#-2 per Second";
  }
  

  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      mineplex.core.common.util.UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    return true;
  }
  

  public void Skill(Player player, int level)
  {
    this._active.add(player);
  }
  
  @org.bukkit.event.EventHandler
  public void Update(UpdateEvent event)
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
          else if (!this.Factory.Energy().Use(cur, GetName(), 1.5D - 0.1D * level, true, false))
          {
            this._active.remove(cur);

          }
          else
          {
            Item fire = cur.getWorld().dropItem(cur.getEyeLocation().add(cur.getLocation().getDirection()), ItemStackFactory.Instance.CreateStack(Material.FIRE));
            this.Factory.Fire().Add(fire, cur, 0.7D, 0.0D, 0.3D + 0.1D * level, 1, GetName());
            
            fire.teleport(cur.getEyeLocation());
            double x = 0.07000000000000001D - UtilMath.r(14) / 100.0D;
            double y = 0.07000000000000001D - UtilMath.r(14) / 100.0D;
            double z = 0.07000000000000001D - UtilMath.r(14) / 100.0D;
            fire.setVelocity(cur.getLocation().getDirection().add(new Vector(x, y, z)).multiply(1.05D + 0.15D * level));
            

            cur.getWorld().playSound(cur.getLocation(), org.bukkit.Sound.GHAST_FIREBALL, 0.1F, 1.0F);
          }
        } }
    }
  }
  
  public void Reset(Player player) {
    this._active.remove(player);
  }
}
