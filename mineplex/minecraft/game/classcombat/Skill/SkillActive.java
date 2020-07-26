package mineplex.minecraft.game.classcombat.Skill;

import java.util.HashSet;
import java.util.Map;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.energy.Energy;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public abstract class SkillActive extends Skill implements Listener
{
  protected int _energy;
  protected int _energyMod;
  protected long _recharge;
  protected long _rechargeMod;
  protected boolean _rechargeInform;
  protected HashSet<Material> _itemSet = new HashSet();
  protected HashSet<Action> _actionSet = new HashSet();
  





  public SkillActive(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels, int energy, int energyMod, long recharge, long rechargeMod, boolean rechargeInform, Material[] itemArray, Action[] actionArray)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    this._energy = energy;
    this._energyMod = energyMod;
    
    this._recharge = recharge;
    this._rechargeMod = rechargeMod;
    this._rechargeInform = rechargeInform;
    
    for (Material cur : itemArray) {
      this._itemSet.add(cur);
    }
    for (Action cur : actionArray) {
      this._actionSet.add(cur);
    }
  }
  
  public HashSet<Material> GetItems() {
    return this._itemSet;
  }
  
  public HashSet<Action> GetActions()
  {
    return this._actionSet;
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void Interact(PlayerInteractEvent event)
  {
    SkillCheck(event.getPlayer(), event.getAction(), event.getClickedBlock());
  }
  
  public boolean SkillCheck(Player player, Action action, Block block)
  {
    int level = getLevel(player);
    
    if (level == 0) {
      return false;
    }
    
    if (UtilBlock.usable(block)) {
      return false;
    }
    
    if (!this._actionSet.contains(action)) {
      return false;
    }
    
    if (!this._itemSet.contains(player.getItemInHand().getType())) {
      return false;
    }
    
    if (player.getItemInHand().getEnchantments().containsKey(org.bukkit.enchantments.Enchantment.ARROW_DAMAGE)) {
      return false;
    }
    
    SkillTriggerEvent event = new SkillTriggerEvent(player, GetName(), GetClassType());
    Bukkit.getServer().getPluginManager().callEvent(event);
    
    if (event.IsCancelled()) {
      return false;
    }
    
    if (!CustomCheck(player, level)) {
      return false;
    }
    
    if (!EnergyRechargeCheck(player, level)) {
      return false;
    }
    
    Skill(player, level);
    return true;
  }
  

  public abstract boolean CustomCheck(Player paramPlayer, int paramInt);
  
  public abstract void Skill(Player paramPlayer, int paramInt);
  
  public boolean EnergyRechargeCheck(Player player, int level)
  {
    if (!this.Factory.Energy().Use(player, GetName(level), Energy(level), false, true)) {
      return false;
    }
    
    if (!Recharge.Instance.use(player, GetName(), GetName(level), Recharge(level), this._rechargeInform, true)) {
      return false;
    }
    
    this.Factory.Energy().Use(player, GetName(level), Energy(level), true, true);
    
    return true;
  }
  
  public int Energy(int level)
  {
    return this._energy + this._energyMod * level;
  }
  
  public long Recharge(int level)
  {
    return this._recharge + this._rechargeMod * level;
  }
  

  public String GetEnergyString()
  {
    if (this._energy == 0) {
      return null;
    }
    if (this._energyMod != 0) {
      return "Energy: #" + this._energy + "#" + this._energyMod;
    }
    
    return "Energy: " + this._energy;
  }
  

  public String GetRechargeString()
  {
    if (this._recharge == 0L) {
      return null;
    }
    if (this._rechargeMod != 0L) {
      return "Recharge: #" + UtilMath.trim(1, this._recharge / 1000.0D) + "#" + UtilMath.trim(1, this._rechargeMod / 1000.0D) + " Seconds";
    }
    
    return "Recharge: " + UtilMath.trim(1, this._recharge / 1000.0D) + " Seconds";
  }
}
