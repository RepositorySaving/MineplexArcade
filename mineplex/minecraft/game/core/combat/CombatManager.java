package mineplex.minecraft.game.core.combat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import mineplex.core.MiniPlugin;
import mineplex.core.account.event.ClientUnloadEvent;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.event.ClearCombatEvent;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import net.minecraft.server.v1_7_R3.ItemStack;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CombatManager extends MiniPlugin
{
  private NautHashMap<Player, CombatLog> _active = new NautHashMap();
  private NautHashMap<String, ClientCombat> _combatClients = new NautHashMap();
  
  private HashSet<Player> _removeList = new HashSet();
  
  protected long ExpireTime = 15000L;
  
  public CombatManager(JavaPlugin plugin)
  {
    super("Combat", plugin);
  }
  
  @EventHandler
  public void UnloadDonor(ClientUnloadEvent event)
  {
    this._combatClients.remove(event.GetName());
  }
  
  public ClientCombat Get(String name)
  {
    if (!this._combatClients.containsKey(name)) {
      this._combatClients.put(name, new ClientCombat());
    }
    return (ClientCombat)this._combatClients.get(name);
  }
  

  @EventHandler(priority=EventPriority.MONITOR)
  public void AddAttack(EntityDamageEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getEntity() == null) || (!(event.getEntity() instanceof Player))) {
      return;
    }
    Player damagee = (Player)event.getEntity();
    
    LivingEntity damagerEnt = UtilEvent.GetDamagerEntity(event, true);
    

    if (damagerEnt != null)
    {
      if ((damagerEnt instanceof Player)) {
        Get((Player)damagerEnt).SetLastCombat(System.currentTimeMillis());
      }
      Get(damagee).Attacked(
        UtilEnt.getName(damagerEnt), 
        event.getDamage(), damagerEnt, 
        event.getCause());

    }
    else
    {
      EntityDamageEvent.DamageCause cause = event.getCause();
      
      String source = "?";
      String reason = "-";
      
      if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
      {
        source = "Explosion";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.CONTACT)
      {
        source = "Cactus";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.CUSTOM)
      {
        source = "Custom";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.DROWNING)
      {
        source = "Water";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
      {
        source = "Entity";
        reason = "Attack";
      } else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
      {
        source = "Explosion";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.FALL)
      {
        source = "Fall";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK)
      {
        source = "Falling Block";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.FIRE)
      {
        source = "Fire";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.FIRE_TICK)
      {
        source = "Fire";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.LAVA)
      {
        source = "Lava";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.LIGHTNING)
      {
        source = "Lightning";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.MAGIC)
      {
        source = "Magic";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.MELTING)
      {
        source = "Melting";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.POISON)
      {
        source = "Poison";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE)
      {
        source = "Projectile";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.STARVATION)
      {
        source = "Starvation";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.SUFFOCATION)
      {
        source = "Suffocation";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.SUICIDE)
      {
        source = "Suicide";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.VOID)
      {
        source = "Void";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.WITHER)
      {
        source = "Wither";
        reason = "-";
      }
      
      Get(damagee).Attacked(source, 
        event.getDamage(), null, reason);
    }
  }
  


  public void AddAttack(CustomDamageEvent event)
  {
    if (event.GetDamageePlayer() == null) {
      return;
    }
    
    if (event.GetDamagerEntity(true) != null)
    {
      String reason = event.GetReason();
      
      if (reason == null)
      {
        if (event.GetDamagerPlayer(false) != null)
        {
          Player damager = event.GetDamagerPlayer(false);
          
          reason = "Fists";
          
          if (damager.getItemInHand() != null)
          {
            ItemStack itemStack = CraftItemStack.asNMSCopy(damager
              .getItemInHand());
            
            if (itemStack != null)
            {
              reason = 
                CraftItemStack.asNMSCopy(damager.getItemInHand()).getName();
            }
          }
        } else if (event.GetProjectile() != null)
        {
          if ((event.GetProjectile() instanceof Arrow)) {
            reason = "Archery";
          } else if ((event.GetProjectile() instanceof org.bukkit.entity.Fireball)) {
            reason = "Fireball";
          }
        }
      }
      if ((event.GetDamagerEntity(true) instanceof Player)) {
        Get((Player)event.GetDamagerEntity(true)).SetLastCombat(System.currentTimeMillis());
      }
      Get(event.GetDamageePlayer()).Attacked(
        UtilEnt.getName(event.GetDamagerEntity(true)), 
        (int)event.GetDamage(), event.GetDamagerEntity(true), 
        reason);

    }
    else
    {
      EntityDamageEvent.DamageCause cause = event.GetCause();
      
      String source = "?";
      String reason = "-";
      
      if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)
      {
        source = "Explosion";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.CONTACT)
      {
        source = "Cactus";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.CUSTOM)
      {
        source = "Custom";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.DROWNING)
      {
        source = "Water";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
      {
        source = "Entity";
        reason = "Attack";
      } else if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
      {
        source = "Explosion";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.FALL)
      {
        source = "Fall";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK)
      {
        source = "Falling Block";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.FIRE)
      {
        source = "Fire";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.FIRE_TICK)
      {
        source = "Fire";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.LAVA)
      {
        source = "Lava";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.LIGHTNING)
      {
        source = "Lightning";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.MAGIC)
      {
        source = "Magic";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.MELTING)
      {
        source = "Melting";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.POISON)
      {
        source = "Poison";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.PROJECTILE)
      {
        source = "Projectile";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.STARVATION)
      {
        source = "Starvation";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.SUFFOCATION)
      {
        source = "Suffocation";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.SUICIDE)
      {
        source = "Suicide";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.VOID)
      {
        source = "Void";
        reason = "-";
      } else if (cause == EntityDamageEvent.DamageCause.WITHER)
      {
        source = "Wither";
        reason = "-";
      }
      
      if (event.GetReason() != null) {
        reason = event.GetReason();
      }
      Get(event.GetDamageePlayer()).Attacked(source, 
        (int)event.GetDamage(), null, reason);
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void HandleDeath(PlayerDeathEvent event)
  {
    event.setDeathMessage(null);
    
    if (!this._active.containsKey(event.getEntity())) {
      return;
    }
    CombatLog log = (CombatLog)this._active.remove(event.getEntity());
    log.SetDeathTime(System.currentTimeMillis());
    

    Get(event.getEntity().getName()).GetDeaths().addFirst(log);
    

    int assists = 0;
    for (int i = 0; i < log.GetAttackers().size(); i++)
    {
      if (((CombatComponent)log.GetAttackers().get(i)).IsPlayer())
      {


        if (!UtilTime.elapsed(((CombatComponent)log.GetAttackers().get(i)).GetLastDamage(), this.ExpireTime))
        {

          if (log.GetKiller() == null)
          {
            log.SetKiller((CombatComponent)log.GetAttackers().get(i));
            
            ClientCombat killerClient = Get(((CombatComponent)log.GetAttackers().get(i)).GetName());
            
            if (killerClient != null) {
              killerClient.GetKills().addFirst(log);
            }
          }
          else
          {
            assists++;
            
            ClientCombat assistClient = Get(((CombatComponent)log.GetAttackers().get(i)).GetName());
            
            if (assistClient != null)
              assistClient.GetAssists().addFirst(log);
          } }
      }
    }
    log.SetAssists(assists);
    

    CombatDeathEvent deathEvent = new CombatDeathEvent(event, Get(event.getEntity().getName()), log);
    UtilServer.getServer().getPluginManager().callEvent(deathEvent);
    

    if ((deathEvent.GetBroadcastType() == DeathMessageType.Detailed) || (deathEvent.GetBroadcastType() == DeathMessageType.Absolute))
    {

      for (Player cur : event.getEntity().getWorld().getPlayers())
      {

        String killedColor = log.GetKilledColor();
        
        String deadPlayer = killedColor + event.getEntity().getName();
        

        if (log.GetKiller() != null)
        {
          String killerColor = log.GetKillerColor();
          
          String killPlayer = killerColor + log.GetKiller().GetName();
          
          if (log.GetAssists() > 0) {
            killPlayer = killPlayer + " + " + log.GetAssists();
          }
          String weapon = log.GetKiller().GetLastDamageSource();
          
          UtilPlayer.message(
            cur, 
            F.main("Death", 
            deadPlayer + C.cGray + " killed by " + 
            killPlayer + C.cGray + " with " + 
            F.item(weapon) + "."));



        }
        else if (log.GetAttackers().isEmpty()) {
          UtilPlayer.message(cur, F.main("Death", deadPlayer + 
            C.cGray + " has died."));

        }
        else if ((log.GetLastDamager() != null) && (log.GetLastDamager().GetReason() != null) && (log.GetLastDamager().GetReason().length() > 1))
        {
          UtilPlayer.message(
            cur, 
            
            F.main("Death", new StringBuilder(String.valueOf(deadPlayer))
            .append(C.cGray)
            .append(" killed by ")
            .append(F.name(log.GetLastDamager()
            .GetReason())).toString()) + 
            C.cGray + ".");
        }
        else
        {
          UtilPlayer.message(
            cur, 
            
            F.main("Death", new StringBuilder(String.valueOf(deadPlayer))
            .append(C.cGray)
            .append(" killed by ")
            .append(F.name(
            ((CombatComponent)log.GetAttackers().getFirst()).GetName())).toString()) + 
            C.cGray + ".");
        }
      }
      




      if (deathEvent.GetBroadcastType() == DeathMessageType.Absolute) {
        UtilPlayer.message(event.getEntity(), log.DisplayAbsolute());
      } else {
        UtilPlayer.message(event.getEntity(), log.Display());
      }
    } else if (deathEvent.GetBroadcastType() == DeathMessageType.Simple)
    {

      if (log.GetKiller() != null)
      {

        String killerColor = log.GetKillerColor();
        String killPlayer = killerColor + log.GetKiller().GetName();
        


        String killedColor = log.GetKilledColor();
        String deadPlayer = killedColor + event.getEntity().getName();
        
        if (log.GetAssists() > 0) {
          killPlayer = killPlayer + " + " + log.GetAssists();
        }
        String weapon = log.GetKiller().GetLastDamageSource();
        
        Player killer = UtilPlayer.searchExact(log.GetKiller().GetName());
        UtilPlayer.message(killer, F.main("Death", "You killed " + F.elem(deadPlayer) + " with " + F.item(weapon) + "."));
        
        UtilPlayer.message(event.getEntity(), F.main("Death", killPlayer + C.cGray + " killed you with " + F.item(weapon) + "."));



      }
      else if (log.GetAttackers().isEmpty())
      {
        UtilPlayer.message(event.getEntity(), F.main("Death", "You have died."));

      }
      else
      {
        UtilPlayer.message(event.getEntity(), F.main("Death", new StringBuilder("You were killed by ")
          .append(F.name(((CombatComponent)log.GetAttackers().getFirst()).GetName())).toString()) + C.cGray + ".");
      }
    }
  }
  

  @EventHandler
  public void ExpireOld(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (CombatLog log : this._active.values()) {
      log.ExpireOld();
    }
  }
  
  public void Add(Player player) {
    this._active.put(player, new CombatLog(player, 15000L));
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void Clear(ClearCombatEvent event)
  {
    this._active.remove(event.GetPlayer());
  }
  
  public CombatLog Get(Player player)
  {
    if (!this._active.containsKey(player))
    {
      Add(player);
    }
    

    return (CombatLog)this._active.get(player);
  }
  
  public long GetExpireTime()
  {
    return this.ExpireTime;
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void ClearInactives(UpdateEvent event)
  {
    if (event.getType() == UpdateType.MIN_02)
    {

      Iterator<Player> removeIterator = this._removeList.iterator();
      
      while (removeIterator.hasNext())
      {
        Player player = (Player)removeIterator.next();
        
        if (!player.isOnline()) {
          this._active.remove(player);
        }
        removeIterator.remove();
      }
      

      for (Player player : this._active.keySet())
      {
        if (!player.isOnline()) {
          this._removeList.add(player);
        }
      }
    }
  }
  
  public void DebugInfo(Player player) {
    StringBuilder nameBuilder = new StringBuilder();
    
    for (Player combats : this._active.keySet())
    {
      if (!combats.isOnline())
      {
        if (nameBuilder.length() != 0) {
          nameBuilder.append(", ");
        }
        nameBuilder.append(combats.getName());
      }
    }
    
    player.sendMessage(F.main(GetName(), nameBuilder.toString()));
  }
}
