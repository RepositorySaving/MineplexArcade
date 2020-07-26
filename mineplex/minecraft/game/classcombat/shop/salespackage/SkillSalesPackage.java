package mineplex.minecraft.game.classcombat.shop.salespackage;

import mineplex.core.common.CurrencyType;
import mineplex.core.shop.item.SalesPackageBase;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkillSalesPackage
  extends SalesPackageBase
{
  public SkillSalesPackage(ISkill skill)
  {
    super("Champions " + skill.GetName(), Material.BOOK, (byte)0, skill.GetDesc(0), skill.GetGemCost());
    this.Free = skill.IsFree();
    this.KnownPackage = false;
  }
  
  public void Sold(Player player, CurrencyType currencyType) {}
}
