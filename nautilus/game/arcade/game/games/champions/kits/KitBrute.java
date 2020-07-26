package nautilus.game.arcade.game.games.champions.kits;

import java.util.HashMap;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.KitAvailability;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class KitBrute extends Kit
{
  private HashMap<Player, ClientClass> _class = new HashMap();
  














  public KitBrute(ArcadeManager manager)
  {
    super(manager, "Brute", KitAvailability.Free, new String[] {"A giant of a man, able to smash and", "destroy anything in his path.", "", "Takes additional damage to counter", "the strength of Diamond Armor." }, new nautilus.game.arcade.kit.Perk[0], org.bukkit.entity.EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  


  public void Deselected(Player player)
  {
    this._class.remove(player);
  }
  

  public void Selected(Player player)
  {
    this._class.put(player, (ClientClass)this.Manager.getClassManager().Get(player));
    ClientClass clientClass = (ClientClass)this._class.get(player);
    IPvpClass pvpClass = this.Manager.getClassManager().GetClass("Brute");
    
    clientClass.SetGameClass(pvpClass);
    pvpClass.ApplyArmor(player);
    clientClass.ClearDefaults();
    clientClass.EquipCustomBuild((CustomBuildToken)clientClass.GetCustomBuilds(pvpClass).get(Integer.valueOf(0)));
    
    if (!this.Manager.GetGame().InProgress()) {
      clientClass.SetActiveCustomBuild(pvpClass, pvpClass.getDefaultBuild());
    }
    this.Manager.openClassShop(player);
  }
  

  public void GiveItems(Player player)
  {
    ((ClientClass)this._class.get(player)).ResetToDefaults(true, true);
  }
  

  public void SpawnCustom(LivingEntity ent)
  {
    ent.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
    ent.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
  }
  
  public void DisplayDesc(Player player) {}
}
