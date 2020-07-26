package mineplex.minecraft.game.classcombat.Class.repository.token;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.item.Item;
import org.bukkit.Material;


public class CustomBuildToken
{
  public static int MAX_SKILL_TOKENS = 12;
  public static int MAX_ITEM_TOKENS = 12;
  
  public int CustomBuildId;
  
  public String PlayerName;
  
  public String Name;
  public boolean Active;
  public Integer CustomBuildNumber = Integer.valueOf(0);
  
  public String PvpClass = "";
  
  public String SwordSkill = "";
  public Integer SwordSkillLevel = Integer.valueOf(0);
  
  public String AxeSkill = "";
  public Integer AxeSkillLevel = Integer.valueOf(0);
  
  public String BowSkill = "";
  public Integer BowSkillLevel = Integer.valueOf(0);
  
  public String ClassPassiveASkill = "";
  public Integer ClassPassiveASkillLevel = Integer.valueOf(0);
  
  public String ClassPassiveBSkill = "";
  public Integer ClassPassiveBSkillLevel = Integer.valueOf(0);
  
  public String GlobalPassiveSkill = "";
  public Integer GlobalPassiveSkillLevel = Integer.valueOf(0);
  
  public List<SlotToken> Slots = new ArrayList(9);
  
  public int SkillTokens = MAX_SKILL_TOKENS;
  public int ItemTokens = 1;
  
  public CustomBuildToken() {}
  
  public CustomBuildToken(IPvpClass.ClassType gameClassType)
  {
    this.PvpClass = gameClassType.name();
    
    for (int i = 0; i < 9; i++)
    {
      this.Slots.add(new SlotToken());
    }
    
    this.Slots.set(0, new SlotToken("Standard Sword", Material.IRON_SWORD, 1));
    this.Slots.set(1, new SlotToken("Standard Axe", Material.IRON_AXE, 1));
    
    for (int i = 2; i < 9; i++)
    {
      this.Slots.set(i, new SlotToken("Mushroom Soup", Material.MUSHROOM_SOUP, 1));
    }
    
    if ((gameClassType == IPvpClass.ClassType.Assassin) || (gameClassType == IPvpClass.ClassType.Ranger))
    {
      this.Slots.set(2, new SlotToken("Standard Bow", Material.BOW, 1));
      this.Slots.set(3, new SlotToken(gameClassType.name() + " Arrows", Material.ARROW, gameClassType == IPvpClass.ClassType.Assassin ? 12 : 24));
      this.ItemTokens = 1;
    }
    else
    {
      if (gameClassType != IPvpClass.ClassType.Mage)
      {
        this.Slots.set(7, new SlotToken("Water Bottle", Material.POTION, 1));
      }
      else
      {
        this.Slots.set(7, new SlotToken("Cobweb", Material.WEB, 3));
      }
      
      this.Slots.set(8, new SlotToken());
      this.ItemTokens = 0;
    }
  }
  
  public void printInfo()
  {
    System.out.println("CustomBuildId : " + this.CustomBuildId);
    System.out.println("PlayerName : " + this.PlayerName);
    System.out.println("Name : " + this.Name);
    System.out.println("Active : " + this.Active);
    
    System.out.println("CustomBuildNumber : " + this.CustomBuildNumber);
    
    System.out.println("PvpClass : " + this.PvpClass);
    
    System.out.println("SwordSkill : " + this.SwordSkill);
    System.out.println("SwordLevel : " + this.SwordSkillLevel);
    
    System.out.println("AxeSkill : " + this.AxeSkill);
    System.out.println("AxeLevel : " + this.AxeSkillLevel);
    
    System.out.println("BowSkill : " + this.BowSkill);
    System.out.println("BowLevel : " + this.BowSkillLevel);
    
    System.out.println("ClassPassiveASkill : " + this.ClassPassiveASkill);
    System.out.println("ClassPassiveALevel : " + this.ClassPassiveASkillLevel);
    
    System.out.println("ClassPassiveBSkill : " + this.ClassPassiveBSkill);
    System.out.println("ClassPassiveBLevel : " + this.ClassPassiveBSkillLevel);
    
    System.out.println("GlobalPassiveSkill : " + this.GlobalPassiveSkill);
    System.out.println("GlobalPassiveLevel : " + this.GlobalPassiveSkillLevel);
    
    for (SlotToken token : this.Slots)
    {
      token.printInfo();
    }
  }
  
  public void setSkill(ISkill skill, int level)
  {
    switch (skill.GetSkillType())
    {
    case Axe: 
      this.AxeSkill = skill.GetName();
      this.AxeSkillLevel = Integer.valueOf(level);
      break;
    case Bow: 
      this.BowSkill = skill.GetName();
      this.BowSkillLevel = Integer.valueOf(level);
      break;
    case Sword: 
      break;
    case PassiveB: 
      this.GlobalPassiveSkill = skill.GetName();
      this.GlobalPassiveSkillLevel = Integer.valueOf(level);
      break;
    case GlobalPassive: 
      this.ClassPassiveASkill = skill.GetName();
      this.ClassPassiveASkillLevel = Integer.valueOf(level);
      break;
    case PassiveA: 
      this.ClassPassiveBSkill = skill.GetName();
      this.ClassPassiveBSkillLevel = Integer.valueOf(level);
      break;
    case Class: 
      this.SwordSkill = skill.GetName();
      this.SwordSkillLevel = Integer.valueOf(level);
      break;
    }
    
    

    this.SkillTokens = (this.SkillTokens - skill.GetTokenCost() * level);
  }
  
