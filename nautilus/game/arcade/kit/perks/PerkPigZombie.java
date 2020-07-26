package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguisePig;
import mineplex.core.disguise.disguises.DisguisePigZombie;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PerkPigZombie extends nautilus.game.arcade.kit.Perk
{
  public HashSet<Player> _active = new HashSet();
  



  public PerkPigZombie()
  {
    super("Nether Pig", new String[] {C.cGray + "Become Nether Pig when HP is below 4.", C.cGray + "Return to Pig when HP is above 6." });
  }
  

  @org.bukkit.event.EventHandler
  public void Check(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FASTER) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {


        if (this._active.contains(player))
        {
          this.Manager.GetCondition().Factory().Speed("Pig Zombie", player, player, 0.9D, 0, false, false, false);
          
          if (player.getHealth() >= 8.0D)
          {


            this._active.remove(player);
            

            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
            player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
            player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
            
            player.getInventory().remove(Material.DIAMOND_HELMET);
            player.getInventory().remove(Material.DIAMOND_CHESTPLATE);
            player.getInventory().remove(Material.DIAMOND_LEGGINGS);
            player.getInventory().remove(Material.DIAMOND_BOOTS);
            

            DisguisePig disguise = new DisguisePig(player);
            
            if (this.Manager.GetGame().GetTeam(player) != null) {
              disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
            } else {
              disguise.SetName(player.getName());
            }
            disguise.SetCustomNameVisible(true);
            this.Manager.GetDisguise().disguise(disguise);
            

            player.getWorld().playSound(player.getLocation(), Sound.PIG_IDLE, 2.0F, 1.0F);
            player.getWorld().playSound(player.getLocation(), Sound.PIG_IDLE, 2.0F, 1.0F);
            

            mineplex.core.common.util.UtilPlayer.message(player, F.main("Skill", "You returned to " + F.skill("Pig Form") + "."));
          }
          

        }
        else if ((player.getHealth() > 0.0D) && (player.getHealth() <= 4.0D))
        {


          this._active.add(player);
          

          player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
          player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
          player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
          player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
          

          DisguisePigZombie disguise = new DisguisePigZombie(player);
          
          if (this.Manager.GetGame().GetTeam(player) != null) {
            disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
          } else {
            disguise.SetName(player.getName());
          }
          disguise.SetCustomNameVisible(true);
          this.Manager.GetDisguise().disguise(disguise);
          

          player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2.0F, 1.0F);
          player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2.0F, 1.0F);
          

          mineplex.core.common.util.UtilPlayer.message(player, F.main("Skill", "You transformed into " + F.skill("Nether Pig Form") + "."));
        }
      }
    }
  }
  
  @org.bukkit.event.EventHandler
  public void Clean(PlayerDeathEvent event) {
    this._active.remove(event.getEntity());
  }
}
