package mineplex.core.npc;

import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.Navigation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Entity;


public class NpcEntry
{
  public String Name;
  public Entity Entity;
  public int Radius;
  public Location Location;
  private boolean _returning = false;
  
  public NpcEntry(String name, Entity entity, int radius, Location location)
  {
    this.Name = name;
    this.Entity = entity;
    this.Radius = radius;
    this.Location = location;
  }
  
  public void ReturnToPost()
  {
    EntityCreature ec = ((CraftCreature)this.Entity).getHandle();
    ec.getNavigation().a(this.Location.getX(), this.Location.getY(), this.Location.getZ(), 0.800000011920929D);
    
    this._returning = true;
  }
  
  public boolean IsInRadius()
  {
    Location entityLocation = this.Entity.getLocation();
    return Math.abs(entityLocation.getBlockX() - this.Location.getBlockX()) + Math.abs(entityLocation.getBlockY() - this.Location.getBlockY()) + Math.abs(entityLocation.getBlockZ() - this.Location.getBlockZ()) <= this.Radius;
  }
  
  public boolean Returning()
  {
    return this._returning;
  }
  
  public void ClearGoals()
  {
    this._returning = false;
    
    Location entityLocation = this.Entity.getLocation();
    EntityCreature ec = ((CraftCreature)this.Entity).getHandle();
    ec.getNavigation().a(entityLocation.getX(), entityLocation.getY(), entityLocation.getZ(), 0.800000011920929D);
  }
}
