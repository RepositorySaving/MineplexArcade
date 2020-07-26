package mineplex.minecraft.game.core;

import org.bukkit.entity.Player;

public abstract interface IRelation
{
  public abstract boolean CanHurt(Player paramPlayer1, Player paramPlayer2);
  
  public abstract boolean CanHurt(String paramString1, String paramString2);
  
  public abstract boolean IsSafe(Player paramPlayer);
}
