package mineplex.core.message;

import java.util.LinkedList;
import mineplex.core.MiniClientPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.message.Commands.AdminCommand;
import mineplex.core.message.Commands.MessageAdminCommand;
import mineplex.core.message.Commands.ResendAdminCommand;
import mineplex.core.message.Commands.ResendCommand;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageManager extends MiniClientPlugin<ClientMessage>
{
  private LinkedList<String> _randomMessage;
  private CoreClientManager _clientManager;
  
  public MessageManager(JavaPlugin plugin, CoreClientManager clientManager)
  {
    super("Message", plugin);
    
    this._clientManager = clientManager;
  }
  


  public void Enable()
  {
    this._randomMessage = new LinkedList();
    this._randomMessage.clear();
    this._randomMessage.add("Hello, do you have any wild boars for purchase?");
    this._randomMessage.add("There's a snake in my boot!");
    this._randomMessage.add("Monk, I need a Monk!");
    this._randomMessage.add("Hi, I'm from planet minecraft, op me plz dooooood!");
    this._randomMessage.add("Somebody's poisoned the waterhole!");
    this._randomMessage.add("MORE ORBZ MORE ORBZ MORE ORBZ MORE ORBZ!");
    this._randomMessage.add("Chiss is a chiss and chiss chiss.");
    this._randomMessage.add("*_*");
    this._randomMessage.add("#swag");
    this._randomMessage.add("Everything went better then I thought.");
    this._randomMessage.add("HAVE A CHICKEN!");
    this._randomMessage.add("follow me, i have xrays");
    this._randomMessage.add("I'm making a java");
    this._randomMessage.add("Do you talk to strangers?  I have candy if it helps.");
    this._randomMessage.add("Solid 2.9/10");
    this._randomMessage.add("close your eyes to sleep");
    this._randomMessage.add("I crashed because my internet ran out.");
    this._randomMessage.add("I saw morgan freeman on a breaking bad ad on a bus.");
    this._randomMessage.add("Where is the volume control?");
    this._randomMessage.add("I saw you playing on youtube with that guy and stuff.");
    this._randomMessage.add("Your worms must be worse than useless.");
    this._randomMessage.add("meow");
    this._randomMessage.add("7");
    this._randomMessage.add("Don't you wish your girlfriend was hot like me?");
    this._randomMessage.add("how do you play mindcrafts?");
    this._randomMessage.add("7 cats meow meow meow meow meow meow meow");
    this._randomMessage.add("For King Jonalon!!!!!");
    this._randomMessage.add("Do you like apples?");
    this._randomMessage.add("I'm Happy Happy Happy.");
    this._randomMessage.add("kthxbye");
  }
  

  public void AddCommands()
  {
    AddCommand(new mineplex.core.message.Commands.MessageCommand(this));
    AddCommand(new ResendCommand(this));
    
    AddCommand(new MessageAdminCommand(this));
    AddCommand(new ResendAdminCommand(this));
    

    AddCommand(new AdminCommand(this));
  }
  
  public void Help(Player caller, String message)
  {
    UtilPlayer.message(caller, F.main(this._moduleName, "Commands List:"));
    UtilPlayer.message(caller, F.help("/npc add <radius> <name>", "Right click mob to attach npc.", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/npc del ", "Right click npc to delete", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/npc clear", "Removes all npcs", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/npc home", " Teleport npcs to home locations.", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/npc reattach", "Attempt to reattach npcs to entities.", Rank.OWNER));
    
    if (message != null) {
      UtilPlayer.message(caller, F.main(this._moduleName, ChatColor.RED + message));
    }
  }
  
  public void Help(Player caller) {
    Help(caller, null);
  }
  











  public void DoMessage(Player from, Player to, String message)
  {
    UtilPlayer.message(from, C.cGold + "§l" + from.getName() + " > " + to.getName() + C.cYellow + " §l" + message);
    

    ((ClientMessage)Get(from)).LastTo = to.getName();
    

    if (to.getName().equals("Chiss"))
    {
      UtilPlayer.message(from, C.cPurple + "Chiss is often AFK or minimized, due to plugin development.");
      UtilPlayer.message(from, C.cPurple + "Please be patient if he does not reply instantly.");
    }
    

    if (to.getName().equals("defek7"))
    {
      UtilPlayer.message(from, C.cPurple + "defek7 is often AFK or minimized, due to plugin development.");
      UtilPlayer.message(from, C.cPurple + "Please be patient if he does not reply instantly.");
    }
    

    if (this._clientManager.Get(to).GetRank() == Rank.YOUTUBE)
    {
      if (!this._clientManager.Get(from).GetRank().Has(from, Rank.MODERATOR, true))
      {
        UtilPlayer.message(from, C.cPurple + "YouTubers cannot be private messaged.");
        return;
      }
    }
    








    from.playSound(to.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
    to.playSound(to.getLocation(), Sound.NOTE_PIANO, 2.0F, 2.0F);
    

    UtilPlayer.message(to, C.cGold + "§l" + from.getName() + " > " + to.getName() + C.cYellow + " §l" + message);
  }
  

  public void DoMessageAdmin(Player from, Player to, String message)
  {
    UtilPlayer.message(from, C.cPurple + "-> " + F.rank(this._clientManager.Get(to).GetRank()) + " " + to.getName() + " " + C.cPurple + message);
    

    for (Player staff : mineplex.core.common.util.UtilServer.getPlayers()) {
      if ((!to.equals(staff)) && 
        (!from.equals(staff)) && 
        (this._clientManager.Get(staff).GetRank().Has(Rank.HELPER))) {
        UtilPlayer.message(staff, F.rank(this._clientManager.Get(from).GetRank()) + " " + from.getName() + 
          C.cPurple + " -> " + F.rank(this._clientManager.Get(to).GetRank()) + " " + to.getName() + " " + C.cPurple + message);
      }
    }
    ((ClientMessage)Get(from)).LastAdminTo = to.getName();
    

    UtilPlayer.message(to, C.cPurple + "<- " + F.rank(this._clientManager.Get(from).GetRank()) + " " + from.getName() + " " + C.cPurple + message);
    

    from.playSound(to.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
    to.playSound(to.getLocation(), Sound.NOTE_PIANO, 2.0F, 2.0F);
  }
  




  protected ClientMessage AddPlayer(String player)
  {
    Set(player, new ClientMessage());
    return (ClientMessage)Get(player);
  }
  
  public LinkedList<String> GetRandomMessages()
  {
    return this._randomMessage;
  }
  
  public String GetRandomMessage()
  {
    if (this._randomMessage.isEmpty()) {
      return "meow";
    }
    return (String)this._randomMessage.get(UtilMath.r(this._randomMessage.size()));
  }
  
  public CoreClientManager GetClientManager()
  {
    return this._clientManager;
  }
}
