package nautilus.game.arcade.addons;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilPlayer;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SoupAddon extends MiniPlugin
{
  public ArcadeManager Manager;
  
  public SoupAddon(JavaPlugin plugin, ArcadeManager manager)
  {
    super("Soup Addon", plugin);
    
    this.Manager = manager;
  }
  
  @EventHandler
  public void EatSoup(PlayerInteractEvent event)
  {
    if (this.Manager.GetGame() == null) {
      return;
    }
    if (!this.Manager.GetGame().IsLive()) {
      return;
    }
    if (!this.Manager.GetGame().SoupEnabled) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!mineplex.core.common.util.UtilGear.isMat(player.getItemInHand(), Material.MUSHROOM_SOUP)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    
    player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.EAT, 2.0F, 1.0F);
    player.getWorld().playEffect(player.getEyeLocation(), Effect.STEP_SOUND, 39);
    player.getWorld().playEffect(player.getEyeLocation(), Effect.STEP_SOUND, 40);
    

    this.Manager.GetCondition().Factory().Custom("Mushroom Soup", player, player, mineplex.minecraft.game.core.condition.Condition.ConditionType.REGENERATION, 4.0D, 1, false, Material.MUSHROOM_SOUP, (byte)0, true);
    

    UtilPlayer.hunger(player, 3);
    
    event.setCancelled(true);
    player.setItemInHand(null);
  }
}
