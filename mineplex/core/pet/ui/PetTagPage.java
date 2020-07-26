package mineplex.core.pet.ui;

import mineplex.core.account.CoreClientManager;
import mineplex.core.common.CurrencyType;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.donation.DonationManager;
import mineplex.core.pet.Pet;
import mineplex.core.pet.PetClient;
import mineplex.core.pet.PetExtra;
import mineplex.core.pet.PetManager;
import mineplex.core.pet.PetShop;
import mineplex.core.pet.repository.PetRepository;
import mineplex.core.pet.repository.token.PetChangeToken;
import mineplex.core.pet.repository.token.PetToken;
import mineplex.core.shop.page.ConfirmationPage;
import mineplex.core.shop.page.ShopPageBase;
import net.minecraft.server.v1_7_R3.IInventory;
import net.minecraft.server.v1_7_R3.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PetTagPage extends ShopPageBase<PetManager, PetShop>
{
  private String _tagName = "Pet Tag";
  private Pet _pet;
  private boolean _petPurchase;
  
  public PetTagPage(PetManager plugin, PetShop shop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player, Pet pet, boolean petPurchase)
  {
    super(plugin, shop, clientManager, donationManager, name, player, 3);
    
    this._pet = pet;
    this._petPurchase = petPurchase;
    
    BuildPage();
    
    this.Player.setLevel(5);
  }
  

  protected void BuildPage()
  {
    this.inventory.setItem(0, new ItemStack(net.minecraft.server.v1_7_R3.Items.NAME_TAG));
    
    this.ButtonMap.put(Integer.valueOf(0), new CloseButton());
    this.ButtonMap.put(Integer.valueOf(1), new CloseButton());
    this.ButtonMap.put(Integer.valueOf(2), new SelectTagButton(this));
  }
  

  public void PlayerClosed()
  {
    super.PlayerClosed();
    
    this.Player.setLevel(0);
  }
  
  public void SelectTag()
  {
    if (ChatColor.stripColor(this._tagName).length() > 16)
    {
      UtilPlayer.message(this.Player, mineplex.core.common.util.F.main(((PetManager)this.Plugin).GetName(), ChatColor.RED + "Pet name cannot be longer than 16 characters."));
      PlayDenySound(this.Player);
      
      return;
    }
    
    PetExtra tag = new PetExtra("Rename " + this._pet.GetName() + " to " + this._tagName, Material.NAME_TAG, 1000);
    
    this._pet.setDisplayName(mineplex.core.common.util.C.cGreen + "Purchase " + this._tagName);
    
    ((PetShop)this.Shop).OpenPageForPlayer(this.Player, new ConfirmationPage((PetManager)this.Plugin, (PetShop)this.Shop, this.ClientManager, this.DonationManager, new Runnable()
    {
      public void run()
      {
        PetChangeToken token = new PetChangeToken();
        token.Name = PetTagPage.this.Player.getName();
        token.PetType = PetTagPage.this._pet.GetPetType().toString();
        token.PetName = PetTagPage.this._tagName;
        
        PetToken petToken = new PetToken();
        petToken.PetType = token.PetType;
        
        if (PetTagPage.this._petPurchase)
        {
          ((PetManager)PetTagPage.this.Plugin).GetRepository().AddPet(token);
          ((PetManager)PetTagPage.this.Plugin).addPetOwnerToQueue(PetTagPage.this.Player.getName(), PetTagPage.this._pet.GetPetType());
        }
        else
        {
          ((PetManager)PetTagPage.this.Plugin).GetRepository().UpdatePet(token);
          ((PetManager)PetTagPage.this.Plugin).addRenamePetToQueue(PetTagPage.this.Player.getName(), token.PetName);
        }
        
        ((PetClient)((PetManager)PetTagPage.this.Plugin).Get(PetTagPage.this.Player)).GetPets().put(PetTagPage.this._pet.GetPetType(), token.PetName);
        
        PetTagPage.this.Player.closeInventory();
      }
    }, null, this._petPurchase ? this._pet : tag, CurrencyType.Gems, this.Player));
  }
  
  public void SetTagName(String tagName)
  {
    this._tagName = tagName;
  }
}
