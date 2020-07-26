package mineplex.minecraft.game.classcombat.shop;

import mineplex.core.MiniPlugin;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.item.ItemFactory;
import org.bukkit.plugin.java.JavaPlugin;

public class ClassShopManager
  extends MiniPlugin
{
  private ClassManager _classManager;
  private SkillFactory _skillFactory;
  private ItemFactory _itemFactory;
  
  public ClassShopManager(JavaPlugin plugin, ClassManager classManager, SkillFactory skillFactory, ItemFactory itemFactory)
  {
    super("Class Shop Manager", plugin);
    
    this._classManager = classManager;
    this._skillFactory = skillFactory;
    this._itemFactory = itemFactory;
  }
  
  public ClassManager GetClassManager()
  {
    return this._classManager;
  }
  
  public SkillFactory GetSkillFactory()
  {
    return this._skillFactory;
  }
  
  public ItemFactory GetItemFactory()
  {
    return this._itemFactory;
  }
}
