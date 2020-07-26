package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashSet;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class Fissure extends SkillActive
{
  private HashSet<FissureData> _active = new HashSet();
  










  public Fissure(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Fissures the earth infront of you,", 
      "creating an impassable wall.", 
      "", 
      "Players struck by the initial slam", 
      "receive Slow 2 for #2#0.5 seconds", 
      "", 
      "Players struck by the fissure", 
      "receive #2#0.4 damage plus an ", 
      "additional #0.6#0.2 damage for", 
      "every block fissure has travelled." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    if (!UtilEnt.isGrounded(player))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " while airborne."));
      return false;
    }
    
    return true;
  }
  

  public void Skill(Player player, int level)
  {
    FissureData data = new FissureData(this, player, level, player.getLocation().getDirection(), player.getLocation().add(0.0D, -0.5D, 0.0D));
    this._active.add(data);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
  }
  
  @org.bukkit.event.EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<FissureData> remove = new HashSet();
    
    for (FissureData data : this._active) {
      if (data.Update())
        remove.add(data);
    }
    for (FissureData data : remove)
    {
      this._active.remove(data);
      data.Clear();
    }
  }
  
  public void Reset(Player player) {}
}
