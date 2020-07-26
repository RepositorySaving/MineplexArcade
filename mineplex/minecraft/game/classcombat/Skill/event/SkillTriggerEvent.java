package mineplex.minecraft.game.classcombat.Skill.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class SkillTriggerEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  
  private Player _player;
  private String _skill;
  private List<Entity> _targets;
  private IPvpClass.ClassType _classType;
  private boolean _cancelled = false;
  
  public SkillTriggerEvent(Player player, String skill, IPvpClass.ClassType classType, List<Entity> targets)
  {
    this._player = player;
    this._skill = skill;
    this._classType = classType;
    this._targets = targets;
  }
  
  public SkillTriggerEvent(Player player, String skill, IPvpClass.ClassType classType, Entity target)
  {
    this._player = player;
    this._skill = skill;
    this._classType = classType;
    this._targets = new ArrayList();
    this._targets.add(target);
  }
  
  public SkillTriggerEvent(Player player, String skill, IPvpClass.ClassType classType, Set<LivingEntity> targets)
  {
    this._player = player;
    this._skill = skill;
    this._classType = classType;
    this._targets = new ArrayList();
    for (LivingEntity ent : targets) {
      this._targets.add(ent);
    }
  }
  
  public SkillTriggerEvent(Player player, String skill, IPvpClass.ClassType classType) {
    this._player = player;
    this._skill = skill;
    this._classType = classType;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public String GetSkillName()
  {
    return this._skill;
  }
  
  public Player GetPlayer()
  {
    return this._player;
  }
  
  public IPvpClass.ClassType GetClassType()
  {
    return this._classType;
  }
  
  public List<Entity> GetTargets()
  {
    return this._targets;
  }
  
  public boolean IsCancelled() {
    return this._cancelled;
  }
  
  public void SetCancelled(boolean cancelled)
  {
    this._cancelled = cancelled;
  }
}
