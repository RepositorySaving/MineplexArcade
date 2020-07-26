package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.event.PerkLeapEvent;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

public class PerkLeap extends Perk
{
  private String _name;
  private double _power;
  private double _heightMax;
  private long _recharge;
  private int _maxUses;
  private NautHashMap<String, Integer> _uses = new NautHashMap();
  


  public PerkLeap(String name, double power, double heightLimit, long recharge)
  {
    super("Leaper", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + name });
    

    this._name = name;
    this._power = power;
    this._heightMax = heightLimit;
    this._recharge = recharge;
    this._maxUses = 0;
  }
  


  public PerkLeap(String name, double power, double heightLimit, long recharge, int uses)
  {
    super("Leaper", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + name + C.cGray + "  (" + C.cWhite + uses + " Charges" + C.cGray + ")" });
    

    this._name = name;
    this._power = power;
    this._heightMax = heightLimit;
    this._recharge = recharge;
    this._maxUses = uses;
  }
  
  @EventHandler
  public void Leap(PlayerInteractEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if (!event.getPlayer().getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    
    if (this._maxUses > 0)
    {
      if (!this._uses.containsKey(player.getName())) {
        this._uses.put(player.getName(), Integer.valueOf(this._maxUses));
      }
      if (((Integer)this._uses.get(player.getName())).intValue() <= 0)
      {
        UtilPlayer.message(player, F.main("Skill", "You cannot use " + F.skill(this._name) + " anymore."));
        return;
      }
    }
    

    if (!Recharge.Instance.use(player, this._name, this._recharge, true, true)) {
      return;
    }
    
    if (this._maxUses > 0)
    {
      int count = ((Integer)this._uses.get(player.getName())).intValue();
      count--;
      
      player.setExp(Math.min(0.99F, count / this._maxUses));
      
      this._uses.put(player.getName(), Integer.valueOf(count));
    }
    
    org.bukkit.entity.Entity ent = player;
    
    if ((player.getVehicle() != null) && 
      ((player.getVehicle() instanceof Horse))) {
      ent = player.getVehicle();
    }
    UtilAction.velocity(ent, this._power, 0.2D, this._heightMax, true);
    
    player.setFallDistance(0.0F);
    
    UtilPlayer.message(player, F.main("Skill", "You used " + F.skill(this._name) + "."));
    
    player.getWorld().playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);
    
    PerkLeapEvent leapEvent = new PerkLeapEvent(player);
    UtilServer.getServer().getPluginManager().callEvent(leapEvent);
  }
}
