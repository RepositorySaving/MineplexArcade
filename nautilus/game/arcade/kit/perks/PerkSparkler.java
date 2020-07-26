package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PerkSparkler extends Perk
{
  private HashSet<Item> _items = new HashSet();
  

  private int _spawnRate;
  
  private int _max;
  

  public PerkSparkler(int spawnRate, int max)
  {
    super("", new String[] {C.cGray + "Receive 1 Sparkler every " + spawnRate + " seconds. Maximum of " + max + ".", C.cYellow + "Click" + C.cGray + " with Sparkler to " + C.cGreen + "Throw Sparkler" });
    

    this._spawnRate = spawnRate;
    this._max = max;
  }
  
  public void Apply(Player player)
  {
    Recharge.Instance.use(player, GetName(), this._spawnRate * 1000, false, false);
  }
  
  @EventHandler
  public void SparklerSpawn(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : mineplex.core.common.util.UtilServer.getPlayers())
    {
      if (this.Kit.HasKit(cur))
      {

        if (this.Manager.GetGame().IsAlive(cur))
        {

          if (Recharge.Instance.use(cur, GetName(), this._spawnRate * 1000, false, false))
          {

            if (!UtilInv.contains(cur, Material.EMERALD, (byte)0, this._max))
            {


              cur.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.EMERALD, 0, 1, F.item("Throwing Sparkler")) });
              
              cur.playSound(cur.getLocation(), org.bukkit.Sound.ITEM_PICKUP, 2.0F, 1.0F);
            } } } }
    }
  }
  
  @EventHandler
  public void SparklerDrop(PlayerDropItemEvent event) {
    if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), Material.EMERALD, (byte)0)) {
      return;
    }
    
    event.setCancelled(true);
    

    mineplex.core.common.util.UtilPlayer.message(event.getPlayer(), F.main(GetName(), "You cannot drop " + F.item("Throwing Sparkler") + "."));
  }
  
  @EventHandler
  public void SparklerDeathRemove(PlayerDeathEvent event)
  {
    HashSet<ItemStack> remove = new HashSet();
    
    for (ItemStack item : event.getDrops()) {
      if (UtilInv.IsItem(item, Material.EMERALD, (byte)0))
        remove.add(item);
    }
    for (ItemStack item : remove) {
      event.getDrops().remove(item);
    }
  }
  
  @EventHandler
  public void SparklerInvClick(InventoryClickEvent event) {
    UtilInv.DisallowMovementOf(event, "Throwing Sparkler", Material.EMERALD, (byte)0, true);
  }
  
  @EventHandler
  public void SparklerThrow(PlayerInteractEvent event)
  {
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK) && 
      (event.getAction() != Action.LEFT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_AIR)) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!UtilInv.IsItem(player.getItemInHand(), Material.EMERALD, (byte)0)) {
      return;
    }
    if (!this.Kit.HasKit(player)) {
      return;
    }
    event.setCancelled(true);
    
    UtilInv.remove(player, Material.EMERALD, (byte)0, 1);
    UtilInv.Update(player);
    
    Item item = player.getWorld().dropItem(player.getEyeLocation().add(player.getLocation().getDirection()), 
      ItemStackFactory.Instance.CreateStack(Material.EMERALD, (byte)0, 1, F.item("Throwing Sparkler")));
    
    item.setPickupDelay(2000);
    
    this._items.add(item);
    
    mineplex.core.common.util.UtilAction.velocity(item, player.getLocation().getDirection(), 0.8D, false, 0.0D, 0.1D, 10.0D, false);
  }
  
  @EventHandler
  public void Sparkle(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    Iterator<Item> itemIterator = this._items.iterator();
    
    while (itemIterator.hasNext())
    {
      Item item = (Item)itemIterator.next();
      
      if ((!item.isValid()) || (item.getTicksLived() > 100))
      {
        item.remove();
        itemIterator.remove();
      }
      else
      {
        FireworkEffect effect = FireworkEffect.builder().withColor(org.bukkit.Color.GREEN).with(org.bukkit.FireworkEffect.Type.BURST).build();
        
        try
        {
          this.Manager.GetFirework().playFirework(item.getLocation(), effect);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  public HashSet<Item> GetItems() {
    return this._items;
  }
}
