package mineplex.minecraft.game.classcombat.Class;

import java.util.HashSet;
import mineplex.core.common.util.UtilGear;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.minecraft.game.classcombat.Class.repository.token.CustomBuildToken;
import mineplex.minecraft.game.classcombat.Skill.ISkill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;



public class PvpClass
  implements IPvpClass
{
  private IPvpClass.ClassType _type;
  private CustomBuildToken _customBuild;
  private int _salesPackageId;
  private String[] _desc;
  private int _cost;
  private boolean _free;
  private Material _head;
  private Material _chest;
  private Material _legs;
  private Material _boots;
  private Color _leatherColor = null;
  
  private HashSet<ISkill> _skillSet;
  
  private ClassManager _classes;
  
  public PvpClass(ClassManager classes, int salesPackageId, IPvpClass.ClassType type, CustomBuildToken customBuild, String[] desc, Material head, Material chest, Material legs, Material boots, Color leatherColor)
  {
    this._classes = classes;
    this._salesPackageId = salesPackageId;
    this._cost = 5000;
    this._desc = desc;
    
    this._type = type;
    
    this._customBuild = customBuild;
    this._customBuild.PvpClass = type.name();
    
    this._head = head;
    this._chest = chest;
    this._legs = legs;
    this._boots = boots;
    
    this._skillSet = new HashSet();
    this._skillSet.addAll(this._classes.GetSkillFactory().GetSkillsFor(this));
    this._skillSet.addAll(this._classes.GetSkillFactory().GetGlobalSkillsFor(this));
    
    this._leatherColor = leatherColor;
  }
  

  public String GetName()
  {
    return this._type.name();
  }
  

  public IPvpClass.ClassType GetType()
  {
    return this._type;
  }
  

  public Material GetHead()
  {
    return this._head;
  }
  

  public Material GetChestplate()
  {
    return this._chest;
  }
  

  public Material GetLeggings()
  {
    return this._legs;
  }
  

  public Material GetBoots()
  {
    return this._boots;
  }
  

  public HashSet<ISkill> GetSkills()
  {
    return this._skillSet;
  }
  
  public void checkEquip()
  {
    for (Player cur : )
    {
      ClientClass client = (ClientClass)this._classes.Get(cur);
      

      if ((client.GetGameClass() != null) && 
        (client.GetGameClass().GetType() == this._type))
      {
        PlayerInventory inv = cur.getInventory();
        

        if ((this._head != null) && 
          (!UtilGear.isMat(inv.getHelmet(), this._head)))
        {
          Unequip(cur);
          continue;
        }
        

        if ((this._chest != null) && 
          (!UtilGear.isMat(inv.getChestplate(), this._chest)))
        {
          Unequip(cur);
          continue;
        }
        

        if ((this._legs != null) && 
          (!UtilGear.isMat(inv.getLeggings(), this._legs)))
        {
          Unequip(cur);
          continue;
        }
        

        if ((this._boots != null) && 
          (!UtilGear.isMat(inv.getBoots(), this._boots)))
        {
          Unequip(cur);
          continue;
        }
        
        if (this._leatherColor != null)
        {
          if ((!((LeatherArmorMeta)inv.getHelmet().getItemMeta()).getColor().equals(this._leatherColor)) || 
            (!((LeatherArmorMeta)inv.getChestplate().getItemMeta()).getColor().equals(this._leatherColor)) || 
            (!((LeatherArmorMeta)inv.getLeggings().getItemMeta()).getColor().equals(this._leatherColor)) || 
            (!((LeatherArmorMeta)inv.getBoots().getItemMeta()).getColor().equals(this._leatherColor)))
          {
            Unequip(cur);
            continue;
          }
        }
      }
      

      if ((client.GetGameClass() == null) || (client.GetGameClass().GetType() == null) || (
        (this._leatherColor != null) && (client.GetGameClass().GetType() != GetType())))
      {
        PlayerInventory inv = cur.getInventory();
        

        if ((this._head == null) || 
          (UtilGear.isMat(inv.getHelmet(), this._head)))
        {


          if ((this._chest == null) || 
            (UtilGear.isMat(inv.getChestplate(), this._chest)))
          {


            if ((this._legs == null) || 
              (UtilGear.isMat(inv.getLeggings(), this._legs)))
            {


              if ((this._boots == null) || 
                (UtilGear.isMat(inv.getBoots(), this._boots)))
              {

                if ((this._leatherColor == null) || (
                
                  (((LeatherArmorMeta)inv.getHelmet().getItemMeta()).getColor().equals(this._leatherColor)) && 
                  (((LeatherArmorMeta)inv.getChestplate().getItemMeta()).getColor().equals(this._leatherColor)) && 
                  (((LeatherArmorMeta)inv.getLeggings().getItemMeta()).getColor().equals(this._leatherColor)) && 
                  (((LeatherArmorMeta)inv.getBoots().getItemMeta()).getColor().equals(this._leatherColor))))
                {


                  Equip(cur); } } } }
        }
      }
    }
  }
  
  public void Equip(Player player) {
    ClientClass client = (ClientClass)this._classes.Get(player);
    
    CustomBuildToken customBuild = client.GetActiveCustomBuild(this);
    
    if (customBuild != null)
    {
      client.EquipCustomBuild(customBuild);
    }
    else
    {
      client.SetGameClass(this);
    }
    

    player.setSneaking(false);
  }
  
  public void Unequip(Player player)
  {
    ((ClientClass)this._classes.Get(player)).SetGameClass(null);
  }
  



  public int GetSalesPackageId()
  {
    return this._salesPackageId;
  }
  

  public Integer GetCost()
  {
    return Integer.valueOf(this._cost);
  }
  

  public String[] GetDesc()
  {
    return this._desc;
  }
  

  public boolean IsFree()
  {
    return this._free;
  }
  

  public void ApplyArmor(Player caller)
  {
    ItemStack head = ItemStackFactory.Instance.CreateStack(GetHead(), 1);
    ItemStack chest = ItemStackFactory.Instance.CreateStack(GetChestplate(), 1);
    ItemStack legs = ItemStackFactory.Instance.CreateStack(GetLeggings(), 1);
    ItemStack boots = ItemStackFactory.Instance.CreateStack(GetBoots(), 1);
    
    if (this._leatherColor != null)
    {



      LeatherArmorMeta meta = (LeatherArmorMeta)head.getItemMeta();
      meta.setColor(this._leatherColor);
      head.setItemMeta(meta);
      

      meta = (LeatherArmorMeta)chest.getItemMeta();
      meta.setColor(this._leatherColor);
      chest.setItemMeta(meta);
      

      meta = (LeatherArmorMeta)legs.getItemMeta();
      meta.setColor(this._leatherColor);
      legs.setItemMeta(meta);
      

      meta = (LeatherArmorMeta)boots.getItemMeta();
      meta.setColor(this._leatherColor);
      boots.setItemMeta(meta);
    }
    
    caller.getInventory().setHelmet(head);
    caller.getInventory().setChestplate(chest);
    caller.getInventory().setLeggings(legs);
    caller.getInventory().setBoots(boots);
  }
  

  public CustomBuildToken getDefaultBuild()
  {
    return this._customBuild;
  }
}
