package mineplex.minecraft.game.classcombat.item;

import org.bukkit.Material;

public abstract interface IItem
{
  public abstract Material GetType();
  
  public abstract int GetAmount();
  
  public abstract int GetGemCost();
  
  public abstract String GetName();
  
  public abstract String[] GetDesc();
}
