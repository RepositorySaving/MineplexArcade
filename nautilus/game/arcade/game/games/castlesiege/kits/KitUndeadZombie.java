package nautilus.game.arcade.game.games.castlesiege.kits;

import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseZombie;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkRegeneration;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;








public class KitUndeadZombie
  extends Kit
{
  public KitUndeadZombie(ArcadeManager manager)
  {
    super(manager, "Undead Zombie", KitAvailability.Blue, new String[] {"Regenerates rapidly" }, new Perk[] {new PerkRegeneration(2) }, EntityType.ZOMBIE, new ItemStack(Material.STONE_AXE));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.STONE_AXE) });
    
    DisguiseZombie disguise = new DisguiseZombie(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null)
    {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
      disguise.SetCustomNameVisible(true);
    }
    
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
