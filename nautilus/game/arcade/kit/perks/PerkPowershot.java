package nautilus.game.arcade.kit.perks;

import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PerkPowershot extends Perk
{
  private WeakHashMap<Player, Integer> _charge = new WeakHashMap();
  private WeakHashMap<Player, Long> _chargeLast = new WeakHashMap();
  
  private WeakHashMap<Arrow, Integer> _arrows = new WeakHashMap();
  

  private int _max;
  
  private long _tick;
  

  public PerkPowershot(int max, long tick)
  {
    super("Power Shot", new String[] {C.cYellow + "Charge" + C.cGray + " your Bow to use " + C.cGreen + "Power Shot", "Arrows deal up to +15 damage" });
    

    this._max = max;
    this._tick = tick;
  }
  
  @EventHandler
  public void DrawBow(PlayerInteractEvent event)
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
      (UtilBlock.usable(event.getClickedBlock()))) {
      return;
    }
    
    this._charge.put(player, Integer.valueOf(0));
    this._chargeLast.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler
  public void ChargeBow(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {

      if (this._charge.containsKey(cur))
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
            } } } }
    }
  }
  
  @EventHandler
  public void FireBow(EntityShootBowEvent event) {
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
    
    this._arrows.put((Arrow)event.getProjectile(), (Integer)this._charge.remove(player));
    this._chargeLast.put(player, Long.valueOf(System.currentTimeMillis()));
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGH)
  public void Damage(CustomDamageEvent event)
  {
    if (event.GetProjectile() == null) {
      return;
    }
    if (event.GetDamagerPlayer(true) == null) {
      return;
    }
    if (!(event.GetProjectile() instanceof Arrow)) {
      return;
    }
    Arrow arrow = (Arrow)event.GetProjectile();
    
    if (!this._arrows.containsKey(arrow)) {
      return;
    }
    int charge = ((Integer)this._arrows.remove(arrow)).intValue();
    
    event.AddMod("Power Shot", "Power Shot", charge * 3, true);
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Arrow> arrowIterator = this._arrows.keySet().iterator(); arrowIterator.hasNext();)
    {
      Arrow arrow = (Arrow)arrowIterator.next();
      
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
  }
}
