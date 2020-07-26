package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.data.IcePathData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkIcePath extends Perk
{
  private HashSet<IcePathData> _data = new HashSet();
  


  public PerkIcePath()
  {
    super("Ice Path", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + "Ice Path" });
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
    if (mineplex.core.common.util.UtilBlock.usable(event.getClickedBlock())) {
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
    if (!Recharge.Instance.use(player, GetName(), 12000L, true, true)) {
      return;
    }
    player.setVelocity(new Vector(0, 0, 0));
    player.teleport(player.getLocation().add(0.0D, 0.75D, 0.0D));
    
    this._data.add(new IcePathData(player));
    

    UtilPlayer.message(player, F.main("Game", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Freeze(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    Iterator<IcePathData> dataIterator = this._data.iterator();
    
    while (dataIterator.hasNext())
    {
      IcePathData data = (IcePathData)dataIterator.next();
      
      Block block = data.GetNextBlock();
      
      if (block == null)
      {
        dataIterator.remove();
      }
      else
      {
        block.getWorld().playEffect(block.getLocation(), org.bukkit.Effect.STEP_SOUND, 79);
        this.Manager.GetBlockRestore().Add(block, 79, (byte)0, 6000L);
      }
    }
  }
}
