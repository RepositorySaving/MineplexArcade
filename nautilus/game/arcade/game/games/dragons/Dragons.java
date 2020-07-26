package nautilus.game.arcade.game.games.dragons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.PlayerStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam.PlayerState;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.dragons.kits.KitCoward;
import nautilus.game.arcade.game.games.dragons.kits.KitMarksman;
import nautilus.game.arcade.game.games.dragons.kits.KitPyrotechnic;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkSparkler;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;

public class Dragons extends SoloGame
{
  private HashMap<EnderDragon, DragonData> _dragons = new HashMap();
  private ArrayList<Location> _dragonSpawns = new ArrayList();
  
  private PerkSparkler _sparkler = null;
  













  public Dragons(ArcadeManager manager)
  {
    super(manager, GameType.Dragons, new Kit[] {new KitCoward(manager), new KitMarksman(manager), new KitPyrotechnic(manager) }, new String[] {"You have angered the Dragons!", "Survive as best you can!!!", "Last player alive wins!" });
    

    this.DamagePvP = false;
    this.HungerSet = 20;
    this.WorldWaterDamage = 4;
  }
  

  public void ParseData()
  {
    this._dragonSpawns = this.WorldData.GetDataLocs("RED");
  }
  
  @EventHandler
  public void SparklerAttract(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this._sparkler == null)
    {
      for (Kit kit : GetKits())
      {
        for (Perk perk : kit.GetPerks())
        {
          if ((perk instanceof PerkSparkler))
          {
            this._sparkler = ((PerkSparkler)perk);
          }
        }
      }
    }
    
    for (Iterator localIterator = this._sparkler.GetItems().iterator(); localIterator.hasNext(); 
        
        ((Iterator)???).hasNext())
    {
      Item item = (Item)localIterator.next();
      
      ??? = this._dragons.values().iterator(); continue;DragonData data = (DragonData)((Iterator)???).next();
      
      if (mineplex.core.common.util.UtilMath.offset(data.Location, item.getLocation()) < 48.0D)
      {
        data.TargetEntity = item;
      }
    }
  }
  

  @EventHandler
  public void Death(PlayerStateChangeEvent event)
  {
    if (event.GetState() != GameTeam.PlayerState.OUT) {
      return;
    }
    long time = System.currentTimeMillis() - GetStateTime();
    double gems = time / 10000.0D;
    String reason = "Survived for " + mineplex.core.common.util.UtilTime.MakeStr(time);
    
    AddGems(event.GetPlayer(), gems, reason, false);
  }
  
  @EventHandler
  public void DragonSpawn(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    if (GetState() != Game.GameState.Live) {
      return;
    }
    Iterator<EnderDragon> dragonIterator = this._dragons.keySet().iterator();
    
    while (dragonIterator.hasNext())
    {
      EnderDragon ent = (EnderDragon)dragonIterator.next();
      
      if (!ent.isValid())
      {
        dragonIterator.remove();
        ent.remove();
      }
    }
    
    if (this._dragons.size() < 7)
    {
      this.CreatureAllowOverride = true;
      EnderDragon ent = (EnderDragon)GetSpectatorLocation().getWorld().spawn((Location)this._dragonSpawns.get(0), EnderDragon.class);
      mineplex.core.common.util.UtilEnt.Vegetate(ent);
      this.CreatureAllowOverride = false;
      
      ent.getWorld().playSound(ent.getLocation(), org.bukkit.Sound.ENDERDRAGON_GROWL, 20.0F, 1.0F);
      
      this._dragons.put(ent, new DragonData(this, ent));
    }
  }
  
  @EventHandler
  public void DragonLocation(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (GetState() != Game.GameState.Live) {
      return;
    }
    
    for (DragonData data : this._dragons.values())
    {
      data.Target();
      data.Move();
    }
  }
  
  @EventHandler
  public void DragonTargetCancel(EntityTargetEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void DragonArrowDamage(CustomDamageEvent event)
  {
    if (event.GetProjectile() == null) {
      return;
    }
    if (!this._dragons.containsKey(event.GetDamageeEntity())) {
      return;
    }
    ((DragonData)this._dragons.get(event.GetDamageeEntity())).HitByArrow();
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (event.GetDamagerEntity(true) == null) {
      return;
    }
    event.SetCancelled("Dragon");
    event.AddMod("Dragon", "Damage Reduction", -1.0D * (event.GetDamageInitial() - 1.0D), false);
    
    event.SetKnockback(false);
    
    damagee.playEffect(EntityEffect.HURT);
    
    UtilAction.velocity(damagee, UtilAlg.getTrajectory(event.GetDamagerEntity(true), damagee), 1.0D, false, 0.0D, 0.6D, 2.0D, true);
  }
  
  @EventHandler
  public void FallDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.FALL) {
      event.AddMod("Fall Reduction", "Fall Reduction", -1.0D, false);
    }
  }
}
