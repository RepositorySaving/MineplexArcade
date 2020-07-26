package nautilus.game.arcade.game.games.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.explosion.ExplosionEvent;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.CombatComponent;
import mineplex.minecraft.game.core.combat.CombatLog;
import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerDeathOutEvent;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.games.bridge.kits.KitArcher;
import nautilus.game.arcade.game.games.bridge.kits.KitMammoth;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.ore.OreHider;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Bridge extends TeamGame implements nautilus.game.arcade.ore.OreObsfucation
{
  private int _bridgeTime = 600000;
  private boolean _bridgesDown = false;
  

  private ArrayList<Location> _woodBridge = new ArrayList();
  private HashMap<Location, Integer> _woodBridgeBlocks = null;
  

  private ArrayList<Location> _lavaBridge = new ArrayList();
  private ArrayList<Location> _lavaSource = new ArrayList();
  

  private HashSet<BridgePart> _bridgeParts = new HashSet();
  

  private long _lastAnimal = System.currentTimeMillis();
  private HashMap<GameTeam, HashSet<Entity>> _animalSet = new HashMap();
  

  private long _lastMushroom = System.currentTimeMillis();
  

  private ArrayList<ItemStack> _chestLoot = new ArrayList();
  
  private OreHider _ore;
  
  private double _oreDensity = 2.2D;
  


  private int _buildHeight = -1;
  private int _iceForm = -1;
  

  private HashSet<String> _usedLife = new HashSet();
  

  private boolean _tournament = false;
  private HashMap<GameTeam, Integer> _tournamentKills = new HashMap();
  

  private ArrayList<String> _lastScoreboard = new ArrayList();
  
















  public Bridge(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.Bridge, new Kit[] {new nautilus.game.arcade.game.games.bridge.kits.KitApple(manager), new nautilus.game.arcade.game.games.bridge.kits.KitBeserker(manager), new KitMammoth(manager), new KitArcher(manager), new nautilus.game.arcade.game.games.bridge.kits.KitMiner(manager), new nautilus.game.arcade.game.games.bridge.kits.KitBomber(manager) }, new String[] {"Gather resources and prepare for combat.", "After 10 minutes, The Bridges will emerge.", "Special loot is located in the center.", "The last team alive wins!" });
    

    this._ore = new OreHider();
    

    this.DamageSelf = true;
    
    this.ItemDrop = true;
    this.ItemPickup = true;
    
    this.PrivateBlocks = true;
    this.BlockBreak = true;
    this.BlockPlace = true;
    
    this.InventoryOpen = true;
    
    this.WorldTimeSet = 2000;
    
    this.WorldWaterDamage = 0;
    this.WorldBoundaryKill = false;
    
    this.CompassEnabled = true;
    
    this.DeathDropItems = true;
    
    this.GemMultiplier = 2.5D;
    

    this.QuitOut = true;
    this.AutoStart = true;
    this.AutoBalance = true;
  }
  
  @EventHandler
  public void PlayerOut(PlayerDeathOutEvent event)
  {
    if (this._bridgesDown) {
      return;
    }
    Player player = event.GetPlayer();
    
    if ((this.Manager.GetClients().Get(player).GetRank().Has(Rank.ULTRA)) || (this.Manager.GetDonation().Get(player.getName()).OwnsUnknownPackage(GetName() + " ULTRA")))
    {
      if (!this._usedLife.contains(player.getName()))
      {
        this._usedLife.add(player.getName());
        
        UtilPlayer.message(player, F.main("Game", "You used your " + F.elem(new StringBuilder(String.valueOf(C.cAqua)).append("Ultra Rank Revive").toString()) + "."));
        
        event.setCancelled(true);
      }
      
    }
    else {
      UtilPlayer.message(player, F.main("Game", "Purchase " + F.elem(new StringBuilder(String.valueOf(C.cAqua)).append("Ultra Rank").toString()) + " to revive during pre-game!"));
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void GameStateChange(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    if (this.WorldWaterDamage > 0) {
      Announce(F.main(C.cWhite + C.Bold + "WARNING", C.cRed + C.Bold + "Water is very hot/cold and will hurt you!"));
    }
  }
  
  public void ParseData()
  {
    if (!this.WorldData.GetDataLocs("WHITE").isEmpty()) {
      this.WorldWaterDamage = 4;
    }
    ParseLavaBridge();
    ParseWoodBridge();
    ParseIceBridge();
    
    ParseChests();
    
    ParseOre(this.WorldData.GetCustomLocs("73"));
    ParseOre(this.WorldData.GetCustomLocs("14"));
    ParseOre(this.WorldData.GetCustomLocs("129"));
    ParseOre(this.WorldData.GetCustomLocs("56"));
    

    if (!this.WorldData.GetCustomLocs("152").isEmpty()) ParseOre(this.WorldData.GetCustomLocs("152"));
    if (!this.WorldData.GetCustomLocs("41").isEmpty()) ParseOre(this.WorldData.GetCustomLocs("41"));
    if (!this.WorldData.GetCustomLocs("133").isEmpty()) ParseOre(this.WorldData.GetCustomLocs("133"));
    if (!this.WorldData.GetCustomLocs("57").isEmpty()) { ParseOre(this.WorldData.GetCustomLocs("57"));
    }
    if (!this.WorldData.GetCustomLocs("100").isEmpty()) ParseOre(this.WorldData.GetCustomLocs("100"));
    if (!this.WorldData.GetCustomLocs("86").isEmpty()) ParseOre(this.WorldData.GetCustomLocs("86"));
    if (!this.WorldData.GetCustomLocs("103").isEmpty()) ParseOre(this.WorldData.GetCustomLocs("103"));
    if (!this.WorldData.GetCustomLocs("22").isEmpty()) ParseOre(this.WorldData.GetCustomLocs("22"));
  }
  
  private void ParseChests() { int count;
    int i;
    for (Iterator localIterator = this.WorldData.GetCustomLocs("54").iterator(); localIterator.hasNext(); 
        








        i < count)
    {
      Location loc = (Location)localIterator.next();
      
      if (loc.getBlock().getType() != Material.CHEST) {
        loc.getBlock().setType(Material.CHEST);
      }
      Chest chest = (Chest)loc.getBlock().getState();
      
      chest.getBlockInventory().clear();
      
      count = 2 + UtilMath.r(5);
      i = 0; continue;
      
      chest.getBlockInventory().addItem(new ItemStack[] { GetChestItem() });i++;
    }
  }
  



  private ItemStack GetChestItem()
  {
    if (this._chestLoot.isEmpty())
    {
      for (int i = 0; i < 1; i++)
        this._chestLoot.add(new ItemStack(Material.DIAMOND_HELMET));
      for (int i = 0; i < 1; i++)
        this._chestLoot.add(new ItemStack(Material.DIAMOND_CHESTPLATE));
      for (int i = 0; i < 1; i++)
        this._chestLoot.add(new ItemStack(Material.DIAMOND_LEGGINGS));
      for (int i = 0; i < 1; i++)
        this._chestLoot.add(new ItemStack(Material.DIAMOND_BOOTS));
      for (int i = 0; i < 1; i++)
        this._chestLoot.add(new ItemStack(Material.DIAMOND_SWORD));
      for (int i = 0; i < 1; i++)
        this._chestLoot.add(new ItemStack(Material.DIAMOND_AXE));
      for (int i = 0; i < 1; i++) {
        this._chestLoot.add(new ItemStack(Material.DIAMOND_PICKAXE));
      }
      for (int i = 0; i < 6; i++)
        this._chestLoot.add(new ItemStack(Material.IRON_HELMET));
      for (int i = 0; i < 6; i++)
        this._chestLoot.add(new ItemStack(Material.IRON_CHESTPLATE));
      for (int i = 0; i < 6; i++)
        this._chestLoot.add(new ItemStack(Material.IRON_LEGGINGS));
      for (int i = 0; i < 6; i++)
        this._chestLoot.add(new ItemStack(Material.IRON_BOOTS));
      for (int i = 0; i < 6; i++)
        this._chestLoot.add(new ItemStack(Material.IRON_SWORD));
      for (int i = 0; i < 6; i++)
        this._chestLoot.add(new ItemStack(Material.IRON_AXE));
      for (int i = 0; i < 6; i++) {
        this._chestLoot.add(new ItemStack(Material.IRON_PICKAXE));
      }
      for (int i = 0; i < 18; i++)
        this._chestLoot.add(new ItemStack(Material.BOW));
      for (int i = 0; i < 24; i++) {
        this._chestLoot.add(new ItemStack(Material.ARROW, 8));
      }
      for (int i = 0; i < 48; i++)
        this._chestLoot.add(new ItemStack(Material.MUSHROOM_SOUP));
      for (int i = 0; i < 24; i++) {
        this._chestLoot.add(new ItemStack(Material.COOKED_CHICKEN, 2));
      }
    }
    ItemStack stack = (ItemStack)this._chestLoot.get(UtilMath.r(this._chestLoot.size()));
    
    int amount = 1;
    
    if (stack.getType().getMaxStackSize() > 1) {
      amount = stack.getAmount() + UtilMath.r(stack.getAmount());
    }
    return ItemStackFactory.Instance.CreateStack(stack.getTypeId(), amount);
  }
  
  @EventHandler
  public void ChestDeny(PlayerInteractEvent event)
  {
    if (this._bridgesDown) {
      return;
    }
    if (event.getClickedBlock() == null) {
      return;
    }
    if (event.getClickedBlock().getType() != Material.CHEST) {
      return;
    }
    if (!mineplex.core.common.util.UtilEvent.isAction(event, mineplex.core.common.util.UtilEvent.ActionType.R_BLOCK)) {
      return;
    }
    for (Location loc : this.WorldData.GetCustomLocs("54"))
    {
      if (loc.getBlock().equals(event.getClickedBlock()))
      {
        event.setCancelled(true);
        return;
      }
    }
  }
  
  private void ParseOre(ArrayList<Location> teamOre)
  {
    int coal = (int)(teamOre.size() / 32.0D * this._oreDensity);
    int iron = (int)(teamOre.size() / 24.0D * this._oreDensity);
    int gold = (int)(teamOre.size() / 64.0D * this._oreDensity);
    int diamond = 1 + (int)(teamOre.size() / 128.0D * this._oreDensity);
    
    int gravel = (int)(teamOre.size() / 64.0D * this._oreDensity);
    
    int lowY = 256;
    int highY = 0;
    
    for (Location loc : teamOre)
    {
      if (loc.getBlockY() < lowY) {
        lowY = loc.getBlockY();
      }
      if (loc.getBlockY() > highY) {
        highY = loc.getBlockY();
      }
      loc.getBlock().setTypeId(1);
    }
    
    int varY = highY - lowY;
    

    for (int i = 0; (i < gravel) && (!teamOre.isEmpty()); i++)
    {
      int attempts = 20;
      int id = 0;
      
      while (attempts > 0)
      {
        id = UtilMath.r(teamOre.size());
        
        double height = (((Location)teamOre.get(id)).getBlockY() - lowY) / varY;
        
        if (height > 0.8D) {
          break;
        }
        if ((height > 0.6D) && (Math.random() > 0.4D)) {
          break;
        }
        if ((height > 0.4D) && (Math.random() > 0.6D)) {
          break;
        }
        if ((height > 0.2D) && (Math.random() > 0.8D)) {
          break;
        }
      }
      CreateOre((Location)teamOre.remove(id), Material.GRAVEL, 6);
    }
    

    for (int i = 0; (i < coal) && (!teamOre.isEmpty()); i++)
    {
      int attempts = 20;
      int id = 0;
      
      while (attempts > 0)
      {
        id = UtilMath.r(teamOre.size());
        
        double height = (((Location)teamOre.get(id)).getBlockY() - lowY) / varY;
        
        if (height > 0.8D) {
          break;
        }
        if ((height > 0.6D) && (Math.random() > 0.4D)) {
          break;
        }
        if ((height > 0.4D) && (Math.random() > 0.6D)) {
          break;
        }
        if ((height > 0.2D) && (Math.random() > 0.8D)) {
          break;
        }
      }
      CreateOre((Location)teamOre.remove(id), Material.COAL_ORE, 6);
    }
    

    for (int i = 0; (i < iron) && (!teamOre.isEmpty()); i++)
    {
      int id = UtilMath.r(teamOre.size());
      
      CreateOre((Location)teamOre.remove(id), Material.IRON_ORE, 3);
    }
    

    for (int i = 0; (i < gold) && (!teamOre.isEmpty()); i++)
    {
      int attempts = 20;
      int id = 0;
      
      while (attempts > 0)
      {
        id = UtilMath.r(teamOre.size());
        
        double height = (((Location)teamOre.get(id)).getBlockY() - lowY) / 
          varY;
        
        if ((height > 0.8D) && (Math.random() > 0.8D)) {
          break;
        }
        if ((height > 0.6D) && (Math.random() > 0.7D)) {
          break;
        }
        if ((height > 0.4D) && (Math.random() > 0.6D)) {
          break;
        }
        if ((height > 0.2D) && (Math.random() > 0.4D)) {
          break;
        }
        if (Math.random() > 0.2D) {
          break;
        }
      }
      CreateOre((Location)teamOre.remove(id), Material.GOLD_ORE, 3);
    }
    

    for (int i = 0; (i < diamond) && (!teamOre.isEmpty()); i++)
    {
      int attempts = 20;
      int id = 0;
      
      while (attempts > 0)
      {
        id = UtilMath.r(teamOre.size());
        
        double height = (((Location)teamOre.get(id)).getBlockY() - lowY) / 
          varY;
        
        if (height <= 0.8D)
        {

          if ((height > 0.6D) && (Math.random() > 0.9D)) {
            break;
          }
          if ((height > 0.4D) && (Math.random() > 0.7D)) {
            break;
          }
          if ((height <= 0.2D) || (Math.random() <= 0.5D)) break;
          break;
        }
      }
      


      CreateOre((Location)teamOre.remove(id), Material.DIAMOND_ORE, 2);
    }
  }
  
  private void CreateOre(Location loc, Material type, int amount)
  {
    double bonus = Math.random() + 1.0D;
    
    amount = (int)(amount * bonus);
    
    int attempts = 100;
    while ((amount > 0) && (attempts > 0))
    {
      attempts--;
      
      loc.add(1 - UtilMath.r(3), 1 - UtilMath.r(3), 1 - UtilMath.r(3));
      
      if (loc.getBlock().getTypeId() == 1)
      {

        this._ore.AddOre(loc, type);
        
        amount--;
      }
    }
  }
  
  private void ParseWoodBridge() { this._woodBridge = new ArrayList();
    

    for (Location loc : this.WorldData.GetDataLocs("BROWN")) {
      this._woodBridge.add(loc.getBlock().getLocation());
    }
    
    for (Location loc : this.WorldData.GetDataLocs("GRAY")) {
      this._woodBridge.add(loc.getBlock().getLocation());
      this._woodBridge.add(loc.getBlock().getRelative(BlockFace.UP)
        .getLocation());
    }
    

    this._woodBridgeBlocks = new HashMap();
    
    for (Location loc : this._woodBridge)
    {
      if (this._woodBridge.contains(loc.getBlock().getRelative(BlockFace.DOWN).getLocation())) {
        this._woodBridgeBlocks.put(loc, Integer.valueOf(85));
      }
      

      if (this._woodBridge.contains(loc.getBlock().getRelative(BlockFace.UP).getLocation())) {
        this._woodBridgeBlocks.put(loc, Integer.valueOf(17));
      }
      
      if (!this._woodBridgeBlocks.containsKey(loc)) {
        this._woodBridgeBlocks.put(loc, Integer.valueOf(126));
      }
    }
  }
  
  private void ParseLavaBridge() {
    for (Location loc : this.WorldData.GetDataLocs("RED")) {
      this._lavaBridge.add(loc.getBlock().getLocation());
    }
    
    for (Location loc : this.WorldData.GetDataLocs("ORANGE")) {
      this._lavaBridge.add(loc.getBlock().getLocation());
      this._lavaBridge.add(loc.getBlock().getRelative(BlockFace.UP)
        .getLocation());
    }
    
    this._lavaSource = this.WorldData.GetDataLocs("BLACK");
  }
  
  private void ParseIceBridge()
  {
    if (this.WorldData.GetCustomLocs("WATER_HEIGHT").isEmpty()) {
      return;
    }
    this._iceForm = ((Location)this.WorldData.GetCustomLocs("WATER_HEIGHT").get(0)).getBlockY();
  }
  
  @EventHandler
  public void BridgeBuild(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    if (!UtilTime.elapsed(GetStateTime(), this._bridgeTime)) {
      return;
    }
    this._bridgesDown = true;
    
    BuildWood();
    BuildLava();
    BuildIce();
  }
  
  private void BuildIce()
  {
    if (this._iceForm <= 0) {
      return;
    }
    if (UtilTime.elapsed(GetStateTime(), this._bridgeTime + 120000))
    {
      this.WorldData.World.setStorm(false);
      return;
    }
    
    this.WorldData.World.setStorm(true);
    

    if (!UtilTime.elapsed(GetStateTime(), this._bridgeTime + 6000)) {
      return;
    }
    int xVar = this.WorldData.MaxX - this.WorldData.MinX;
    int zVar = this.WorldData.MaxZ - this.WorldData.MinZ;
    

    BuildIceArea(this.WorldData.MinX, this.WorldData.MinX + xVar / 2, this.WorldData.MinZ, this.WorldData.MinZ + zVar / 2, Material.REDSTONE_BLOCK);
    BuildIceArea(this.WorldData.MinX + xVar / 2, this.WorldData.MaxX, this.WorldData.MinZ, this.WorldData.MinZ + zVar / 2, Material.GOLD_BLOCK);
    BuildIceArea(this.WorldData.MinX, this.WorldData.MinX + xVar / 2, this.WorldData.MinZ + zVar / 2, this.WorldData.MaxZ, Material.EMERALD_BLOCK);
    BuildIceArea(this.WorldData.MinX + xVar / 2, this.WorldData.MaxX, this.WorldData.MinZ + zVar / 2, this.WorldData.MaxZ, Material.DIAMOND_BLOCK);
  }
  
  private void BuildIceArea(int xLow, int xHigh, int zLow, int zHigh, Material mat)
  {
    int attempts = 1000;
    int complete = 10;
    

    while ((attempts > 0) && (complete > 0))
    {
      attempts--;
      
      int x = xLow + UtilMath.r(xHigh - xLow);
      int z = zLow + UtilMath.r(zHigh - zLow);
      
      Block block = this.WorldData.World.getBlockAt(x, this._iceForm, z);
      
      if (block.isLiquid())
      {

        if ((!block.getRelative(BlockFace.NORTH).isLiquid()) || 
          (!block.getRelative(BlockFace.EAST).isLiquid()) || 
          (!block.getRelative(BlockFace.SOUTH).isLiquid()) || 
          (!block.getRelative(BlockFace.WEST).isLiquid()))
        {

          block.setType(Material.ICE);
          
          complete--;
        } }
    }
  }
  
  private void BuildLava() {
    for (int i = 0; i < 3; i++)
      if ((this._lavaBridge != null) && (this._lavaSource != null) && 
        (!this._lavaBridge.isEmpty()) && (!this._lavaSource.isEmpty()))
      {
        Location bestLoc = (Location)this._lavaBridge.get(UtilMath.r(this._lavaBridge
          .size()));
        

        if (!bestLoc.getBlock().getRelative(BlockFace.DOWN).isLiquid())
        {

          this._lavaBridge.remove(bestLoc);
          
          Location source = (Location)this._lavaSource.get(UtilMath.r(this._lavaSource
            .size()));
          

          FallingBlock block = bestLoc.getWorld().spawnFallingBlock(
            source, 10, (byte)0);
          BridgePart part = new BridgePart(block, bestLoc, true);
          this._bridgeParts.add(part);
          

          source.getWorld().playSound(source, org.bukkit.Sound.EXPLODE, 
            5.0F * (float)Math.random(), 
            0.5F + (float)Math.random());
        }
      }
  }
  
  private void BuildWood() {
    if ((this._woodBridgeBlocks != null) && (!this._woodBridgeBlocks.isEmpty()))
    {
      ArrayList<Location> toDo = new ArrayList();
      
      BlockFace[] faces = { BlockFace.NORTH, 
        BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
      
      for (Location loc : this._woodBridgeBlocks.keySet())
      {
        if (((Integer)this._woodBridgeBlocks.get(loc)).intValue() == 17)
        {
          int adjacent = 0;
          
          for (BlockFace face : faces) {
            if (loc.getBlock().getRelative(face).getTypeId() != 0)
              adjacent++;
          }
          if (adjacent > 0) {
            toDo.add(loc);
          }
        } else if (((Integer)this._woodBridgeBlocks.get(loc)).intValue() == 85)
        {
          if (loc.getBlock().getRelative(BlockFace.DOWN).getTypeId() != 0)
          {

            toDo.add(loc); }
        } else if (((Integer)this._woodBridgeBlocks.get(loc)).intValue() == 126)
        {
          int adjacent = 0;
          
          for (BlockFace face : faces) {
            if (loc.getBlock().getRelative(face).getTypeId() != 0)
              adjacent++;
          }
          if (adjacent > 0) {
            toDo.add(loc);
          }
        }
      }
      if (toDo.size() == 0) {
        return;
      }
      for (Location loc : toDo)
      {
        int id = ((Integer)this._woodBridgeBlocks.remove(loc)).intValue();
        
        Location source = loc.clone().add(0.0D, 30.0D, 0.0D);
        

        FallingBlock block = loc.getWorld().spawnFallingBlock(source, 
          id, (byte)0);
        block.setVelocity(new org.bukkit.util.Vector(0, -1, 0));
        BridgePart part = new BridgePart(block, loc, false);
        this._bridgeParts.add(part);
      }
    }
  }
  
  @EventHandler
  public void BridgeUpdate(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    Iterator<BridgePart> partIterator = this._bridgeParts.iterator();
    
    while (partIterator.hasNext())
    {
      BridgePart part = (BridgePart)partIterator.next();
      
      if (part.Update()) {
        partIterator.remove();
      }
    }
  }
  
  @EventHandler
  public void BridgeForm(EntityChangeBlockEvent event) {
    for (BridgePart part : this._bridgeParts) {
      if (part.Entity.equals(event.getEntity()))
        event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void BridgeItem(ItemSpawnEvent event) {
    for (BridgePart part : this._bridgeParts) {
      if (part.ItemSpawn(event.getEntity()))
        event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void IceForm(BlockFormEvent event) {
    event.setCancelled(true);
  }
  
  @EventHandler
  public void AnimalSpawn(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (!UtilTime.elapsed(this._lastAnimal, 30000L)) {
      return;
    }
    this._lastAnimal = System.currentTimeMillis();
    
    for (GameTeam team : GetTeamList())
    {
      if (this._animalSet.get(team) == null) {
        this._animalSet.put(team, new HashSet());
      }
      
      Iterator<Entity> entIterator = ((HashSet)this._animalSet.get(team)).iterator();
      
      while (entIterator.hasNext())
      {
        Entity ent = (Entity)entIterator.next();
        
        if ((ent.isDead()) || (!ent.isValid())) {
          entIterator.remove();
        }
      }
      
      if (((HashSet)this._animalSet.get(team)).size() <= 4)
      {


        double rand = Math.random();
        


        this.CreatureAllowOverride = true;
        Entity ent; Entity ent; if (rand > 0.66D) {
          ent = team.GetSpawn().getWorld().spawn(team.GetSpawn(), Cow.class); } else { Entity ent;
          if (rand > 0.33D) {
            ent = team.GetSpawn().getWorld().spawn(team.GetSpawn(), org.bukkit.entity.Pig.class);
          } else
            ent = team.GetSpawn().getWorld().spawn(team.GetSpawn(), Chicken.class); }
        this.CreatureAllowOverride = false;
        
        ((HashSet)this._animalSet.get(team)).add(ent);
      }
    }
  }
  
  @EventHandler
  public void MushroomSpawn(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    if (!UtilTime.elapsed(this._lastMushroom, 20000L)) {
      return;
    }
    this._lastMushroom = System.currentTimeMillis();
    
    for (GameTeam team : GetTeamList())
    {
      Block block = team.GetSpawn().getBlock();
      
      while (!UtilBlock.airFoliage(block))
      {
        block = block.getRelative(BlockFace.UP);
      }
      
      while (UtilBlock.airFoliage(block))
      {
        block = block.getRelative(BlockFace.DOWN);
      }
      
      block = block.getRelative(BlockFace.UP);
      
      if (block.getTypeId() == 0)
      {
        if (Math.random() > 0.5D) {
          block.setTypeId(39);
        } else {
          block.setTypeId(40);
        }
      }
    }
  }
  
  @EventHandler
  public void OreReveal(ExplosionEvent event) {
    this._ore.Explosion(event);
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void OreReveal(BlockBreakEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    this._ore.BlockBreak(event);
  }
  
  @EventHandler
  public void OreRevealToggle(AsyncPlayerChatEvent event)
  {
    if (!this.Manager.GetClients().Get(event.getPlayer()).GetRank().Has(Rank.OWNER)) {
      return;
    }
    if (event.getMessage().contains("toggleorevisibility")) {
      this._ore.ToggleVisibility();
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void BlockPlace(BlockPlaceEvent event) {
    if (event.isCancelled()) {
      return;
    }
    if (!IsAlive(event.getPlayer())) {
      return;
    }
    
    if (event.getBlock().getLocation().getBlockY() > GetHeightLimit())
    {
      UtilPlayer.message(event.getPlayer(), F.main("Game", 
        "Cannot place blocks this high up."));
      event.setCancelled(true);
      return;
    }
    
    if (this._bridgesDown) {
      return;
    }
    
    if ((event.getBlockReplacedState().getTypeId() == 8) || 
      (event.getBlockReplacedState().getTypeId() == 9) || 
      (event.getBlockReplacedState().getTypeId() == 10) || 
      (event.getBlockReplacedState().getTypeId() == 11))
    {
      UtilPlayer.message(event.getPlayer(), F.main("Game", 
        "Cannot place blocks in liquids until Bridge is down."));
      event.setCancelled(true);
      return;
    }
    

    for (int i = 1; i <= event.getBlock().getLocation().getY(); i++)
    {
      Block below = event.getBlock().getRelative(BlockFace.DOWN, i);
      
      if (below.isLiquid())
      {

        UtilPlayer.message(
          event.getPlayer(), 
          F.main("Game", 
          "Cannot place blocks above water until Bridge is down."));
        event.setCancelled(true);
        return;
      }
      
      if (event.getBlock().getLocation().getY() - i <= 0.0D)
      {

        UtilPlayer.message(
          event.getPlayer(), 
          F.main("Game", 
          "Cannot place blocks above void until Bridge is down."));
        event.setCancelled(true);
        return;
      }
      
      if (below.getTypeId() != 0) {
        break;
      }
    }
  }
  
  @EventHandler
  public void BridgeTimer(UpdateEvent event) {
    if (GetState() != Game.GameState.Live) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    long time = this._bridgeTime - (
      System.currentTimeMillis() - GetStateTime());
    
    if (time > 0L) {
      GetObjectiveSide().setDisplayName(
        ChatColor.WHITE + "§lBridges in " + C.cGreen + "§l" + 
        UtilTime.MakeStr(time));
    } else {
      GetObjectiveSide().setDisplayName(
        ChatColor.WHITE + "§lBridges are down!");
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void ChestProtect(EntityExplodeEvent event) {
    Iterator<Block> blockIterator = event.blockList().iterator();
    
    while (blockIterator.hasNext())
    {
      Block block = (Block)blockIterator.next();
      
      if ((block.getType() == Material.CHEST) || 
        (block.getType() == Material.FURNACE) || 
        (block.getType() == Material.BURNING_FURNACE) || 
        (block.getType() == Material.WORKBENCH)) {
        blockIterator.remove();
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void BucketEmpty(PlayerBucketEmptyEvent event) {
    if (this.WorldWaterDamage <= 0) {
      return;
    }
    if (event.getBucket() == Material.WATER_BUCKET)
    {
      UtilPlayer.message(
        event.getPlayer(), 
        F.main("Game", "Cannot use " + F.elem("Water Bucket") + " on this map."));
      event.setCancelled(true);
    }
  }
  
  public int GetHeightLimit()
  {
    if (this._buildHeight == -1)
    {
      this._buildHeight = 0;
      int amount = 0;
      Iterator localIterator2;
      for (Iterator localIterator1 = GetTeamList().iterator(); localIterator1.hasNext(); 
          localIterator2.hasNext())
      {
        GameTeam team = (GameTeam)localIterator1.next();
        localIterator2 = team.GetSpawns().iterator(); continue;Location loc = (Location)localIterator2.next();
        
        this._buildHeight += loc.getBlockY();
        amount++;
      }
      

      this._buildHeight /= amount;
    }
    
    return this._buildHeight + 24;
  }
  

  public OreHider GetOreHider()
  {
    return this._ore;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void CraftingDeny(PrepareItemCraftEvent event)
  {
    if (event.getRecipe().getResult() == null) {
      return;
    }
    Material type = event.getRecipe().getResult().getType();
    
    if ((type != Material.GOLDEN_APPLE) && 
      (type != Material.GOLDEN_CARROT) && 
      (type != Material.FLINT_AND_STEEL)) {
      return;
    }
    if (!(event.getInventory() instanceof CraftingInventory)) {
      return;
    }
    CraftingInventory inv = event.getInventory();
    inv.setResult(null);
  }
  

  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    
    for (String string : this._lastScoreboard)
      GetScoreboard().resetScores(string);
    this._lastScoreboard.clear();
    

    if (!this._tournament)
    {
      for (GameTeam team : GetTeamList())
      {
        String out = team.GetPlayers(true).size() + " " + team.GetColor() + team.GetName();
        if (out.length() > 16)
          out = out.substring(0, 16);
        this._lastScoreboard.add(out);
        
        Score score = GetObjectiveSide().getScore(out);
        score.setScore(team.GetPlayers(true).size());
      }
      
    }
    else
    {
      for (GameTeam team : GetTeamList())
      {
        int kills = 0;
        if (this._tournamentKills.containsKey(team)) {
          kills = ((Integer)this._tournamentKills.get(team)).intValue();
        }
        String out = kills + " " + team.GetColor() + team.GetPlayers(true).size() + " " + team.GetName();
        if (out.length() > 16)
          out = out.substring(0, 16);
        this._lastScoreboard.add(out);
        
        if (kills == 0) {
          kills = -1;
        }
        Score score = GetObjectiveSide().getScore(out);
        score.setScore(kills);
      }
    }
  }
  
  @EventHandler
  public void RecordKill(CombatDeathEvent event)
  {
    Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    if (!(event.GetEvent().getEntity() instanceof Player)) {
      return;
    }
    Player killed = (Player)event.GetEvent().getEntity();
    
    GameTeam killedTeam = GetTeam(killed);
    if (killedTeam == null) {
      return;
    }
    if (event.GetLog().GetKiller() != null)
    {
      Player killer = UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
      
      if ((killer != null) && (!killer.equals(killed)))
      {
        GameTeam killerTeam = GetTeam(killer);
        if (killerTeam == null) {
          return;
        }
        if (killerTeam.equals(killedTeam)) {
          return;
        }
        if (!this._tournamentKills.containsKey(killerTeam)) {
          this._tournamentKills.put(killerTeam, Integer.valueOf(1));
        } else {
          this._tournamentKills.put(killerTeam, Integer.valueOf(((Integer)this._tournamentKills.get(killerTeam)).intValue() + 1));
        }
      }
    }
  }
  
  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    ArrayList<GameTeam> teamsAlive = new ArrayList();
    
    for (GameTeam team : GetTeamList()) {
      if (team.GetPlayers(true).size() > 0)
        teamsAlive.add(team);
    }
    if (!this.QuitOut)
    {

      for (GameTeam team : this.RejoinTeam.values()) {
        teamsAlive.add(team);
      }
    }
    if (teamsAlive.size() <= 1)
    {
      int bestKills;
      if (!this._tournament)
      {
        if (teamsAlive.size() > 0) {
          AnnounceEnd((GameTeam)teamsAlive.get(0));
        }
      }
      else {
        GameTeam bestTeam = null;
        bestKills = 0;
        
        for (GameTeam team : GetTeamList())
        {
          if (this._tournamentKills.containsKey(team))
          {
            int kills = ((Integer)this._tournamentKills.get(team)).intValue();
            
            if ((bestTeam == null) || (bestKills < kills))
            {
              bestTeam = team;
              bestKills = kills;
            }
          }
        }
        
        if (bestTeam != null) {
          AnnounceEnd(bestTeam);
        }
      }
      
      for (Iterator localIterator2 = GetTeamList().iterator(); localIterator2.hasNext(); 
          






          ???.hasNext())
      {
        GameTeam team = (GameTeam)localIterator2.next();
        
        if ((this.WinnerTeam != null) && (team.equals(this.WinnerTeam)))
        {
          for (Player player : team.GetPlayers(false)) {
            AddGems(player, 10.0D, "Winning Team", false);
          }
        }
        ??? = team.GetPlayers(false).iterator(); continue;Player player = (Player)???.next();
        if (player.isOnline()) {
          AddGems(player, 10.0D, "Participation", false);
        }
      }
      
      SetState(Game.GameState.End);
    }
  }
}
