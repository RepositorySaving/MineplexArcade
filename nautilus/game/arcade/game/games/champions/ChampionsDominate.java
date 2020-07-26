package nautilus.game.arcade.game.games.champions;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.core.combat.DeathMessageType;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.games.champions.kits.KitAssassin;
import nautilus.game.arcade.game.games.champions.kits.KitBrute;
import nautilus.game.arcade.game.games.champions.kits.KitKnight;
import nautilus.game.arcade.game.games.champions.kits.KitMage;
import nautilus.game.arcade.game.games.champions.kits.KitRanger;
import nautilus.game.arcade.game.games.common.Domination;
import nautilus.game.arcade.kit.Kit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;





public class ChampionsDominate
  extends Domination
{
  public ChampionsDominate(ArcadeManager manager)
  {
    super(manager, GameType.ChampionsDominate, new Kit[] {new KitBrute(manager), new KitRanger(manager), new KitKnight(manager), new KitMage(manager), new KitAssassin(manager) });
    

    this._help = 
      new String[] {
      "Capture Beacons faster with more people!", 
      "Make sure you use all of your Skill/Item Tokens", 
      "Collect Emeralds to get 300 Points", 
      "Collect Resupply Chests to restock your inventory", 
      "Customize your Class to suit your play style", 
      "Gold Sword boosts Sword Skill by 1 Level", 
      "Gold Axe boosts Axe Skill by 1 Level", 
      "Gold/Iron Weapons deal 6 damage", 
      "Diamond Weapons deal 7 damage" };
    


    this.Manager.GetDamage().UseSimpleWeaponDamage = false;
  }
  



  public void ValidateKit(Player player, GameTeam team)
  {
    if (GetKit(player) == null)
    {
      SetKit(player, GetKits()[2], true);
      player.closeInventory();
    }
  }
  

  public DeathMessageType GetDeathMessageType()
  {
    return DeathMessageType.Detailed;
  }
  
  @EventHandler
  public void WaterArrowCancel(EntityShootBowEvent event)
  {
    if (event.getEntity().getLocation().getBlock().isLiquid())
    {
      UtilPlayer.message(event.getEntity(), F.main("Game", "You cannot use your Bow while swimming."));
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void InventoryClick(InventoryClickEvent event)
  {
    if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
    {
      event.setCancelled(true);
      event.getWhoClicked().closeInventory();
    }
  }
}
