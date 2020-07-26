package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

public class PerkSkullShot extends Perk
{
  private HashMap<Player, Long> _shootTime = new HashMap();
  private HashMap<WitherSkull, Vector> _skullDir = new HashMap();
  


  public PerkSkullShot()
  {
    super("Skull Shot", new String[] {C.cYellow + "Shoot Bow" + C.cGray + " to use " + C.cGreen + "Skull Shot" });
  }
  

  @EventHandler
  public void Fire(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    Vector vel = event.getProjectile().getVelocity();
    event.getProjectile().remove();
    
    WitherSkull skull = (WitherSkull)player.launchProjectile(WitherSkull.class);
    skull.setDirection(vel);
    skull.setVelocity(vel);
    
    this._skullDir.put(skull, vel.multiply(0.5D));
    
    this._shootTime.put(player, Long.valueOf(System.currentTimeMillis()));
    

    player.getInventory().setHelmet(null);
    player.getInventory().remove(Material.ARROW);
    

    player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 1.0F, 1.0F);
    

    mineplex.core.common.util.UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void SkullDir(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    Iterator<WitherSkull> skullIterator = this._skullDir.keySet().iterator();
    
    while (skullIterator.hasNext())
    {
      WitherSkull skull = (WitherSkull)skullIterator.next();
      
      if (!skull.isValid())
      {
        skullIterator.remove();
      }
      else
      {
        skull.setVelocity((Vector)this._skullDir.get(skull));
        skull.setDirection((Vector)this._skullDir.get(skull));
      }
    }
  }
  
  @EventHandler
  public void ArrowRespawn(UpdateEvent event) {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    Iterator<Player> playerIterator = this._shootTime.keySet().iterator();
    
    while (playerIterator.hasNext())
    {
      Player player = (Player)playerIterator.next();
      
      if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._shootTime.get(player)).longValue(), 2000L))
      {

        playerIterator.remove();
        

        ItemStack head = ItemStackFactory.Instance.CreateStack(Material.SKULL_ITEM, (byte)1, 1);
        player.getInventory().setHelmet(head);
        player.getInventory().addItem(new ItemStack[] { new ItemStack(Material.ARROW) });
      }
    }
  }
  
  @EventHandler
  public void Death(PlayerDeathEvent event) {
    this._shootTime.remove(event.getEntity());
  }
}
