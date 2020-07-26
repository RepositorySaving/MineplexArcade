package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkBarrage extends Perk
{
  private WeakHashMap<Player, Integer> _charge = new WeakHashMap();
  private WeakHashMap<Player, Long> _chargeLast = new WeakHashMap();
  
  private HashSet<Player> _firing = new HashSet();
  private HashSet<Projectile> _arrows = new HashSet();
  
  private int _max;
  
  private long _tick;
  
  private boolean _remove;
  private boolean _noDelay;
  
  public PerkBarrage(int max, long tick, boolean remove, boolean noDelay)
  {
    super("Barrage", new String[] {C.cYellow + "Charge" + C.cGray + " your Bow to use " + C.cGreen + "Barrage" });
    

    this._max = max;
    this._tick = tick;
    this._remove = remove;
    this._noDelay = noDelay;
  }
  
  @EventHandler
  public void BarrageDrawBow(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if ((player.getItemInHand() == null) || (player.getItemInHand().getType() != Material.BOW)) {
      return;
    }
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!player.getInventory().contains(Material.ARROW)) {
      return;
    }
    if ((event.getClickedBlock() != null) && 
      (mineplex.core.common.util.UtilBlock.usable(event.getClickedBlock()))) {
      return;
    }
    
    this._charge.put(player, Integer.valueOf(0));
    this._chargeLast.put(player, Long.valueOf(System.currentTimeMillis()));
    this._firing.remove(player);
  }
  
  @EventHandler
  public void BarrageCharge(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : mineplex.core.common.util.UtilServer.getPlayers())
    {

      if (this._charge.containsKey(cur))
      {

        if (!this._firing.contains(cur))
        {


          if (((Integer)this._charge.get(cur)).intValue() < this._max)
          {


            if (((Integer)this._charge.get(cur)).intValue() == 0 ? 
            
              UtilTime.elapsed(((Long)this._chargeLast.get(cur)).longValue(), 1000L) : 
              



              UtilTime.elapsed(((Long)this._chargeLast.get(cur)).longValue(), this._tick))
            {



              if ((cur.getItemInHand() == null) || (cur.getItemInHand().getType() != Material.BOW))
              {
                this._charge.remove(cur);
                this._chargeLast.remove(cur);

              }
              else
              {
                this._charge.put(cur, Integer.valueOf(((Integer)this._charge.get(cur)).intValue() + 1));
                this._chargeLast.put(cur, Long.valueOf(System.currentTimeMillis()));
                

                cur.playSound(cur.getLocation(), Sound.CLICK, 1.0F, 1.0F + 0.1F * ((Integer)this._charge.get(cur)).intValue());
              } } } } }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void BarrageFireBow(EntityShootBowEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (!this.Manager.GetGame().IsLive()) {
      return;
    }
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!(event.getProjectile() instanceof Arrow)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this._charge.containsKey(player)) {
      return;
    }
    
    this._firing.add(player);
    this._chargeLast.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void BarrageArrows(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    HashSet<Player> remove = new HashSet();
    
    for (Player cur : this._firing)
    {
      if ((!this._charge.containsKey(cur)) || (!this._chargeLast.containsKey(cur)))
      {
        remove.add(cur);


      }
      else if ((cur.getItemInHand() == null) || (cur.getItemInHand().getType() != Material.BOW))
      {
        remove.add(cur);
      }
      else
      {
        int arrows = ((Integer)this._charge.get(cur)).intValue();
        if (arrows <= 0)
        {
          remove.add(cur);
        }
        else
        {
          this._charge.put(cur, Integer.valueOf(arrows - 1));
          

          Vector random = new Vector((Math.random() - 0.5D) / 10.0D, (Math.random() - 0.5D) / 10.0D, (Math.random() - 0.5D) / 10.0D);
          Projectile arrow = cur.launchProjectile(Arrow.class);
          arrow.setVelocity(cur.getLocation().getDirection().add(random).multiply(3));
          this._arrows.add(arrow);
          cur.getWorld().playSound(cur.getLocation(), Sound.SHOOT_ARROW, 1.0F, 1.0F);
        }
      } }
    for (Player cur : remove)
    {
      this._charge.remove(cur);
      this._chargeLast.remove(cur);
      this._firing.remove(cur);
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void BarrageDamageTime(CustomDamageEvent event)
  {
    if (!this._noDelay) {
      return;
    }
    if (event.GetProjectile() == null) {
      return;
    }
    if (event.GetDamagerPlayer(true) == null) {
      return;
    }
    if (!(event.GetProjectile() instanceof Arrow)) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    event.SetCancelled("Barrage Cancel");
    
    event.GetProjectile().remove();
    

    this.Manager.GetDamage().NewDamageEvent(event.GetDamageeEntity(), damager, null, 
      org.bukkit.event.entity.EntityDamageEvent.DamageCause.THORNS, event.GetDamage(), true, true, false, 
      damager.getName(), GetName());
  }
  
  @EventHandler
  public void BarrageProjectileHit(ProjectileHitEvent event)
  {
    if ((this._remove) && 
      (this._arrows.remove(event.getEntity()))) {
      event.getEntity().remove();
    }
  }
  
  @EventHandler
  public void BarrageClean(UpdateEvent event) {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Projectile> arrowIterator = this._arrows.iterator(); arrowIterator.hasNext();)
    {
      Projectile arrow = (Projectile)arrowIterator.next();
      
      if ((arrow.isDead()) || (!arrow.isValid())) {
        arrowIterator.remove();
      }
    }
  }
  
  @EventHandler
  public void Quit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    
    this._charge.remove(player);
    this._chargeLast.remove(player);
    this._firing.remove(player);
  }
}
