package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.PlayerGameRespawnEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PerkInfernalHorror extends Perk
{
  public HashSet<Player> _active = new HashSet();
  



  public PerkInfernalHorror()
  {
    super("Infernal Horror", new String[] {C.cGray + "Tranform into " + F.skill("Infernal Horror") + " at 100% Rage.", C.cGray + "Charge your Rage by dealing/taking damage." });
  }
  

  @EventHandler
  public void EnergyUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {

        player.setExp((float)Math.max(0.0D, player.getExp() - 0.001D));
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void DamagerEnergy(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    damager.setExp(Math.min(0.999F, damager.getExp() + (float)(event.GetDamage() / 80.0D)));
    


    ActiveCheck(damager);
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void DamageeEnergy(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.VOID) {
      return;
    }
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this.Kit.HasKit(damagee)) {
      return;
    }
    damagee.setExp(Math.min(0.999F, damagee.getExp() + (float)(event.GetDamage() / 80.0D)));
    
    ActiveCheck(damagee);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void DamageBoost(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    if (!this._active.contains(damager)) {
      return;
    }
    event.AddMod(damager.getName(), GetName(), 1.0D, false);
  }
  
  @EventHandler
  public void Check(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true)) {
      if (this.Kit.HasKit(player)) {
        ActiveCheck(player);
      }
    }
  }
  
  public void ActiveCheck(Player player) {
    if (this._active.contains(player))
    {
      player.setExp((float)Math.max(0.0D, player.getExp() - 0.005D));
      
      if (player.getExp() > 0.0F)
      {

        this.Manager.GetCondition().Factory().Speed(GetName(), player, player, 0.9D, 1, false, false, false);
        

        UtilParticle.PlayParticle(UtilParticle.ParticleType.FLAME, player.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.25F, 0.25F, 0.0F, 1);
        
        if (Math.random() > 0.9D) {
          UtilParticle.PlayParticle(UtilParticle.ParticleType.LAVA, player.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.25F, 0.25F, 0.0F, 1);
        }
      }
      else {
        this._active.remove(player);
        

        mineplex.core.common.util.UtilPlayer.message(player, F.main("Skill", "You are no longer " + F.skill("Infernal Horror") + "."));
      }
      
    }
    else if (player.getExp() > 0.99D)
    {
      this._active.add(player);
      

      player.getWorld().playSound(player.getLocation(), Sound.FIRE, 2.0F, 1.0F);
      player.getWorld().playSound(player.getLocation(), Sound.FIRE, 2.0F, 1.0F);
      

      mineplex.core.common.util.UtilPlayer.message(player, F.main("Skill", "You transformed into " + F.skill("Infernal Horror") + "."));
    }
  }
  
  @EventHandler
  public void Clean(PlayerGameRespawnEvent event)
  {
    event.GetPlayer().setExp(0.0F);
    this._active.remove(event.GetPlayer());
  }
  
  public boolean IsActive(Player player)
  {
    return this._active.contains(player);
  }
}
