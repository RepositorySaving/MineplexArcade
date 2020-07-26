package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseBase;
import mineplex.core.disguise.disguises.DisguiseHorse;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PerkHorseKick extends Perk
{
  private HashMap<Player, Long> _active = new HashMap();
  
  private HashMap<Entity, Long> _burning = new HashMap();
  


  public PerkHorseKick()
  {
    super("Bone Kick", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to use " + C.cGreen + "Bone Kick" });
  }
  

  @EventHandler
  public void Activate(PlayerInteractEvent event)
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 6000L, true, true)) {
      return;
    }
    
    DisguiseBase horse = this.Manager.GetDisguise().getDisguise(player);
    if ((horse != null) && ((horse instanceof DisguiseHorse)))
    {
      ((DisguiseHorse)horse).kick();
      this.Manager.GetDisguise().updateDisguise(horse);
    }
    

    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
    

    boolean infernal = false;
    for (Perk perk : this.Kit.GetPerks())
    {
      if ((perk instanceof PerkInfernalHorror))
      {
        infernal = ((PerkInfernalHorror)perk).IsActive(player);
      }
    }
    
    String name = GetName();
    if (infernal) {
      name = "Flame Kick";
    }
    
    Location loc = player.getLocation();
    loc.add(player.getLocation().getDirection().setY(0).normalize().multiply(1.5D));
    loc.add(0.0D, 0.8D, 0.0D);
    
    for (??? = player.getWorld().getEntities().iterator(); ((Iterator)???).hasNext();) { Entity other = (Entity)((Iterator)???).next();
      
      if ((other instanceof LivingEntity))
      {

        if ((!(other instanceof Player)) || 
          (this.Manager.GetGame().IsAlive((Player)other)))
        {

          if (!other.equals(player))
          {

            if (mineplex.core.common.util.UtilMath.offset(loc, other.getLocation()) <= 2.5D)
            {


              this.Manager.GetDamage().NewDamageEvent((LivingEntity)other, player, null, 
                org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, 6.0D, true, true, false, 
                player.getName(), name);
              

              player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.SKELETON_HURT, 4.0F, 0.6F);
              player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.SKELETON_HURT, 4.0F, 0.6F);
              

              UtilPlayer.message(other, F.main("Skill", F.name(player.getName()) + " hit you with " + F.skill(name) + "."));
              

              if (infernal)
              {
                this._burning.put(other, Long.valueOf(System.currentTimeMillis()));
                
                if ((other instanceof LivingEntity))
                  this.Manager.GetCondition().Factory().Ignite(name, (LivingEntity)other, player, 2.5D, true, false);
              }
            } }
        }
      }
    }
    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(name) + "."));
    

    this.Manager.GetCondition().Factory().Slow(name, player, player, 0.8D, 3, false, false, true, false);
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    
    Iterator<Player> playerIterator = this._active.keySet().iterator();
    
    while (playerIterator.hasNext())
    {
      Player player = (Player)playerIterator.next();
      
      if ((!player.isValid()) || (player.getHealth() <= 0.0D) || (UtilTime.elapsed(((Long)this._active.get(player)).longValue(), 1000L)))
      {
        playerIterator.remove();
        

        DisguiseBase horse = this.Manager.GetDisguise().getDisguise(player);
        if ((horse != null) && ((horse instanceof DisguiseHorse)))
        {
          ((DisguiseHorse)horse).stopKick();
          this.Manager.GetDisguise().updateDisguise(horse);
        }
        
        this.Manager.GetCondition().EndCondition(player, null, GetName());
      }
      else
      {
        Location loc = player.getLocation();
        loc.add(player.getLocation().getDirection().setY(0).normalize().multiply(1.5D));
        loc.add(0.0D, 0.8D, 0.0D);
        
        UtilParticle.PlayParticle(UtilParticle.ParticleType.LARGE_SMOKE, loc, 0.3F, 0.3F, 0.3F, 0.0F, 2);
      }
    }
    

    Iterator<Entity> burningIterator = this._burning.keySet().iterator();
    
    while (burningIterator.hasNext())
    {
      Entity ent = (Entity)burningIterator.next();
      
      if ((!ent.isValid()) || (UtilTime.elapsed(((Long)this._burning.get(ent)).longValue(), 2500L)))
      {
        burningIterator.remove();
      }
      else
      {
        UtilParticle.PlayParticle(UtilParticle.ParticleType.FLAME, ent.getLocation().add(0.0D, 0.8D, 0.0D), 0.0F, 0.0F, 0.0F, 0.0F, 1);
      }
    }
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if ((event.GetReason() == null) || ((!event.GetReason().contains(GetName())) && (!event.GetReason().contains("Flame Kick")))) {
      return;
    }
    event.AddKnockback(GetName(), 4.0D);
  }
}
