package nautilus.game.arcade.game.games.evolution.kits;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkRecharge;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;













public class KitRecharge
  extends Kit
{
  public KitRecharge(ArcadeManager manager)
  {
    super(manager, "Stamina", KitAvailability.Free, new String[] {"You are able to use your abilities more often!" }, new Perk[] {new PerkRecharge(0.5D) }, EntityType.ZOMBIE, null);
  }
  
  public void GiveItems(Player player) {}
}
