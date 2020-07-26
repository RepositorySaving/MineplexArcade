package nautilus.game.arcade.game.games.survivalgames;

import java.io.PrintStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.antistack.AntiStack;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilFirework;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.common.util.UtilWorld;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.survivalgames.kit.KitBrawler;
import nautilus.game.arcade.game.games.survivalgames.kit.KitNecromancer;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

public class SurvivalGames extends SoloGame
{
  private HashSet<Location> _openedChest = new HashSet();
  private ArrayList<ItemStack> _baseChestLoot = new ArrayList();
  private ArrayList<ItemStack> _superChestLoot = new ArrayList();
  

  private HashMap<Entity, Player> _tntMap = new HashMap();
  private HashSet<Location> _placedBlocks = new HashSet();
  
  private Location _spawn;
  
  private int _maxSpreadRate = 120;
  private ArrayList<Location> _redLocations = new ArrayList();
  private int _spreadType = 0;
  private String _spreadName = "";
  private boolean _ignoreLiquids = true;
  private ArrayList<java.util.Map.Entry<Integer, Integer>> _spreadTypeBlocks;
  private HashMap<Player, Long> _redOutTime = new HashMap();
  
  private HashMap<Integer, HashMap<Integer, HashSet<Integer>>> _redMap = new HashMap();
  private HashMap<Player, ArrayList<ChunkChange>> _redChunks = new HashMap();
  

  private ArrayList<Location> _supplyLocations = new ArrayList();
  private Location _supplyCurrent = null;
  private Location _supplyEffect = null;
  private ArrayList<Block> _supplyChests = new ArrayList();
  

  private boolean _deathmatchCountdown = false;
  private boolean _deathmatchLive = false;
  private long _deathmatchTime = 0L;
  

  private long totalTime = 0L;
  






















  public SurvivalGames(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.SurvivalGames, new nautilus.game.arcade.kit.Kit[] {new nautilus.game.arcade.game.games.survivalgames.kit.KitAxeman(manager), new nautilus.game.arcade.game.games.survivalgames.kit.KitKnight(manager), new nautilus.game.arcade.game.games.survivalgames.kit.KitArcher(manager), new KitBrawler(manager), new nautilus.game.arcade.game.games.survivalgames.kit.KitAssassin(manager), new nautilus.game.arcade.game.games.survivalgames.kit.KitBeastmaster(manager), new nautilus.game.arcade.game.games.survivalgames.kit.KitBomber(manager), new KitNecromancer(manager) }, new String[] {"Search for chests to find loot", "Slaughter your opponents", "Stay away from the Deep Freeze!", "Last tribute alive wins!" });
    

    this._help = 
      new String[] {
      C.cGreen + "Use a Compass to find and kill enemies!", 
      C.cAqua + "Crouch to become invisible to Compass tracking!", 
      C.cGreen + "Avoid the Deep Freeze at all costs!", 
      C.cAqua + "Use TNT & Tripwires to make traps!", 
      C.cGreen + "You lose Speed 2 at start of game if you attack.", 
      C.cAqua + "Avoid enemies who have better gear than you!", 
      C.cGreen + "Compass finds Supply Drops during night time.", 
      C.cAqua + "Compass finds Players during day time." };
    

    this.Manager.GetAntiStack().SetEnabled(false);
    
    this.WorldTimeSet = 0;
    this.WorldBoundaryKill = false;
    
    this.SpawnDistanceRequirement = 48;
    
    this.DamageSelf = true;
    this.DamageTeamSelf = true;
    
    this.DeathDropItems = true;
    
    this.ItemDrop = true;
    this.ItemPickup = true;
    
    this.CompassEnabled = false;
    

    this.BlockBreakAllow.add(Integer.valueOf(46));
    this.BlockPlaceAllow.add(Integer.valueOf(46));
    
    this.BlockBreakAllow.add(Integer.valueOf(30));
    this.BlockPlaceAllow.add(Integer.valueOf(30));
    
    this.BlockBreakAllow.add(Integer.valueOf(132));
    this.BlockPlaceAllow.add(Integer.valueOf(132));
    
    this.BlockBreakAllow.add(Integer.valueOf(131));
    this.BlockPlaceAllow.add(Integer.valueOf(131));
    
    this.BlockBreakAllow.add(Integer.valueOf(55));
    this.BlockPlaceAllow.add(Integer.valueOf(55));
    
    this.BlockBreakAllow.add(Integer.valueOf(72));
    this.BlockPlaceAllow.add(Integer.valueOf(72));
    
    this.BlockBreakAllow.add(Integer.valueOf(69));
    this.BlockPlaceAllow.add(Integer.valueOf(69));
    
    this.BlockBreakAllow.add(Integer.valueOf(18));
    

    this._spreadType = 1;
    
    this._spreadTypeBlocks = new ArrayList();
    
    if (this._spreadType == 0)
    {
      this._spreadName = "Red";
      this._spreadTypeBlocks.add(new AbstractMap.SimpleEntry(Integer.valueOf(159), Integer.valueOf(14)));
    }
    else if (this._spreadType == 1)
    {
      this._spreadName = "Deep Freeze";
      this._ignoreLiquids = false;
      this._spreadTypeBlocks.add(new AbstractMap.SimpleEntry(Integer.valueOf(78), Integer.valueOf(0)));
      this._spreadTypeBlocks.add(new AbstractMap.SimpleEntry(Integer.valueOf(79), Integer.valueOf(0)));
      this._spreadTypeBlocks.add(new AbstractMap.SimpleEntry(Integer.valueOf(80), Integer.valueOf(0)));
      this._spreadTypeBlocks.add(new AbstractMap.SimpleEntry(Integer.valueOf(49), Integer.valueOf(0)));
    }
    else if (this._spreadType == 2)
    {
      this._spreadName = "Nether Corruption";
      this._ignoreLiquids = false;
      this._spreadTypeBlocks.add(new AbstractMap.SimpleEntry(Integer.valueOf(49), Integer.valueOf(0)));
      this._spreadTypeBlocks.add(new AbstractMap.SimpleEntry(Integer.valueOf(87), Integer.valueOf(0)));
      this._spreadTypeBlocks.add(new AbstractMap.SimpleEntry(Integer.valueOf(88), Integer.valueOf(0)));
      this._spreadTypeBlocks.add(new AbstractMap.SimpleEntry(Integer.valueOf(89), Integer.valueOf(0)));
      this._spreadTypeBlocks.add(new AbstractMap.SimpleEntry(Integer.valueOf(153), Integer.valueOf(0)));
    }
    
