package nautilus.game.arcade.kit.perks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkBatWave extends Perk
{
  private HashMap<Player, Long> _active = new HashMap();
  private HashMap<Player, Location> _direction = new HashMap();
  private HashMap<Player, ArrayList<Bat>> _bats = new HashMap();
  private HashSet<Player> _pulling = new HashSet();
  private HashSet<Player> _allowLeash = new HashSet();
  



  public PerkBatWave()
  {
    super("Bat Wave", new String[] {C.cYellow + "Right-Click" + C.cGray + " to use " + C.cGreen + "Bat Wave", C.cYellow + "Double Right-Click" + C.cGray + " to use " + C.cGreen + "Bat Leash" });
  }
  

  @EventHandler
  public void Deactivate(CustomDamageEvent event)
  {
    Player player = event.GetDamageePlayer();
    if (player == null) { return;
    }
    if (this._pulling.remove(player))
    {
      for (Bat bat : (ArrayList)this._bats.get(player)) {
        bat.setLeashHolder(null);
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SPADE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 8000L, false, true))
    {
      if (this._active.containsKey(player))
      {
        if (!Recharge.Instance.use(player, "Leash Bats", 500L, false, false)) {
          return;
        }
        if (!this._pulling.remove(player))
        {
          if (this._allowLeash.remove(player))
          {
            this._pulling.add(player);
            
            for (Bat bat : (ArrayList)this._bats.get(player)) {
              bat.setLeashHolder(player);
            }
          }
        }
        else {
          for (Bat bat : (ArrayList)this._bats.get(player)) {
            bat.setLeashHolder(null);
          }
        }
      }
      else
      {
        Recharge.Instance.use(player, GetName(), 8000L, true, true);
      }
      
    }
    else
    {
      this._direction.put(player, player.getEyeLocation());
      this._active.put(player, Long.valueOf(System.currentTimeMillis()));
      this._allowLeash.add(player);
      
      this._bats.put(player, new ArrayList());
      
      for (int i = 0; i < 32; i++)
      {
        this.Manager.GetGame().CreatureAllowOverride = true;
        Bat bat = (Bat)player.getWorld().spawn(player.getEyeLocation(), Bat.class);
        ((ArrayList)this._bats.get(player)).add(bat);
        this.Manager.GetGame().CreatureAllowOverride = false;
      }
      

      UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (this._active.containsKey(cur))
      {

        if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._active.get(cur)).longValue(), 3000L))
        {
          Clear(cur);
        }
        else
        {
          Location loc = (Location)this._direction.get(cur);
          
          Vector batVec = new Vector(0, 0, 0);
          double batCount = 0.0D;
          

          for (Bat bat : (ArrayList)this._bats.get(cur))
          {
            if (bat.isValid())
            {

              batVec.add(bat.getLocation().toVector());
              batCount += 1.0D;
              
              Vector rand = new Vector((Math.random() - 0.5D) / 2.0D, (Math.random() - 0.5D) / 2.0D, (Math.random() - 0.5D) / 2.0D);
              bat.setVelocity(loc.getDirection().clone().multiply(0.5D).add(rand));
              
              for (Player other : this.Manager.GetGame().GetPlayers(true))
              {
                if (!other.equals(cur))
                {

                  if (Recharge.Instance.usable(other, "Hit by Bat"))
                  {

                    if (UtilEnt.hitBox(bat.getLocation(), other, 2.0D, null))
                    {

                      this.Manager.GetDamage().NewDamageEvent(other, cur, null, 
                        EntityDamageEvent.DamageCause.CUSTOM, 2.5D, true, true, false, 
                        cur.getName(), GetName());
                      

                      bat.getWorld().playSound(bat.getLocation(), org.bukkit.Sound.BAT_HURT, 1.0F, 1.0F);
                      UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_SMOKE, bat.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 3);
                      
                      bat.remove();
                      

                      Recharge.Instance.useForce(other, "Hit by Bat", 200L);
                    } }
                }
              }
            }
          }
          if (this._pulling.contains(cur))
          {
            batVec.multiply(1.0D / batCount);
            
            Location batLoc = batVec.toLocation(cur.getWorld());
            
            UtilAction.velocity(cur, UtilAlg.getTrajectory(cur.getLocation(), batLoc), 0.5D, false, 0.0D, 0.0D, 10.0D, false);
          }
        } }
    }
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event) {
    Clear(event.getPlayer());
  }
  
  @EventHandler
  public void PlayerDeath(PlayerDeathEvent event)
  {
    Clear(event.getEntity());
  }
  
  public void Clear(Player player)
  {
    this._active.remove(player);
    this._direction.remove(player);
    this._pulling.remove(player);
    if (this._bats.containsKey(player))
    {
      for (Bat bat : (ArrayList)this._bats.get(player))
      {
        if (bat.isValid()) {
          UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_SMOKE, bat.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 3);
        }
        bat.remove();
      }
      

      this._bats.remove(player);
    }
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 1.75D);
  }
}
