package nautilus.game.arcade.kit.perks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.EntityWolf;
import net.minecraft.server.v1_7_R3.Navigation;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftWolf;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PerkWolfPet extends Perk
{
  private HashMap<Player, ArrayList<Wolf>> _wolfMap = new HashMap();
  
  private HashMap<Wolf, Long> _tackle = new HashMap();
  
  private int _spawnRate;
  
  private int _max;
  
  private boolean _baby;
  
  private boolean _name;
  
  public PerkWolfPet(int spawnRate, int max, boolean baby, boolean name)
  {
    super("Wolf Master", new String[] {C.cGray + "Spawn 1 Wolf every " + spawnRate + " seconds. Maximum of " + max + ".", C.cYellow + "Right-Click" + C.cGray + " with Sword/Axe to use " + C.cGreen + "Wolf Tackle" });
    

    this._spawnRate = spawnRate;
    this._max = max;
    this._baby = baby;
    this._name = name;
  }
  

  public void Apply(Player player)
  {
    Recharge.Instance.use(player, GetName(), this._spawnRate * 1000, false, false);
  }
  
  @EventHandler
  public void CubSpawn(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
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

            if (!this._wolfMap.containsKey(cur)) {
              this._wolfMap.put(cur, new ArrayList());
            }
            if (((ArrayList)this._wolfMap.get(cur)).size() < this._max)
            {

              this.Manager.GetGame().CreatureAllowOverride = true;
              Wolf wolf = (Wolf)cur.getWorld().spawn(cur.getLocation(), Wolf.class);
              this.Manager.GetGame().CreatureAllowOverride = false;
              
              wolf.setOwner(cur);
              wolf.setCollarColor(org.bukkit.DyeColor.GREEN);
              wolf.playEffect(EntityEffect.WOLF_HEARTS);
              
              wolf.setMaxHealth(18.0D);
              wolf.setHealth(wolf.getMaxHealth());
              
              if (this._baby) {
                wolf.setBaby();
              }
              if (this._name)
              {
                wolf.setCustomName(cur.getName() + "'s Wolf");
                wolf.setCustomNameVisible(true);
              }
              

              ((ArrayList)this._wolfMap.get(cur)).add(wolf);
              
              cur.playSound(cur.getLocation(), Sound.WOLF_HOWL, 1.0F, 1.0F);
            }
          } } } }
  }
  
  @EventHandler
  public void CubTargetCancel(EntityTargetEvent event) {
    if (!this._wolfMap.containsKey(event.getTarget())) {
      return;
    }
    if (((ArrayList)this._wolfMap.get(event.getTarget())).contains(event.getEntity())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void CubUpdate(UpdateEvent event) {
    if (event.getType() != UpdateType.FAST)
      return;
    Iterator<Wolf> wolfIterator;
    for (Iterator localIterator = this._wolfMap.keySet().iterator(); localIterator.hasNext(); 
        


        wolfIterator.hasNext())
    {
      Player player = (Player)localIterator.next();
      
      wolfIterator = ((ArrayList)this._wolfMap.get(player)).iterator();
      
      continue;
      
      Wolf wolf = (Wolf)wolfIterator.next();
      

      if (!wolf.isValid())
      {
        wolf.getWorld().playSound(wolf.getLocation(), Sound.WOLF_DEATH, 1.0F, 1.0F);
        Recharge.Instance.useForce(player, GetName(), this._spawnRate * 1000);
        wolfIterator.remove();
      }
      else
      {
        if (player.isSneaking())
        {
          ((CraftWolf)wolf).getHandle().setGoalTarget(null);
        }
        

        double range = 0.5D;
        if (wolf.getTarget() != null) {
          range = 12.0D;
        }
        Location target = player.getLocation().add(player.getLocation().getDirection().multiply(3));
        target.setY(player.getLocation().getY());
        
        if (UtilMath.offset(wolf.getLocation(), target) > range)
        {
          float speed = 1.0F;
          if (player.isSprinting()) {
            speed = 1.4F;
          }
          
          EntityCreature ec = ((CraftCreature)wolf).getHandle();
          Navigation nav = ec.getNavigation();
          nav.a(target.getX(), target.getY(), target.getZ(), speed);
          
          wolf.setTarget(null);
        }
      }
    }
  }
  
  @EventHandler
  public void CubStrikeTrigger(PlayerInteractEvent event)
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
    if ((!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) && (!event.getPlayer().getItemInHand().getType().toString().contains("_SWORD"))) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    if ((!this._wolfMap.containsKey(player)) || (((ArrayList)this._wolfMap.get(player)).isEmpty()))
    {
      UtilPlayer.message(player, F.main("Game", "You have no Wolf Cubs."));
      return;
    }
    
    if (!Recharge.Instance.use(player, "Cub Strike", 4000L, true, true)) {
      return;
    }
    Wolf wolf = (Wolf)((ArrayList)this._wolfMap.get(player)).get(UtilMath.r(((ArrayList)this._wolfMap.get(player)).size()));
    
    mineplex.core.common.util.UtilAction.velocity(wolf, player.getLocation().getDirection(), 1.4D, false, 0.0D, 0.2D, 1.2D, true);
    
    wolf.playEffect(EntityEffect.WOLF_SMOKE);
    
    player.getWorld().playSound(wolf.getLocation(), Sound.WOLF_BARK, 1.0F, 1.2F);
    

    this._tackle.put(wolf, Long.valueOf(System.currentTimeMillis()));
    

    UtilPlayer.message(player, F.main("Game", "You used " + F.skill("Cub Strike") + "."));
  }
  
  @EventHandler
  public void CubStrikeEnd(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    
    Iterator<Wolf> wolfIterator = this._tackle.keySet().iterator();
    
    while (wolfIterator.hasNext())
    {
      Wolf wolf = (Wolf)wolfIterator.next();
      
      for (Player other : this.Manager.GetGame().GetPlayers(true)) {
        if ((other.getGameMode() == org.bukkit.GameMode.SURVIVAL) && 
          (UtilEnt.hitBox(wolf.getLocation(), other, 2.0D, null)))
        {
          if (!other.equals(wolf.getOwner()))
          {

            CubStrikeHit((Player)wolf.getOwner(), other, wolf);
            wolfIterator.remove();
            return;
          } }
      }
      if (UtilEnt.isGrounded(wolf))
      {

        if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._tackle.get(wolf)).longValue(), 1000L))
        {

          wolfIterator.remove();
        }
      }
    }
  }
  
  public void CubStrikeHit(Player damager, LivingEntity damagee, Wolf wolf) {
    ((CraftWolf)wolf).getHandle().setGoalTarget(((CraftLivingEntity)damagee).getHandle());
    

    damagee.getWorld().playSound(damagee.getLocation(), Sound.WOLF_BARK, 1.5F, 1.5F);
    

    this.Manager.GetCondition().Factory().Slow(GetName(), damagee, damager, 4.0D, 1, false, false, true, false);
    

    UtilPlayer.message(damager, F.main("Game", "You hit " + F.name(UtilEnt.getName(damagee)) + " with " + F.skill("Wolf Tackle") + "."));
    UtilPlayer.message(damagee, F.main("Game", F.name(damager.getName()) + " hit you with " + F.skill("Wolf Tackle") + "."));
  }
  
  @EventHandler
  public void CubHeal(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC)
      return;
    Iterator localIterator2;
    for (Iterator localIterator1 = this._wolfMap.values().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      ArrayList<Wolf> wolves = (ArrayList)localIterator1.next();
      
      localIterator2 = wolves.iterator(); continue;Wolf wolf = (Wolf)localIterator2.next();
      
      if (wolf.getHealth() > 0.0D) {
        wolf.setHealth(Math.min(wolf.getMaxHealth(), wolf.getHealth() + 1.0D));
      }
    }
  }
  
  @EventHandler
  public void PlayerDeath(PlayerDeathEvent event)
  {
    ArrayList<Wolf> wolves = (ArrayList)this._wolfMap.remove(event.getEntity());
    
    if (wolves == null) {
      return;
    }
    for (Wolf wolf : wolves) {
      wolf.remove();
    }
    wolves.clear();
  }
  
  public boolean IsMinion(Entity ent) {
    Iterator localIterator2;
    for (Iterator localIterator1 = this._wolfMap.values().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      ArrayList<Wolf> minions = (ArrayList)localIterator1.next();
      
      localIterator2 = minions.iterator(); continue;Wolf minion = (Wolf)localIterator2.next();
      
      if (ent.equals(minion))
      {
        return true;
      }
    }
    

    return false;
  }
  
  @EventHandler
  public void Damage(CustomDamageEvent event)
  {
    if (event.GetDamagerEntity(true) == null) {
      return;
    }
    if (!IsMinion(event.GetDamagerEntity(true))) {
      return;
    }
    double damage = 4.0D;
    
    event.AddMod("Wolf Minion", "Negate", -event.GetDamageInitial(), false);
    event.AddMod("Wolf Minion", "Damage", damage, false);
  }
}
