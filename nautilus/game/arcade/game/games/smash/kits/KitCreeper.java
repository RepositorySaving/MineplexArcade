package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseCreeper;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkCreeperElectricity;
import nautilus.game.arcade.kit.perks.PerkCreeperExplode;
import nautilus.game.arcade.kit.perks.PerkCreeperSulphurBomb;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;












public class KitCreeper
  extends SmashKit
{
  public KitCreeper(ArcadeManager manager)
  {
    super(manager, "Creeper", KitAvailability.Green, new String[0], new Perk[] {new PerkSmashStats(6.0D, 1.65D, 0.4D, 3.5D), new PerkDoubleJump("Double Jump", 0.9D, 0.9D, false), new PerkCreeperElectricity(), new PerkCreeperSulphurBomb(), new PerkCreeperExplode() }, EntityType.CREEPER, new ItemStack(Material.TNT));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Sulphur Bomb", 
      
      new String[] {
      ChatColor.RESET + "Throw a small bomb of sulphur.", 
      ChatColor.RESET + "Explodes on contact with players,", 
      ChatColor.RESET + "dealing some damage and knockback." }) });
    


    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SPADE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Explosive Leap", 
      
      new String[] {
      ChatColor.RESET + "You freeze in location and charge up", 
      ChatColor.RESET + "for 1.5 seconds. Then you explode!", 
      ChatColor.RESET + "You are sent flying in the direction", 
      ChatColor.RESET + "you are looking, while opponents take", 
      ChatColor.RESET + "large damage and knockback." }) });
    


    if (this.Manager.GetGame().GetState() == Game.GameState.Recruit) {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.NETHER_STAR, 0, 1, 
        C.cYellow + C.Bold + "Passive" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Lightning Shield", 
        
        new String[] {
        ChatColor.RESET + "When attacked by a non-melee attack,", 
        ChatColor.RESET + "you gain Lightning Shield for 2 seconds.", 
        ChatColor.RESET, 
        ChatColor.RESET + "Lightning Shield blocks 1 melee attack,", 
        ChatColor.RESET + "striking lightning on the attacker." }) });
    }
    
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.LEATHER_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.LEATHER_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.LEATHER_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.LEATHER_BOOTS));
    

    DisguiseCreeper disguise = new DisguiseCreeper(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
  

  public int GetCost()
  {
    return 4000;
  }
}
