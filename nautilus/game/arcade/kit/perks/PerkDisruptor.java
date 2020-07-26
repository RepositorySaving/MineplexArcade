package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.util.Vector;

public class PerkDisruptor extends Perk
{
  private HashMap<org.bukkit.entity.Entity, Player> _tntMap = new HashMap();
  

  private int _spawnRate;
  
  private int _max;
  

  public PerkDisruptor(int spawnRate, int max)
  {
    super("Bomber", new String[] {C.cGray + "Receive 1 Disruptor every " + spawnRate + " seconds. Maximum of " + max + ".", C.cYellow + "Click" + C.cGray + " with TNT to " + C.cGreen + "Place Disruptor" });
    

    this._spawnRate = spawnRate;
    this._max = max;
  }
  
  public void Apply(Player player)
  {
    Recharge.Instance.use(player, GetName(), this._spawnRate * 1000, false, false);
  }
  
  @EventHandler
  public void Spawn(UpdateEvent event)
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

          if (Recharge.Instance.use(cur, GetName(), this._spawnRate * 1000, false, true))
          {

            if (!UtilInv.contains(cur, Material.TNT, (byte)0, this._max))
            {


              cur.getInventory().addItem(new org.bukkit.inventory.ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.TNT, 0, 1, F.item("Disruptor")) });
              
              cur.playSound(cur.getLocation(), Sound.ITEM_PICKUP, 2.0F, 1.0F);
            } } } }
    }
  }
  
  @EventHandler
  public void Place(PlayerInteractEvent event) {
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK) && 
      (event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_AIR)) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!UtilInv.IsItem(player.getItemInHand(), Material.TNT, (byte)0)) {
      return;
    }
    if (!this.Kit.HasKit(player)) {
      return;
    }
    event.setCancelled(true);
    
    UtilInv.remove(player, Material.TNT, (byte)0, 1);
    UtilInv.Update(player);
    
    Item item = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(Material.TNT));
    item.setVelocity(new Vector(0, 0, 0));
    
    this._tntMap.put(item, player);
  }
  
  @EventHandler
  public void Explode(PlayerPickupItemEvent event)
  {
    if (!this._tntMap.containsKey(event.getItem())) {
      return;
    }
    event.setCancelled(true);
    
    if (!this.Manager.GetGame().IsAlive(event.getPlayer())) {
      return;
    }
    if (event.getItem().getTicksLived() < 40) {
      return;
    }
    if (mineplex.core.common.util.UtilMath.offset(event.getItem(), event.getPlayer()) > 2.0D) {
      return;
    }
    
    if (event.getPlayer().equals(this._tntMap.get(event.getItem()))) {
      return;
    }
    this._tntMap.remove(event.getItem());
    event.getItem().remove();
    
    event.getPlayer().setVelocity(new Vector(0.0D, 0.5D, 0.0D));
    event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.EXPLODE, 1.0F, 2.0F);
    event.getPlayer().playEffect(EntityEffect.HURT);
    
    event.getPlayer().setSprinting(false);
  }
}
