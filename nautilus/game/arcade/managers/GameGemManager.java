package nautilus.game.arcade.managers;

import java.util.HashMap;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerStateChangeEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameServerConfig;
import nautilus.game.arcade.game.GemData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameGemManager implements org.bukkit.event.Listener
{
  ArcadeManager Manager;
  boolean DoubleGem = false;
  
  public GameGemManager(ArcadeManager manager)
  {
    this.Manager = manager;
    
    this.Manager.GetPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  
  @EventHandler
  public void PlayerKillAward(CombatDeathEvent event)
  {
    Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    Player killed = (Player)event.GetEvent().getEntity();
    
    if (event.GetLog().GetKiller() != null)
    {
      Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
      
      if ((killer != null) && (!killer.equals(killed)))
      {

        game.AddGems(killer, game.GetKillsGems(killer, killed, false), "Kills", true);
        

        if (game.FirstKill)
        {
          game.AddGems(killer, 10.0D, "First Blood", false);
          
          game.FirstKill = false;
          
          game.Announce(F.main("Game", this.Manager.GetColor(killer) + killer.getName() + " drew first blood!"));
        }
      }
    }
    
    for (CombatComponent log : event.GetLog().GetAttackers())
    {
      if ((event.GetLog().GetKiller() == null) || (!log.equals(event.GetLog().GetKiller())))
      {

        Player assist = UtilPlayer.searchExact(log.GetName());
        

        if (assist != null)
          game.AddGems(assist, game.GetKillsGems(assist, killed, true), "Kill Assists", true);
      }
    }
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event) {
    Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    RewardGems(game, event.getPlayer(), true);
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.MONITOR)
  public void PlayerStateChange(PlayerStateChangeEvent event)
  {
    if (event.GetState() != nautilus.game.arcade.game.GameTeam.PlayerState.OUT) {
      return;
    }
    if (event.GetGame().GetType() == GameType.Paintball) {
      return;
    }
    RewardGems(event.GetGame(), event.GetPlayer(), false);
  }
  
  @EventHandler
  public void GameStateChange(GameStateChangeEvent event)
  {
    if (event.GetState() != nautilus.game.arcade.game.Game.GameState.Dead) {
      return;
    }
    for (Player player : UtilServer.getPlayers()) {
      RewardGems(event.GetGame(), player, true);
    }
  }
  
  public void RewardGems(Game game, Player player, boolean give) {
    if (game.GetType() == GameType.UHC) {
      return;
    }
    
    AnnounceGems(game, player, (HashMap)game.GetPlayerGems().get(player), give);
    

    if (give) {
      GiveGems(game, player, (HashMap)game.GetPlayerGems().remove(player), game.GemMultiplier);
    }
  }
  
  public void GiveGems(Game game, Player player, HashMap<String, GemData> gems, double gameMult) {
    if (gems == null) {
      return;
    }
    int total = 0;
    
    for (GemData data : gems.values()) {
      total += (int)data.Gems;
    }
    if (total <= 0) {
      total = 1;
    }
    if (this.Manager.GetClients().Get(player).GetRank().Has(Rank.HERO)) {
      total *= 3;
    }
    else if ((this.Manager.GetClients().Get(player).GetRank().Has(Rank.ULTRA)) || (this.Manager.GetDonation().Get(player.getName()).OwnsUnknownPackage(this.Manager.GetServerConfig().ServerType + " ULTRA"))) {
      total *= 2;
    }
    if (this.DoubleGem) {
      total *= 2;
    }
    total = (int)(total * gameMult);
    
    this.Manager.GetDonation().RewardGems(null, "Earned " + game.GetName(), player.getName(), total);
  }
  
  public void AnnounceGems(Game game, Player player, HashMap<String, GemData> gems, boolean give)
  {
    if (gems == null) {
      return;
    }
    player.playSound(player.getLocation(), org.bukkit.Sound.LEVEL_UP, 2.0F, 1.0F);
    
    UtilPlayer.message(player, "");
    UtilPlayer.message(player, ArcadeFormat.Line);
    
    UtilPlayer.message(player, "§aGame - §f§l" + game.GetName());
    UtilPlayer.message(player, "");
    
    int earnedGems = 0;
    
    for (String type : gems.keySet())
    {
      int gemCount = (int)((GemData)gems.get(type)).Gems;
      if (gemCount <= 0) {
        gemCount = 1;
      }
      earnedGems += gemCount;
      
      int amount = ((GemData)gems.get(type)).Amount;
      String amountStr = "";
      if (amount > 0) {
        amountStr = amount + " ";
      }
      UtilPlayer.message(player, F.elem(new StringBuilder(String.valueOf(C.cGreen)).append("+").append((int)(gemCount * game.GemMultiplier)).append(" Gems").toString()) + " for " + F.elem(new StringBuilder(String.valueOf(amountStr)).append(type).toString()));
    }
    
    earnedGems = (int)(earnedGems * game.GemMultiplier);
    

    if (this.Manager.GetClients().Get(player).GetRank().Has(Rank.HERO))
    {
      UtilPlayer.message(player, F.elem(new StringBuilder(String.valueOf(C.cGreen)).append("+").append(earnedGems * 2).append(" Gems").toString()) + " for " + F.elem(new StringBuilder(String.valueOf(C.cPurple)).append("Hero Rank 3x Gems").toString()));
      earnedGems *= 3;
    }
    else if ((this.Manager.GetClients().Get(player).GetRank().Has(Rank.ULTRA)) || (this.Manager.GetDonation().Get(player.getName()).OwnsUnknownPackage(this.Manager.GetServerConfig().ServerType + " ULTRA")))
    {
      UtilPlayer.message(player, F.elem(new StringBuilder(String.valueOf(C.cGreen)).append("+").append(earnedGems).append(" Gems").toString()) + " for " + F.elem(new StringBuilder(String.valueOf(C.cAqua)).append("Ultra Rank 2x Gems").toString()));
      earnedGems *= 2;
    }
    

    if (this.DoubleGem)
    {
      UtilPlayer.message(player, F.elem(new StringBuilder(String.valueOf(C.cGreen)).append("+").append(earnedGems).append(" Gems").toString()) + " for " + F.elem(new StringBuilder(String.valueOf(C.cDGreen)).append("Double Gem Weekend").toString()));
      earnedGems *= 2;
    }
    

    UtilPlayer.message(player, "");
    if (give)
    {
      UtilPlayer.message(player, F.elem(C.cWhite + "§lYou now have " + 
        C.cGreen + C.Bold + (this.Manager.GetDonation().Get(player.getName()).GetGems() + earnedGems) + " Gems"));
    }
    else
    {
      UtilPlayer.message(player, F.elem(C.cWhite + "§lGame is still in progress..."));
      UtilPlayer.message(player, F.elem(C.cWhite + "§lYou may earn more " + C.cGreen + C.Bold + "Gems" + C.cWhite + C.Bold + " when its completed."));
    }
    
    UtilPlayer.message(player, ArcadeFormat.Line);
  }
}
