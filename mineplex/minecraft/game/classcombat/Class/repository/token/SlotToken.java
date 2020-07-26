package mineplex.minecraft.game.classcombat.Class.repository.token;

import java.io.PrintStream;

public class SlotToken
{
  public String Name = "";
  public String Material = "";
  public int Amount = 0;
  
  public SlotToken() {}
  
  public SlotToken(String name, org.bukkit.Material material, int amount)
  {
    this.Name = name;
    this.Material = material.name();
    this.Amount = amount;
  }
  
  public void printInfo()
  {
    System.out.println("Name : " + this.Name);
    System.out.println("Material : " + this.Material);
    System.out.println("Amount : " + this.Amount);
  }
}
