package nautilus.game.arcade.game.games.smash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.smash.kits.KitSkeletalHorse;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class SuperSmash extends SoloGame
{
  private HashMap<Player, Integer> _lives = new HashMap();
  
  private ArrayList<String> _lastScoreboard = new ArrayList();
  































  public SuperSmash(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.Smash, new Kit[] {new nautilus.game.arcade.game.games.smash.kits.KitSkeleton(manager), new nautilus.game.arcade.game.games.smash.kits.KitGolem(manager), new nautilus.game.arcade.game.games.smash.kits.KitSpider(manager), new nautilus.game.arcade.game.games.smash.kits.KitSlime(manager), new nautilus.game.arcade.game.games.smash.kits.KitCreeper(manager), new nautilus.game.arcade.game.games.smash.kits.KitEnderman(manager), new nautilus.game.arcade.game.games.smash.kits.KitSnowman(manager), new nautilus.game.arcade.game.games.smash.kits.KitWolf(manager), new nautilus.game.arcade.game.games.smash.kits.KitBlaze(manager), new nautilus.game.arcade.game.games.smash.kits.KitWitch(manager), new nautilus.game.arcade.game.games.smash.kits.KitChicken(manager), new KitSkeletalHorse(manager), new nautilus.game.arcade.game.games.smash.kits.KitPig(manager), new nautilus.game.arcade.game.games.smash.kits.KitSkySquid(manager), new nautilus.game.arcade.game.games.smash.kits.KitWitherSkeleton(manager), new nautilus.game.arcade.game.games.smash.kits.KitMagmaCube(manager) }, new String[] {"Each player has 3 respawns", "Attack to restore hunger!", "Last player alive wins!" });
    


    this.DeathOut = false;
    
    this.DamageTeamSelf = true;
    
    this.CompassEnabled = true;
    
    this.SpawnDistanceRequirement = 16;
    
    this.InventoryOpen = false;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void GameStateChange(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    for (Player player : GetPlayers(true)) {
      this._lives.put(player, Integer.valueOf(4));
    }
  }
  
  @EventHandler
  public void PlayerOut(PlayerDeathEvent event) {
    if (!LoseLife(event.getEntity()))
    {
      SetPlayerState(event.getEntity(), nautilus.game.arcade.game.GameTeam.PlayerState.OUT);
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
  
  private boolean LoseLife(Player player)
  {
    int lives = GetLives(player) - 1;
    
    if (lives > 0)
    {
      UtilPlayer.message(player, C.cRed + C.Bold + "You have died!");
      UtilPlayer.message(player, C.cRed + C.Bold + "You have " + lives + " lives left!");
      player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 2.0F, 0.5F);
      
      this._lives.put(player, Integer.valueOf(lives));
      
      return true;
    }
    

    UtilPlayer.message(player, C.cRed + C.Bold + "You are out of the game!");
    player.playSound(player.getLocation(), Sound.EXPLODE, 2.0F, 1.0F);
    
    return false;
  }
  


  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    
    for (String string : this._lastScoreboard)
    {
      GetScoreboard().resetScores(string);
    }
    this._lastScoreboard.clear();
    
    if (GetPlayers(true).size() > 15)
    {
      String out = C.cGreen + "Players Alive";
      this._lastScoreboard.add(out);
      GetObjectiveSide().getScore(out).setScore(GetPlayers(true).size());
      
      out = C.cRed + "Players Dead";
      this._lastScoreboard.add(out);
      GetObjectiveSide().getScore(out).setScore(GetPlayers(false).size() - GetPlayers(true).size());

    }
    else
    {
      for (Player player : GetPlayers(true))
      {
        int lives = GetLives(player);
        String out;
        String out;
        if (lives >= 4) { out = C.cGreen + player.getName(); } else { String out;
          if (lives == 3) { out = C.cYellow + player.getName(); } else { String out;
            if (lives == 2) { out = C.cGold + player.getName(); } else { String out;
              if (lives == 1) { out = C.cRed + player.getName();
              } else { if (lives != 0) continue; out = C.cRed + player.getName();
              }
            }
          } }
        if (out.length() >= 16) {
          out = out.substring(0, 15);
        }
        this._lastScoreboard.add(out);
        
        GetObjectiveSide().getScore(out).setScore(lives);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void FallDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() == EntityDamageEvent.DamageCause.FALL) {
      event.SetCancelled("No Fall Damage");
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void Knockback(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetDamageePlayer() != null) {
      event.AddKnockback("Smash Knockback", 1.0D + 0.1D * (20.0D - event.GetDamageePlayer().getHealth()));
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void ArenaWalls(CustomDamageEvent event) {
    if (event.IsCancelled()) {
      return;
    }
    if ((event.GetCause() == EntityDamageEvent.DamageCause.VOID) || (event.GetCause() == EntityDamageEvent.DamageCause.LAVA))
    {
      event.GetDamageeEntity().eject();
      event.GetDamageeEntity().leaveVehicle();
      
      if (event.GetDamageePlayer() != null) {
        event.GetDamageeEntity().getWorld().strikeLightningEffect(event.GetDamageeEntity().getLocation());
      }
      event.AddMod("Smash", "Super Smash Mobs", 5000.0D, false);
    }
  }
  
  @EventHandler
  public void HealthChange(EntityRegainHealthEvent event)
  {
    if (event.getRegainReason() == org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.SATIATED) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void EntityDeath(EntityDeathEvent event) {
    event.getDrops().clear();
  }
  

  public void SetKit(Player player, Kit kit, boolean announce)
  {
    GameTeam team = GetTeam(player);
    if (team != null)
    {
      if (!team.KitAllowed(kit))
      {
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 2.0F, 0.5F);
        UtilPlayer.message(player, F.main("Kit", F.elem(team.GetFormattedName()) + " cannot use " + F.elem(new StringBuilder(String.valueOf(kit.GetFormattedName())).append(" Kit").toString()) + "."));
        return;
      }
    }
    
    this._playerKit.put(player, kit);
    
    if (announce)
    {
      player.playSound(player.getLocation(), Sound.ORB_PICKUP, 2.0F, 1.0F);
      UtilPlayer.message(player, F.main("Kit", "You equipped " + F.elem(new StringBuilder(String.valueOf(kit.GetFormattedName())).append(" Kit").toString()) + "."));
      kit.ApplyKit(player);
      mineplex.core.common.util.UtilInv.Update(player);
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void AbilityDescription(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (player.getItemInHand() == null) {
      return;
    }
    if (player.getItemInHand().getItemMeta() == null) {
      return;
    }
    if (player.getItemInHand().getItemMeta().getDisplayName() == null) {
      return;
    }
    if (player.getItemInHand().getItemMeta().getLore() == null) {
      return;
    }
    if ((this.Manager.GetGame() == null) || (this.Manager.GetGame().GetState() != Game.GameState.Recruit)) {
      return;
    }
    for (int i = player.getItemInHand().getItemMeta().getLore().size(); i <= 7; i++) {
      UtilPlayer.message(player, " ");
    }
    UtilPlayer.message(player, ArcadeFormat.Line);
    
    UtilPlayer.message(player, "§aAbility - §f§l" + player.getItemInHand().getItemMeta().getDisplayName());
    

    for (String line : player.getItemInHand().getItemMeta().getLore())
    {
      UtilPlayer.message(player, line);
    }
    
    UtilPlayer.message(player, ArcadeFormat.Line);
    
    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 2.0F);
    
    event.setCancelled(true);
  }
  
  @EventHandler
  public void ExplosionDamageCancel(EntityDamageEvent event)
  {
    if ((event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || (event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION))
    {
      event.setCancelled(true);
    }
  }
  

  public double GetKillsGems(Player killer, Player killed, boolean assist)
  {
    return 4.0D;
  }
  
  @EventHandler
  public void BlockFade(BlockFadeEvent event)
  {
    event.setCancelled(true);
  }
  
  private int hungerTick = 0;
  
  @EventHandler
  public void Hunger(UpdateEvent event) {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    this.hungerTick = ((this.hungerTick + 1) % 10);
    
    for (Player player : GetPlayers(true))
    {
      player.setSaturation(3.0F);
      player.setExhaustion(0.0F);
      
      if (player.getFoodLevel() <= 0)
      {
        this.Manager.GetDamage().NewDamageEvent(player, null, null, 
          EntityDamageEvent.DamageCause.STARVATION, 1.0D, false, true, false, 
          "Starvation", GetName());
        
        UtilPlayer.message(player, F.main("Game", "Attack other players to restore hunger!"));
      }
      
      if (this.hungerTick == 0) {
        UtilPlayer.hunger(player, -1);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void HungerRestore(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetDamagerPlayer(true) == null) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) {
      return;
    }
    if (!Recharge.Instance.use(damager, "Hunger Restore", 250L, false, false)) {
      return;
    }
    int amount = Math.max(1, (int)(event.GetDamage() / 2.0D));
    UtilPlayer.hunger(damager, amount);
  }
  
  public String GetMode()
  {
    return "Deathmatch";
  }
}
