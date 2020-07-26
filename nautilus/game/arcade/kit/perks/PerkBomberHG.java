package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.List;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PerkBomberHG extends Perk
{
  private int _spawnRate;
  private int _max;
  
  public PerkBomberHG(int spawnRate, int max)
  {
    super("Explosives", new String[] {C.cGray + "Receive 1 TNT every " + spawnRate + " seconds. Maximum of " + max + "." });
    

    this._spawnRate = spawnRate;
    this._max = max;
  }
  
  public void Apply(Player player)
  {
    Recharge.Instance.use(player, GetName(), this._spawnRate * 1000, false, false);
  }
  
  @EventHandler
  public void TNTSpawn(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (this.Kit.HasKit(cur))
      {

        if (this.Manager.GetGame().IsAlive(cur))
        {

          if (Recharge.Instance.use(cur, GetName(), this._spawnRate * 1000, false, false))
          {

            if (!UtilInv.contains(cur, Material.TNT, (byte)0, this._max))
            {


              cur.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.TNT, 0, 1, F.item("Throwing TNT")) });
              
              cur.playSound(cur.getLocation(), org.bukkit.Sound.ITEM_PICKUP, 2.0F, 1.0F);
            } } } }
    }
  }
  
  @EventHandler
  public void TNTDrop(PlayerDropItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.TNT, (byte)0)) {
      return;
    }
    
    event.setCancelled(true);
    

    UtilPlayer.message(event.getPlayer(), F.main(GetName(), "You cannot drop " + F.item("Throwing TNT") + "."));
  }
  
  @EventHandler
  public void TNTDeathRemove(PlayerDeathEvent event)
  {
    HashSet<ItemStack> remove = new HashSet();
    
    for (ItemStack item : event.getDrops()) {
      if (UtilInv.IsItem(item, Material.TNT, (byte)0))
        remove.add(item);
    }
    for (ItemStack item : remove) {
      event.getDrops().remove(item);
    }
  }
}
