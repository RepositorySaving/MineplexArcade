package nautilus.game.arcade.managers;

import java.util.Iterator;
import java.util.Set;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameServerConfig;
import nautilus.game.arcade.game.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class GameChatManager implements org.bukkit.event.Listener
{
  ArcadeManager Manager;
  
  public GameChatManager(ArcadeManager manager)
  {
    this.Manager = manager;
    
    this.Manager.GetPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  
  @EventHandler
  public void MeCancel(PlayerCommandPreprocessEvent event)
  {
    if (event.getMessage().startsWith("/me"))
    {
      event.getPlayer().sendMessage(mineplex.core.common.util.F.main("Mirror", "You can't see /me messages, are you a vampire?"));
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void HandleChat(AsyncPlayerChatEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player sender = event.getPlayer();
    

    String dead = "";
    if ((this.Manager.GetGame() != null) && 
      (this.Manager.GetGame().GetTeam(sender) != null) && 
      (!this.Manager.GetGame().IsAlive(sender))) {
      dead = C.cGray + "Dead ";
    }
    Rank rank = this.Manager.GetClients().Get(sender).GetRank();
    boolean ownsUltra = false;
    
    if (this.Manager.GetGame() != null) {
      ownsUltra = this.Manager.GetDonation().Get(sender.getName()).OwnsUnknownPackage(this.Manager.GetServerConfig().ServerType + " ULTRA");
    }
    
    String rankStr = "";
    if ((rank != Rank.ALL) && (this.Manager.GetGame() != null) && (this.Manager.GetGame().GetType() != GameType.UHC)) {
      rankStr = rank.GetTag(true, true) + " ";
    }
    if ((ownsUltra) && (!rank.Has(Rank.ULTRA)) && (this.Manager.GetGame() != null) && (this.Manager.GetGame().GetType() != GameType.UHC)) {
      rankStr = Rank.ULTRA.GetTag(true, true) + " ";
    }
    
    event.setFormat(dead + rankStr + this.Manager.GetColor(sender) + "%1$s " + ChatColor.WHITE + "%2$s");
    

    if ((this.Manager.GetGame() != null) && (this.Manager.GetGame().GetState() == nautilus.game.arcade.game.Game.GameState.Live))
    {
      boolean globalMessage = false;
      

      GameTeam team = this.Manager.GetGame().GetTeam(sender);
      
      if (team != null)
      {

        if (event.getMessage().charAt(0) == '@')
        {
          event.setMessage(event.getMessage().substring(1, event.getMessage().length()));
          event.setFormat(C.cWhite + C.Bold + "Team" + " " + dead + rankStr + team.GetColor() + "%1$s " + C.cWhite + "%2$s");

        }
        else
        {
          globalMessage = true;
          event.setFormat(dead + rankStr + team.GetColor() + "%1$s " + C.cWhite + "%2$s");
          
          if (this.Manager.GetGame().GetType() == GameType.UHC) {
            event.setFormat(ChatColor.YELLOW + "%1$s " + ChatColor.WHITE + "%2$s");
          } else {
            event.setFormat(dead + rankStr + team.GetColor() + "%1$s " + C.cWhite + "%2$s");
          }
        }
      }
      if (globalMessage) {
        return;
      }
      
      Iterator<Player> recipientIterator = event.getRecipients().iterator();
      
      while (recipientIterator.hasNext())
      {
        Player receiver = (Player)recipientIterator.next();
        
        if (!this.Manager.GetClients().Get(receiver).GetRank().Has(Rank.MODERATOR))
        {

          if ((this.Manager.GetGame().GetTeam(receiver) != null) && (this.Manager.GetGame().GetTeam(sender) != this.Manager.GetGame().GetTeam(receiver))) {
            recipientIterator.remove();
          }
        }
      }
    }
  }
}
