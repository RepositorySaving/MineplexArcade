package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.recharge.Recharge;
import mineplex.core.recharge.RechargedEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkPaintballShotgun extends Perk
{
  public PerkPaintballShotgun()
  {
    super("Shotgun", new String[] {C.cYellow + "Right-Click" + C.cGray + " to use " + C.cGreen + "Shotgun" });
  }
  

  @EventHandler
  public void Recharge(RechargedEvent event)
  {
    if (!event.GetAbility().equals(GetName())) {
      return;
    }
    event.GetPlayer().playSound(event.GetPlayer().getLocation(), Sound.NOTE_STICKS, 2.0F, 1.0F);
    event.GetPlayer().playSound(event.GetPlayer().getLocation(), Sound.NOTE_STICKS, 2.0F, 1.5F);
  }
  
  @EventHandler
  public void Shoot(PlayerInteractEvent event)
  {
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand().getType() != Material.GOLD_BARDING) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    GameTeam team = this.Manager.GetGame().GetTeam(player);
    if (team == null) {
      return;
    }
    event.setCancelled(true);
    
    if (!Recharge.Instance.use(player, GetName(), 1400L, true, false)) {
      return;
    }
    for (int i = 0; i < 8; i++)
    {
      Vector rand = new Vector(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D);
      rand.multiply(0.4D);
      
      if (team.GetColor() == ChatColor.AQUA)
      {
        Projectile proj = player.launchProjectile(org.bukkit.entity.Snowball.class);
        proj.setVelocity(proj.getVelocity().multiply(1).add(rand));
        

        player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.8F, 1.0F);
      }
      else
      {
        Projectile proj = player.launchProjectile(org.bukkit.entity.EnderPearl.class);
        proj.setVelocity(proj.getVelocity().multiply(1).add(rand));
        

        player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.8F, 0.75F);
      }
    }
  }
}
