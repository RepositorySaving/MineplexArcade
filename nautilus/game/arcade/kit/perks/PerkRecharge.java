package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.recharge.RechargeEvent;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.event.EventHandler;


public class PerkRecharge
  extends Perk
{
  private double _reduction;
  
  public PerkRecharge(double reduction)
  {
    super("Recharge", new String[] {C.cGray + "Reduces ability cooldowns by " + (int)(reduction * 100.0D) + "%" });
    

    this._reduction = reduction;
  }
  
  @EventHandler
  public void DigSpeed(RechargeEvent event)
  {
    if (!this.Kit.HasKit(event.GetPlayer())) {
      return;
    }
    event.SetRecharge((event.GetRecharge() * (1.0D - this._reduction)));
  }
}
