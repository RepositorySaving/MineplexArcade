package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkSuperSquid
  extends Perk
{
  private HashMap<Player, Long> _active = new HashMap();
  


  public PerkSuperSquid()
  {
    super("Super Squid", new String[] {C.cYellow + "Hold Block" + C.cGray + " to use " + C.cGreen + "Super Squid" });
  }
  

  @EventHandler
  public void Activate(PlayerInteractEvent event)
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SWORD")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 8000L, true, true)) {
      return;
    }
    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
    
    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (this._active.containsKey(cur))
      {

        if (!cur.isBlocking())
        {
          this._active.remove(cur);


        }
        else if (UtilTime.elapsed(((Long)this._active.get(cur)).longValue(), 800L))
        {
          this._active.remove(cur);
        }
        else
        {
          UtilAction.velocity(cur, 0.6D, 0.1D, 1.0D, true);
          
          cur.getWorld().playSound(cur.getLocation(), Sound.SPLASH2, 0.2F, 1.0F);
          cur.getWorld().playEffect(cur.getLocation(), Effect.STEP_SOUND, 8);
        } }
    }
  }
  
  @EventHandler
  public void DamageCancel(CustomDamageEvent event) {
    if (this._active.containsKey(event.GetDamageeEntity())) {
      event.SetCancelled("Super Squid");
    }
  }
}
