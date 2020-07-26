package nautilus.game.arcade.game.games.squidshooter.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSquid;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkSquidSniper;
import nautilus.game.arcade.kit.perks.PerkSquidSwim;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitSniper
  extends Kit
{
  public KitSniper(ArcadeManager manager)
  {
    super(manager, "Squid Sniper", KitAvailability.Blue, new String[] {"Hitting someone with Ink Sniper recharges it!" }, new Perk[] {new PerkSquidSwim(), new PerkSquidSniper() }, EntityType.SQUID, new ItemStack(Material.INK_SACK));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE) });
    
    ItemStack helm = ItemStackFactory.Instance.CreateStack(Material.DIAMOND_HELMET);
    helm.addEnchantment(Enchantment.OXYGEN, 3);
    player.getInventory().setHelmet(helm);
    

    DisguiseSquid disguise = new DisguiseSquid(player);
    disguise.SetName(C.cWhite + player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
}
