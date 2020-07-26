package mineplex.core.disguise.disguises;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_7_R3.DataWatcher;
import net.minecraft.server.v1_7_R3.ItemStack;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityEquipment;

public abstract class DisguiseInsentient extends DisguiseLiving
{
  private boolean _showArmor;
  
  public DisguiseInsentient(org.bukkit.entity.Entity entity)
  {
    super(entity);
    
    this.DataWatcher.a(11, Byte.valueOf((byte)0));
    this.DataWatcher.a(10, "");
  }
  
  public void SetName(String name)
  {
    this.DataWatcher.watch(10, name);
  }
  
  public boolean HasCustomName()
  {
    return this.DataWatcher.getString(10).length() > 0;
  }
  
  public void SetCustomNameVisible(boolean visible)
  {
    this.DataWatcher.watch(11, Byte.valueOf((byte)(visible ? 1 : 0)));
  }
  
  public boolean GetCustomNameVisible()
  {
    return this.DataWatcher.getByte(11) == 1;
  }
  
  public boolean armorVisible()
  {
    return this._showArmor;
  }
  
  public void showArmor()
  {
    this._showArmor = true;
  }
  
  public void hideArmor()
  {
    this._showArmor = false;
  }
  
  public List<net.minecraft.server.v1_7_R3.Packet> getArmorPackets()
  {
    List<PacketPlayOutEntityEquipment> p5 = new ArrayList();
    ItemStack[] armorContents = this.Entity.getEquipment();
    
    for (short i = 0; i < armorContents.length; i = (short)(i + 1))
    {
      ItemStack armorSlot = armorContents[i];
      
      if (armorSlot != null)
      {
        p5.add(new PacketPlayOutEntityEquipment(this.Entity.getId(), i, armorSlot));
      }
    }
    
    return null;
  }
}
