package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.projectile.IThrown;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.projectile.ProjectileUser;
import mineplex.core.updater.UpdateType;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;



public class FreezingBlast
  extends SkillActive
  implements IThrown
{
  public FreezingBlast(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Launch a freezing blast;", 
      "Creates long lasting Snow, and", 
      "gives Slow 4 to nearby players.", 
      "", 
      "Direct hit applies Frost Armor,", 
      "giving Protection 4 and Slow 4", 
      "for 10 seconds." });
  }
  


  public boolean CustomCheck(Player player, int level)
  {
    if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
    {
      UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(GetName()) + " in water."));
      return false;
    }
    
    return true;
  }
  


  public void Skill(Player player, int level)
  {
    Item item = player.getWorld().dropItem(player.getEyeLocation().add(player.getLocation().getDirection()), ItemStackFactory.Instance.CreateStack(79));
    item.setVelocity(player.getLocation().getDirection());
    this.Factory.Projectile().AddThrow(item, player, this, -1L, true, true, true, 
      Sound.FIZZ, 0.6F, 1.6F, null, 0, UpdateType.FASTEST, 2.0D);
    

    UtilPlayer.message(player, F.main(GetClassType().name(), "You used " + F.skill(GetName(level)) + "."));
    

    item.getWorld().playSound(item.getLocation(), Sound.SILVERFISH_HIT, 2.0F, 1.0F);
  }
  

  public void Collide(LivingEntity target, Block block, ProjectileUser data)
  {
    Action(target, data);
  }
  

  public void Idle(ProjectileUser data)
  {
    Action(null, data);
  }
  

  public void Expire(ProjectileUser data)
  {
    Action(null, data);
  }
  

  public void Action(LivingEntity target, ProjectileUser data)
  {
    data.GetThrown().getWorld().playEffect(data.GetThrown().getLocation(), Effect.STEP_SOUND, 79);
    

    data.GetThrown().remove();
    

    if (!(data.GetThrower() instanceof Player)) {
      return;
    }
    Player player = (Player)data.GetThrower();
    

    int level = getLevel(player);
    if (level == 0) { return;
    }
    
    HashMap<Block, Double> blocks = UtilBlock.getInRadius(data.GetThrown().getLocation(), 4.0D);
    for (Block cur : blocks.keySet())
    {
      if ((UtilBlock.airFoliage(cur)) && (UtilBlock.solid(cur.getRelative(BlockFace.DOWN))))
      {
        this.Factory.BlockRestore().Snow(cur, (byte)(int)(7.0D * ((Double)blocks.get(cur)).doubleValue()), (byte)7, (15000.0D * (1.0D + ((Double)blocks.get(cur)).doubleValue())), 1000L, 0);
        cur.getWorld().playEffect(cur.getLocation(), Effect.STEP_SOUND, 80);
      }
    }
    

    for (Player curPlayer : UtilPlayer.getNearby(data.GetThrown().getLocation(), 4.0D)) {
      this.Factory.Condition().Factory().Slow(GetName(), curPlayer, player, 2.9D, 0, false, true, true, true);
    }
    if (target == null) {
      return;
    }
    
    this.Factory.Condition().Factory().Protection(GetName(), target, player, 10.0D, 3, false, true, true);
    this.Factory.Condition().Factory().Slow(GetName(), target, player, 10.0D, 3, false, true, true, true);
    

    UtilPlayer.message(target, F.main(GetClassType().name(), F.name(player.getName()) + " used " + F.skill("Frost Armor") + " on you."));
  }
  
  public void Reset(Player player) {}
}
