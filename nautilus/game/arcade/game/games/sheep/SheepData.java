package nautilus.game.arcade.game.games.sheep;

import java.util.ArrayList;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilDisplay;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilFirework;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.GameTeam;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;


public class SheepData
{
  public SheepGame Host;
  public Sheep Sheep;
  public long LastMoved = 0L;
  
  public Player Holder = null;
  public GameTeam HolderTeam = null;
  
  public GameTeam Owner = null;
  public ArrayList<Block> OwnerPen = null;
  public Location OwnerPenMiddle = null;
  
  public Location StuckLocation = null;
  public long StuckTime = System.currentTimeMillis();
  
  public Location Target = null;
  
  public SheepData(SheepGame host, Sheep sheep)
  {
    this.Host = host;
    this.Sheep = sheep;
    
    this.LastMoved = System.currentTimeMillis();
    
    this.StuckLocation = this.Sheep.getLocation();
    this.StuckTime = System.currentTimeMillis();
    
    UtilEnt.Vegetate(this.Sheep);
    UtilEnt.ghost(this.Sheep, true, false);
  }
  
  public void SetHolder(Player player)
  {
    this.Holder = player;
    this.HolderTeam = this.Host.GetTeam(player);
  }
  

  public void SetOwner(GameTeam team, ArrayList<Block> locs)
  {
    if ((this.Sheep.getVehicle() != null) && 
      (this.HolderTeam != null) && (!this.HolderTeam.equals(team))) {
      return;
    }
    this.Sheep.leaveVehicle();
    
    this.Target = null;
    

    if ((this.Owner != null) && (this.Owner.equals(team))) {
      return;
    }
    this.Owner = team;
    this.OwnerPen = locs;
    

    Vector vec = new Vector(0, 0, 0);
    for (Block block : locs)
      vec.add(block.getLocation().toVector());
    vec.multiply(1.0D / locs.size());
    this.OwnerPenMiddle = vec.toLocation(((Block)this.OwnerPen.get(0)).getWorld());
    this.OwnerPenMiddle.add(0.5D, 0.0D, 0.5D);
    
    this.Sheep.setColor(DyeColor.getByWoolData(team.GetColorData()));
    
    this.StuckLocation = this.Sheep.getLocation();
    this.StuckTime = System.currentTimeMillis();
    

    this.Sheep.getWorld().playSound(this.Sheep.getLocation(), Sound.SHEEP_IDLE, 2.0F, 1.5F);
    UtilFirework.playFirework(this.Sheep.getLocation().add(0.0D, 0.5D, 0.0D), FireworkEffect.builder().flicker(false).withColor(team.GetColorBase()).with(FireworkEffect.Type.BALL).trail(false).build());
    

    if ((this.Holder != null) && (this.HolderTeam != null))
    {
      this.Host.AddGems(this.Holder, 3.0D, "Sheep Captured", true);
      
      UtilPlayer.message(this.Holder, F.main("Game", "You captured a Sheep!"));
      UtilDisplay.displayTextBar(this.Host.Manager.GetPlugin(), this.Holder, 0.0D, C.cGreen + C.Bold + "You captured a Sheep!");
    }
    
    this.Holder = null;
    this.HolderTeam = null;
  }
  
  public boolean Update()
  {
    if (!this.Sheep.isValid()) {
      return true;
    }
    this.Host.GetTeamPen(this);
    
    if (this.Sheep.getVehicle() != null) {
      return false;
    }
    
    if ((this.Owner == null) || (this.OwnerPen == null))
    {
      if (UtilMath.offset(this.Sheep.getLocation(), this.Host.GetSheepSpawn()) > 14.0D)
      {
        UtilEnt.CreatureMoveFast(this.Sheep, this.Host.GetSheepSpawn(), 1.2F);
        
        if (UtilMath.offset(this.Sheep.getLocation(), this.StuckLocation) > 1.0D)
        {
          this.StuckLocation = this.Sheep.getLocation();
          this.StuckTime = System.currentTimeMillis();


        }
        else if (UtilTime.elapsed(this.StuckTime, 1000L))
        {
          UtilAction.velocity(this.Sheep, 0.3D, 0.3D, 0.7D, true);
        }
        
      }
      

    }
    else if (IsInsideOwnPen())
    {
      this.LastMoved = System.currentTimeMillis();
      

      if (this.Target == null) {
        this.Target = ((Block)UtilAlg.Random(this.OwnerPen)).getLocation().add(0.5D, 0.0D, 0.5D);
      }
      if (UtilMath.offset(this.Sheep.getLocation(), this.Target) < 1.0D) {
        this.Target = ((Block)UtilAlg.Random(this.OwnerPen)).getLocation().add(0.5D, 0.0D, 0.5D);
      }
      UtilEnt.CreatureMoveFast(this.Sheep, this.Target, 0.8F);
    }
    else
    {
      UtilEnt.CreatureMoveFast(this.Sheep, this.OwnerPenMiddle, 1.2F);
      
      if (UtilMath.offset(this.Sheep.getLocation(), this.StuckLocation) > 1.0D)
      {
        this.StuckLocation = this.Sheep.getLocation();
        this.StuckTime = System.currentTimeMillis();


      }
      else if (UtilTime.elapsed(this.StuckTime, 1000L))
      {
        UtilAction.velocity(this.Sheep, 0.3D, 0.3D, 0.7D, true);
      }
    }
    


    return false;
  }
  
  public Block SheepBlock()
  {
    Entity ent = this.Sheep;
    
    while (ent.getVehicle() != null)
    {
      ent = ent.getVehicle();
    }
    
    return ent.getLocation().getBlock();
  }
  
  public boolean IsInsideOwnPen()
  {
    if (this.OwnerPen == null) {
      return false;
    }
    return this.OwnerPen.contains(this.Sheep.getLocation().getBlock());
  }
}
