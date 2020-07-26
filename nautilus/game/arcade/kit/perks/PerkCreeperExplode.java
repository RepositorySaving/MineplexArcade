package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseCreeper;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PerkCreeperExplode extends Perk
{
  private HashMap<Player, Long> _active = new HashMap();
  


  public PerkCreeperExplode()
  {
    super("Explode", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Shovel use " + C.cGreen + "Explosive Leap" });
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
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_SPADE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, GetName(), 8000L, true, true)) {
      return;
    }
    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
    
    IncreaseSize(player);
    
    UtilPlayer.message(player, F.main("Skill", "You are charging " + F.skill(GetName()) + "."));
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Player> chargeIterator = this._active.keySet().iterator();
    
    while (chargeIterator.hasNext())
    {
      Player player = (Player)chargeIterator.next();
      
      double elapsed = (System.currentTimeMillis() - ((Long)this._active.get(player)).longValue()) / 1000.0D;
      

      player.setVelocity(new Vector(0, 0, 0));
      

      player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.CREEPER_HISS, (float)(0.5D + elapsed), (float)(0.5D + elapsed));
      
      IncreaseSize(player);
      
      player.setExp(Math.min(0.99F, (float)(elapsed / 1.5D)));
      

      if (elapsed >= 1.5D)
      {

        chargeIterator.remove();
        

        DecreaseSize(player);
        

        player.getWorld().createExplosion(player.getLocation(), 2.0F);
        

        for (Entity ent : player.getWorld().getEntities())
        {
          if ((ent instanceof LivingEntity))
          {

            if (!ent.equals(player))
            {

              double dist = mineplex.core.common.util.UtilMath.offset(player.getLocation(), ent.getLocation());
              
              double maxRange = 8.0D;
              
              if (dist <= maxRange)
              {

                if ((!(ent instanceof Player)) || 
                  (this.Manager.GetGame().IsAlive((Player)ent)))
                {

                  LivingEntity livingEnt = (LivingEntity)ent;
                  
                  double scale = 0.1D + 0.9D * ((maxRange - dist) / maxRange);
                  

                  this.Manager.GetDamage().NewDamageEvent(livingEnt, player, null, 
                    EntityDamageEvent.DamageCause.CUSTOM, 20.0D * scale, true, true, false, 
                    player.getName(), GetName());
                } }
            } }
        }
        mineplex.core.common.util.UtilAction.velocity(player, 1.8D, 0.2D, 1.4D, true);
        

        UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
      }
    }
  }
  
  public DisguiseCreeper GetDisguise(Player player) {
    mineplex.core.disguise.disguises.DisguiseBase disguise = this.Manager.GetDisguise().getDisguise(player);
    if (disguise == null) {
      return null;
    }
    if (!(disguise instanceof DisguiseCreeper)) {
      return null;
    }
    return (DisguiseCreeper)disguise;
  }
  
  public int GetSize(Player player)
  {
    DisguiseCreeper creeper = GetDisguise(player);
    if (creeper == null) { return 0;
    }
    return creeper.bV();
  }
  
  public void DecreaseSize(Player player)
  {
    DisguiseCreeper creeper = GetDisguise(player);
    if (creeper == null) { return;
    }
    creeper.a(-1);
    
    this.Manager.GetDisguise().updateDisguise(creeper);
  }
  
  public void IncreaseSize(Player player)
  {
    DisguiseCreeper creeper = GetDisguise(player);
    if (creeper == null) { return;
    }
    creeper.a(1);
    
    this.Manager.GetDisguise().updateDisguise(creeper);
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event)
  {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 2.5D);
  }
  
  @EventHandler
  public void Death(PlayerDeathEvent event)
  {
    if (!this.Kit.HasKit(event.getEntity())) {
      return;
    }
    this._active.remove(event.getEntity());
    
    DecreaseSize(event.getEntity());
  }
}
