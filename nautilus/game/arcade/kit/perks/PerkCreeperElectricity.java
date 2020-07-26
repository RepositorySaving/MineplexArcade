package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseCreeper;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PerkCreeperElectricity extends Perk
{
  private HashMap<Player, Long> _active = new HashMap();
  


  public PerkCreeperElectricity()
  {
    super("Lightning Shield", new String[] {"When hit by a non-melee attack, you gain " + C.cGreen + "Lightning Shield" });
  }
  


  @EventHandler
  public void Shield(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this.Kit.HasKit(damagee)) {
      return;
    }
    this._active.put(damagee, Long.valueOf(System.currentTimeMillis()));
    
    SetPowered(damagee, true);
    

    damagee.getWorld().playSound(damagee.getLocation(), Sound.CREEPER_HISS, 3.0F, 1.25F);
    

    UtilPlayer.message(damagee, F.main("Skill", "You gained " + F.skill(GetName()) + "."));
  }
  

  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    Iterator<Player> shieldIterator = this._active.keySet().iterator();
    
    while (shieldIterator.hasNext())
    {
      Player player = (Player)shieldIterator.next();
      
      if (!IsPowered(player))
      {
        shieldIterator.remove();
        SetPowered(player, false);


      }
      else if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._active.get(player)).longValue(), 2000L))
      {
        shieldIterator.remove();
        
        SetPowered(player, false);
        

        player.getWorld().playSound(player.getLocation(), Sound.CREEPER_HISS, 3.0F, 0.75F);
      }
    }
  }
  
  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this.Kit.HasKit(damagee)) {
      return;
    }
    if (!IsPowered(damagee)) {
      return;
    }
    event.SetCancelled("Lightning Shield");
    

    UtilPlayer.message(damagee, F.main("Skill", "You hit " + F.elem(mineplex.core.common.util.UtilEnt.getName(event.GetDamagerPlayer(false))) + " with " + F.skill(GetName()) + "."));
    

    damagee.getWorld().strikeLightningEffect(damagee.getLocation());
    SetPowered(damagee, false);
    

    this.Manager.GetDamage().NewDamageEvent(event.GetDamagerEntity(false), damagee, null, 
      EntityDamageEvent.DamageCause.LIGHTNING, 4.0D, true, true, false, 
      damagee.getName(), GetName());
  }
  
  public DisguiseCreeper GetDisguise(Player player)
  {
    mineplex.core.disguise.disguises.DisguiseBase disguise = this.Manager.GetDisguise().getDisguise(player);
    if (disguise == null) {
      return null;
    }
    if (!(disguise instanceof DisguiseCreeper)) {
      return null;
    }
    return (DisguiseCreeper)disguise;
  }
  
  public void SetPowered(Player player, boolean powered)
  {
    DisguiseCreeper creeper = GetDisguise(player);
    if (creeper == null) { return;
    }
    creeper.SetPowered(powered);
    
    this.Manager.GetDisguise().updateDisguise(creeper);
  }
  
  public boolean IsPowered(Player player)
  {
    DisguiseCreeper creeper = GetDisguise(player);
    if (creeper == null) { return false;
    }
    return creeper.IsPowered();
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 2.5D);
  }
}
