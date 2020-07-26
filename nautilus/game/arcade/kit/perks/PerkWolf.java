package nautilus.game.arcade.kit.perks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.Navigation;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkWolf extends Perk
{
  private HashMap<Wolf, Player> _owner = new HashMap();
  private HashMap<Wolf, LivingEntity> _tackle = new HashMap();
  
  private HashMap<Player, Long> _strike = new HashMap();
  
  private HashMap<Player, ArrayList<Long>> _repeat = new HashMap();
  
  private HashMap<LivingEntity, Long> _tackleStrike = new HashMap();
  





  public PerkWolf()
  {
    super("Wolf Skills", new String[] {C.cGray + "Attacks give +1 Damage for 3 seconds. Stacks.", C.cYellow + "Right-Click" + C.cGray + " with Axe to use " + C.cGreen + "Cub Tackle", C.cYellow + "Right-Click" + C.cGray + " with Spade to use " + C.cGreen + "Wolf Strike", C.cGray + "Wolf Strike deals 200% Knockback to tackled opponents." });
  }
  

  @EventHandler
  public void TackleTrigger(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, "Cub Tackle", 8000L, true, true)) {
      return;
    }
    
    this.Manager.GetGame().CreatureAllowOverride = true;
    Wolf wolf = (Wolf)player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), Wolf.class);
    this.Manager.GetGame().CreatureAllowOverride = false;
    
    wolf.setBaby();
    
    wolf.setAngry(true);
    
    UtilEnt.Vegetate(wolf);
    
    wolf.setMaxHealth(30.0D);
    wolf.setHealth(wolf.getMaxHealth());
    
    mineplex.core.common.util.UtilAction.velocity(wolf, player.getLocation().getDirection(), 1.8D, false, 0.0D, 0.2D, 1.2D, true);
    
    player.getWorld().playSound(wolf.getLocation(), Sound.WOLF_BARK, 1.0F, 1.8F);
    

    this._owner.put(wolf, player);
    

    UtilPlayer.message(player, F.main("Game", "You used " + F.skill("Cub Tackle") + "."));
  }
  
  @EventHandler
  public void TackleCollide(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    
    Iterator<Wolf> wolfIterator = this._owner.keySet().iterator();
    
    while (wolfIterator.hasNext())
    {
      Wolf wolf = (Wolf)wolfIterator.next();
      

      for (Player other : this.Manager.GetGame().GetPlayers(true)) {
        if ((other.getGameMode() == GameMode.SURVIVAL) && 
          (UtilEnt.hitBox(wolf.getLocation(), other, 2.0D, null)))
        {
          if (!other.equals(TackleGetOwner(wolf)))
          {

            TackleCollideAction(TackleGetOwner(wolf), other, wolf);
            wolfIterator.remove();
            return;
          } }
      }
      if ((!wolf.isValid()) || ((UtilEnt.isGrounded(wolf)) && (wolf.getTicksLived() > 20)))
      {
        wolf.remove();
        wolfIterator.remove();
      }
    }
  }
  
  public void TackleCollideAction(Player damager, LivingEntity damagee, Wolf wolf)
  {
    if (damager == null) {
      return;
    }
    this._tackle.put(wolf, damagee);
    
    wolf.setVelocity(new Vector(0.0D, -0.6D, 0.0D));
    damagee.setVelocity(new Vector(0, 0, 0));
    

    this.Manager.GetDamage().NewDamageEvent(damagee, damager, null, 
      EntityDamageEvent.DamageCause.CUSTOM, 5.0D, false, true, false, 
      damager.getName(), "Cub Tackle");
    

    damagee.getWorld().playSound(damagee.getLocation(), Sound.WOLF_GROWL, 1.5F, 1.5F);
    

    UtilPlayer.message(damager, F.main("Game", "You hit " + F.name(UtilEnt.getName(damagee)) + " with " + F.skill("Cub Tackle") + "."));
    UtilPlayer.message(damagee, F.main("Game", F.name(damager.getName()) + " hit you with " + F.skill("Cub Tackle") + "."));
  }
  
  @EventHandler
  public void TackleUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Wolf> wolfIterator = this._tackle.keySet().iterator();
    
    while (wolfIterator.hasNext())
    {
      Wolf wolf = (Wolf)wolfIterator.next();
      LivingEntity ent = (LivingEntity)this._tackle.get(wolf);
      
      if ((!wolf.isValid()) || (!ent.isValid()) || (wolf.getTicksLived() > 100))
      {
        wolf.remove();
        wolfIterator.remove();
      }
      else
      {
        if (mineplex.core.common.util.UtilMath.offset(wolf, ent) < 2.5D)
        {
          this.Manager.GetCondition().Factory().Slow("Cub Table", ent, wolf, 0.9D, 1, false, false, false, false);
          ent.setVelocity(new Vector(0.0D, -0.3D, 0.0D));
        }
        

        Location loc = ent.getLocation();
        loc.add(mineplex.core.common.util.UtilAlg.getTrajectory2d(ent, wolf).multiply(1));
        
        EntityCreature ec = ((CraftCreature)wolf).getHandle();
        Navigation nav = ec.getNavigation();
        nav.a(loc.getX(), loc.getY(), loc.getZ(), 1.0D);
      }
    }
  }
  
  public Player TackleGetOwner(Wolf wolf) {
    if (this._owner.containsKey(wolf)) {
      return (Player)this._owner.get(wolf);
    }
    return null;
  }
  
  @EventHandler
  public void TackleTargetCancel(EntityTargetEvent event)
  {
    if ((this._owner.containsKey(event.getEntity())) && 
      (((Player)this._owner.get(event.getEntity())).equals(event.getTarget()))) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void TackleDamage(CustomDamageEvent event) {
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    LivingEntity damager = event.GetDamagerEntity(false);
    if (damager == null) { return;
    }
    if ((damager instanceof Wolf)) {
      event.SetCancelled("Wolf Cub");
    }
  }
  
  @EventHandler
  public void StrikeTrigger(PlayerInteractEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
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
    if (!Recharge.Instance.use(player, "Wolf Strike", 8000L, true, true)) {
      return;
    }
    
    mineplex.core.common.util.UtilAction.velocity(player, player.getLocation().getDirection(), 1.6D, false, 1.0D, 0.2D, 1.2D, true);
    

    this._strike.put(player, Long.valueOf(System.currentTimeMillis()));
    
    player.getWorld().playSound(player.getLocation(), Sound.WOLF_BARK, 1.0F, 1.2F);
    

    UtilPlayer.message(player, F.main("Game", "You used " + F.skill("Wolf Strike") + "."));
  }
  
  @EventHandler
  public void StrikeEnd(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    
    Iterator<Player> playerIterator = this._strike.keySet().iterator();
    
    while (playerIterator.hasNext())
    {
      Player player = (Player)playerIterator.next();
      
      for (Player other : this.Manager.GetGame().GetPlayers(true)) {
        if ((!player.equals(other)) && 
          (other.getGameMode() == GameMode.SURVIVAL) && 
          (UtilEnt.hitBox(player.getLocation().add(0.0D, 1.0D, 0.0D), other, 2.0D, null)))
        {
          StrikeHit(player, other);
          playerIterator.remove();
          return;
        }
      }
      if (UtilEnt.isGrounded(player))
      {

        if (UtilTime.elapsed(((Long)this._strike.get(player)).longValue(), 1500L))
        {

          playerIterator.remove(); }
      }
    }
  }
  
  public void StrikeHit(Player damager, LivingEntity damagee) {
    damager.setVelocity(new Vector(0, 0, 0));
    

    Iterator<Wolf> wolfIterator = this._tackle.keySet().iterator();
    while (wolfIterator.hasNext())
    {
      Wolf wolf = (Wolf)wolfIterator.next();
      
      if (((LivingEntity)this._tackle.get(wolf)).equals(damagee))
      {
        wolf.remove();
        wolfIterator.remove();
        
        this._tackleStrike.put(damagee, Long.valueOf(System.currentTimeMillis()));
      }
    }
    
    this.Manager.GetDamage().NewDamageEvent(damagee, damager, null, 
      EntityDamageEvent.DamageCause.CUSTOM, 7.0D, true, true, false, 
      damager.getName(), "Wolf Strike");
    


    damagee.getWorld().playSound(damagee.getLocation(), Sound.WOLF_BARK, 1.5F, 1.0F);
    

    UtilPlayer.message(damager, F.main("Game", "You hit " + F.name(UtilEnt.getName(damagee)) + " with " + F.skill("Wolf Strike") + "."));
    UtilPlayer.message(damagee, F.main("Game", F.name(damager.getName()) + " hit you with " + F.skill("Wolf Strike") + "."));
  }
  
  @EventHandler
  public void StrikeKnockback(CustomDamageEvent event)
  {
    if ((event.GetReason() != null) && (event.GetReason().contains("Wolf Strike")))
    {
      if ((this._tackleStrike.containsKey(event.GetDamageeEntity())) && (!UtilTime.elapsed(((Long)this._tackleStrike.get(event.GetDamageeEntity())).longValue(), 100L)))
      {
        event.AddKnockback(GetName(), 3.0D);
        

        event.GetDamageeEntity().getWorld().playEffect(event.GetDamageeEntity().getLocation(), org.bukkit.Effect.STEP_SOUND, 55);
        

        event.GetDamageeEntity().getWorld().playSound(event.GetDamageeEntity().getLocation(), Sound.WOLF_BARK, 2.0F, 1.5F);
      }
      else
      {
        event.AddKnockback(GetName(), 1.5D);
      }
    }
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.HIGHEST)
  public void RepeatDamage(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
      return;
    }
    Player damager = event.GetDamagerPlayer(false);
    if (damager == null) { return;
    }
    if (!this.Kit.HasKit(damager)) {
      return;
    }
    if (!this._repeat.containsKey(damager))
    {
      this._repeat.put(damager, new ArrayList());
      ((ArrayList)this._repeat.get(damager)).add(Long.valueOf(System.currentTimeMillis()));
      

      damager.setExp(Math.min(0.9999F, ((ArrayList)this._repeat.get(damager)).size() / 9.0F));
      
      return;
    }
    
    int count = ((ArrayList)this._repeat.get(damager)).size();
    
    if (count > 0)
    {
      event.AddMod(damager.getName(), "Ravage", count, false);
      

      damager.getWorld().playSound(damager.getLocation(), Sound.WOLF_BARK, (float)(0.5D + count * 0.25D), (float)(1.0D + count * 0.25D));
    }
    
    ((ArrayList)this._repeat.get(damager)).add(Long.valueOf(System.currentTimeMillis()));
    

    damager.setExp(Math.min(0.9999F, ((ArrayList)this._repeat.get(damager)).size() / 9.0F));
  }
  
  @EventHandler
  public void RepeatExpire(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<Player> playerIterator = this._repeat.keySet().iterator();
    
    while (playerIterator.hasNext())
    {
      Player player = (Player)playerIterator.next();
      
      Iterator<Long> timeIterator = ((ArrayList)this._repeat.get(player)).iterator();
      
      while (timeIterator.hasNext())
      {
        long time = ((Long)timeIterator.next()).longValue();
        
        if (UtilTime.elapsed(time, 3000L)) {
          timeIterator.remove();
        }
      }
      
      player.setExp(Math.min(0.9999F, ((ArrayList)this._repeat.get(player)).size() / 9.0F));
    }
  }
}
