package nautilus.game.arcade.game.games.castlesiege;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.games.castlesiege.kits.KitUndeadArcher;
import nautilus.game.arcade.game.games.castlesiege.kits.KitUndeadZombie;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.NullKit;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class CastleSiege extends TeamGame
{
  private long _tntSpawn = 0L;
  private ArrayList<Location> _tntSpawns = new ArrayList();
  private ArrayList<Location> _tntWeakness = new ArrayList();
  private HashMap<Player, FallingBlock> _tntCarry = new HashMap();
  
  private ArrayList<Location> _kingLocs;
  private Creature _king;
  private Location _kingLoc;
  private String _kingName;
  private Player _kingDamager = null;
  private int _kingHealth = 40;
  







  private ArrayList<Location> _peasantSpawns;
  







  private ArrayList<Location> _horseSpawns;
  








  public CastleSiege(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.CastleSiege, new Kit[] {new nautilus.game.arcade.game.games.castlesiege.kits.KitHumanMarksman(manager), new nautilus.game.arcade.game.games.castlesiege.kits.KitHumanKnight(manager), new NullKit(manager), new nautilus.game.arcade.game.games.castlesiege.kits.KitHumanPeasant(manager), new NullKit(manager), new nautilus.game.arcade.game.games.castlesiege.kits.KitUndeadGhoul(manager), new KitUndeadArcher(manager), new KitUndeadZombie(manager) }, new String[] {F.elem(new StringBuilder(String.valueOf(C.cAqua)).append("Defenders").toString()) + C.cWhite + " must defend the King.", F.elem(new StringBuilder(String.valueOf(C.cAqua)).append("Defenders").toString()) + C.cWhite + " win when the sun rises.", F.elem(new StringBuilder(String.valueOf(C.cAqua)).append("Defenders").toString()) + C.cWhite + " respawn as wolves.", "", F.elem(new StringBuilder(String.valueOf(C.cRed)).append("Undead").toString()) + C.cWhite + " must kill the King.", F.elem(new StringBuilder(String.valueOf(C.cRed)).append("Undead").toString()) + C.cWhite + " lose when the sun rises." });
    


    this._help = 
      new String[] {
      "Marksmen are extremely important to defence!", 
      "It's recommended 50%+ of defence are Marksmen.", 
      "Use Barricades to block the Undeads path.", 
      "Use TNT to destroy weak points in walls.", 
      "Weak points are marked by cracked stone brick.", 
      "Undead can break fences with their axes.", 
      "Undead Archers must pick up arrows from the ground." };
    


    this.HungerSet = 20;
    this.DeathOut = false;
    this.WorldTimeSet = 14000;
    this.BlockPlaceAllow.add(Integer.valueOf(85));
    
    this._kingName = (C.cYellow + C.Bold + "King Jonalon");
  }
  

  public void ParseData()
  {
    this._tntSpawns = this.WorldData.GetDataLocs("RED");
    this._tntWeakness = this.WorldData.GetDataLocs("BLACK");
    
    this._kingLocs = this.WorldData.GetDataLocs("YELLOW");
    
    this._peasantSpawns = this.WorldData.GetDataLocs("GREEN");
    this._horseSpawns = this.WorldData.GetDataLocs("BROWN");
  }
  

  public void RestrictKits()
  {
    for (Kit kit : GetKits())
    {
      for (GameTeam team : GetTeamList())
      {
        if (team.GetColor() == ChatColor.RED)
        {
          if (kit.GetName().contains("Castle")) {
            team.GetRestrictedKits().add(kit);
          }
        }
        else {
          if (kit.GetName().contains("Undead")) {
            team.GetRestrictedKits().add(kit);
          }
          team.SetRespawnTime(8.0D);
        }
      }
    }
  }
  
  @EventHandler
  public void MoveKits(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    for (int i = 0; (i < this.WorldData.GetDataLocs("PINK").size()) && (i < 3); i++)
    {
      if (GetKits().length > 5 + i)
      {

        this.CreatureAllowOverride = true;
        Entity ent = GetKits()[(5 + i)].SpawnEntity((Location)this.WorldData.GetDataLocs("PINK").get(i));
        this.CreatureAllowOverride = false;
        
        this.Manager.GetLobby().AddKitLocation(ent, GetKits()[(5 + i)], (Location)this.WorldData.GetDataLocs("PINK").get(i));
      }
    }
  }
  
  @EventHandler
  public void HorseSpawn(GameStateChangeEvent event) {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    for (Location loc : this._horseSpawns)
    {
      this.CreatureAllowOverride = true;
      Horse horse = (Horse)loc.getWorld().spawn(loc, Horse.class);
      this.CreatureAllowOverride = false;
      
      horse.setAdult();
      horse.setAgeLock(true);
      horse.setColor(org.bukkit.entity.Horse.Color.BLACK);
      horse.setStyle(Horse.Style.BLACK_DOTS);
      horse.setVariant(org.bukkit.entity.Horse.Variant.HORSE);
      horse.setMaxDomestication(1);
      horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
      horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING));
      
      horse.setMaxHealth(60.0D);
      horse.setHealth(horse.getMaxHealth());
      
      horse.setCustomName("War Horse");
    }
  }
  
  @EventHandler
  public void HorseInteract(PlayerInteractEntityEvent event)
  {
    if (!(event.getRightClicked() instanceof Horse)) {
      return;
    }
    Player player = event.getPlayer();
    GameTeam team = GetTeam(player);
    
    if ((team == null) || (team.GetColor() == ChatColor.RED) || (!IsAlive(player)))
    {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void HorseDamageCancel(CustomDamageEvent event)
  {
    if (!(event.GetDamageeEntity() instanceof Horse)) {
      return;
    }
    Player player = event.GetDamagerPlayer(true);
    if (player == null) {
      return;
    }
    if (!IsAlive(player)) {
      return;
    }
    if (GetTeam(player) == null) {
      return;
    }
    if (GetTeam(player).GetColor() == ChatColor.RED) {
      return;
    }
    event.SetCancelled("Horse Team Damage");
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void GameStateChange(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    
    this.CreatureAllowOverride = true;
    
    this._kingLoc = ((Location)this._kingLocs.get(UtilMath.r(this._kingLocs.size())));
    
    this._king = ((Creature)this._kingLoc.getWorld().spawnEntity(this._kingLoc, EntityType.ZOMBIE));
    
    this._king.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    this._king.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    this._king.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    this._king.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
    this._king.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
    
    this._king.setCustomName(this._kingName);
    this._king.setCustomNameVisible(true);
    
    this._king.setRemoveWhenFarAway(false);
    
    this.CreatureAllowOverride = false;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void SetDefenderRespawn(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    
    GetTeam(ChatColor.AQUA).SetSpawns(this._peasantSpawns);
  }
  

  @EventHandler
  public void KingTarget(EntityTargetEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void KingDamage(CustomDamageEvent event)
  {
    if ((this._king == null) || (!this._king.isValid())) {
      return;
    }
    if (!event.GetDamageeEntity().equals(this._king)) {
      return;
    }
    event.SetCancelled("King Damage");
    
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    GameTeam team = GetTeam(damager);
    
    if ((team != null) && (team.GetColor() == ChatColor.RED))
    {
      this._kingDamager = damager;
      this._kingHealth -= 1;
      
      if (this._kingHealth <= 0) {
        this._king.damage(500.0D);
      }
    }
  }
  
  @EventHandler
  public void KingUpdate(UpdateEvent event) {
    if (GetState() != Game.GameState.Live) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (this._king == null) {
      return;
    }
    if (UtilMath.offset(this._king.getLocation(), this._kingLoc) > 6.0D)
    {
      this._king.teleport(this._kingLoc);
    }
    else
    {
      mineplex.core.common.util.UtilEnt.CreatureMove(this._king, this._kingLoc, 1.0F);
    }
  }
  
  @EventHandler
  public void PlayerDeath(PlayerDeathEvent event)
  {
    if (GetTeam(ChatColor.AQUA).HasPlayer(event.getEntity())) {
      SetKit(event.getEntity(), GetKits()[3], true);
    }
  }
  
  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    
    if ((this._king != null) && (this._king.isValid()))
    {
      GetObjectiveSide().getScore(C.cYellow + C.Bold + "Kings Health").setScore(this._kingHealth);
    }
    

    HashMap<String, Integer> _scoreGroup = new HashMap();
    _scoreGroup.put(C.cAqua + "Wolves", Integer.valueOf(0));
    _scoreGroup.put(C.cAqua + "Defenders", Integer.valueOf(0));
    _scoreGroup.put(C.cRed + "Undead", Integer.valueOf(0));
    
    for (Player player : UtilServer.getPlayers())
    {
      if (IsAlive(player))
      {

        Kit kit = GetKit(player);
        if (kit != null)
        {
          if (kit.GetName().contains("Castle"))
          {
            if (kit.GetName().contains("Wolf"))
            {
              _scoreGroup.put(C.cAqua + "Wolves", Integer.valueOf(1 + ((Integer)_scoreGroup.get(C.cAqua + "Wolves")).intValue()));
            }
            else
            {
              _scoreGroup.put(C.cAqua + "Defenders", Integer.valueOf(1 + ((Integer)_scoreGroup.get(C.cAqua + "Defenders")).intValue()));
            }
          }
          else if (kit.GetName().contains("Undead"))
          {
            _scoreGroup.put(C.cRed + "Undead", Integer.valueOf(1 + ((Integer)_scoreGroup.get(C.cRed + "Undead")).intValue())); }
        }
      }
    }
    for (String group : _scoreGroup.keySet())
    {
      GetObjectiveSide().getScore(group).setScore(((Integer)_scoreGroup.get(group)).intValue());
    }
  }
  

  public void EndCheck()
  {
    if (!IsLive())
      return;
    Iterator localIterator1;
    if (this.WorldTimeSet > 24100)
    {
      SetCustomWinLine(this._kingName + ChatColor.RESET + " has survived the seige!");
      
      SetState(Game.GameState.End);
      AnnounceEnd(GetTeam(ChatColor.AQUA));
      
      for (localIterator1 = GetTeamList().iterator(); localIterator1.hasNext(); 
          








          ???.hasNext())
      {
        GameTeam team = (GameTeam)localIterator1.next();
        
        if ((this.WinnerTeam != null) && (team.equals(this.WinnerTeam)))
        {
          for (Player player : team.GetPlayers(false))
          {
            AddGems(player, 10.0D, "Winning Team", false);
          }
        }
        
        ??? = team.GetPlayers(false).iterator(); continue;Player player = (Player)???.next();
        if (player.isOnline()) {
          AddGems(player, 10.0D, "Participation", false);
        }
      }
    }
    if (!this._king.isValid())
    {
      if (this._kingDamager != null)
      {
        SetCustomWinLine(C.cRed + this._kingDamager.getName() + C.cWhite + " slaughtered " + this._kingName + ChatColor.RESET + "!");
        AddGems(this._kingDamager, 20.0D, "King Slayer", false);
      }
      else {
        SetCustomWinLine(this._kingName + ChatColor.RESET + " has died!");
      }
      SetState(Game.GameState.End);
      AnnounceEnd(GetTeam(ChatColor.RED));
      
      for (localIterator1 = GetTeamList().iterator(); localIterator1.hasNext(); 
          








          ???.hasNext())
      {
        GameTeam team = (GameTeam)localIterator1.next();
        
        if ((this.WinnerTeam != null) && (team.equals(this.WinnerTeam)))
        {
          for (Player player : team.GetPlayers(false))
          {
            AddGems(player, 10.0D, "Winning Team", false);
          }
        }
        
        ??? = team.GetPlayers(false).iterator(); continue;Player player = (Player)???.next();
        if (player.isOnline()) {
          AddGems(player, 10.0D, "Participation", false);
        }
      }
    }
  }
  
  @EventHandler
  public void TNTSpawn(UpdateEvent event) {
    if (GetState() != Game.GameState.Live) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (!UtilTime.elapsed(GetStateTime(), 20000L)) {
      return;
    }
    if (!UtilTime.elapsed(this._tntSpawn, 25000L)) {
      return;
    }
    if (this._tntSpawns.isEmpty()) {
      return;
    }
    Location loc = (Location)this._tntSpawns.get(UtilMath.r(this._tntSpawns.size()));
    
    if (loc.getBlock().getTypeId() == 46) {
      return;
    }
    loc.getBlock().setTypeId(46);
    this._tntSpawn = System.currentTimeMillis();
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void TNTPickup(PlayerInteractEvent event)
  {
    if ((event.getAction() != Action.RIGHT_CLICK_BLOCK) && (event.getAction() != Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    if (event.getClickedBlock().getTypeId() != 46) {
      return;
    }
    event.setCancelled(true);
    
    Player player = event.getPlayer();
    
    if (!IsAlive(player)) {
      return;
    }
    if (!GetTeam(ChatColor.RED).HasPlayer(player)) {
      return;
    }
    if (this._tntCarry.containsKey(player)) {
      return;
    }
    event.getClickedBlock().setTypeId(0);
    
    FallingBlock tnt = player.getWorld().spawnFallingBlock(player.getEyeLocation(), 46, (byte)0);
    
    player.eject();
    player.setPassenger(tnt);
    
    this._tntCarry.put(player, tnt);
    
    UtilPlayer.message(player, F.main("Game", "You picked up " + F.skill("TNT") + "."));
    UtilPlayer.message(player, F.main("Game", F.elem("Right-Click") + " to detonate yourself."));
  }
  
  @EventHandler(priority=EventPriority.NORMAL)
  public void TNTUse(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_BLOCK) && (event.getAction() != Action.RIGHT_CLICK_AIR)) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this._tntCarry.containsKey(player)) {
      return;
    }
    event.setCancelled(true);
    
    for (Location loc : this._tntSpawns)
    {
      if (UtilMath.offset(player.getLocation(), loc) < 16.0D)
      {
        UtilPlayer.message(player, F.main("Game", "You cannot " + F.skill("Detonate") + " so far from the Castle."));
        return;
      }
    }
    
    ((FallingBlock)this._tntCarry.remove(player)).remove();
    
    TNTPrimed tnt = (TNTPrimed)player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);
    tnt.setFuseTicks(0);
    UtilPlayer.message(player, F.main("Game", "You used " + F.skill("Detonate") + "."));
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void TNTDeath(PlayerDeathEvent event)
  {
    Player player = event.getEntity();
    
    if (!this._tntCarry.containsKey(player)) {
      return;
    }
    ((FallingBlock)this._tntCarry.remove(player)).remove();
    
    TNTPrimed tnt = (TNTPrimed)player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);
    tnt.setFuseTicks(0);
    UtilPlayer.message(player, F.main("Game", "You used " + F.skill("Detonate") + "."));
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void TNTDamageDivert(ProjectileHitEvent event)
  {
    for (Player player : this._tntCarry.keySet())
    {
      if (player.getPassenger() != null)
      {

        double dist = UtilMath.offset(player.getPassenger().getLocation(), event.getEntity().getLocation().add(event.getEntity().getVelocity()));
        
        if (dist < 2.0D)
        {
          int damage = (int)(5.0D * (event.getEntity().getVelocity().length() / 3.0D));
          

          this.Manager.GetDamage().NewDamageEvent(player, event.getEntity().getShooter(), event.getEntity(), 
            EntityDamageEvent.DamageCause.CUSTOM, damage, true, false, false, 
            null, GetName());
          
          event.getEntity().remove();
        }
      }
    }
  }
  
  @EventHandler
  public void TNTExpire(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    Iterator<Player> tntIterator = this._tntCarry.keySet().iterator();
    
    while (tntIterator.hasNext())
    {
      Player player = (Player)tntIterator.next();
      FallingBlock block = (FallingBlock)this._tntCarry.get(player);
      
      if ((player.isDead()) || (!block.isValid()) || (block.getTicksLived() > 900))
      {
        player.eject();
        block.remove();
        
        TNTPrimed tnt = (TNTPrimed)player.getWorld().spawn(player.getEyeLocation(), TNTPrimed.class);
        tnt.setFuseTicks(0);
        
        tntIterator.remove();
      }
      else
      {
        FireworkEffect effect = FireworkEffect.builder().withColor(org.bukkit.Color.RED).with(org.bukkit.FireworkEffect.Type.BURST).build();
        
        try
        {
          this.Manager.GetFirework().playFirework(player.getEyeLocation(), effect);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  @EventHandler
  public void TNTWeakness(ExplosionPrimeEvent event) {
    Location weakness = null;
    for (Location loc : this._tntWeakness)
    {
      if (UtilMath.offset(loc, event.getEntity().getLocation()) < 4.0D)
      {
        weakness = loc;
        break;
      }
    }
    
    if (weakness == null) {
      return;
    }
    this._tntWeakness.remove(weakness);
    
    final Location extra = weakness;
    
    for (int i = 0; i < 10; i++)
    {
      this.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
      {
        public void run()
        {
          TNTPrimed tnt = (TNTPrimed)extra.getWorld().spawn(extra.clone().add(3 - UtilMath.r(6), 5 + UtilMath.r(2), 3 - UtilMath.r(6)), TNTPrimed.class);
          tnt.setFuseTicks(0);
          tnt.setIsIncendiary(true);
        }
      }, i * 3);
    }
    
    weakness.getWorld().playSound(weakness, org.bukkit.Sound.EXPLODE, 16.0F, 0.8F);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void AttackerBlockBreak(BlockBreakEvent event)
  {
    GameTeam team = GetTeam(event.getPlayer());
    if (team == null) {
      return;
    }
    if (team.GetColor() != ChatColor.RED) {
      return;
    }
    if (event.getBlock().getTypeId() == 85) {
      event.setCancelled(false);
    }
  }
  
  @EventHandler
  public void DefenderBlockPlace(BlockPlaceEvent event) {
    GameTeam team = GetTeam(event.getPlayer());
    if (team == null) {
      return;
    }
    if (team.GetColor() != ChatColor.AQUA) {
      return;
    }
    if (event.getBlock().getTypeId() != 85) {
      return;
    }
    for (Block block : mineplex.core.common.util.UtilBlock.getSurrounding(event.getBlock(), false))
    {
      if (block.isLiquid())
      {
        event.setCancelled(true);
        UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot place " + F.elem("Barricade") + " in water."));
      }
    }
    
    if (event.getBlockAgainst().getTypeId() == 85)
    {
      event.setCancelled(true);
      UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot place " + F.elem("Barricade") + " on each other."));
    }
    
    if ((this._king != null) && (UtilMath.offset(this._king.getLocation(), event.getBlock().getLocation().add(0.5D, 0.5D, 0.5D)) < 4.0D))
    {
      event.setCancelled(true);
      UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot place " + F.elem("Barricade") + " near " + F.elem(new StringBuilder(String.valueOf(C.cAqua)).append(this._kingName).toString()) + "."));
    }
  }
  
  @EventHandler
  public void DayTimer(UpdateEvent event)
  {
    if (GetState() != Game.GameState.Live) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    this.WorldTimeSet += 1;
    
    long timeLeft = 24000 - this.WorldTimeSet;
    timeLeft = timeLeft / 20L * 1000L;
    

    if (timeLeft > 0L) {
      GetObjectiveSide().setDisplayName(
        ChatColor.WHITE + "§lSun Rise: " + C.cGreen + "§l" + 
        UtilTime.MakeStr(timeLeft));
    }
    else {
      GetObjectiveSide().setDisplayName(
        ChatColor.WHITE + "§lSun has risen!");
      
      for (Player player : GetTeam(ChatColor.RED).GetPlayers(true)) {
        this.Manager.GetCondition().Factory().Ignite("Sun Damage", player, player, 5.0D, false, false);
      }
    }
  }
  
  @EventHandler
  public void SnowDamage(UpdateEvent event)
  {
    if (GetState() != Game.GameState.Live) {
      return;
    }
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player player : GetPlayers(true)) {
      if (player.getLocation().getBlock().getTypeId() == 78)
      {

        this.Manager.GetDamage().NewDamageEvent(player, null, null, 
          EntityDamageEvent.DamageCause.DROWNING, 2.0D, false, true, false, 
          "Snow", "Snow Damage");
        
        player.getWorld().playEffect(player.getLocation(), org.bukkit.Effect.STEP_SOUND, 80);
      }
    }
  }
}
