package nautilus.game.arcade.game.games.mineware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.mineware.order.Order;
import nautilus.game.arcade.game.games.mineware.random.ActionMilkCow;
import nautilus.game.arcade.game.games.mineware.random.ActionShearSheep;
import nautilus.game.arcade.game.games.mineware.random.CraftLadder;
import nautilus.game.arcade.game.games.mineware.random.CraftStoneShovel;
import nautilus.game.arcade.game.games.mineware.random.DamageGhast;
import nautilus.game.arcade.game.games.mineware.random.GatherYellowFlower;
import nautilus.game.arcade.game.games.mineware.random.PlaceDoor;
import nautilus.game.arcade.game.games.mineware.random.RideBoat;
import nautilus.game.arcade.game.games.mineware.random.StandAlone;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

public class MineWare extends SoloGame
{
  private HashMap<Player, Integer> _lives = new HashMap();
  
  private Order _order = null;
  private long _orderTime = 0L;
  private int _orderCount = 0;
  
  private ArrayList<Order> _orders = new ArrayList();
  private ArrayList<Order> _ordersCopy = new ArrayList();
  
  private Location _ghastLoc = null;
  private Location _ghastTarget = null;
  private Ghast _ghast = null;
  private ArrayList<Location> _mobLocs = new ArrayList();
  private ArrayList<Creature> _mobs = new ArrayList();
  












  public MineWare(ArcadeManager manager)
  {
    super(manager, GameType.MineWare, new nautilus.game.arcade.kit.Kit[] {new nautilus.game.arcade.game.games.spleef.kits.KitLeaper(manager) }, new String[] {"Follow the orders given in chat!", "First half to follow it win the round.", "Other players lose one life.", "Last player with lives wins!" });
    

    this.PrepareFreeze = false;
    
    this.DamagePvP = false;
    
    this.BlockPlace = true;
    this.BlockBreak = true;
    
    PopulateOrders();
  }
  

  public void ParseData()
  {
    this._ghastLoc = ((Location)this.WorldData.GetDataLocs("WHITE").get(0));
    
    while (this._mobLocs.size() < 100)
    {
      Location loc = this.WorldData.GetRandomXZ();
      
      while (mineplex.core.common.util.UtilBlock.airFoliage(loc.getBlock())) {
        loc.add(0.0D, -1.0D, 0.0D);
      }
      Material mat = loc.getBlock().getType();
      
      if ((mat == Material.STONE) || 
        (mat == Material.GRASS) || 
        (mat == Material.SAND)) {
        this._mobLocs.add(loc);
      }
    }
  }
  
  public void PopulateOrders() {
    this._orders.add(new ActionMilkCow(this));
    this._orders.add(new ActionShearSheep(this));
    
    this._orders.add(new CraftLadder(this));
    this._orders.add(new CraftStoneShovel(this));
    
    this._orders.add(new nautilus.game.arcade.game.games.mineware.random.DamageChicken(this));
    this._orders.add(new nautilus.game.arcade.game.games.mineware.random.DamageFall(this));
    this._orders.add(new DamageGhast(this));
    
    this._orders.add(new nautilus.game.arcade.game.games.mineware.random.GatherCobble(this));
    this._orders.add(new nautilus.game.arcade.game.games.mineware.random.GatherRedFlower(this));
    this._orders.add(new GatherYellowFlower(this));
    this._orders.add(new nautilus.game.arcade.game.games.mineware.random.GatherSand(this));
    
    this._orders.add(new PlaceDoor(this));
    
    this._orders.add(new RideBoat(this));
    this._orders.add(new nautilus.game.arcade.game.games.mineware.random.RidePig(this));
    
    this._orders.add(new StandAlone(this));
    this._orders.add(new nautilus.game.arcade.game.games.mineware.random.StandShelter(this));
    this._orders.add(new nautilus.game.arcade.game.games.mineware.random.StandStone(this));
    this._orders.add(new nautilus.game.arcade.game.games.mineware.random.StandWater(this));
  }
  
