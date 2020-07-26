package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSlime;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PerkSlimeRocket extends Perk implements mineplex.core.projectile.IThrown
{
  private HashMap<Player, Slime> _active = new HashMap();
  private HashMap<Slime, Player> _owner = new HashMap();
  private HashMap<Slime, Long> _lastAttack = new HashMap();
  


  public PerkSlimeRocket()
  {
    super("Slime Rocket", new String[] {C.cYellow + "Hold/Release Block" + C.cGray + " to use " + C.cGreen + "Slime Rocket" });
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

        int size = 1;
        if (player.getExp() > 0.8D) { size = 3;
        } else if (player.getExp() > 0.55D) { size = 2;
        }
        
        DisguiseSlime slime = (DisguiseSlime)this.Manager.GetDisguise().getDisguise(player);
        if ((slime != null) && (slime.GetSize() != size))
        {
          slime.SetSize(size);
          this.Manager.GetDisguise().updateDisguise(slime);
        }
        
        if (!player.isBlocking())
        {

          player.setExp((float)Math.min(0.999D, player.getExp() + 0.004D)); }
      }
    }
  }
  
  @EventHandler
  public void Activate(PlayerInteractEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (mineplex.core.common.util.UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SWORD")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 4000L, true, true)) {
      return;
    }
    
    this.Manager.GetGame().CreatureAllowOverride = true;
    Slime slime = (Slime)player.getWorld().spawn(player.getEyeLocation(), Slime.class);
    slime.setSize(1);
    slime.setCustomNameVisible(true);
    this.Manager.GetGame().CreatureAllowOverride = false;
    
    player.setPassenger(slime);
    
    this._active.put(player, slime);
    this._owner.put(slime, player);
    
    UtilPlayer.message(player, F.main("Skill", "You are charging " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void ChargeRelease(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Player> chargeIterator = this._active.keySet().iterator();
    
    while (chargeIterator.hasNext())
    {
      Player player = (Player)chargeIterator.next();
      Slime slime = (Slime)this._active.get(player);
      

      if (!slime.isValid())
      {
        slime.remove();
        FireRocket(player);
        chargeIterator.remove();
      }
      
      if (player.isBlocking())
      {

        if (player.getExp() < 0.1D)
        {
          FireRocket(player);
          chargeIterator.remove();
        }
        else
        {
          slime.getWorld().playSound(slime.getLocation(), org.bukkit.Sound.SLIME_WALK, 0.5F, (float)(0.5D + Math.max(3.0D, slime.getTicksLived() / 20.0D) / 3.0D));
          
          if (slime.getTicksLived() > 60)
          {
            if (slime.getTicksLived() > 100)
            {
              FireRocket(player);
              chargeIterator.remove();
            }
          }
          else
          {
            player.setExp(Math.max(0.0F, player.getExp() - 0.01F));
            

            slime.setSize((int)(1.25D + slime.getTicksLived() / 25.0D));
            
            slime.setMaxHealth(5 + slime.getTicksLived() / 3);
            slime.setHealth(slime.getMaxHealth());
            

            String out = C.cRed;
            
            if (slime.getSize() == 1) { out = C.cGold;
            } else if (slime.getSize() == 2) { out = C.cYellow;
            } else if (slime.getSize() == 3) { out = C.cGreen;
            }
            for (int i = 0; i < slime.getTicksLived() / 4; i++)
            {
              out = out + "|";
            }
            
            slime.setCustomName(out);
          }
          
        }
      }
      else
      {
        FireRocket(player);
        chargeIterator.remove();
      }
    }
  }
  
  public void FireRocket(Player player)
  {
    double charge = Math.max(1.0D, Math.min(3.0D, ((Slime)this._active.get(player)).getTicksLived() / 20.0D));
    
    if ((this._active.get(player) == null) || (!((Slime)this._active.get(player)).isValid()))
    {

      UtilPlayer.message(player, F.main("Skill", "You failed " + F.skill(GetName()) + "."));
      return;
    }
    
    Slime slime = (Slime)this._active.get(player);
    
    slime.setCustomName(null);
    slime.setCustomNameVisible(false);
    slime.setTicksLived(1);
    

    UtilPlayer.message(player, F.main("Skill", "You released " + F.skill(GetName()) + "."));
    
    slime.leaveVehicle();
    player.eject();
    
    mineplex.core.common.util.UtilAction.velocity(slime, player.getLocation().getDirection(), 1.0D + charge / 2.0D, false, 0.0D, 0.2D, 10.0D, true);
    
    this.Manager.GetProjectile().AddThrow(slime, player, this, -1L, true, true, true, 
      null, 0.0F, 0.0F, null, 0, UpdateType.FASTEST, 2.0D);
  }
  
  @EventHandler
  public void SlimeTarget(EntityTargetEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (!this._owner.containsKey(event.getEntity())) {
      return;
    }
    if (((Player)this._owner.get(event.getEntity())).equals(event.getTarget()))
    {
      event.setCancelled(true);
    }
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    if (target == null) {
      return;
    }
    if (!(data.GetThrown() instanceof Slime)) {
      return;
    }
    Slime slime = (Slime)data.GetThrown();
    

    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      EntityDamageEvent.DamageCause.PROJECTILE, slime.getSize() * 5, true, true, false, 
      mineplex.core.common.util.UtilEnt.getName(data.GetThrower()), GetName());
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 3.0D);
  }
  



  public void Idle(ProjectileUser data) {}
  



  public void Expire(ProjectileUser data) {}
  


  @EventHandler
  public void SlimeDamage(CustomDamageEvent event)
  {
    if (!(event.GetDamagerEntity(false) instanceof Slime)) {
      return;
    }
    Slime slime = (Slime)event.GetDamagerEntity(false);
    


    if ((this._lastAttack.containsKey(slime)) && (!mineplex.core.common.util.UtilTime.elapsed(((Long)this._lastAttack.get(slime)).longValue(), 500L)))
    {
      event.SetCancelled("Slime Attack Rate");
      return;
    }
    
    this._lastAttack.put(slime, Long.valueOf(System.currentTimeMillis()));
    

    Player owner = (Player)this._owner.get(slime);
    



    if ((owner != null) && (owner.equals(event.GetDamageeEntity())))
    {
      event.SetCancelled("Owner Damage");
      

      if (slime.getVehicle() == null)
      {
        UtilPlayer.health(owner, 1.0D);
      }
    }
    else
    {
      event.AddMod("Slime Damage", "Negate", -event.GetDamageInitial(), false);
      event.AddMod("Slime Damage", "Attack", 2 * slime.getSize(), true);
      event.AddKnockback("Slime Knockback", 2.0D);
    }
  }
  
  @EventHandler
  public void SlimeClean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    Iterator<Slime> slimeIterator = this._owner.keySet().iterator();
    
    while (slimeIterator.hasNext())
    {
      Slime slime = (Slime)slimeIterator.next();
      

      if (slime.getVehicle() == null)
      {
        if (slime.getTicksLived() > 120)
        {
          slime.setTicksLived(1);
          
          this.Manager.GetBlood().Effects(slime.getLocation(), 6 + 6 * slime.getSize(), 0.2D + 0.1D * slime.getSize(), null, 1.0F, 1.0F, Material.SLIME_BALL, (byte)0, 15, false);
          
          if (slime.getSize() <= 1) {
            slime.remove();
          } else {
            slime.setSize(slime.getSize() - 1);
          }
        }
      }
      if (!slime.isValid()) {
        slimeIterator.remove();
      }
    }
    slimeIterator = this._lastAttack.keySet().iterator();
    
    while (slimeIterator.hasNext())
    {
      Slime slime = (Slime)slimeIterator.next();
      
      if (!slime.isValid()) {
        slimeIterator.remove();
      }
    }
  }
}
