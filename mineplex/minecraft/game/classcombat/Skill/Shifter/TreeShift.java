package mineplex.minecraft.game.classcombat.Skill.Shifter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scheduler.BukkitScheduler;

public class TreeShift extends SkillActive
{
  private HashMap<Location, Long> trees = new HashMap();
  










  public TreeShift(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Creates an illusionary tree at target location.", 
      "Other players cannot attack or see through it.", 
      "You view it as a sapling, and can attack over it.", 
      "Lasts 2 + 2pL seconds." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    Block block = player.getTargetBlock(null, 0);
    if (UtilMath.offset(block.getLocation().add(0.5D, 0.5D, 0.5D), player.getLocation()) > 8 + level)
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " so far away."));
      return false;
    }
    
    return true;
  }
  

  public void Skill(Player player, int level)
  {
    HashMap<Location, Material> tree = new HashMap();
    
    Block block = player.getTargetBlock(null, 0);
    

    final Player fPlayer = player;
    final Location fLoc = block.getLocation().add(0.0D, 1.0D, 0.0D);
    UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Factory.GetPlugin(), new Runnable()
    {
      public void run()
      {
        fPlayer.sendBlockChange(fLoc, 6, (byte)0);
      }
    }, 0L);
    



    for (int i = 0; i < 6; i++)
    {
      block = block.getRelative(BlockFace.UP);
      
      if (block.getTypeId() != 0)
      {
        block = block.getRelative(BlockFace.DOWN);
        break;
      }
      
      tree.put(block.getLocation(), Material.LOG);
    }
    
    if (tree.size() > 5)
    {
      for (Block leaf : mineplex.core.common.util.UtilBlock.getInRadius(block.getLocation(), 2.5D).keySet())
      {
        if ((!tree.containsKey(leaf.getLocation())) && (leaf.getTypeId() == 0))
        {
          tree.put(leaf.getLocation(), Material.LEAVES);
        }
      }
    }
    
    Iterator localIterator2;
    
    for (??? = tree.keySet().iterator(); ???.hasNext(); 
        


        localIterator2.hasNext())
    {
      Location loc = (Location)???.next();
      
      this.trees.put(loc, Long.valueOf(System.currentTimeMillis() + (2000 + 2000 * level)));
      
      localIterator2 = player.getWorld().getPlayers().iterator(); continue;Player other = (Player)localIterator2.next();
      
      if (!other.equals(player))
      {

        other.sendBlockChange(loc, (Material)tree.get(loc), (byte)0);
        
        if (tree.get(loc) == Material.LOG) {
          other.playEffect(loc, org.bukkit.Effect.STEP_SOUND, 17);
        }
      }
    }
    
    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
  }
  
  @org.bukkit.event.EventHandler
  public void Detree(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    if (this.trees.isEmpty()) {
      return;
    }
    HashSet<Location> remove = new HashSet();
    
    for (Location loc : this.trees.keySet())
    {
      if (System.currentTimeMillis() > ((Long)this.trees.get(loc)).longValue()) {
        remove.add(loc);
      }
    }
    for (Location loc : remove)
    {
      for (Player player : loc.getWorld().getPlayers()) {
        player.sendBlockChange(loc, 0, (byte)0);
      }
      this.trees.remove(loc);
    }
  }
  
  public void Reset(Player player) {}
}
