package mineplex.core.punish.UI;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilTime;
import mineplex.core.punish.Category;
import mineplex.core.punish.Punish;
import mineplex.core.punish.PunishClient;
import mineplex.core.punish.Punishment;
import mineplex.core.punish.PunishmentResponse;
import mineplex.core.punish.PunishmentSorter;
import mineplex.core.shop.item.IButton;
import mineplex.core.shop.item.ShopItem;
import net.minecraft.server.v1_7_R3.IInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PunishPage extends CraftInventoryCustom implements Listener
{
  private Punish _plugin;
  private NautHashMap<Integer, IButton> _buttonMap;
  private Player _player;
  private String _target;
  private String _reason;
  private ShopItem _chatOffenseButton;
  private ShopItem _exploitingButton;
  private ShopItem _hackingButton;
  private ShopItem _warningButton;
  private ShopItem _permMuteButton;
  private ShopItem _permBanButton;
  
  public PunishPage(Punish plugin, Player player, String target, String reason)
  {
    super(null, 54, "            Punish");
    
    this._plugin = plugin;
    this._buttonMap = new NautHashMap();
    
    this._player = player;
    this._target = target;
    this._reason = reason;
    
    BuildPage();
    
    this._player.openInventory(this);
    this._plugin.RegisterEvents(this);
  }
  

  private void BuildPage()
  {
    getInventory().setItem(4, new ShopItem(Material.SKULL_ITEM, (byte)3, this._target, new String[] { ChatColor.RESET + this._reason }, 1, false, true).getHandle());
    
    PunishClient client = this._plugin.GetClient(this._target);
    
    int chatOffenseCount = 0;
    int exploitingCount = 0;
    int hackingCount = 0;
    
    List<Map.Entry<Category, Punishment>> punishments = new ArrayList();
    
    for (Category category : client.GetPunishments().keySet())
    {
      for (Punishment punishment : (List)client.GetPunishments().get(category))
      {
        punishments.add(new AbstractMap.SimpleEntry(category, punishment));
      }
      
      switch (category)
      {
      case Advertisement: 
        chatOffenseCount = ((List)client.GetPunishments().get(category)).size();
        break;
      case Exploiting: 
        exploitingCount = ((List)client.GetPunishments().get(category)).size();
        break;
      case Hacking: 
        hackingCount = ((List)client.GetPunishments().get(category)).size();
      }
      
    }
    


    String examplePrefix = ChatColor.RESET + ChatColor.GRAY;
    String examplePrefixEx = ChatColor.RESET + ChatColor.WHITE;
    String examplePrefixNote = ChatColor.RESET + ChatColor.DARK_GREEN;
    
    this._chatOffenseButton = new ShopItem(Material.BOOK_AND_QUILL, (byte)0, "Chat Offense", new String[] { ChatColor.RESET + "Past offenses : " + ChatColor.YELLOW + chatOffenseCount, examplePrefix + "Verbal Abuse, Spam, Harassment, Trolling, etc" }, 1, false, true);
    this._exploitingButton = new ShopItem(Material.HOPPER, (byte)0, "General Offense", new String[] { ChatColor.RESET + "Past offenses : " + ChatColor.YELLOW + exploitingCount, examplePrefix + "Commmand/Map/Class/Skill exploits, etc" }, 1, false, true);
    this._hackingButton = new ShopItem(Material.IRON_SWORD, (byte)0, "Client Mod", new String[] { ChatColor.RESET + "Past offenses : " + ChatColor.YELLOW + hackingCount, examplePrefix + "X-ray, Forcefield, Speed, Fly, Inventory Hacks, etc" }, 1, false, true);
    this._warningButton = new ShopItem(Material.PAPER, (byte)0, "Warning", new String[0], 1, false, true);
    this._permMuteButton = new ShopItem(Material.EMERALD_BLOCK, (byte)0, "Permanent Mute", new String[0], 1, false, true);
    this._permBanButton = new ShopItem(Material.REDSTONE_BLOCK, (byte)0, "Permanent Ban", new String[0], 1, false, true);
    
    getInventory().setItem(10, this._chatOffenseButton.getHandle());
    getInventory().setItem(12, this._exploitingButton.getHandle());
    getInventory().setItem(14, this._hackingButton.getHandle());
    


    AddButton(19, new ShopItem(Material.INK_SACK, (byte)2, "Severity 1", new String[] {
      ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "2 Hours", 
      " ", 
      examplePrefix + "Spamming same thing in chat (3-5 times)", 
      " ", 
      examplePrefix + "Light Advertising;", 
      examplePrefixEx + "   'anyone want to play on minecade?'", 
      " ", 
      examplePrefix + "Trolling", 
      " ", 
      examplePrefix + "Constantly just talking crap", 
      " ", 
      examplePrefix + "Pestering staff in admin chat", 
      " ", 
      examplePrefix + "Accusing a player of hacks in chat", 
      " ", 
      examplePrefixNote + "Use Severity 2 if many Severity 1 past offences" }, 
      1, false, true), new PunishButton(this, Category.ChatOffense, 1, false, 2L));
    
    if (this._plugin.GetClients().Get(this._player).GetRank().Has(Rank.MODERATOR))
    {
      AddButton(28, new ShopItem(Material.INK_SACK, (byte)11, "Severity 2", new String[] {
        ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "24 Hours", 
        " ", 
        examplePrefix + "Spamming same thing in chat (6-14 times)", 
        " ", 
        examplePrefix + "Medium Advertising;", 
        examplePrefixEx + "   'check out my server! crap.server.net'", 
        " ", 
        examplePrefix + "Rudeness, arguing or abuse between players;", 
        examplePrefixEx + "   'go fucking cry, you baby'.", 
        examplePrefixEx + "   'SHIT ADMINS ARE SHIT!!!'", 
        examplePrefixEx + "   'youre terrible, learn to play'", 
        " ", 
        examplePrefixNote + "Use Severity 3 if many Severity 2 past offences" }, 
        1, false, true), new PunishButton(this, Category.ChatOffense, 2, false, 24L));
      
      AddButton(37, new ShopItem(Material.INK_SACK, (byte)1, "Severity 3", new String[] {
        ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "Permanent", 
        " ", 
        examplePrefix + "Spamming same thing in chat (15+ times)", 
        " ", 
        examplePrefix + "Strong Advertising;", 
        examplePrefixEx + "   'JOIN MINECADE!! MINEPLEX SUCKS'", 
        " ", 
        examplePrefix + "Severe chat abuse towards players/staff", 
        examplePrefixEx + "   'go fucking die in a fire you fucking sack of shit'" }, 
        1, false, true), new PunishButton(this, Category.ChatOffense, 3, false, -1L));
    }
    



    AddButton(21, new ShopItem(Material.INK_SACK, (byte)2, "Severity 1", new String[] {
      ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "2 Hours", 
      " ", 
      examplePrefix + "Some Examples;", 
      examplePrefixEx + "   Trolling (Gameplay Only)", 
      examplePrefixEx + "   ", 
      " ", 
      examplePrefixNote + "Use Severity 2 if many Severity 1 past offences" }, 
      1, false, true), new PunishButton(this, Category.Exploiting, 1, true, 2L));
    
    if (this._plugin.GetClients().Get(this._player).GetRank().Has(Rank.MODERATOR))
    {

      AddButton(30, new ShopItem(Material.INK_SACK, (byte)11, "Severity 2", new String[] {
        ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "24", 
        " ", 
        examplePrefix + "Examples;", 
        examplePrefixEx + "   Team killing with water in Bridges", 
        examplePrefixEx + "   Wither Skeleton block glitching in SSM to hide", 
        " ", 
        examplePrefixNote + "Use Severity 3 if many Severity 2 past offences" }, 
        1, false, true), new PunishButton(this, Category.Exploiting, 2, true, 24L));
      
      AddButton(39, new ShopItem(Material.INK_SACK, (byte)1, "Severity 3", new String[] {
        ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "Permanent", 
        " ", 
        examplePrefix + "Examples;", 
        examplePrefixEx + "   Repeatedly crashing server with glitch", 
        " " }, 
        1, false, true), new PunishButton(this, Category.Exploiting, 3, true, -1L));
    }
    



    AddButton(23, new ShopItem(Material.INK_SACK, (byte)2, "Severity 1", new String[] {
      ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "12 Hours", 
      " ", 
      examplePrefix + "Damage Indicators, Better Sprint, Minimaps", 
      " ", 
      examplePrefixNote + "Use this for 1st Offence" }, 
      1, false, true), new PunishButton(this, Category.Hacking, 1, true, 12L));
    
    if (this._plugin.GetClients().Get(this._player).GetRank().Has(Rank.MODERATOR))
    {
      AddButton(32, new ShopItem(Material.INK_SACK, (byte)11, "Severity 2", new String[] {
        ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "1 Week", 
        " ", 
        examplePrefix + "Examples;", 
        examplePrefixEx + "   Damage Indicators", 
        examplePrefixEx + "   Better Sprint", 
        examplePrefixEx + "   Player Radar", 
        " ", 
        examplePrefixNote + "Use this for 2nd Offence", 
        " ", 
        examplePrefixNote + "Use Severity 3 for 3rd Offence" }, 
        1, false, true), new PunishButton(this, Category.Hacking, 2, true, 168L));
      
      AddButton(41, new ShopItem(Material.INK_SACK, (byte)1, "Severity 3", new String[] {
        ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "Permanent", 
        " ", 
        examplePrefix + "Examples;", 
        examplePrefixEx + "   Fly Hack", 
        examplePrefixEx + "   Speed Hack", 
        examplePrefixEx + "   Forcefield", 
        " ", 
        examplePrefixNote + "Must be 100% sure they were hacking!" }, 
        1, false, true), new PunishButton(this, Category.Hacking, 3, true, -1L));
    }
    


    AddButton(25, new ShopItem(Material.PAPER, (byte)0, "Warning", new String[] {
      " ", 
      examplePrefix + "Example Warning Input;", 
      examplePrefixEx + "   Spam - Repeatedly writing MEOW", 
      examplePrefixEx + "   Swearing - Saying 'fuck' and 'shit'", 
      examplePrefixEx + "   Hack Accusation - Accused Tomp13 of hacking", 
      examplePrefixEx + "   Trolling - was trying to make bob angry in chat" }, 
      
      1, false, true), new PunishButton(this, Category.Warning, 1, false, 0L));
    
    if (this._plugin.GetClients().Get(this._player).GetRank().Has(Rank.MODERATOR))
    {
      AddButton(34, new ShopItem(Material.REDSTONE_BLOCK, (byte)0, "Permanent Ban", new String[] {
        ChatColor.RESET + "Ban Duration: " + ChatColor.YELLOW + "Permanent", 
        " ", 
        examplePrefixNote + "Must supply detailed reason for Ban." }, 
        1, false, true), new PunishButton(this, Category.Other, 1, true, -1L));
      
      AddButton(43, new ShopItem(Material.EMERALD_BLOCK, (byte)0, "Permanent Mute", new String[] {
        ChatColor.RESET + "Mute Duration: " + ChatColor.YELLOW + "Permanent", 
        " ", 
        examplePrefixNote + "Must supply detailed reason for Mute." }, 
        1, false, true), new PunishButton(this, Category.Other, 1, false, -1L));
    }
    
    java.util.Collections.sort(punishments, new PunishmentSorter());
    
    int slot = 45;
    
    for (Map.Entry<Category, Punishment> punishmentEntry : punishments)
    {
      if (punishmentEntry.getKey() != Category.Advertisement)
      {

        if (slot >= 54) {
          break;
        }
        ShopItem button = null;
        
        switch ((Category)punishmentEntry.getKey())
        {
        case Advertisement: 
          button = this._chatOffenseButton.clone();
          break;
        case Exploiting: 
          button = this._exploitingButton.clone();
          break;
        case Hacking: 
          button = this._hackingButton.clone();
          break;
        case Other: 
          button = this._warningButton.clone();
          break;
        case PermMute: 
          button = this._permMuteButton.clone();
          break;
        case Warning: 
          button = this._permBanButton.clone();
          break;
        }
        
        

        Punishment punishment = (Punishment)punishmentEntry.getValue();
        
        if ((punishmentEntry.getKey() == Category.ChatOffense) || 
          (punishmentEntry.getKey() == Category.Exploiting) || 
          (punishmentEntry.getKey() == Category.Hacking))
        {
          if (punishment.GetRemoved())
          {
            button.SetLore(
              new String[] {
              ChatColor.RESET + "Punishment Type: " + ChatColor.YELLOW + punishment.GetCategory().toString(), 
              ChatColor.RESET + "Severity: " + ChatColor.YELLOW + punishment.GetSeverity(), 
              " ", 
              ChatColor.RESET + "Reason: " + ChatColor.YELLOW + punishment.GetReason(), 
              " ", 
              ChatColor.RESET + "Admin: " + ChatColor.YELLOW + punishment.GetAdmin(), 
              ChatColor.RESET + "Date: " + ChatColor.YELLOW + UtilTime.when(punishment.GetTime()), 
              " ", 
              ChatColor.RESET + "Removed by: " + (punishment.GetRemoved() ? ChatColor.GREEN + punishment.GetRemoveAdmin() : new StringBuilder().append(ChatColor.RED).append("Not Removed").toString()), 
              ChatColor.RESET + "Remove Reason: " + (punishment.GetRemoved() ? ChatColor.GREEN + punishment.GetRemoveReason() : new StringBuilder().append(ChatColor.RED).append("Not Removed").toString()) });

          }
          else
          {

            button.SetLore(
              new String[] {
              ChatColor.RESET + "Punishment Type: " + ChatColor.YELLOW + punishment.GetCategory().toString(), 
              ChatColor.RESET + "Severity: " + ChatColor.YELLOW + punishment.GetSeverity(), 
              " ", 
              ChatColor.RESET + "Reason: " + ChatColor.YELLOW + punishment.GetReason(), 
              " ", 
              ChatColor.RESET + "Admin: " + ChatColor.YELLOW + punishment.GetAdmin(), 
              ChatColor.RESET + "Date: " + ChatColor.YELLOW + UtilTime.when(punishment.GetTime()) });

          }
          

        }
        else if (punishment.GetRemoved())
        {
          button.SetLore(
            new String[] {
            ChatColor.RESET + "Punishment Type: " + ChatColor.YELLOW + punishment.GetCategory().toString(), 
            " ", 
            ChatColor.RESET + "Reason: " + ChatColor.YELLOW + punishment.GetReason(), 
            " ", 
            ChatColor.RESET + "Admin: " + ChatColor.YELLOW + punishment.GetAdmin(), 
            ChatColor.RESET + "Date: " + ChatColor.YELLOW + UtilTime.when(punishment.GetTime()), 
            " ", 
            ChatColor.RESET + "Removed by: " + (punishment.GetRemoved() ? ChatColor.GREEN + punishment.GetRemoveAdmin() : new StringBuilder().append(ChatColor.RED).append("Not Removed").toString()), 
            ChatColor.RESET + "Remove Reason: " + (punishment.GetRemoved() ? ChatColor.GREEN + punishment.GetRemoveReason() : new StringBuilder().append(ChatColor.RED).append("Not Removed").toString()) });

        }
        else
        {
          button.SetLore(
            new String[] {
            ChatColor.RESET + "Punishment Type: " + ChatColor.YELLOW + punishment.GetCategory().toString(), 
            " ", 
            ChatColor.RESET + "Reason: " + ChatColor.YELLOW + punishment.GetReason(), 
            " ", 
            ChatColor.RESET + "Admin: " + ChatColor.YELLOW + punishment.GetAdmin(), 
            ChatColor.RESET + "Date: " + ChatColor.YELLOW + UtilTime.when(punishment.GetTime()) });
        }
        



        if (((punishment.GetHours() == -1.0D) || (punishment.GetRemaining() > 0L)) && (!punishment.GetRemoved()) && (punishment.GetActive()))
        {
          button.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
          AddButton(slot, button, new RemovePunishmentButton(this, punishment, button));
        }
        else
        {
          getInventory().setItem(slot, button.getHandle());
        }
        
        slot++;
      }
    }
  }
  
  @EventHandler
  public void OnInventoryClick(InventoryClickEvent event) {
    if ((this.inventory.getInventoryName().equalsIgnoreCase(event.getInventory().getTitle())) && (event.getWhoClicked() == this._player))
    {
      if (this._buttonMap.containsKey(Integer.valueOf(event.getRawSlot())))
      {
        if ((event.getWhoClicked() instanceof Player))
        {
          if (event.isLeftClick()) {
            ((IButton)this._buttonMap.get(Integer.valueOf(event.getRawSlot()))).ClickedLeft((Player)event.getWhoClicked());
          } else if (event.isRightClick()) {
            ((IButton)this._buttonMap.get(Integer.valueOf(event.getRawSlot()))).ClickedRight((Player)event.getWhoClicked());
          }
        }
      }
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void OnInventoryClose(InventoryCloseEvent event)
  {
    if ((this.inventory.getInventoryName().equalsIgnoreCase(event.getInventory().getTitle())) && (event.getPlayer() == this._player))
    {
      ClosePunish();
    }
  }
  
  private void AddButton(int slot, ShopItem item, IButton button)
  {
    getInventory().setItem(slot, item.getHandle());
    this._buttonMap.put(Integer.valueOf(slot), button);
  }
  
  public void AddInfraction(Category category, int severity, boolean ban, long punishTime)
  {
    this._plugin.AddPunishment(this._target, category, this._reason, this._player, severity, ban, punishTime);
    this._player.closeInventory();
    ClosePunish();
  }
  
  private void ClosePunish()
  {
    HandlerList.unregisterAll(this);
  }
  
  public void RemovePunishment(final Punishment punishment, ItemStack item)
  {
    this._plugin.RemovePunishment(punishment.GetPunishmentId(), this._target, this._player, this._reason, new Callback()
    {

      public void run(String result)
      {
        PunishmentResponse punishResponse = PunishmentResponse.valueOf(result);
        
        if (punishResponse != PunishmentResponse.PunishmentRemoved)
        {
          PunishPage.this._player.sendMessage(F.main(PunishPage.this._plugin.GetName(), "There was a problem removing the punishment."));
        }
        else
        {
          punishment.Remove(PunishPage.this._player.getName(), PunishPage.this._reason);
          PunishPage.this._player.closeInventory();
          PunishPage.this.ClosePunish();
        }
      }
    });
  }
}
