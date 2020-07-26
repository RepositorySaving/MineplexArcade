package mineplex.minecraft.game.classcombat.Skill.Shifter;

import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Shifter extends Skill
{
  public Shifter(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Permanent Protection II." });
  }
  

  @org.bukkit.event.EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() == UpdateType.FAST) {
      for (Player cur : GetUsers())
        this.Factory.Condition().Factory().Protection(GetName(), cur, cur, 1.9D, 1, false, false, false);
    }
    if (event.getType() == UpdateType.SLOWER) {
      for (Player cur : GetUsers())
      {
        PlayerInventory inv = cur.getInventory();
        
        if ((inv.getHelmet() != null) && (inv.getHelmet().getDurability() > 0)) {
          inv.getHelmet().setDurability((short)(inv.getHelmet().getDurability() - 1));
        }
        if ((inv.getChestplate() != null) && (inv.getChestplate().getDurability() > 0)) {
          inv.getChestplate().setDurability((short)(inv.getChestplate().getDurability() - 1));
        }
        if ((inv.getLeggings() != null) && (inv.getLeggings().getDurability() > 0)) {
          inv.getLeggings().setDurability((short)(inv.getLeggings().getDurability() - 1));
        }
        if ((inv.getBoots() != null) && (inv.getBoots().getDurability() > 0)) {
          inv.getBoots().setDurability((short)(inv.getBoots().getDurability() - 1));
        }
      }
    }
  }
  
  public void Reset(Player player) {}
}
