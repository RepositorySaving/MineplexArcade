package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilServer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
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
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkPaintballMachineGun extends Perk
{
  public PerkPaintballMachineGun()
  {
    super("Machine Gun", new String[] {C.cYellow + "Right-Click" + C.cGray + " to use " + C.cGreen + "Machine Gun", "Experience Bar represents weapon overheating." });
  }
  

  @EventHandler
  public void WeaponCooldown(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (this.Kit.HasKit(player))
      {

        if (Recharge.Instance.usable(player, "Cool"))
          player.setExp(Math.max(0.0F, player.getExp() - 0.02F));
      }
    }
  }
  
  @EventHandler
  public void Shoot(PlayerInteractEvent event) {
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand().getType() != Material.DIAMOND_BARDING) {
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
    
    if (!Recharge.Instance.use(player, GetName(), 80L, false, false)) {
      return;
    }
    ShootPaintball(player, team);
  }
  











  public void ShootPaintball(Player player, GameTeam team)
  {
    if (player.getExp() >= 0.97D)
      return;
    player.setExp((float)(player.getExp() + 0.025D));
    

    Vector rand = new Vector(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D);
    rand.multiply(0.25D);
    
    if (team.GetColor() == ChatColor.AQUA)
    {




      Projectile proj = player.launchProjectile(Snowball.class);
      proj.setVelocity(proj.getVelocity().multiply(1.6D).add(rand));
      

      player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.5F, 2.0F);


    }
    else
    {


      Projectile proj = player.launchProjectile(org.bukkit.entity.EnderPearl.class);
      proj.setVelocity(proj.getVelocity().multiply(1.6D).add(rand));
      

      player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 1.5F, 1.75F);
    }
    
    Recharge.Instance.useForce(player, "Cool", 250L);
  }
}
