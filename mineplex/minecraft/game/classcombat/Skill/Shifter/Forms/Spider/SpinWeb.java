package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider;

import java.util.HashSet;
import java.util.Map;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.energy.Energy;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;



public class SpinWeb
  extends SkillActive
{
  public SpinWeb(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels, energy, energyMod, recharge, rechargeMod, rechargeInform, itemArray, actionArray);
  }
  

  @EventHandler(priority=EventPriority.LOW)
  public void Interact(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    

    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    
    if (!this._actionSet.contains(event.getAction())) {
      return;
    }
    
    if (!this._itemSet.contains(player.getItemInHand().getType())) {
      return;
    }
    
    int level = getLevel(player);
    if (level <= 0) { return;
    }
    if (!CustomCheck(player, level)) {
      return;
    }
    
    if (player.getItemInHand().getEnchantments().containsKey(Enchantment.ARROW_DAMAGE)) {
      return;
    }
    
    Block block = event.getClickedBlock();
    if (block == null) { return;
    }
    block = block.getRelative(event.getBlockFace());
    

    if (!this.Factory.Energy().Use(player, GetName(), 20 - level * 2, true, true)) {
      return;
    }
    
    this.Factory.BlockRestore().Add(block, 30, (byte)0, 5000 + 1000 * level);
    

    block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 30);
    

    player.getWorld().playSound(player.getLocation(), Sound.SPIDER_IDLE, 1.0F, 0.3F);
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
  
  public void Skill(Player player, int level) {}
  
  public void Reset(Player player) {}
}
