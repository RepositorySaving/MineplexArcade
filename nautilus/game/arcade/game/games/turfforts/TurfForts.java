package nautilus.game.arcade.game.games.turfforts;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.games.turfforts.kits.KitMarksman;
import nautilus.game.arcade.game.games.turfforts.kits.KitShredder;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.world.WorldData;
import net.minecraft.server.v1_7_R3.EntityArrow;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class TurfForts extends TeamGame
{
  private ArrayList<Location> _turf;
  private Location _red;
  private Location _redBase;
  private Location _blue;
  private Location _blueBase;
  private int xRed = 0;
  private int zRed = 0;
  
  private long _phaseTime = 0L;
  private long _buildTime = 20000L;
  private long _fightTime = 90000L;
  private boolean _fight = false;
  private int _lines = 0;
  
  private HashMap<Player, Long> _enemyTurf = new HashMap();
  















  public TurfForts(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.TurfWars, new Kit[] {new KitMarksman(manager), new nautilus.game.arcade.game.games.turfforts.kits.KitInfiltrator(manager), new KitShredder(manager) }, new String[] {"You have 30 seconds to build your Fort!", "", "Each kill advances your turf forwards.", "Take over all the turf to win!" });
    


    this.HungerSet = 20;
    this.DeathOut = false;
    this.BlockPlaceAllow.add(Integer.valueOf(35));
    this.BlockBreakAllow.add(Integer.valueOf(35));
    this.ItemDrop = false;
    this.ItemPickup = false;
    this.DamageSelf = false;
  }
  

  public void ParseData()
  {
    this._turf = this.WorldData.GetDataLocs("YELLOW");
    
    this._red = ((Location)this.WorldData.GetDataLocs("RED").get(0));
    this._redBase = ((Location)this.WorldData.GetDataLocs("PINK").get(0));
    
    this._blue = ((Location)this.WorldData.GetDataLocs("BLUE").get(0));
    this._blueBase = ((Location)this.WorldData.GetDataLocs("LIGHT_BLUE").get(0));
    
    if (this._red.getBlockX() > this._blue.getBlockX()) { this.xRed = 1;
    } else if (this._red.getBlockX() < this._blue.getBlockX()) { this.xRed = -1;
    }
    if (this._red.getBlockZ() > this._blue.getBlockZ()) { this.zRed = 1;
    } else if (this._red.getBlockZ() < this._blue.getBlockZ()) { this.zRed = -1;
    }
    
    for (Location loc : this._turf)
    {
      if (UtilMath.offset(loc, this._red) < UtilMath.offset(loc, this._blue)) {
        MapUtil.QuickChangeBlockAt(loc, 159, (byte)14);
      } else {
        MapUtil.QuickChangeBlockAt(loc, 159, (byte)3);
      }
    }
  }
  
  @EventHandler
  public void PlayerKillAward(CombatDeathEvent event) {
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    if (event.GetLog().GetKiller() == null) {
      return;
    }
    Player killed = (Player)event.GetEvent().getEntity();
    
    Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    if (killer == null) {
      return;
    }
    if (GetTeam(killer) == null) {
      return;
    }
    if (GetTeam(killed) == null) {
      return;
    }
    if (GetTeam(killer).equals(GetTeam(killed))) {
      return;
    }
    if (GetTeam(killer).GetColor() == ChatColor.RED)
    {
      TurfMove(true);
    }
    else
    {
      TurfMove(false);
    }
  }
  
  private void TurfMove(boolean red)
  {
    for (int line = 0; line < GetLinesPerKill(); line++)
    {
      if (red)
      {
        if (this.xRed != 0) {
          for (Location loc : this._turf) {
            if (loc.getBlockX() == this._blue.getBlockX())
            {
              MapUtil.QuickChangeBlockAt(loc, 159, (byte)14);
              
              for (int i = 1; i < 6; i++)
                if (loc.getBlock().getRelative(BlockFace.UP, i).getTypeId() == 35)
                  MapUtil.QuickChangeBlockAt(loc.clone().add(0.0D, i, 0.0D), 0, (byte)0);
            }
          }
        }
        if (this.zRed != 0) {
          for (Location loc : this._turf)
            if (loc.getBlockZ() == this._blue.getBlockZ())
            {
              MapUtil.QuickChangeBlockAt(loc, 159, (byte)14);
              
              for (int i = 1; i < 6; i++)
                if (loc.getBlock().getRelative(BlockFace.UP, i).getTypeId() == 35)
                  MapUtil.QuickChangeBlockAt(loc.clone().add(0.0D, i, 0.0D), 0, (byte)0);
            }
        }
        this._red.subtract(this.xRed, 0.0D, this.zRed);
        this._blue.subtract(this.xRed, 0.0D, this.zRed);
      }
      else
      {
        if (this.xRed != 0) {
          for (Location loc : this._turf)
            if (loc.getBlockX() == this._red.getBlockX())
            {
              MapUtil.QuickChangeBlockAt(loc, 159, (byte)3);
              
              for (int i = 1; i < 6; i++)
                if (loc.getBlock().getRelative(BlockFace.UP, i).getTypeId() == 35)
                  MapUtil.QuickChangeBlockAt(loc.clone().add(0.0D, i, 0.0D), 0, (byte)0);
            }
        }
        if (this.zRed != 0) {
          for (Location loc : this._turf)
            if (loc.getBlockZ() == this._red.getBlockZ())
            {
              MapUtil.QuickChangeBlockAt(loc, 159, (byte)3);
              
              for (int i = 1; i < 6; i++)
                if (loc.getBlock().getRelative(BlockFace.UP, i).getTypeId() == 35)
                  MapUtil.QuickChangeBlockAt(loc.clone().add(0.0D, i, 0.0D), 0, (byte)0);
            }
        }
        this._red.add(this.xRed, 0.0D, this.zRed);
        this._blue.add(this.xRed, 0.0D, this.zRed);
      }
      
      EndCheck();
    }
  }
  
  @EventHandler
  public void BowCancel(EntityShootBowEvent event)
  {
    if (!this._fight)
    {
      UtilPlayer.message(event.getEntity(), F.main("Game", "You cannot attack during Build Time!"));
      event.getProjectile().remove();
    }
  }
  
  @EventHandler
  public void BlockPlace(BlockPlaceEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    GameTeam team = GetTeam(event.getPlayer());
    if (team == null)
    {
      event.setCancelled(true);
      return;
    }
    


    Block block = event.getBlock().getRelative(BlockFace.DOWN);
    while (block.getTypeId() == 0) {
      block = block.getRelative(BlockFace.DOWN);
    }
    if (block.getData() != team.GetColorData())
    {
      UtilPlayer.message(event.getPlayer(), F.main("Game", "You can only build above " + F.elem(new StringBuilder().append(team.GetColor()).append(team.GetName()).toString()) + "."));
      event.setCancelled(true);
      return;
    }
    

    boolean aboveTurf = false;
    for (int i = 1; i <= 5; i++)
    {
      if (event.getBlock().getRelative(BlockFace.DOWN, i).getTypeId() == 159)
      {

        aboveTurf = true;
        break;
      }
    }
    if (!aboveTurf)
    {
      UtilPlayer.message(event.getPlayer(), F.main("Game", "You cannot build this high above Turf."));
      event.setCancelled(true);
      return;
    }
  }
  
  @EventHandler
  public void BlockDamage(ProjectileHitEvent event)
  {
    if (event.getEntity().getShooter() == null) {
      return;
    }
    if (!(event.getEntity() instanceof Arrow)) {
      return;
    }
    if (!(event.getEntity().getShooter() instanceof Player)) {
      return;
    }
    Player shooter = (Player)event.getEntity().getShooter();
    final GameTeam team = GetTeam(shooter);
    if (team == null) {
      return;
    }
    final Arrow arrow = (Arrow)event.getEntity();
    
    this.Manager.GetPlugin().getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
    {
      public void run()
      {
        try
        {
          EntityArrow entityArrow = ((CraftArrow)arrow).getHandle();
          
          Field fieldX = EntityArrow.class.getDeclaredField("d");
          Field fieldY = EntityArrow.class.getDeclaredField("e");
          Field fieldZ = EntityArrow.class.getDeclaredField("f");
          
          fieldX.setAccessible(true);
          fieldY.setAccessible(true);
          fieldZ.setAccessible(true);
          
          int x = fieldX.getInt(entityArrow);
          int y = fieldY.getInt(entityArrow);
          int z = fieldZ.getInt(entityArrow);
          
          Block block = arrow.getWorld().getBlockAt(x, y, z);
          
          if (block.getTypeId() == 35)
          {
            if ((block.getData() == 14) && (team.GetColor() != ChatColor.RED))
            {
              block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());
            }
            else if ((block.getData() == 3) && (team.GetColor() != ChatColor.AQUA))
            {
              block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.LAPIS_BLOCK.getId());
            }
            
            block.breakNaturally();
          }
          
          arrow.remove();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }, 0L);
  }
  
  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    if (event.GetCause() == EntityDamageEvent.DamageCause.FALL)
    {
      event.SetCancelled("No Fall");
      return;
    }
    
    if ((!this._fight) && ((event.GetCause() == EntityDamageEvent.DamageCause.PROJECTILE) || (event.GetCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)))
    {
      event.SetCancelled("Build Time");
      return;
    }
    
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.PROJECTILE)
    {
      event.AddMod("Turf Forts", "Nullify", -event.GetDamageInitial(), false);
      
      if (GetKit(damager).GetName().contains("Shredder"))
      {
        event.SetCancelled("Shredder");
        
        this.Manager.GetDamage().NewDamageEvent(event.GetDamageeEntity(), damager, null, 
          EntityDamageEvent.DamageCause.CUSTOM, 9.0D, true, true, false, 
          damager.getName(), "Barrage");
        
        return;
      }
      

      event.AddMod("Turf Forts", "One Hit Kill", 30.0D, false);

    }
    else if (event.GetCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
    {
      event.AddMod("Turf Forts", "Nullify", -event.GetDamageInitial(), false);
      
      if (mineplex.core.common.util.UtilGear.isMat(damager.getItemInHand(), Material.IRON_SWORD))
      {
        event.AddMod("Turf Forts", "One Hit Kill", 12.0D, false);
      }
      else
      {
        event.AddMod("Turf Forts", "One Hit Kill", 6.0D, false);
      }
    }
  }
  
  @EventHandler
  public void ScoreboardTitle(UpdateEvent event)
  {
    if (GetState() != Game.GameState.Live) {
      return;
    }
    
    if (this._phaseTime == 0L) {
      this._phaseTime = (System.currentTimeMillis() + this._buildTime);
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    
    if (!this._fight)
    {
      long time = this._buildTime - (System.currentTimeMillis() - this._phaseTime);
      
      long displayTime = Math.max(0L, time);
      GetObjectiveSide().setDisplayName(ChatColor.WHITE + C.Bold + "Build Time " + C.cGreen + C.Bold + UtilTime.MakeStr(displayTime));
      
      if (time <= 0L)
      {
        this._fight = true;
        this._lines += 1;
        
        Announce(" ");
        Announce(C.cWhite + C.Bold + "1 Kill" + C.cWhite + C.Bold + " = " + C.cWhite + C.Bold + GetLinesPerKill() + " Turf Lines");
        Announce(C.cWhite + C.Bold + "90 Seconds of " + C.cYellow + C.Bold + "Combat Time" + C.cWhite + C.Bold + " has begun!");
        Announce(" ");
        
        this._phaseTime = System.currentTimeMillis();
      }
      
    }
    else
    {
      long time = this._fightTime - (System.currentTimeMillis() - this._phaseTime);
      
      long displayTime = Math.max(0L, time);
      GetObjectiveSide().setDisplayName(ChatColor.WHITE + C.Bold + "Combat Time " + C.cGreen + C.Bold + UtilTime.MakeStr(displayTime));
      
      if (time <= 0L)
      {
        this._fight = false;
        
        Announce(" ");
        Announce(C.cWhite + C.Bold + "20 Seconds of " + C.cGreen + C.Bold + "Build Time" + C.cWhite + C.Bold + " has begun!");
        Announce(" ");
        
        this._phaseTime = System.currentTimeMillis();
        Iterator localIterator2;
        for (Iterator localIterator1 = GetTeamList().iterator(); localIterator1.hasNext(); 
            
            localIterator2.hasNext())
        {
          GameTeam team = (GameTeam)localIterator1.next();
          
          localIterator2 = team.GetPlayers(true).iterator(); continue;Player player = (Player)localIterator2.next();
          
          player.getInventory().addItem(new org.bukkit.inventory.ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.WOOL, team.GetColorData(), 16) });
        }
      }
    }
  }
  


  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (GameTeam team : GetTeamList())
    {
      String name = team.GetColor() + team.GetName();
      if (name.length() > 16) {
        name = name.substring(0, 16);
      }
      int lines = 0;
      if (team.GetColor() == ChatColor.RED) lines = GetRedLines(); else {
        lines = GetBlueLines();
      }
      Score score = GetObjectiveSide().getScore(name);
      score.setScore(lines);
    }
  }
  
  public int GetRedLines()
  {
    if (!InProgress()) {
      return 0;
    }
    if (this.xRed != 0)
    {
      return Math.abs(this._redBase.getBlockX() - this._red.getBlockX());
    }
    
    return Math.abs(this._redBase.getBlockZ() - this._red.getBlockZ());
  }
  
  public int GetBlueLines()
  {
    if (!InProgress()) {
      return 0;
    }
    if (this.xRed != 0)
    {
      return Math.abs(this._blueBase.getBlockX() - this._blue.getBlockX());
    }
    
    return Math.abs(this._blueBase.getBlockZ() - this._blue.getBlockZ());
  }
  
  public int GetLinesPerKill()
  {
    return this._lines;
  }
  

  @EventHandler
  public void PlayerDeath(PlayerDeathEvent event)
  {
    this._enemyTurf.remove(event.getEntity());
  }
  
  @EventHandler
  public void Territory(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FASTER)
      return;
    Iterator localIterator2;
    for (Iterator localIterator1 = GetTeamList().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      GameTeam team = (GameTeam)localIterator1.next();
      
      localIterator2 = team.GetPlayers(true).iterator(); continue;Player player = (Player)localIterator2.next();
      
      Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
      while ((block.getTypeId() != 159) && (block.getY() > 0)) {
        block = block.getRelative(BlockFace.DOWN);
      }
      if (block.getTypeId() != 0)
      {

        byte data = block.getData();
        

        if (this._enemyTurf.containsKey(player))
        {
          int time = (int)((System.currentTimeMillis() - ((Long)this._enemyTurf.get(player)).longValue()) / 2500L);
          
          if (time > 0) {
            this.Manager.GetCondition().Factory().Slow("Infiltrator Slow", player, player, 0.9D, time - 1, false, false, false, false);
          }
        }
        
        if (((team.GetColor() == ChatColor.RED) && (data == 3)) || ((team.GetColor() == ChatColor.AQUA) && (data == 14)))
        {


          if ((this._fight) && (GetKit(player) != null) && (GetKit(player).GetName().contains("Infil")))
          {

            if (!this._enemyTurf.containsKey(player)) {
              this._enemyTurf.put(player, Long.valueOf(System.currentTimeMillis()));
            }
            

          }
          else if (Recharge.Instance.use(player, "Territory Knockback", 2000L, false, false))
          {
            mineplex.core.common.util.UtilAction.velocity(player, UtilAlg.getTrajectory2d(player.getLocation(), team.GetSpawn()), 2.0D, false, 0.0D, 0.8D, 1.0D, true);
            
            player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_BASS, 2.0F, 1.0F);
            UtilPlayer.message(player, F.main("Game", "You cannot walk on the enemies turf!"));

          }
          

        }
        else if (((team.GetColor() == ChatColor.RED) && (data == 14)) || ((team.GetColor() == ChatColor.AQUA) && (data == 3)))
        {
          this._enemyTurf.remove(player);
        }
      }
    }
  }
  
  @EventHandler
  public void ItemRemoval(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Entity ent : this._red.getWorld().getEntities())
    {
      if (!(ent instanceof net.minecraft.server.v1_7_R3.Item)) {
        return;
      }
      if (ent.getTicksLived() > 40) {
        ent.remove();
      }
    }
  }
  
  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    if ((GetRedLines() == 0) || (GetTeam(ChatColor.RED).GetPlayers(true).size() == 0))
    {
      AnnounceEnd(GetTeam(ChatColor.AQUA));
    }
    else if ((GetBlueLines() == 0) || (GetTeam(ChatColor.AQUA).GetPlayers(true).size() == 0))
    {
      AnnounceEnd(GetTeam(ChatColor.RED));
    }
    else {
      return;
    }
    for (Iterator localIterator1 = GetTeamList().iterator(); localIterator1.hasNext(); 
        






        ???.hasNext())
    {
      GameTeam team = (GameTeam)localIterator1.next();
      
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
