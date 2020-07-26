package mineplex.minecraft.game.classcombat.Skill;

import org.bukkit.entity.Player;

public abstract interface ISkill
{
  public abstract String GetName();
  
  public abstract int getLevel(org.bukkit.entity.Entity paramEntity);
  
  public abstract mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType GetClassType();
  
  public static enum SkillType {
    Axe, 
    Bow, 
    Sword, 
    PassiveA, 
    PassiveB, 
    GlobalPassive, 
    Class;
  }
  
  public abstract SkillType GetSkillType();
  
  public abstract int GetGemCost();
  
  public abstract int GetTokenCost();
  
  public abstract boolean IsFree();
  
  public abstract void setFree(boolean paramBoolean);
  
  public abstract String[] GetDesc(int paramInt);
  
  public abstract void Reset(Player paramPlayer);
  
  public abstract java.util.Set<Player> GetUsers();
  
  public abstract void AddUser(Player paramPlayer, int paramInt);
  
  public abstract void RemoveUser(Player paramPlayer);
  
  public abstract Integer GetSalesPackageId();
  
  public abstract int getMaxLevel();
}
