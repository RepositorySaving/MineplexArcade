package nautilus.game.arcade.game.games.smash.kits;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSkeleton;
import mineplex.core.itemstack.ItemStackFactory;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.KitAvailability;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.kit.SmashKit;
import nautilus.game.arcade.kit.perks.PerkBarrage;
import nautilus.game.arcade.kit.perks.PerkBoneExplosion;
import nautilus.game.arcade.kit.perks.PerkDoubleJump;
import nautilus.game.arcade.kit.perks.PerkFletcher;
import nautilus.game.arcade.kit.perks.PerkKnockbackArrow;
import nautilus.game.arcade.kit.perks.PerkRopedArrow;
import nautilus.game.arcade.kit.perks.PerkSmashStats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;














public class KitSkeleton
  extends SmashKit
{
  public KitSkeleton(ArcadeManager manager)
  {
    super(manager, "Skeleton", KitAvailability.Free, new String[0], new Perk[] {new PerkSmashStats(5.0D, 1.25D, 0.2D, 6.0D), new PerkDoubleJump("Double Jump", 0.9D, 0.9D, false), new PerkFletcher(1, 2, false), new PerkKnockbackArrow(2.0D), new PerkBoneExplosion(), new PerkRopedArrow("Roped Arrow", 1.0D, 5000L), new PerkBarrage(5, 250L, true, false) }, EntityType.SKELETON, new ItemStack(Material.BOW));
  }
  

  public void GiveItems(Player player)
  {
    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 0, 1, 
      C.cYellow + C.Bold + "Right-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Bone Explosion", 
      
      new String[] {
      ChatColor.RESET + "Releases an explosion of bones from", 
      ChatColor.RESET + "your body, repelling all nearby enemies." }) });
    

    player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BOW, 0, 1, 
      C.cYellow + C.Bold + "Left-Click" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Roped Arrow", 
      
      new String[] {
      ChatColor.RESET + "Instantly fires an arrow. When it ", 
      ChatColor.RESET + "collides with something, you are pulled", 
      ChatColor.RESET + "towards it, with great power." }) });
    

    if (this.Manager.GetGame().GetState() == Game.GameState.Recruit) {
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BONE, 0, 1, 
        C.cYellow + C.Bold + "Charge Bow" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Barrage", 
        
        new String[] {
        ChatColor.RESET + "Slowly load more arrows into your bow.", 
        ChatColor.RESET + "When you release, you will quickly fire", 
        ChatColor.RESET + "all the arrows in succession." }) });
    }
    
    player.getInventory().setHelmet(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_HELMET));
    player.getInventory().setChestplate(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_CHESTPLATE));
    player.getInventory().setLeggings(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_LEGGINGS));
    player.getInventory().setBoots(ItemStackFactory.Instance.CreateStack(Material.CHAINMAIL_BOOTS));
    

    DisguiseSkeleton disguise = new DisguiseSkeleton(player);
    
    if (this.Manager.GetGame().GetTeam(player) != null) {
      disguise.SetName(this.Manager.GetGame().GetTeam(player).GetColor() + player.getName());
    } else {
      disguise.SetName(player.getName());
    }
    disguise.SetCustomNameVisible(true);
    disguise.hideArmor();
    this.Manager.GetDisguise().disguise(disguise);
  }
}
