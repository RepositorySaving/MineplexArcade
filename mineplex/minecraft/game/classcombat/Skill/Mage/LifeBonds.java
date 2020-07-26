package mineplex.minecraft.game.classcombat.Skill.Mage;

import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.energy.Energy;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.minecraft.game.classcombat.Class.IPvpClass.ClassType;
import mineplex.minecraft.game.classcombat.Skill.ISkill.SkillType;
import mineplex.minecraft.game.classcombat.Skill.Skill;
import mineplex.minecraft.game.classcombat.Skill.SkillFactory;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.core.IRelation;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.Vector;

public class LifeBonds extends Skill
{
  private HashSet<Player> _active = new HashSet();
  private HashSet<Item> _items = new HashSet();
  
  private HashSet<LifeBondsData> _hearts = new HashSet();
  
  public LifeBonds(SkillFactory skills, String name, IPvpClass.ClassType classType, ISkill.SkillType skillType, int cost, int levels)
  {
    super(skills, name, classType, skillType, cost, levels);
    
    SetDesc(
      new String[] {
      "Drop Axe/Sword to Toggle.", 
      "", 
      "Transfers life from healthy allies", 
      "to nearby allies with less health.", 
      "", 
      "Transfers #0.5#0.5 health every second.", 
      "Maximum range of #3#3 Blocks from user." });
  }
  


  public String GetEnergyString()
  {
    return "Energy: #4.5#-0.5 per Second";
  }
  
  @EventHandler
  public void Toggle(PlayerDropItemEvent event)
  {
    Player player = event.getPlayer();
    
    if (getLevel(player) == 0) {
      return;
    }
    if (!UtilGear.isWeapon(event.getItemDrop().getItemStack())) {
      return;
    }
    event.setCancelled(true);
    

    SkillTriggerEvent trigger = new SkillTriggerEvent(player, GetName(), GetClassType());
    UtilServer.getServer().getPluginManager().callEvent(trigger);
    if (trigger.IsCancelled()) {
      return;
    }
    if (this._active.contains(player))
    {
      Remove(player);
    }
    else
    {
      if (!this.Factory.Energy().Use(player, "Enable " + GetName(), 10.0D, true, true)) {
        return;
      }
      Add(player);
    }
  }
  
  public void Add(Player player)
  {
    this._active.add(player);
    UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.oo("Enabled", true)));
  }
  
  public void Remove(Player player)
  {
    this._active.remove(player);
    UtilPlayer.message(player, F.main(GetClassType().name(), GetName() + ": " + F.oo("Disabled", false)));
  }
  
  @EventHandler
  public void Energy(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {


        int level = getLevel(cur);
        if (level == 0)
        {
          Remove(cur);

        }
        else
        {
          SkillTriggerEvent trigger = new SkillTriggerEvent(cur, GetName(), GetClassType());
          UtilServer.getServer().getPluginManager().callEvent(trigger);
          if (trigger.IsCancelled())
          {
            Remove(cur);



          }
          else if (!this.Factory.Energy().Use(cur, GetName(), 0.225D - level * 0.025D, true, true))
          {
            this._active.remove(cur);
            UtilPlayer.message(cur, F.main(GetClassType().name(), GetName() + ": " + F.oo("Disabled", false)));
          }
        }
      }
    }
  }
  
  @EventHandler
  public void Plants(UpdateEvent event) {
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {

        for (Player other : UtilPlayer.getNearby(cur.getLocation(), 8.0D))
        {
          if ((!this.Factory.Relation().CanHurt(cur, other)) || (other.equals(cur)))
          {




            double r = Math.random();
            ItemStack stack;
            ItemStack stack; if (r > 0.4D) { stack = ItemStackFactory.Instance.CreateStack(31, (byte)1); } else { ItemStack stack;
              if (r > 0.11D) { stack = ItemStackFactory.Instance.CreateStack(31, (byte)2); } else { ItemStack stack;
                if (r > 0.05D) stack = ItemStackFactory.Instance.CreateStack(37, (byte)0); else
                  stack = ItemStackFactory.Instance.CreateStack(38, (byte)0);
              } }
            Item item = other.getWorld().dropItem(other.getLocation().add(0.0D, 0.4D, 0.0D), stack);
            this._items.add(item);
            
            Vector vec = new Vector(Math.random() - 0.5D, Math.random() / 2.0D + 0.2D, Math.random() - 0.5D).normalize();
            vec.multiply(0.1D + Math.random() / 8.0D);
            item.setVelocity(vec);
          } }
      }
    }
  }
  
  @EventHandler
  public void LifeTransfer(UpdateEvent event) {
    if (event.getType() != UpdateType.SEC) {
      return;
    }
    for (Player cur : GetUsers())
    {
      if (this._active.contains(cur))
      {

        int level = getLevel(cur);
        

        Player highest = null;
        double highestHp = 0.0D;
        
        Player lowest = null;
        double lowestHp = 20.0D;
        
        for (Player other : UtilPlayer.getNearby(cur.getLocation(), 6 + 2 * level))
        {
          if ((!this.Factory.Relation().CanHurt(cur, other)) || (other.equals(cur)))
          {

            if ((highest == null) || (other.getHealth() > highestHp))
            {
              highest = other;
              highestHp = other.getHealth();
            }
            
            if ((lowest == null) || (other.getHealth() < lowestHp))
            {
              lowest = other;
              lowestHp = other.getHealth();
            }
          }
        }
        
        if ((highest != null) && (lowest != null) && (!highest.equals(lowest)) && (highestHp - lowestHp >= 2.0D))
        {

          double amount = 0.5D + 0.5D * level;
          
          amount = Math.min((highestHp - lowestHp) / 2.0D, amount);
          

          UtilPlayer.health(highest, -amount);
          

          this._hearts.add(new LifeBondsData(highest.getLocation().add(0.0D, 0.8D, 0.0D), lowest, amount));
        }
      }
    }
  }
  


  @EventHandler
  public void Hearts(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    Iterator<LifeBondsData> heartIterator = this._hearts.iterator();
    
    while (heartIterator.hasNext())
    {
      LifeBondsData data = (LifeBondsData)heartIterator.next();
      
      if (data.Update()) {
        heartIterator.remove();
      }
    }
  }
  
  @EventHandler
  public void ItemPickup(PlayerPickupItemEvent event) {
    if (this._items.contains(event.getItem())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void HopperPickup(InventoryPickupItemEvent event) {
    if (this._items.contains(event.getItem())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void ItemDestroy(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this._items.isEmpty()) {
      return;
    }
    HashSet<Item> remove = new HashSet();
    
    for (Item cur : this._items) {
      if ((UtilEnt.isGrounded(cur)) || (cur.getTicksLived() > 400) || (cur.isDead()) || (!cur.isValid()))
        remove.add(cur);
    }
    for (Item cur : remove)
    {
      Block block = cur.getLocation().getBlock();
      if (block.getTypeId() == 0)
      {
        int below = block.getRelative(BlockFace.DOWN).getTypeId();
        if ((below == 2) || (below == 3))
        {
          byte data = 0;
          if (cur.getItemStack().getData() != null) {
            data = cur.getItemStack().getData().getData();
          }
          this.Factory.BlockRestore().Add(block, cur.getItemStack().getTypeId(), data, 2000L);
        }
      }
      
      this._items.remove(cur);
      cur.remove();
    }
  }
  

  public void Reset(Player player)
  {
    this._active.remove(player);
  }
}
