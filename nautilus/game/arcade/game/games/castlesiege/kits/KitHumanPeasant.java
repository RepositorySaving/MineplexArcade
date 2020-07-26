package nautilus.game.arcade.game.games.castlesiege.kits;

import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseWolf;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkKnockbackGive;
import nautilus.game.arcade.kit.perks.PerkStrength;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;










public class KitHumanPeasant
  extends Kit
{
  public KitHumanPeasant(ArcadeManager manager)
  {
    super(manager, "Castle Wolf", KitAvailability.Hide, new String[] {"OINK! OINK!" }, new Perk[] {new PerkStrength(1), new PerkKnockbackGive(2.0D) }, EntityType.ZOMBIE, new ItemStack(Material.IRON_HOE));
  }
  

  @EventHandler
  public void FireItemResist(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (HasKit(player))
      {

        this.Manager.GetCondition().Factory().FireItemImmunity(GetName(), player, player, 1.9D, false);
      }
    }
  }
  
  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BONE, 0, 0, "Wolf Bite") });
    
    player.setHealth(4.0D);
    
    DisguiseWolf disguise = new DisguiseWolf(player);
    disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
}
