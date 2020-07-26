package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.fire.Fire;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkInfernoFinn extends Perk
{
  private HashMap<Player, Long> _active = new HashMap();
  


  public PerkInfernoFinn()
  {
    super("Inferno", new String[] {C.cYellow + "Block" + C.cGray + " with Gold Sword to use " + C.cGreen + "Inferno" });
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("GOLD_SWORD")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, "Inferno", 2000L, true, false)) {
      return;
    }
    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
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
        else if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._active.get(cur)).longValue(), 1000L))
        {
          this._active.remove(cur);

        }
        else
        {
          Item fire = cur.getWorld().dropItem(cur.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.FIRE));
          this.Manager.GetFire().Add(fire, cur, 0.7D, 0.0D, 2.0D, 2, "Inferno");
          
          fire.teleport(cur.getEyeLocation());
          double x = 0.07000000000000001D - UtilMath.r(14) / 100.0D;
          double y = 0.07000000000000001D - UtilMath.r(14) / 100.0D;
          double z = 0.07000000000000001D - UtilMath.r(14) / 100.0D;
          fire.setVelocity(cur.getLocation().getDirection().add(new Vector(x, y, z)).multiply(1.6D));
          

          cur.getWorld().playSound(cur.getLocation(), org.bukkit.Sound.GHAST_FIREBALL, 0.1F, 1.0F);
        } }
    }
  }
  
  @EventHandler
  public void Refresh(UpdateEvent event) {
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (!this._active.containsKey(player))
      {

        if (this.Kit.HasKit(player))
        {

          if (mineplex.core.common.util.UtilGear.isMat(player.getItemInHand(), Material.GOLD_SWORD))
          {

            if (player.getItemInHand().getDurability() == player.getItemInHand().getType().getMaxDurability()) {
              player.getItemInHand().setDurability((short)(player.getItemInHand().getDurability() + 1));
            } else {
              player.getItemInHand().setDurability((short)(player.getItemInHand().getDurability() - 1));
            }
          }
        }
      }
    }
  }
}
