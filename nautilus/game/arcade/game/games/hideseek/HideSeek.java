package nautilus.game.arcade.game.games.hideseek;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.explosion.Explosion;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.core.combat.DeathMessageType;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import mineplex.minecraft.game.core.damage.DamageManager;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerPrepareTeleportEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.games.hideseek.forms.BlockForm;
import nautilus.game.arcade.game.games.hideseek.forms.CreatureForm;
import nautilus.game.arcade.game.games.hideseek.forms.Form;
import nautilus.game.arcade.game.games.hideseek.kits.KitHiderQuick;
import nautilus.game.arcade.game.games.hideseek.kits.KitHiderSwapper;
import nautilus.game.arcade.game.games.hideseek.kits.KitSeekerLeaper;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.world.WorldData;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.Navigation;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

public class HideSeek extends TeamGame
{
  private GameTeam _hiders;
  private GameTeam _seekers;
  private long _hideTime = 20000L;
  private long _gameTime = 360000L;
  
  private boolean _started = false;
  
  private HashMap<Player, Integer> _arrowHits = new HashMap();
  
  private HashMap<Player, Form> _forms = new HashMap();
  
  private HashMap<Creature, Location> _mobs = new HashMap();
  






  private ArrayList<Material> _allowedBlocks;
  






  private ArrayList<EntityType> _allowedEnts;
  






  public HideSeek(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.HideSeek, new Kit[] {new KitHiderSwapper(manager), new KitHiderQuick(manager), new nautilus.game.arcade.game.games.hideseek.kits.KitHiderShocker(manager), new nautilus.game.arcade.kit.NullKit(manager), new KitSeekerLeaper(manager), new nautilus.game.arcade.game.games.hideseek.kits.KitSeekerTNT(manager), new nautilus.game.arcade.game.games.hideseek.kits.KitSeekerRadar(manager) }, new String[] {C.cAqua + "Hiders" + C.cWhite + " Run and Hide from Seekers", C.cAqua + "Hiders" + C.cWhite + " Disguise as Blocks or Animals", C.cAqua + "Hiders" + C.cWhite + " Shoot Seekers for Axe Upgrades", C.cAqua + "Hiders" + C.cWhite + " Right-Click with Axe for Speed Boost", C.cRed + "Seekers" + C.cWhite + " Find and kill the Hiders!" });
    

    this.DamageSelf = false;
    this.DeathOut = false;
    this.InventoryOpen = false;
    this.HungerSet = 20;
    this.PrepareFreeze = false;
    
    this._allowedBlocks = new ArrayList();
    this._allowedBlocks.add(Material.TNT);
    this._allowedBlocks.add(Material.BOOKSHELF);
    this._allowedBlocks.add(Material.WORKBENCH);
    this._allowedBlocks.add(Material.FURNACE);
    this._allowedBlocks.add(Material.MELON_BLOCK);
    this._allowedBlocks.add(Material.CAULDRON);
    this._allowedBlocks.add(Material.FLOWER_POT);
    this._allowedBlocks.add(Material.ANVIL);
    this._allowedBlocks.add(Material.HAY_BLOCK);
    this._allowedBlocks.add(Material.CAKE_BLOCK);
    
    this._allowedEnts = new ArrayList();
    this._allowedEnts.add(EntityType.PIG);
    this._allowedEnts.add(EntityType.COW);
    this._allowedEnts.add(EntityType.CHICKEN);
    this._allowedEnts.add(EntityType.SHEEP);
    
