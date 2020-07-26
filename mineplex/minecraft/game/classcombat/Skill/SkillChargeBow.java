package mineplex.minecraft.game.classcombat.Skill;

import java.util.WeakHashMap;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;

public abstract class SkillChargeBow
  extends SkillCharge
  implements Listener
{
  protected boolean _canChargeInWater;
  protected boolean _canChargeInAir;
  
  public SkillChargeBow(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int maxLevel, float base, float boost, boolean inWater, boolean inAir)
  {
    super(skills, name, classType, skillType, cost, maxLevel, base, boost);
    
    this._canChargeInWater = inWater;
    this._canChargeInAir = inAir;
  }
  
  @EventHandler
  public void ChargeBowInit(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!UtilGear.isMat(event.getItem(), Material.BOW)) {
      return;
    }
    if (!UtilEvent.isAction(event, UtilEvent.ActionType.R)) {
      return;
    }
    if (!player.getInventory().contains(Material.ARROW)) {
      return;
    }
    
    if ((!this._canChargeInAir) && (!UtilEnt.isGrounded(player))) {
      return;
    }
    if ((!this._canChargeInWater) && (player.getLocation().getBlock().isLiquid())) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    
    int level = getLevel(player);
    if (level == 0) { return;
    }
    
    this._charge.put(player, Float.valueOf(0.0F));
  }
  
  @EventHandler
  public void ChargeBow(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {

      if (this._charge.containsKey(cur))
      {


        int level = getLevel(cur);
        if (level == 0)
        {
          this._charge.remove(cur);



        }
        else if (!UtilGear.isMat(cur.getItemInHand(), Material.BOW))
        {
          this._charge.remove(cur);



        }
        else if (!UtilPlayer.isChargingBow(cur))
        {
          this._charge.remove(cur);



        }
        else if ((this._canChargeInAir) || (UtilEnt.isGrounded(cur)))
        {

          if ((this._canChargeInWater) || (!cur.getLocation().getBlock().isLiquid()))
          {

            float charge = ((Float)this._charge.get(cur)).floatValue();
            

            charge = Math.min(1.0F, charge + this._rateBase + this._rateBoost * level);
            this._charge.put(cur, Float.valueOf(charge));
            

            DisplayProgress(cur, GetName(), charge);
          } }
      } }
  }
  
  @EventHandler
  public void TriggerBow(EntityShootBowEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    if (!(event.getProjectile() instanceof Arrow)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    if (!this._charge.containsKey(player)) {
      return;
    }
    DoSkill(player, ((Float)this._charge.remove(player)).floatValue(), (Arrow)event.getProjectile());
  }
  
  public void DoSkill(Player player, float charge, Arrow arrow)
  {
    player.setExp(0.0F);
    
    DoSkillCustom(player, charge, arrow);
  }
  

  public abstract void DoSkillCustom(Player paramPlayer, float paramFloat, Arrow paramArrow);
  
  public void Reset(Player player)
  {
    this._charge.remove(player);
  }
}
