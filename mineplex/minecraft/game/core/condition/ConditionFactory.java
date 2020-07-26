package mineplex.minecraft.game.core.condition;

import mineplex.minecraft.game.core.condition.conditions.Burning;
import mineplex.minecraft.game.core.condition.conditions.Cloak;
import mineplex.minecraft.game.core.condition.conditions.FireItemImmunity;
import mineplex.minecraft.game.core.condition.conditions.Silence;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class ConditionFactory
{
  public ConditionManager Manager;
  
  public ConditionFactory(ConditionManager manager)
  {
    this.Manager = manager;
  }
  


  public Condition Custom(String reason, LivingEntity ent, LivingEntity source, Condition.ConditionType type, double duration, int mult, boolean extend, Material indMat, byte indData, boolean showIndicator)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      type, mult, (int)(20.0D * duration), extend, 
      indMat, indData, showIndicator, false));
  }
  

  public Condition Invulnerable(String reason, LivingEntity ent, LivingEntity source, double duration, boolean extend, boolean showIndicator)
  {
    showIndicator = false;
    
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.INVULNERABLE, 0, (int)(20.0D * duration), extend, 
      Material.GHAST_TEAR, (byte)0, showIndicator, false));
  }
  

  public Condition FireItemImmunity(String reason, LivingEntity ent, LivingEntity source, double duration, boolean extend)
  {
    return this.Manager.AddCondition(new FireItemImmunity(this.Manager, reason, ent, source, 
      Condition.ConditionType.FIRE_ITEM_IMMUNITY, 0, (int)(20.0D * duration), extend, 
      Material.GHAST_TEAR, (byte)0, false));
  }
  

  public Condition Cloak(String reason, LivingEntity ent, LivingEntity source, double duration, boolean extend, boolean inform)
  {
    return this.Manager.AddCondition(new Cloak(this.Manager, reason, ent, source, 
      Condition.ConditionType.CLOAK, 0, (int)(20.0D * duration), extend, 
      Material.GHAST_TEAR, (byte)0, false));
  }
  


  public Condition Explosion(String reason, LivingEntity ent, LivingEntity source, int mult, double duration, boolean extend, boolean showIndicator)
  {
    showIndicator = false;
    
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.EXPLOSION, mult, (int)(20.0D * duration), extend, 
      Material.GHAST_TEAR, (byte)0, showIndicator, false));
  }
  

  public Condition Lightning(String reason, LivingEntity ent, LivingEntity source, int mult, double duration, boolean extend, boolean showIndicator)
  {
    showIndicator = false;
    
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.LIGHTNING, mult, (int)(20.0D * duration), extend, 
      Material.GHAST_TEAR, (byte)0, showIndicator, false));
  }
  

  public Condition Falling(String reason, LivingEntity ent, LivingEntity source, double duration, boolean extend, boolean showIndicator)
  {
    showIndicator = false;
    
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.FALLING, 0, (int)(20.0D * duration), extend, 
      Material.GHAST_TEAR, (byte)0, showIndicator, false));
  }
  

  public Condition Silence(String reason, LivingEntity ent, LivingEntity source, double duration, boolean extend, boolean showIndicator)
  {
    return this.Manager.AddCondition(new Silence(this.Manager, reason, ent, source, 
      Condition.ConditionType.SILENCE, 0, (int)(20.0D * duration), extend, 
      Material.WATCH, (byte)0, showIndicator));
  }
  

  public Condition Speed(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.SPEED, mult, (int)(20.0D * duration), extend, 
      Material.FEATHER, (byte)0, showIndicator, ambient));
  }
  

  public Condition Strength(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.INCREASE_DAMAGE, mult, (int)(20.0D * duration), extend, 
      Material.IRON_SWORD, (byte)0, showIndicator, ambient));
  }
  

  public Condition Hunger(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    showIndicator = false;
    
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.HUNGER, mult, (int)(20.0D * duration), extend, 
      Material.ROTTEN_FLESH, (byte)0, showIndicator, ambient));
  }
  

  public Condition Regen(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.REGENERATION, mult, (int)(20.0D * duration), extend, 
      Material.INK_SACK, (byte)1, showIndicator, ambient));
  }
  

  public Condition Weakness(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.WEAKNESS, mult, (int)(20.0D * duration), extend, 
      Material.INK_SACK, (byte)15, showIndicator, ambient));
  }
  

  public Condition Protection(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.DAMAGE_RESISTANCE, mult, (int)(20.0D * duration), extend, 
      Material.IRON_CHESTPLATE, (byte)0, showIndicator, ambient));
  }
  

  public Condition FireResist(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.FIRE_RESISTANCE, mult, (int)(20.0D * duration), extend, 
      Material.BLAZE_POWDER, (byte)0, showIndicator, ambient));
  }
  

  public Condition Breath(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.WATER_BREATHING, mult, (int)(20.0D * duration), extend, 
      Material.INK_SACK, (byte)4, showIndicator, ambient));
  }
  

  public Condition DigFast(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.FAST_DIGGING, mult, (int)(20.0D * duration), extend, 
      Material.GLOWSTONE_DUST, (byte)0, showIndicator, ambient));
  }
  

  public Condition DigSlow(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.SLOW_DIGGING, mult, (int)(20.0D * duration), extend, 
      Material.WOOD_PICKAXE, (byte)0, showIndicator, ambient));
  }
  

  public Condition Jump(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.JUMP, mult, (int)(20.0D * duration), extend, 
      Material.CARROT_ITEM, (byte)0, showIndicator, ambient));
  }
  

  public Condition Invisible(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    showIndicator = false;
    
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.INVISIBILITY, mult, (int)(20.0D * duration), extend, 
      Material.SNOW_BALL, (byte)0, showIndicator, ambient));
  }
  











  public Condition Shock(String reason, LivingEntity ent, LivingEntity source, double duration, boolean extend, boolean showIndicator)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.SHOCK, 0, (int)(20.0D * duration), extend, 
      Material.DEAD_BUSH, (byte)0, showIndicator, false));
  }
  

  public Condition Ignite(String reason, LivingEntity ent, LivingEntity source, double duration, boolean extend, boolean showIndicator)
  {
    showIndicator = false;
    
    return this.Manager.AddCondition(new Burning(this.Manager, reason, ent, source, 
      Condition.ConditionType.BURNING, 0, (int)(20.0D * duration), extend, 
      Material.GHAST_TEAR, (byte)0, showIndicator));
  }
  

  public Condition Slow(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean stun, boolean ambient)
  {
    if (stun) {
      ent.setVelocity(new Vector(0, 0, 0));
    }
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.SLOW, mult, (int)(20.0D * duration), extend, 
      Material.WEB, (byte)0, showIndicator, ambient));
  }
  

  public Condition Poison(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.POISON, mult, (int)(20.0D * duration), extend, 
      Material.SLIME_BALL, (byte)14, showIndicator, ambient));
  }
  

  public Condition Confuse(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.CONFUSION, mult, (int)(20.0D * duration), extend, 
      Material.ENDER_PEARL, (byte)0, showIndicator, ambient));
  }
  

  public Condition Blind(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.BLINDNESS, mult, (int)(20.0D * duration), extend, 
      Material.EYE_OF_ENDER, (byte)0, showIndicator, ambient));
  }
  

  public Condition NightVision(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.NIGHT_VISION, mult, (int)(20.0D * duration), extend, 
      Material.EYE_OF_ENDER, (byte)0, showIndicator, ambient));
  }
  

  public Condition HealthBoost(String reason, LivingEntity ent, LivingEntity source, double duration, int mult, boolean extend, boolean showIndicator, boolean ambient)
  {
    return this.Manager.AddCondition(new Condition(this.Manager, reason, ent, source, 
      Condition.ConditionType.HEALTH_BOOST, mult, (int)(20.0D * duration), extend, 
      Material.APPLE, (byte)0, showIndicator, ambient));
  }
}
