package nautilus.game.arcade.managers;

import java.util.ArrayList;
import mineplex.core.account.CoreClient;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.donation.Donor;
import mineplex.core.shop.page.ConfirmationPage;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameServerConfig;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.shop.KitPackage;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GamePlayerManager implements org.bukkit.event.Listener
{
  ArcadeManager Manager;
  
  public GamePlayerManager(ArcadeManager manager)
  {
    this.Manager = manager;
    
    this.Manager.GetPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  public void PlayerDeath(CombatDeathEvent event)
  {
    event.GetEvent().getEntity().setHealth(20.0D);
    

    if (this.Manager.GetGame() != null) {
      event.SetBroadcastType(this.Manager.GetGame().GetDeathMessageType());
    }
    
    if (event.GetLog().GetKiller() != null)
    {
      Player player = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
      if (player != null) {
        event.GetLog().SetKillerColor(this.Manager.GetColor(player));
      }
    }
    
    if ((event.GetEvent().getEntity() instanceof Player))
    {
      Player player = (Player)event.GetEvent().getEntity();
      if (player != null) {
        event.GetLog().SetKilledColor(this.Manager.GetColor(player));
      }
    }
  }
  
  @EventHandler
  public void PlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    

    this.Manager.GetLobby().AddPlayerToScoreboards(player, null);
    

    if ((this.Manager.GetGame() == null) || (!this.Manager.GetGame().InProgress()))
    {
      this.Manager.Clear(player);
      player.teleport(this.Manager.GetLobby().GetSpawn());
      return;
    }
    

    if (this.Manager.GetGame().IsAlive(player))
    {
      Location loc = (Location)this.Manager.GetGame().GetLocationStore().remove(player.getName());
      if ((loc != null) && (!loc.getWorld().getName().equalsIgnoreCase("world")))
      {
        player.teleport(loc);
      }
      else
      {
        this.Manager.Clear(player);
        player.teleport(this.Manager.GetGame().GetTeam(player).GetSpawn());
      }
    }
    else
    {
      this.Manager.Clear(player);
      this.Manager.GetGame().SetSpectator(player);
      UtilPlayer.message(player, F.main("Game", this.Manager.GetGame().GetName() + " is in progress, please wait for next game!"));
    }
    
    player.setScoreboard(this.Manager.GetGame().GetScoreboard());
  }
  
  @EventHandler
  public void PlayerRespawn(PlayerRespawnEvent event)
  {
    if ((this.Manager.GetGame() == null) || (!this.Manager.GetGame().InProgress()))
    {
      event.setRespawnLocation(this.Manager.GetLobby().GetSpawn());
      return;
    }
    
    Player player = event.getPlayer();
    
    if (this.Manager.GetGame().IsAlive(player))
    {
      event.setRespawnLocation(this.Manager.GetGame().GetTeam(player).GetSpawn());
    }
    else
    {
      this.Manager.GetGame().SetSpectator(player);
      
      event.setRespawnLocation(this.Manager.GetGame().GetSpectatorLocation());
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void TeamInteract(PlayerInteractEntityEvent event)
  {
    if (event.getRightClicked() == null) {
      return;
    }
    Player player = event.getPlayer();
    
    GameTeam team = this.Manager.GetLobby().GetClickedTeam(event.getRightClicked());
    
    if (team == null) {
      return;
    }
    TeamClick(player, team);
  }
  
  @EventHandler
  public void TeamDamage(CustomDamageEvent event)
  {
    Player player = event.GetDamagerPlayer(false);
    if (player == null) { return;
    }
    LivingEntity target = event.GetDamageeEntity();
    
    GameTeam team = this.Manager.GetLobby().GetClickedTeam(target);
    
    if (team == null) {
      return;
    }
    TeamClick(player, team);
  }
  
  public void TeamClick(Player player, GameTeam team)
  {
    if (this.Manager.GetGame() == null) {
      return;
    }
    if (this.Manager.GetGame().GetState() != nautilus.game.arcade.game.Game.GameState.Recruit) {
      return;
    }
    if (!this.Manager.GetGame().HasTeam(team)) {
      return;
    }
    AddTeamPreference(this.Manager.GetGame(), player, team);
  }
  
  public void AddTeamPreference(Game game, Player player, GameTeam team)
  {
    GameTeam past = game.GetTeamPreference(player);
    
    GameTeam current = game.GetTeam(player);
    if ((current != null) && (current.equals(team)))
    {
      game.RemoveTeamPreference(player);
      UtilPlayer.message(player, F.main("Team", "You are already on " + F.elem(team.GetFormattedName()) + "."));
      return;
    }
    
    if ((past == null) || (!past.equals(team)))
    {
      if (past != null)
      {
        game.RemoveTeamPreference(player);
        
        if (game.GetType() == GameType.UHC)
        {
          String players = "";
          for (Player other : (ArrayList)game.GetTeamPreferences().get(past)) {
            players = players + other.getName() + " ";
          }
          if (players.length() > 0) {
            players = players.substring(0, players.length() - 1);
          }
          for (Player other : (ArrayList)game.GetTeamPreferences().get(past)) {
            UtilPlayer.message(other, past.GetFormattedName() + " Team: " + ChatColor.RESET + players);
          }
        }
      }
      if (!game.GetTeamPreferences().containsKey(team)) {
        game.GetTeamPreferences().put(team, new ArrayList());
      }
      ((ArrayList)game.GetTeamPreferences().get(team)).add(player);
    }
    
    if (game.GetType() == GameType.UHC)
    {
      String players = "";
      for (Player other : (ArrayList)game.GetTeamPreferences().get(team)) {
        players = players + other.getName() + " ";
      }
      if (players.length() > 0) {
        players = players.substring(0, players.length() - 1);
      }
      for (Player other : (ArrayList)game.GetTeamPreferences().get(team)) {
        UtilPlayer.message(other, team.GetFormattedName() + " Team: " + ChatColor.RESET + players);
      }
    }
    else {
      UtilPlayer.message(player, F.main("Team", "You are " + F.elem(game.GetTeamQueuePosition(player)) + " in queue for " + F.elem(new StringBuilder(String.valueOf(team.GetFormattedName())).append(" Team").toString()) + "."));
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void KitInteract(PlayerInteractEntityEvent event)
  {
    if (event.getRightClicked() == null) {
      return;
    }
    Player player = event.getPlayer();
    
    if (player.getGameMode() != GameMode.SURVIVAL) {
      return;
    }
    Kit kit = this.Manager.GetLobby().GetClickedKit(event.getRightClicked());
    
    if (kit == null) {
      return;
    }
    KitClick(player, kit, event.getRightClicked());
    
    event.setCancelled(true);
  }
  
  @EventHandler
  public void KitDamage(CustomDamageEvent event)
  {
    if ((this.Manager.GetGame() != null) && (this.Manager.GetGame().InProgress())) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player player = event.GetDamagerPlayer(false);
    if (player == null) { return;
    }
    if (player.getGameMode() != GameMode.SURVIVAL) {
      return;
    }
    LivingEntity target = event.GetDamageeEntity();
    
    Kit kit = this.Manager.GetLobby().GetClickedKit(target);
    
    if (kit == null) {
      return;
    }
    KitClick(player, kit, target);
  }
  
  public void KitClick(final Player player, final Kit kit, final org.bukkit.entity.Entity entity)
  {
    kit.DisplayDesc(player);
    
    if (this.Manager.GetGame() == null) {
      return;
    }
    if (!this.Manager.GetGame().HasKit(kit)) {
      return;
    }
    
    CoreClient client = this.Manager.GetClients().Get(player);
    Donor donor = this.Manager.GetDonation().Get(player.getName());
    
    if ((kit.GetAvailability() == KitAvailability.Free) || (client.GetRank().Has(Rank.ULTRA)) || (donor.OwnsUnknownPackage(this.Manager.GetServerConfig().ServerType + " ULTRA")) || (donor.OwnsUnknownPackage(this.Manager.GetGame().GetName() + " " + kit.GetName())))
    {
      this.Manager.GetGame().SetKit(player, kit, true);
    }
    else if ((kit.GetAvailability() == KitAvailability.Green) && (donor.GetBalance(CurrencyType.Gems) > kit.GetCost()))
    {
      this.Manager.GetShop().OpenPageForPlayer(player, new ConfirmationPage(
        this.Manager, this.Manager.GetShop(), this.Manager.GetClients(), this.Manager.GetDonation(), new Runnable()
        {
          public void run()
          {
            if (player.isOnline())
            {
              GamePlayerManager.this.Manager.GetGame().SetKit(player, kit, true);
              ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entity.getEntityId(), ((CraftEntity)entity).getHandle().getDataWatcher(), true));
            }
          }
        }, null, new KitPackage(this.Manager.GetGame().GetName(), kit), CurrencyType.Gems, player));
    }
    else
    {
      player.playSound(player.getLocation(), org.bukkit.Sound.NOTE_BASS, 2.0F, 0.5F);
      
      if (kit.GetAvailability() == KitAvailability.Blue)
      {
        UtilPlayer.message(player, F.main("Kit", "This kit requires " + F.elem(new StringBuilder(String.valueOf(C.cAqua)).append("Ultra").toString()) + "."));
        UtilPlayer.message(player, F.main("Kit", "Purchase at " + F.elem(new StringBuilder(String.valueOf(C.cYellow)).append("www.mineplex.com/shop").toString())));
      }
      else
      {
        UtilPlayer.message(player, F.main("Kit", "You do not have enough " + F.elem(new StringBuilder(String.valueOf(C.cGreen)).append("Gems").toString()) + "."));
        UtilPlayer.message(player, F.main("Kit", "Purchase more at " + F.elem(new StringBuilder(String.valueOf(C.cYellow)).append("www.mineplex.com/shop").toString())));
      }
    }
  }
}
