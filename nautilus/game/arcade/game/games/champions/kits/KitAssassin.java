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

public class KitAssassin extends Kit
{
  private HashMap<Player, ClientClass> _class = new HashMap();
  















  public KitAssassin(ArcadeManager manager)
  {
    super(manager, "Assassin", KitAvailability.Free, new String[] {"Extremely agile warrior, trained in", "the mystic arts of assassination.", "", "Attack Damage increased by 1", "Fall Damage reduced by 1", "Permanent Speed 2" }, new nautilus.game.arcade.kit.Perk[0], org.bukkit.entity.EntityType.ZOMBIE, new ItemStack(Material.IRON_SWORD));
  }
  


  public void Deselected(Player player)
  {
    this._class.remove(player);
  }
  

  public void Selected(Player player)
  {
    this._class.put(player, (ClientClass)this.Manager.getClassManager().Get(player));
    ClientClass clientClass = (ClientClass)this._class.get(player);
    IPvpClass pvpClass = this.Manager.getClassManager().GetClass("Assassin");
    
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
    ent.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
    ent.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
    ent.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
    ent.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
  }
  
  public void DisplayDesc(Player player) {}
}
