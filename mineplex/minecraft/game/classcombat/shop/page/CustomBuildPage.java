package mineplex.minecraft.game.classcombat.shop.page;

import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.shop.item.ShopItem;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Class.event.ClassSetupEvent;
import mineplex.minecraft.game.classcombat.Class.event.ClassSetupEvent.SetupType;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.classcombat.shop.ClassShopManager;
import net.minecraft.server.v1_7_R3.IInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class CustomBuildPage extends mineplex.core.shop.page.ShopPageBase<ClassShopManager, ClassCombatShop>
{
  private IPvpClass _pvpClass;
  
  public CustomBuildPage(ClassShopManager shopManager, ClassCombatShop shop, CoreClientManager clientManager, DonationManager donationManager, Player player)
  {
    super(shopManager, shop, clientManager, donationManager, "       Custom Build", player);
    this._pvpClass = ((ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player)).GetGameClass();
    
    BuildPage();
  }
  

  protected void BuildPage()
  {
    int slot = 9;
    
    for (int i = 0; i < 5; i++)
    {

      String[] lockedText = new String[0];
      boolean locked = false;
      byte itemData;
      byte itemData; byte itemData; switch (i)
      {
      case 0: 
        itemData = 1;
        break;
      case 1: 
        itemData = 14;
        break;
      case 2: 
        byte itemData = 11;
        
        if ((!this.Client.GetRank().Has(Rank.ULTRA)) && (!this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage("Competitive ULTRA")))
        {
          locked = true;
          lockedText = new String[] { "§rGet Ultra rank to access this slot" };
        }
        break;
      case 3: 
        byte itemData = 2;
        
        if ((!this.Client.GetRank().Has(Rank.ULTRA)) && (!this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage("Competitive ULTRA")))
        {
          locked = true;
          lockedText = new String[] { "§rGet Ultra rank to access this slot" };
        }
        break;
      default: 
        itemData = 4;
        
        if ((!this.Client.GetRank().Has(Rank.ULTRA)) && (!this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage("Competitive ULTRA")))
        {
          locked = true;
          lockedText = new String[] { "§rGet Ultra rank to access this slot" };
        }
        break;
      }
      
      ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(this.Player);
      
      CustomBuildToken customBuild = (CustomBuildToken)clientClass.GetCustomBuilds(this._pvpClass).get(Integer.valueOf(i));
      
      if (customBuild != null)
      {
        AddButton(slot, new ShopItem(Material.INK_SACK, itemData, "Apply " + customBuild.Name, lockedText, 1, locked, true), new mineplex.minecraft.game.classcombat.shop.button.SelectCustomBuildButton(this, customBuild));
      }
      else
      {
        getInventory().setItem(slot, new ShopItem(Material.INK_SACK, (byte)8, locked ? "Locked Build" : "Unsaved Build", lockedText, 1, locked, true).getHandle());
      }
      
      if (!locked)
      {
        if (customBuild == null)
        {
          customBuild = new CustomBuildToken(this._pvpClass.GetType());
          customBuild.CustomBuildNumber = Integer.valueOf(i);
          customBuild.Name = ("Build " + (i + 1));
          customBuild.PvpClass = this._pvpClass.GetName();
        }
        
        if (i != 0)
        {
          AddButton(slot + 18, new ShopItem(Material.ANVIL, "Edit Build", new String[0], 1, locked, true), new mineplex.minecraft.game.classcombat.shop.button.EditAndSaveCustomBuildButton(this, customBuild));
          AddButton(slot + 36, new ShopItem(Material.FIRE, "Delete Build", new String[] { "§rIt will never come back..." }, 1, locked, true), new mineplex.minecraft.game.classcombat.shop.button.DeleteCustomBuildButton(this, customBuild));
        }
      }
      else
      {
        getInventory().setItem(slot + 18, new ShopItem(Material.ANVIL, "Edit Build", new String[0], 1, locked, true).getHandle());
        getInventory().setItem(slot + 36, new ShopItem(Material.FIRE, "Delete Build", new String[] { "§rIt will never come back..." }, 1, locked, true).getHandle());
      }
      
      slot += 2;
    }
  }
  
  public void EditAndSaveCustomBuild(CustomBuildToken customBuild)
  {
    ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(this.Player);
    clientClass.SetActiveCustomBuild(this._pvpClass, customBuild);
    
    ClassSetupEvent event = new ClassSetupEvent(this.Player, ClassSetupEvent.SetupType.SaveEditCustomBuild, this._pvpClass.GetType(), customBuild.CustomBuildNumber.intValue(), customBuild);
    ((ClassShopManager)this.Plugin).GetPlugin().getServer().getPluginManager().callEvent(event);
    
    if (event.IsCancelled()) {
      return;
    }
    clientClass.EquipCustomBuild(customBuild, false);
    clientClass.SetSavingCustomBuild(this._pvpClass, customBuild);
    
    ((ClassCombatShop)this.Shop).OpenPageForPlayer(this.Player, new SkillPage((ClassShopManager)this.Plugin, (ClassCombatShop)this.Shop, this.ClientManager, this.DonationManager, this.Player, this._pvpClass));
  }
  
  public void SelectCustomBuild(CustomBuildToken customBuild)
  {
    ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(this.Player);
    clientClass.SetActiveCustomBuild(this._pvpClass, customBuild);
    
    ClassSetupEvent event = new ClassSetupEvent(this.Player, ClassSetupEvent.SetupType.ApplyCustomBuild, this._pvpClass.GetType(), customBuild.CustomBuildNumber.intValue() + 1, customBuild);
    ((ClassShopManager)this.Plugin).GetPluginManager().callEvent(event);
    
    if (event.IsCancelled()) {
      return;
    }
    clientClass.EquipCustomBuild(customBuild);
    
    this.Player.closeInventory();
  }
  

  public void DeleteCustomBuild(CustomBuildToken customBuild)
  {
    ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(this.Player);
    

    ClassSetupEvent event = new ClassSetupEvent(this.Player, ClassSetupEvent.SetupType.DeleteCustomBuild, this._pvpClass.GetType(), customBuild.CustomBuildNumber.intValue() + 1, customBuild);
    ((ClassShopManager)this.Plugin).GetPlugin().getServer().getPluginManager().callEvent(event);
    
    if (event.IsCancelled()) {
      return;
    }
    clientClass.GetCustomBuilds(this._pvpClass).remove(customBuild.CustomBuildNumber);
    
    BuildPage();
    this.Player.updateInventory();
  }
}
