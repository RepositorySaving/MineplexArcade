package mineplex.core.common.util;

import java.io.PrintStream;
import java.lang.reflect.Field;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class UtilParticle
{
  public static enum ParticleType
  {
    HUGE_EXPLOSION("hugeexplosion"), 
    LARGE_EXPLODE("largeexplode"), 
    FIREWORKS_SPARK("fireworksSpark"), 
    BUBBLE("bubble"), 
    SUSPEND("suspend"), 
    DEPTH_SUSPEND("depthSuspend"), 
    TOWN_AURA("townaura"), 
    CRIT("crit"), 
    MAGIC_CRIT("magicCrit"), 
    MOB_SPELL("mobSpell"), 
    MOB_SPELL_AMBIENT("mobSpellAmbient"), 
    SPELL("spell"), 
    INSTANT_SPELL("instantSpell"), 
    WITCH_MAGIC("witchMagic"), 
    NOTE("note"), 
    PORTAL("portal"), 
    ENCHANTMENT_TABLE("enchantmenttable"), 
    EXPLODE("explode"), 
    FLAME("flame"), 
    LAVA("lava"), 
    FOOTSTEP("footstep"), 
    SPLASH("splash"), 
    LARGE_SMOKE("largesmoke"), 
    CLOUD("cloud"), 
    RED_DUST("reddust"), 
    SNOWBALL_POOF("snowballpoof"), 
    DRIP_WATER("dripWater"), 
    DRIP_LAVA("dripLava"), 
    SNOW_SHOVEL("snowshovel"), 
    SLIME("slime"), 
    HEART("heart"), 
    ANGRY_VILLAGER("angryVillager"), 
    HAPPY_VILLAGER("happerVillager");
    
    public String particleName;
    
    private ParticleType(String particleName)
    {
      this.particleName = particleName;
    }
  }
  
  public static void PlayParticle(Player player, ParticleType type, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count)
  {
    PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();
    
    for (Field field : packet.getClass().getDeclaredFields())
    {
      try
      {
        field.setAccessible(true);
        String fieldName = field.getName();
        String str1; switch ((str1 = fieldName).hashCode()) {case 97:  if (str1.equals("a")) break; break; case 98:  if (str1.equals("b")) {} break; case 99:  if (str1.equals("c")) {} break; case 100:  if (str1.equals("d")) {} break; case 101:  if (str1.equals("e")) {} break; case 102:  if (str1.equals("f")) {} break; case 103:  if (str1.equals("g")) {} break; case 104:  if (str1.equals("h")) {} break; case 105:  if (!str1.equals("i"))
          {

            continue;field.set(packet, type.particleName);
            continue;
            
            field.setFloat(packet, (float)location.getX());
            continue;
            
            field.setFloat(packet, (float)location.getY());
            continue;
            
            field.setFloat(packet, (float)location.getZ());
            continue;
            
            field.setFloat(packet, offsetX);
            continue;
            
            field.setFloat(packet, offsetY);
            continue;
            
            field.setFloat(packet, offsetZ);
            continue;
            
            field.setFloat(packet, speed);
          }
          else {
            field.setInt(packet, count);
          }
          break;
        }
      }
      catch (Exception e) {
        System.out.println(e.getMessage());
        return;
      }
    }
    
    ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
  }
  
  public static void PlayParticle(ParticleType type, Location location, float offsetX, float offsetY, float offsetZ, float speed, int count)
  {
    for (Player player : ) {
      PlayParticle(player, type, location, offsetX, offsetY, offsetZ, speed, count);
    }
  }
}
