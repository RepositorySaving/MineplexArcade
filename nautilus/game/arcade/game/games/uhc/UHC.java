package nautilus.game.arcade.game.games.uhc;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.managers.GameLobbyManager;
import nautilus.game.arcade.world.WorldData;
import net.minecraft.server.v1_7_R3.DedicatedPlayerList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UHC extends TeamGame
{
  private NautHashMap<String, Long> _deathTime = new NautHashMap();
  private NautHashMap<String, Long> _combatTime = new NautHashMap();
  
  private int _borders = 1000;
  
  private boolean _borderShrink = true;
  private int _borderShrinkSize = 1000;
  
  private int _gameMinutes = 0;
  private long _lastMinute = System.currentTimeMillis();
  
  private boolean _soloGame = false;
  private boolean _timerStarted = false;
  private boolean _ended = false;
  
  private boolean _dragonMode = false;
  private ArrayList<Location> _portalBlock = null;
  private ArrayList<Location> _portal = null;
  private boolean _portalCreated = false;
  private GameTeam _lastDragonDamager = null;
  
  private Location _exitLocation = null;
  










  public UHC(ArcadeManager manager)
  {
    super(manager, GameType.UHC, new nautilus.game.arcade.kit.Kit[] {new KitUHC(manager) }, new String[] {"20 minutes of no PvP", "No default health regeneration", "Last player/team alive wins!" });
    

    this.DamageTeamSelf = true;
    
    this.DeathDropItems = true;
    
    this.ItemDrop = true;
    this.ItemPickup = true;
    
    this.BlockBreak = true;
    this.BlockPlace = true;
    
    this.InventoryOpen = true;
    
    this.DeathOut = true;
    this.QuitOut = false;
    
    this.CreatureAllow = true;
    
    this.AnnounceStay = false;
    this.AnnounceJoinQuit = false;
    this.AnnounceSilence = false;
    
    this.DisplayLobbySide = false;
    
    this.DeathMessages = false;
    
    this.SoupEnabled = false;
    
    this.IdleKick = false;
    this.AutoStart = false;
    this.CompassEnabled = false;
    
    this.WorldBoundaryKill = false;
    
    CraftRecipes();
    

    this.Manager.GetCreature().SetDisableCustomDrops(true);
    

    this.Manager.GetAntiStack().SetEnabled(false);
  }
  

  public void ParseData()
  {
    this.WorldData.World.setDifficulty(Difficulty.HARD);
    
    this._portalBlock = this.WorldData.GetDataLocs("YELLOW");
    this._portal = this.WorldData.GetDataLocs("BLACK");
    

    for (int i = 0; i < this._portalBlock.size(); i++)
    {
      if (i < 9) {
        ((Location)this._portalBlock.get(i)).getBlock().setTypeIdAndData(Material.ENDER_PORTAL_FRAME.getId(), (byte)4, true);
      } else {
        ((Location)this._portalBlock.get(i)).getBlock().setTypeIdAndData(Material.ENDER_PORTAL_FRAME.getId(), (byte)0, true);
      }
    }
  }
  
  @EventHandler
  public void endPortalTransfer(PlayerPortalEvent event) {
    if (event.getCause() == org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.END_PORTAL)
    {
      event.setCancelled(true);
      event.getPlayer().teleport(((CraftServer)Bukkit.getServer()).getHandle().getServer().getPlayerList().calculateTarget(event.getPlayer().getLocation(), ((CraftWorld)Bukkit.getWorld("world_the_end")).getHandle()));
    }
  }
  
  @EventHandler
  public void endPortalCreation(EntityCreatePortalEvent event)
  {
    if ((event.getEntity() instanceof EnderDragon))
    {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void TimeUpdate(UpdateEvent event)
  {
    if (!this._timerStarted) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!UtilTime.elapsed(this._lastMinute, 60000L)) {
      return;
    }
    this._gameMinutes += 1;
    this._lastMinute = System.currentTimeMillis();
    
    if (this._gameMinutes == 5)
    {
      Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 15 minutes.");
    }
    else if (this._gameMinutes == 10)
    {
      Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 10 minutes.");
    }
    else if (this._gameMinutes == 15)
    {
      Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 5 minutes.");
    }
    else if (this._gameMinutes == 16)
    {
      Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 4 minutes.");
    }
    else if (this._gameMinutes == 17)
    {
      Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 3 minutes.");
    }
    else if (this._gameMinutes == 18)
    {
      Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 2 minutes.");
    }
    else if (this._gameMinutes == 19)
    {
      Announce(ChatColor.WHITE + C.Bold + "PvP enabled in 1 minutes.");
    }
    else if (this._gameMinutes == 20)
    {
      Announce(ChatColor.WHITE + C.Bold + "PvP enabled! 20 minutes have passed.");
      
      this.DamagePvP = true;
    }
    else if (this._gameMinutes % 20 == 0)
    {
      Announce(ChatColor.WHITE + C.Bold + this._gameMinutes + " minutes have passed.");
    }
    

    if ((this._borderShrink) && (this._gameMinutes >= 90) && (this._borderShrinkSize > 50))
    {
      int time = (this._gameMinutes - 90) % 20;
      
      int newSize = Math.max(50, this._borderShrinkSize - 150);
      

      if ((this._gameMinutes >= 110) && (time == 0))
      {
        Announce(ChatColor.RED + C.Bold + "Borders have shrunk to " + ChatColor.YELLOW + C.Bold + "+/-" + newSize + ChatColor.RED + C.Bold + "!");
        this._borderShrinkSize = newSize;
      }
      

      if (time != 0)
      {


        if (time == 5)
        {
          Announce(ChatColor.GOLD + C.Bold + "Borders will shrink to " + ChatColor.YELLOW + C.Bold + "+/-" + newSize + ChatColor.GOLD + C.Bold + " in 15 minutes.");
        }
        else if (time == 10)
        {
          Announce(ChatColor.GOLD + C.Bold + "Borders will shrink to " + ChatColor.YELLOW + C.Bold + "+/-" + newSize + ChatColor.GOLD + C.Bold + " in 10 minutes.");
        }
        else if (time == 15)
        {
          Announce(ChatColor.GOLD + C.Bold + "Borders will shrink to " + ChatColor.YELLOW + C.Bold + "+/-" + newSize + ChatColor.GOLD + C.Bold + " in 5 minutes.");
        }
        else if (time == 19)
        {
          Announce(ChatColor.GOLD + C.Bold + "Borders will shrink to " + ChatColor.YELLOW + C.Bold + "+/-" + newSize + ChatColor.GOLD + C.Bold + " in 1 minute.");
        }
      }
    }
  }
  
  @EventHandler
  public void GameStart(GameStateChangeEvent event) {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    
    this.Manager.GetDamage().SetEnabled(false);
    
    this.WorldData.World.setTime(2000L);
    

    for (Entity ent : this.WorldData.World.getEntities())
    {
      if ((ent instanceof Monster))
      {

        ent.remove();
      }
    }
    
    for (Player player : GetPlayers(true))
    {
      this.Manager.GetCondition().Factory().Blind("Start Blind", player, player, 8.0D, 1, false, false, false);
      this.Manager.GetCondition().Factory().Slow("Start Slow", player, player, 8.0D, 4, false, false, false, false);
      
      player.setSaturation(3.0F);
      player.setExhaustion(0.0F);
    }
  }
  
  @EventHandler
  public void WorldBoundaryCheck(PlayerMoveEvent event)
  {
    if (!IsLive()) {
      return;
    }
    

    if ((event.getTo().getX() < this.WorldData.MaxX) && 
      (event.getTo().getX() >= this.WorldData.MinX) && 
      (event.getTo().getZ() < this.WorldData.MaxZ) && 
      (event.getTo().getZ() >= this.WorldData.MinZ)) {
      return;
    }
    Location from = event.getFrom();
    if (from.getX() >= this.WorldData.MaxX) from.setX(this.WorldData.MaxX - 1);
    if (from.getX() < this.WorldData.MinX) from.setX(this.WorldData.MinX + 1);
    if (from.getZ() >= this.WorldData.MaxZ) from.setZ(this.WorldData.MaxZ - 1);
    if (from.getZ() < this.WorldData.MinZ) { from.setZ(this.WorldData.MinZ + 1);
    }
    event.setTo(event.getFrom());
    
    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_BASS, 0.5F, 1.0F);
  }
  
  @EventHandler
  public void WorldBoundaryShrinkCheck(PlayerMoveEvent event)
  {
    if (!IsLive()) {
      return;
    }
    

    if ((event.getTo().getX() < this._borderShrinkSize) && 
      (event.getTo().getX() >= -this._borderShrinkSize) && 
      (event.getTo().getZ() < this._borderShrinkSize) && 
      (event.getTo().getZ() >= -this._borderShrinkSize)) {
      return;
    }
    if (Recharge.Instance.use(event.getPlayer(), "Border Shrink", 500L, false, false))
    {
      this.Manager.GetCondition().Factory().Poison("Border", event.getPlayer(), event.getPlayer(), 1.9D, 0, false, false, false);
      this.Manager.GetCondition().Factory().Speed("Border", event.getPlayer(), event.getPlayer(), 1.9D, 1, false, false, false);
      this.Manager.GetCondition().Factory().DigFast("Border", event.getPlayer(), event.getPlayer(), 1.9D, 1, false, false, false);
    }
  }
  
  @EventHandler
  public void WorldBoundarySet(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    long time = System.currentTimeMillis();
    
    this.WorldData.MinX = (-this._borders);
    this.WorldData.MaxX = this._borders;
    this.WorldData.MinZ = (-this._borders);
    this.WorldData.MaxZ = this._borders;
    
    this.WorldData.MinY = -1000;
    this.WorldData.MaxY = 1000;
    
    for (int y = 0; y < 128; y++) {
      for (int x = -this._borders; x < this._borders; x++) {
        for (int z = -this._borders; z < this._borders; z++)
        {
          if ((x == -this._borders) || (x == this._borders - 1) || (z == -this._borders) || (z == this._borders - 1))
          {
            MapUtil.QuickChangeBlockAt(this.WorldData.World, x, y, z, 159, 14); }
        }
      }
    }
    System.out.println("Time: " + UtilTime.MakeStr(System.currentTimeMillis() - time));
  }
  
  @EventHandler
  public void WorldBoundaryBlockBreak(BlockBreakEvent event)
  {
    Block block = event.getBlock();
    

    if ((block.getX() < this.WorldData.MaxX) && 
      (block.getX() >= this.WorldData.MinX) && 
      (block.getZ() < this.WorldData.MaxZ) && 
      (block.getZ() >= this.WorldData.MinZ)) {
      return;
    }
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void GenerateTeamNames(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    GameTeam team;
    if (GetTeamList().size() > 10)
    {
      int i = 0;
      for (Iterator localIterator = GetTeamList().iterator(); localIterator.hasNext();) { team = (GameTeam)localIterator.next();
        
        team.SetColor(ChatColor.WHITE);
        team.SetName(i);
        
        i++;
      }
      
      this._soloGame = true;
      
      return;
    }
    
    for (GameTeam team : GetTeamList())
    {
      if (team.GetColor() == ChatColor.RED) { team.SetName("Red");
      } else if (team.GetColor() == ChatColor.GOLD) { team.SetName("Orange");
      } else if (team.GetColor() == ChatColor.YELLOW) { team.SetName("Yellow");
      } else if (team.GetColor() == ChatColor.GREEN) { team.SetName("Green");
      } else if (team.GetColor() == ChatColor.DARK_BLUE) { team.SetName("Blue");
      } else if (team.GetColor() == ChatColor.AQUA) { team.SetName("Aqua");
      } else if (team.GetColor() == ChatColor.LIGHT_PURPLE) { team.SetName("Purple");
      } else if (team.GetColor() == ChatColor.WHITE) team.SetName("White");
    }
  }
  
  @EventHandler
  public void GenerateSpawns(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    for (GameTeam team : GetTeamList())
    {
      team.GetSpawns().clear();
    }
    GameTeam team;
    for (??? = GetTeamList().iterator(); ???.hasNext(); 
        































        team.GetSpawns().size() < 5)
    {
      team = (GameTeam)???.next();
      
      Location loc = GetRandomSpawn(null);
      
      double dist = 250.0D;
      if (this._soloGame) {
        dist = 80.0D;
      }
      
      for (;;)
      {
        boolean clash = false;
        
        for (GameTeam otherTeam : GetTeamList())
        {
          if (!otherTeam.GetSpawns().isEmpty())
          {

            if (UtilMath.offset(loc, otherTeam.GetSpawn()) < dist)
            {
              clash = true;
              break;
            }
          }
        }
        if (!clash) {
          break;
        }
        loc = GetRandomSpawn(null);
      }
      
      team.GetSpawns().add(loc);
      
      continue;
      
      Location other = GetRandomSpawn(loc);
      
      team.GetSpawns().add(other);
    }
  }
  

  public Location GetRandomSpawn(Location around)
  {
    HashSet<Material> ignore = new HashSet();
    ignore.add(Material.LEAVES);
    
    Location loc = null;
    
    while (loc == null)
    {
      Block block = null;
      

      if (around == null)
      {
        block = UtilBlock.getHighest(this.WorldData.World, -900 + UtilMath.r(1800), -900 + UtilMath.r(1800), ignore);

      }
      else
      {
        block = UtilBlock.getHighest(this.WorldData.World, around.getBlockX() - 5 + UtilMath.r(10), around.getBlockZ() - 5 + UtilMath.r(10), ignore);
      }
      



      if (!block.getRelative(BlockFace.DOWN).isLiquid())
      {


        if ((block.getTypeId() == 0) && (block.getRelative(BlockFace.UP).getTypeId() == 0))
        {

          loc = block.getLocation().add(0.5D, 0.0D, 0.5D); }
      }
    }
    return loc;
  }
  
  @EventHandler
  public void GhastDrops(EntityDeathEvent event)
  {
    if ((event.getEntity() instanceof Ghast))
    {
      event.getDrops().clear();
      event.getDrops().add(ItemStackFactory.Instance.CreateStack(Material.GOLD_INGOT, 1));
    }
  }
  
  @EventHandler
  public void PlayerDeath(PlayerDeathEvent event)
  {
    Player player = event.getEntity();
    
    GameTeam team = GetTeam(player);
    if (team == null) { return;
    }
    
    event.getDrops().add(ItemStackFactory.Instance.CreateStack(Material.SKULL, (byte)3, 1, team.GetColor() + player.getName() + "'s Head"));
    

    Location loc = player.getLocation();
    loc.setY(-150.0D);
    player.getWorld().strikeLightningEffect(loc);
    

    this._deathTime.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void PlayerDeathMessage(CombatDeathEvent event)
  {
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    Player dead = (Player)event.GetEvent().getEntity();
    
    CombatLog log = event.GetLog();
    
    Player killer = null;
    if (log.GetKiller() != null) {
      killer = UtilPlayer.searchExact(log.GetKiller().GetName());
    }
    
    if (killer != null)
    {
      Announce(this.Manager.GetColor(dead) + C.Bold + dead.getName() + 
        C.cWhite + C.Bold + " was killed by " + 
        this.Manager.GetColor(killer) + C.Bold + killer.getName() + 
        C.cWhite + C.Bold + ".");


    }
    else if (log.GetAttackers().isEmpty())
    {
      Announce(this.Manager.GetColor(dead) + C.Bold + dead.getName() + 
        C.cWhite + C.Bold + " has died by unknown causes.");

    }
    else
    {
      Announce(this.Manager.GetColor(dead) + C.Bold + dead.getName() + 
        C.cWhite + C.Bold + " was killed by " + ((CombatComponent)log.GetAttackers().getFirst()).GetName() + ".");
    }
  }
  

  @EventHandler
  public void PlayerDeathTimeKick(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (this._deathTime.containsKey(player.getName()))
      {

        if (UtilTime.elapsed(((Long)this._deathTime.get(player.getName())).longValue(), 60000L))
        {

          player.kickPlayer(C.cYellow + "60 Seconds have passed since you died.\nYou have been removed.");
          this._deathTime.remove(player.getName());
        } }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void PlayerKick(PlayerKickEvent event) {
    event.setLeaveMessage(null);
  }
  






































  @EventHandler
  public void DamageRecord(EntityDamageEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    LivingEntity damagerEnt = UtilEvent.GetDamagerEntity(event, true);
    

    if (this._gameMinutes < 20)
    {
      if ((damagerEnt != null) && ((damagerEnt instanceof Player)) && ((event.getEntity() instanceof Player)))
      {
        event.setCancelled(true);
        return;
      }
    }
    

    if (damagerEnt != null)
    {
      if ((damagerEnt instanceof Player))
      {
        Player damager = (Player)damagerEnt;
        

        if (!IsAlive(damager))
        {
          event.setCancelled(true);
          return;
        }
        


        this._combatTime.put(damager.getName(), Long.valueOf(System.currentTimeMillis()));
      }
    }
    


    if ((event.getEntity() instanceof Player))
    {
      Player damagee = (Player)event.getEntity();
      
      if (!UtilTime.elapsed(GetStateTime(), 20000L))
      {
        event.setCancelled(true);
        return;
      }
      

      if (!IsAlive(damagee))
      {
        event.setCancelled(true);
        return;
      }
      
      if ((damagerEnt != null) && ((damagerEnt instanceof Player)))
      {
        this._combatTime.put(damagee.getName(), Long.valueOf(System.currentTimeMillis()));
      }
    }
  }
  
  @EventHandler
  public void CreatureCull(UpdateEvent event)
  {
    if (!InProgress()) {
      return;
    }
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    HashMap<EntityType, ArrayList<Entity>> ents = new HashMap();
    
    for (Entity ent : this.WorldData.World.getEntities())
    {
      if (!ents.containsKey(ent.getType())) {
        ents.put(ent.getType(), new ArrayList());
      }
      ((ArrayList)ents.get(ent.getType())).add(ent);
    }
    
    for (EntityType type : ents.keySet())
    {
      ArrayList<Entity> entList = (ArrayList)ents.get(type);
      int count = 0;
      
      while (entList.size() > 400)
      {
        Entity ent = (Entity)entList.remove(UtilMath.r(entList.size()));
        ent.remove();
        count++;
      }
      
      if (count > 0) {
        System.out.println("Removed " + count + " " + type);
      }
    }
  }
  
  private void CraftRecipes() {
    ShapelessRecipe goldMelon = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON, 1));
    goldMelon.addIngredient(1, Material.MELON);
    goldMelon.addIngredient(1, Material.GOLD_BLOCK);
    UtilServer.getServer().addRecipe(goldMelon);
    
    ShapedRecipe headApple = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE, 1));
    headApple.shape(new String[] { "GGG", "GHG", "GGG" });
    headApple.setIngredient('G', Material.GOLD_INGOT);
    headApple.setIngredient('H', Material.SKULL);
    UtilServer.getServer().addRecipe(headApple);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void CraftGoldenAppleDeny(PrepareItemCraftEvent event)
  {
    if (event.getRecipe().getResult() == null) {
      return;
    }
    Material type = event.getRecipe().getResult().getType();
    
    if (type != Material.GOLDEN_APPLE) {
      return;
    }
    if (!(event.getInventory() instanceof CraftingInventory)) {
      return;
    }
    CraftingInventory inv = event.getInventory();
    

    for (ItemStack item : inv.getMatrix()) {
      if ((item != null) && (item.getType() != Material.AIR) && 
        (item.getType() == Material.GOLD_INGOT))
        return;
    }
    inv.setResult(null);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void CraftGoldenAppleHead(PrepareItemCraftEvent event)
  {
    if (event.getRecipe().getResult() == null) {
      return;
    }
    Material type = event.getRecipe().getResult().getType();
    
    if (type != Material.GOLDEN_APPLE) {
      return;
    }
    if (!(event.getInventory() instanceof CraftingInventory)) {
      return;
    }
    CraftingInventory inv = event.getInventory();
    

    for (ItemStack item : inv.getMatrix()) {
      if ((item != null) && (item.getType() != Material.AIR) && 
        (item.getType() == Material.SKULL))
      {
        if (item.getItemMeta() != null)
        {

          if (item.getItemMeta().getDisplayName() != null)
          {

            ItemStack apple = ItemStackFactory.Instance.CreateStack(Material.GOLDEN_APPLE, (byte)0, 1, item.getItemMeta().getDisplayName() + ChatColor.AQUA + " Golden Apple");
            apple.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.ARROW_DAMAGE, 1);
            
            inv.setResult(apple);
            return;
          } } }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void CraftGlisteringMelon(PrepareItemCraftEvent event) {
    if (event.getRecipe().getResult() == null) {
      return;
    }
    Material type = event.getRecipe().getResult().getType();
    
    if (type != Material.SPECKLED_MELON) {
      return;
    }
    if (!(event.getInventory() instanceof CraftingInventory)) {
      return;
    }
    CraftingInventory inv = event.getInventory();
    

    for (ItemStack item : inv.getMatrix()) {
      if ((item != null) && (item.getType() != Material.AIR) && 
        (item.getType() == Material.GOLD_BLOCK))
        return;
    }
    inv.setResult(null);
  }
  
  @EventHandler
  public void HealthChange(EntityRegainHealthEvent event)
  {
    if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void HeadPlaceCancel(BlockPlaceEvent event) {
    if ((event.getItemInHand().getType() == Material.SKULL) || (event.getItemInHand().getType() == Material.SKULL_ITEM)) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void ConsumeHeadApple(PlayerItemConsumeEvent event) {
    if (event.getItem().getItemMeta().getDisplayName() == null) {
      return;
    }
    if (!event.getItem().getItemMeta().getDisplayName().contains("Head")) {
      return;
    }
    UtilPlayer.message(event.getPlayer(), "You ate " + event.getItem().getItemMeta().getDisplayName());
    
    new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0).apply(event.getPlayer());
    new PotionEffect(PotionEffectType.REGENERATION, 200, 1).apply(event.getPlayer());
  }
  
  @EventHandler
  public void NetherObsidianCancel(BlockPlaceEvent event)
  {
    if (event.getBlock().getWorld().getEnvironment() == org.bukkit.World.Environment.NETHER)
    {
      if (event.getBlock().getType() == Material.OBSIDIAN)
      {
        UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot place " + F.elem("Obsidian") + " in the " + F.elem("Nether") + "."));
        event.setCancelled(true);
      }
    }
  }
  


  @EventHandler
  public void KillCancel(PlayerCommandPreprocessEvent event) {}
  

  @EventHandler
  public void Commands(PlayerCommandPreprocessEvent event)
  {
    if (event.getMessage().startsWith("/kill")) {
      event.setCancelled(true);
    }
    if (event.getMessage().startsWith("/uhc timer start"))
    {
      event.setCancelled(true);
      this._timerStarted = true;
      
      Announce(event.getPlayer().getName() + " started the 20 minute timer!");
    }
    
    if (event.getMessage().startsWith("/uhc game start"))
    {
      SetCountdownForce(true);
      SetCountdown(11);
      event.setCancelled(true);
      
      Announce(event.getPlayer().getName() + " started the game!");
    }
    
    if (event.getMessage().startsWith("/uhc game stop"))
    {
      SetState(Game.GameState.End);
      event.setCancelled(true);
      
      Announce(event.getPlayer().getName() + " stopped the game!");
    }
    
    if (event.getMessage().startsWith("/uhc time day"))
    {
      this.WorldTimeSet = 4000;
      event.setCancelled(true);
      
      Announce(event.getPlayer().getName() + " set time to Always Day!");
    }
    
    if (event.getMessage().startsWith("/uhc time night"))
    {
      this.WorldTimeSet = 16000;
      event.setCancelled(true);
      
      Announce(event.getPlayer().getName() + " set time to Always Night!");
    }
    
    if (event.getMessage().startsWith("/uhc time cycle"))
    {
      this.WorldTimeSet = -1;
      event.setCancelled(true);
      
      Announce(event.getPlayer().getName() + " set time to Day and Night!");
    }
    
    if (event.getMessage().startsWith("/dragondamage"))
    {
      if (event.getPlayer().getName().equals("Chiss"))
      {
        for (Entity ent : event.getPlayer().getWorld().getEntities())
        {
          if ((ent instanceof EnderDragon))
          {
            ((EnderDragon)ent).damage(100.0D, event.getPlayer());
          }
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void clearCreeperExplode(EntityExplodeEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void clearCreeperExplodeReenable(EntityExplodeEvent event)
  {
    event.setCancelled(false);
  }
  
  @EventHandler
  public void TabHealth(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!InProgress()) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      GameTeam team = GetTeam(player);
      
      ChatColor col = ChatColor.GREEN;
      if (player.getHealth() <= 12.0D) col = ChatColor.YELLOW;
      if (player.getHealth() <= 6.0D) { col = ChatColor.RED;
      }
      String health = " - " + col;
      if (this._soloGame) {
        health = " - ";
      }
      int hp = (int)(player.getHealth() + 0.9999999999D);
      
      if (hp % 2 == 0) {
        health = health + hp / 2;
      } else {
        health = health + UtilMath.trim(1, hp / 2.0D);
      }
      String name = team.GetColor() + player.getName();
      if (this._soloGame) {
        name = col + player.getName();
      }
      try
      {
        while (name.length() + health.length() > 16) {
          name = name.substring(0, name.length() - 1);
        }
        player.setPlayerListName(name + health);
      }
      catch (Exception e)
      {
        System.out.println("TAB NAME: " + name + health);
        e.printStackTrace();
      }
    }
  }
  

  public boolean CanJoinTeam(GameTeam team)
  {
    if (this._soloGame) {
      return team.GetPlayers(true).isEmpty();
    }
    return team.GetPlayers(true).size() < 4;
  }
  

  public void AnnounceGame()
  {
    for (Player player : )
    {
      player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 1.0F);
      
      for (int i = 0; i < 6 - GetDesc().length; i++) {
        UtilPlayer.message(player, "");
      }
      UtilPlayer.message(player, ArcadeFormat.Line);
      
      UtilPlayer.message(player, C.cYellow + C.Bold + GetName());
      UtilPlayer.message(player, "");
      
      for (String line : GetDesc())
      {
        UtilPlayer.message(player, C.cWhite + "- " + line);
      }
      
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, C.cWhite + "Created and Hosted by " + C.cYellow + C.Bold + "Mineplex.com");
      
      UtilPlayer.message(player, ArcadeFormat.Line);
    }
  }
  

  public void AnnounceEnd(GameTeam team)
  {
    for (Player player : )
    {
      player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 1.0F);
      
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, ArcadeFormat.Line);
      
      UtilPlayer.message(player, C.cYellow + C.Bold + GetName());
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, "");
      
      if (team != null)
      {
        this.WinnerTeam = team;
        this.Winner = (team.GetName() + " Team");
        
        if (this._soloGame) {
          UtilPlayer.message(player, team.GetColor() + C.Bold + ((Player)team.GetPlayers(false).get(0)).getName() + " won the match!");
        } else {
          UtilPlayer.message(player, team.GetColor() + C.Bold + "Team " + team.GetName() + " won the match!");
        }
      }
      else {
        UtilPlayer.message(player, ChatColor.WHITE + "§lNobody won the game...");
      }
      
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, C.cWhite + "Created and Hosted by " + C.cYellow + C.Bold + "Mineplex.com");
      
      UtilPlayer.message(player, ArcadeFormat.Line);
    }
  }
  
  public void AnnounceEndLose()
  {
    for (Player player : )
    {
      player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 1.0F);
      
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, ArcadeFormat.Line);
      
      UtilPlayer.message(player, C.cYellow + C.Bold + GetName());
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, "");
      
      UtilPlayer.message(player, ChatColor.WHITE + "§lEnder Dragon has won the game...");
      
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, "");
      UtilPlayer.message(player, C.cWhite + "Created and Hosted by " + C.cYellow + C.Bold + "Mineplex.com");
      
      UtilPlayer.message(player, ArcadeFormat.Line);
    }
  }
  

  public boolean AdvertiseText(GameLobbyManager gameLobbyManager, int _advertiseStage)
  {
    if (_advertiseStage == 0)
    {
      gameLobbyManager.WriteAdvertiseLine("         ", 0, 159, (byte)15);
      gameLobbyManager.WriteAdvertiseLine("ULTRA HARDCORE", 1, 159, (byte)15);
      gameLobbyManager.WriteAdvertiseLine("CODED AND HOSTED BY", 2, 159, (byte)15);
      gameLobbyManager.WriteAdvertiseLine("MINEPLEX.COM", 3, 159, (byte)4);
      gameLobbyManager.WriteAdvertiseLine("             ", 4, 159, (byte)15);
    }
    else if (_advertiseStage == 1)
    {
      gameLobbyManager.WriteAdvertiseLine("         ", 0, 159, (byte)15);
      gameLobbyManager.WriteAdvertiseLine("JOIN", 1, 159, (byte)15);
      gameLobbyManager.WriteAdvertiseLine("MINEPLEX.COM", 2, 159, (byte)4);
      gameLobbyManager.WriteAdvertiseLine("TO PLAY", 3, 159, (byte)15);
      gameLobbyManager.WriteAdvertiseLine("         ", 4, 159, (byte)15);
    }
    
    return true;
  }
  

  public void Announce(String message)
  {
    for (Player player : )
    {
      UtilPlayer.message(player, message);
    }
    
    System.out.println("[Announcement] " + message);
  }
  

  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    if (this._ended) {
      return;
    }
    ArrayList<GameTeam> teamsAlive = new ArrayList();
    

    for (GameTeam team : GetTeamList()) {
      if (team.GetPlayers(true).size() > 0) {
        teamsAlive.add(team);
      }
    }
    for (GameTeam team : this.RejoinTeam.values()) {
      teamsAlive.add(team);
    }
    if ((!this._dragonMode) && (teamsAlive.size() == 1))
    {
      AnnounceEnd((GameTeam)teamsAlive.get(0));
      
      this._ended = true;
    }
    
    if (teamsAlive.size() == 0)
    {

      AnnounceEndLose();
      

      this._ended = true;
    }
  }
  

  @EventHandler
  public void DragonDamage(EntityDamageEvent event)
  {
    if (!this._dragonMode) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    if (this._ended) {
      return;
    }
    if (!(event.getEntity() instanceof EnderDragon)) {
      return;
    }
    
    LivingEntity damagerEnt = UtilEvent.GetDamagerEntity(event, true);
    if (damagerEnt != null)
    {
      if ((damagerEnt instanceof Player))
      {
        Player damager = (Player)damagerEnt;
        
        GameTeam team = GetTeam(damager);
        
        if (team != null)
        {
          if ((this._lastDragonDamager == null) || (!this._lastDragonDamager.equals(team)))
          {
            this._lastDragonDamager = team;
          }
        }
      }
    }
  }
  
  @EventHandler
  public void DragonDeath(EntityDeathEvent event)
  {
    if (!this._dragonMode) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    if (this._ended) {
      return;
    }
    if (this._lastDragonDamager == null) {
      return;
    }
    if (!(event.getEntity() instanceof EnderDragon)) {
      return;
    }
    AnnounceEnd(this._lastDragonDamager);
    this._ended = true;
  }
  
  @EventHandler
  public void PortalCreate(UpdateEvent event)
  {
    if (!this._dragonMode) {
      return;
    }
    if (!InProgress()) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    boolean complete = true;
    for (Location loc : this._portalBlock)
    {
      if (loc.getBlock().getType() != Material.ENDER_PORTAL_FRAME) {
        loc.getBlock().setType(Material.ENDER_PORTAL_FRAME);
      }
      if (loc.getBlock().getData() < 4) {
        complete = false;
      }
    }
    if (complete)
    {
      if (!this._portalCreated)
      {
        for (Player player : UtilServer.getPlayers()) {
          player.playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 1.0F);
        }
        Announce(ChatColor.WHITE + C.Bold + "The portal to The End has been opened!");
      }
      
      this._portalCreated = true;
      
      for (Location loc : this._portal) {
        loc.getBlock().setTypeIdAndData(Material.ENDER_PORTAL.getId(), (byte)0, false);
      }
    }
    else {
      for (Location loc : this._portal) {
        loc.getBlock().setType(Material.AIR);
      }
    }
  }
  
  @EventHandler
  public void PortalBreak(BlockBreakEvent event) {
    if (!this._dragonMode) {
      return;
    }
    if (event.getBlock().getType() == Material.ENDER_PORTAL_FRAME) {
      event.setCancelled(true);
    }
  }
  


  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event) {}
  

  @EventHandler
  public void EndUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    if (!this._ended) {
      return;
    }
    GameTeam endTeam = GetTeam(ChatColor.GRAY);
    
    if (endTeam == null)
    {
      endTeam = new GameTeam(this, "Post Game", ChatColor.GRAY, ((GameTeam)GetTeamList().get(0)).GetSpawns());
      AddTeam(endTeam);
    }
    
    for (Player player : UtilServer.getPlayers())
    {
      GameTeam team = GetTeam(player);
      
      if ((team != null) && (!endTeam.equals(team)))
      {
        team.RemovePlayer(player);
        this.Manager.Clear(player);
        
        endTeam.AddPlayer(player);
        player.setGameMode(GameMode.CREATIVE);
        player.setFlying(true);
      }
    }
  }
  
  @EventHandler
  public void EndCommands(PlayerCommandPreprocessEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (!this._ended) {
      return;
    }
    if (event.getMessage().startsWith("/world"))
    {
      event.setCancelled(true);
      event.getPlayer().teleport(((Location)this._portalBlock.get(0)).clone().add(0.0D, 10.0D, 0.0D));
      event.getPlayer().setGameMode(GameMode.CREATIVE);
      event.getPlayer().setFlying(true);
    }
  }
}
