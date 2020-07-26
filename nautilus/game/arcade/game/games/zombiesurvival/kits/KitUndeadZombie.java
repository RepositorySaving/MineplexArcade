package nautilus.game.arcade.game.games.zombiesurvival.kits;

import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSkeleton;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkIronSkin;
import nautilus.game.arcade.kit.perks.PerkLeap;
import nautilus.game.arcade.kit.perks.PerkRegeneration;
import nautilus.game.arcade.kit.perks.PerkStrength;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;









public class KitUndeadZombie
  extends Kit
{
  public KitUndeadZombie(ArcadeManager manager)
  {
    super(manager, "Undead", KitAvailability.Hide, new String[] {"Just a standard Zombie..." }, new Perk[] {new PerkLeap("Leap", 1.0D, 1.0D, 8000L), new PerkStrength(1), new PerkIronSkin(1.0D), new PerkRegeneration(0) }, EntityType.ZOMBIE, new ItemStack(Material.STONE_AXE));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.STONE_AXE) });
    
    DisguiseSkeleton disguise = new DisguiseSkeleton(player);
    disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
}
