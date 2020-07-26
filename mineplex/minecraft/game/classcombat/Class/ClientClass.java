package mineplex.minecraft.game.classcombat.Class;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.donation.Donor;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.classcombat.Class.repository.ClassRepository;
import mineplex.minecraft.game.classcombat.Class.repository.token.ClientClassToken;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.Class.repository.token.SlotToken;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ClientClass
{
  private ClassManager _classFactory;
  private SkillFactory _skillFactory;
  private CoreClient _client;
  private Donor _donor;
  private IPvpClass _gameClass;
  private NautHashMap<ISkill.SkillType, ISkill> _skillMap = new NautHashMap();
  
  private IPvpClass _lastClass;
  private NautHashMap<Integer, ItemStack> _lastItems = new NautHashMap();
  private ItemStack[] _lastArmor = new ItemStack[4];
  private NautHashMap<ISkill.SkillType, Map.Entry<ISkill, Integer>> _lastSkillMap = new NautHashMap();
  
  private NautHashMap<IPvpClass, HashMap<Integer, CustomBuildToken>> _customBuilds;
  
  private NautHashMap<IPvpClass, CustomBuildToken> _activeCustomBuilds;
  private CustomBuildToken _savingCustomBuild;
  
  public ClientClass(ClassManager classFactory, SkillFactory skillFactory, CoreClient client, Donor donor, ClientClassToken token)
  {
    this._classFactory = classFactory;
    this._skillFactory = skillFactory;
    this._client = client;
    this._donor = donor;
    
    Load(token);
  }
  
  public void Load(ClientClassToken token)
  {
    this._customBuilds = new NautHashMap();
    this._activeCustomBuilds = new NautHashMap();
    
    for (IPvpClass pvpClass : this._classFactory.GetAllClasses())
    {
      this._customBuilds.put(pvpClass, new HashMap());
      ((HashMap)this._customBuilds.get(pvpClass)).put(Integer.valueOf(0), pvpClass.getDefaultBuild());
    }
    
    if (token == null) {
      return;
    }
    for (CustomBuildToken buildToken : token.CustomBuilds)
    {
      if (buildToken.CustomBuildNumber.intValue() != 0)
      {

        IPvpClass pvpClass = this._classFactory.GetClass(buildToken.PvpClass);
        
        ISkill swordSkill = this._skillFactory.GetSkill(buildToken.SwordSkill);
        ISkill axeSkill = this._skillFactory.GetSkill(buildToken.AxeSkill);
        ISkill bowSkill = this._skillFactory.GetSkill(buildToken.BowSkill);
        ISkill classPassiveASkill = this._skillFactory.GetSkill(buildToken.ClassPassiveASkill);
        ISkill classPassiveBSkill = this._skillFactory.GetSkill(buildToken.ClassPassiveBSkill);
        ISkill globalPassive = this._skillFactory.GetSkill(buildToken.GlobalPassiveSkill);
        
        if ((buildToken.SwordSkill.isEmpty()) || (ValidSkill(buildToken.SwordSkill, swordSkill, ISkill.SkillType.Sword)))
        {

          if ((buildToken.AxeSkill.isEmpty()) || (ValidSkill(buildToken.AxeSkill, axeSkill, ISkill.SkillType.Axe)))
          {

            if ((buildToken.BowSkill.isEmpty()) || (ValidSkill(buildToken.BowSkill, bowSkill, ISkill.SkillType.Bow)))
            {

              if ((buildToken.ClassPassiveASkill.isEmpty()) || (ValidSkill(buildToken.ClassPassiveASkill, classPassiveASkill, ISkill.SkillType.PassiveA)))
              {

                if ((buildToken.ClassPassiveBSkill.isEmpty()) || (ValidSkill(buildToken.ClassPassiveBSkill, classPassiveBSkill, ISkill.SkillType.PassiveB)))
                {

                  if ((buildToken.GlobalPassiveSkill.isEmpty()) || (ValidSkill(buildToken.GlobalPassiveSkill, globalPassive, ISkill.SkillType.GlobalPassive)))
                  {

                    boolean allEmpty = true;
                    
                    for (SlotToken slotToken : buildToken.Slots)
                    {
                      if (slotToken != null)
                      {

                        if (slotToken.Material != null)
                        {

                          if (!slotToken.Material.isEmpty())
                          {

                            allEmpty = false;
                            break;
                          } } }
                    }
                    if (allEmpty)
                    {
                      buildToken.SkillTokens = CustomBuildToken.MAX_SKILL_TOKENS;
                      buildToken.ItemTokens = CustomBuildToken.MAX_ITEM_TOKENS;
                      
                      if ((!buildToken.SwordSkill.isEmpty()) && (!ValidSkill(buildToken.SwordSkill, swordSkill, ISkill.SkillType.Sword))) {
                        buildToken.SkillTokens -= swordSkill.GetTokenCost();
                      }
                      if ((!buildToken.AxeSkill.isEmpty()) && (!ValidSkill(buildToken.AxeSkill, axeSkill, ISkill.SkillType.Axe))) {
                        buildToken.SkillTokens -= axeSkill.GetTokenCost();
                      }
                      if ((!buildToken.BowSkill.isEmpty()) && (!ValidSkill(buildToken.BowSkill, bowSkill, ISkill.SkillType.Bow))) {
                        buildToken.SkillTokens -= bowSkill.GetTokenCost();
                      }
                      if ((!buildToken.ClassPassiveASkill.isEmpty()) && (!ValidSkill(buildToken.ClassPassiveASkill, classPassiveASkill, ISkill.SkillType.PassiveA))) {
                        buildToken.SkillTokens -= classPassiveASkill.GetTokenCost();
                      }
                      if ((!buildToken.ClassPassiveBSkill.isEmpty()) && (!ValidSkill(buildToken.ClassPassiveBSkill, classPassiveBSkill, ISkill.SkillType.PassiveB))) {
                        buildToken.SkillTokens -= classPassiveBSkill.GetTokenCost();
                      }
                      if ((!buildToken.GlobalPassiveSkill.isEmpty()) && (!ValidSkill(buildToken.GlobalPassiveSkill, globalPassive, ISkill.SkillType.GlobalPassive))) {
                        buildToken.SkillTokens -= globalPassive.GetTokenCost();
                      }
                    }
                    ((HashMap)this._customBuilds.get(pvpClass)).put(buildToken.CustomBuildNumber, buildToken);
                  } } } } } }
      } }
  }
  
  public NautHashMap<Integer, ItemStack> GetDefaultItems() {
    return this._lastItems;
  }
  
  public void SetDefaultHead(ItemStack armor)
  {
    this._lastArmor[3] = armor;
  }
  
  public void SetDefaultChest(ItemStack armor)
  {
    this._lastArmor[2] = armor;
  }
  
  public void SetDefaultLegs(ItemStack armor)
  {
    this._lastArmor[1] = armor;
  }
  
  public void SetDefaultFeet(ItemStack armor)
  {
    this._lastArmor[0] = armor;
  }
  
  public void SaveActiveCustomBuild()
  {
    this._savingCustomBuild.PlayerName = this._client.GetPlayerName();
    
    this._classFactory.GetRepository().SaveCustomBuild(this._savingCustomBuild);
    this._savingCustomBuild = null;
  }
  
  public void SetSavingCustomBuild(IPvpClass pvpClass, CustomBuildToken customBuild)
  {
    this._savingCustomBuild = customBuild;
    this._savingCustomBuild.PvpClass = pvpClass.GetName();
    
    ((HashMap)this._customBuilds.get(pvpClass)).put(this._savingCustomBuild.CustomBuildNumber, this._savingCustomBuild);
  }
  
  public void SetActiveCustomBuild(IPvpClass pvpClass, CustomBuildToken customBuild)
  {
    customBuild.Active = true;
    this._activeCustomBuilds.put(pvpClass, customBuild);
  }
  
  public CustomBuildToken GetActiveCustomBuild(IPvpClass pvpClass)
  {
    return (CustomBuildToken)this._activeCustomBuilds.get(pvpClass);
  }
  
  public CustomBuildToken GetSavingCustomBuild()
  {
    return this._savingCustomBuild;
  }
  
  public boolean IsSavingCustomBuild()
  {
    return this._savingCustomBuild != null;
  }
  
  public HashMap<Integer, CustomBuildToken> GetCustomBuilds(IPvpClass pvpClass)
  {
    return (HashMap)this._customBuilds.get(pvpClass);
  }
  
  public void EquipCustomBuild(CustomBuildToken customBuild)
  {
    EquipCustomBuild(customBuild, true);
  }
  
  public void EquipCustomBuild(CustomBuildToken customBuild, boolean notify)
  {
    this._lastClass = this._classFactory.GetClass(customBuild.PvpClass);
    
    if (this._lastClass == null) {
      return;
    }
    this._lastSkillMap.remove(ISkill.SkillType.Class);
    
    SetDefaultHead(ItemStackFactory.Instance.CreateStack(this._lastClass.GetHead()));
    SetDefaultChest(ItemStackFactory.Instance.CreateStack(this._lastClass.GetChestplate()));
    SetDefaultLegs(ItemStackFactory.Instance.CreateStack(this._lastClass.GetLeggings()));
    SetDefaultFeet(ItemStackFactory.Instance.CreateStack(this._lastClass.GetBoots()));
    
    if (!customBuild.SwordSkill.isEmpty()) {
      this._lastSkillMap.put(ISkill.SkillType.Sword, new AbstractMap.SimpleEntry(this._skillFactory.GetSkill(customBuild.SwordSkill), customBuild.SwordSkillLevel));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.Sword);
    }
    if (!customBuild.AxeSkill.isEmpty()) {
      this._lastSkillMap.put(ISkill.SkillType.Axe, new AbstractMap.SimpleEntry(this._skillFactory.GetSkill(customBuild.AxeSkill), customBuild.AxeSkillLevel));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.Axe);
    }
    if (!customBuild.BowSkill.isEmpty()) {
      this._lastSkillMap.put(ISkill.SkillType.Bow, new AbstractMap.SimpleEntry(this._skillFactory.GetSkill(customBuild.BowSkill), customBuild.BowSkillLevel));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.Bow);
    }
    if (!customBuild.ClassPassiveASkill.isEmpty()) {
      this._lastSkillMap.put(ISkill.SkillType.PassiveA, new AbstractMap.SimpleEntry(this._skillFactory.GetSkill(customBuild.ClassPassiveASkill), customBuild.ClassPassiveASkillLevel));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.PassiveA);
    }
    if (!customBuild.ClassPassiveBSkill.isEmpty()) {
      this._lastSkillMap.put(ISkill.SkillType.PassiveB, new AbstractMap.SimpleEntry(this._skillFactory.GetSkill(customBuild.ClassPassiveBSkill), customBuild.ClassPassiveBSkillLevel));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.PassiveB);
    }
    if (!customBuild.GlobalPassiveSkill.isEmpty()) {
      this._lastSkillMap.put(ISkill.SkillType.GlobalPassive, new AbstractMap.SimpleEntry(this._skillFactory.GetSkill(customBuild.GlobalPassiveSkill), customBuild.GlobalPassiveSkillLevel));
    } else {
      this._lastSkillMap.remove(ISkill.SkillType.GlobalPassive);
    }
    for (int i = 0; i < 9; i++)
    {
      SlotToken token = (SlotToken)customBuild.Slots.get(i);
      
      if ((token == null) || (token.Material == null) || (token.Material.isEmpty()))
      {
        this._lastItems.put(Integer.valueOf(i), null);
      }
      else
      {
        ItemStack itemStack = ItemStackFactory.Instance.CreateStack((Material)Enum.valueOf(Material.class, token.Material), (byte)0, token.Amount, token.Name);
        
        if (token.Name.contains("Booster")) {
          itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        }
        this._lastItems.put(Integer.valueOf(i), itemStack);
      }
    }
    ResetToDefaults(true, true);
    
    if (notify)
    {
      ListSkills(this._client.GetPlayer());
      this._client.GetPlayer().getWorld().playSound(this._client.GetPlayer().getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
      
      this._client.GetPlayer().sendMessage(F.main("Class", "You equipped " + F.skill(customBuild.Name) + "."));
    }
  }
  
  public void ListSkills(Player caller)
  {
    UtilPlayer.message(caller, F.main("Skill", "Listing Class Skills:"));
    
    for (ISkill.SkillType type : ISkill.SkillType.values()) {
      if (((caller.isOp()) || (type != ISkill.SkillType.Class)) && 
        (this._skillMap.containsKey(type)))
        UtilPlayer.message(caller, F.desc(type.toString(), ((ISkill)this._skillMap.get(type)).GetName()));
    }
  }
  
  public void ResetSkills(Player player) {
    for (ISkill skill : GetSkills())
    {
      skill.Reset(player);
    }
  }
  
  public void ResetItems()
  {
    this._client.GetPlayer().getInventory().clear();
    
    for (Map.Entry<Integer, ItemStack> defaultItem : GetDefaultItems().entrySet())
    {
      this._client.GetPlayer().getInventory().setItem(((Integer)defaultItem.getKey()).intValue(), (ItemStack)defaultItem.getValue());
    }
  }
  
  public void ResetToDefaults(boolean equipItems, boolean equipDefaultArmor)
  {
    if (this._lastClass == null)
    {
      this._lastClass = this._classFactory.GetClass("Knight");
      
      this._lastArmor[3] = ItemStackFactory.Instance.CreateStack(this._lastClass.GetHead());
      this._lastArmor[2] = ItemStackFactory.Instance.CreateStack(this._lastClass.GetChestplate());
      this._lastArmor[1] = ItemStackFactory.Instance.CreateStack(this._lastClass.GetLeggings());
      this._lastArmor[0] = ItemStackFactory.Instance.CreateStack(this._lastClass.GetBoots());
      
      EquipCustomBuild((CustomBuildToken)((HashMap)this._customBuilds.get(this._lastClass)).get(Integer.valueOf(0)));
    }
    
    SetGameClass(this._lastClass);
    
    if (equipDefaultArmor)
    {
      if (this._lastArmor[3] != null) {
        this._client.GetPlayer().getInventory().setHelmet(this._lastArmor[3].clone());
      }
      if (this._lastArmor[2] != null) {
        this._client.GetPlayer().getInventory().setChestplate(this._lastArmor[2].clone());
      }
      if (this._lastArmor[1] != null) {
        this._client.GetPlayer().getInventory().setLeggings(this._lastArmor[1].clone());
      }
      if (this._lastArmor[0] != null) {
        this._client.GetPlayer().getInventory().setBoots(this._lastArmor[0].clone());
      }
    }
    if (equipItems)
    {
      ResetItems();
    }
    
    ClearSkills();
    
    if (this._skillFactory.GetSkill(this._gameClass.GetName() + " Class") != null)
    {
      AddSkill(this._skillFactory.GetSkill(this._gameClass.GetName() + " Class"), 1);
    }
    
    for (Map.Entry<ISkill, Integer> skill : this._lastSkillMap.values())
    {
      AddSkill((ISkill)skill.getKey(), ((Integer)skill.getValue()).intValue());
    }
  }
  
  public void ClearSkills()
  {
    if (this._skillMap != null)
    {
      for (ISkill skill : this._skillMap.values())
      {
        skill.RemoveUser(this._client.GetPlayer());
      }
    }
    
    this._skillMap.clear();
  }
  
  public void ClearDefaultSkills()
  {
    this._lastSkillMap = new NautHashMap();
  }
  
  public void SetGameClass(IPvpClass gameClass)
  {
    ClearSkills();
    
    this._gameClass = gameClass;
  }
  
  public IPvpClass GetGameClass()
  {
    return this._gameClass;
  }
  
  public boolean IsGameClass(IPvpClass.ClassType type)
  {
    if (GetGameClass() == null) {
      return false;
    }
    return GetGameClass().GetType() == type;
  }
  
  public Collection<ISkill> GetSkills()
  {
    if (this._skillMap == null) {
      this._skillMap = new NautHashMap();
    }
    return this._skillMap.values();
  }
  
  public Collection<Map.Entry<ISkill, Integer>> GetDefaultSkills()
  {
    return this._lastSkillMap.values();
  }
  
  public ISkill GetSkillByType(ISkill.SkillType skillType)
  {
    if (this._skillMap == null) {
      this._skillMap = new NautHashMap();
    }
    if (this._skillMap.containsKey(skillType)) {
      return (ISkill)this._skillMap.get(skillType);
    }
    return null;
  }
  
  public void AddSkill(ISkill skill, int level)
  {
    if (skill == null) {
      return;
    }
    if (this._skillMap == null) {
      this._skillMap = new NautHashMap();
    }
    if (this._skillMap.get(skill.GetSkillType()) != null) {
      ((ISkill)this._skillMap.get(skill.GetSkillType())).RemoveUser(this._client.GetPlayer());
    }
    this._skillMap.put(skill.GetSkillType(), skill);
    this._lastSkillMap.put(skill.GetSkillType(), new AbstractMap.SimpleEntry(skill, Integer.valueOf(level)));
    
    skill.AddUser(this._client.GetPlayer(), level);
    
    if (IsSavingCustomBuild()) {
      this._savingCustomBuild.setSkill(skill, level);
    }
  }
  
  public void RemoveSkill(ISkill skill) {
    if (skill == null) {
      return;
    }
    if (this._skillMap == null) {
      return;
    }
    this._skillMap.remove(skill.GetSkillType());
    this._lastSkillMap.remove(skill.GetSkillType());
    
    if (IsSavingCustomBuild()) {
      this._savingCustomBuild.removeSkill(skill);
    }
    skill.RemoveUser(this._client.GetPlayer());
  }
  
  public ItemStack[] GetDefaultArmor()
  {
    return this._lastArmor;
  }
  
  public void ClearDefaults()
  {
    this._lastItems.clear();
    this._lastArmor = new ItemStack[4];
    this._lastSkillMap.clear();
  }
  
  private boolean ValidSkill(String skillName, ISkill skill, ISkill.SkillType expectedType)
  {
    if ((skillName == null) || (skill == null) || (expectedType == null)) {
      return false;
    }
    if ((!skillName.isEmpty()) && ((skill == null) || (skill.GetSkillType() != expectedType) || ((!skill.IsFree()) && (!this._donor.OwnsUnknownPackage("Champions " + skillName)) && (!this._client.GetRank().Has(Rank.ULTRA)) && (!this._donor.OwnsUnknownPackage("Competitive ULTRA"))))) {
      return false;
    }
    return true;
  }
  
  public void DisplaySkills(Player player)
  {
    player.sendMessage("------------------------------------------");
    
    for (ISkill.SkillType type : this._lastSkillMap.keySet())
    {
      player.sendMessage(C.cGreen + type + ": " + C.cWhite + ((ISkill)((Map.Entry)this._lastSkillMap.get(type)).getKey()).GetName() + " " + ((Map.Entry)this._lastSkillMap.get(type)).getValue());
    }
  }
}
