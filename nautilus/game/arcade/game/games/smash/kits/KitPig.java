package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguisePig;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkPigBaconBomb;
import nautilus.game.arcade.kit.perks.PerkPigBaconBounce;
import nautilus.game.arcade.kit.perks.PerkPigZombie;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;









public class KitPig
  extends SmashKit
{
  public KitPig(ArcadeManager manager)
  {
    super(manager, "Pig", KitAvailability.Blue, new String[0], new Perk[] {new PerkSmashStats(5.0D, 1.7D, 0.25D, 5.0D), new PerkDoubleJump("Double Jump", 0.9D, 0.9D, false), new PerkPigBaconBounce(), new PerkPigBaconBomb(), new PerkPigZombie() }, EntityType.PIG, new ItemStack(Material.PORK));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Bouncy Bacon", 
      
      new String[] {
      ChatColor.RESET + "Bouncy Bacon launches a peice of bacon,", 
      ChatColor.RESET + "dealing damage and knockback to enemies.", 
      ChatColor.RESET, 
      ChatColor.RESET + "Eat the bacon to restore some Energy.", 
      ChatColor.RESET + "Bacon that hit an enemy will restore Health." }) });
    


    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SPADE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Baby Bacon Bombs", 
      
      new String[] {
      ChatColor.RESET + "Give birth to a baby pig, giving", 
      ChatColor.RESET + "yourself a boost forwards. ", 
      ChatColor.RESET, 
      ChatColor.RESET + "Your baby pig will run to annoy", 
      ChatColor.RESET + "nearby enemies, exploding on them." }) });
    

    if (this.Manager.GetGame().GetState() == Game.GameState.Recruit) {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.PORK, 0, 1, 
        C.cYellow + C.Bold + "Passive" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Nether Pig", 
        
        new String[] {
        ChatColor.RESET + "When your health drops below 4, you morph", 
        ChatColor.RESET + "into a Nether Pig. This gives you Speed I,", 
        ChatColor.RESET + "10 Armor and half Energy costs for skills.", 
        ChatColor.RESET, 
        ChatColor.RESET + "When your health returns to 8, you return", 
        ChatColor.RESET + "back to Pig Form." }) });
    }
    
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
    

    DisguisePig disguise = new DisguisePig(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
  
  @EventHandler
  public void EnergyUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (HasKit(player))
      {

        player.setExp((float)Math.min(0.999D, player.getExp() + 0.005D));
      }
    }
  }
}
