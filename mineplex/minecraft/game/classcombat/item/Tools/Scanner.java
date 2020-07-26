package mineplex.minecraft.game.classcombat.item.Tools;

import java.util.Iterator;
import java.util.LinkedList;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.projectile.ProjectileUser;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import mineplex.minecraft.game.classcombat.item.ItemUsable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;



public class Scanner
  extends ItemUsable
{
  public Scanner(ItemFactory factory, Material type, int amount, boolean canDamage, int gemCost, int tokenCost, UtilEvent.ActionType useAction, boolean useStock, long useDelay, int useEnergy, UtilEvent.ActionType throwAction, boolean throwStock, long throwDelay, int throwEnergy, float throwPower, long throwExpire, boolean throwPlayer, boolean throwBlock, boolean throwIdle, boolean throwPickup)
  {
    super(factory, "Scanner VR-9000", new String[] { "Displays target players skills." }, type, amount, canDamage, gemCost, tokenCost, useAction, useStock, useDelay, useEnergy, throwAction, throwStock, throwDelay, throwEnergy, throwPower, throwExpire, throwPlayer, throwBlock, throwIdle, throwPickup);
  }
  

  public void UseAction(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    double max = 100.0D;
    double cur = 4.0D;
    
    while (cur < max)
    {
      Iterator localIterator = UtilPlayer.getNearby(player.getLocation().add(player.getLocation().getDirection().multiply(cur)), 2.0D).iterator(); if (localIterator.hasNext()) { Player target = (Player)localIterator.next();
        
        ((ClientClass)this.Factory.ClassManager().Get(target)).ListSkills(player);
        return;
      }
      
      cur += 2.0D;
    }
    
    UtilPlayer.message(player, F.main("Scanner", "There are no targets in range."));
  }
  
  public void Collide(LivingEntity target, Block block, ProjectileUser data) {}
  
  public void Idle(ProjectileUser data) {}
  
  public void Expire(ProjectileUser data) {}
}
