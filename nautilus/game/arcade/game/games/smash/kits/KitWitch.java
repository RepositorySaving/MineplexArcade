package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilServer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseWitch;
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
import nautilus.game.arcade.kit.perks.PerkBatWave;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import nautilus.game.arcade.kit.perks.PerkWitchPotion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;









public class KitWitch
  extends SmashKit
{
  public KitWitch(ArcadeManager manager)
  {
    super(manager, "Witch", KitAvailability.Blue, new String[0], new Perk[] {new PerkSmashStats(6.0D, 1.5D, 0.3D, 5.0D), new PerkDoubleJump("Double Jump", 0.9D, 0.9D, false), new PerkWitchPotion(), new PerkBatWave() }, EntityType.WITCH, new ItemStack(Material.POTION));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Daze Potion", 
      
      new String[] {
      ChatColor.RESET + "Throw a potion that damages and slows", 
      ChatColor.RESET + "anything it splashes onto!" }) });
    

    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SPADE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Bat Wave", 
      
      new String[] {
      ChatColor.RESET + "Release a wave of bats which give", 
      ChatColor.RESET + "damage and knockback to anything they", 
      ChatColor.RESET + "collide with." }) });
    

    if (this.Manager.GetGame().GetState() == Game.GameState.Recruit) {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.LEASH, 0, 1, 
        C.cYellow + C.Bold + "Double Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Bat Leash", 
        
        new String[] {
        ChatColor.RESET + "Attach a rope to your wave of bats,", 
        ChatColor.RESET + "causing you to be pulled behind them!" }) });
    }
    

    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
    

    DisguiseWitch disguise = new DisguiseWitch(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    this.Manager.GetDisguise().disguise(disguise);
  }
  
  @EventHandler
  public void Visuals(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (HasKit(player))
      {

        for (Player other : UtilServer.getPlayers())
        {
          UtilParticle.PlayParticle(other, UtilParticle.ParticleType.WITCH_MAGIC, player.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.5F, 0.25F, 0.0F, 2);
        }
      }
    }
  }
}
