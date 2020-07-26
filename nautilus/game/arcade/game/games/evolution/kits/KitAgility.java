package nautilus.game.arcade.game.games.evolution.kits;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSpeed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;














public class KitAgility
  extends Kit
{
  public KitAgility(ArcadeManager manager)
  {
    super(manager, "Agility", KitAvailability.Free, new String[] {"You are extremely agile and can double jump!" }, new Perk[] {new PerkDoubleJump("Double Jump", 0.8D, 0.8D, false), new PerkSpeed(0) }, EntityType.ZOMBIE, null);
  }
  
  public void GiveItems(Player player) {}
}
