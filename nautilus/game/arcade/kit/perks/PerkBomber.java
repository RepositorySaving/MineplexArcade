package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkBomber extends Perk
{
  private HashMap<Entity, Player> _tntMap = new HashMap();
  
  private int _spawnRate;
  
  private int _max;
  
  private int _fuse;
  

  public PerkBomber(int spawnRate, int max, int fuse)
  {
    super("Bomber", new String[] {C.cGray + "Receive 1 TNT every " + spawnRate + " seconds. Maximum of " + max + ".", C.cYellow + "Click" + C.cGray + " with TNT to " + C.cGreen + "Throw TNT" });
    

    this._spawnRate = spawnRate;
    this._max = max;
    this._fuse = fuse;
  }
  
  public void Apply(Player player)
  {
    Recharge.Instance.use(player, GetName(), this._spawnRate * 1000, false, false);
  }
  
  @EventHandler
  public void TNTSpawn(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
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
  
  @EventHandler
  public void TNTInvClick(InventoryClickEvent event) {
    UtilInv.DisallowMovementOf(event, "Throwing TNT", Material.TNT, (byte)0, true);
  }
  
  @EventHandler
  public void TNTThrow(PlayerInteractEvent event)
  {
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
    
    if (!this.Manager.GetGame().CanThrowTNT(player.getLocation()))
    {

      UtilPlayer.message(event.getPlayer(), F.main(GetName(), "You cannot use " + F.item("Throwing TNT") + " here."));
      return;
    }
    
    UtilInv.remove(player, Material.TNT, (byte)0, 1);
    UtilInv.Update(player);
    
    TNTPrimed tnt = (TNTPrimed)player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), TNTPrimed.class);
    
    if (this._fuse != -1) {
      tnt.setFuseTicks(this._fuse);
    }
    mineplex.core.common.util.UtilAction.velocity(tnt, player.getLocation().getDirection(), 0.5D, false, 0.0D, 0.1D, 10.0D, false);
    
    this._tntMap.put(tnt, player);
  }
  
  @EventHandler
  public void ExplosionPrime(ExplosionPrimeEvent event)
  {
    if (!this._tntMap.containsKey(event.getEntity())) {
      return;
    }
    Player player = (Player)this._tntMap.remove(event.getEntity());
    
    for (Player other : UtilPlayer.getNearby(event.getEntity().getLocation(), 14.0D))
    {
      this.Manager.GetCondition().Factory().Explosion("Throwing TNT", other, player, 50, 0.1D, false, false);
    }
  }
}
