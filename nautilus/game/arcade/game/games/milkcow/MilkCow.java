package nautilus.game.arcade.game.games.milkcow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.NullKit;
import nautilus.game.arcade.world.WorldData;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.Navigation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class MilkCow extends SoloGame
{
  private GameTeam _farmers;
  private GameTeam _cows;
  private ArrayList<Location> _chickens;
  private ArrayList<Location> _pigs;
  private ArrayList<Location> _villager;
  private ArrayList<CowScore> _ranks = new ArrayList();
  private ArrayList<String> _lastScoreboard = new ArrayList();
  
  private HashSet<Cow> _herd = new HashSet();
  








  private Objective _scoreObj;
  









  public MilkCow(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.MilkCow, new Kit[] {new nautilus.game.arcade.game.games.milkcow.kits.KitFarmerJump(manager), new NullKit(manager), new nautilus.game.arcade.game.games.milkcow.kits.KitCow(manager) }, new String[] {"Farmers get 1 point for drinking milk.", "You lose 5 points for dying!", "", "Cows get 1 point for killing farmers.", "Defend your herd to stop farmers!", "", "First player to 15 points wins!" });
    

    this.CompassEnabled = true;
    this.DeathOut = false;
    
    this._scoreObj = GetScoreboard().registerNewObjective("Milk", "dummy");
    this._scoreObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
  }
  

  public void ParseData()
  {
    this._chickens = this.WorldData.GetDataLocs("WHITE");
    this._pigs = this.WorldData.GetDataLocs("PINK");
    this._villager = this.WorldData.GetDataLocs("PURPLE");
  }
  

  public void RestrictKits()
  {
    for (Kit kit : GetKits())
    {
      for (GameTeam team : GetTeamList())
      {
        if (team.GetColor() == ChatColor.RED)
        {
          if (kit.GetName().contains("Farmer")) {
            team.GetRestrictedKits().add(kit);
          }
          
        }
        else if (kit.GetName().contains("Cow")) {
          team.GetRestrictedKits().add(kit);
        }
      }
    }
  }
  

  @EventHandler
  public void CustomTeamGeneration(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    this._farmers = ((GameTeam)GetTeamList().get(0));
    this._farmers.SetName("Farmers");
    

    this._cows = new GameTeam(this, "Cow", ChatColor.RED, this._farmers.GetSpawns());
    GetTeamList().add(this._cows);
    
    RestrictKits();
  }
  

  public GameTeam ChooseTeam(Player player)
  {
    return this._farmers;
  }
  
  @EventHandler
  public void SpawnAnimals(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    for (Location loc : this._chickens)
    {
      this.CreatureAllowOverride = true;
      Chicken ent = (Chicken)loc.getWorld().spawn(loc, Chicken.class);
      if (Math.random() > 0.75D)
      {
        ent.setBaby();
        ent.setAgeLock(true);
      }
      
      this.CreatureAllowOverride = false;
    }
    
    for (Location loc : this._pigs)
    {
      this.CreatureAllowOverride = true;
      Pig ent = (Pig)loc.getWorld().spawn(loc, Pig.class);
      if (Math.random() > 0.75D)
      {
        ent.setBaby();
        ent.setAgeLock(true);
      }
      
      this.CreatureAllowOverride = false;
    }
    
    for (Location loc : this._villager)
    {
      this.CreatureAllowOverride = true;
      Villager ent = (Villager)loc.getWorld().spawn(loc, Villager.class);
      if (Math.random() > 0.75D)
      {
        ent.setCustomName("Bob");
        ent.setCustomNameVisible(true);
      }
      
      this.CreatureAllowOverride = false;
    }
  }
  
  @EventHandler
  public void HerdUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    if (this._cows.GetPlayers(true).size() <= 0) {
      return;
    }
    Player host = (Player)this._cows.GetPlayers(true).get(0);
    
    Iterator<Cow> herdIterator = this._herd.iterator();
    
    while (herdIterator.hasNext())
    {
      Cow cow = (Cow)herdIterator.next();
      
      if (!cow.isValid())
      {
        cow.remove();
        herdIterator.remove();

      }
      else
      {
        EntityCreature ec = ((CraftCreature)cow).getHandle();
        Navigation nav = ec.getNavigation();
        
        if (UtilMath.offset(cow, host) > 6.0D)
        {
          if (UtilMath.offset(cow, host) > 16.0D)
          {
            Location target = cow.getLocation();
            
            target.add(UtilAlg.getTrajectory(cow, host).multiply(16));
            
            nav.a(target.getX(), target.getY(), target.getZ(), 1.799999952316284D);
          }
          else {
            nav.a(host.getLocation().getX(), host.getLocation().getY(), host.getLocation().getZ(), 1.399999976158142D);
          } }
      }
    }
    while (this._herd.size() < 5)
    {
      this.CreatureAllowOverride = true;
      Cow cow = (Cow)host.getWorld().spawn(host.getLocation(), Cow.class);
      if (Math.random() > 0.5D)
      {
        cow.setBaby();
        cow.setAgeLock(true);
      }
      
      this._herd.add(cow);
      this.CreatureAllowOverride = false;
    }
  }
  
  @EventHandler
  public void HerdDamage(CustomDamageEvent event)
  {
    if ((event.GetDamageeEntity() instanceof Creature)) {
      event.SetCancelled("Cow Immunity");
    }
  }
  
  @EventHandler
  public void CowUpdate(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    int req = 1;
    
    while ((this._cows.GetPlayers(true).size() < req) && (this._farmers.GetPlayers(true).size() > 0))
    {
      Player player = (Player)this._farmers.GetPlayers(true).get(UtilMath.r(this._farmers.GetPlayers(true).size()));
      SetCow(player, true);
    }
  }
  
  public void SetCow(Player player, boolean forced)
  {
    if (!GetPlaces().contains(player)) {
      GetPlaces().add(0, player);
    }
    SetPlayerTeam(player, this._cows);
    

    Kit newKit = GetKits()[2];
    
    SetKit(player, newKit, false);
    newKit.ApplyKit(player);
    

    for (Player other : UtilServer.getPlayers())
    {
      other.hidePlayer(player);
      other.showPlayer(player);
    }
    
    if (forced)
    {
      AddGems(player, 10.0D, "Forced Cow", false);
      
      Announce(F.main("Game", F.elem(new StringBuilder().append(this._farmers.GetColor()).append(player.getName()).toString()) + " has become " + 
        F.elem(new StringBuilder().append(this._cows.GetColor()).append(newKit.GetName()).toString()) + "."));
      
      player.getWorld().strikeLightningEffect(player.getLocation());
    }
  }
  
  @EventHandler
  public void GetMilk(PlayerInteractEntityEvent event)
  {
    if (!UtilGear.isMat(event.getPlayer().getItemInHand(), Material.BUCKET)) {
      return;
    }
    if (!(event.getRightClicked() instanceof Player)) {
      return;
    }
    Player cow = (Player)event.getRightClicked();
    
    if (!this._cows.HasPlayer(cow)) {
      return;
    }
    event.setCancelled(true);
    
    event.getPlayer().setItemInHand(new ItemStack(Material.MILK_BUCKET));
  }
  
  @EventHandler
  public void DrinkMilk(PlayerItemConsumeEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getItem().getType() != Material.MILK_BUCKET) {
      return;
    }
    SetScore(event.getPlayer(), GetScore(event.getPlayer()) + 1.0D);
    event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), org.bukkit.Sound.BURP, 2.0F, 1.0F);
    UtilPlayer.health(event.getPlayer(), 2.0D);
    
    AddGems(event.getPlayer(), 0.5D, "Milk Drunk", true);
  }
  
  @EventHandler
  public void LoseMilk(PlayerDeathEvent event)
  {
    SetScore(event.getEntity(), Math.max(0.0D, GetScore(event.getEntity()) - 5.0D));
  }
  
  @EventHandler
  public void KillFarmer(CombatDeathEvent event)
  {
    if (event.GetLog().GetKiller() == null) {
      return;
    }
    if (!event.GetLog().GetKiller().IsPlayer()) {
      return;
    }
    Player player = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    if (player == null) { return;
    }
    
    SetScore(player, GetScore(player) + 1.0D);
  }
  
  public void SetScore(Player player, double level)
  {
    this._scoreObj.getScore(player.getName()).setScore((int)level);
    

    for (CowScore score : this._ranks)
    {
      if (score.Player.equals(player))
      {
        score.Score = level;
        
        if (level == 15.0D) {
          End();
        }
        return;
      }
    }
    
    this._ranks.add(new CowScore(player, level));
  }
  
  public double GetScore(Player player)
  {
    if (!IsAlive(player)) {
      return 0.0D;
    }
    
    for (CowScore score : this._ranks)
    {
      if (score.Player.equals(player))
      {
        return score.Score;
      }
    }
    
    return 0.0D;
  }
  
  private void SortScores()
  {
    for (int i = 0; i < this._ranks.size(); i++)
    {
      for (int j = this._ranks.size() - 1; j > 0; j--)
      {
        if (((CowScore)this._ranks.get(j)).Score > ((CowScore)this._ranks.get(j - 1)).Score)
        {
          CowScore temp = (CowScore)this._ranks.get(j);
          this._ranks.set(j, (CowScore)this._ranks.get(j - 1));
          this._ranks.set(j - 1, temp);
        }
      }
    }
  }
  
  private void End()
  {
    SortScores();
    

    this._places.clear();
    for (int i = 0; i < this._ranks.size(); i++) {
      this._places.add(i, ((CowScore)this._ranks.get(i)).Player);
    }
    
    if (this._ranks.size() >= 1) {
      AddGems(((CowScore)this._ranks.get(0)).Player, 20.0D, "1st Place", false);
    }
    if (this._ranks.size() >= 2) {
      AddGems(((CowScore)this._ranks.get(1)).Player, 15.0D, "2nd Place", false);
    }
    if (this._ranks.size() >= 3) {
      AddGems(((CowScore)this._ranks.get(2)).Player, 10.0D, "3rd Place", false);
    }
    
    for (Player player : GetPlayers(false)) {
      if (player.isOnline())
        AddGems(player, 10.0D, "Participation", false);
    }
    SetState(Game.GameState.End);
    AnnounceEnd(this._places);
  }
  

  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    if (!mineplex.core.common.util.UtilTime.elapsed(GetStateTime(), 5000L)) {
      return;
    }
    if (this._cows.GetPlayers(true).size() <= 0) {
      End();
    }
    if (this._farmers.GetPlayers(true).size() <= 0)
    {
      SetScore((Player)this._cows.GetPlayers(true).get(0), 20.0D);
      End();
    }
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
    
    boolean added = false;
    

    for (Player player : GetPlayers(true))
    {
      int score = (int)GetScore(player);
      
      if (score > 0) {
        added = true;
      }
      GameTeam team = GetTeam(player);
      if (team != null)
      {
        String out = score + " " + team.GetColor() + player.getName();
        
        if (out.length() >= 16) {
          out = out.substring(0, 15);
        }
        this._lastScoreboard.add(out);
        
        GetObjectiveSide().getScore(out).setScore(score);
      }
    }
    if (!added)
    {
      String out = "DRINK ITS MILK!";
      
      this._lastScoreboard.add(out);
      
      GetObjectiveSide().getScore(out).setScore(1);
    }
  }
  

  public boolean CanJoinTeam(GameTeam team)
  {
    int cows = GetPlayers(true).size() / 5;
    
    if (team.GetColor() == ChatColor.RED) {
      return team.GetSize() < cows;
    }
    return team.GetSize() < GetPlayers(true).size() - cows;
  }
  
  @EventHandler
  public void BucketFill(PlayerBucketFillEvent event)
  {
    if (!IsLive())
    {
      event.setCancelled(true);
      UtilInv.Update(event.getPlayer());
    }
    
    if (event.getBlockClicked() == null) {
      return;
    }
    if ((event.getBlockClicked().getTypeId() != 8) && (event.getBlockClicked().getTypeId() != 9)) {
      return;
    }
    event.setCancelled(true);
    UtilInv.Update(event.getPlayer());
    
    if (event.getBlockClicked() != null) {
      event.getPlayer().sendBlockChange(event.getBlockClicked().getLocation(), 8, (byte)0);
    }
  }
}
