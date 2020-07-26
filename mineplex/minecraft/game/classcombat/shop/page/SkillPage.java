package mineplex.minecraft.game.classcombat.shop.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ConfirmationPage;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Class.ClientClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.item.Item;
import mineplex.minecraft.game.classcombat.shop.ClassCombatShop;
import mineplex.minecraft.game.classcombat.shop.ClassShopManager;
import mineplex.minecraft.game.classcombat.shop.button.SelectSkillButton;
import net.minecraft.server.v1_7_R3.IInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SkillPage extends ShopPageBase<ClassShopManager, ClassCombatShop>
{
  private IPvpClass _pvpClass;
  
  public SkillPage(ClassShopManager plugin, ClassCombatShop shop, CoreClientManager clientManager, DonationManager donationManager, Player player, IPvpClass pvpClass)
  {
    super(plugin, shop, clientManager, donationManager, "    Select Skills", player);
    
    this._pvpClass = pvpClass;
    
    BuildPage();
  }
  
  public void PlayerClosed()
  {
    super.PlayerClosed();
    
    if ((this.Player != null) && (this.Player.isOnline()))
    {
      for (int i = 9; i < 36; i++)
      {
        this.Player.getInventory().setItem(i, null);
      }
    }
  }
  

  protected void BuildPage()
  {
    this.ButtonMap.clear();
    clear();
    
    ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(this.Player);
    
    BuildClassSkills(this._pvpClass, clientClass);
    BuildGlobalSkills(clientClass);
    BuildItems(this._pvpClass, clientClass);
  }
  
  private void BuildItems(IPvpClass gameClass, ClientClass clientClass)
  {
    if (clientClass.GetSavingCustomBuild().ItemTokens > 0) {
      AddItem(62, new ShopItem(Material.IRON_INGOT, clientClass.GetSavingCustomBuild().ItemTokens + " Item Tokens", null, clientClass.GetSavingCustomBuild().ItemTokens, true, true));
    } else {
      AddItem(62, new ShopItem(Material.REDSTONE_BLOCK, "0 Item Tokens", null, 1, true, true));
    }
    
    int slotNumber = 54;
    
    int swordSlotNumber = 72;
    int axeSlotNumber = 73;
    int bowSlotNumber = 74;
    int i;
    label525: for (Iterator localIterator = ((ClassShopManager)this.Plugin).GetItemFactory().GetItems().iterator(); localIterator.hasNext(); 
        













































        i < 9)
    {
      Item item = (Item)localIterator.next();
      
      if (item.GetName().contains("Sword"))
      {
        slotNumber = swordSlotNumber;
        swordSlotNumber -= 9;
      }
      else if (item.GetName().contains("Axe"))
      {
        slotNumber = axeSlotNumber;
        axeSlotNumber -= 9;
      }
      else if (item.GetName().contains("Bow"))
      {
        if ((gameClass.GetType() != IPvpClass.ClassType.Assassin) && (gameClass.GetType() != IPvpClass.ClassType.Ranger)) {
          break label525;
        }
        slotNumber = bowSlotNumber;
        bowSlotNumber -= 9;
      }
      else
      {
        if ((gameClass.GetType() != IPvpClass.ClassType.Assassin) && (gameClass.GetType() != IPvpClass.ClassType.Ranger) && (item.GetName().contains("Arrow"))) {
          break label525;
        }
        if ((gameClass.GetType() == IPvpClass.ClassType.Assassin) && (item.GetName().contains("Ranger"))) {
          break label525;
        }
        if ((gameClass.GetType() == IPvpClass.ClassType.Ranger) && (item.GetName().contains("Assassin"))) {
          break label525;
        }
        if (item.GetType() == Material.ARROW) {
          slotNumber = 65;
        } else if (item.GetType() == Material.MUSHROOM_SOUP) {
          slotNumber = 67;
        } else if (item.GetType() == Material.COMMAND) {
          slotNumber = 68;
        } else if (item.GetType() == Material.WEB) {
          slotNumber = 69;
        } else if (item.GetType() == Material.POTION) {
          slotNumber = 76;
        } else if (item.GetType() == Material.REDSTONE_LAMP_OFF) {
          slotNumber = 77;
        }
      }
      BuildItem(item, slotNumber, clientClass);
      
      i = 0; continue;
      
      ItemStack itemStack = this.Player.getInventory().getItem(i);
      
      if ((itemStack != null) && (itemStack.getType() == item.GetType()) && (itemStack.getAmount() == item.GetAmount()))
      {
        this.ButtonMap.put(Integer.valueOf(81 + i), new mineplex.minecraft.game.classcombat.shop.button.DeselectItemButton(this, item, i));
      }
      i++;
    }
  }
  








  private void BuildClassSkills(IPvpClass gameClass, ClientClass clientClass)
  {
    if (clientClass.GetSavingCustomBuild().SkillTokens > 0) {
      getInventory().setItem(8, new ShopItem(Material.GOLD_INGOT, clientClass.GetSavingCustomBuild().SkillTokens + " Skill Tokens", null, clientClass.GetSavingCustomBuild().SkillTokens, true, true).getHandle());
    } else {
      getInventory().setItem(8, new ShopItem(Material.REDSTONE_BLOCK, "0 Skill Tokens", null, 1, true, true).getHandle());
    }
    getInventory().setItem(0, new ShopItem(Material.IRON_SWORD, "Sword Skills", null, 1, true, true).getHandle());
    getInventory().setItem(9, new ShopItem(Material.IRON_AXE, "Axe Skills", null, 1, true, true).getHandle());
    getInventory().setItem(18, new ShopItem(Material.BOW, "Bow Skills", null, 1, true, true).getHandle());
    getInventory().setItem(27, new ShopItem(Material.INK_SACK, (byte)1, "Class Passive A Skills", null, 1, true, true).getHandle());
    getInventory().setItem(36, new ShopItem(Material.INK_SACK, (byte)14, "Class Passive B Skills", null, 1, true, true).getHandle());
    
    int slotNumber = 53;
    
    int swordSlotNumber = 1;
    int axeSlotNumber = 10;
    int bowSlotNumber = 19;
    int passiveASlotNumber = 28;
    int passiveBSlotNumber = 37;
    
    for (ISkill skill : ((ClassShopManager)this.Plugin).GetSkillFactory().GetSkillsFor(gameClass))
    {
      switch (skill.GetSkillType())
      {
      case Class: 
        slotNumber = swordSlotNumber;
        swordSlotNumber++;
        break;
      case Axe: 
        slotNumber = axeSlotNumber;
        axeSlotNumber++;
        break;
      case Bow: 
        slotNumber = bowSlotNumber;
        bowSlotNumber++;
        break;
      case GlobalPassive: 
        slotNumber = passiveASlotNumber;
        passiveASlotNumber++;
        break;
      case PassiveA: 
        slotNumber = passiveBSlotNumber;
        passiveBSlotNumber++;
        break;
      }
      
      


      BuildSkillItem(skill, slotNumber, clientClass);
    }
  }
  
  private void BuildGlobalSkills(ClientClass clientClass)
  {
    getInventory().setItem(45, new ShopItem(Material.INK_SACK, (byte)11, "Global Passive Skills", null, 1, true, true).getHandle());
    
    int slotNumber = 46;
    
    for (ISkill skill : ((ClassShopManager)this.Plugin).GetSkillFactory().GetGlobalSkillsFor(clientClass.GetGameClass()))
    {
      BuildSkillItem(skill, slotNumber++, clientClass);
    }
  }
  
  protected void BuildSkillItem(ISkill skill, int slotNumber, ClientClass clientClass)
  {
    List<String> skillLore = new ArrayList();
    
    boolean locked = isSkillLocked(skill);
    Material material = clientClass.GetSavingCustomBuild().hasSkill(skill) ? Material.WRITTEN_BOOK : locked ? Material.EMERALD : Material.BOOK;
    boolean hasSkill = clientClass.GetSavingCustomBuild().hasSkill(skill);
    int level = hasSkill ? clientClass.GetSavingCustomBuild().getLevel(skill) : 1;
    String name = skill.GetName() + 
      ChatColor.RESET + C.Bold + " - " + ChatColor.GREEN + C.Bold + "Level " + (hasSkill ? level : 0) + "/" + skill.getMaxLevel();
    

    if (locked)
    {
      skillLore.add(C.cYellow + skill.GetGemCost() + " Gems");
      skillLore.add(C.cBlack);
    }
    

    skillLore.addAll(Arrays.asList(skill.GetDesc(hasSkill ? level : 0)));
    

    skillLore.add("");
    skillLore.add("");
    if ((!hasSkill) || (level < skill.getMaxLevel()))
    {
      skillLore.add(C.cYellow + "Skill Token Cost: " + C.cWhite + skill.GetTokenCost());
      skillLore.add(C.cYellow + "Skill Tokens Remaining: " + C.cWhite + clientClass.GetSavingCustomBuild().SkillTokens + "/" + CustomBuildToken.MAX_SKILL_TOKENS);
      skillLore.add("");
      
      if (clientClass.GetSavingCustomBuild().SkillTokens >= skill.GetTokenCost())
      {
        if (hasSkill) {
          skillLore.add(C.cGreen + "Left-Click to Upgrade to Level " + (level + 1));
        } else {
          skillLore.add(C.cGreen + "Left-Click to Select");
        }
      }
      else {
        skillLore.add(C.cRed + "You don't have enough Skill Tokens.");
      }
    }
    else
    {
      skillLore.add(C.cGold + "You have the maximum Level.");
    }
    

    for (int i = 0; i < skillLore.size(); i++)
    {
      skillLore.set(i, C.cGray + (String)skillLore.get(i));
    }
    

    ShopItem skillItem = new ShopItem(material, name, (String[])skillLore.toArray(new String[skillLore.size()]), level, locked, true);
    
    if (locked) {
      AddButton(slotNumber, skillItem, new mineplex.minecraft.game.classcombat.shop.button.PurchaseSkillButton(this, skill));
    } else {
      AddButton(slotNumber, skillItem, new SelectSkillButton(this, skill, Math.min(hasSkill ? level + 1 : level, skill.getMaxLevel()), clientClass.GetSavingCustomBuild().SkillTokens >= skill.GetTokenCost()));
    }
  }
  
  protected void BuildItem(Item item, int slotNumber, ClientClass clientClass) {
    List<String> itemLore = new ArrayList();
    
    boolean locked = isItemLocked(item);
    Material material = locked ? Material.EMERALD : item.GetType();
    boolean hasItem = locked ? false : clientClass.GetSavingCustomBuild().hasItem(material, item.GetName());
    
    String name = locked ? ChatColor.RED + item.GetName() + " (Locked)" : item.GetName();
    
    if (locked)
    {
      itemLore.add(C.cYellow + item.GetGemCost() + " Gems");
      itemLore.add(C.cBlack);
    }
    

    itemLore.addAll(Arrays.asList(item.GetDesc()));
    

    itemLore.add("");
    itemLore.add("");
    
    itemLore.add(C.cYellow + "Item Token Cost: " + C.cWhite + item.getTokenCost());
    itemLore.add(C.cYellow + "Item Tokens Remaining: " + C.cWhite + clientClass.GetSavingCustomBuild().ItemTokens + "/" + CustomBuildToken.MAX_ITEM_TOKENS);
    itemLore.add("");
    
    if (clientClass.GetSavingCustomBuild().ItemTokens >= item.getTokenCost())
    {
      itemLore.add(C.cGreen + "Left-Click to Select");
    }
    else
    {
      itemLore.add(C.cRed + "You don't have enough Item Tokens.");
    }
    

    for (int i = 0; i < itemLore.size(); i++)
    {
      itemLore.set(i, C.cGray + (String)itemLore.get(i));
    }
    
    ShopItem itemGUI = new ShopItem(material, name, (String[])itemLore.toArray(new String[itemLore.size()]), item.GetAmount(), locked, true);
    
    if (locked) {
      AddButton(slotNumber, itemGUI, new mineplex.minecraft.game.classcombat.shop.button.PurchaseItemButton(this, item));
    } else {
      AddButton(slotNumber, itemGUI, new mineplex.minecraft.game.classcombat.shop.button.SelectItemButton(this, item, clientClass.GetSavingCustomBuild().ItemTokens >= item.getTokenCost()));
    }
  }
  
  public void SelectSkill(Player player, ISkill skill, int level) {
    ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player);
    ISkill existingSkill = clientClass.GetSkillByType(skill.GetSkillType());
    
    if (existingSkill != null)
    {
      clientClass.RemoveSkill(existingSkill);
    }
    
    if (level > 0)
    {
      clientClass.AddSkill(skill, level);
    }
    
    PlayAcceptSound(player);
    
    BuildPage();
  }
  
  public void DeselectSkill(Player player, ISkill skill)
  {
    if (skill.getLevel(player) == 0) {
      return;
    }
    ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player);
    ISkill existingSkill = clientClass.GetSkillByType(skill.GetSkillType());
    
    if (existingSkill == null)
    {
      return;
    }
    
    int level = existingSkill.getLevel(player) - 1;
    
    clientClass.RemoveSkill(existingSkill);
    
    if (level > 0)
    {
      clientClass.AddSkill(skill, level);
    }
    
    PlayRemoveSound(player);
    
    BuildPage();
  }
  
  public void PurchaseSkill(Player player, ISkill skill)
  {
    ((ClassCombatShop)this.Shop).OpenPageForPlayer(player, new ConfirmationPage((ClassShopManager)this.Plugin, (ClassCombatShop)this.Shop, this.ClientManager, this.DonationManager, new Runnable()
    {
      public void run()
      {
        SkillPage.this.BuildPage();
      }
    }, this, new mineplex.minecraft.game.classcombat.shop.salespackage.SkillSalesPackage(skill), CurrencyType.Gems, player));
  }
  
  private boolean isSkillLocked(ISkill skill)
  {
    if ((skill.IsFree()) || (this.ClientManager.Get(this.Player.getName()).GetRank().Has(Rank.ULTRA)) || (this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage("Champions ULTRA")) || (this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage("Champions " + skill.GetName()))) {
      return false;
    }
    return true;
  }
  
  private boolean isItemLocked(Item item)
  {
    if ((item.isFree()) || (this.ClientManager.Get(this.Player.getName()).GetRank().Has(Rank.ULTRA)) || (this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage("Champions ULTRA")) || (this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage("Champions " + item.GetName()))) {
      return false;
    }
    return true;
  }
  
  public void PurchaseItem(Player player, Item item)
  {
    ((ClassCombatShop)this.Shop).OpenPageForPlayer(player, new ConfirmationPage((ClassShopManager)this.Plugin, (ClassCombatShop)this.Shop, this.ClientManager, this.DonationManager, new Runnable()
    {
      public void run()
      {
        SkillPage.this.BuildPage();
      }
    }, this, new mineplex.minecraft.game.classcombat.shop.salespackage.ItemSalesPackage(item), CurrencyType.Gems, player));
  }
  
  public void SelectItem(Player player, Item item)
  {
    int index = -1;
    ClientClass clientClass = (ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player);
    




































    if ((index == -1) && (player.getInventory().firstEmpty() < 9)) {
      index = player.getInventory().firstEmpty();
    }
    if (index != -1)
    {
      PlayAcceptSound(player);
      
      clientClass.GetSavingCustomBuild().addItem(item, index);
      
      ItemStack itemStack = ItemStackFactory.Instance.CreateStack(item.GetType(), (byte)0, item.GetAmount(), item.GetName());
      
      if (item.GetName().contains("Booster")) {
        itemStack.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 5);
      }
      player.getInventory().setItem(index, itemStack);
    }
    else
    {
      PlayDenySound(player);
    }
    
    BuildPage();
  }
  
  public void DeselectItem(Player player, Item item)
  {
    DeselectItem(player, item, ((ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player)).GetSavingCustomBuild().getLastItemIndexWithNameLike(item.GetName()));
  }
  
  public void DeselectItem(Player player, Item item, int index)
  {
    if (index != -1)
    {
      PlayAcceptSound(player);
      
      ((ClientClass)((ClassShopManager)this.Plugin).GetClassManager().Get(player)).GetSavingCustomBuild().removeItem(item, index);
      player.getInventory().setItem(index, null);
    }
    else
    {
      PlayDenySound(player);
    }
    
    BuildPage();
  }
}
