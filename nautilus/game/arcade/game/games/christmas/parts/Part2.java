package nautilus.game.arcade.game.games.christmas.parts;

import java.util.ArrayList;
import java.util.HashMap;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilFirework;
import mineplex.core.common.util.UtilMath;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSpider;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.christmas.Sleigh;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;



public class Part2
  extends Part
{
  private ArrayList<Location> _spiders;
  private ArrayList<Location> _switches;
  private ArrayList<Location> _switchLights;
  private ArrayList<Location> _switched = new ArrayList();
  
  private boolean _a = false;
  private boolean _b = false;
  
  public Part2(Christmas host, Location sleigh, Location[] presents, ArrayList<Location> spiders, ArrayList<Location> switches, ArrayList<Location> switchLights)
  {
    super(host, sleigh, presents);
    
    this._spiders = spiders;
    
    this._switches = new ArrayList();
    for (Location loc : switches) {
      this._switches.add(loc.getBlock().getLocation());
    }
    this._switchLights = switchLights;
    
    for (Location loc : this._switchLights) {
      loc.getBlock().setTypeIdAndData(35, (byte)14, false);
    }
  }
  
  public void Activate()
  {
    for (Location loc : this._switches) {
      loc.getBlock().setTypeIdAndData(69, (byte)5, false);
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event) {
    if (event.getType() == UpdateType.FAST) {
      UpdateIntroA();
    }
    if (event.getType() == UpdateType.FAST) {
      UpdateIntroB();
    }
    if (event.getType() == UpdateType.FAST) {
      UpdateSpiders();
    }
    if (event.getType() == UpdateType.FASTER) {
      UpdateSpiderLeap();
    }
    if (event.getType() == UpdateType.SEC)
    {
      if ((this._switched.size() == 4) && (HasPresents()))
      {
        SetObjectiveText("Wait for the Magic Bridge", 1.0D);
      }
    }
  }
  
  private void UpdateIntroA()
  {
    if (this._a) {
      return;
    }
    if (UtilMath.offset(this.Host.GetSleigh().GetLocation(), GetSleighWaypoint()) > 20.0D) {
      return;
    }
    this._a = true;
    
    this.Host.SantaSay("Oh no! My magic bridge has been turned off!");
  }
  
  private void UpdateIntroB()
  {
    if (this._b) {
      return;
    }
    if (UtilMath.offset(this.Host.GetSleigh().GetLocation(), GetSleighWaypoint()) > 10.0D) {
      return;
    }
    this._b = true;
    
    this.Host.SantaSay("Turn on all four switches to rebuild it!");
    SetObjectiveText("Turn on the 4 switches", 0.0D);
  }
  
  private void UpdateSpiders()
  {
    if (GetCreatures().size() > 40) {
      return;
    }
    if (!this._a) {
      return;
    }
    
    Location loc = (Location)UtilAlg.Random(this._spiders);
    
    this.Host.CreatureAllowOverride = true;
    Skeleton ent = (Skeleton)loc.getWorld().spawn(loc, Skeleton.class);
    this.Host.CreatureAllowOverride = false;
    DisguiseSpider disguise = new DisguiseSpider(ent);
    this.Host.Manager.GetDisguise().disguise(disguise);
    
    ent.setHealth(10.0D);
    
    AddCreature(ent);
  }
  
  private void UpdateSpiderLeap()
  {
    for (Creature ent : GetCreatures().keySet())
    {
      if (UtilEnt.isGrounded(ent))
      {

        if (Math.random() <= 0.05D)
        {

          Player target = (Player)GetCreatures().get(ent);
          if ((target != null) && (target.isValid()))
          {

            double flatDist = UtilMath.offset(ent, target);
            
            double yDiff = target.getLocation().getY() - ent.getLocation().getY();
            
            UtilAction.velocity(ent, UtilAlg.getTrajectory(ent, target), Math.min(1.6D, 0.2D + 0.1D * flatDist), false, 0.0D, 0.1D + 0.1D * yDiff, 1.4D, true);
            
            ent.getWorld().playSound(ent.getLocation(), Sound.SPIDER_IDLE, 1.5F, 2.0F);
          }
        }
      }
    }
  }
  
  @EventHandler
  public void ToggleSwitch(PlayerInteractEvent event) {
    if (event.getClickedBlock() == null) {
      return;
    }
    if (!this._switches.contains(event.getClickedBlock().getLocation())) {
      return;
    }
    if (this._switched.contains(event.getClickedBlock().getLocation())) {
      return;
    }
    event.setCancelled(true);
    
    if (!this.Host.IsLive()) {
      return;
    }
    if (!this.Host.IsAlive(event.getPlayer())) {
      return;
    }
    
    this._switched.add(event.getClickedBlock().getLocation());
    

    Location bestLoc = null;
    double bestDist = 0.0D;
    
    for (Location loc : this._switchLights)
    {
      double dist = UtilMath.offset(event.getClickedBlock().getLocation(), loc);
      
      if ((bestLoc == null) || (bestDist > dist))
      {
        bestLoc = loc;
        bestDist = dist;
      }
    }
    
    bestLoc.getBlock().setData((byte)5);
    UtilFirework.playFirework(bestLoc, FireworkEffect.builder().flicker(true).withColor(Color.GREEN).with(FireworkEffect.Type.BALL).trail(true).build());
    

    if (this._switched.size() == 1)
    {
      this.Host.SantaSay("Great job, " + event.getPlayer().getName() + "! Only 3 switches to go!");
      SetObjectiveText("Turn on the 4 switches", 0.25D);
    }
    else if (this._switched.size() == 2)
    {
      this.Host.SantaSay("Well done, " + event.getPlayer().getName() + "! Only 2 switches to go!");
      SetObjectiveText("Turn on the 4 switches", 0.5D);
    }
    else if (this._switched.size() == 3)
    {
      this.Host.SantaSay("Wonderful, " + event.getPlayer().getName() + "! Only 1 switch to go!");
      SetObjectiveText("Turn on the 4 switches", 0.75D);
    }
    else if (this._switched.size() == 4)
    {
      this.Host.SantaSay("Congratulations, " + event.getPlayer().getName() + "! That's all 4 switches!");
      
      if (!HasPresents()) {
        SetObjectivePresents();
      }
    }
  }
  
  public boolean CanFinish()
  {
    return this._switched.size() >= 4;
  }
}
