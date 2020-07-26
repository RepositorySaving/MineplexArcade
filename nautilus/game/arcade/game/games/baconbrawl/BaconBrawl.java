package nautilus.game.arcade.game.games.baconbrawl;

import java.util.ArrayList;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.baconbrawl.kits.KitBabyPig;
import nautilus.game.arcade.game.games.baconbrawl.kits.KitPig;
import nautilus.game.arcade.game.games.baconbrawl.kits.KitSheepPig;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;







public class BaconBrawl
  extends SoloGame
{
  public BaconBrawl(ArcadeManager manager)
  {
    super(manager, GameType.BaconBrawl, new Kit[] {new KitPig(manager), new KitBabyPig(manager), new KitSheepPig(manager) }, new String[] {"Knock other pigs out of the arena!", "Last pig in the arena wins!" });
    

    this.DamageTeamSelf = true;
    this.HungerSet = 20;
    this.PrepareFreeze = false;
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
          EntityDamageEvent.DamageCause.STARVATION, 4.0D, false, true, false, 
          "Starvation", GetName());
      }
      
      UtilPlayer.hunger(player, -1);
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
  
  @EventHandler
  public void DamageEvent(CustomDamageEvent event) {
    if ((event.GetCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) || (event.GetCause() == EntityDamageEvent.DamageCause.CUSTOM) || (event.GetCause() == EntityDamageEvent.DamageCause.PROJECTILE))
    {
      event.GetDamageeEntity().setHealth(event.GetDamageeEntity().getMaxHealth());
      event.AddKnockback("Pig Wrestle", 2.0D);
    }
  }
}
