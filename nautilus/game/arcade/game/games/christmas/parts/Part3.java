package nautilus.game.arcade.game.games.christmas.parts;

import java.util.ArrayList;
import java.util.Iterator;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.Sleigh;
import nautilus.game.arcade.game.games.christmas.content.SnowmanBoss;
import nautilus.game.arcade.game.games.christmas.content.SnowmanWaveA;
import nautilus.game.arcade.game.games.christmas.content.SnowmanWaveB;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitScheduler;

public class Part3 extends Part
{
  private ArrayList<Location> _snowmenA;
  private ArrayList<Location> _snowmenB;
  private ArrayList<Location> _boss;
  private ArrayList<Location> _bridge;
  private SnowmanBoss _snowman;
  private SnowmanWaveA _waveA;
  private SnowmanWaveB _waveB;
  private boolean _a = false;
  
  public Part3(Christmas host, Location sleigh, Location[] presents, ArrayList<Location> snowmenA, ArrayList<Location> snowmenB, ArrayList<Location> boss, ArrayList<Location> bridge)
  {
    super(host, sleigh, presents);
    
    this._snowmenA = snowmenA;
    this._snowmenB = snowmenB;
    this._boss = boss;
    this._bridge = bridge;
  }
  

  public void Activate()
  {
    this._waveA = new SnowmanWaveA(this.Host, this._snowmenA, GetSleighWaypoint(), GetPresents());
    this._waveB = new SnowmanWaveB(this.Host, this._snowmenB, GetSleighWaypoint(), GetPresents());
  }
  
  private void UpdateBridge()
  {
    if (this._bridge.isEmpty()) {
      return;
    }
    int lowest = 1000;
    
    for (Location loc : this._bridge) {
      if (loc.getBlockZ() < lowest)
        lowest = loc.getBlockZ();
    }
    Iterator<Location> gateIterator = this._bridge.iterator();
    
    boolean sound = true;
    
    while (gateIterator.hasNext())
    {
      Location loc = (Location)gateIterator.next();
      
      if (loc.getBlockZ() == lowest)
      {
        byte color = 14;
        if (lowest % 6 == 1) { color = 1;
        } else if (lowest % 6 == 2) { color = 4;
        } else if (lowest % 6 == 3) { color = 5;
        } else if (lowest % 6 == 4) { color = 3;
        } else if (lowest % 6 == 5) { color = 2;
        }
        loc.getBlock().setTypeIdAndData(35, color, false);
        loc.getWorld().playEffect(loc, Effect.STEP_SOUND, 35);
        gateIterator.remove();
        
        if (sound)
        {
          loc.getWorld().playSound(loc, Sound.ZOMBIE_UNFECT, 3.0F, 1.0F);
          sound = false;
        }
      }
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() == UpdateType.FASTER) {
      UpdateBridge();
    }
    if ((event.getType() == UpdateType.TICK) && 
      (this._snowman != null)) {
      this._snowman.UpdateMove();
    }
    if ((event.getType() == UpdateType.FASTEST) && 
      (this._snowman != null)) {
      this._snowman.UpdateCombine();
    }
    if ((event.getType() == UpdateType.TICK) && 
      (this._snowman != null)) {
      this._snowman.UpdateSnowball();
    }
    if ((event.getType() == UpdateType.TICK) && 
      (this._snowman != null))
    {
      SetObjectiveText("Kill the Snow Monster", this._snowman.GetHealth());
    }
    
    if ((event.getType() == UpdateType.TICK) && 
      (this._waveA != null)) {
      this._waveA.Update();
    }
    if ((event.getType() == UpdateType.TICK) && 
      (this._waveB != null)) {
      this._waveB.Update();
    }
    if ((event.getType() == UpdateType.FAST) && 
      (this._snowman == null) && 
      (HasPresents()))
    {
      this._snowman = new SnowmanBoss(this.Host, (Location)this._boss.get(0));
      this.Host.SantaSay("WATCH OUT! It's some kind of Snow Monster!");
      
      UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Host.Manager.GetPlugin(), new Runnable()
      {
        public void run()
        {
          Part3.this.Host.SantaSay("Shoot the Iron Golem with your bow!");
        }
      }, 80L);
    }
  }
  

  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    if (this._snowman != null) {
      this._snowman.Damage(event);
    }
  }
  
  @EventHandler
  public void UpdateIntro(UpdateEvent event) {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!this._a)
    {
      if (UtilMath.offset(this.Host.GetSleigh().GetLocation(), GetSleighWaypoint()) > 10.0D) {
        return;
      }
      this._a = true;
      
      this.Host.SantaSay("Collect those presents, I'll try to open the gate!");
      SetObjectivePresents();
    }
  }
  

  public boolean CanFinish()
  {
    return (this._snowman != null) && (this._snowman.IsDead()) && (HasPresents());
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void DamageCancel(CustomDamageEvent event)
  {
    if (!(event.GetDamageeEntity() instanceof Snowman)) {
      return;
    }
    if ((this._boss == null) || (event.GetDamageeEntity().getPassenger() == null))
    {
      event.SetCancelled("Snowman Wave Cancel");
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void SnowballDamage(CustomDamageEvent event)
  {
    if (event.GetProjectile() == null) {
      return;
    }
    if (!(event.GetProjectile() instanceof Snowball)) {
      return;
    }
    if (event.GetDamageePlayer() != null) {
      event.AddMod("Christmas Part 3", "Snowball", 1.0D, false);
    } else {
      event.SetCancelled("Snowball vs Mobs");
    }
  }
}
