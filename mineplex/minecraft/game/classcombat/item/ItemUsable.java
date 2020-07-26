package mineplex.minecraft.game.classcombat.item;

import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilInv;
import mineplex.core.energy.Energy;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.classcombat.item.event.ItemTriggerEvent;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public abstract class ItemUsable
  extends Item
  implements IThrown
{
  private UtilEvent.ActionType _useAction;
  private boolean _useStock;
  private long _useDelay;
  private int _useEnergy;
  private UtilEvent.ActionType _throwAction;
  private boolean _throwStock;
  private long _throwDelay;
  private int _throwEnergy;
  private float _throwPower;
  private long _throwExpire;
  private boolean _throwPlayer;
  private boolean _throwBlock;
  private boolean _throwIdle;
  private boolean _throwPickup;
  
  public ItemUsable(ItemFactory factory, String name, String[] desc, Material type, int amount, boolean canDamage, int gemCost, int tokenCost, UtilEvent.ActionType useAction, boolean useStock, long useDelay, int useEnergy, UtilEvent.ActionType throwAction, boolean throwStock, long throwDelay, int throwEnergy, float throwPower, long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup)
  {
    super(factory, name, desc, type, amount, canDamage, gemCost, tokenCost);
    this._useAction = useAction;
    this._throwAction = throwAction;
    this._useStock = useStock;
    this._useDelay = useDelay;
    this._useEnergy = useEnergy;
    
    this._throwStock = throwStock;
    this._throwDelay = throwDelay;
    this._throwPower = throwPower;
    this._throwEnergy = throwEnergy;
    this._throwExpire = throwExpire;
    this._throwIdle = throwIdle;
    this._throwPlayer = throwPlayer;
    this._throwBlock = throwBlock;
    this._throwPickup = throwPickup;
  }
  
  @EventHandler
  public void Use(PlayerInteractEvent event)
  {
    if (this._useAction == null) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!UtilGear.isMat(player.getItemInHand(), GetType())) {
      return;
    }
    if (!UtilEvent.isAction(event, this._useAction)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    event.setCancelled(true);
    

    ItemTriggerEvent trigger = new ItemTriggerEvent(player, this);
    Bukkit.getServer().getPluginManager().callEvent(trigger);
    
    if (trigger.IsCancelled()) {
      return;
    }
    if (!EnergyRecharge(player, GetName(), this._useEnergy, this._useDelay)) {
      return;
    }
    if (this._useStock)
    {
      if (player.getItemInHand().getAmount() > 1) {
        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
      } else {
        player.setItemInHand(null);
      }
      UtilInv.Update(player);
    }
    
    UseAction(event);
  }
  
  public abstract void UseAction(PlayerInteractEvent paramPlayerInteractEvent);
  
  @EventHandler
  public void Throw(PlayerInteractEvent event)
  {
    if (this._throwAction == null) {
      return;
    }
    Player player = event.getPlayer();
    
    if (((CraftPlayer)player).getHandle().spectating) {
      return;
    }
    if (!UtilGear.isMat(player.getItemInHand(), GetType())) {
      return;
    }
    if (!UtilEvent.isAction(event, this._throwAction)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    event.setCancelled(true);
    

    ItemTriggerEvent trigger = new ItemTriggerEvent(player, this);
    Bukkit.getServer().getPluginManager().callEvent(trigger);
    
    if (trigger.IsCancelled()) {
      return;
    }
    if (!EnergyRecharge(player, GetName(), this._throwEnergy, this._throwDelay)) {
      return;
    }
    if (this._throwStock)
    {
      if (player.getItemInHand().getAmount() > 1) {
        player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
      } else {
        player.setItemInHand(null);
      }
      UtilInv.Update(player);
    }
    

    long expire = -1L;
    if (this._throwExpire >= 0L) {
      expire = System.currentTimeMillis() + this._throwExpire;
    }
    org.bukkit.entity.Item ent = player.getWorld().dropItem(player.getEyeLocation(), ItemStackFactory.Instance.CreateStack(GetType()));
    UtilAction.velocity(ent, player.getLocation().getDirection(), this._throwPower, false, 0.0D, 0.2D, 10.0D, false);
    this.Factory.Throw().AddThrow(ent, player, this, expire, this._throwPlayer, this._throwBlock, this._throwIdle, this._throwPickup, 1.0D);
    
    ThrowCustom(event, ent);
  }
  


  public void ThrowCustom(PlayerInteractEvent event, org.bukkit.entity.Item ent) {}
  

  public abstract void Collide(LivingEntity paramLivingEntity, Block paramBlock, ProjectileUser paramProjectileUser);
  

  public abstract void Idle(ProjectileUser paramProjectileUser);
  

  public abstract void Expire(ProjectileUser paramProjectileUser);
  

  private boolean EnergyRecharge(Player player, String ability, int energy, long recharge)
  {
    if (!this.Factory.Energy().Use(player, ability, energy, false, true)) {
      return false;
    }
    if (!Recharge.Instance.use(player, ability, recharge, recharge > 2000L, false)) {
      return false;
    }
    if (!this.Factory.Energy().Use(player, ability, energy, true, true)) {
      return false;
    }
    return true;
  }
}
