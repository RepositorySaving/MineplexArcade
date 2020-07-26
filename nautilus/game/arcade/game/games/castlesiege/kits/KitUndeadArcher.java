package nautilus.game.arcade.game.games.castlesiege.kits;

import mineplex.core.common.util.UtilInv;
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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;








public class KitUndeadArcher
  extends Kit
{
  public KitUndeadArcher(ArcadeManager manager)
  {
    super(manager, "Undead Archer", KitAvailability.Green, new String[] {"Makes use of arrows scavenged from human archers." }, new Perk[] {new PerkIronSkin(1.0D) }, EntityType.SKELETON, new ItemStack(Material.BOW));
  }
  


  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.STONE_AXE) });
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW) });
    
    DisguiseSkeleton disguise = new DisguiseSkeleton(player);
    disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
  
  @EventHandler
  public void ArrowPickup(PlayerPickupItemEvent event)
  {
    if (event.getItem().getItemStack().getType() != Material.ARROW) {
      return;
    }
    if (!HasKit(event.getPlayer())) {
      return;
    }
    if (UtilInv.contains(event.getPlayer(), Material.ARROW, (byte)0, 4)) {
      return;
    }
    event.getItem().remove();
    
    event.getPlayer().getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.ARROW) });
    
    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 1.0F, 1.0F);
  }
}
