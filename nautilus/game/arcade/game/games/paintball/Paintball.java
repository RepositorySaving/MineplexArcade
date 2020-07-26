package nautilus.game.arcade.game.games.paintball;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.recharge.Recharge;
import mineplex.minecraft.game.core.condition.ConditionFactory;
import mineplex.minecraft.game.core.condition.ConditionManager;
import mineplex.minecraft.game.core.damage.CustomDamageEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.events.PlayerPrepareTeleportEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.GameTeam.PlayerState;
import nautilus.game.arcade.game.TeamGame;
import nautilus.game.arcade.game.games.paintball.kits.KitMachineGun;
import nautilus.game.arcade.game.games.paintball.kits.KitRifle;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitScheduler;

public class Paintball extends TeamGame
{
  private HashMap<Player, PlayerCopy> _doubles = new HashMap();
  private HashSet<Projectile> _water = new HashSet();
  













  public Paintball(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.Paintball, new nautilus.game.arcade.kit.Kit[] {new KitRifle(manager), new nautilus.game.arcade.game.games.paintball.kits.KitShotgun(manager), new KitMachineGun(manager) }, new String[] {"Shoot enemies to paint them", "Revive/heal with Water Bombs", "Last team alive wins!" });
    

    this.HungerSet = 20;
  }
  
  @EventHandler
  public void CustomTeamGeneration(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    ((GameTeam)GetTeamList().get(0)).SetColor(ChatColor.AQUA);
    ((GameTeam)GetTeamList().get(0)).SetName("Frost");
    
    ((GameTeam)GetTeamList().get(1)).SetColor(ChatColor.RED);
    ((GameTeam)GetTeamList().get(1)).SetName("Nether");
  }
  

  @EventHandler(priority=EventPriority.HIGHEST)
  public void ColorArmor(PlayerPrepareTeleportEvent event)
  {
    CleanColorArmor(event.GetPlayer());
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void RefreshPlayers(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Live)
      return;
    Iterator localIterator2;
    for (Iterator localIterator1 = GetPlayers(true).iterator(); localIterator1.hasNext(); 
        
        localIterator2.hasNext())
    {
      Player player = (Player)localIterator1.next();
      
      localIterator2 = GetPlayers(true).iterator(); continue;Player other = (Player)localIterator2.next();
      
      other.hidePlayer(player);
      other.showPlayer(player);
    }
  }
  

  @EventHandler
  public void HealthRegen(EntityRegainHealthEvent event)
  {
    if (event.getRegainReason() == org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.SATIATED) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void Teleport(PlayerTeleportEvent event) {
    if (event.getCause() == org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void Paint(ProjectileHitEvent event) {
    if ((event.getEntity() instanceof ThrownPotion)) {
      return;
    }
    byte color = 3;
    if ((event.getEntity() instanceof EnderPearl)) {
      color = 14;
    }
    Location loc = event.getEntity().getLocation().add(event.getEntity().getVelocity());
    
    for (Block block : UtilBlock.getInRadius(loc, 1.5D).keySet())
    {
      if ((block.getType() == Material.WOOL) || (block.getType() == Material.STAINED_CLAY))
      {

        block.setData(color);
      }
    }
    if (color == 3) loc.getWorld().playEffect(loc, Effect.STEP_SOUND, 8); else {
      loc.getWorld().playEffect(loc, Effect.STEP_SOUND, 10);
    }
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event) {
    this._doubles.remove(event.getPlayer());
  }
  
  @EventHandler
  public void DamageCancel(CustomDamageEvent event)
  {
    if (event.GetDamageePlayer() == null) {
      event.SetCancelled("Not Player");
    }
    if (event.GetProjectile() == null) {
      event.SetCancelled("No Projectile");
    }
  }
  
  @EventHandler
  public void PaintballDamage(CustomDamageEvent event) {
    if (event.GetProjectile() == null) {
      return;
    }
    if ((!(event.GetProjectile() instanceof Snowball)) && (!(event.GetProjectile() instanceof EnderPearl))) {
      return;
    }
    
    event.AddMod("Negate", "Negate", -event.GetDamageInitial(), false);
    
    event.AddMod("Paintball", "Paintball", 2.0D, true);
    event.AddKnockback("Paintball", 2.0D);
    
    Player damagee = event.GetDamageePlayer();
    if (damagee == null) { return;
    }
    Player damager = event.GetDamagerPlayer(true);
    if (damager == null) { return;
    }
    GameTeam damageeTeam = GetTeam(damagee);
    if (damageeTeam == null) { return;
    }
    GameTeam damagerTeam = GetTeam(damager);
    if (damagerTeam == null) { return;
    }
    if (damagerTeam.equals(damageeTeam)) {
      return;
    }
    
    int count = 1;
    if (GetKit(damager) != null)
    {
      if ((GetKit(damager) instanceof KitRifle))
      {
        count = 3;
      }
    }
    

    if (Color(damagee, count))
    {
      for (Player player : UtilServer.getPlayers()) {
        UtilPlayer.message(player, damageeTeam.GetColor() + damagee.getName() + ChatColor.RESET + " was painted by " + 
          damagerTeam.GetColor() + damager.getName() + ChatColor.RESET + "!");
      }
      PlayerOut(damagee);
      
      AddGems(damager, 2.0D, "Kills", true);
    }
    

    Player player = event.GetDamagerPlayer(true);
    if (player != null) {
      player.playSound(player.getLocation(), org.bukkit.Sound.ORB_PICKUP, 1.0F, 3.0F);
    }
  }
  
  public boolean Color(Player player, int amount)
  {
    ArrayList<ItemStack> nonColored = new ArrayList();
    for (ItemStack stack : player.getInventory().getArmorContents())
    {
      if ((stack.getItemMeta() instanceof LeatherArmorMeta))
      {

        LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();
        
        if ((meta.getColor().equals(Color.RED)) || (meta.getColor().equals(Color.AQUA))) {
          nonColored.add(stack);
        }
      }
    }
    for (int i = 0; i < amount; i++)
    {
      if (nonColored.isEmpty()) {
        break;
      }
      ItemStack armor = (ItemStack)nonColored.remove(UtilMath.r(nonColored.size()));
      
      LeatherArmorMeta meta = (LeatherArmorMeta)armor.getItemMeta();
      meta.setColor(Color.PURPLE);
      armor.setItemMeta(meta);
    }
    
    player.setHealth(Math.min(20, Math.max(2, nonColored.size() * 5 + 1)));
    
    return nonColored.isEmpty();
  }
  

  public void PlayerOut(Player player)
  {
    SetPlayerState(player, GameTeam.PlayerState.OUT);
    player.setHealth(20.0D);
    

    this.Manager.GetCondition().Factory().Blind("Hit", player, player, 1.5D, 0, false, false, false);
    this.Manager.GetCondition().Factory().Cloak("Hit", player, player, 9999.0D, false, false);
    

    player.setGameMode(GameMode.CREATIVE);
    player.setFlying(true);
    ((CraftPlayer)player).getHandle().spectating = true;
    ((CraftPlayer)player).getHandle().k = false;
    
    player.setVelocity(new org.bukkit.util.Vector(0.0D, 1.2D, 0.0D));
    
    this._doubles.put(player, new PlayerCopy(this, player));
  }
  
  @EventHandler
  public void CleanThrow(PlayerInteractEvent event)
  {
    if (!IsLive()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!UtilGear.isMat(player.getItemInHand(), Material.POTION)) {
      return;
    }
    if (!IsAlive(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, "Water Bomb", 500L, false, false)) {
      return;
    }
    
    mineplex.core.common.util.UtilInv.remove(player, Material.POTION, (byte)0, 1);
    

    ThrownPotion potion = (ThrownPotion)player.launchProjectile(ThrownPotion.class);
    
    this._water.add(potion);
    

    UtilPlayer.message(player, F.main("Skill", "You threw " + F.skill("Water Bomb") + "."));
  }
  
  @EventHandler
  public void CleanHit(ProjectileHitEvent event)
  {
    if (!this._water.remove(event.getEntity())) {
      return;
    }
    if (event.getEntity().getShooter() == null) {
      return;
    }
    if (!(event.getEntity().getShooter() instanceof Player)) {
      return;
    }
    Player thrower = (Player)event.getEntity().getShooter();
    
    GameTeam throwerTeam = GetTeam(thrower);
    if (throwerTeam == null) { return;
    }
    
    Iterator<PlayerCopy> copyIterator = this._doubles.values().iterator();
    GameTeam otherTeam; while (copyIterator.hasNext())
    {
      PlayerCopy copy = (PlayerCopy)copyIterator.next();
      
      otherTeam = GetTeam(copy.GetPlayer());
      if ((otherTeam != null) && (otherTeam.equals(throwerTeam)))
      {

        if (UtilMath.offset(copy.GetEntity().getLocation().add(0.0D, 1.0D, 0.0D), event.getEntity().getLocation()) <= 3.0D)
        {

          PlayerIn(copy.GetPlayer(), copy.GetEntity());
          copyIterator.remove();
          
          AddGems(thrower, 3.0D, "Revived Ally", true);
        }
      }
    }
    for (Player player : GetPlayers(true))
    {
      GameTeam otherTeam = GetTeam(player);
      if ((otherTeam != null) && (otherTeam.equals(throwerTeam)))
      {

        if (UtilMath.offset(player.getLocation().add(0.0D, 1.0D, 0.0D), event.getEntity().getLocation()) <= 3.0D)
        {

          PlayerIn(player, null);
        }
      }
    }
  }
  
  public void PlayerIn(final Player player, final LivingEntity copy) {
    SetPlayerState(player, GameTeam.PlayerState.IN);
    player.setHealth(20.0D);
    

    if (copy != null)
    {
      Location loc = player.getLocation();
      loc.setX(copy.getLocation().getX());
      loc.setY(copy.getLocation().getY());
      loc.setZ(copy.getLocation().getZ());
      player.teleport(loc);
    }
    

    player.setGameMode(GameMode.SURVIVAL);
    player.setFlying(false);
    ((CraftPlayer)player).getHandle().spectating = false;
    ((CraftPlayer)player).getHandle().k = true;
    

    player.getInventory().remove(Material.WATCH);
    player.getInventory().remove(Material.COMPASS);
    

    CleanColorArmor(player);
    

    UtilPlayer.message(player, F.main("Game", "You have been cleaned!"));
    

    if (copy != null)
    {
      UtilServer.getServer().getScheduler().scheduleSyncDelayedTask(this.Manager.GetPlugin(), new Runnable()
      {

        public void run()
        {
          if (Paintball.this.IsAlive(player)) {
            Paintball.this.Manager.GetCondition().EndCondition(player, mineplex.minecraft.game.core.condition.Condition.ConditionType.CLOAK, null);
          }
          
          copy.remove();
        }
      }, 4L);
    }
  }
  
  public void CleanColorArmor(Player player)
  {
    Color color = Color.RED;
    if (this.Manager.GetColor(player) != ChatColor.RED) {
      color = Color.AQUA;
    }
    for (ItemStack stack : player.getEquipment().getArmorContents())
    {
      System.out.println("Type:" + stack.getType() + " Meta:" + stack.getItemMeta());
      if ((stack.getItemMeta() instanceof LeatherArmorMeta))
      {

        LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();
        meta.setColor(color);
        stack.setItemMeta(meta);
        System.out.println("Changed leather meta for " + player.getName());
      }
    }
  }
  
  @EventHandler
  public void InventoryClick(InventoryClickEvent event) {
    event.setCancelled(true);
    event.getWhoClicked().closeInventory();
  }
}
