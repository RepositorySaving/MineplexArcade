package mineplex.minecraft.game.core.damage.compatibility;

import mineplex.core.npc.NpcManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NpcProtectListener implements Listener
{
  private NpcManager _npcManager;
  
  public NpcProtectListener(NpcManager npcManager)
  {
    this._npcManager = npcManager;
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void CustomDamage(CustomDamageEvent event)
  {
    if ((event.GetDamageeEntity() != null) && (this._npcManager.GetNpcByUUID(event.GetDamageeEntity().getUniqueId()) != null))
    {
      event.SetCancelled("NPC");
    }
  }
}