    this.Manager.GetExplosion().SetRegenerate(true);
    this.Manager.GetExplosion().SetTNTSpread(false);
  }
  
  public Material GetItemEquivilent(Material mat)
  {
    if (mat == Material.CAULDRON) return Material.CAULDRON_ITEM;
    if (mat == Material.FLOWER_POT) return Material.FLOWER_POT_ITEM;
    if (mat == Material.CAKE_BLOCK) { return Material.CAKE;
    }
    return mat;
  }
  

  public void ParseData()
  {
    int i = 0;
    Iterator localIterator2;
    for (Iterator localIterator1 = this.WorldData.GetAllCustomLocs().values().iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      ArrayList<Location> locs = (ArrayList)localIterator1.next();
      
      localIterator2 = locs.iterator(); continue;Location loc = (Location)localIterator2.next();
      
      if (Math.random() <= 0.25D)
      {

        if (loc.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR)
        {

          loc.getBlock().setType(Material.AIR);
          i++;
        }
      }
    }
    System.out.println("Removed " + i + " Random Blocks.");
    
    for (Location loc : this.WorldData.GetDataLocs("BLACK")) {
      loc.getBlock().setType(Material.FENCE);
    }
  }
  
  @EventHandler
  public void CustomTeamGeneration(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    this._hiders = ((GameTeam)GetTeamList().get(0));
    this._hiders.SetColor(ChatColor.AQUA);
    this._hiders.SetName("Hiders");
    
    this._seekers = ((GameTeam)GetTeamList().get(1));
    this._seekers.SetColor(ChatColor.RED);
    this._seekers.SetName("Hunters");
    
    RestrictKits();
  }
  

  public void RestrictKits()
  {
    for (Kit kit : GetKits())
    {
      for (GameTeam team : GetTeamList())
      {
        if (team.GetColor() == ChatColor.RED)
        {
          if (kit.GetName().contains("Hider")) {
            team.GetRestrictedKits().add(kit);
          }
          
        }
        else if (kit.GetName().contains("Hunter")) {
          team.GetRestrictedKits().add(kit);
        }
      }
    }
  }
  
  @EventHandler
  public void MoveKits(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    for (int i = 0; (i < this.WorldData.GetDataLocs("RED").size()) && (i < 3); i++)
    {
      if (GetKits().length > 4 + i)
      {

        this.CreatureAllowOverride = true;
        Entity ent = GetKits()[(4 + i)].SpawnEntity((Location)this.WorldData.GetDataLocs("RED").get(i));
        this.CreatureAllowOverride = false;
        
        this.Manager.GetLobby().AddKitLocation(ent, GetKits()[(4 + i)], (Location)this.WorldData.GetDataLocs("RED").get(i));
      }
    }
  }
  
  public void GiveItems() {
    for (Player player : this._hiders.GetPlayers(true))
    {





      player.getInventory().setItem(0, ItemStackFactory.Instance.CreateStack(Material.WOOD_AXE, (byte)1, 1, C.cGreen + "Speed Axe"));
      

      ItemStack bow = ItemStackFactory.Instance.CreateStack(Material.BOW, (byte)0, 1, C.cYellow + C.Bold + "Shoot Hunters" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "Upgrades Axe");
      bow.addEnchantment(org.bukkit.enchantments.Enchantment.ARROW_INFINITE, 1);
      player.getInventory().setItem(1, bow);
      player.getInventory().setItem(28, ItemStackFactory.Instance.CreateStack(Material.ARROW));
      

      player.getInventory().setItem(4, ItemStackFactory.Instance.CreateStack(Material.SUGAR, (byte)0, 1, C.cYellow + C.Bold + "Meow" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "+0.25 Gems"));
      

      ItemStack firework = ItemStackFactory.Instance.CreateStack(Material.FIREWORK, (byte)0, 5, C.cYellow + C.Bold + "Firework" + C.cWhite + C.Bold + " - " + C.cGreen + C.Bold + "+2 Gems");
      FireworkMeta metaData = (FireworkMeta)firework.getItemMeta();
      metaData.setPower(1);
      metaData.addEffect(org.bukkit.FireworkEffect.builder().flicker(true).withColor(org.bukkit.Color.AQUA).with(org.bukkit.FireworkEffect.Type.BALL_LARGE).trail(true).build());
      firework.setItemMeta(metaData);
      player.getInventory().setItem(5, firework);
      

      Recharge.Instance.useForce(player, "Meow", 15000L);
      Recharge.Instance.useForce(player, "Firework", 15000L);
    }
  }
  
  @EventHandler
  public void InitialDisguise(PlayerPrepareTeleportEvent event)
  {
    if (this._hiders.HasPlayer(event.GetPlayer().getName(), true)) {
      this._forms.put(event.GetPlayer(), new BlockForm(this, event.GetPlayer(), (Material)this._allowedBlocks.get(UtilMath.r(this._allowedBlocks.size()))));
    }
  }
  

  @EventHandler
  public void ChangeDisguise(PlayerInteractEvent event)
  {
    if (event.getClickedBlock() == null) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!UtilGear.isMat(player.getItemInHand(), Material.SLIME_BALL)) {
      return;
    }
    if (!this._allowedBlocks.contains(event.getClickedBlock().getType()))
    {
      UtilPlayer.message(player, F.main("Game", "You cannot morph into " + F.elem(new StringBuilder(String.valueOf(ItemStackFactory.Instance.GetName(event.getClickedBlock().getType(), (byte)0, false))).append(" Block").toString()) + "."));
      return;
    }
    
    if (!Recharge.Instance.use(player, "Change Form", 6000L, true, false)) {
      return;
    }
    if (!(GetKit(player) instanceof KitHiderSwapper)) {
      UtilInv.remove(player, Material.SLIME_BALL, (byte)0, 1);
    }
    
    ((Form)this._forms.get(player)).Remove();
    

    this._forms.put(player, new BlockForm(this, player, event.getClickedBlock().getType()));
  }
  
  @EventHandler
  public void ChangeDisguise(PlayerInteractEntityEvent event)
  {
    if (event.getRightClicked() == null) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!UtilGear.isMat(player.getItemInHand(), Material.SLIME_BALL)) {
      return;
    }
    if (!this._allowedEnts.contains(event.getRightClicked().getType()))
    {
      UtilPlayer.message(player, F.main("Game", "You cannot morph into " + F.elem(UtilEnt.getName(event.getRightClicked())) + "."));
      return;
    }
    
    if (!Recharge.Instance.use(player, "Change Form", 6000L, true, false)) {
      return;
    }
    if (!(GetKit(player) instanceof KitHiderSwapper)) {
      UtilInv.remove(player, Material.SLIME_BALL, (byte)0, 1);
    }
    
    ((Form)this._forms.get(player)).Remove();
    

    this._forms.put(player, new CreatureForm(this, player, event.getRightClicked().getType()));
  }
  
  @EventHandler
  public void ChangeDisguise(CustomDamageEvent event)
  {
    Player player = event.GetDamagerPlayer(false);
    if (player == null) { return;
    }
    if (!UtilGear.isMat(player.getItemInHand(), Material.SLIME_BALL)) {
      return;
    }
    if (!this._allowedEnts.contains(event.GetDamageeEntity().getType()))
    {
      UtilPlayer.message(player, F.main("Game", "You cannot morph into " + F.elem(UtilEnt.getName(event.GetDamageeEntity())) + "."));
      return;
    }
    
    if (!Recharge.Instance.use(player, "Change Form", 6000L, true, false)) {
      return;
    }
    if (!(GetKit(player) instanceof KitHiderSwapper)) {
      UtilInv.remove(player, Material.SLIME_BALL, (byte)0, 1);
    }
    
    ((Form)this._forms.get(player)).Remove();
    

    this._forms.put(player, new CreatureForm(this, player, event.GetDamageeEntity().getType()));
  }
  
  @EventHandler
  public void FallingBlockBreak(ItemSpawnEvent event)
  {
    event.setCancelled(true);
    
    for (Form form : this._forms.values()) {
      if ((form instanceof BlockForm))
        ((BlockForm)form).FallingBlockCheck();
    }
  }
  
  @EventHandler
  public void FallingBlockLand(EntityChangeBlockEvent event) {
    if ((event.getEntity() instanceof FallingBlock))
    {
      event.setCancelled(true);
      event.getEntity().remove();
      
      for (Form form : this._forms.values()) {
        if ((form instanceof BlockForm))
          ((BlockForm)form).FallingBlockCheck();
      }
    }
  }
  
  @EventHandler
  public void FallingBlockUpdate(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (!InProgress()) {
      return;
    }
    for (Form form : this._forms.values()) {
      if ((form instanceof BlockForm))
        ((BlockForm)form).FallingBlockCheck();
    }
  }
  
  @EventHandler
  public void SolidifyUpdate(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Form form : this._forms.values()) {
      if ((form instanceof BlockForm))
        ((BlockForm)form).SolidifyUpdate();
    }
  }
  
  @EventHandler
  public void SolidBlockDamage(BlockDamageEvent event) {
    if (!this._seekers.HasPlayer(event.getPlayer())) {
      return;
    }
    for (Form form : this._forms.values())
    {
      if ((form instanceof BlockForm))
      {

        if (((BlockForm)form).GetBlock() != null)
        {

          if (((BlockForm)form).GetBlock().equals(event.getBlock()))
          {


            this.Manager.GetDamage().NewDamageEvent(form.Player, event.getPlayer(), null, 
              EntityDamageEvent.DamageCause.CUSTOM, 4.0D, true, true, false, 
              event.getPlayer().getName(), null);
            
            ((BlockForm)form).SolidifyRemove();
          } } }
    }
  }
  
  @EventHandler
  public void FallingBlockDamage(EntityDamageEvent event) {
    if (!(event instanceof EntityDamageByEntityEvent)) {
      return;
    }
    if (!(event.getEntity() instanceof org.bukkit.entity.FallingSand)) {
      return;
    }
    if (event.getEntity().getVehicle() == null) {
      return;
    }
    if (!(event.getEntity().getVehicle() instanceof LivingEntity)) {
      return;
    }
    LivingEntity damagee = (LivingEntity)event.getEntity().getVehicle();
    
    EntityDamageByEntityEvent eventEE = (EntityDamageByEntityEvent)event;
    
    LivingEntity damager = null;
    Projectile proj = null;
    
    if ((eventEE.getDamager() instanceof Projectile))
    {
      proj = (Projectile)eventEE.getDamager();
      damager = proj.getShooter();
    }
    else if ((eventEE.getDamager() instanceof LivingEntity))
    {
      damager = (LivingEntity)eventEE.getDamager();
    }
    

    this.Manager.GetDamage().NewDamageEvent(damagee, damager, proj, 
      event.getCause(), event.getDamage(), true, false, false, 
      null, null);
    
    event.setCancelled(true);
  }
  
  @EventHandler
  public void AnimalSpawn(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    this.CreatureAllowOverride = true;
    
    for (Location loc : this.WorldData.GetDataLocs("WHITE")) {
      this._mobs.put((Creature)loc.getWorld().spawn(loc, org.bukkit.entity.Sheep.class), loc);
    }
    for (Location loc : this.WorldData.GetDataLocs("PINK")) {
      this._mobs.put((Creature)loc.getWorld().spawn(loc, org.bukkit.entity.Pig.class), loc);
    }
    for (Location loc : this.WorldData.GetDataLocs("YELLOW")) {
      this._mobs.put((Creature)loc.getWorld().spawn(loc, org.bukkit.entity.Chicken.class), loc);
    }
    for (Location loc : this.WorldData.GetDataLocs("BROWN")) {
      this._mobs.put((Creature)loc.getWorld().spawn(loc, Cow.class), loc);
    }
    this.CreatureAllowOverride = false;
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.LOW)
  public void AnimalDamage(CustomDamageEvent event)
  {
    if ((event.GetDamageePlayer() == null) && (!(event.GetDamageeEntity() instanceof Slime))) {
      event.SetCancelled("Animal Damage");
    }
    if ((event.GetDamagerEntity(false) != null) && ((event.GetDamagerEntity(false) instanceof Slime))) {
      event.SetCancelled("Slime Attack");
    }
  }
  
  @EventHandler
  public void AnimalReturn(UpdateEvent event) {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Creature ent : this._mobs.keySet())
    {
      if (UtilMath.offset(ent.getLocation(), (Location)this._mobs.get(ent)) >= 5.0D)
      {

        Location loc = ((Location)this._mobs.get(ent)).add(UtilAlg.getTrajectory((Location)this._mobs.get(ent), ent.getLocation()).multiply(Math.random() * 3.0D));
        
        EntityCreature ec = ((CraftCreature)ent).getHandle();
        Navigation nav = ec.getNavigation();
        nav.a(loc.getX(), loc.getY(), loc.getZ(), 1.0D);
      }
    }
  }
  
  @EventHandler
  public void AttackSeeker(CustomDamageEvent event) {
    if (event.GetDamagerPlayer(true) == null) {
      return;
    }
    if (!this._hiders.HasPlayer(event.GetDamagerPlayer(true))) {
      return;
    }
    if (event.GetDamageInitial() > 1.0D) {
      return;
    }
    event.AddMod("H&S", "Negate", -event.GetDamageInitial(), false);
    event.AddMod("H&S", "Attack", event.GetDamageInitial(), true);
  }
  
  @EventHandler
  public void ArrowShoot(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player shooter = (Player)event.getEntity();
    
    if (!this._hiders.HasPlayer(shooter)) {
      return;
    }
    Arrow arrow = shooter.getWorld().spawnArrow(
      shooter.getEyeLocation().add(shooter.getLocation().getDirection().multiply(1.5D)), 
      shooter.getLocation().getDirection(), (float)event.getProjectile().getVelocity().length(), 0.0F);
    arrow.setShooter(shooter);
    
    event.setCancelled(true);
  }
  
  @EventHandler
  public void ArrowHit(CustomDamageEvent event)
  {
    if (event.IsCancelled()) {
      return;
    }
    if (event.GetProjectile() == null) {
      return;
    }
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    if (!this._seekers.HasPlayer(damagee)) {
      return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    if (!this._hiders.HasPlayer(damager)) {
      return;
    }
    event.AddMod("Hide & Seek", "Negate", -event.GetDamageInitial(), false);
    event.AddMod("Hide & Seek", "Damage Set", 2.0D, false);
    event.AddKnockback("Hide & Seek", 2.0D);
    
    Powerup(damager);
  }
  
  public void Powerup(Player player)
  {
    int count = 1;
    if (this._arrowHits.containsKey(player)) {
      count += ((Integer)this._arrowHits.get(player)).intValue();
    }
    this._arrowHits.put(player, Integer.valueOf(count));
    
    if (count == 4)
    {
      player.getInventory().remove(Material.WOOD_AXE);
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.STONE_AXE, 1, 1, C.cGreen + "Super Axe") });
      

      player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
      

      UtilPlayer.message(player, F.main("Game", "You upgraded to " + F.elem("Super Axe") + "!"));
    }
    else if (count == 8)
    {
      player.getInventory().remove(Material.STONE_AXE);
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_AXE, 1, 1, C.cGreen + "Ultra Axe") });
      

      player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
      

      UtilPlayer.message(player, F.main("Game", "You upgraded to " + F.elem("Ultra Axe") + "!"));
    }
    else if (count == 12)
    {
      player.getInventory().remove(Material.IRON_AXE);
      player.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.DIAMOND_AXE, 1, 1, C.cGreen + "Hyper Axe") });
      

      player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
      

      UtilPlayer.message(player, F.main("Game", "You upgraded to " + F.elem("Hyper Axe") + "!"));
    }
    else if (count < 12)
    {

      player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
    }
  }
  
  @EventHandler
  public void UseBoost(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!UtilEvent.isAction(event, UtilEvent.ActionType.R)) {
      return;
    }
    if (player.getItemInHand() == null) {
      return;
    }
    if (!player.getItemInHand().getType().toString().contains("_AXE")) {
      return;
    }
    if (!this._hiders.HasPlayer(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, "Axe Boost", 16000L, true, true)) {
      return;
    }
    if (UtilGear.isMat(player.getItemInHand(), Material.WOOD_AXE))
    {
      this.Manager.GetCondition().Factory().Speed("Boost", player, player, 4.0D, 0, false, false, false);
      

      UtilPlayer.message(player, F.main("Game", "You used " + F.elem("Basic Boost") + "!"));
      

      player.getWorld().playSound(player.getLocation(), Sound.BLAZE_BREATH, 1.0F, 1.0F);
    }
    else if (UtilGear.isMat(player.getItemInHand(), Material.STONE_AXE))
    {
      this.Manager.GetCondition().Factory().Speed("Boost", player, player, 4.0D, 1, false, false, false);
      

      UtilPlayer.message(player, F.main("Game", "You used " + F.elem("Ultra Boost") + "!"));
      

      player.getWorld().playSound(player.getLocation(), Sound.BLAZE_BREATH, 1.0F, 1.0F);
    }
    else if (UtilGear.isMat(player.getItemInHand(), Material.IRON_AXE))
    {
      this.Manager.GetCondition().Factory().Speed("Boost", player, player, 4.0D, 1, false, false, false);
      this.Manager.GetCondition().Factory().Regen("Boost", player, player, 4.0D, 0, false, false, false);
      

      UtilPlayer.message(player, F.main("Game", "You used " + F.elem("Mega Boost") + "!"));
      

      player.getWorld().playSound(player.getLocation(), Sound.BLAZE_BREATH, 1.0F, 1.0F);
    }
    else if (UtilGear.isMat(player.getItemInHand(), Material.DIAMOND_AXE))
    {
      this.Manager.GetCondition().Factory().Speed("Boost", player, player, 4.0D, 2, false, false, false);
      this.Manager.GetCondition().Factory().Regen("Boost", player, player, 4.0D, 1, false, false, false);
      

      UtilPlayer.message(player, F.main("Game", "You used " + F.elem("Hyper Boost") + "!"));
      

      player.getWorld().playSound(player.getLocation(), Sound.BLAZE_BREATH, 1.0F, 1.0F);
    }
  }
  
  @EventHandler
  public void UseMeow(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!UtilEvent.isAction(event, UtilEvent.ActionType.R)) {
      return;
    }
    if (!UtilGear.isMat(player.getItemInHand(), Material.SUGAR)) {
      return;
    }
    event.setCancelled(true);
    
    if (!Recharge.Instance.use(player, "Meow", 5000L, true, false)) {
      return;
    }
    player.getWorld().playSound(player.getLocation(), Sound.CAT_MEOW, 1.0F, 1.0F);
    
    AddGems(player, 0.25D, "Meows", true);
    
    UtilParticle.PlayParticle(mineplex.core.common.util.UtilParticle.ParticleType.NOTE, player.getLocation().add(0.0D, 0.75D, 0.0D), 0.4F, 0.4F, 0.4F, 0.0F, 6);
  }
  
  @EventHandler
  public void UseFirework(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!UtilEvent.isAction(event, UtilEvent.ActionType.R_BLOCK)) {
      return;
    }
    if (!UtilGear.isMat(player.getItemInHand(), Material.FIREWORK)) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      return;
    }
    if (!Recharge.Instance.use(player, "Firework", 15000L, true, false))
    {
      event.setCancelled(true);
      return;
    }
    
    AddGems(player, 2.0D, "Fireworks", true);
  }
  
  @EventHandler
  public void HiderTimeGems(UpdateEvent event)
  {
    if (GetState() != Game.GameState.Live) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Player player : this._hiders.GetPlayers(true))
    {
      AddGems(player, 0.05D, "Seconds Alive", true);
    }
  }
  
  @EventHandler
  public void UpdateSeekers(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    int req = Math.max(1, GetPlayers(true).size() / 5);
    
    while ((this._seekers.GetPlayers(true).size() < req) && (this._hiders.GetPlayers(true).size() > 0))
    {
      Player player = (Player)this._hiders.GetPlayers(true).get(UtilMath.r(this._hiders.GetPlayers(true).size()));
      SetSeeker(player, true);
    }
  }
  
  @EventHandler
  public void WaterDamage(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FAST) {}
  }
  

  @EventHandler
  public void WorldWaterDamage(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Player player : this._hiders.GetPlayers(true)) {
      if ((player.getLocation().getBlock().getTypeId() == 8) || (player.getLocation().getBlock().getTypeId() == 9))
      {

        this.Manager.GetDamage().NewDamageEvent(player, null, null, 
          EntityDamageEvent.DamageCause.DROWNING, 2.0D, false, false, false, 
          "Water", "Water Damage");
        
        player.getWorld().playSound(player.getLocation(), 
          Sound.SPLASH, 0.8F, 
          1.0F + (float)Math.random() / 2.0F);
      }
    }
  }
  
  @EventHandler
  public void PlayerDeath(PlayerQuitEvent event) {
    Form form = (Form)this._forms.remove(event.getPlayer());
    if (form != null) {
      form.Remove();
    }
  }
  
  @EventHandler
  public void PlayerDeath(PlayerDeathEvent event) {
    if (this._hiders.HasPlayer(event.getEntity())) {
      SetSeeker(event.getEntity(), false);
    }
  }
  
  public void SetSeeker(Player player, boolean forced) {
    SetPlayerTeam(player, this._seekers);
    
    this.Manager.GetDisguise().undisguise(player);
    

    Form form = (Form)this._forms.remove(player);
    if (form != null) {
      form.Remove();
    }
    
    SetKit(player, GetKits()[4], false);
    GetKits()[4].ApplyKit(player);
    

    for (Player other : mineplex.core.common.util.UtilServer.getPlayers())
    {
      other.hidePlayer(player);
      other.showPlayer(player);
    }
    
    if (forced)
    {
      AddGems(player, 10.0D, "Forced Seeker", false);
      
      Announce(F.main("Game", F.elem(new StringBuilder().append(this._hiders.GetColor()).append(player.getName()).toString()) + " was moved to " + 
        F.elem(new StringBuilder(String.valueOf(C.cRed)).append(C.Bold).append("Hunters").toString()) + "."));
      
      player.getWorld().strikeLightningEffect(player.getLocation());
      
      player.damage(1000.0D);
    }
    
    UtilPlayer.message(player, C.cRed + C.Bold + "You are now a Hunter!");
    
    player.eject();
    player.leaveVehicle();
    player.teleport(this._seekers.GetSpawn());
  }
  

  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    if (GetPlayers(true).isEmpty())
    {
      SetState(Game.GameState.End);
      return;
    }
    
    if (this._hiders.GetPlayers(true).isEmpty())
    {
      SetState(Game.GameState.End);
      AnnounceEnd(this._seekers);
      
      for (Player player : GetPlayers(false)) {
        if (player.isOnline()) {
          AddGems(player, 10.0D, "Participation", false);
        }
      }
    }
  }
  
  public double GetKillsGems(Player killer, Player killed, boolean assist) {
    if (this._hiders.HasPlayer(killed))
    {
      if (!assist) {
        return 4.0D;
      }
      return 1.0D;
    }
    
    if (!assist) {
      return 1.0D;
    }
    return 0.0D;
  }
  
  @EventHandler
  public void AnnounceHideTime(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live) {
      return;
    }
    Announce(C.cAqua + C.Bold + "Hiders have 20 Seconds to hide!");
  }
  


  @EventHandler
  public void Timer(UpdateEvent event)
  {
    if (GetState() != Game.GameState.Live) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    
    if (!this._started)
    {
      long timeLeft = this._hideTime - (System.currentTimeMillis() - GetStateTime());
      
      if (timeLeft > 0L)
      {
        GetObjectiveSide().setDisplayName(
          ChatColor.WHITE + "§lHide Time: " + C.cGreen + "§l" + 
          UtilTime.MakeStr(timeLeft));
      }
      else
      {
        this._started = true;
        

        GiveItems();
        

        for (Location loc : this.WorldData.GetDataLocs("BLACK")) {
          loc.getBlock().setType(Material.AIR);
        }
        Announce(C.cRed + C.Bold + "The Hunters have been released!");
      }
      
    }
    else
    {
      long timeLeft = this._gameTime - (System.currentTimeMillis() - GetStateTime() - this._hideTime);
      
      if (timeLeft > 0L)
      {
        GetObjectiveSide().setDisplayName(
          ChatColor.WHITE + "§lHunt Time: " + C.cGreen + "§l" + 
          UtilTime.MakeStr(timeLeft));
      }
      else
      {
        GetObjectiveSide().setDisplayName(
          ChatColor.WHITE + "§lHunt Time: " + C.cGreen + "§l" + 
          UtilTime.MakeStr(0L));
        
        SetState(Game.GameState.End);
        AnnounceEnd(this._hiders);
        
        for (Player player : this._hiders.GetPlayers(true)) {
          AddGems(player, 10.0D, "Winning Team", false);
        }
        for (Player player : GetPlayers(false)) {
          if (player.isOnline()) {
            AddGems(player, 10.0D, "Participation", false);
          }
        }
      }
    }
  }
  
  public GameTeam ChooseTeam(Player player) {
    if (CanJoinTeam(this._seekers)) {
      return this._seekers;
    }
    return this._hiders;
  }
  

  public boolean CanJoinTeam(GameTeam team)
  {
    if (team.GetColor() == ChatColor.RED)
    {
      return team.GetSize() < Math.max(1, GetPlayers(true).size() / 5);
    }
    
    return true;
  }
  

  public boolean CanThrowTNT(Location location)
  {
    for (Location loc : this._seekers.GetSpawns()) {
      if (UtilMath.offset(loc, location) < 24.0D)
        return false;
    }
    return true;
  }
  

  public DeathMessageType GetDeathMessageType()
  {
    return DeathMessageType.Detailed;
  }
  
  @EventHandler
  public void UsableCancel(PlayerInteractEvent event)
  {
    if (event.getClickedBlock() == null) {
      return;
    }
    if (UtilBlock.usable(event.getClickedBlock())) {
      event.setCancelled(true);
    }
  }
}
