package mineplex.minecraft.game.classcombat.Class;

import java.util.Collection;
import java.util.HashMap;
import mineplex.core.MiniClientPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.account.event.ClientWebResponseEvent;
import mineplex.core.donation.DonationManager;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.repository.ClassRepository;
import mineplex.minecraft.game.classcombat.Class.repository.token.ClientClassTokenWrapper;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;



public class ClassManager
  extends MiniClientPlugin<ClientClass>
  implements IClassFactory
{
  private CoreClientManager _clientManager;
  private DonationManager _donationManager;
  private SkillFactory _skillFactory;
  private ClassRepository _repository;
  private HashMap<String, IPvpClass> _classes;
  private HashMap<Integer, IPvpClass> _classSalesPackageIdMap;
  private Object _clientLock = new Object();
  

  public ClassManager(JavaPlugin plugin, CoreClientManager clientManager, DonationManager donationManager, SkillFactory skillFactory, String webAddress)
  {
    super("Class Manager", plugin);
    
    this._plugin = plugin;
    this._clientManager = clientManager;
    this._donationManager = donationManager;
    this._skillFactory = skillFactory;
    this._repository = new ClassRepository(webAddress);
    this._classes = new HashMap();
    this._classSalesPackageIdMap = new HashMap();
    
    PopulateClasses();
  }
  
  @EventHandler
  public void OnClientWebResponse(ClientWebResponseEvent event)
  {
    ClientClassTokenWrapper token = (ClientClassTokenWrapper)new Gson().fromJson(event.GetResponse(), ClientClassTokenWrapper.class);
    LoadClassBuilds(token);
  }
  
  private void LoadClassBuilds(ClientClassTokenWrapper token)
  {
    synchronized (this._clientLock)
    {
      Set(token.Name, 
        new ClientClass(this, this._skillFactory, this._clientManager.Get(token.Name), this._donationManager
        .Get(token.Name), token.DonorToken));
    }
  }
  
  public ClientClass Get(String name)
  {
    synchronized (this._clientLock)
    {
      return (ClientClass)super.Get(name);
    }
  }
  
  private void PopulateClasses()
  {
    this._classes.clear();
    
    AddKnight();
    AddRanger();
    AddBrute();
    AddMage();
    AddAssassin();
    








    for (IPvpClass pvpClass : GetAllClasses())
    {
      CustomBuildToken customBuild = pvpClass.getDefaultBuild();
      ISkill swordSkill = this._skillFactory.GetSkill(customBuild.SwordSkill);
      ISkill axeSkill = this._skillFactory.GetSkill(customBuild.AxeSkill);
      ISkill bowSkill = this._skillFactory.GetSkill(customBuild.BowSkill);
      ISkill classPassiveASkill = this._skillFactory.GetSkill(customBuild.ClassPassiveASkill);
      ISkill classPassiveBSkill = this._skillFactory.GetSkill(customBuild.ClassPassiveBSkill);
      ISkill globalPassive = this._skillFactory.GetSkill(customBuild.GlobalPassiveSkill);
      
      if (swordSkill != null) {
        swordSkill.setFree(true);
      }
      if (axeSkill != null) {
        axeSkill.setFree(true);
      }
      if (bowSkill != null) {
        bowSkill.setFree(true);
      }
      if (classPassiveASkill != null) {
        classPassiveASkill.setFree(true);
      }
      if (classPassiveBSkill != null) {
        classPassiveBSkill.setFree(true);
      }
      if (globalPassive != null) {
        globalPassive.setFree(true);
      }
    }
  }
  
  private void AddAssassin() {
    CustomBuildToken customBuild = new CustomBuildToken(IPvpClass.ClassType.Assassin);
    customBuild.Name = "Default Build";
    
    customBuild.SwordSkill = "Evade";
    customBuild.SwordSkillLevel = Integer.valueOf(2);
    customBuild.AxeSkill = "Leap";
    customBuild.AxeSkillLevel = Integer.valueOf(3);
    customBuild.BowSkill = "Smoke Arrow";
    customBuild.BowSkillLevel = Integer.valueOf(2);
    customBuild.ClassPassiveASkill = "Smoke Bomb";
    customBuild.ClassPassiveASkillLevel = Integer.valueOf(2);
    customBuild.ClassPassiveBSkill = "Combo Attack";
    customBuild.ClassPassiveBSkillLevel = Integer.valueOf(2);
    customBuild.GlobalPassiveSkill = "Break Fall";
    customBuild.GlobalPassiveSkillLevel = Integer.valueOf(1);
    
    AddClass(new PvpClass(this, 5, IPvpClass.ClassType.Assassin, customBuild, new String[] { "Extremely nimble and smart.", 
      "Excels at ambushing and takedowns.", "", "Permanent Speed II" }, Material.LEATHER_HELMET, 
      Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS, null));
  }
  
  private void AddMage()
  {
    CustomBuildToken customBuild = new CustomBuildToken(IPvpClass.ClassType.Mage);
    customBuild.Name = "Default Build";
    
    customBuild.SwordSkill = "Blizzard";
    customBuild.SwordSkillLevel = Integer.valueOf(3);
    customBuild.AxeSkill = "Ice Prison";
    customBuild.AxeSkillLevel = Integer.valueOf(3);
    customBuild.BowSkill = "";
    customBuild.ClassPassiveASkill = "Arctic Armor";
    customBuild.ClassPassiveASkillLevel = Integer.valueOf(2);
    customBuild.ClassPassiveBSkill = "Glacial Blade";
    customBuild.ClassPassiveBSkillLevel = Integer.valueOf(2);
    customBuild.GlobalPassiveSkill = "Mana Pool";
    customBuild.GlobalPassiveSkillLevel = Integer.valueOf(2);
    
    AddClass(new PvpClass(this, 4, IPvpClass.ClassType.Mage, customBuild, new String[] { "Trained in the ancient arts.", 
      "Able to adapt to many roles in combat." }, Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, 
      Material.GOLD_LEGGINGS, Material.GOLD_BOOTS, null));
  }
  
  private void AddBrute()
  {
    CustomBuildToken customBuild = new CustomBuildToken(IPvpClass.ClassType.Brute);
    customBuild.Name = "Default Build";
    
    customBuild.SwordSkill = "Block Toss";
    customBuild.SwordSkillLevel = Integer.valueOf(4);
    customBuild.AxeSkill = "Seismic Slam";
    customBuild.AxeSkillLevel = Integer.valueOf(3);
    customBuild.BowSkill = "";
    customBuild.BowSkillLevel = Integer.valueOf(0);
    customBuild.ClassPassiveASkill = "Stampede";
    customBuild.ClassPassiveASkillLevel = Integer.valueOf(2);
    customBuild.ClassPassiveBSkill = "Crippling Blow";
    customBuild.ClassPassiveBSkillLevel = Integer.valueOf(2);
    customBuild.GlobalPassiveSkill = "Recharge";
    customBuild.GlobalPassiveSkillLevel = Integer.valueOf(1);
    
    AddClass(new PvpClass(this, 3, IPvpClass.ClassType.Brute, customBuild, new String[] { "Uses pure strength to dominate.", 
      "Great at crowd control." }, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, 
      Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, null));
  }
  
  private void AddRanger()
  {
    CustomBuildToken customBuild = new CustomBuildToken(IPvpClass.ClassType.Ranger);
    customBuild.Name = "Default Build";
    
    customBuild.SwordSkill = "Disengage";
    customBuild.SwordSkillLevel = Integer.valueOf(3);
    customBuild.AxeSkill = "Agility";
    customBuild.AxeSkillLevel = Integer.valueOf(2);
    customBuild.BowSkill = "Napalm Shot";
    customBuild.BowSkillLevel = Integer.valueOf(3);
    customBuild.ClassPassiveASkill = "Barrage";
    customBuild.ClassPassiveASkillLevel = Integer.valueOf(2);
    customBuild.ClassPassiveBSkill = "Barbed Arrows";
    customBuild.ClassPassiveBSkillLevel = Integer.valueOf(1);
    customBuild.GlobalPassiveSkill = "Resistance";
    customBuild.GlobalPassiveSkillLevel = Integer.valueOf(1);
    
    AddClass(new PvpClass(this, -1, IPvpClass.ClassType.Ranger, customBuild, new String[] { "Mastery with a Bow and Arrow.", 
      "Adept in Wilderness Survival" }, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, 
      Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS, null));
  }
  
  private void AddKnight()
  {
    CustomBuildToken customBuild = new CustomBuildToken(IPvpClass.ClassType.Knight);
    customBuild.Name = "Default Build";
    
    customBuild.SwordSkill = "Hilt Smash";
    customBuild.SwordSkillLevel = Integer.valueOf(3);
    customBuild.AxeSkill = "Bulls Charge";
    customBuild.AxeSkillLevel = Integer.valueOf(3);
    customBuild.BowSkill = "";
    customBuild.ClassPassiveASkill = "Swordsmanship";
    customBuild.ClassPassiveASkillLevel = Integer.valueOf(2);
    customBuild.ClassPassiveBSkill = "Vengeance";
    customBuild.ClassPassiveBSkillLevel = Integer.valueOf(2);
    customBuild.GlobalPassiveSkill = "Resistance";
    customBuild.GlobalPassiveSkillLevel = Integer.valueOf(2);
    
    AddClass(new PvpClass(this, -1, IPvpClass.ClassType.Knight, customBuild, new String[] {
      "Trained in the arts of melee combat.", "Able to stand his ground against foes." }, 
      Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS, null));
  }
  
  public IPvpClass GetClass(String className)
  {
    return (IPvpClass)this._classes.get(className);
  }
  
  public IPvpClass GetClass(int id)
  {
    return (IPvpClass)this._classSalesPackageIdMap.get(Integer.valueOf(id));
  }
  
  public Collection<IPvpClass> GetAllClasses()
  {
    return this._classes.values();
  }
  
  public void AddClass(PvpClass newClass)
  {
    this._classes.put(newClass.GetName(), newClass);
  }
  

  public Collection<IPvpClass> GetGameClasses()
  {
    return this._classes.values();
  }
  
  @EventHandler
  public void update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (IPvpClass cur : this._classes.values()) {
      cur.checkEquip();
    }
  }
  
  public SkillFactory GetSkillFactory() {
    return this._skillFactory;
  }
  

  protected ClientClass AddPlayer(String player)
  {
    return new ClientClass(this, this._skillFactory, this._clientManager.Get(player), this._donationManager.Get(player), null);
  }
  
  public ClassRepository GetRepository()
  {
    return this._repository;
  }
  
  @EventHandler
  public void SkillDisplay(PlayerCommandPreprocessEvent event)
  {
    if (event.getMessage().equals("/skill"))
    {
      ClientClass client = Get(event.getPlayer().getName());
      
      if (client == null) {
        event.getPlayer().sendMessage("You do not have a ClientClass.");
      }
      else {
        client.DisplaySkills(event.getPlayer());
      }
      
      event.setCancelled(true);
    }
  }
}
