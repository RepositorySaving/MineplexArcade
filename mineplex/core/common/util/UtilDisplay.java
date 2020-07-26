package mineplex.core.common.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import net.minecraft.server.v1_7_R3.DataWatcher;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.Packet;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;





public class UtilDisplay
{
  public static final int ENTITY_ID = 1234;
  private static HashMap<String, Boolean> hasHealthBar = new HashMap();
  
  public static void sendPacket(Player player, Packet packet) {
    EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
    
    entityPlayer.playerConnection.sendPacket(packet);
  }
  
  public static PacketPlayOutSpawnEntityLiving getMobPacket(String text, double healthPercent, Location loc)
  {
    PacketPlayOutSpawnEntityLiving mobPacket = new PacketPlayOutSpawnEntityLiving();
    
    mobPacket.a = 1234;
    mobPacket.b = ((byte)EntityType.ENDER_DRAGON.getTypeId());
    mobPacket.c = ((int)Math.floor(loc.getBlockX() * 32.0D));
    mobPacket.d = MathHelper.floor(-6400.0D);
    mobPacket.e = ((int)Math.floor(loc.getBlockZ() * 32.0D));
    mobPacket.f = 0;
    mobPacket.g = 0;
    mobPacket.h = 0;
    mobPacket.i = 0;
    mobPacket.j = 0;
    mobPacket.k = 0;
    
    DataWatcher watcher = getWatcher(text, healthPercent * 200.0D);
    
    mobPacket.l = watcher;
    
    return mobPacket;
  }
  
  public static PacketPlayOutEntityDestroy getDestroyEntityPacket() {
    PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[] { 1234 });
    
    return packet;
  }
  
  public static PacketPlayOutEntityMetadata getMetadataPacket(DataWatcher watcher) {
    PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata();
    
    metaPacket.a = 1234;
    try
    {
      Field b = PacketPlayOutEntityMetadata.class.getDeclaredField("b");
      
      b.setAccessible(true);
      b.set(metaPacket, watcher.c());
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return metaPacket;
  }
  
  public static DataWatcher getWatcher(String text, double health) {
    DataWatcher watcher = new DataWatcher(null);
    
    watcher.a(0, Byte.valueOf((byte)32));
    watcher.a(6, Float.valueOf((float)health));
    watcher.a(10, text);
    watcher.a(11, Byte.valueOf((byte)1));
    watcher.a(16, Integer.valueOf((int)health));
    
    return watcher;
  }
  

  public static void displayTextBar(JavaPlugin plugin, Player player, double healthPercent, String text)
  {
    PacketPlayOutSpawnEntityLiving mobPacket = getMobPacket(text, healthPercent, player.getLocation());
    
    sendPacket(player, mobPacket);
    hasHealthBar.put(player.getName(), Boolean.valueOf(true));
    
    new BukkitRunnable()
    {
      public void run() {
        PacketPlayOutEntityDestroy destroyEntityPacket = UtilDisplay.getDestroyEntityPacket();
        
        UtilDisplay.sendPacket(UtilDisplay.this, destroyEntityPacket);
        UtilDisplay.hasHealthBar.put(UtilDisplay.this.getName(), Boolean.valueOf(false));
      }
    }.runTaskLater(plugin, 120L);
  }
  
  public static void displayLoadingBar(final String text, final String completeText, final Player player, final int healthAdd, long delay, boolean loadUp, final JavaPlugin plugin) {
    PacketPlayOutSpawnEntityLiving mobPacket = getMobPacket(text, 0.0D, player.getLocation());
    
    sendPacket(player, mobPacket);
    hasHealthBar.put(player.getName(), Boolean.valueOf(true));
    
    new BukkitRunnable() {
      int health = ??? ? 0 : 200;
      
      public void run()
      {
        if (this.val$loadUp ? this.health < 200 : this.health > 0) {
          DataWatcher watcher = UtilDisplay.getWatcher(text, this.health);
          PacketPlayOutEntityMetadata metaPacket = UtilDisplay.getMetadataPacket(watcher);
          
          UtilDisplay.sendPacket(player, metaPacket);
          
          if (this.val$loadUp) {
            this.health += healthAdd;
          } else {
            this.health -= healthAdd;
          }
        } else {
          DataWatcher watcher = UtilDisplay.getWatcher(text, this.val$loadUp ? 200 : 0);
          PacketPlayOutEntityMetadata metaPacket = UtilDisplay.getMetadataPacket(watcher);
          PacketPlayOutEntityDestroy destroyEntityPacket = UtilDisplay.getDestroyEntityPacket();
          
          UtilDisplay.sendPacket(player, metaPacket);
          UtilDisplay.sendPacket(player, destroyEntityPacket);
          UtilDisplay.hasHealthBar.put(player.getName(), Boolean.valueOf(false));
          

          PacketPlayOutSpawnEntityLiving mobPacket = UtilDisplay.getMobPacket(completeText, 100.0D, player.getLocation());
          
          UtilDisplay.sendPacket(player, mobPacket);
          UtilDisplay.hasHealthBar.put(player.getName(), Boolean.valueOf(true));
          
          DataWatcher watcher2 = UtilDisplay.getWatcher(completeText, 200.0D);
          PacketPlayOutEntityMetadata metaPacket2 = UtilDisplay.getMetadataPacket(watcher2);
          
          UtilDisplay.sendPacket(player, metaPacket2);
          
          new BukkitRunnable()
          {
            public void run() {
              PacketPlayOutEntityDestroy destroyEntityPacket = UtilDisplay.getDestroyEntityPacket();
              
              UtilDisplay.sendPacket(this.val$player, destroyEntityPacket);
              UtilDisplay.hasHealthBar.put(this.val$player.getName(), Boolean.valueOf(false));
            }
          }.runTaskLater(plugin, 40L);
          
          cancel();
        }
      }
    }.runTaskTimer(plugin, delay, delay);
  }
  
  public static void displayLoadingBar(String text, String completeText, Player player, int secondsDelay, boolean loadUp, JavaPlugin plugin) {
    int healthChangePerSecond = 200 / secondsDelay;
    
    displayLoadingBar(text, completeText, player, healthChangePerSecond, 20L, loadUp, plugin);
  }
}
