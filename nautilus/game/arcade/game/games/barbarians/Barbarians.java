package nautilus.game.arcade.game.games.barbarians;

import java.util.ArrayList;
import java.util.HashSet;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.barbarians.kits.KitArcher;
import nautilus.game.arcade.game.games.barbarians.kits.KitBomber;
import nautilus.game.arcade.game.games.barbarians.kits.KitBrute;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemSpawnEvent;









public class Barbarians
  extends SoloGame
{
  public Barbarians(ArcadeManager manager)
  {
    super(manager, GameType.Barbarians, new Kit[] {new KitBrute(manager), new KitArcher(manager), new KitBomber(manager) }, new String[] {"Free for all fight to the death!", "Wooden blocks are breakable.", "Attack people to restore hunger!", "Last player alive wins!" });
    

    this.DamageTeamSelf = true;
    this.CompassEnabled = true;
    
    this.BlockBreakAllow.add(Integer.valueOf(5));
    this.BlockBreakAllow.add(Integer.valueOf(17));
    this.BlockBreakAllow.add(Integer.valueOf(18));
    this.BlockBreakAllow.add(Integer.valueOf(20));
    this.BlockBreakAllow.add(Integer.valueOf(30));
    this.BlockBreakAllow.add(Integer.valueOf(47));
    this.BlockBreakAllow.add(Integer.valueOf(53));
    this.BlockBreakAllow.add(Integer.valueOf(54));
    this.BlockBreakAllow.add(Integer.valueOf(58));
    this.BlockBreakAllow.add(Integer.valueOf(64));
    this.BlockBreakAllow.add(Integer.valueOf(83));
    this.BlockBreakAllow.add(Integer.valueOf(85));
    this.BlockBreakAllow.add(Integer.valueOf(96));
    this.BlockBreakAllow.add(Integer.valueOf(125));
    this.BlockBreakAllow.add(Integer.valueOf(126));
    this.BlockBreakAllow.add(Integer.valueOf(134));
    this.BlockBreakAllow.add(Integer.valueOf(135));
    this.BlockBreakAllow.add(Integer.valueOf(136));
  }
  


  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    if (GetPlayers(true).size() <= 1)
    {
      if (GetPlayers(true).size() == 1) {
        GetPlaces().add(0, (Player)GetPlayers(true).get(0));
      }
      if (GetPlaces().size() >= 1) {
        AddGems((Player)GetPlaces().get(0), 15.0D, "1st Place", false);
      }
      if (GetPlaces().size() >= 2) {
        AddGems((Player)GetPlaces().get(1), 10.0D, "2nd Place", false);
      }
      if (GetPlaces().size() >= 3) {
        AddGems((Player)GetPlaces().get(2), 5.0D, "3rd Place", false);
      }
      for (Player player : GetPlayers(false)) {
        if (player.isOnline())
          AddGems(player, 10.0D, "Participation", false);
      }
      SetState(Game.GameState.End);
      AnnounceEnd(GetPlaces());
    }
  }
  
  @EventHandler
  public void BlockDamage(BlockDamageEvent event)
  {
    event.setInstaBreak(true);
  }
  
  @EventHandler
  public void ItemSpawn(ItemSpawnEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void Hunger(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      if (player.getFoodLevel() <= 0)
      {
        this.Manager.GetDamage().NewDamageEvent(player, null, null, 
          EntityDamageEvent.DamageCause.STARVATION, 1.0D, false, true, false, 
          "Starvation", GetName());
      }
      
      UtilPlayer.hunger(player, -2);
    }
  }
  
  @EventHandler
  public void HungerRestore(CustomDamageEvent event)
  {
    Player damager = event.GetDamagerPlayer(true);
    if (damager != null) {
      UtilPlayer.hunger(damager, 2);
    }
  }
}