    System.out.println("===================");
    System.out.println("CREEP TYPE: " + this._spreadName);
    System.out.println("===================");
  }
  


  public double GetKillsGems(Player killer, Player killed, boolean assist)
  {
    return 4.0D;
  }
  

  public void ParseData()
  {
    this._spawn = UtilWorld.averageLocation(((GameTeam)GetTeamList().get(0)).GetSpawns());
    
    for (Location loc : ((GameTeam)GetTeamList().get(0)).GetSpawns()) {
      loc.setYaw(UtilAlg.GetYaw(UtilAlg.getTrajectory(loc, this._spawn)));
    }
    CreateChestCraftEnchant();
    
    this._supplyLocations = this.WorldData.GetDataLocs("WHITE");
    for (Location loc : this._supplyLocations) {
      loc.getBlock().setType(Material.GLASS);
    }
    if (!this.WorldData.GetCustomLocs("VARS").isEmpty())
    {
      this._maxSpreadRate = ((Location)this.WorldData.GetCustomLocs("VARS").get(0)).getBlockX();
      System.out.println("Spread Rate: " + this._maxSpreadRate);
    }
  }
  
  private void CreateChestCraftEnchant()
  {
    ArrayList<Location> chests = this.WorldData.GetCustomLocs("54");
    
    System.out.println("Map Chest Locations: " + chests.size());
    

    System.out.println("Enchanting Tables: " + Math.min(5, chests.size()));
    for (int i = 0; (i < 5) && (!chests.isEmpty()); i++)
    {
      Location loc = (Location)chests.remove(UtilMath.r(chests.size()));
      loc.getBlock().setType(Material.ENCHANTMENT_TABLE);
    }
    

    System.out.println("Crafting Benches: " + Math.min(10, chests.size()));
    for (int i = 0; (i < 10) && (!chests.isEmpty()); i++)
    {
      Location loc = (Location)chests.remove(UtilMath.r(chests.size()));
      loc.getBlock().setType(Material.WORKBENCH);
    }
    
    int spawn = 0;
    

    System.out.println("Chests: " + Math.min(250, chests.size()));
    Location loc; for (int i = 0; (i < 250) && (!chests.isEmpty()); i++)
    {
      loc = (Location)chests.remove(UtilMath.r(chests.size()));
      
      if (UtilMath.offset2d(loc, this._spawn) < 8.0D) {
        spawn++;
      }
    }
    for (Location loc : chests)
    {
      if ((spawn < 10) && (UtilMath.offset(loc, this._spawn) < 8.0D))
      {
        spawn++;
      }
      else
      {
        loc.getBlock().setType(Material.AIR);
      }
    }
  }
  
  @EventHandler
  public void CreateRandomChests(GameStateChangeEvent event) {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    HashSet<Material> ignore = new HashSet();
    
    ignore.add(Material.LEAVES);
    
    int xDiff = this.WorldData.MaxX - this.WorldData.MinX;
    int zDiff = this.WorldData.MaxZ - this.WorldData.MinZ;
    
    int done = 0;
    
    while (done < 40)
    {

      Block block = UtilBlock.getHighest(this.WorldData.World, this.WorldData.MinX + UtilMath.r(xDiff), this.WorldData.MinZ + UtilMath.r(zDiff), ignore);
      
      if ((UtilBlock.airFoliage(block)) && (UtilBlock.solid(block.getRelative(BlockFace.DOWN))))
      {

        block.setTypeIdAndData(54, (byte)UtilMath.r(4), true);
        done++;
      }
    }
  }
  
  @EventHandler
  private void OpenChest(PlayerInteractEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (event.getClickedBlock() == null) {
      return;
    }
    if (!(event.getClickedBlock().getState() instanceof Chest)) {
      return;
    }
    if (GetState() != Game.GameState.Live) {
      return;
    }
    if (this._openedChest.contains(event.getClickedBlock().getLocation())) {
      return;
    }
    this._openedChest.add(event.getClickedBlock().getLocation());
    
    Chest chest = (Chest)event.getClickedBlock().getState();
    
    chest.getBlockInventory().clear();
    
    int count = 2;
    if (Math.random() > 0.5D) count++;
    if (Math.random() > 0.65D) count++;
    if (Math.random() > 0.8D) count++;
    if (Math.random() > 0.95D) { count++;
    }
    if (UtilMath.offset(chest.getLocation(), this._spawn) < 8.0D) {
      count += 3;
    }
    if ((GetKit(event.getPlayer()) instanceof nautilus.game.arcade.game.games.survivalgames.kit.KitLooter))
    {
      count += UtilMath.r(3);
    }
    
    if (this._supplyChests.contains(event.getClickedBlock()))
    {
      count = 2;
      if (Math.random() > 0.75D) count++;
      if (Math.random() > 0.95D) { count++;
      }
    }
    for (int i = 0; i < count; i++) {
      chest.getBlockInventory().setItem(UtilMath.r(27), GetChestItem(this._supplyChests.contains(event.getClickedBlock())));
    }
    this._supplyChests.remove(event.getClickedBlock());
  }
  


  private ItemStack GetChestItem(boolean superChest)
  {
    if (this._baseChestLoot.isEmpty())
    {

      for (int i = 0; i < 10; i++) this._baseChestLoot.add(new ItemStack(Material.IRON_HELMET));
      for (int i = 0; i < 3; i++) this._baseChestLoot.add(new ItemStack(Material.IRON_CHESTPLATE));
      for (int i = 0; i < 5; i++) this._baseChestLoot.add(new ItemStack(Material.IRON_LEGGINGS));
      for (int i = 0; i < 10; i++) { this._baseChestLoot.add(new ItemStack(Material.IRON_BOOTS));
      }
      for (int i = 0; i < 30; i++) this._baseChestLoot.add(new ItemStack(Material.CHAINMAIL_HELMET));
      for (int i = 0; i < 20; i++) this._baseChestLoot.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
      for (int i = 0; i < 25; i++) this._baseChestLoot.add(new ItemStack(Material.CHAINMAIL_LEGGINGS));
      for (int i = 0; i < 30; i++) { this._baseChestLoot.add(new ItemStack(Material.CHAINMAIL_BOOTS));
      }
      for (int i = 0; i < 40; i++) this._baseChestLoot.add(new ItemStack(Material.GOLD_HELMET));
      for (int i = 0; i < 30; i++) this._baseChestLoot.add(new ItemStack(Material.GOLD_CHESTPLATE));
      for (int i = 0; i < 35; i++) this._baseChestLoot.add(new ItemStack(Material.GOLD_LEGGINGS));
      for (int i = 0; i < 40; i++) { this._baseChestLoot.add(new ItemStack(Material.GOLD_BOOTS));
      }
      for (int i = 0; i < 100; i++) this._baseChestLoot.add(new ItemStack(Material.LEATHER_HELMET));
      for (int i = 0; i < 90; i++) this._baseChestLoot.add(new ItemStack(Material.LEATHER_CHESTPLATE));
      for (int i = 0; i < 85; i++) this._baseChestLoot.add(new ItemStack(Material.LEATHER_LEGGINGS));
      for (int i = 0; i < 100; i++) { this._baseChestLoot.add(new ItemStack(Material.LEATHER_BOOTS));
      }
      
      for (int i = 0; i < 70; i++) this._baseChestLoot.add(new ItemStack(Material.WOOD_AXE));
      for (int i = 0; i < 45; i++) this._baseChestLoot.add(new ItemStack(Material.GOLD_AXE));
      for (int i = 0; i < 35; i++) this._baseChestLoot.add(new ItemStack(Material.STONE_AXE));
      for (int i = 0; i < 15; i++) { this._baseChestLoot.add(new ItemStack(Material.IRON_AXE));
      }
      for (int i = 0; i < 60; i++) this._baseChestLoot.add(new ItemStack(Material.WOOD_SWORD));
      for (int i = 0; i < 35; i++) this._baseChestLoot.add(new ItemStack(Material.GOLD_SWORD));
      for (int i = 0; i < 25; i++) this._baseChestLoot.add(new ItemStack(Material.STONE_SWORD));
      for (int i = 0; i < 8; i++) { this._baseChestLoot.add(new ItemStack(Material.IRON_SWORD));
      }
      for (int i = 0; i < 45; i++) this._baseChestLoot.add(new ItemStack(Material.BOW));
      for (int i = 0; i < 55; i++) { this._baseChestLoot.add(new ItemStack(Material.ARROW, 4));
      }
      for (int i = 0; i < 15; i++) this._baseChestLoot.add(new ItemStack(Material.TNT, 1));
      for (int i = 0; i < 30; i++) { this._baseChestLoot.add(new ItemStack(Material.WEB, 2));
      }
      
      for (int i = 0; i < 40; i++) this._baseChestLoot.add(new ItemStack(Material.MUSHROOM_SOUP));
      for (int i = 0; i < 50; i++) this._baseChestLoot.add(new ItemStack(Material.COOKED_CHICKEN));
      for (int i = 0; i < 80; i++) this._baseChestLoot.add(new ItemStack(Material.RAW_BEEF));
      for (int i = 0; i < 50; i++) this._baseChestLoot.add(new ItemStack(Material.GRILLED_PORK));
      for (int i = 0; i < 100; i++) this._baseChestLoot.add(new ItemStack(Material.BREAD));
      for (int i = 0; i < 40; i++) this._baseChestLoot.add(new ItemStack(Material.PUMPKIN_PIE));
      for (int i = 0; i < 80; i++) this._baseChestLoot.add(new ItemStack(Material.COOKIE));
      for (int i = 0; i < 90; i++) { this._baseChestLoot.add(new ItemStack(Material.ROTTEN_FLESH));
      }
      for (int i = 0; i < 80; i++) { this._baseChestLoot.add(new ItemStack(Material.WHEAT, 6));
      }
      
      for (int i = 0; i < 50; i++) this._baseChestLoot.add(new ItemStack(Material.COMPASS, 1));
      for (int i = 0; i < 25; i++) this._baseChestLoot.add(new ItemStack(Material.EXP_BOTTLE, 1));
      for (int i = 0; i < 50; i++) this._baseChestLoot.add(new ItemStack(Material.GOLD_INGOT, 2));
      for (int i = 0; i < 30; i++) this._baseChestLoot.add(new ItemStack(Material.IRON_INGOT));
      for (int i = 0; i < 5; i++) this._baseChestLoot.add(new ItemStack(Material.DIAMOND));
      for (int i = 0; i < 60; i++) this._baseChestLoot.add(new ItemStack(Material.STICK, 4));
      for (int i = 0; i < 80; i++) this._baseChestLoot.add(new ItemStack(Material.FLINT, 6));
      for (int i = 0; i < 80; i++) this._baseChestLoot.add(new ItemStack(Material.FEATHER, 6));
      for (int i = 0; i < 40; i++) this._baseChestLoot.add(new ItemStack(Material.BOAT));
      for (int i = 0; i < 70; i++) { this._baseChestLoot.add(new ItemStack(Material.FISHING_ROD));
      }
      
      for (int i = 0; i < 45; i++) this._baseChestLoot.add(new ItemStack(Material.PISTON_BASE, 4));
      for (int i = 0; i < 45; i++) this._baseChestLoot.add(new ItemStack(Material.STRING, 4));
      for (int i = 0; i < 45; i++) { this._baseChestLoot.add(new ItemStack(Material.TRIPWIRE_HOOK, 4));
      }
    }
    




















    if (this._superChestLoot.isEmpty())
    {
      for (int i = 0; i < 3; i++) this._superChestLoot.add(new ItemStack(Material.DIAMOND_HELMET));
      for (int i = 0; i < 1; i++) this._superChestLoot.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
      for (int i = 0; i < 2; i++) this._superChestLoot.add(new ItemStack(Material.DIAMOND_LEGGINGS));
      for (int i = 0; i < 3; i++) { this._superChestLoot.add(new ItemStack(Material.DIAMOND_BOOTS));
      }
      for (int i = 0; i < 30; i++) this._superChestLoot.add(new ItemStack(Material.IRON_HELMET));
      for (int i = 0; i < 24; i++) this._superChestLoot.add(new ItemStack(Material.IRON_CHESTPLATE));
      for (int i = 0; i < 27; i++) this._superChestLoot.add(new ItemStack(Material.IRON_LEGGINGS));
      for (int i = 0; i < 30; i++) { this._superChestLoot.add(new ItemStack(Material.IRON_BOOTS));
      }
      for (int i = 0; i < 24; i++) this._superChestLoot.add(new ItemStack(Material.IRON_SWORD));
      for (int i = 0; i < 8; i++) this._superChestLoot.add(new ItemStack(Material.DIAMOND_SWORD));
      for (int i = 0; i < 16; i++) { this._superChestLoot.add(new ItemStack(Material.DIAMOND_AXE));
      }
    }
    
    ItemStack stack = (ItemStack)this._baseChestLoot.get(UtilMath.r(this._baseChestLoot.size()));
    if (superChest) {
      stack = (ItemStack)this._superChestLoot.get(UtilMath.r(this._superChestLoot.size()));
    }
    int amount = 1;
    
    if (stack.getType().getMaxStackSize() > 1) {
      amount = Math.max(1, UtilMath.r(stack.getAmount()));
    }
    if (stack.getTypeId() == 33) {
      return ItemStackFactory.Instance.CreateStack(stack.getTypeId(), (byte)0, amount, "Barricade");
    }
    return ItemStackFactory.Instance.CreateStack(stack.getTypeId(), amount);
  }
  
  @EventHandler
  public void StartEffectApply(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      this.Manager.GetCondition().Factory().Speed("Start Speed", player, player, 30.0D, 1, false, false, false);
      this.Manager.GetCondition().Factory().HealthBoost("Start Health", player, player, 30.0D, 1, false, false, false);
      
      player.setHealth(player.getMaxHealth());
    }
  }
  
  @EventHandler
  public void SpeedRemove(CustomDamageEvent event)
  {
    Player damager = event.GetDamagerPlayer(true);
    if (damager != null) {
      this.Manager.GetCondition().EndCondition(damager, null, "Start Speed");
    }
  }
  
  @EventHandler
  public void ItemSpawn(ItemSpawnEvent event)
  {
    for (Player player : GetPlayers(true)) {
      if (UtilMath.offset(player, event.getEntity()) < 6.0D)
        return;
    }
    event.setCancelled(true);
  }
  
  @EventHandler
  public void RedBorderStart(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    


    for (int x = this.WorldData.MinX; x <= this.WorldData.MaxX; x++)
    {
      Block block = this.WorldData.World.getHighestBlockAt(x, this.WorldData.MinZ);
      while ((!UtilBlock.solid(block)) && (!block.isLiquid()) && (block.getY() > 0))
        block = block.getRelative(BlockFace.DOWN);
      if (block.getY() > 0) {
        this._redLocations.add(block.getLocation());
      }
      block = this.WorldData.World.getHighestBlockAt(x, this.WorldData.MaxZ);
      while ((!UtilBlock.solid(block)) && (!block.isLiquid()) && (block.getY() > 0))
        block = block.getRelative(BlockFace.DOWN);
      if (block.getY() > 0) {
        this._redLocations.add(block.getLocation());
      }
    }
    for (int z = this.WorldData.MinZ; z <= this.WorldData.MaxZ; z++)
    {
      Block block = this.WorldData.World.getHighestBlockAt(this.WorldData.MinX, z);
      while ((!UtilBlock.solid(block)) && (!block.isLiquid()) && (block.getY() > 0))
        block = block.getRelative(BlockFace.DOWN);
      if (block.getY() > 0) {
        this._redLocations.add(block.getLocation());
      }
      block = this.WorldData.World.getHighestBlockAt(this.WorldData.MaxX, z);
      while ((!UtilBlock.solid(block)) && (!block.isLiquid()) && (block.getY() > 0))
        block = block.getRelative(BlockFace.DOWN);
      if (block.getY() > 0) {
        this._redLocations.add(block.getLocation());
      }
    }
  }
  
  public int RedMax() {
    return this._maxSpreadRate;
  }
  
  @EventHandler
  public void RedUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    long time = System.currentTimeMillis();
    
    if (this._redLocations.isEmpty()) {
      return;
    }
    int max = Math.max(5, Math.min(RedMax(), this._redLocations.size() / 100));
    
    for (int i = 0; i < max; i++)
    {
      if (this._redLocations.isEmpty()) {
        break;
      }
      Location loc = (Location)this._redLocations.remove(UtilMath.r(this._redLocations.size()));
      

      if (IsRed(loc.getBlock()))
      {
        i = Math.max(0, i - 1);

      }
      else
      {
        SetRed(loc);
        

        Block block = loc.getBlock();
        
        RedSpread(block.getRelative(BlockFace.UP));
        RedSpread(block.getRelative(BlockFace.DOWN));
        
        if (!RedSpread(block.getRelative(BlockFace.NORTH)))
        {
          RedSpread(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP));
          RedSpread(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN));
        }
        
        if (!RedSpread(block.getRelative(BlockFace.EAST)))
        {
          RedSpread(block.getRelative(BlockFace.EAST).getRelative(BlockFace.UP));
          RedSpread(block.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN));
        }
        
        if (!RedSpread(block.getRelative(BlockFace.SOUTH)))
        {
          RedSpread(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP));
          RedSpread(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN));
        }
        
        if (!RedSpread(block.getRelative(BlockFace.WEST)))
        {
          RedSpread(block.getRelative(BlockFace.WEST).getRelative(BlockFace.UP));
          RedSpread(block.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN));
        }
      }
    }
    this.totalTime += System.currentTimeMillis() - time;
  }
  
  @EventHandler
  public void RedTimer(UpdateEvent event)
  {
    if (event.getType() != UpdateType.MIN_01) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    System.out.println(" ");
    System.out.println("Game Time: " + UtilTime.MakeStr(System.currentTimeMillis() - GetStateTime()));
    System.out.println("Creep Size: " + this._redLocations.size());
    System.out.println("Creep Time: " + UtilTime.convertString(this.totalTime, 4, mineplex.core.common.util.UtilTime.TimeUnit.SECONDS));
    System.out.println(" ");
    
    this.totalTime = 0L;
  }
  
  public boolean RedSpread(Block block)
  {
    if ((block == null) || (block.getType() == Material.AIR)) {
      return false;
    }
    
    if (UtilMath.offset(this._spawn, block.getLocation()) < 48.0D) {
      return false;
    }
    
    if (block.isLiquid())
    {
      if (this._ignoreLiquids) {
        return false;
      }
      
      boolean surroundedByWater = true;
      for (Block other : UtilBlock.getSurrounding(block, false))
      {
        if (other.getY() >= block.getY())
        {

          if ((!other.isLiquid()) && (!IsRed(other)))
          {
            surroundedByWater = false;
            break;
          } }
      }
      if (surroundedByWater) {
        return false;
      }
      
      if ((block.getTypeId() == 9) && 
        (block.getData() != 0)) {
        return false;
      }
      
      for (Block other : UtilBlock.getSurrounding(block, false))
      {
        if ((other.getTypeId() == 9) && (other.getData() != 0))
        {
          return false;
        }
      }
    }
    

    if ((block.getType() == Material.SIGN) || (block.getType() == Material.SIGN_POST) || (block.getType() == Material.WALL_SIGN)) {
      return false;
    }
    
    if (IsRed(block))
    {
      return false;
    }
    
    if ((!UtilBlock.solid(block)) || (UtilBlock.airFoliage(block)) || (block.getType() == Material.CHEST))
    {
      if (!block.isLiquid())
      {
        while (block.getType() == Material.VINE)
        {
          RedChangeBlock(block.getLocation(), 0, (byte)0);
          block = block.getRelative(BlockFace.DOWN);
        }
        

        if (block.getType() != Material.AIR) {
          RedChangeBlock(block.getLocation(), 0, (byte)0);
        }
        return false;
      }
    }
    

    if ((block.getX() < this.WorldData.MinX) || (block.getX() > this.WorldData.MaxX) || (block.getZ() < this.WorldData.MinZ) || (block.getZ() > this.WorldData.MaxZ)) {
      return false;
    }
    
    if (!UtilBlock.isVisible(block)) {
      return false;
    }
    
    this._redLocations.add(block.getLocation());
    return true;
  }
  
  public void RedChangeBlock(Location loc, int id, byte data)
  {
    if (!IsLive()) {
      return;
    }
    MapUtil.ChunkBlockChange(loc, id, data);
    




















    for (Player player : UtilServer.getPlayers())
    {
      if (Math.abs(player.getLocation().getChunk().getX() - loc.getChunk().getX()) <= UtilServer.getServer().getViewDistance())
      {

        if (Math.abs(player.getLocation().getChunk().getZ() - loc.getChunk().getZ()) <= UtilServer.getServer().getViewDistance())
        {

          if (!this._redChunks.containsKey(player)) {
            this._redChunks.put(player, new ArrayList());
          }
          boolean added = false;
          for (ChunkChange change : (ArrayList)this._redChunks.get(player))
          {
            if (change.Chunk.equals(loc.getChunk()))
            {
              change.AddChange(loc, id, data);
              added = true;
              break;
            }
          }
          if (!added)
            ((ArrayList)this._redChunks.get(player)).add(new ChunkChange(loc, id, data));
        } }
    }
  }
  
  @EventHandler
  public void RedChunkUpdate(UpdateEvent event) {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    for (Player player : this._redChunks.keySet())
    {

      Iterator<ChunkChange> chunkIterator = ((ArrayList)this._redChunks.get(player)).iterator();
      while (chunkIterator.hasNext())
      {
        ChunkChange change = (ChunkChange)chunkIterator.next();
        
        if ((Math.abs(player.getLocation().getChunk().getX() - change.Chunk.getX()) > UtilServer.getServer().getViewDistance()) || 
          (Math.abs(player.getLocation().getChunk().getZ() - change.Chunk.getZ()) > UtilServer.getServer().getViewDistance()))
        {
          chunkIterator.remove();
        }
      }
      
      if (!((ArrayList)this._redChunks.get(player)).isEmpty())
      {


        int bestId = -1;
        double bestScore = 0.0D;
        
        for (int i = 0; i < ((ArrayList)this._redChunks.get(player)).size(); i++)
        {
          ChunkChange change = (ChunkChange)((ArrayList)this._redChunks.get(player)).get(i);
          

          double score = change.Changes.size();
          

          score *= (1.0D + (System.currentTimeMillis() - change.Time) / 200.0D);
          

          double dist = 0.5D;
          if (!player.getLocation().getChunk().equals(change.Chunk))
          {
            int x = Math.abs(player.getLocation().getChunk().getX() - change.Chunk.getX());
            int z = Math.abs(player.getLocation().getChunk().getZ() - change.Chunk.getZ());
            dist = Math.sqrt(x * x + z * z);
          }
          score /= dist * dist;
          
          if ((bestId == -1) || (score > bestScore))
          {
            bestId = i;
            bestScore = score;
          }
        }
        
        if (bestId != -1)
        {


          ChunkChange change = (ChunkChange)((ArrayList)this._redChunks.get(player)).remove(bestId);
          
          if (change.DirtyCount > 63) {
            MapUtil.SendChunkForPlayer(change.Chunk.getX(), change.Chunk.getZ(), player);
          } else
            MapUtil.SendMultiBlockForPlayer(change.Chunk.getX(), change.Chunk.getZ(), change.DirtyBlocks, change.DirtyCount, change.Chunk.getWorld(), player);
        }
      }
    }
  }
  
  public boolean IsRed(Block block) { if (!this._redMap.containsKey(Integer.valueOf(block.getX()))) {
      return false;
    }
    if (!((HashMap)this._redMap.get(Integer.valueOf(block.getX()))).containsKey(Integer.valueOf(block.getY()))) {
      return false;
    }
    return ((HashSet)((HashMap)this._redMap.get(Integer.valueOf(block.getX()))).get(Integer.valueOf(block.getY()))).contains(Integer.valueOf(block.getZ()));
  }
  

  public void SetRed(Location loc)
  {
    if (!this._redMap.containsKey(Integer.valueOf(loc.getBlockX()))) {
      this._redMap.put(Integer.valueOf(loc.getBlockX()), new HashMap());
    }
    if (!((HashMap)this._redMap.get(Integer.valueOf(loc.getBlockX()))).containsKey(Integer.valueOf(loc.getBlockY()))) {
      ((HashMap)this._redMap.get(Integer.valueOf(loc.getBlockX()))).put(Integer.valueOf(loc.getBlockY()), new HashSet());
    }
    ((HashSet)((HashMap)this._redMap.get(Integer.valueOf(loc.getBlockX()))).get(Integer.valueOf(loc.getBlockY()))).add(Integer.valueOf(loc.getBlockZ()));
    

    if (this._spreadType == 0)
    {
      RedChangeBlock(loc, 159, (byte)14);

    }
    else if (this._spreadType == 1)
    {
      if (loc.getBlock().getType() != Material.LEAVES)
      {


        if ((loc.getBlock().getTypeId() == 8) || (loc.getBlock().getTypeId() == 9)) { RedChangeBlock(loc, 79, (byte)0);
        } else if ((loc.getBlock().getTypeId() == 10) || (loc.getBlock().getTypeId() == 11)) RedChangeBlock(loc, 49, (byte)0); else {
          RedChangeBlock(loc, 80, (byte)0);
        }
        
      }
    }
    else if (loc.getBlock().getType() == Material.LEAVES) { RedChangeBlock(loc, 88, (byte)0);
    } else if ((loc.getBlock().getTypeId() == 8) || (loc.getBlock().getTypeId() == 9)) { RedChangeBlock(loc, 49, (byte)0);
    }
    else {
      double r = Math.random();
      if (r > 0.1D) RedChangeBlock(loc, 87, (byte)0); else {
        RedChangeBlock(loc, 153, (byte)0);
      }
    }
  }
  
  @EventHandler
  public void RedAttack(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    for (Player player : GetPlayers(true))
    {
      boolean near = false;
      
      for (Block block : UtilBlock.getInRadius(player.getLocation(), 5.0D).keySet())
      {
        if (IsRed(block))
        {

          near = true;
          

          if (this._spreadType == 0)
          {
            if (block.getRelative(BlockFace.UP).getType() == Material.AIR)
            {
              block.getRelative(BlockFace.UP).setType(Material.FIRE);
              break;
            }
            

          }
          else if (this._spreadType == 1)
          {
            if (Math.random() > 0.95D) {
              player.playEffect(block.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, Material.SNOW_BLOCK);
            }
            if (Math.random() > 0.8D)
            {
              Vector traj = UtilAlg.getTrajectory(block.getLocation().add(0.5D, 1.5D, 0.5D), player.getLocation());
              
              Snowball ball = (Snowball)player.getWorld().spawn(block.getLocation().add(0.5D, 1.5D, 0.5D).subtract(traj.clone().multiply(8 + UtilMath.r(8))), Snowball.class);
              
              ball.setVelocity(UtilAlg.getTrajectory(ball.getLocation(), player.getEyeLocation().add(0.0D, 3.0D, 0.0D)).add(new Vector(Math.random() - 0.5D, Math.random() - 0.5D, Math.random() - 0.5D).multiply(0.1D)));
            }
          }
          

          if (this._spreadType == 2)
          {
            if (block.getRelative(BlockFace.UP).getType() == Material.AIR)
            {
              block.getRelative(BlockFace.UP).setType(Material.FIRE);
              break;
            }
          }
        }
      }
      if (!near)
      {
        if (!mineplex.core.common.util.UtilEnt.isGrounded(player))
        {
          Block block = player.getLocation().getBlock();
          
          while ((!UtilBlock.solid(block)) && (block.getY() > 0)) {
            block = block.getRelative(BlockFace.DOWN);
          }
          if ((IsRed(block)) || (block.getY() == 0)) {
            near = true;
          }
        }
      }
      if (!near)
      {
        if ((this._deathmatchLive) && (UtilMath.offset(player.getLocation(), this._spawn) > 48.0D)) {
          near = true;
        }
      }
      if (near)
      {
        player.getWorld().playSound(player.getLocation(), Sound.AMBIENCE_RAIN, 0.5F, 0.0F);
        
        if (!this._redOutTime.containsKey(player))
        {
          this._redOutTime.put(player, Long.valueOf(System.currentTimeMillis()));


        }
        else if (UtilTime.elapsed(((Long)this._redOutTime.get(player)).longValue(), 5000L))
        {
          this.Manager.GetDamage().NewDamageEvent(player, null, null, 
            org.bukkit.event.entity.EntityDamageEvent.DamageCause.VOID, 1.0D, false, true, false, 
            "Hunger Games", this._spreadName);
        }
        
      }
      else
      {
        this._redOutTime.remove(player);
      }
    }
  }
  
  @EventHandler
  public void RedDamage(CustomDamageEvent event)
  {
    if (event.GetProjectile() == null) {
      return;
    }
    if (!(event.GetProjectile() instanceof Snowball)) {
      return;
    }
    event.AddMod("Snowball", this._spreadName, 2.0D, true);
    
    event.AddKnockback("Snowball", 4.0D);
  }
  
  @EventHandler
  public void DayNightCycle(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    long time = this.WorldData.World.getTime();
    
    if ((time > 22000L) || (time < 14000L))
    {
      this.WorldTimeSet = ((this.WorldTimeSet + 4) % 24000);
    }
    else
    {
      this.WorldTimeSet = ((this.WorldTimeSet + 16) % 24000);
    }
    
    this.WorldData.World.setTime(this.WorldTimeSet);
  }
  
  @EventHandler
  public void SupplyDrop(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    long time = this.WorldData.World.getTime();
    
    if ((time > 14000L) && (time < 23000L))
    {
      if ((this._supplyCurrent == null) && (!this._deathmatchCountdown) && (!this._deathmatchLive))
      {
        if (this._supplyLocations.isEmpty()) {
          return;
        }
        this._supplyCurrent = ((Location)this._supplyLocations.remove(UtilMath.r(this._supplyLocations.size())));
        

        this._supplyChests.remove(this._supplyCurrent.getBlock().getRelative(BlockFace.UP));
        this._supplyCurrent.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
        

        this._supplyCurrent.getBlock().setType(Material.BEACON);
        for (int x = -1; x <= 1; x++) {
          for (int z = -1; z <= 1; z++) {
            this._supplyCurrent.getBlock().getRelative(x, -1, z).setType(Material.IRON_BLOCK);
          }
        }
        Announce(C.cYellow + C.Bold + "Supply Drop Incoming (" + ChatColor.RESET + UtilWorld.locToStrClean(this._supplyCurrent) + C.cYellow + C.Bold + ")");
      }
      

    }
    else if (this._supplyCurrent != null)
    {
      if (this._supplyEffect == null)
      {
        this._supplyEffect = this._supplyCurrent.clone();
        this._supplyEffect.setY(250.0D);
      }
      
      FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).with(FireworkEffect.Type.BURST).trail(false).build();
      UtilFirework.playFirework(this._supplyEffect, effect);
      
      this._supplyEffect.setY(this._supplyEffect.getY() - 2.0D);
      
      if (UtilMath.offset(this._supplyEffect, this._supplyCurrent) < 2.0D)
      {
        effect = FireworkEffect.builder().flicker(false).withColor(Color.YELLOW).with(FireworkEffect.Type.BALL_LARGE).trail(true).build();
        UtilFirework.playFirework(this._supplyEffect, effect);
        

        this._supplyCurrent.getBlock().setType(Material.GLASS);
        this._supplyCurrent.getBlock().getRelative(BlockFace.UP).setType(Material.CHEST);
        this._supplyChests.add(this._supplyCurrent.getBlock().getRelative(BlockFace.UP));
        this._openedChest.remove(this._supplyCurrent);
        

        this._supplyEffect = null;
        this._supplyCurrent = null;
      }
    }
  }
  

  @EventHandler
  public void SupplyGlow(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this._supplyChests.isEmpty()) {
      return;
    }
    Iterator<Block> chestIterator = this._supplyChests.iterator();
    
    while (chestIterator.hasNext())
    {
      Block block = (Block)chestIterator.next();
      
      if (block.getType() != Material.CHEST)
      {
        chestIterator.remove();
      }
      else
      {
        mineplex.core.common.util.UtilParticle.PlayParticle(mineplex.core.common.util.UtilParticle.ParticleType.SPELL, block.getLocation().add(0.5D, 0.5D, 0.5D), 0.3F, 0.3F, 0.3F, 0.0F, 1);
      }
    }
  }
  
  @EventHandler
  public void DeathmatchUpdate(UpdateEvent event) {
    if (!IsLive())
      return;
    Iterator localIterator2;
    if (this._deathmatchLive)
    {
      if (event.getType() != UpdateType.SEC) {
        return;
      }
      if (this._deathmatchTime <= 0L) {
        return;
      }
      for (Iterator localIterator1 = GetPlayers(true).iterator(); localIterator1.hasNext(); 
          localIterator2.hasNext())
      {
        Player player = (Player)localIterator1.next();
        localIterator2 = GetPlayers(true).iterator(); continue;Player other = (Player)localIterator2.next();
        
        player.hidePlayer(other);
        player.showPlayer(other);
      }
      
      Announce(C.cRed + C.Bold + "Deathmatch in " + this._deathmatchTime + "...");
      this._deathmatchTime -= 1L;

    }
    else if (this._deathmatchCountdown)
    {
      if (event.getType() != UpdateType.TICK) {
        return;
      }
      long timeLeft = 60000L - (System.currentTimeMillis() - this._deathmatchTime);
      
      if (timeLeft > 0L)
      {
        GetObjectiveSide().setDisplayName(
          ChatColor.WHITE + "§lDeathmatch: " + C.cGreen + "§l" + 
          UtilTime.MakeStr(timeLeft));
      }
      else
      {
        GetObjectiveSide().setDisplayName(
          ChatColor.WHITE + "§lDeathmatch");
        
        this._deathmatchLive = true;
        
        ((GameTeam)GetTeamList().get(0)).SpawnTeleport();
        
        this._redLocations.clear();
        
        for (Block block : UtilBlock.getInRadius(this._spawn, 52.0D).keySet()) {
          RedSpread(block);
        }
        this._deathmatchTime = 10L;
      }
    }
    else
    {
      if (event.getType() != UpdateType.SEC) {
        return;
      }
      if (!UtilTime.elapsed(GetStateTime(), 360000L)) {
        return;
      }
      if (GetPlayers(true).size() > 4) {
        return;
      }
      if (!UtilTime.elapsed(this._deathmatchTime, 60000L)) {
        return;
      }
      Announce(C.cGreen + C.Bold + "Type " + ChatColor.RESET + C.Bold + "/dm" + C.cGreen + C.Bold + " to start Deathmatch!");
      
      this._deathmatchTime = System.currentTimeMillis();
    }
  }
  
  @EventHandler
  public void DeathmatchMoveCancel(PlayerMoveEvent event)
  {
    if (!this._deathmatchLive) {
      return;
    }
    if (this._deathmatchTime <= 0L) {
      return;
    }
    if (UtilMath.offset2d(event.getFrom(), event.getTo()) == 0.0D) {
      return;
    }
    if (!IsAlive(event.getPlayer())) {
      return;
    }
    event.setTo(event.getFrom());
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void DeathmatchDamage(CustomDamageEvent event)
  {
    if (!this._deathmatchLive) {
      return;
    }
    if (this._deathmatchTime <= 0L) {
      return;
    }
    event.SetCancelled("Deathmatch");
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void DeathmatchStart(PlayerCommandPreprocessEvent event)
  {
    if (!event.getMessage().equalsIgnoreCase("/dm")) {
      return;
    }
    event.setCancelled(true);
    
    if (!IsAlive(event.getPlayer()))
    {
      UtilPlayer.message(event.getPlayer(), F.main("Game", "You are not in the game."));
      return;
    }
    
    if (!IsLive())
    {
      UtilPlayer.message(event.getPlayer(), F.main("Game", "Deathmatch cannot be started now."));
      return;
    }
    
    if (!UtilTime.elapsed(GetStateTime(), 360000L))
    {
      UtilPlayer.message(event.getPlayer(), F.main("Game", "Deathmatch cannot be started now."));
      return;
    }
    
    if (GetPlayers(true).size() > 4)
    {
      UtilPlayer.message(event.getPlayer(), F.main("Game", "Deathmatch cannot be started now."));
      return;
    }
    
    if (this._deathmatchCountdown)
    {
      UtilPlayer.message(event.getPlayer(), F.main("Game", "Deathmatch cannot be started now."));
      return;
    }
    
    this._deathmatchCountdown = true;
    
    Announce(C.cGreen + C.Bold + event.getPlayer().getName() + " has initiated Deathmatch!");
    Announce(C.cGreen + C.Bold + "Deathmatch starting in 60 seconds...");
    this._deathmatchTime = System.currentTimeMillis();
    
    for (Player player : UtilServer.getPlayers()) {
      player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0F, 1.0F);
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void CropTrample(PlayerInteractEvent event) {
    if (event.getAction() != org.bukkit.event.block.Action.PHYSICAL) {
      return;
    }
    if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.SOIL) {
      return;
    }
    event.setCancelled(true);
  }
  
  @EventHandler
  public void CompassUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (!IsLive()) {
      return;
    }
    Iterator localIterator1;
    if ((this.WorldData.World.getTime() > 14000L) && (this.WorldData.World.getTime() < 23000L) && (this._supplyCurrent != null)) {
      Iterator localIterator2;
      for (localIterator1 = GetPlayers(true).iterator(); localIterator1.hasNext(); 
          


          localIterator2.hasNext())
      {
        Player player = (Player)localIterator1.next();
        
        player.setCompassTarget(this._supplyCurrent);
        
        localIterator2 = player.getInventory().all(Material.COMPASS).keySet().iterator(); continue;int i = ((Integer)localIterator2.next()).intValue();
        
        ItemStack stack = player.getInventory().getItem(i);
        
        ItemMeta itemMeta = stack.getItemMeta();
        itemMeta.setDisplayName(
          C.cWhite + C.Bold + "Supply Drop Location: " + C.cYellow + UtilMath.trim(1, UtilMath.offset(player.getLocation(), this._supplyCurrent)));
        stack.setItemMeta(itemMeta);
        
        player.getInventory().setItem(i, stack);
      }
      

    }
    else
    {
      for (Player player : GetPlayers(true))
      {
        Player target = null;
        double bestDist = 0.0D;
        
        for (Player other : this.Manager.GetGame().GetPlayers(true))
        {
          if (!other.equals(player))
          {

            if (!other.isSneaking())
            {

              double dist = UtilMath.offset(player, other);
              
              if ((target == null) || (dist < bestDist))
              {
                target = other;
                bestDist = dist;
              }
            } }
        }
        if (target != null)
        {
          player.setCompassTarget(target.getLocation());
          
          for (??? = player.getInventory().all(Material.COMPASS).keySet().iterator(); ???.hasNext();) { int i = ((Integer)???.next()).intValue();
            
            ItemStack stack = player.getInventory().getItem(i);
            

            ItemMeta itemMeta = stack.getItemMeta();
            itemMeta.setDisplayName(
              "    " + C.cWhite + C.Bold + "Nearest Player: " + C.cYellow + target.getName() + 
              "    " + C.cWhite + C.Bold + "Distance: " + C.cYellow + UtilMath.trim(1, bestDist));
            stack.setItemMeta(itemMeta);
            
            player.getInventory().setItem(i, stack);
          }
        }
      }
    }
  }
  
  @EventHandler
  public void DisallowBrewFurnace(PlayerInteractEvent event)
  {
    if (event.getClickedBlock() == null) {
      return;
    }
    if ((event.getClickedBlock().getType() == Material.BREWING_STAND) || 
      (event.getClickedBlock().getType() == Material.FURNACE) || 
      (event.getClickedBlock().getType() == Material.BURNING_FURNACE)) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void CancelItemFrameBreak(HangingBreakEvent event) {
    if ((event.getEntity() instanceof org.bukkit.entity.ItemFrame))
    {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void TNTDelay(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    for (Player player : UtilServer.getPlayers()) {
      Recharge.Instance.useForce(player, "Throw TNT", 30000L);
    }
  }
  
  @EventHandler
  public void TNTThrow(PlayerInteractEvent event) {
    if (!IsLive()) {
      return;
    }
    if (!mineplex.core.common.util.UtilEvent.isAction(event, mineplex.core.common.util.UtilEvent.ActionType.L)) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!UtilInv.IsItem(player.getItemInHand(), Material.TNT, (byte)0)) {
      return;
    }
    event.setCancelled(true);
    
    if (!Recharge.Instance.use(player, "Throw TNT", 0L, true, false))
    {
      UtilPlayer.message(event.getPlayer(), F.main(GetName(), "You cannot use " + F.item("Throw TNT") + " yet."));
      return;
    }
    
    if (!this.Manager.GetGame().CanThrowTNT(player.getLocation()))
    {

      UtilPlayer.message(event.getPlayer(), F.main(GetName(), "You cannot use " + F.item("Throw TNT") + " here."));
      return;
    }
    
    UtilInv.remove(player, Material.TNT, (byte)0, 1);
    UtilInv.Update(player);
    
    TNTPrimed tnt = (TNTPrimed)player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), TNTPrimed.class);
    
    mineplex.core.common.util.UtilAction.velocity(tnt, player.getLocation().getDirection(), 0.5D, false, 0.0D, 0.1D, 10.0D, false);
    
    this._tntMap.put(tnt, player);
  }
  
  @EventHandler
  public void TNTExplosion(ExplosionPrimeEvent event)
  {
    if (!this._tntMap.containsKey(event.getEntity())) {
      return;
    }
    Player player = (Player)this._tntMap.remove(event.getEntity());
    
    for (Player other : UtilPlayer.getNearby(event.getEntity().getLocation(), 14.0D)) {
      this.Manager.GetCondition().Factory().Explosion("Throwing TNT", other, player, 50, 0.1D, false, false);
    }
  }
  
  @EventHandler
  public void BlockPlace(BlockPlaceEvent event) {
    if (IsRed(event.getBlockAgainst()))
    {
      event.setCancelled(true);
      return;
    }
    
    if (event.getItemInHand().getType() == Material.PISTON_BASE)
    {
      this._placedBlocks.add(event.getBlock().getLocation());
      event.setCancelled(false);
      
      final Block block = event.getBlock();
      
      UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
      {
        public void run()
        {
          block.setType(Material.PISTON_BASE);
          block.setData((byte)6);
        }
      }, 0L);
    }
  }
  
  @EventHandler
  public void TourneyKills(CombatDeathEvent event)
  {
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    Player killed = (Player)event.GetEvent().getEntity();
    
    if (event.GetLog().GetKiller() != null)
    {
      Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
      
      if (killer != null) { killer.equals(killed);
      }
    }
    


    if (event.GetLog().GetPlayer() != null) {}
  }
  






  @EventHandler
  public void BlockBreak(BlockBreakEvent event)
  {
    if (this._placedBlocks.remove(event.getBlock().getLocation()))
    {
      event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND, event.getBlock().getType());
      event.getBlock().setType(Material.AIR);
    }
  }
  
  @EventHandler
  public void BlockBurn(BlockBurnEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void BlockSpread(BlockSpreadEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void BlockFade(BlockFadeEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void BlockDecay(LeavesDecayEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void PlayerKillAward(CombatDeathEvent event)
  {
    Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    FireworkEffect effect = FireworkEffect.builder().flicker(false).withColor(Color.RED).with(FireworkEffect.Type.BALL_LARGE).trail(false).build();
    for (int i = 0; i < 3; i++) {
      UtilFirework.launchFirework(event.GetEvent().getEntity().getLocation(), effect, null, 3);
    }
    if (event.GetLog().GetKiller() == null) {
      return;
    }
    Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    if (killer == null) {
      return;
    }
    if (killer.equals(event.GetEvent().getEntity())) {
      return;
    }
    killer.giveExpLevels(1);
  }
  
  @EventHandler
  public void DisableDamageLevel(CustomDamageEvent event)
  {
    event.SetDamageToLevel(false);
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void ExplosionDamageRemove(EntityExplodeEvent event)
  {
    event.blockList().clear();
  }
  
  @EventHandler
  public void FixClean(PlayerQuitEvent event)
  {
    this._redChunks.remove(event.getPlayer());
  }
}
