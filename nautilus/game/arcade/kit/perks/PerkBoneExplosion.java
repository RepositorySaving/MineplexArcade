package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.blood.Blood;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkBoneExplosion extends Perk
{
  public PerkBoneExplosion()
  {
    super("Bone Explosion", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Bone Explosion" });
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 10000L, true, true)) {
      return;
    }
    HashMap<Player, Double> nearby = UtilPlayer.getInRadius(player.getLocation(), 10.0D);
    for (Player other : nearby.keySet())
    {
      if (!player.equals(other))
      {


        UtilPlayer.message(player, F.main("Game", F.elem(new StringBuilder().append(this.Manager.GetColor(player)).append(player.getName()).toString()) + " used " + F.skill(GetName()) + "."));
        

        this.Manager.GetDamage().NewDamageEvent(other, player, null, 
          EntityDamageEvent.DamageCause.CUSTOM, 6.0D * ((Double)nearby.get(other)).doubleValue(), true, true, false, 
          player.getName(), GetName());
      }
    }
    
    UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
    

    this.Manager.GetBlood().Effects(player.getLocation().add(0.0D, 0.5D, 0.0D), 48, 0.8D, org.bukkit.Sound.SKELETON_HURT, 2.0F, 1.2F, Material.BONE, (byte)0, 40, false);
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 4.0D);
  }
}
