package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseMagmaCube;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PerkMagmaBoost extends Perk
{
  private HashMap<Player, Integer> _kills = new HashMap();
  



  public PerkMagmaBoost()
  {
    super("Fuel the Fire", new String[] {C.cGray + "Kills give +1 Damage, -15% Knockback Taken and +1 Size.", C.cGray + "Kill bonuses can stack 3 times, and reset on death." });
  }
  

  @EventHandler
  public void Kill(CombatDeathEvent event)
  {
    Player killed = (Player)event.GetEvent().getEntity();
    
    this._kills.remove(killed);
    
    if (event.GetLog().GetKiller() == null) {
      return;
    }
    Player killer = mineplex.core.common.util.UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    
    if ((killer == null) || (killer.equals(killed)) || (!this.Kit.HasKit(killer))) {
      return;
    }
    DisguiseMagmaCube slime = (DisguiseMagmaCube)this.Manager.GetDisguise().getDisguise(killer);
    if (slime == null) {
      return;
    }
    int size = 1;
    if (this._kills.containsKey(killer)) {
      size += ((Integer)this._kills.get(killer)).intValue();
    }
    size = Math.min(3, size);
    
    this._kills.put(killer, Integer.valueOf(size));
    
    slime.SetSize(size + 1);
    this.Manager.GetDisguise().updateDisguise(slime);
    
    killer.setExp(0.99F * (size / 3.0F));
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void SizeDamage(CustomDamageEvent event)
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
    if (!this._kills.containsKey(damager)) {
      return;
    }
    int bonus = ((Integer)this._kills.get(damager)).intValue();
    
    event.AddMod(damager.getName(), GetName(), bonus, false);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void SizeKnockback(CustomDamageEvent event)
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
    if (!this._kills.containsKey(damagee)) {
      return;
    }
    int bonus = ((Integer)this._kills.get(damagee)).intValue();
    
    event.AddKnockback(GetName(), bonus * 0.15D);
  }
  
  @EventHandler
  public void EnergyUpdate(UpdateEvent event)
  {
    if ((event.getType() != UpdateType.SEC) && (event.getType() != UpdateType.FAST) && (event.getType() != UpdateType.FASTER) && (event.getType() != UpdateType.FASTEST)) {
      return;
    }
    for (Player player : mineplex.core.common.util.UtilServer.getPlayers())
    {
      if (this.Kit.HasKit(player))
      {

        float size = 0.0F;
        if (this._kills.containsKey(player)) {
          size += ((Integer)this._kills.get(player)).intValue();
        }
        if ((size == 0.0F) && (event.getType() == UpdateType.SEC)) {
          UtilParticle.PlayParticle(UtilParticle.ParticleType.LAVA, player.getLocation().add(0.0D, 0.4D, 0.0D), 0.15F + 0.15F * size, 0.15F + 0.15F * size, 0.15F + 0.15F * size, 0.0F, 1);
        } else if ((size == 1.0F) && (event.getType() == UpdateType.FAST)) {
          UtilParticle.PlayParticle(UtilParticle.ParticleType.LAVA, player.getLocation().add(0.0D, 0.4D, 0.0D), 0.15F + 0.15F * size, 0.15F + 0.15F * size, 0.15F + 0.15F * size, 0.0F, 1);
        } else if ((size == 2.0F) && (event.getType() == UpdateType.FASTER)) {
          UtilParticle.PlayParticle(UtilParticle.ParticleType.LAVA, player.getLocation().add(0.0D, 0.4D, 0.0D), 0.15F + 0.15F * size, 0.15F + 0.15F * size, 0.15F + 0.15F * size, 0.0F, 1);
        } else if ((size == 3.0F) && (event.getType() == UpdateType.FASTEST)) {
          UtilParticle.PlayParticle(UtilParticle.ParticleType.LAVA, player.getLocation().add(0.0D, 0.4D, 0.0D), 0.15F + 0.15F * size, 0.15F + 0.15F * size, 0.15F + 0.15F * size, 0.0F, 1);
        }
      }
    }
  }
}