  public void removeSkill(ISkill skill)
  {
    int level = 0;
    
    switch (skill.GetSkillType())
    {
    case Axe: 
      this.AxeSkill = "";
      level = this.AxeSkillLevel.intValue();
      this.AxeSkillLevel = Integer.valueOf(0);
      break;
    case Bow: 
      this.BowSkill = "";
      level = this.BowSkillLevel.intValue();
      this.BowSkillLevel = Integer.valueOf(0);
      break;
    case Sword: 
      break;
    case PassiveB: 
      this.GlobalPassiveSkill = "";
      level = this.GlobalPassiveSkillLevel.intValue();
      this.GlobalPassiveSkillLevel = Integer.valueOf(0);
      break;
    case GlobalPassive: 
      this.ClassPassiveASkill = "";
      level = this.ClassPassiveASkillLevel.intValue();
      this.ClassPassiveASkillLevel = Integer.valueOf(0);
      break;
    case PassiveA: 
      this.ClassPassiveBSkill = "";
      level = this.ClassPassiveBSkillLevel.intValue();
      this.ClassPassiveBSkillLevel = Integer.valueOf(0);
      break;
    case Class: 
      this.SwordSkill = "";
      level = this.SwordSkillLevel.intValue();
      this.SwordSkillLevel = Integer.valueOf(0);
      break;
    }
    
    

    this.SkillTokens = (this.SkillTokens + skill.GetTokenCost() * level);
  }
  
  public boolean hasSkill(ISkill skill)
  {
    return (this.SwordSkill.equalsIgnoreCase(skill.GetName())) || 
      (this.AxeSkill.equalsIgnoreCase(skill.GetName())) || 
      (this.BowSkill.equalsIgnoreCase(skill.GetName())) || 
      (this.ClassPassiveASkill.equalsIgnoreCase(skill.GetName())) || 
      (this.ClassPassiveBSkill.equalsIgnoreCase(skill.GetName())) || 
      (this.GlobalPassiveSkill.equalsIgnoreCase(skill.GetName()));
  }
  
  public int getLevel(ISkill skill)
  {
    switch (skill.GetSkillType())
    {
    case Axe: 
      return this.AxeSkillLevel.intValue();
    case Bow: 
      return this.BowSkillLevel.intValue();
    case PassiveB: 
      return this.GlobalPassiveSkillLevel.intValue();
    case GlobalPassive: 
      return this.ClassPassiveASkillLevel.intValue();
    case PassiveA: 
      return this.ClassPassiveBSkillLevel.intValue();
    case Class: 
      return this.SwordSkillLevel.intValue();
    }
    return 0;
  }
  

  public boolean hasItem(Material material, String name)
  {
    for (SlotToken token : this.Slots)
    {

      if (token != null)
      {

        if (token.Material != null)
        {

          if (token.Name != null)
          {

            if ((token.Material.equalsIgnoreCase(material.name())) && (token.Name.equalsIgnoreCase(name)))
              return true; } }
      }
    }
    return false;
  }
  
  public boolean hasItemType(Material material)
  {
    for (SlotToken token : this.Slots)
    {

      if (token != null)
      {

        if (token.Material != null)
        {

          if (token.Material.equalsIgnoreCase(material.name()))
            return true; }
      }
    }
    return false;
  }
  
  public boolean hasItemWithNameLike(String name)
  {
    for (SlotToken token : this.Slots)
    {

      if (token != null)
      {

        token.printInfo();
        
        if (token.Name != null)
        {

          if (token.Name.contains(name))
          {
            return true; }
        }
      }
    }
    return false;
  }
  
  public int getLastItemIndexWithNameLike(String name)
  {
    for (int i = this.Slots.size() - 1; i >= 0; i--)
    {
      SlotToken token = (SlotToken)this.Slots.get(i);
      

      if (token != null)
      {

        if (token.Name != null)
        {

          if (token.Name.contains(name))
            return this.Slots.indexOf(token); }
      }
    }
    return -1;
  }
  
  public int getItemIndexWithNameLike(String name)
  {
    for (SlotToken token : this.Slots)
    {

      if (token != null)
      {

        if (token.Name != null)
        {

          if (token.Name.contains(name))
            return this.Slots.indexOf(token); }
      }
    }
    return -1;
  }
  
  public void addItem(Item item, int index)
  {
    SlotToken token = (SlotToken)this.Slots.get(index);
    
    token.Material = item.GetType().name();
    token.Amount = item.GetAmount();
    token.Name = item.GetName();
    
    this.ItemTokens -= item.getTokenCost();
  }
  
  public void removeItem(Item item, int index)
  {
    SlotToken token = (SlotToken)this.Slots.get(index);
    
    token.Material = null;
    token.Amount = 0;
    token.Name = null;
    
    this.ItemTokens += item.getTokenCost();
  }
}
