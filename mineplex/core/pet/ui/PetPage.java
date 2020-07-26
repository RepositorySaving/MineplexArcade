package mineplex.core.pet.ui;

import java.util.ArrayList;
import java.util.List;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.C;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.pet.Pet;
import mineplex.core.pet.PetClient;
import mineplex.core.pet.PetExtra;
import mineplex.core.pet.PetFactory;
import mineplex.core.pet.PetManager;
import mineplex.core.pet.PetShop;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ShopPageBase;
import net.minecraft.server.v1_7_R3.Container;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.IInventory;
import net.minecraft.server.v1_7_R3.Items;
import net.minecraft.server.v1_7_R3.PacketPlayOutOpenWindow;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PetPage extends ShopPageBase<PetManager, PetShop>
{
  public PetPage(PetManager plugin, PetShop shop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player)
  {
    super(plugin, shop, clientManager, donationManager, name, player, 27);
    
    BuildPage();
  }
  
  protected void BuildPage()
  {
    int slot = 1;
    
    List<Pet> pets = new ArrayList(((PetManager)this.Plugin).GetFactory().GetPets());
    
    java.util.Collections.sort(pets, new PetSorter());
    
    for (Pet pet : pets)
    {
      List<String> itemLore = new ArrayList();
      
      itemLore.add(C.cYellow + pet.GetCost(CurrencyType.Gems) + " Gems");
      itemLore.add(C.cBlack);
      
      if (this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage(pet.GetPetName()))
      {
        if ((((PetManager)this.Plugin).hasActivePet(this.Player.getName())) && (((PetManager)this.Plugin).getActivePet(this.Player.getName()).getType() == pet.GetPetType()))
        {
          AddButton(slot, new ShopItem(Material.MONSTER_EGG, (byte)pet.GetPetType().getTypeId(), "Deactivate " + (String)((PetClient)((PetManager)this.Plugin).Get(this.Player)).GetPets().get(pet.GetPetType()), new String[0], 1, false, false), new DeactivatePetButton(pet, this));
        }
        else
        {
          AddButton(slot, new ShopItem(Material.MONSTER_EGG, (byte)pet.GetPetType().getTypeId(), "Activate " + (String)((PetClient)((PetManager)this.Plugin).Get(this.Player)).GetPets().get(pet.GetPetType()), new String[0], 1, false, false), new ActivatePetButton(pet, this));
        }
        

      }
      else if (this.DonationManager.Get(this.Player.getName()).GetBalance(CurrencyType.Gems) >= pet.GetCost(CurrencyType.Gems)) {
        AddButton(slot, new ShopItem(Material.MONSTER_EGG, (byte)pet.GetPetType().getTypeId(), "Purchase " + pet.GetPetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, false, false), new PetButton(pet, this));
      } else {
        setItem(slot, new ShopItem(Material.MONSTER_EGG, (byte)pet.GetPetType().getTypeId(), "Purchase " + pet.GetPetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, true, false));
      }
      
      slot++;
    }
    
    slot = 22;
    for (PetExtra petExtra : ((PetManager)this.Plugin).GetFactory().GetPetExtras())
    {
      List<String> itemLore = new ArrayList();
      
      if (!((PetManager)this.Plugin).hasActivePet(this.Player.getName()))
      {
        itemLore.add(C.cWhite + "You must have an active pet to use this!");
        getInventory().setItem(slot, new ShopItem(petExtra.GetMaterial(), (byte)0, C.cRed + petExtra.GetName(), (String[])itemLore.toArray(new String[itemLore.size()]), 1, true, false).getHandle());
      }
      else
      {
        AddButton(slot, new ShopItem(petExtra.GetMaterial(), (byte)0, "Rename " + ((PetManager)this.Plugin).getActivePet(this.Player.getName()).getCustomName() + " for " + C.cYellow + petExtra.GetCost(CurrencyType.Gems) + C.cGreen + " Gems", (String[])itemLore.toArray(new String[itemLore.size()]), 1, false, false), new RenamePetButton(this));
      }
      
      slot++;
    }
  }
  
  public void PurchasePet(Player player, Pet pet)
  {
    renamePet(player, pet, true);
  }
  
  public void renameCurrentPet(Player player)
  {
    PlayAcceptSound(player);
    Creature currentPet = ((PetManager)this.Plugin).getActivePet(player.getName());
    renamePet(player, new Pet(currentPet.getCustomName(), currentPet.getType(), 1), false);
  }
  
  public void renamePet(Player player, Pet pet, boolean petPurchase)
  {
    PlayAcceptSound(player);
    
    PetTagPage petTagPage = new PetTagPage((PetManager)this.Plugin, (PetShop)this.Shop, this.ClientManager, this.DonationManager, "Repairing", this.Player, pet, petPurchase);
    EntityPlayer entityPlayer = ((CraftPlayer)this.Player).getHandle();
    int containerCounter = entityPlayer.nextContainerCounter();
    entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerCounter, 8, "Repairing", 9, true));
    entityPlayer.activeContainer = new mineplex.core.shop.page.AnvilContainer(entityPlayer.inventory, petTagPage.getInventory());
    entityPlayer.activeContainer.windowId = containerCounter;
    entityPlayer.activeContainer.addSlotListener(entityPlayer);
    entityPlayer.playerConnection.sendPacket(new net.minecraft.server.v1_7_R3.PacketPlayOutSetSlot(containerCounter, 0, new net.minecraft.server.v1_7_R3.ItemStack(Items.NAME_TAG)));
    
    ((PetShop)this.Shop).SetCurrentPageForPlayer(this.Player, petTagPage);
  }
  
  public void ActivatePet(Player player, Pet pet)
  {
    PlayAcceptSound(player);
    ((PetManager)this.Plugin).AddPetOwner(player, pet.GetPetType(), player.getLocation());
    this.Player.closeInventory();
  }
  
  public void DeactivatePet(Player player)
  {
    PlayAcceptSound(player);
    ((PetManager)this.Plugin).RemovePet(player, true);
    this.Player.closeInventory();
  }
}
