package mineplex.minecraft.game.classcombat.Skill;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import mineplex.core.MiniPlugin;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.donation.repository.GameSalesPackageToken;
import mineplex.core.energy.Energy;
import mineplex.core.movement.Movement;
import mineplex.core.projectile.ProjectileManager;
import mineplex.core.teleport.Teleport;
import mineplex.minecraft.game.classcombat.Class.IPvpClass;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.Assassin.Assassin;
import mineplex.minecraft.game.classcombat.Skill.Assassin.Blink;
import mineplex.minecraft.game.classcombat.Skill.Assassin.ComboAttack;
import mineplex.minecraft.game.classcombat.Skill.Assassin.Evade;
import mineplex.minecraft.game.classcombat.Skill.Assassin.MarkedForDeath;
import mineplex.minecraft.game.classcombat.Skill.Assassin.SmokeArrow;
import mineplex.minecraft.game.classcombat.Skill.Brute.Intimidation;
import mineplex.minecraft.game.classcombat.Skill.Brute.SeismicSlam;
import mineplex.minecraft.game.classcombat.Skill.Global.BreakFall;
import mineplex.minecraft.game.classcombat.Skill.Global.Fitness;
import mineplex.minecraft.game.classcombat.Skill.Global.Recharge;
import mineplex.minecraft.game.classcombat.Skill.Knight.BullsCharge;
import mineplex.minecraft.game.classcombat.Skill.Knight.Fortitude;
import mineplex.minecraft.game.classcombat.Skill.Knight.Vengeance;
import mineplex.minecraft.game.classcombat.Skill.Mage.Blizzard;
import mineplex.minecraft.game.classcombat.Skill.Mage.IcePrison;
import mineplex.minecraft.game.classcombat.Skill.Mage.Inferno;
import mineplex.minecraft.game.classcombat.Skill.Mage.Mage;
import mineplex.minecraft.game.classcombat.Skill.Ranger.HealingShot;
import mineplex.minecraft.game.classcombat.Skill.Ranger.Longshot;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Chicken.ChickenForm;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider.SpinWeb;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid.Construction;
import mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid.SquidForm;
import mineplex.minecraft.game.classcombat.Skill.repository.SkillRepository;
import mineplex.minecraft.game.classcombat.Skill.repository.token.SkillToken;
import mineplex.minecraft.game.core.IRelation;
import mineplex.minecraft.game.core.combat.CombatManager;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.DamageManager;
import mineplex.minecraft.game.core.fire.Fire;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillFactory extends MiniPlugin implements ISkillFactory
{
  private DamageManager _damageManager;
  private IRelation _relation;
  private CombatManager _combatManager;
  private ConditionManager _conditionManager;
  private ProjectileManager _projectileManager;
  private BlockRestore _blockRestore;
  private Fire _fire;
  private Movement _movement;
  private Teleport _teleport;
  private Energy _energy;
  private SkillRepository _repository;
  private HashMap<String, Skill> _skillMap;
  private HashMap<Integer, ISkill> _skillSalesPackageMap;
  
  public SkillFactory(JavaPlugin plugin, DamageManager damageManager, IRelation relation, CombatManager combatManager, ConditionManager conditionManager, ProjectileManager projectileManager, BlockRestore blockRestore, Fire fire, Movement movement, Teleport teleport, Energy energy, String webAddress)
  {
    super("Skill Factory", plugin);
    
    this._repository = new SkillRepository(webAddress);
    this._damageManager = damageManager;
    this._relation = relation;
    this._combatManager = combatManager;
    this._conditionManager = conditionManager;
    this._projectileManager = projectileManager;
    this._blockRestore = blockRestore;
    this._fire = fire;
    this._movement = movement;
    this._teleport = teleport;
    this._energy = energy;
    this._skillMap = new HashMap();
    this._skillSalesPackageMap = new HashMap();
    
    PopulateSkills();
  }
  
  public ConditionManager Condition()
  {
    return this._conditionManager;
  }
  
  public Teleport Teleport()
  {
    return this._teleport;
  }
  
  public Energy Energy()
  {
    return this._energy;
  }
  
  private void PopulateSkills()
  {
    this._skillMap.clear();
    
    AddAssassin();
    AddBrute();
    AddKnight();
    AddMage();
    AddRanger();
    
    AddGlobal();
    
    for (Skill skill : this._skillMap.values()) {
      GetPlugin().getServer().getPluginManager().registerEvents(skill, GetPlugin());
    }
    List<SkillToken> skillTokens = new java.util.ArrayList();
    int i;
    for (Iterator localIterator2 = this._skillMap.values().iterator(); localIterator2.hasNext(); 
        
        i < 1)
    {
      Skill skill = (Skill)localIterator2.next();
      
      i = 0; continue;
      
      SkillToken skillToken = new SkillToken();
      
      skillToken.Name = skill.GetName();
      skillToken.Level = Integer.valueOf(i + 1);
      skillToken.SalesPackage = new GameSalesPackageToken();
      skillToken.SalesPackage.Gems = Integer.valueOf(2000);
      
      skillTokens.add(skillToken);i++;
    }
    










    for (SkillToken skillToken : this._repository.GetSkills(skillTokens))
    {
      if (this._skillMap.containsKey(skillToken.Name))
      {
        Skill skill = (Skill)this._skillMap.get(skillToken.Name);
        this._skillSalesPackageMap.put(skillToken.SalesPackage.GameSalesPackageId, skill);
        ((Skill)this._skillMap.get(skillToken.Name)).Update(skillToken);
      }
    }
  }
  

  public void AddGlobal()
  {
    AddSkill(new BreakFall(this, "Break Fall", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Global.Resistance(this, "Resistance", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Global.Cooldown(this, "Quick Recovery", IPvpClass.ClassType.Global, ISkill.SkillType.GlobalPassive, 1, 3));
    

    AddSkill(new Fitness(this, "Mana Pool", IPvpClass.ClassType.Mage, ISkill.SkillType.GlobalPassive, 1, 3));
    AddSkill(new Recharge(this, "Mana Regeneration", IPvpClass.ClassType.Mage, ISkill.SkillType.GlobalPassive, 1, 3));
    
    AddSkill(new Fitness(this, "Fitness", IPvpClass.ClassType.Assassin, ISkill.SkillType.GlobalPassive, 1, 3));
    AddSkill(new Recharge(this, "Rest", IPvpClass.ClassType.Assassin, ISkill.SkillType.GlobalPassive, 1, 3));
  }
  




  public void AddAssassin()
  {
    AddSkill(new Assassin(this, "Assassin Class", IPvpClass.ClassType.Assassin, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new Evade(this, "Evade", IPvpClass.ClassType.Assassin, ISkill.SkillType.Sword, 
      1, 4, 
      40, -2, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new Blink(this, "Blink", IPvpClass.ClassType.Assassin, ISkill.SkillType.Axe, 
      1, 4, 
      80, -4, 
      12000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Assassin.Flash(this, "Flash", IPvpClass.ClassType.Assassin, ISkill.SkillType.Axe, 
      1, 4, 
      30, -2, 
      0L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Assassin.Leap(this, "Leap", IPvpClass.ClassType.Assassin, ISkill.SkillType.Axe, 
      1, 4, 
      40, -2, 
      11500L, -1500L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new MarkedForDeath(this, "Marked for Death", IPvpClass.ClassType.Assassin, ISkill.SkillType.Bow, 
      1, 4, 
      40, 0, 
      20000L, 0L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new SmokeArrow(this, "Smoke Arrow", IPvpClass.ClassType.Assassin, ISkill.SkillType.Bow, 
      1, 4, 
      40, 0, 
      20000L, 0L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Assassin.SilencingArrow(this, "Silencing Arrow", IPvpClass.ClassType.Assassin, ISkill.SkillType.Bow, 
      1, 4, 
      40, 0, 
      20000L, 0L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    


    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Assassin.SmokeBomb(this, "Smoke Bomb", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveA, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Assassin.Recall(this, "Recall", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveA, 1, 3));
    


    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Assassin.ShockingStrikes(this, "Shocking Strikes", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveB, 1, 3));
    AddSkill(new ComboAttack(this, "Combo Attack", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveB, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Assassin.ViperStrikes(this, "Viper Strikes", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveB, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Assassin.BackStab(this, "Backstab", IPvpClass.ClassType.Assassin, ISkill.SkillType.PassiveB, 1, 3));
  }
  
  public void AddBrute()
  {
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Brute.Brute(this, "Brute Class", IPvpClass.ClassType.Brute, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Brute.DwarfToss(this, "Dwarf Toss", IPvpClass.ClassType.Brute, ISkill.SkillType.Sword, 
      1, 5, 
      0, 0, 
      20000L, -2000L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Brute.FleshHook(this, "Flesh Hook", IPvpClass.ClassType.Brute, ISkill.SkillType.Sword, 
      1, 5, 
      0, 0, 
      15000L, -1000L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Brute.BlockToss(this, "Block Toss", IPvpClass.ClassType.Brute, ISkill.SkillType.Sword, 1, 5));
    

    AddSkill(new SeismicSlam(this, "Seismic Slam", IPvpClass.ClassType.Brute, ISkill.SkillType.Axe, 
      1, 5, 
      0, 0, 
      30000L, -3000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Brute.Takedown(this, "Takedown", IPvpClass.ClassType.Brute, ISkill.SkillType.Axe, 
      1, 5, 
      0, 0, 
      30000L, -3000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Brute.Stampede(this, "Stampede", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveA, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Brute.Bloodlust(this, "Bloodlust", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveA, 1, 3));
    AddSkill(new Intimidation(this, "Intimidation", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveA, 1, 3));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Brute.CripplingBlow(this, "Crippling Blow", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveB, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Brute.Colossus(this, "Colossus", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveB, 2, 1));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Brute.Overwhelm(this, "Overwhelm", IPvpClass.ClassType.Brute, ISkill.SkillType.PassiveB, 1, 3));
  }
  
  public void AddKnight()
  {
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Knight.Knight(this, "Knight Class", IPvpClass.ClassType.Knight, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Knight.HiltSmash(this, "Hilt Smash", IPvpClass.ClassType.Knight, ISkill.SkillType.Sword, 
      1, 5, 
      0, 0, 
      15000L, -1000L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Knight.Riposte(this, "Riposte", IPvpClass.ClassType.Knight, ISkill.SkillType.Sword, 
      1, 5, 
      0, 0, 
      10000L, -1000L, false, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Knight.DefensiveStance(this, "Defensive Stance", IPvpClass.ClassType.Knight, ISkill.SkillType.Sword, 
      2, 1, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    


    AddSkill(new BullsCharge(this, "Bulls Charge", IPvpClass.ClassType.Knight, ISkill.SkillType.Axe, 
      1, 5, 
      0, 0, 
      10000L, 1000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Knight.HoldPosition(this, "Hold Position", IPvpClass.ClassType.Knight, ISkill.SkillType.Axe, 
      1, 5, 
      0, 0, 
      16000L, 2000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Knight.ShieldSmash(this, "Shield Smash", IPvpClass.ClassType.Knight, ISkill.SkillType.Axe, 
      1, 5, 
      0, 0, 
      8000L, -1000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    


    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Knight.Cleave(this, "Cleave", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveA, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Knight.Swordsmanship(this, "Swordsmanship", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveA, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Knight.Deflection(this, "Deflection", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveA, 1, 3));
    

    AddSkill(new Vengeance(this, "Vengeance", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveB, 1, 3));
    AddSkill(new Fortitude(this, "Fortitude", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveB, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Knight.LevelField(this, "Level Field", IPvpClass.ClassType.Knight, ISkill.SkillType.PassiveB, 1, 3));
  }
  
  public void AddMage()
  {
    AddSkill(new Mage(this, "Mage Class", IPvpClass.ClassType.Mage, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new Blizzard(this, "Blizzard", IPvpClass.ClassType.Mage, ISkill.SkillType.Sword, 
      1, 5, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Inferno(this, "Inferno", IPvpClass.ClassType.Mage, ISkill.SkillType.Sword, 
      1, 5, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.Rupture(this, "Rupture", IPvpClass.ClassType.Mage, ISkill.SkillType.Sword, 
      1, 5, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.FireBlast(this, "Fire Blast", IPvpClass.ClassType.Mage, ISkill.SkillType.Axe, 
      1, 5, 
      60, -3, 
      11000L, -1000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new IcePrison(this, "Ice Prison", IPvpClass.ClassType.Mage, ISkill.SkillType.Axe, 
      1, 5, 
      60, -3, 
      21000L, -1000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.LightningOrb(this, "Lightning Orb", IPvpClass.ClassType.Mage, ISkill.SkillType.Axe, 
      1, 5, 
      60, -2, 
      11000L, -1000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.Fissure(this, "Fissure", IPvpClass.ClassType.Mage, ISkill.SkillType.Axe, 
      1, 5, 
      60, -3, 
      11000L, -1000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    













    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.ArcticArmor(this, "Arctic Armor", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveA, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.Immolate(this, "Immolate", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveA, 2, 1));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.Void(this, "Void", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveA, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.LifeBonds(this, "Life Bonds", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveA, 1, 3));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.GlacialBlade(this, "Glacial Blade", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveB, 
      1, 3, 
      16, -2, 
      1300L, -300L, false, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.MagmaBlade(this, "Magma Blade", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveB, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Mage.NullBlade(this, "Null Blade", IPvpClass.ClassType.Mage, ISkill.SkillType.PassiveB, 1, 3));
  }
  

  public void AddRanger()
  {
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.Ranger(this, "Ranger Class", IPvpClass.ClassType.Ranger, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.Disengage(this, "Disengage", IPvpClass.ClassType.Ranger, ISkill.SkillType.Sword, 
      1, 4, 
      0, 0, 
      18000L, -2000L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.WolfsPounce(this, "Wolfs Pounce", IPvpClass.ClassType.Ranger, ISkill.SkillType.Sword, 1, 4));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.Agility(this, "Agility", IPvpClass.ClassType.Ranger, ISkill.SkillType.Axe, 
      1, 4, 
      0, 0, 
      20000L, 2000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.WolfsFury(this, "Wolfs Fury", IPvpClass.ClassType.Ranger, ISkill.SkillType.Axe, 
      1, 4, 
      0, 0, 
      20000L, 2000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new HealingShot(this, "Healing Shot", IPvpClass.ClassType.Ranger, ISkill.SkillType.Bow, 
      1, 4, 
      0, 0, 
      20000L, -2000L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.IncendiaryShot(this, "Incendiary Shot", IPvpClass.ClassType.Ranger, ISkill.SkillType.Bow, 
      1, 4, 
      0, 0, 
      20000L, -2000L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.NapalmShot(this, "Napalm Shot", IPvpClass.ClassType.Ranger, ISkill.SkillType.Bow, 
      1, 4, 
      0, 0, 
      30000L, -2000L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.PinDown(this, "Pin Down", IPvpClass.ClassType.Ranger, ISkill.SkillType.Bow, 
      1, 4, 
      0, 0, 
      13000L, -1000L, true, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.RopedArrow(this, "Roped Arrow", IPvpClass.ClassType.Ranger, ISkill.SkillType.Bow, 
      1, 4, 
      0, 0, 
      10000L, -1500L, false, 
      new Material[] { Material.BOW }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.Barrage(this, "Barrage", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveA, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.Overcharge(this, "Overcharge", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveA, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.VitalitySpores(this, "Vitality Spores", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveA, 1, 3));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.BarbedArrows(this, "Barbed Arrows", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveB, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.HeavyArrows(this, "Heavy Arrows", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveB, 1, 3));
    
    AddSkill(new Longshot(this, "Longshot", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveB, 1, 3));
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Ranger.Sharpshooter(this, "Sharpshooter", IPvpClass.ClassType.Ranger, ISkill.SkillType.PassiveB, 1, 3));
  }
  

  public void AddShifter()
  {
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.Shifter(this, "Shifter Class", IPvpClass.ClassType.Shifter, ISkill.SkillType.Class, 0, 1));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.TreeShift(this, "Tree Shift", IPvpClass.ClassType.Shifter, ISkill.SkillType.Axe, 
      0, 1, 
      40, 0, 
      4000L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.Polysmash(this, "Polysmash", IPvpClass.ClassType.Shifter, ISkill.SkillType.Sword, 
      0, 1, 
      30, 0, 
      16000L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    

    AddSkill(new ChickenForm(this, "Chicken Form", IPvpClass.ClassType.Shifter, ISkill.SkillType.PassiveB, 0, 5));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Chicken.Flap(this, "Flap", IPvpClass.ClassType.Shifter, ISkill.SkillType.Sword, 
      0, 1, 
      5, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    



    AddSkill(new SquidForm(this, "Squid Form", IPvpClass.ClassType.Shifter, ISkill.SkillType.PassiveB, 0, 5));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Squid.Propel(this, "Propel", IPvpClass.ClassType.Shifter, ISkill.SkillType.Sword, 
      0, 1, 
      12, 0, 
      250L, 0L, false, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new Construction(this, "Ice Construction", IPvpClass.ClassType.Shifter, ISkill.SkillType.Axe, 
      0, 1, 
      8, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK }));
    


    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem.GolemForm(this, "Magnetic Golem Form", IPvpClass.ClassType.Shifter, ISkill.SkillType.PassiveA, 0, 5));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem.MagneticPull(this, "Magnetic Pull", IPvpClass.ClassType.Shifter, ISkill.SkillType.Sword, 
      0, 1, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Golem.MagneticRepel(this, "Magnetic Repel", IPvpClass.ClassType.Shifter, ISkill.SkillType.Axe, 
      0, 1, 
      60, 0, 
      30000L, -3000L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    


    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider.SpiderForm(this, "Spitting Spider Form", IPvpClass.ClassType.Shifter, ISkill.SkillType.PassiveA, 0, 5));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider.Needler(this, "Needler", IPvpClass.ClassType.Shifter, ISkill.SkillType.Sword, 
      0, 1, 
      0, 0, 
      0L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD }, 
      new Action[] { Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new SpinWeb(this, "Spin Web", IPvpClass.ClassType.Shifter, ISkill.SkillType.Axe, 
      0, 1, 
      20, -1, 
      0L, 0L, true, 
      new Material[] { Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.RIGHT_CLICK_BLOCK }));
    
    AddSkill(new mineplex.minecraft.game.classcombat.Skill.Shifter.Forms.Spider.Pounce(this, "Pounce", IPvpClass.ClassType.Shifter, ISkill.SkillType.PassiveB, 
      0, 1, 
      20, 0, 
      6000L, 0L, true, 
      new Material[] { Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD, Material.IRON_AXE, Material.GOLD_AXE, Material.DIAMOND_AXE }, 
      new Action[] { Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK }));
  }
  
  public ISkill GetSkillBySalesPackageId(int id)
  {
    return (ISkill)this._skillSalesPackageMap.get(Integer.valueOf(id));
  }
  
  public Skill GetSkill(String skillName)
  {
    return (Skill)this._skillMap.get(skillName);
  }
  
  public Collection<Skill> GetAllSkills()
  {
    return this._skillMap.values();
  }
  
  public void AddSkill(Skill skill)
  {
    this._skillMap.put(skill.GetName(), skill);
  }
  
  public void RemoveSkill(String skillName, String defaultReplacement)
  {
    if (skillName == null)
    {
      System.out.println("[Skill Factory] Remove Skill: Remove Skill NULL [" + skillName + "].");
      return;
    }
    
    Skill remove = (Skill)this._skillMap.get(skillName);
    if (remove == null)
    {
      System.out.println("[Skill Factory] Remove Skill: Remove Skill NULL [" + skillName + "].");
      return;
    }
    
    Skill replacement = null;
    if (defaultReplacement != null)
    {
      replacement = (Skill)this._skillMap.get(defaultReplacement);
      if (replacement == null)
      {
        System.out.println("[Skill Factory] Remove Skill: Replacement Skill NULL [" + defaultReplacement + "].");
        return;
      }
    }
    

    this._skillMap.remove(remove.GetName());
    org.bukkit.event.HandlerList.unregisterAll(remove);
    
    System.out.println("Skill Factory: Removed " + remove.GetName() + " from SkillMap.");
  }
  

  public List<ISkill> GetGlobalSkillsFor(IPvpClass gameClass)
  {
    List<ISkill> skills = new LinkedList();
    
    for (ISkill cur : this._skillMap.values())
    {
      if ((cur.GetSkillType() == ISkill.SkillType.GlobalPassive) && ((cur.GetClassType() == IPvpClass.ClassType.Global) || ((gameClass != null) && (cur.GetClassType() == gameClass.GetType()))))
      {
        skills.add(cur);
      }
    }
    
    return skills;
  }
  

  public List<ISkill> GetSkillsFor(IPvpClass gameClass)
  {
    List<ISkill> skills = new LinkedList();
    
    for (ISkill cur : this._skillMap.values())
    {
      if ((cur.GetClassType() == gameClass.GetType()) && (cur.GetSkillType() != ISkill.SkillType.GlobalPassive))
      {
        skills.add(cur);
      }
    }
    
    return skills;
  }
  


  public HashMap<ISkill, Integer> GetDefaultSkillsFor(IPvpClass classType)
  {
    HashMap<ISkill, Integer> skills = new HashMap();
    if (classType.GetType() == IPvpClass.ClassType.Knight)
    {
      AddSkill(skills, "Knight Class", 1);
      
      AddSkill(skills, "Bulls Charge", 1);
      AddSkill(skills, "Riposte", 1);
      AddSkill(skills, "Deflection", 1);
      AddSkill(skills, "Vengeance", 1);
      
      AddSkill(skills, "Resistance", 1);

    }
    else if (classType.GetType() == IPvpClass.ClassType.Ranger)
    {
      AddSkill(skills, "Ranger Class", 1);
      
      AddSkill(skills, "Napalm Shot", 1);
      AddSkill(skills, "Agility", 1);
      AddSkill(skills, "Disengage", 1);
      AddSkill(skills, "Barrage", 1);
      AddSkill(skills, "Barbed Arrows", 1);
      
      AddSkill(skills, "Quick Recovery", 1);

    }
    else if (classType.GetType() == IPvpClass.ClassType.Brute)
    {
      AddSkill(skills, "Brute Class", 1);
      
      AddSkill(skills, "Seismic Slam", 1);
      AddSkill(skills, "Dwarf Toss", 1);
      AddSkill(skills, "Stampede", 1);
      AddSkill(skills, "Crippling Blow", 1);
      
      AddSkill(skills, "Resistance", 1);

    }
    else if (classType.GetType() == IPvpClass.ClassType.Assassin)
    {
      AddSkill(skills, "Assassin Class", 1);
      
      AddSkill(skills, "Blink", 1);
      AddSkill(skills, "Evade", 1);
      AddSkill(skills, "Toxic Arrow", 1);
      AddSkill(skills, "Smoke Bomb", 1);
      AddSkill(skills, "Repeated Strikes", 1);
      
      AddSkill(skills, "Break Fall", 1);

    }
    else if (classType.GetType() == IPvpClass.ClassType.Mage)
    {
      AddSkill(skills, "Mage Class", 1);
      
      AddSkill(skills, "Freezing Blast", 1);
      AddSkill(skills, "Blizzard", 1);
      AddSkill(skills, "Arctic Armor", 1);
      AddSkill(skills, "Glacial Blade", 1);
      
      AddSkill(skills, "Fitness", 1);

    }
    else if (classType.GetType() == IPvpClass.ClassType.Shifter)
    {
      AddSkill(skills, "Shifter Class", 1);
      
      AddSkill(skills, "Tree Shift", 1);
      AddSkill(skills, "Polysmash", 1);
      AddSkill(skills, "Golem Form", 1);
      AddSkill(skills, "Chicken Form", 1);
      
      AddSkill(skills, "Quick Recovery", 1);
    }
    
    skills.remove(null);
    
    return skills;
  }
  
  public void AddSkill(HashMap<ISkill, Integer> skills, String skillName, int level)
  {
    ISkill skill = GetSkill(skillName);
    
    if (skill == null) {
      return;
    }
    skills.put(skill, Integer.valueOf(level));
  }
  
  public Movement Movement()
  {
    return this._movement;
  }
  
  public DamageManager Damage()
  {
    return this._damageManager;
  }
  
  public CombatManager Combat()
  {
    return this._combatManager;
  }
  
  public ProjectileManager Projectile()
  {
    return this._projectileManager;
  }
  
  public BlockRestore BlockRestore()
  {
    return this._blockRestore;
  }
  
  public Fire Fire()
  {
    return this._fire;
  }
  
  public IRelation Relation()
  {
    return this._relation;
  }
}
