package mineplex.minecraft.game.classcombat.item;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.MiniPlugin;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.energy.Energy;
import mineplex.core.projectile.ProjectileManager;
import mineplex.minecraft.game.classcombat.Class.ClassManager;
import mineplex.minecraft.game.classcombat.item.Consume.Soup;
import mineplex.minecraft.game.classcombat.item.Throwable.ProximityZapper;
import mineplex.minecraft.game.classcombat.item.Throwable.Web;
import mineplex.minecraft.game.classcombat.item.weapon.BoosterSword;
import mineplex.minecraft.game.classcombat.item.weapon.StandardBow;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemFactory extends MiniPlugin implements IItemFactory
{
  private BlockRestore _blockRestore;
  private ClassManager _classManager;
  private ConditionManager _condition;
  private DamageManager _damage;
  private Energy _energy;
  private Fire _fire;
  private ProjectileManager _projectileManager;
  private Field _itemMaxDurability;
  private HashMap<String, Item> _items;
  private HashSet<String> _ignore;
  
  public ItemFactory(JavaPlugin plugin, BlockRestore blockRestore, ClassManager classManager, ConditionManager condition, DamageManager damage, Energy energy, Fire fire, ProjectileManager projectileManager, String webAddress)
  {
    this(plugin, blockRestore, classManager, condition, damage, energy, fire, projectileManager, webAddress, new HashSet());
  }
  
  public ItemFactory(JavaPlugin plugin, BlockRestore blockRestore, ClassManager classManager, ConditionManager condition, DamageManager damage, Energy energy, Fire fire, ProjectileManager projectileManager, String webAddress, HashSet<String> ignore)
  {
    super("Item Factory", plugin);
    
    this._blockRestore = blockRestore;
    this._classManager = classManager;
    this._condition = condition;
    this._damage = damage;
    this._energy = energy;
    this._fire = fire;
    this._projectileManager = projectileManager;
    
    this._items = new HashMap();
    this._ignore = ignore;
    
    try
    {
      this._itemMaxDurability = net.minecraft.server.v1_7_R3.Item.class.getDeclaredField("durability");
      this._itemMaxDurability.setAccessible(true);
    }
    catch (SecurityException e)
    {
      e.printStackTrace();
    }
    catch (NoSuchFieldException e)
    {
      e.printStackTrace();
    }
    
    PopulateFactory(webAddress);
  }
  
  private void PopulateFactory(String webAddress)
  {
    this._items.clear();
    
    AddConsumables();
    AddPassive();
    AddThrowable();
    AddTools();
    AddOther();
    addWeapons();
    
    for (Item cur : this._items.values()) {
      RegisterEvents(cur);
    }
  }
  





  private void AddConsumables()
  {
    AddItem(new Soup(this, Material.MUSHROOM_SOUP, 1, true, 0, 1, 
      UtilEvent.ActionType.R, true, 500L, 0, 
      null, false, 0L, 0, 0.0F, 
      -1L, true, true, true, false));
  }
  
  private void addWeapons()
  {
    AddItem(new mineplex.minecraft.game.classcombat.item.weapon.StandardSword(this, 0, 2));
    AddItem(new mineplex.minecraft.game.classcombat.item.weapon.StandardAxe(this, 0, 2));
    
    AddItem(new BoosterSword(this, 2000, 4));
    AddItem(new mineplex.minecraft.game.classcombat.item.weapon.BoosterAxe(this, 2000, 4));
    
    AddItem(new mineplex.minecraft.game.classcombat.item.weapon.PowerSword(this, 2000, 4));
    AddItem(new mineplex.minecraft.game.classcombat.item.weapon.PowerAxe(this, 2000, 4));
    
    AddItem(new StandardBow(this, 0, 1));
  }
  


  private void AddPassive() {}
  

  private void AddThrowable()
  {
    AddItem(new mineplex.minecraft.game.classcombat.item.Throwable.WaterBottle(this, Material.POTION, 1, false, 0, 1, 
      UtilEvent.ActionType.R, true, 500L, 0, 
      UtilEvent.ActionType.L, true, 500L, 0, 1.0F, 
      -1L, true, true, true, false));
    
    AddItem(new Web(this, Material.WEB, 3, false, 500, 1, 
      null, true, 0L, 0, 
      UtilEvent.ActionType.L, true, 250L, 0, 1.0F, 
      -1L, true, true, true, false));
    







    AddItem(new mineplex.minecraft.game.classcombat.item.Throwable.ProximityExplosive(this, Material.COMMAND, 1, false, 1000, 2, 
      null, true, 0L, 0, 
      UtilEvent.ActionType.L, true, 250L, 0, 0.8F, 
      4000L, false, false, false, true));
    
    AddItem(new ProximityZapper(this, Material.REDSTONE_LAMP_OFF, 1, false, 1000, 2, 
      null, true, 0L, 0, 
      UtilEvent.ActionType.L, true, 250L, 0, 0.8F, 
      4000L, false, false, false, true));
  }
  





  private void AddTools() {}
  




  private void AddOther()
  {
    Item assassinArrows = new Item(this, "Assassin Arrows", new String[] { "Arrows for your bow." }, Material.ARROW, 12, true, 0, 1);
    Item rangerArrows = new Item(this, "Ranger Arrows", new String[] { "Arrows for your bow." }, Material.ARROW, 24, true, 0, 1);
    assassinArrows.setFree(true);
    rangerArrows.setFree(true);
    
    AddItem(assassinArrows);
    AddItem(rangerArrows);
  }
  
  public IItem GetItem(String weaponName)
  {
    return (IItem)this._items.get(weaponName);
  }
  

  public java.util.Collection<Item> GetItems()
  {
    return this._items.values();
  }
  
  public void AddItem(Item newItem)
  {
    if (this._ignore.contains(newItem.GetName()))
    {
      System.out.println("Item Factory: Ignored " + newItem.GetName());
      return;
    }
    
    try
    {
      this._itemMaxDurability.setInt(net.minecraft.server.v1_7_R3.Item.d(newItem.GetType().getId()), 56);
    }
    catch (IllegalArgumentException e)
    {
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      e.printStackTrace();
    }
    
    this._items.put(newItem.GetName(), newItem);
  }
  
  public BlockRestore BlockRestore()
  {
    return this._blockRestore;
  }
  
  public ClassManager ClassManager()
  {
    return this._classManager;
  }
  
  public ConditionManager Condition()
  {
    return this._condition;
  }
  
  public DamageManager Damage()
  {
    return this._damage;
  }
  
  public Energy Energy()
  {
    return this._energy;
  }
  
  public Fire Fire()
  {
    return this._fire;
  }
  
  public ProjectileManager Throw()
  {
    return this._projectileManager;
  }
}
