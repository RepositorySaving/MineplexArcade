package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PerkFletcher extends Perk
{
  private HashSet<Entity> _fletchArrows = new HashSet();
  
  private int _max = 0;
  private int _time = 0;
  
  private boolean _remove;
  

  public PerkFletcher(int time, int max, boolean remove)
  {
    super("Fletcher", new String[] {mineplex.core.common.util.C.cGray + "Receive 1 Arrow every " + time + " seconds. Maximum of " + max + "." });
    

    this._time = time;
    this._max = max;
    this._remove = remove;
  }
  
  @EventHandler
  public void FletchShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    for (int i = 0; i <= 8; i++) {
      if ((player.getInventory().getItem(i) != null) && 
        (UtilInv.IsItem(player.getInventory().getItem(i), Material.ARROW, (byte)1)))
      {
        this._fletchArrows.add(event.getProjectile());
        return;
      }
    }
  }
  
  @EventHandler
  public void FletchProjectileHit(ProjectileHitEvent event) {
    if ((this._remove) && 
      (this._fletchArrows.remove(event.getEntity()))) {
      event.getEntity().remove();
    }
  }
  
  @EventHandler
  public void Fletch(UpdateEvent event) {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : mineplex.core.common.util.UtilServer.getPlayers())
    {
      if (cur.getGameMode() == GameMode.SURVIVAL)
      {

        if (this.Kit.HasKit(cur))
        {

          if (this.Manager.GetGame().IsAlive(cur))
          {

            if (Recharge.Instance.use(cur, GetName(), this._time * 1000, false, false))
            {

              if (!UtilInv.contains(cur, Material.ARROW, (byte)1, this._max))
              {


                cur.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(262, 1, 1, F.item("Fletched Arrow")) });
                
                cur.playSound(cur.getLocation(), org.bukkit.Sound.ITEM_PICKUP, 2.0F, 1.0F);
              } } } } }
    }
  }
  
  @EventHandler
  public void FletchDrop(PlayerDropItemEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.ARROW, (byte)1)) {
      return;
    }
    
    event.setCancelled(true);
    

    UtilPlayer.message(event.getPlayer(), F.main(GetName(), "You cannot drop " + F.item("Fletched Arrow") + "."));
  }
  
  @EventHandler
  public void FletchDeathRemove(PlayerDeathEvent event)
  {
    HashSet<ItemStack> remove = new HashSet();
    
    for (ItemStack item : event.getDrops()) {
      if (UtilInv.IsItem(item, Material.ARROW, (byte)1))
        remove.add(item);
    }
    for (ItemStack item : remove) {
      event.getDrops().remove(item);
    }
  }
  
  @EventHandler
  public void FletchInvClick(InventoryClickEvent event) {
    UtilInv.DisallowMovementOf(event, "Fletched Arrow", Material.ARROW, (byte)1, true);
  }
  
  @EventHandler
  public void FletchClean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Entity> arrowIterator = this._fletchArrows.iterator(); arrowIterator.hasNext();)
    {
      Entity arrow = (Entity)arrowIterator.next();
      
      if ((arrow.isDead()) || (!arrow.isValid())) {
        arrowIterator.remove();
      }
    }
  }
}
