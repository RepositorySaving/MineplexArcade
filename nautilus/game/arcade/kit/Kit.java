package nautilus.game.arcade.kit;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.creature.Creature;
import nautilus.game.arcade.ArcadeFormat;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;


public abstract class Kit
  implements Listener
{
  public ArcadeManager Manager;
  private String _kitName;
  private String[] _kitDesc;
  private KitAvailability _kitAvailability;
  private Perk[] _kitPerks;
  protected EntityType _entityType;
  protected ItemStack _itemInHand;
  protected Material _displayItem;
  
  public Kit(ArcadeManager manager, String name, KitAvailability kitAvailability, String[] kitDesc, Perk[] kitPerks, EntityType entityType, ItemStack itemInHand)
  {
    this.Manager = manager;
    
    this._kitName = name;
    this._kitDesc = kitDesc;
    this._kitPerks = kitPerks;
    
    for (Perk perk : this._kitPerks) {
      perk.SetHost(this);
    }
    this._kitAvailability = kitAvailability;
    
    this._entityType = entityType;
    this._itemInHand = itemInHand;
    
    this._displayItem = Material.BOOK;
    if (itemInHand != null) {
      this._displayItem = itemInHand.getType();
    }
  }
  
  public String GetFormattedName() {
    return GetAvailability().GetColor() + "§l" + this._kitName;
  }
  
  public String GetName()
  {
    return this._kitName;
  }
  
  public ItemStack GetItemInHand()
  {
    return this._itemInHand;
  }
  
  public KitAvailability GetAvailability()
  {
    return this._kitAvailability;
  }
  
  public String[] GetDesc()
  {
    return this._kitDesc;
  }
  
  public Perk[] GetPerks()
  {
    return this._kitPerks;
  }
  
  public boolean HasKit(Player player)
  {
    if (this.Manager.GetGame() == null) {
      return false;
    }
    return this.Manager.GetGame().HasKit(player, this);
  }
  
  public void ApplyKit(Player player)
  {
    UtilInv.Clear(player);
    
    for (Perk perk : this._kitPerks) {
      perk.Apply(player);
    }
    GiveItems(player);
    
    UtilInv.Update(player);
  }
  
  public abstract void GiveItems(Player paramPlayer);
  
  public Entity SpawnEntity(Location loc)
  {
    EntityType type = this._entityType;
    if (type == EntityType.PLAYER) {
      type = EntityType.ZOMBIE;
    }
    
    LivingEntity entity = (LivingEntity)this.Manager.GetCreature().SpawnEntity(loc, type);
    
    entity.setRemoveWhenFarAway(false);
    entity.setCustomName(GetAvailability().GetColor() + GetName() + " Kit" + (GetAvailability() == KitAvailability.Blue ? ChatColor.GRAY + " (" + ChatColor.WHITE + "Ultra" + ChatColor.GRAY + ")" : ""));
    entity.setCustomNameVisible(true);
    entity.getEquipment().setItemInHand(this._itemInHand);
    
    if ((type == EntityType.SKELETON) && ((GetName().contains("Wither")) || (GetName().contains("Alpha"))))
    {
      Skeleton skel = (Skeleton)entity;
      skel.setSkeletonType(Skeleton.SkeletonType.WITHER);
    }
    
    UtilEnt.Vegetate(entity);
    UtilEnt.silence(entity, true);
    UtilEnt.ghost(entity, true, false);
    
    SpawnCustom(entity);
    
    return entity;
  }
  
  public void SpawnCustom(LivingEntity ent) {}
  
  public void DisplayDesc(Player player)
  {
    for (int i = 0; i < 3; i++) {
      UtilPlayer.message(player, "");
    }
    UtilPlayer.message(player, ArcadeFormat.Line);
    
    UtilPlayer.message(player, "§aKit - §f§l" + GetName());
    

    for (String line : GetDesc())
    {
      UtilPlayer.message(player, C.cGray + "  " + line);
    }
    

    for (Perk perk : GetPerks())
    {
      if (perk.IsVisible())
      {

        UtilPlayer.message(player, "");
        UtilPlayer.message(player, C.cWhite + C.Bold + perk.GetName());
        for (String line : perk.GetDesc())
        {
          UtilPlayer.message(player, C.cGray + "  " + line);
        }
      }
    }
    UtilPlayer.message(player, ArcadeFormat.Line);
  }
  

  public int GetCost()
  {
    return 2000;
  }
  
  public Material getDisplayMaterial()
  {
    return this._displayItem;
  }
  
  public void Deselected(Player player) {}
  
  public void Selected(Player player) {}
}
