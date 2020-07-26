package mineplex.minecraft.game.classcombat.Skill.Ranger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.WeakHashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

public class Fletcher extends Skill
{
  private WeakHashMap<Player, Long> _time = new WeakHashMap();
  private HashSet<Entity> _fletchArrows = new HashSet();
  private HashSet<Entity> _fletchDisable = new HashSet();
  
  public Fletcher(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Craft arrows from your surroundings,", 
      "creating 1 Arrow every #13#-3 seconds.", 
      "", 
      "Maximum of #2#2 Fletched Arrows.", 
      "Fletched Arrows are temporary." });
  }
  

  @EventHandler
  public void ShootBow(EntityShootBowEvent event)
  {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player)event.getEntity();
    
    int level = getLevel(player);
    if (level == 0) { return;
    }
    for (int i = 0; i <= 8; i++) {
      if ((player.getInventory().getItem(i) != null) && 
        (player.getInventory().getItem(i).getType() == Material.ARROW) && 
        (player.getInventory().getItem(i).getData() != null))
      {
        if (player.getInventory().getItem(i).getData().getData() == 1) {
          this._fletchArrows.add(event.getProjectile());
        }
        return;
      }
    }
  }
  
  @EventHandler
  public void ProjectileHit(ProjectileHitEvent event) {
    if (this._fletchArrows.remove(event.getEntity())) {
      event.getEntity().remove();
    }
  }
  
  @EventHandler
  public void Fletch(UpdateEvent event) {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (!this._time.containsKey(cur)) {
        this._time.put(cur, Long.valueOf(System.currentTimeMillis()));
      }
      if (mineplex.core.common.util.UtilTime.elapsed(((Long)this._time.get(cur)).longValue(), 10000L))
      {

        if (!UtilInv.contains(cur, Material.ARROW, (byte)1, 8))
        {

          if (!this._fletchDisable.contains(cur))
          {

            this._time.put(cur, Long.valueOf(System.currentTimeMillis()));
            

            cur.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(262, 1, 1, "Fletched Arrow") });
          }
        }
      }
    }
  }
  
  @EventHandler
  public void Drop(PlayerDropItemEvent event)
  {
    if (event.getItemDrop().getItemStack().getType() != Material.ARROW) {
      return;
    }
    if (event.getItemDrop().getItemStack().getData() == null) {
      return;
    }
    if (event.getItemDrop().getItemStack().getData().getData() != 1) {
      return;
    }
    
    event.setCancelled(true);
    

    UtilPlayer.message(event.getPlayer(), F.main(GetName(), "You cannot drop " + F.item("Fletched Arrow") + "."));
  }
  
  @EventHandler
  public void Dead(PlayerDeathEvent event)
  {
    HashSet<ItemStack> remove = new HashSet();
    
    for (ItemStack item : event.getDrops()) {
      if ((item.getType() == Material.ARROW) && 
        (item.getData() != null) && 
        (item.getData().getData() == 1))
        remove.add(item);
    }
    for (ItemStack item : remove) {
      event.getDrops().remove(item);
    }
  }
  
  @EventHandler
  public void ChestRemove(PlayerInteractEvent event) {
    if (!mineplex.core.common.util.UtilEvent.isAction(event, mineplex.core.common.util.UtilEvent.ActionType.R_BLOCK)) {
      return;
    }
    if ((event.getClickedBlock().getType() != Material.CHEST) && 
      (event.getClickedBlock().getType() != Material.FURNACE) && 
      (event.getClickedBlock().getType() != Material.BURNING_FURNACE) && 
      (event.getClickedBlock().getType() != Material.WORKBENCH) && 
      (event.getClickedBlock().getType() != Material.DISPENSER) && 
      (event.getClickedBlock().getType() != Material.ENCHANTMENT_TABLE) && 
      (event.getClickedBlock().getType() != Material.BEACON)) {
      return;
    }
    UtilInv.removeAll(event.getPlayer(), Material.ARROW, (byte)1);
  }
  
  @EventHandler
  public void InvDisable(InventoryOpenEvent event)
  {
    if (getLevel(event.getPlayer()) > 0) {
      this._fletchDisable.add(event.getPlayer());
    }
  }
  
  @EventHandler
  public void InvEnable(InventoryCloseEvent event) {
    this._fletchDisable.remove(event.getPlayer());
  }
  
  @EventHandler
  public void InvClick(InventoryClickEvent event)
  {
    if (event.getCurrentItem() == null) {
      return;
    }
    if (event.getCurrentItem().getType() != Material.ARROW) {
      return;
    }
    if (event.getCurrentItem().getData() == null) {
      return;
    }
    if (event.getCurrentItem().getData().getData() != 1) {
      return;
    }
    event.setCancelled(true);
    

    UtilPlayer.message(event.getWhoClicked(), F.main(GetName(), "You cannot move " + F.item("Fletched Arrow") + "."));
  }
  
  @EventHandler
  public void Clean(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Iterator<Entity> arrowIterator = this._fletchArrows.iterator(); arrowIterator.hasNext();)
    {
      Entity arrow = (Entity)arrowIterator.next();
      
      if ((arrow.isDead()) || (!arrow.isValid())) {
        arrowIterator.remove();
      }
    }
  }
  
  public void Reset(Player player)
  {
    this._time.remove(player);
    this._fletchDisable.remove(player);
    UtilInv.removeAll(player, Material.ARROW, (byte)1);
  }
}
