package mineplex.minecraft.game.classcombat.Class;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public abstract interface IPvpClass
{
  public abstract int GetSalesPackageId();
  
  public abstract String GetName();
  
  public abstract ClassType GetType();
  
  public static enum ClassType
  {
    Global, 
    Knight, 
    Ranger, 
    Assassin, 
    Mage, 
    Brute, 
    Shifter;
  }
  
  public abstract Material GetHead();
  
  public abstract Material GetChestplate();
  
  public abstract Material GetLeggings();
  
  public abstract Material GetBoots();
  
  public abstract java.util.HashSet<mineplex.minecraft.game.classcombat.Skill.ISkill> GetSkills();
  
  public abstract void checkEquip();
  
  public abstract Integer GetCost();
  
  public abstract boolean IsFree();
  
  public abstract String[] GetDesc();
  
  public abstract void ApplyArmor(Player paramPlayer);
  
  public abstract mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken getDefaultBuild();
}
