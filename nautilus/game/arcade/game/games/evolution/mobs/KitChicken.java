package nautilus.game.arcade.game.games.evolution.mobs;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseChicken;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkConstructor;
import nautilus.game.arcade.kit.perks.PerkFallDamage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;










public class KitChicken
  extends Kit
{
  public KitChicken(ArcadeManager manager)
  {
    super(manager, "Chicken", KitAvailability.Hide, new String[] {"" }, new Perk[] {new PerkConstructor("Egg Pouch", 0.8D, 8, Material.EGG, "Egg", false), new PerkFallDamage(-2) }, EntityType.SLIME, null);
  }
  


  public void GiveItems(Player player)
  {
    UtilPlayer.message(player, C.Line);
    UtilPlayer.message(player, C.Bold + "You evolved into " + F.elem(new StringBuilder(String.valueOf(C.cGreen)).append(C.Bold).append(GetName()).toString()) + "!");
    UtilPlayer.message(player, F.elem("Right-Click with Eggs") + " to use " + F.elem("Throw Egg"));
    UtilPlayer.message(player, C.Line);
    
    player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_IDLE, 4.0F, 1.0F);
    

    DisguiseChicken disguise = new DisguiseChicken(player);
    disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
  
  @EventHandler
  public void EggHit(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetProjectile() == null) {
      return;
    }
    if (!(event.GetProjectile() instanceof Egg)) {
      return;
    }
    event.AddMod("Chicken Kit", "Egg", 1.0D, true);
  }
}
