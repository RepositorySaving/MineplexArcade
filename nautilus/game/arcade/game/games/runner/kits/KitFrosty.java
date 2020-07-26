package nautilus.game.arcade.game.games.runner.kits;

import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.perks.PerkConstructor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;









public class KitFrosty
  extends Kit
{
  public KitFrosty(ArcadeManager manager)
  {
    super(manager, "Frosty", KitAvailability.Blue, new String[] {"Slow enemies to send them to their death!" }, new Perk[] {new PerkConstructor("Frost Balls", 0.5D, 16, Material.SNOW_BALL, "Snowball", true) }, EntityType.SKELETON, new ItemStack(Material.SNOW_BALL));
  }
  



  public void GiveItems(Player player) {}
  


  @EventHandler
  public void SnowballHit(CustomDamageEvent event)
  {
    if (event.GetProjectile() == null) {
      return;
    }
    if (!(event.GetProjectile() instanceof Snowball)) {
      return;
    }
    event.GetDamageeEntity().playEffect(EntityEffect.HURT);
    
    this.Manager.GetCondition().Factory().Slow("Snowball Slow", event.GetDamageeEntity(), event.GetProjectile().getShooter(), 2.0D, 1, false, false, true, false);
  }
}
