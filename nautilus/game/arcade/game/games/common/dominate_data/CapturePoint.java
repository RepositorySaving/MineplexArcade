package nautilus.game.arcade.game.games.common.dominate_data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilFirework;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.games.common.Domination;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;



public class CapturePoint
{
  private Domination Host;
  private String _name;
  private ArrayList<Block> _floor = new ArrayList();
  private ArrayList<Block> _indicators = new ArrayList();
  
  private Location _loc;
  
  private double _captureMax = 24.0D;
  private double _captureRate = 1.0D;
  private double _captureAmount = 0.0D;
  private GameTeam _owner = null;
  private boolean _captured = false;
  private ArrayList<Block> _captureFloor = new ArrayList();
  private long _decayDelay = 0L;
  
  private int _indicatorTick = 0;
  
  private ChatColor _scoreboardColor = ChatColor.WHITE;
  private int _scoreboardTick = 0;
  
  public CapturePoint(Domination host, String name, Location loc)
  {
    this.Host = host;
    
    this._name = name;
    
    for (int x = -3; x <= 3; x++)
    {
      for (int z = -3; z <= 3; z++)
      {

        if ((Math.abs(x) == 3) && (Math.abs(z) == 3))
        {
          Block ind = loc.getBlock().getRelative(x, 3, z);
          ind.setType(Material.WOOL);
          this._indicators.add(ind);
        }
        

        if ((Math.abs(x) <= 2) && (Math.abs(z) <= 2))
        {
          if ((x != 0) || (z != 0))
          {
            Block floor = loc.getBlock().getRelative(x, -2, z);
            floor.setType(Material.WOOL);
            this._floor.add(floor);
          }
          else
          {
            Block block = loc.getBlock().getRelative(x, -1, z);
            block.setType(Material.BEACON);
          }
        }
        

        if ((Math.abs(x) <= 2) && (Math.abs(z) <= 2))
        {
          Block block = loc.getBlock().getRelative(x, -1, z);
          block.setType(Material.GLASS);
        }
        

        if ((Math.abs(x) <= 1) && (Math.abs(z) <= 1))
        {
          Block block = loc.getBlock().getRelative(x, -3, z);
          block.setType(Material.IRON_BLOCK);
        }
      }
    }
    

    this._loc = loc;
  }
  
  public void Update()
  {
    CaptureUpdate();
    Points();
  }
  
  private void Points()
  {
    if (!this._captured) {
      return;
    }
    this.Host.AddScore(this._owner, 4);
  }
  

  private void CaptureUpdate()
  {
    GameTeam teamA = null;
    ArrayList<Player> playersA = new ArrayList();
    
    GameTeam teamB = null;
    ArrayList<Player> playersB = new ArrayList();
    Iterator localIterator2;
    for (Iterator localIterator1 = this.Host.GetTeamList().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      GameTeam team = (GameTeam)localIterator1.next();
      
      localIterator2 = team.GetPlayers(true).iterator(); continue;Player player = (Player)localIterator2.next();
      
      if (player.getGameMode() == GameMode.SURVIVAL)
      {

        if (Math.abs(this._loc.getX() - player.getLocation().getX()) <= 2.5D)
        {

          if (Math.abs(this._loc.getY() - player.getLocation().getY()) <= 2.5D)
          {

            if (Math.abs(this._loc.getZ() - player.getLocation().getZ()) <= 2.5D)
            {

              if ((teamA == null) || (teamA.equals(team)))
              {
                teamA = team;
                playersA.add(player);
              }
              else
              {
                teamB = team;
                playersB.add(player);
              } }
          }
        }
      }
    }
    if (teamA == null)
    {
      if (this._captureAmount > 0.0D) {
        RegenDegen();
      }
      return;
    }
    


    if (teamB == null) {
      Capture(teamA, playersA.size(), playersA);
    }
    else if (playersA.size() > playersB.size()) {
      Capture(teamA, playersA.size() - playersB.size(), playersA);
    }
    else if (playersB.size() > playersA.size()) {
      Capture(teamB, playersB.size() - playersA.size(), playersB);
    }
  }
  
