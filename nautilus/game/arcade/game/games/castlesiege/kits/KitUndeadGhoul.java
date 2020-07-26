package nautilus.game.arcade.game.games.castlesiege.kits;

import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguisePigZombie;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkLeap;
import nautilus.game.arcade.kit.perks.PerkSpeed;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;








public class KitUndeadGhoul
  extends Kit
{
  public KitUndeadGhoul(ArcadeManager manager)
  {
    super(manager, "Undead Ghoul", KitAvailability.Free, new String[] {"Weak, but able to jump around with ease." }, new Perk[] {new PerkLeap("Ghoul Leap", 1.2D, 0.8D, 8000L), new PerkSpeed(0) }, EntityType.PIG_ZOMBIE, new ItemStack(Material.STONE_AXE));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.STONE_AXE) });
    
    DisguisePigZombie disguise = new DisguisePigZombie(player);
    disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
  
  @EventHandler
  public void PickupArrow(PlayerPickupItemEvent event)
  {
    if (!HasKit(event.getPlayer())) {
      return;
    }
    if (event.getItem().getItemStack().getType() == Material.ARROW) {
      event.setCancelled(true);
    }
  }
}