  public Order GetOrder()
  {
    if (this._ordersCopy.isEmpty())
    {
      for (Order order : this._orders)
      {
        this._ordersCopy.add(order);
      }
    }
    
    return (Order)this._ordersCopy.remove(UtilMath.r(this._ordersCopy.size()));
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void GameStateChange(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    for (Player player : GetPlayers(true)) {
      this._lives.put(player, Integer.valueOf(10));
    }
  }
  
  @EventHandler
  public void UpdateOrder(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    
    if (this._order == null)
    {
      if (!mineplex.core.common.util.UtilTime.elapsed(this._orderTime, 1000L)) {
        return;
      }
      this._order = GetOrder();
      
      if (this._order == null)
      {
        SetState(Game.GameState.Dead);
        return;
      }
      

      UtilServer.getServer().getPluginManager().registerEvents(this._order, this.Manager.GetPlugin());
      this._order.StartOrder(this._orderCount++);
      
      Announce(C.cYellow + C.Bold + this._order.GetOrder().toUpperCase());
      
      GetObjectiveSide().setDisplayName(
        org.bukkit.ChatColor.WHITE + "§lMineWare " + C.cGreen + "§l" + 
        "Round " + this._orderCount);



    }
    else if (this._order.Finish())
    {
      this._orderTime = System.currentTimeMillis();
      
      if (this._order.PlayerHasCompleted())
      {
        for (Player player : GetPlayers(true))
        {
          if (!this._order.IsCompleted(player))
          {
            LoseLife(player);
            
            if (IsAlive(player)) {
              this._order.FailItems(player);
            }
          }
        }
      }
      

      HandlerList.unregisterAll(this._order);
      this._order.EndOrder();
      this._order = null;

    }
    else
    {
      for (Player player : UtilServer.getPlayers())
      {
        player.setLevel(this._order.GetRemainingPlaces());
        player.setExp(this._order.GetTimeLeftPercent());
      }
    }
  }
  

  private int GetLives(Player player)
  {
    if (!this._lives.containsKey(player)) {
      return 0;
    }
    if (!IsAlive(player)) {
      return 0;
    }
    return ((Integer)this._lives.get(player)).intValue();
  }
  
  private void LoseLife(Player player)
  {
    int lives = GetLives(player) - 1;
    
    if (lives > 0)
    {
      UtilPlayer.message(player, C.cRed + C.Bold + "You failed the task!");
      UtilPlayer.message(player, C.cRed + C.Bold + "You have " + lives + " lives left!");
      player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 2.0F, 0.5F);
      
      this._lives.put(player, Integer.valueOf(lives));
    }
    else
    {
      UtilPlayer.message(player, C.cRed + C.Bold + "You are out of the game!");
      player.playSound(player.getLocation(), Sound.EXPLODE, 2.0F, 1.0F);
      
      player.damage(5000.0D);
      
      GetScoreboard().resetScores(player);
    }
  }
  
  @EventHandler
  public void UpdateMobs(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!InProgress()) {
      return;
    }
    Iterator<Creature> mobIterator = this._mobs.iterator();
    
    while (mobIterator.hasNext())
    {
      Creature mob = (Creature)mobIterator.next();
      
      if (!mob.isValid())
      {
        mob.remove();
        mobIterator.remove();
      }
    }
    
    if (this._mobs.size() < 200)
    {
      Location loc = ((Location)this._mobLocs.get(UtilMath.r(this._mobLocs.size()))).clone().add(new Vector(0.5D, 1.0D, 0.5D));
      double r = Math.random();
      
      this.CreatureAllowOverride = true;
      
      if (r > 0.75D) { this._mobs.add((Creature)loc.getWorld().spawn(loc, org.bukkit.entity.Pig.class));
      } else if (r > 0.5D) { this._mobs.add((Creature)loc.getWorld().spawn(loc, org.bukkit.entity.Cow.class));
      } else if (r > 0.25D) this._mobs.add((Creature)loc.getWorld().spawn(loc, org.bukkit.entity.Chicken.class)); else {
        this._mobs.add((Creature)loc.getWorld().spawn(loc, org.bukkit.entity.Sheep.class));
      }
      this.CreatureAllowOverride = false;
    }
    
    if ((this._ghast == null) || (!this._ghast.isValid()))
    {
      if (this._ghast != null) {
        this._ghast.remove();
      }
      this.CreatureAllowOverride = true;
      this._ghast = ((Ghast)this._ghastLoc.getWorld().spawn(this._ghastLoc, Ghast.class));
      this.CreatureAllowOverride = false;
      
      this._ghast.setMaxHealth(10000.0D);
      this._ghast.setHealth(this._ghast.getMaxHealth());

    }
    else
    {
      if ((this._ghastTarget == null) || (UtilMath.offset(this._ghast.getLocation(), this._ghastTarget) < 5.0D))
      {
        this._ghastTarget = this._ghastLoc.clone().add(40.0D - 80.0D * Math.random(), -20.0D * Math.random(), 40.0D - 80.0D * Math.random());
      }
      
      this._ghast.teleport(this._ghast.getLocation().add(mineplex.core.common.util.UtilAlg.getTrajectory(this._ghast.getLocation(), this._ghastTarget).multiply(0.1D)));
    }
  }
  
  @EventHandler
  public void GhastTarget(EntityTargetEvent event)
  {
    if (event.getEntity().equals(this._ghast)) {
      event.setCancelled(true);
    }
  }
  
  public int GetScoreboardScore(Player player)
  {
    return GetLives(player);
  }
  
  @EventHandler
  public void ItemDrop(PlayerDropItemEvent event)
  {
    event.getItemDrop().remove();
  }
}