  private void RegenDegen() {
    if (!UtilTime.elapsed(this._decayDelay, 2000L)) {
      return;
    }
    
    if (!this._captured)
    {
      this._captureAmount = Math.max(0.0D, this._captureAmount - this._captureRate * 1.0D);
      

      while (this._captureFloor.size() / (this._captureFloor.size() + this._floor.size()) > this._captureAmount / this._captureMax)
      {
        Block block = (Block)this._captureFloor.remove(UtilMath.r(this._captureFloor.size()));
        
        this._floor.add(block);
        
        block.setData((byte)0);
      }
      

      if (this._captureAmount == 0.0D)
      {
        this._captured = false;
        this._owner = null;
        

        for (Block block : this._indicators)
        {
          block.setData((byte)0);
        }
      }
      

      for (Block block : this._indicators) {
        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 35);
      }
    }
    else if (this._captureAmount < this._captureMax)
    {
      this._captureAmount = Math.min(this._captureMax, this._captureAmount + this._captureRate * 1.0D);
      

      while (this._captureFloor.size() / (this._captureFloor.size() + this._floor.size()) < this._captureAmount / this._captureMax)
      {
        Block block = (Block)this._floor.remove(UtilMath.r(this._floor.size()));
        
        this._captureFloor.add(block);
        
        if (this._owner.GetColor() == ChatColor.RED) block.setData((byte)14); else {
          block.setData((byte)11);
        }
      }
      
      for (Block block : this._indicators)
      {
        if (this._owner.GetColor() == ChatColor.RED) {
          block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 152);
        } else {
          block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 22);
        }
      }
    }
  }
  
  public void Capture(GameTeam team, int count, Collection<Player> capturers)
  {
    this._scoreboardColor = team.GetColor();
    

    this._decayDelay = System.currentTimeMillis();
    





    Color color = Color.RED;
    if (team.GetColor() == ChatColor.BLUE) {
      color = Color.BLUE;
    }
    Block block;
    if ((this._owner != null) && (this._owner.equals(team)))
    {

      int bonus = 0;
      if (this._captured) {
        bonus = 1;
      }
      this._captureAmount = Math.min(this._captureMax, this._captureAmount + (this._captureRate * count + bonus));
      

      while (this._captureFloor.size() / (this._captureFloor.size() + this._floor.size()) < this._captureAmount / this._captureMax)
      {
        Block block = (Block)this._floor.remove(UtilMath.r(this._floor.size()));
        
        this._captureFloor.add(block);
        
        if (team.GetColor() == ChatColor.RED) block.setData((byte)14); else {
          block.setData((byte)11);
        }
      }
      
      if ((this._captureAmount == this._captureMax) && (!this._captured))
      {
        this._captured = true;
        


        Firework(this._loc, color, true);
        

        for (Block block : this._indicators)
        {
          if (team.GetColor() == ChatColor.RED) block.setData((byte)14); else {
            block.setData((byte)11);
          }
        }
        
        if (capturers != null)
        {
          for (Player player : capturers)
          {
            RewardCapture(player, 30);
          }
          
        }
        
      }
    }
    else
    {
      int bonus = 0;
      if (!this._captured) {
        bonus = 1;
      }
      this._captureAmount = Math.max(0.0D, this._captureAmount - (this._captureRate * count + bonus));
      

      if ((this._owner != null) && (this._captureFloor.size() >= 24))
      {
        for (Player player : this._owner.GetPlayers(false))
        {
          UtilPlayer.message(player, C.Bold + this._name + " is being captured...");
          player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 0.6F, 0.6F);
        }
      }
      

      while (this._captureFloor.size() / (this._captureFloor.size() + this._floor.size()) > this._captureAmount / this._captureMax)
      {
        Block block = (Block)this._captureFloor.remove(UtilMath.r(this._captureFloor.size()));
        
        this._floor.add(block);
        
        block.setData((byte)0);
      }
      

      if (this._captureAmount == 0.0D)
      {
        this._captured = false;
        this._owner = team;
        

        for (??? = this._indicators.iterator(); ???.hasNext();) { block = (Block)???.next();
          
          block.setData((byte)0);
        }
      }
    }
    
    if (this._captureAmount != this._captureMax)
    {

      if (capturers != null)
      {
        for (Player player : capturers)
        {
          RewardCapture(player, 1);
        }
      }
      
      Indicate(color);
    }
  }
  

  public void Firework(Location loc, Color color, boolean major)
  {
    if (!major) {
      UtilFirework.playFirework(loc, FireworkEffect.builder().flicker(false).withColor(color).with(FireworkEffect.Type.BURST).trail(false).build());
    } else {
      UtilFirework.playFirework(loc, FireworkEffect.builder().flicker(true).withColor(color).with(FireworkEffect.Type.BALL_LARGE).trail(true).build());
    }
  }
  
  public void Indicate(Color color)
  {
    for (Block block : this._indicators) {
      if (color == Color.RED) {
        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 152);
      } else {
        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 22);
      }
    }
    

    this._indicatorTick = ((this._indicatorTick + 1) % this._indicators.size());
  }
  
  public String GetScoreboardName()
  {
    this._scoreboardTick = ((this._scoreboardTick + 1) % 2);
    
    String out = "";
    
    if ((this._scoreboardColor != null) && (this._scoreboardTick == 0))
    {
      if (this._scoreboardColor == ChatColor.BLUE) {
        this._scoreboardColor = ChatColor.AQUA;
      }
      out = this._scoreboardColor + C.Bold + this._name;
      this._scoreboardColor = null;


    }
    else if (this._captured)
    {
      if (this._owner.GetColor() == ChatColor.BLUE) {
        out = ChatColor.AQUA + this._name;
      } else {
        out = this._owner.GetColor() + this._name;
      }
    }
    else {
      out = this._name;
    }
    
    if (out.length() > 16) {
      out = out.substring(0, 16);
    }
    return out;
  }
  
  public void RewardCapture(Player player, int amount)
  {
    this.Host.AddGems(player, amount / 25.0D, "Control Point Score", true);
    this.Host.GetStats(player).CaptureScore += amount;
  }
}
