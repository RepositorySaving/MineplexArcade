package mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid;

import java.util.HashSet;
import java.util.Map;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.energy.Energy;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillActive;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Effect;
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





public class Construction
  extends SkillActive
{
  public Construction(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
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
    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
      block = block.getRelative(event.getBlockFace());
    }
    if ((block.getTypeId() != 8) && (block.getTypeId() != 9) && (event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if ((block.getTypeId() != 79) && (event.getAction() == Action.LEFT_CLICK_BLOCK)) {
      return;
    }
    
    if (!this.Factory.Energy().Use(player, GetName(), 12 - level * 2, true, true)) {
      return;
    }
    
    if (block.getTypeId() == 79)
    {
      block.setTypeId(8);
      

      player.getWorld().playSound(player.getLocation(), Sound.SPLASH, 0.5F, 0.5F);
    }
    else
    {
      block.setTypeId(79);
      

      player.getWorld().playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5F, 3.0F);
    }
    

    block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 80);
  }
  

  public boolean CustomCheck(Player player, int level)
  {
    return true;
  }
  
  public void Skill(Player player, int level) {}
  
  public void Reset(Player player) {}
}
