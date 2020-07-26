package nautilus.game.arcade.game.games.christmas;

import java.util.ArrayList;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilFirework;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;







public class Sleigh
{
  public Christmas Host;
  private Entity CentralEntity;
  private ArrayList<SleighPart> SleighEnts;
  private ArrayList<SleighHorse> SleighHorses;
  private ArrayList<SleighPart> PresentSlots;
  private ArrayList<Location> PresentsCollected = new ArrayList();
  
  private Location Target;
  
  public Sleigh(Christmas host, Location loc)
  {
    this.Host = host;
    
    this.Host.CreatureAllowOverride = true;
    
    this.Target = loc.clone();
    
    this.CentralEntity = loc.getWorld().spawn(loc, Chicken.class);
    UtilEnt.Vegetate(this.CentralEntity, true);
    UtilEnt.ghost(this.CentralEntity, true, true);
    

    this.PresentSlots = new ArrayList();
    
    this.PresentSlots.add(new SleighPart(2, 0, 0, loc.clone(), -1.0D, -2.0D));
    this.PresentSlots.add(new SleighPart(2, 0, 0, loc.clone(), 0.0D, -2.0D));
    this.PresentSlots.add(new SleighPart(2, 0, 0, loc.clone(), 1.0D, -2.0D));
    this.PresentSlots.add(new SleighPart(2, 0, 0, loc.clone(), -1.0D, -1.0D));
    this.PresentSlots.add(new SleighPart(2, 0, 0, loc.clone(), 0.0D, -1.0D));
    this.PresentSlots.add(new SleighPart(2, 0, 0, loc.clone(), 1.0D, -1.0D));
    
    this.PresentSlots.add(new SleighPart(6, 0, 0, loc.clone(), -1.0D, -2.0D));
    this.PresentSlots.add(new SleighPart(6, 0, 0, loc.clone(), 0.0D, -2.0D));
    this.PresentSlots.add(new SleighPart(6, 0, 0, loc.clone(), 1.0D, -2.0D));
    this.PresentSlots.add(new SleighPart(6, 0, 0, loc.clone(), -1.0D, -1.0D));
    this.PresentSlots.add(new SleighPart(6, 0, 0, loc.clone(), 0.0D, -1.0D));
    this.PresentSlots.add(new SleighPart(6, 0, 0, loc.clone(), 1.0D, -1.0D));
    

    this.SleighEnts = new ArrayList();
    
    for (SleighPart part : this.PresentSlots) {
      this.SleighEnts.add(part);
    }
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 0.0D, -3.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), -1.0D, -3.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), -2.0D, -3.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 1.0D, -3.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 2.0D, -3.0D));
    
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), -2.0D, -2.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), -1.0D, -2.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), 0.0D, -2.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), 1.0D, -2.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 2.0D, -2.0D));
    
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), -2.0D, -1.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), -1.0D, -1.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), 0.0D, -1.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), 1.0D, -1.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 2.0D, -1.0D));
    

    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), -2.0D, 0.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), -1.0D, 0.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 0.0D, 0.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 1.0D, 0.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 2.0D, 0.0D));
    
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), -2.0D, 1.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), -1.0D, 1.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 15, loc.clone(), 0.0D, 1.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), 1.0D, 1.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 2.0D, 1.0D));
    
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), -2.0D, 2.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), -1.0D, 2.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), 0.0D, 2.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), 1.0D, 2.0D));
    this.SleighEnts.add(new SleighPart(0, 44, 7, loc.clone(), 2.0D, 2.0D));
    
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), -2.0D, 3.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), -1.0D, 3.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 0.0D, 3.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 1.0D, 3.0D));
    this.SleighEnts.add(new SleighPart(0, 159, 14, loc.clone(), 2.0D, 3.0D));
    

    this.SleighEnts.add(new SleighPart(4, 44, 7, loc.clone(), -2.0D, -3.0D));
    this.SleighEnts.add(new SleighPart(4, 44, 7, loc.clone(), 2.0D, -3.0D));
    this.SleighEnts.add(new SleighPart(4, 44, 7, loc.clone(), -2.0D, 0.0D));
    this.SleighEnts.add(new SleighPart(4, 44, 7, loc.clone(), 2.0D, 0.0D));
    

    SleighPart santa = new SleighPart(3, 0, 0, loc.clone(), 0.0D, 1.0D);
    santa.AddSanta();
    this.SleighEnts.add(santa);
    
    this.SleighHorses = new ArrayList();
    
    this.SleighHorses.add(new SleighHorse(loc.clone(), -1.5D, 8.0D));
    this.SleighHorses.add(new SleighHorse(loc.clone(), 1.5D, 8.0D));
    
    this.SleighHorses.add(new SleighHorse(loc.clone(), -1.5D, 11.0D));
    this.SleighHorses.add(new SleighHorse(loc.clone(), 1.5D, 11.0D));
    
    for (SleighHorse horse : this.SleighHorses) {
      UtilEnt.Leash(horse.Ent, santa.GetTop(), false, false);
    }
  }
  
  public Location GetLocation() {
    return this.CentralEntity.getLocation();
  }
  
  public void SetTarget(Location loc)
  {
    this.Target = loc;
  }
  
  public void Update()
  {
    Bump();
    
    if (this.Target == null) {
      return;
    }
    Move(this.CentralEntity, this.Target, 1.0D);
    

    for (SleighPart part : this.SleighEnts)
    {
      part.RefreshBlocks();
      
      if ((Move(part.Ent, this.CentralEntity.getLocation().add(part.OffsetX, 0.0D, part.OffsetZ), 1.4D)) && 
        ((part.OffsetZ == -3.0D) || (Math.abs(part.OffsetX) == 2.0D)) && 
        (Math.random() > 0.95D)) {
        part.Ent.getWorld().playEffect(part.Ent.getLocation().subtract(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, 80);
      }
    }
    
    for (SleighHorse ent : this.SleighHorses)
    {
      Move(ent.Ent, this.CentralEntity.getLocation().add(ent.OffsetX, 0.0D, ent.OffsetZ), 1.4D);
    }
  }
  
  public boolean Move(Entity ent, Location target, double speed)
  {
    return UtilEnt.CreatureMoveFast(ent, target, (float)speed);
  }
  
  public void Bump()
  {
    for (Player player : this.Host.GetPlayers(true))
    {
      if (Recharge.Instance.usable(player, "Sleigh Bump"))
      {

        for (SleighPart part : this.SleighEnts) {
          if (UtilMath.offset(player, part.Ent) < 1.0D)
          {
            UtilAction.velocity(player, UtilAlg.getTrajectory2d(this.CentralEntity, player), 0.4D, true, 0.2D, 0.0D, 0.0D, true);
            Recharge.Instance.useForce(player, "Sleigh Bump", 400L);
          }
        }
        
        for (SleighHorse part : this.SleighHorses) {
          if (UtilMath.offset(player, part.Ent) < 1.0D)
          {
            UtilAction.velocity(player, UtilAlg.getTrajectory2d(this.CentralEntity, player), 0.4D, true, 0.2D, 0.0D, 0.0D, true);
            Recharge.Instance.useForce(player, "Sleigh Bump", 400L);
          }
        }
        if (player.getLocation().getZ() < this.CentralEntity.getLocation().getZ() - 24.0D)
        {
          player.damage(1.0D);
          UtilPlayer.message(player, C.cRed + C.Bold + "Santa: " + ChatColor.RESET + "Careful " + player.getName() + "! Keep up with my Sleigh!");
          
          UtilAction.velocity(player, UtilAlg.getTrajectory2d(player, this.CentralEntity), 0.6D, true, 0.2D, 0.0D, 0.0D, true);
          Recharge.Instance.useForce(player, "Sleigh Bump", 400L);
        }
      }
    }
  }
  
  public boolean HasPresent(Location loc) {
    return this.PresentsCollected.contains(loc);
  }
  
  public void AddPresent(Location loc)
  {
    this.PresentsCollected.add(loc);
    loc.getBlock().setType(Material.AIR);
    loc.getBlock().getRelative(BlockFace.DOWN).setType(Material.GLASS);
    UtilFirework.launchFirework(loc.clone().add(0.5D, 0.5D, 0.5D), FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).with(FireworkEffect.Type.BALL).trail(true).build(), new Vector(0, 1, 0), 0);
    
    SleighPart part = (SleighPart)this.PresentSlots.remove(0);
    if (part == null) { return;
    }
    part.SetPresent();
  }
  
  public ArrayList<Location> GetPresents()
  {
    return this.PresentsCollected;
  }
  
  public void Damage(CustomDamageEvent event)
  {
    if (event.GetDamageeEntity().equals(this.CentralEntity))
    {
      event.SetCancelled("Sleigh Damage");
      return;
    }
    
    for (SleighPart part : this.SleighEnts)
    {
      if (part.HasEntity(event.GetDamageeEntity()))
      {
        event.SetCancelled("Sleigh Damage");
        return;
      }
    }
    
    for (SleighHorse part : this.SleighHorses)
    {
      if (part.HasEntity(event.GetDamageeEntity()))
      {
        event.SetCancelled("Sleigh Damage");
        return;
      }
    }
  }
}
