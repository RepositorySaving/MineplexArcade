package nautilus.game.arcade.kit.perks;

import java.util.HashSet;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.kit.Perk;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PerkConstructor extends Perk
{
  private int _max = 0;
  private double _time = 0.0D;
  
  private Material _type;
  private String _name = "";
  



  public PerkConstructor(String perkName, double time, int max, Material mat, String name, boolean visible)
  {
    super(perkName, new String[] {C.cGray + "Receive 1 " + ItemStackFactory.Instance.GetName(mat, 0, false) + " every " + time + " seconds. Maximum of " + max + "." }, visible);
    
    this._time = time;
    this._max = max;
    this._type = mat;
    
    if (name == null) {
      this._name = ItemStackFactory.Instance.GetName(mat, (byte)0, false);
    } else {
      this._name = name;
    }
  }
  
  @EventHandler
  public void Construct(UpdateEvent event) {
    if (event.getType() != mineplex.core.updater.UpdateType.FAST) {
      return;
    }
    for (Player cur : UtilServer.getPlayers())
    {
      if (this.Kit.HasKit(cur))
      {

        if (this.Manager.GetGame().IsAlive(cur))
        {

          if (Recharge.Instance.use(cur, GetName(), (this._time * 1000.0D), false, false))
          {

            if (!UtilInv.contains(cur, this._type, (byte)0, this._max))
            {


              byte data = 0;
              if (this._type == Material.WOOL)
              {
                GameTeam team = this.Manager.GetGame().GetTeam(cur);
                if (team != null) {
                  data = team.GetColorData();
                }
                if (!UtilInv.contains(cur, this._type, data, this._max))
                {

                  cur.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.WOOL, team.GetColorData(), 1) });
                }
              }
              else {
                cur.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(this._type, data, 1, F.item(this._name)) });
                
                cur.playSound(cur.getLocation(), org.bukkit.Sound.ITEM_PICKUP, 2.0F, 1.0F);
              }
            } } } } }
  }
  
  @EventHandler
  public void Drop(PlayerDropItemEvent event) {
    if (!UtilInv.IsItem(event.getItemDrop().getItemStack(), this._type, (byte)0)) {
      return;
    }
    
    event.setCancelled(true);
    

    mineplex.core.common.util.UtilPlayer.message(event.getPlayer(), F.main(GetName(), "You cannot drop " + F.item(this._name) + "."));
  }
  
  @EventHandler
  public void DeathRemove(PlayerDeathEvent event)
  {
    HashSet<ItemStack> remove = new HashSet();
    
    for (ItemStack item : event.getDrops()) {
      if (UtilInv.IsItem(item, this._type, (byte)0))
        remove.add(item);
    }
    for (ItemStack item : remove) {
      event.getDrops().remove(item);
    }
  }
  
  @EventHandler
  public void InvClick(InventoryClickEvent event) {
    UtilInv.DisallowMovementOf(event, this._name, this._type, (byte)0, true);
  }
}
