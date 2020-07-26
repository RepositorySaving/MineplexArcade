package nautilus.game.arcade.kit.perks;

import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.world.FireworkHandler;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class PerkBlink extends Perk
{
  private String _name = "";
  
  private double _range;
  
  private long _recharge;
  
  public PerkBlink(String name, double range, long recharge)
  {
    super("Leaper", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Axe to " + C.cGreen + name });
    

    this._name = name;
    this._range = range;
    this._recharge = recharge;
  }
  
  @org.bukkit.event.EventHandler
  public void Blink(PlayerInteractEvent event)
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
    if (!Recharge.Instance.use(player, this._name, this._recharge, true, true)) {
      return;
    }
    
    Block lastSmoke = player.getLocation().getBlock();
    
    double curRange = 0.0D;
    while (curRange <= this._range)
    {
      Location newTarget = player.getLocation().add(new Vector(0.0D, 0.2D, 0.0D)).add(player.getLocation().getDirection().multiply(curRange));
      
      if ((!UtilBlock.airFoliage(newTarget.getBlock())) || 
        (!UtilBlock.airFoliage(newTarget.getBlock().getRelative(org.bukkit.block.BlockFace.UP)))) {
        break;
      }
      
      curRange += 0.2D;
      

      if (!lastSmoke.equals(newTarget.getBlock()))
      {
        lastSmoke.getWorld().playEffect(lastSmoke.getLocation(), org.bukkit.Effect.SMOKE, 4);
      }
      
      lastSmoke = newTarget.getBlock();
    }
    

    curRange -= 0.4D;
    if (curRange < 0.0D) {
      curRange = 0.0D;
    }
    
    Location loc = player.getLocation().add(player.getLocation().getDirection().multiply(curRange).add(new Vector(0.0D, 0.4D, 0.0D)));
    
    if (curRange > 0.0D)
    {

      FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(org.bukkit.Color.BLACK).with(FireworkEffect.Type.BALL).trail(false).build();
      
      try
      {
        this.Manager.GetFirework().playFirework(player.getEyeLocation(), effect);
        player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      
      player.teleport(loc);
      

      try
      {
        this.Manager.GetFirework().playFirework(player.getEyeLocation(), effect);
        player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    

    player.setFallDistance(0.0F);
    

    mineplex.core.common.util.UtilPlayer.message(player, F.main("Game", "You used " + F.skill(this._name) + "."));
  }
}
