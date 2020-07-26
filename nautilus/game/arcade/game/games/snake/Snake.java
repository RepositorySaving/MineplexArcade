package nautilus.game.arcade.game.games.snake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilTime;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseBase;
import mineplex.core.disguise.disguises.DisguiseSheep;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GamePrepareCountdownCommence;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.snake.kits.KitInvulnerable;
import nautilus.game.arcade.game.games.snake.kits.KitSpeed;
import nautilus.game.arcade.kit.Kit;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.Navigation;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Snake extends SoloGame
{
  private double _maxSpeed = 180.0D;
  
  private HashMap<Player, ArrayList<Creature>> _tail = new HashMap();
  private HashSet<Entity> _food = new HashSet();
  
  private HashMap<Player, DyeColor> _color = new HashMap();
  
  private HashMap<Player, Long> _invul = new HashMap();
  private HashMap<Player, Long> _speed = new HashMap();
  
  private HashMap<Player, Location> _move = new HashMap();
  private HashMap<Player, Long> _moveTime = new HashMap();
  














  public Snake(ArcadeManager manager)
  {
    super(manager, GameType.Snake, new Kit[] {new KitSpeed(manager), new KitInvulnerable(manager) }, new String[] {"Avoid hitting snake tails", "You get faster as you grow longer", "Eat slimes to grow faster", "Last one alive wins!" });
    

    this.DamageTeamSelf = true;
    
    this.HungerSet = 2;
    
    this.GemMultiplier = 0.5D;
  }
  
  @EventHandler
  public void CreateSheep(GamePrepareCountdownCommence event) {
    Player player;
    for (int i = 0; i < GetPlayers(true).size(); i++)
    {
      player = (Player)GetPlayers(true).get(i);
      this._color.put(player, DyeColor.getByDyeData((byte)(i % 16)));
      
      this.CreatureAllowOverride = true;
      Sheep sheep = (Sheep)player.getWorld().spawn(player.getLocation(), Sheep.class);
      this.CreatureAllowOverride = false;
      
      sheep.setColor(DyeColor.getByDyeData((byte)(i % 16)));
      sheep.setPassenger(player);
      
      UtilEnt.Vegetate(sheep);
      
      this._tail.put(player, new ArrayList());
      ((ArrayList)this._tail.get(player)).add(sheep);
    }
    
    for (Player player : GetPlayers(true))
    {
      player.playEffect(GetSpectatorLocation(), Effect.RECORD_PLAY, 2259);
    }
  }
  
  @EventHandler
  public void ThirdPerson(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    Announce(C.cYellow + C.Scramble + "@@" + C.cAqua + C.Bold + " Snake is best played in 3rd Person! (Push F5) " + C.cYellow + C.Scramble + "@@");
  }
  
  @EventHandler
  public void AutoGrow(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      Grow(player, 1, false);
    }
  }
  
  @EventHandler
  public void ReSit(UpdateEvent event)
  {
    if (!InProgress()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      if (player.getVehicle() == null)
      {

        if (this._tail.containsKey(player))
        {

          if (!((ArrayList)this._tail.get(player)).isEmpty())
          {

            ((Creature)((ArrayList)this._tail.get(player)).get(0)).setPassenger(player); } }
      }
    }
  }
  
  @EventHandler
  public void Move(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      if (this._tail.containsKey(player))
      {

        double mult = 0.4D;
        
        if (this._tail.containsKey(player)) {
          mult += Math.min(0.7D, ((ArrayList)this._tail.get(player)).size() / this._maxSpeed);
        }
        if (this._speed.containsKey(player)) {
          mult *= 1.5D;
        }
        Vector vel = player.getLocation().getDirection().setY(0).normalize().multiply(4);
        
        Creature before = null;
        for (int i = 0; i < ((ArrayList)this._tail.get(player)).size(); i++)
        {
          Creature tail = (Creature)((ArrayList)this._tail.get(player)).get(i);
          
          Location loc = player.getLocation().add(vel);
          

          if (i == 0) {
            loc = tail.getLocation().add(vel);
          }
          
          if (before != null) {
            loc = before.getLocation();
          }
          if (UtilMath.offset(loc, tail.getLocation()) > 12.0D) {
            loc = tail.getLocation().add(UtilAlg.getTrajectory(tail.getLocation(), loc).multiply(12));
          }
          
          if (before != null)
          {
            Location tp = before.getLocation().add(UtilAlg.getTrajectory2d(before, tail).multiply(1.4D));
            tp.setPitch(tail.getLocation().getPitch());
            tp.setYaw(tail.getLocation().getYaw());
            tail.teleport(tp);
          }
          

          EntityCreature ec = ((CraftCreature)tail).getHandle();
          Navigation nav = ec.getNavigation();
          nav.a(loc.getX(), loc.getY(), loc.getZ(), (1.0D + 2.0D * mult) * 1.0D);
          

          before = tail;
        }
      }
    }
  }
  
  @EventHandler
  public void Idle(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      if (!this._tail.containsKey(player))
      {
        KillPlayer(player, null, "No Tail");


      }
      else if ((!this._move.containsKey(player)) || (UtilMath.offset(((Creature)((ArrayList)this._tail.get(player)).get(0)).getLocation(), (Location)this._move.get(player)) > 2.0D))
      {
        this._move.put(player, ((Creature)((ArrayList)this._tail.get(player)).get(0)).getLocation());
        this._moveTime.put(player, Long.valueOf(System.currentTimeMillis()));


      }
      else if (UtilTime.elapsed(((Long)this._moveTime.get(player)).longValue(), 2000L)) {
        KillPlayer(player, null, "Idle");
      }
    }
  }
  
  @EventHandler
  public void Collide(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    
    for (Player player : GetPlayers(true))
    {
      if (!this._invul.containsKey(player))
      {

        boolean done = false;
        for (Player other : this._tail.keySet())
        {
          int start = 0;
          if (other.equals(player)) {
            start = 3;
          }
          for (int i = start; i < ((ArrayList)this._tail.get(other)).size(); i++)
          {
            Creature tail = (Creature)((ArrayList)this._tail.get(other)).get(i);
            
            if (UtilMath.offset((Entity)((ArrayList)this._tail.get(player)).get(0), tail) < 1.2D)
            {
              KillPlayer(player, other, null);
              
              done = true;
              break;
            }
          }
          
          if (done)
            break;
        }
      }
    }
  }
  
  public void KillPlayer(Player player, Player killer, String type) {
    if (killer != null)
    {

      this.Manager.GetDamage().NewDamageEvent(player, killer, null, 
        EntityDamageEvent.DamageCause.CUSTOM, 500.0D, false, true, false, 
        killer.getName(), "Snake Tail");

    }
    else
    {
      this.Manager.GetDamage().NewDamageEvent(player, null, null, 
        EntityDamageEvent.DamageCause.CUSTOM, 500.0D, false, true, false, 
        type, type);
    }
    
    if (this._tail.containsKey(player))
    {
      for (Creature cur : (ArrayList)this._tail.get(player))
      {
        cur.playEffect(EntityEffect.HURT);
        cur.remove();
      }
      
      ((ArrayList)this._tail.get(player)).clear();
    }
  }
  
  @EventHandler
  public void SpawnFood(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    Location loc = (Location)((GameTeam)GetTeamList().get(0)).GetSpawns().get(UtilMath.r(((GameTeam)GetTeamList().get(0)).GetSpawns().size()));
    
    loc.setX(-48 + UtilMath.r(97));
    loc.setZ(-48 + UtilMath.r(97));
    
    if (!UtilBlock.airFoliage(loc.getBlock())) {
      return;
    }
    if (UtilMath.offset(loc, GetSpectatorLocation()) > 48.0D) {
      return;
    }
    
    this.CreatureAllowOverride = true;
    Slime pig = (Slime)loc.getWorld().spawn(loc, Slime.class);
    this.CreatureAllowOverride = false;
    pig.setSize(2);
    UtilEnt.Vegetate(pig);
    
    this._food.add(pig);
  }
  
  @EventHandler
  public void EatFood(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Entity> foodIterator = this._food.iterator();
    
    while (foodIterator.hasNext())
    {
      Entity food = (Entity)foodIterator.next();
      
      if (!food.isValid())
      {
        food.remove();
        foodIterator.remove();
      }
      else
      {
        for (Player player : GetPlayers(true))
        {
          if (UtilMath.offset(food, player) < 2.0D)
          {
            int amount = 2;
            
            Grow(player, amount, true);
            foodIterator.remove();
            food.remove();
            break;
          }
        }
      }
    }
  }
  
  public void Grow(Player player, int amount, boolean sound) {
    while (amount > 0)
    {

      Location loc = player.getLocation();
      if (!((ArrayList)this._tail.get(player)).isEmpty()) {
        loc = ((Creature)((ArrayList)this._tail.get(player)).get(((ArrayList)this._tail.get(player)).size() - 1)).getLocation();
      }
      if (((ArrayList)this._tail.get(player)).size() > 1) {
        loc.add(UtilAlg.getTrajectory2d((Entity)((ArrayList)this._tail.get(player)).get(((ArrayList)this._tail.get(player)).size() - 2), (Entity)((ArrayList)this._tail.get(player)).get(((ArrayList)this._tail.get(player)).size() - 1)));
      } else {
        loc.subtract(player.getLocation().getDirection().setY(0));
      }
      
      this.CreatureAllowOverride = true;
      Sheep tail = (Sheep)loc.getWorld().spawn(loc, Sheep.class);
      this.CreatureAllowOverride = false;
      
      tail.setRemoveWhenFarAway(false);
      tail.setColor((DyeColor)this._color.get(player));
      

      tail.teleport(loc);
      
      UtilEnt.Vegetate(tail);
      UtilEnt.ghost(tail, true, false);
      
      ((ArrayList)this._tail.get(player)).add(tail);
      

      if (sound) {
        player.getWorld().playSound(player.getLocation(), Sound.EAT, 2.0F, 1.0F);
      }
      amount--;
    }
    
    player.setExp((float)Math.min(0.9998999834060669D, ((ArrayList)this._tail.get(player)).size() / this._maxSpeed));
  }
  
  @EventHandler
  public void DamageCancel(CustomDamageEvent event)
  {
    if (event.GetCause() == EntityDamageEvent.DamageCause.LAVA)
    {
      if (event.GetDamageePlayer() != null)
      {
        KillPlayer(event.GetDamageePlayer(), null, "Lava");
        return;
      }
    }
    
    if (event.GetCause() != EntityDamageEvent.DamageCause.CUSTOM) {
      event.SetCancelled("Snake Damage");
    }
  }
  
  @EventHandler
  public void TargetCancel(EntityTargetEvent event) {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void CombustCancel(EntityCombustEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void InvulnerabilityUse(PlayerInteractEvent event)
  {
    if (!IsLive()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!IsAlive(player)) {
      return;
    }
    if (!UtilGear.isMat(event.getPlayer().getItemInHand(), Material.WOOL)) {
      return;
    }
    if (!Recharge.Instance.use(player, "Snake Item", 1000L, false, false)) {
      return;
    }
    UtilInv.remove(player, Material.WOOL, (byte)0, 1);
    
    this._invul.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void InvulnerabilityUpdate(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Player> invIterator = this._invul.keySet().iterator();
    
    while (invIterator.hasNext())
    {
      Player player = (Player)invIterator.next();
      
      if (UtilTime.elapsed(((Long)this._invul.get(player)).longValue(), 2000L))
      {

        DisguiseBase disguise = this.Manager.GetDisguise().getDisguise(player);
        if ((disguise != null) && ((disguise instanceof DisguiseSheep))) {
          ((DisguiseSheep)disguise).setColor((DyeColor)this._color.get(player));
        }
        
        if (this._tail.containsKey(player)) {
          for (Creature ent : (ArrayList)this._tail.get(player))
            if ((ent instanceof Sheep))
              ((Sheep)ent).setColor((DyeColor)this._color.get(player));
        }
        invIterator.remove();
      }
      else
      {
        DyeColor col = GetColor();
        

        DisguiseBase disguise = this.Manager.GetDisguise().getDisguise(player);
        if ((disguise != null) && ((disguise instanceof DisguiseSheep))) {
          ((DisguiseSheep)disguise).setColor(col);
        }
        
        if (this._tail.containsKey(player))
          for (Creature ent : (ArrayList)this._tail.get(player))
            if ((ent instanceof Sheep))
              ((Sheep)ent).setColor(col);
      }
    }
  }
  
  public DyeColor GetColor() {
    double r = Math.random();
    
    if (r > 0.75D) return DyeColor.RED;
    if (r > 0.5D) return DyeColor.YELLOW;
    if (r > 0.25D) return DyeColor.GREEN;
    return DyeColor.BLUE;
  }
  
  @EventHandler
  public void SpeedUse(PlayerInteractEvent event)
  {
    if (!IsLive()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!IsAlive(player)) {
      return;
    }
    if (!UtilGear.isMat(event.getPlayer().getItemInHand(), Material.FEATHER)) {
      return;
    }
    if (!Recharge.Instance.use(player, "Snake Item", 1000L, false, false)) {
      return;
    }
    UtilInv.remove(player, Material.FEATHER, (byte)0, 1);
    
    this._speed.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void SpeedUpdate(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Player> speedIterator = this._speed.keySet().iterator();
    
    while (speedIterator.hasNext())
    {
      Player player = (Player)speedIterator.next();
      
      if (UtilTime.elapsed(((Long)this._speed.get(player)).longValue(), 2000L))
      {
        speedIterator.remove();
      }
    }
  }
}
