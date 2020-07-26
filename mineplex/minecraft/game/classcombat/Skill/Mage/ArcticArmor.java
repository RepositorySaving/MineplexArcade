package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.energy.Energy;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.PluginManager;

public class ArcticArmor extends Skill
{
  private HashSet<Player> _active = new HashSet();
  
  public ArcticArmor(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Drop Axe/Sword to Toggle.", 
      "", 
      "Create a freezing area around you", 
      "in a #3#1 Block radius. Allies inside", 
      "this area receive Protection 1.", 
      "", 
      "You are permanently immune to the", 
      "Slowing effect of snow." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: #7#-1 per Second";
  }
  
  @EventHandler
  public void Toggle(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    
    if (getLevel(player) == 0) {
      return;
    }
    if (!mineplex.core.common.util.UtilGear.isWeapon(event.getItemDrop().getItemStack())) {
      return;
    }
    event.setCancelled(true);
    

    SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
    UtilServer.getServer().getPluginManager().callEvent(trigger);
    if (trigger.IsCancelled()) {
      return;
    }
    if (this._active.contains(player))
    {
      Remove(player);
    }
    else
    {
      if (!this.Factory.Energy().Use(player, "Enable " + GetName(), 10.0D, true, true)) {
        return;
      }
      Add(player);
    }
  }
  
  public void Add(Player player)
  {
    this._active.add(player);
    UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.oo("Enabled", true)));
  }
  
  public void Remove(Player player)
  {
    this._active.remove(player);
    UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.oo("Disabled", false)));
    this.Factory.Condition().EndCondition(player, null, GetName());
  }
  
  @EventHandler
  public void Audio(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Player cur : this._active) {
      cur.getWorld().playSound(cur.getLocation(), org.bukkit.Sound.AMBIENCE_RAIN, 0.3F, 0.0F);
    }
  }
  
  @EventHandler
  public void SnowAura(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {


        int level = getLevel(cur);
        if (level == 0)
        {
          Remove(cur);

        }
        else
        {

          SkillTriggerEvent trigger = new SkillTriggerEvent(cur, GetName(), GetClassType());
          UtilServer.getServer().getPluginManager().callEvent(trigger);
          if (trigger.IsCancelled())
          {
            Remove(cur);



          }
          else if (!this.Factory.Energy().Use(cur, GetName(), 0.35D - level * 0.05D, true, true))
          {
            Remove(cur);

          }
          else
          {
            double duration = 2000.0D;
            HashMap<Block, Double> blocks = mineplex.core.common.util.UtilBlock.getInRadius(cur.getLocation().getBlock().getLocation(), 3.0D + level);
            for (Block block : blocks.keySet())
            {

              if ((!block.getRelative(org.bukkit.block.BlockFace.UP).isLiquid()) && 
                (block.getLocation().getY() <= cur.getLocation().getY()) && (
                (block.getTypeId() == 8) || (block.getTypeId() == 9) || (block.getTypeId() == 79))) {
                this.Factory.BlockRestore().Add(block, 79, (byte)0, (duration * (1.0D + ((Double)blocks.get(block)).doubleValue())));
              }
              
              this.Factory.BlockRestore().Snow(block, (byte)0, (byte)0, (duration * (1.0D + ((Double)blocks.get(block)).doubleValue())), 250L, 0);
            }
          }
        }
      } }
  }
  
  @EventHandler
  public void ProtectionAura(UpdateEvent event) { if (event.getType() != UpdateType.FAST)
      return;
    Iterator localIterator2;
    for (Iterator localIterator1 = this._active.iterator(); localIterator1.hasNext(); 
        

        localIterator2.hasNext())
    {
      Player cur = (Player)localIterator1.next();
      

      localIterator2 = UtilPlayer.getNearby(cur.getLocation(), 3 + getLevel(cur)).iterator(); continue;Player other = (Player)localIterator2.next();
      if ((!this.Factory.Relation().CanHurt(cur, other)) || (other.equals(cur))) {
        this.Factory.Condition().Factory().Protection(GetName(), other, cur, 1.9D, 1, false, true, true);
      }
    }
  }
  
  @EventHandler
  public void Slow(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (cur.getLocation().getChunk() != null)
      {

        Block block = cur.getLocation().getBlock();
        
        if (block.getTypeId() == 78)
        {

          if (block.getData() != 0)
          {

            if (getLevel(cur) <= 0)
            {

              int level = 0;
              if ((block.getData() == 2) || (block.getData() == 3)) {
                level = 1;
              } else if ((block.getData() == 4) || (block.getData() == 5)) {
                level = 2;
              } else if ((block.getData() == 6) || (block.getData() == 7)) {
                level = 3;
              }
              
              this.Factory.Condition().Factory().Custom("Thick Snow", cur, cur, 
                mineplex.minecraft.game.core.condition.Condition.ConditionType.SLOW, 1.9D, level, false, 
                org.bukkit.Material.SNOW_BALL, (byte)0, true);
            } } }
      } }
  }
  
  @EventHandler
  public void Particle(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Entity ent : this._active)
    {
      mineplex.core.common.util.UtilParticle.PlayParticle(mineplex.core.common.util.UtilParticle.ParticleType.SNOW_SHOVEL, ent.getLocation(), 
        (float)(Math.random() - 0.5D), 0.2F + (float)Math.random(), (float)(Math.random() - 0.5D), 0.0F, 3);
    }
  }
  

  public void Reset(Player player)
  {
    this._active.remove(player);
  }
}
