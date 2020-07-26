package nautilus.game.arcade.game.games.sheep;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilDisplay;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilFirework;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilTime.TimeUnit;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.Condition.ConditionType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerGameRespawnEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.games.sheep.kits.KitArcher;
import nautilus.game.arcade.game.games.sheep.kits.KitBeserker;
import nautilus.game.arcade.game.games.sheep.kits.KitBrute;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
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
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class SheepGame extends TeamGame
{
  private ArrayList<String> _lastScoreboard = new ArrayList();
  
  private HashMap<GameTeam, Integer> _teamScore = new HashMap();
  
  private HashMap<GameTeam, ArrayList<Block>> _sheepPens = new HashMap();
  
  private ArrayList<Location> _sheepSpawns;
  private HashMap<Sheep, SheepData> _sheep = new HashMap();
  private long _sheepTimer = System.currentTimeMillis();
  private long _sheepDelay = 20000L;
  
  private long _gameTime = 300000L;
  private long _gameEndAnnounce = 0L;
  













  public SheepGame(ArcadeManager manager)
  {
    super(manager, GameType.Sheep, new Kit[] {new KitBeserker(manager), new KitArcher(manager), new KitBrute(manager) }, new String[] {C.cYellow + "Right-Click" + C.cGray + " with Saddle to " + C.cGreen + "Grab Sheep", "Return Sheep to your Team Pen!", "Most sheep at 5 minutes wins!" });
    

    this.DeathOut = false;
    this.DeathSpectateSecs = 8.0D;
    
    this.HungerSet = 20;
    
    this.WorldTimeSet = 2000;
  }
  

  public void ParseData()
  {
    for (GameTeam team : GetTeamList())
    {
      ArrayList<Location> locs = null;
      
      if (team.GetColor() == ChatColor.RED) {
        locs = this.WorldData.GetDataLocs("RED");
      } else if (team.GetColor() == ChatColor.AQUA) {
        locs = this.WorldData.GetDataLocs("BLUE");
      } else if (team.GetColor() == ChatColor.YELLOW) {
        locs = this.WorldData.GetDataLocs("YELLOW");
      } else if (team.GetColor() == ChatColor.GREEN) {
        locs = this.WorldData.GetDataLocs("GREEN");
      }
      if (locs == null)
      {
        System.out.println("ERROR! Could not find Sheep Pen for Team " + team.GetColor().toString());
        return;
      }
      
      ArrayList<Block> blocks = new ArrayList();
      for (Location loc : locs) {
        blocks.add(loc.getBlock());
      }
      this._sheepPens.put(team, blocks);
    }
    
    this._sheepSpawns = this.WorldData.GetDataLocs("WHITE");
  }
  
  @EventHandler
  public void SheepSpawnStart(GameStateChangeEvent event)
  {
    if (event.GetState() == Game.GameState.Live) {
      for (int i = 0; i < 4; i++)
        SheepSpawn();
    }
  }
  
  @EventHandler
  public void SheepSpawnUpdate(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!UtilTime.elapsed(this._sheepTimer, this._sheepDelay)) {
      return;
    }
    SheepSpawn();
  }
  

  public void SheepSpawn()
  {
    this.CreatureAllowOverride = true;
    Sheep sheep = (Sheep)((Location)this._sheepSpawns.get(0)).getWorld().spawn((Location)UtilAlg.Random(this._sheepSpawns), Sheep.class);
    sheep.setAdult();
    sheep.setMaxHealth(9999.0D);
    sheep.setHealth(9999.0D);
    this.CreatureAllowOverride = false;
    
    this._sheep.put(sheep, new SheepData(this, sheep));
    
    this._sheepTimer = System.currentTimeMillis();
    

    sheep.getWorld().playSound(sheep.getLocation(), Sound.SHEEP_IDLE, 2.0F, 1.5F);
    UtilFirework.playFirework(sheep.getLocation().add(0.0D, 0.5D, 0.0D), FireworkEffect.builder().flicker(false).withColor(Color.WHITE).with(FireworkEffect.Type.BALL).trail(false).build());
  }
  
  @EventHandler
  public void Stack(PlayerInteractEntityEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (!(event.getRightClicked() instanceof Sheep)) {
      return;
    }
    if (event.getRightClicked().getVehicle() != null) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!Recharge.Instance.usable(player, "Sheep Stack")) {
      return;
    }
    if ((player.getItemInHand() != null) && (player.getItemInHand().getType() != Material.SADDLE)) {
      return;
    }
    if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
      return;
    }
    if (!IsAlive(event.getPlayer())) {
      return;
    }
    if ((player.getPassenger() != null) && ((player.getPassenger() instanceof Player)))
    {
      DropSheep(player);
    }
    

    int count = 0;
    Entity top = player;
    while (top.getPassenger() != null)
    {
      top = top.getPassenger();
      count++;
    }
    
    if (count >= 3)
    {
      UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot hold more than 3 Sheep!"));
      return;
    }
    
    for (SheepData data : this._sheep.values())
    {
      if (data.Sheep.equals(event.getRightClicked()))
      {
        if ((data.Owner != null) && (data.Owner.equals(GetTeam(event.getPlayer()))) && (data.IsInsideOwnPen()))
        {
          UtilPlayer.message(event.getPlayer(), F.main("Game", "You have already captured this Sheep!"));
          return;
        }
        
        data.SetHolder(event.getPlayer());
      }
    }
    

    player.getInventory().setItem(4 + count, ItemStackFactory.Instance.CreateStack(35, ((Sheep)event.getRightClicked()).getColor().getWoolData()));
    UtilInv.Update(player);
    

    event.getRightClicked().getWorld().playEffect(event.getRightClicked().getLocation(), Effect.STEP_SOUND, 35);
    

    top.setPassenger(event.getRightClicked());
    

    player.playSound(player.getLocation(), Sound.SHEEP_IDLE, 2.0F, 3.0F);
  }
  
  @EventHandler
  public void StackPlayer(PlayerInteractEntityEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (!(event.getRightClicked() instanceof Player)) {
      return;
    }
    if (event.getRightClicked().getVehicle() != null) {
      return;
    }
    Player player = event.getPlayer();
    Player other = (Player)event.getRightClicked();
    
    if (!(GetKit(player) instanceof KitBrute)) {
      return;
    }
    if (!GetTeam(player).HasPlayer(other)) {
      return;
    }
    if (player.getPassenger() != null) {
      DropSheep(player);
    }
    if (!Recharge.Instance.usable(player, "Sheep Stack")) {
      return;
    }
    if ((player.getItemInHand() != null) && (player.getItemInHand().getType() != Material.SADDLE)) {
      return;
    }
    if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
      return;
    }
    if (!IsAlive(event.getPlayer())) {
      return;
    }
    
    event.getRightClicked().getWorld().playEffect(event.getRightClicked().getLocation(), Effect.STEP_SOUND, 35);
    

    player.setPassenger(other);
    

    player.playSound(player.getLocation(), Sound.VILLAGER_YES, 2.0F, 3.0F);
    other.playSound(player.getLocation(), Sound.VILLAGER_NO, 2.0F, 3.0F);
    

    UtilPlayer.message(other, F.main("Skill", F.elem(new StringBuilder().append(GetTeam(player).GetColor()).append(player.getName()).toString()) + " picked you up."));
    UtilPlayer.message(player, F.main("Skill", "You picked up " + F.elem(new StringBuilder().append(GetTeam(player).GetColor()).append(player.getName()).toString()) + "."));
  }
  
  @EventHandler
  public void DeathDrop(PlayerDeathEvent event)
  {
    DropSheep(event.getEntity());
  }
  
  public void DropSheep(Player player)
  {
    boolean hadSheep = false;
    
    Entity top = player;
    while (top.getPassenger() != null)
    {
      top = top.getPassenger();
      top.leaveVehicle();
      
      hadSheep = true;
    }
    
    if (hadSheep) {
      UtilDisplay.displayTextBar(this.Manager.GetPlugin(), player, 0.0D, C.cRed + C.Bold + "You dropped your Sheep!");
    }
    player.setExp(0.0F);
    
    player.getInventory().remove(Material.WOOL);
    
    this.Manager.GetCondition().EndCondition(player, Condition.ConditionType.SLOW, null);
    

    player.playSound(player.getLocation(), Sound.SHEEP_IDLE, 2.0F, 1.0F);
  }
  
  @EventHandler
  public void Drop(PlayerDropItemEvent event)
  {
    DropSheep(event.getPlayer());
  }
  
  @EventHandler
  public void SheepUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Sheep> sheepIterator = this._sheep.keySet().iterator();
    
    while (sheepIterator.hasNext())
    {
      Sheep sheep = (Sheep)sheepIterator.next();
      SheepData data = (SheepData)this._sheep.get(sheep);
      
      if (data.Update())
      {
        sheep.remove();
        sheepIterator.remove();
      }
    }
  }
  
  @EventHandler
  public void CarryingEffect(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      int count = 0;
      
      Entity top = player;
      while (top.getPassenger() != null)
      {
        top = top.getPassenger();
        count++;
      }
      
      player.setExp(0.33F * count);
      

      if ((count <= 0) && (UtilGear.isMat(player.getInventory().getItem(4), Material.WOOL)))
        player.getInventory().setItem(4, null);
      if ((count <= 1) && (UtilGear.isMat(player.getInventory().getItem(5), Material.WOOL)))
        player.getInventory().setItem(5, null);
      if ((count <= 2) && (UtilGear.isMat(player.getInventory().getItem(6), Material.WOOL))) {
        player.getInventory().setItem(6, null);
      }
      if (count != 0)
      {

        this.Manager.GetCondition().Factory().Slow("Sheep Slow", player, player, 3.0D, count - 1, false, false, false, true);
        
        UtilDisplay.displayTextBar(this.Manager.GetPlugin(), player, count / 3.0F, C.Bold + "Return the Sheep to your Team Pen!");
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void DamagePasson(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetDamageeEntity().getVehicle() == null) {
      return;
    }
    LivingEntity bottom = event.GetDamageeEntity();
    while ((bottom.getVehicle() != null) && ((bottom.getVehicle() instanceof LivingEntity))) {
      bottom = (LivingEntity)bottom.getVehicle();
    }
    event.SetCancelled("Damage Passdown");
    

    this.Manager.GetDamage().NewDamageEvent(bottom, event.GetDamagerEntity(true), event.GetProjectile(), 
      event.GetCause(), event.GetDamageInitial(), true, false, false, 
      UtilEnt.getName(event.GetDamagerEntity(true)), event.GetReason());
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void DamageSuffocate(CustomDamageEvent event)
  {
    if (event.GetCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
      event.SetCancelled("Sheep Game");
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void DamageUnstack(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if ((event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) && (event.GetCause() != EntityDamageEvent.DamageCause.PROJECTILE) && (event.GetCause() != EntityDamageEvent.DamageCause.CUSTOM)) {
      return;
    }
    Player player = event.GetDamageePlayer();
    if (player == null) { return;
    }
    DropSheep(player);
  }
  
  @EventHandler
  public void InventoryClick(InventoryClickEvent event)
  {
    event.setCancelled(true);
    event.getWhoClicked().closeInventory();
  }
  
  @EventHandler
  public void RespawnInvul(PlayerGameRespawnEvent event)
  {
    this.Manager.GetCondition().Factory().Regen("Respawn", event.GetPlayer(), event.GetPlayer(), 5.0D, 3, false, false, true);
  }
  
  @EventHandler
  public void ScoreboardTimer(UpdateEvent event)
  {
    if (GetState() != Game.GameState.Live) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    long time = this._gameTime - (
      System.currentTimeMillis() - GetStateTime());
    
    if (time > 0L) {
      GetObjectiveSide().setDisplayName(
        ChatColor.WHITE + "§lTime Left " + C.cGreen + "§l" + 
        UtilTime.MakeStr(time));
    } else {
      GetObjectiveSide().setDisplayName(
        ChatColor.WHITE + "§lTime Up!");
    }
  }
  
  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!InProgress()) {
      return;
    }
    
    for (String string : this._lastScoreboard)
      GetScoreboard().resetScores(string);
    this._lastScoreboard.clear();
    



    for (GameTeam team : this._sheepPens.keySet())
    {
      int score = 0;
      
      for (Sheep sheep : this._sheep.keySet())
      {
        if (((ArrayList)this._sheepPens.get(team)).contains(sheep.getLocation().getBlock()))
        {
          score++;
        }
      }
      
      String out = score + " " + team.GetColor() + team.GetName();
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      
      if (score == 0) {
        score = -1;
      }
      GetObjectiveSide().getScore(out).setScore(score);
      
      this._teamScore.put(team, Integer.valueOf(score));
    }
    
    if (!IsLive()) {
      return;
    }
    
    String out = " ";
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(-2);
    

    out = C.Bold + "Next Sheep;";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(-3);
    
    out = C.cGreen + C.Bold + (int)UtilTime.convert(this._sheepDelay - (System.currentTimeMillis() - this._sheepTimer), 0, UtilTime.TimeUnit.FIT) + " Seconds";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(-4);
  }
  
  public void GetTeamPen(SheepData data)
  {
    for (GameTeam team : this._sheepPens.keySet())
    {
      if (((ArrayList)this._sheepPens.get(team)).contains(data.SheepBlock()))
      {
        data.SetOwner(team, (ArrayList)this._sheepPens.get(team));
      }
    }
  }
  
  public Location GetSheepSpawn()
  {
    return (Location)UtilAlg.Random(this._sheepSpawns);
  }
  

  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    ArrayList<GameTeam> teamsAlive = new ArrayList();
    
    for (GameTeam team : GetTeamList()) {
      if (team.GetPlayers(true).size() > 0)
        teamsAlive.add(team);
    }
    if (teamsAlive.size() <= 1)
    {

      if (teamsAlive.size() > 0) {
        AnnounceEnd((GameTeam)teamsAlive.get(0));
      }
      for (??? = GetTeamList().iterator(); ???.hasNext(); 
          






          ???.hasNext())
      {
        GameTeam team = (GameTeam)???.next();
        
        if ((this.WinnerTeam != null) && (team.equals(this.WinnerTeam)))
        {
          for (Player player : team.GetPlayers(false)) {
            AddGems(player, 10.0D, "Winning Team", false);
          }
        }
        ??? = team.GetPlayers(false).iterator(); continue;Player player = (Player)???.next();
        if (player.isOnline()) {
          AddGems(player, 10.0D, "Participation", false);
        }
      }
      
      SetState(Game.GameState.End);
    }
    

    if (this._gameTime - (System.currentTimeMillis() - GetStateTime()) <= 0L)
    {
      GameTeam bestTeam = null;
      int bestScore = -1;
      int duplicate = 0;
      

      for (GameTeam team : this._teamScore.keySet())
      {
        if ((bestTeam == null) || (((Integer)this._teamScore.get(team)).intValue() > bestScore))
        {
          bestTeam = team;
          bestScore = ((Integer)this._teamScore.get(team)).intValue();
          duplicate = 0;
        }
        else if (((Integer)this._teamScore.get(team)).intValue() == bestScore)
        {
          duplicate++;
        }
      }
      
      if (duplicate > 0)
      {
        if (UtilTime.elapsed(this._gameEndAnnounce, 10000L))
        {
          Announce(C.cGold + C.Bold + "First team to take the lead will win the game!");
          
          this._gameEndAnnounce = System.currentTimeMillis();
        }
      }
      else
      {
        AnnounceEnd(bestTeam);
        
        for (??? = GetTeamList().iterator(); ???.hasNext(); 
            






            ???.hasNext())
        {
          GameTeam team = (GameTeam)???.next();
          
          if ((this.WinnerTeam != null) && (team.equals(this.WinnerTeam)))
          {
            for (Player player : team.GetPlayers(false)) {
              AddGems(player, 10.0D, "Winning Team", false);
            }
          }
          ??? = team.GetPlayers(false).iterator(); continue;Player player = (Player)???.next();
          if (player.isOnline()) {
            AddGems(player, 10.0D, "Participation", false);
          }
        }
        
        SetState(Game.GameState.End);
      }
    }
  }
}
