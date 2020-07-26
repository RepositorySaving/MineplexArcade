package mineplex.minecraft.game.core.condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class ConditionApplicator
{
  private HashMap<String, PotionEffectType> _effectMap;
  
  public ConditionApplicator()
  {
    this._effectMap = new HashMap();
    this._effectMap.put("Blindness", PotionEffectType.BLINDNESS);
    this._effectMap.put("Confusion", PotionEffectType.CONFUSION);
    this._effectMap.put("DamageResist", PotionEffectType.DAMAGE_RESISTANCE);
    this._effectMap.put("FastDig", PotionEffectType.FAST_DIGGING);
    this._effectMap.put("FireResist", PotionEffectType.FIRE_RESISTANCE);
    this._effectMap.put("Harm", PotionEffectType.HARM);
    this._effectMap.put("Heal", PotionEffectType.HEAL);
    this._effectMap.put("Hunger", PotionEffectType.HUNGER);
    this._effectMap.put("Strength", PotionEffectType.INCREASE_DAMAGE);
    this._effectMap.put("Jump", PotionEffectType.JUMP);
    this._effectMap.put("Poison", PotionEffectType.POISON);
    this._effectMap.put("Regeneration", PotionEffectType.REGENERATION);
    this._effectMap.put("Slow", PotionEffectType.SLOW);
    this._effectMap.put("SlowDig", PotionEffectType.SLOW_DIGGING);
    this._effectMap.put("Speed", PotionEffectType.SPEED);
    this._effectMap.put("Breathing", PotionEffectType.WATER_BREATHING);
    this._effectMap.put("Weakness", PotionEffectType.WEAKNESS);
    this._effectMap.put("Invisibility", PotionEffectType.INVISIBILITY);
    this._effectMap.put("NightVision", PotionEffectType.NIGHT_VISION);
  }
  
  public void clearEffects(Player player)
  {
    for (PotionEffectType cur : this._effectMap.values()) {
      player.removePotionEffect(cur);
    }
  }
  
  public void listEffect(Player caller) {
    caller.sendMessage(ChatColor.RED + "[C] " + ChatColor.YELLOW + "Listing Potion Effects;");
    
    caller.sendMessage(ChatColor.DARK_GREEN + "Health Effects: " + ChatColor.AQUA + 
      "Harm, Heal, Poison, Regeneration, Hunger");
    
    caller.sendMessage(ChatColor.DARK_GREEN + "Damage Effects: " + ChatColor.AQUA + 
      "Strength, Weakness, DamageResist, FireResist");
    
    caller.sendMessage(ChatColor.DARK_GREEN + "Movement Effects: " + ChatColor.AQUA + 
      "Slow, Speed, Jump, FireResist");
    
    caller.sendMessage(ChatColor.DARK_GREEN + "Vision Effects: " + ChatColor.AQUA + 
      "Blindness, Confusion, NightVision");
    
    caller.sendMessage(ChatColor.DARK_GREEN + "Misc Effects: " + ChatColor.AQUA + 
      "FastDig, SlowDig, Breathing, Invisibility");
  }
  
  public HashMap<String, PotionEffectType> readEffect(Player caller, String eString)
  {
    HashMap<String, PotionEffectType> eList = new HashMap();
    ArrayList<String> errorList = new ArrayList();
    
    String[] eToken = eString.split(",");
    
    for (String eCur : eToken)
    {
      for (String cur : this._effectMap.keySet())
      {
        if (cur.equalsIgnoreCase(eCur))
        {
          eList.put(cur, (PotionEffectType)this._effectMap.get(cur));
        }
        else
        {
          errorList.add(eCur);
        }
      }
    }
    
    if (!errorList.isEmpty())
    {
      String out = ChatColor.RED + "[C] " + ChatColor.YELLOW + "Invalid Effects:" + ChatColor.AQUA;
      
      for (String cur : errorList)
      {
        out = out + " '" + cur + "'";
      }
      
      caller.sendMessage(out);
    }
    
    return eList;
  }
  

  public void doEffect(Player caller, String name, HashMap<String, PotionEffectType> eMap, String durationString, String strengthString, boolean extend)
  {
    ArrayList<Player> targetList = new ArrayList();
    ArrayList<String> invalidList = new ArrayList();
    if (name.equalsIgnoreCase("all"))
    {
      for (Player cur : UtilServer.getPlayers()) {
        targetList.add(cur);
      }
    }
    else
    {
      String[] playerTokens = name.split(",");
      
      for (String curName : playerTokens)
      {
        Player target = (Player)UtilPlayer.matchOnline(null, name, false);
        
        if (target != null)
        {
          targetList.add(target);
        }
        else
        {
          invalidList.add(curName);
        }
      }
    }
    
    if (!invalidList.isEmpty())
    {
      String out = ChatColor.RED + "[C] " + ChatColor.YELLOW + "Invalid Targets:";
      for (String cur : invalidList)
      {
        out = out + " '" + cur + "'";
      }
      caller.sendMessage(out);
    }
    

    if (targetList.isEmpty())
    {
      caller.sendMessage(ChatColor.RED + "[C] " + ChatColor.YELLOW + "No Valid Targets Listed.");
      return;
    }
    

    if (eMap.isEmpty())
    {
      caller.sendMessage(ChatColor.RED + "[C] " + ChatColor.YELLOW + "No Valid Effects Listed.");
      return;
    }
    



    try
    {
      double duration = Double.parseDouble(durationString);
      int strength = Integer.parseInt(strengthString);
      
      if (duration <= 0.0D)
      {
        caller.sendMessage(ChatColor.RED + "[C] " + ChatColor.YELLOW + "Invalid Effect Duration.");
        return;
      }
      
      if (strength < 0)
      {
        caller.sendMessage(ChatColor.RED + "[C] " + ChatColor.YELLOW + "Invalid Effect Strength.");
        return;
      }
    }
    catch (Exception ex)
    {
      caller.sendMessage(ChatColor.RED + "[C] " + ChatColor.YELLOW + "Invalid Effect Duration/Strength."); return;
    }
    
    int strength;
    double duration;
    caller.sendMessage(ChatColor.RED + "[C] " + ChatColor.YELLOW + "Applying Effect(s) to Target(s).");
    Iterator localIterator2; for (??? = targetList.iterator(); ((Iterator)???).hasNext(); 
        
        localIterator2.hasNext())
    {
      Player curPlayer = (Player)((Iterator)???).next();
      
      localIterator2 = this._effectMap.keySet().iterator(); continue;String cur = (String)localIterator2.next();
      
      addEffect(curPlayer, cur, (PotionEffectType)this._effectMap.get(cur), duration, strength, true, extend);
    }
  }
  


  public boolean addEffect(LivingEntity target, String effectName, PotionEffectType type, double duration, int strength, boolean inform, boolean extend)
  {
    int oldDur = 0;
    
    if (target.hasPotionEffect(type))
    {
      for (PotionEffect cur : target.getActivePotionEffects())
      {
        if (cur.getType().equals(type))
        {
          if (cur.getAmplifier() > strength) {
            return true;
          }
          if (extend) {
            oldDur += cur.getDuration();
          }
        }
      }
      
      target.removePotionEffect(type);
    }
    

    target.addPotionEffect(new PotionEffect(type, (int)(duration * 20.0D) + oldDur, strength), true);
    
    if ((inform) && ((target instanceof Player)))
    {
      Player tPlayer = (Player)target;
      UtilPlayer.message(tPlayer, F.main("Condition", "You received " + 
        F.elem(new StringBuilder(String.valueOf(effectName)).append(" ").append(strength + 1).toString()) + 
        " for " + F.time(new StringBuilder().append(UtilMath.trim(1, duration * 20.0D)).toString()) + " Seconds."));
    }
    

    return false;
  }
}
