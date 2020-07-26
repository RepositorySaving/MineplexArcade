package mineplex.minecraft.game.classcombat.Skill.Assassin;

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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Assassin extends Skill
{
  public Assassin(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Permanent Speed II.", 
      "Fall damage reduced by 1." });
  }
  

  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() == UpdateType.FAST) {
      for (Player cur : GetUsers())
        this.Factory.Condition().Factory().Speed(GetName(), cur, cur, 1.9D, 1, false, false, false);
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
        if ((inv.getBoots() != null) && (inv.getBoots().getDurability() > 0))
          inv.getBoots().setDurability((short)(inv.getBoots().getDurability() - 1));
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void AttackDamage(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    int level = getLevel(damager);
    if (level == 0) { return;
    }
    event.SetKnockback(false);
    event.AddMod(damager.getName(), "Assassin Class", 0.0D, false);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void FallDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.FALL) {
      return;
    }
    Player player = event.GetDamageePlayer();
    if (player == null) { return;
    }
    int level = getLevel(player);
    if (level == 0) { return;
    }
    event.AddMod(null, GetName(), -1.0D, false);
  }
  
  public void Reset(Player player) {}
}
