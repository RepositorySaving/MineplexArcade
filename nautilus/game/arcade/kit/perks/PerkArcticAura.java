package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PerkArcticAura extends Perk
{
  public PerkArcticAura()
  {
    super("Arctic Aura", new String[] {"You freeze things around you, slowing enemies." });
  }
  

  @EventHandler
  public void SnowAura(UpdateEvent event)
  {
    if (event.getType() != mineplex.core.updater.UpdateType.TICK) {
      return;
    }
    for (Player player : this.Manager.GetGame().GetPlayers(true))
    {
      if (this.Kit.HasKit(player))
      {

        double range = 5.0F * player.getExp();
        

        double duration = 2000.0D;
        HashMap<Block, Double> blocks = UtilBlock.getInRadius(player.getLocation(), range);
        for (Block block : blocks.keySet())
        {

          this.Manager.GetBlockRestore().Snow(block, (byte)1, (byte)1, (duration * (1.0D + ((Double)blocks.get(block)).doubleValue())), 250L, 0);
        }
        
        for (Player other : this.Manager.GetGame().GetPlayers(true))
        {
          if (!other.equals(player))
          {

            if (UtilMath.offset(player, other) <= range)
            {

              this.Manager.GetCondition().Factory().Slow("Aura Slow", other, player, 0.9D, 0, false, false, false, false);
            }
          }
        }
      }
    }
  }
}
