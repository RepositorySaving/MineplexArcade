package nautilus.game.arcade.kit.perks;

import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkBoneRush extends Perk implements mineplex.core.projectile.IThrown
{
  private WeakHashMap<Player, Long> _active = new WeakHashMap();
  
  private double yLimit = 0.25D;
  



  public PerkBoneRush()
  {
    super("Bone Rush", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Spade to use " + C.cGreen + "Bone Rush", C.cGray + "Crouch to avoid movement with " + C.cGreen + "Bone Rush" });
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
    if (!Recharge.Instance.use(player, GetName(), 10000L, true, true)) {
      return;
    }
    
    boolean infernal = false;
    for (Perk perk : this.Kit.GetPerks())
    {
      if ((perk instanceof PerkInfernalHorror))
      {
        infernal = ((PerkInfernalHorror)perk).IsActive(player);
      }
    }
    
    this._active.put(player, Long.valueOf(System.currentTimeMillis()));
    
    if (!infernal) {
      UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(GetName()) + "."));
    } else {
      UtilPlayer.message(player, F.main("Skill", "You used " + F.skill("Flame Rush") + "."));
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Player> playerIterator = this._active.keySet().iterator();
    
    while (playerIterator.hasNext())
    {
      Player player = (Player)playerIterator.next();
      
      if ((!player.isValid()) || (mineplex.core.common.util.UtilTime.elapsed(((Long)this._active.get(player)).longValue(), 1500L)))
      {
        playerIterator.remove();

      }
      else
      {
        boolean infernal = false;
        for (Perk perk : this.Kit.GetPerks())
        {
          if ((perk instanceof PerkInfernalHorror))
          {
            infernal = ((PerkInfernalHorror)perk).IsActive(player);
          }
        }
        

        if (!infernal) {
          player.getWorld().playSound(player.getLocation(), Sound.SKELETON_HURT, 0.4F, (float)(Math.random() + 1.0D));
        }
        else {
          player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 1.0F, (float)(Math.random() + 1.0D));
          player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1.0F, (float)(Math.random() + 1.0D));
        }
        

        Vector dir = player.getLocation().getDirection();
        if (dir.getY() > this.yLimit) {
          dir.setY(this.yLimit);
        }
        
        if (!player.isSneaking()) {
          UtilAction.velocity(player, dir, 0.6D, false, 0.0D, 0.1D, 0.3D, false);
        }
        
        for (int i = 0; i < 6; i++)
        {
          if (!infernal)
          {
            Item bone = player.getWorld().dropItem(player.getLocation().add(Math.random() * 5.0D - 2.5D, Math.random() * 3.0D, Math.random() * 5.0D - 2.5D), new ItemStack(Material.BONE));
            UtilAction.velocity(bone, dir, 0.6D + 0.3D * Math.random(), false, 0.0D, 0.1D + Math.random() * 0.05D, 0.3D, false);
            this.Manager.GetProjectile().AddThrow(bone, player, this, -1L, true, true, true, false, 1.0D);
          }
          else
          {
            Item fire = player.getWorld().dropItem(player.getLocation().add(Math.random() * 5.0D - 2.5D, Math.random() * 3.0D, Math.random() * 5.0D - 2.5D), new ItemStack(Material.FIRE));
            UtilAction.velocity(fire, dir, 0.6D + 0.3D * Math.random(), false, 0.0D, 0.1D + Math.random() * 0.05D, 0.3D, false);
            this.Manager.GetProjectile().AddThrow(fire, player, this, -1L, true, true, true, false, 1.0D);
          }
        }
      }
    }
  }
  
  @EventHandler
  public void Knockback(CustomDamageEvent event) {
    if ((event.GetReason() == null) || (!event.GetReason().contains(GetName()))) {
      return;
    }
    event.AddKnockback(GetName(), 10.0D);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    boolean burning = ((Item)data.GetThrown()).getItemStack().getType() == Material.FIRE;
    
    data.GetThrown().remove();
    
    if (target == null) {
      return;
    }
    
    this.Manager.GetDamage().NewDamageEvent(target, data.GetThrower(), null, 
      org.bukkit.event.entity.EntityDamageEvent.DamageCause.CUSTOM, 1.0D, false, true, false, 
      mineplex.core.common.util.UtilEnt.getName(data.GetThrower()), GetName());
    
    if (burning) {
      this.Manager.GetCondition().Factory().Ignite("Flame Rush", target, data.GetThrower(), 0.4D, true, false);
    }
    target.setVelocity(data.GetThrown().getVelocity());
  }
  

  public void Idle(ProjectileUser data)
  {
    data.GetThrown().remove();
  }
  

  public void Expire(ProjectileUser data)
  {
    data.GetThrown().remove();
  }
  
  @EventHandler
  public void Clean(PlayerDeathEvent event)
  {
    this._active.remove(event.getEntity());
  }
}
