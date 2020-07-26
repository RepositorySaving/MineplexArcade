package nautilus.game.arcade.game.games.christmas;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilMath;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftFallingSand;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class SleighPart
{
  public Chicken Ent;
  public FallingBlock Block;
  public double OffsetX;
  public double OffsetZ;
  
  public SleighPart(int rise, int id, int data, Location loc, double x, double z)
  {
    this.Ent = ((Chicken)loc.getWorld().spawn(loc.add(x, 0.0D, z), Chicken.class));
    this.Ent.setBaby();
    this.Ent.setAgeLock(true);
    UtilEnt.Vegetate(this.Ent, true);
    UtilEnt.ghost(this.Ent, true, true);
    

    Chicken top = this.Ent;
    for (int i = 0; i < rise; i++)
    {
      Chicken newTop = (Chicken)loc.getWorld().spawn(loc.add(x, 0.0D, z), Chicken.class);
      newTop.setBaby();
      newTop.setAgeLock(true);
      UtilEnt.Vegetate(newTop, true);
      UtilEnt.ghost(newTop, true, true);
      
      top.setPassenger(newTop);
      top = newTop;
    }
    

    if (id != 0)
    {
      this.Block = loc.getWorld().spawnFallingBlock(loc.add(0.0D, 1.0D, 0.0D), id, (byte)data);
      top.setPassenger(this.Block);
    }
    
    this.OffsetX = x;
    this.OffsetZ = z;
  }
  
  public void RefreshBlocks()
  {
    if (this.Ent == null) {
      return;
    }
    Entity ent = this.Ent;
    
    while (ent.getPassenger() != null)
    {
      ent = ent.getPassenger();
      
      if ((ent instanceof FallingBlock)) {
        ((CraftFallingSand)ent).getHandle().b = 1;
      }
    }
  }
  
  public void SetPresent() {
    if (this.Ent == null) {
      return;
    }
    this.Block = this.Ent.getWorld().spawnFallingBlock(this.Ent.getLocation().add(0.0D, 1.0D, 0.0D), 35, (byte)UtilMath.r(15));
    
    Entity top = this.Ent;
    while (top.getPassenger() != null) {
      top = top.getPassenger();
    }
    top.setPassenger(this.Block);
  }
  
  public void AddSanta()
  {
    if (this.Ent == null) {
      return;
    }
    Skeleton skel = (Skeleton)this.Ent.getWorld().spawn(this.Ent.getLocation().add(0.0D, 1.0D, 0.0D), Skeleton.class);
    UtilEnt.Vegetate(skel);
    UtilEnt.ghost(skel, true, false);
    
    ItemStack head = new ItemStack(Material.LEATHER_HELMET);
    LeatherArmorMeta meta = (LeatherArmorMeta)head.getItemMeta();
    meta.setColor(Color.RED);
    head.setItemMeta(meta);
    skel.getEquipment().setHelmet(head);
    
    ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
    meta = (LeatherArmorMeta)chest.getItemMeta();
    meta.setColor(Color.RED);
    chest.setItemMeta(meta);
    skel.getEquipment().setChestplate(chest);
    
    ItemStack legs = new ItemStack(Material.LEATHER_LEGGINGS);
    meta = (LeatherArmorMeta)legs.getItemMeta();
    meta.setColor(Color.RED);
    legs.setItemMeta(meta);
    skel.getEquipment().setLeggings(legs);
    
    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
    meta = (LeatherArmorMeta)boots.getItemMeta();
    meta.setColor(Color.RED);
    boots.setItemMeta(meta);
    skel.getEquipment().setBoots(boots);
    
    skel.setCustomName(C.Bold + "Santa Claus");
    skel.setCustomNameVisible(true);
    
    Entity top = this.Ent;
    while (top.getPassenger() != null) {
      top = top.getPassenger();
    }
    top.setPassenger(skel);
  }
  
  public boolean HasEntity(LivingEntity ent)
  {
    if (this.Ent.equals(ent)) {
      return true;
    }
    Entity top = this.Ent;
    
    while (top.getPassenger() != null)
    {
      top = top.getPassenger();
      
      if (top.equals(ent)) {
        return true;
      }
    }
    return false;
  }
  
  public Entity GetTop()
  {
    Entity ent = this.Ent;
    
    while (ent.getPassenger() != null)
    {
      ent = ent.getPassenger();
    }
    
    return ent;
  }
}
