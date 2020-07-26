package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseBase;
import mineplex.core.disguise.disguises.DisguisePigZombie;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkPigBaconBomb extends Perk
{
  private WeakHashMap<Player, HashSet<Pig>> _pigs = new WeakHashMap();
  


  public PerkPigBaconBomb()
  {
    super("Baby Bacon Bomb", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Spade to " + C.cGreen + "Baby Bacon Bomb" });
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SPADE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    float energy = 0.4F;
    
    DisguiseBase disguise = this.Manager.GetDisguise().getDisguise(player);
    if ((disguise != null) && ((disguise instanceof DisguisePigZombie))) {
      energy = 0.2F;
    }
    
    if (player.getExp() < energy)
    {
      UtilPlayer.message(player, F.main("Energy", "Not enough Energy to use " + F.skill(GetName()) + "."));
      return;
    }
    

    if (!Recharge.Instance.use(player, GetName(), 100L, false, false)) {
      return;
    }
    
    player.setExp(Math.max(0.0F, player.getExp() - energy));
    

    mineplex.core.common.util.UtilAction.velocity(player, player.getLocation().getDirection(), 0.8D, true, 0.9D, 0.0D, 1.0D, true);
    

    player.getWorld().playSound(player.getLocation(), Sound.PIG_IDLE, 2.0F, 0.75F);
    

    this.Manager.GetGame().CreatureAllowOverride = true;
    Pig pig = (Pig)player.getWorld().spawn(player.getLocation(), Pig.class);
    pig.setHealth(5.0D);
    pig.setVelocity(new Vector(0.0D, -0.4D, 0.0D));
    this.Manager.GetGame().CreatureAllowOverride = false;
    
    pig.setBaby();
    UtilEnt.Vegetate(pig);
    UtilEnt.ghost(pig, true, false);
    

    if (!this._pigs.containsKey(player)) {
      this._pigs.put(player, new HashSet());
    }
    ((HashSet)this._pigs.get(player)).add(pig);
    

    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Check(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK)
      return;
    Iterator<Pig> pigIterator;
    for (Iterator localIterator = this._pigs.keySet().iterator(); localIterator.hasNext(); 
        


        pigIterator.hasNext())
    {
      Player player = (Player)localIterator.next();
      
      pigIterator = ((HashSet)this._pigs.get(player)).iterator();
      
      continue;
      
      Pig pig = (Pig)pigIterator.next();
      
      if ((!pig.isValid()) || (pig.getTicksLived() > 120))
      {
        PigExplode(pigIterator, pig, player);
      }
      else
      {
        Player target = UtilPlayer.getClosest(pig.getLocation(), player);
        if (target != null)
        {

          UtilEnt.CreatureMoveFast(pig, target.getLocation(), 1.2F);
          
          if (UtilMath.offset(target, pig) < 2.0D) {
            PigExplode(pigIterator, pig, player);
          }
        }
      }
    }
  }
  
  public void PigExplode(Iterator<Pig> pigIterator, Pig pig, Player owner) {
    UtilParticle.PlayParticle(mineplex.core.common.util.UtilParticle.ParticleType.LARGE_EXPLODE, pig.getLocation().add(0.0D, 0.5D, 0.0D), 0.0F, 0.0F, 0.0F, 0.0F, 1);
    

    pig.getWorld().playSound(pig.getLocation(), Sound.EXPLODE, 0.6F, 2.0F);
    pig.getWorld().playSound(pig.getLocation(), Sound.PIG_DEATH, 1.0F, 2.0F);
    

    HashMap<LivingEntity, Double> targets = UtilEnt.getInRadius(pig.getLocation(), 4.0D);
    for (LivingEntity cur : targets.keySet())
    {
      if (!cur.equals(owner))
      {


        this.Manager.GetDamage().NewDamageEvent(cur, owner, null, 
          org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, 4.0D * ((Double)targets.get(cur)).doubleValue() + 2.0D, false, true, false, 
          owner.getName(), GetName());
      }
    }
    
    pigIterator.remove();
    pig.remove();
  }
}
