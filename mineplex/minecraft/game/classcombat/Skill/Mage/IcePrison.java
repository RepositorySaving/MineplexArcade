package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashMap;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle.ParticleType;
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
import org.bukkit.Effect;
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
import org.bukkit.event.block.BlockFadeEvent;







public class IcePrison
  extends SkillActive
  implements IThrown
{
  public IcePrison(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
    
    SetDesc(
      new String[] {
      "Launch an icy orb. When it collides,", 
      "it creates a hollow sphere of ice", 
      "thats lasts for #2#1 seconds." });
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
    this.Factory.Projectile().AddThrow(item, player, this, System.currentTimeMillis() + 2000L, true, false, false, 
      Sound.FIZZ, 0.6F, 1.6F, UtilParticle.ParticleType.SNOW_SHOVEL, UpdateType.TICK, 2.0D);
    

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
    Block block = data.GetThrown().getLocation().getBlock();
    

    HashMap<Block, Double> blocks = UtilBlock.getInRadius(block, 3.8D);
    for (Block cur : blocks.keySet())
    {
      if (UtilBlock.airFoliage(cur))
      {

        if (UtilMath.offset(block.getLocation(), cur.getLocation()) > 2.9D)
        {


          if ((cur.getX() != block.getX()) || (cur.getZ() != block.getZ()) || (cur.getY() <= block.getY()))
          {

            FreezeBlock(cur, block, level);
          }
        }
      }
    }
  }
  




  public void FreezeBlock(Block freeze, Block mid, int level)
  {
    if (!UtilBlock.airFoliage(freeze)) {
      return;
    }
    long time = 4000 + 1000 * level;
    
    int yDiff = freeze.getY() - mid.getY();
    
    time = (time - (yDiff * 1000 - Math.random() * 1000.0D));
    
    this.Factory.BlockRestore().Add(freeze, 79, (byte)0, time);
    freeze.getWorld().playEffect(freeze.getLocation(), Effect.STEP_SOUND, 79);
  }
  
  @EventHandler
  public void BlockFade(BlockFadeEvent event)
  {
    if (event.getBlock().getType() == Material.ICE) {
      event.setCancelled(true);
    }
  }
  
  public void Reset(Player player) {}
}
