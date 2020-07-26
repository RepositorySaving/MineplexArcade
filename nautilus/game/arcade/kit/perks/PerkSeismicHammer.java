package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkSeismicHammer extends Perk
{
  public PerkSeismicHammer()
  {
    super("Seismic Slam", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Iron Axe to " + C.cGreen + "Seismic Hammer" });
  }
  

  @EventHandler
  public void Skill(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("IRON_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 10000L, true, true)) {
      return;
    }
    
    int damage = 8;
    double range = 10.0D;
    
    HashMap<LivingEntity, Double> targets = UtilEnt.getInRadius(player.getLocation(), range);
    for (LivingEntity cur : targets.keySet())
    {
      if (!(cur instanceof Player))
      {


        this.Manager.GetDamage().NewDamageEvent(cur, player, null, 
          EntityDamageEvent.DamageCause.CUSTOM, damage * ((Double)targets.get(cur)).doubleValue() + 0.5D, false, true, false, 
          player.getName(), GetName());
        

        UtilAction.velocity(cur, 
          UtilAlg.getTrajectory2d(player.getLocation().toVector(), cur.getLocation().toVector()), 
          2.2D * ((Double)targets.get(cur)).doubleValue(), true, 0.0D, 0.4D + 1.0D * ((Double)targets.get(cur)).doubleValue(), 1.6D, true);
      }
    }
    
    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ZOMBIE_METAL, 2.0F, 0.2F);
    for (Block cur : UtilBlock.getInRadius(player.getLocation(), 4.0D).keySet()) {
      if ((UtilBlock.airFoliage(cur.getRelative(BlockFace.UP))) && (!UtilBlock.airFoliage(cur))) {
        cur.getWorld().playEffect(cur.getLocation(), org.bukkit.Effect.STEP_SOUND, cur.getTypeId());
      }
    }
    UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
  }
}
