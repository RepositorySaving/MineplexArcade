package nautilus.game.arcade.kit.perks;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.kit.Perk;
import nautilus.game.arcade.ore.OreHider;
import nautilus.game.arcade.ore.OreObsfucation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PerkOreFinder extends Perk
{
  private HashMap<Material, Material> _blockMap = new HashMap();
  



  public PerkOreFinder()
  {
    super("Ore Finder", new String[] {C.cYellow + "Right-Click" + C.cGray + " with Pickaxe to " + C.cGreen + "Locate Ore", C.cGray + "Locates Ore of same type as your Pickaxe" });
    

    this._blockMap.put(Material.STONE_PICKAXE, Material.COAL_ORE);
    this._blockMap.put(Material.IRON_PICKAXE, Material.IRON_ORE);
    this._blockMap.put(Material.GOLD_PICKAXE, Material.GOLD_ORE);
    this._blockMap.put(Material.DIAMOND_PICKAXE, Material.DIAMOND_ORE);
  }
  
  @org.bukkit.event.EventHandler
  public void SearchOre(PlayerInteractEvent event)
  {
    if ((event.getAction() != Action.RIGHT_CLICK_AIR) && (event.getAction() != Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    if (this.Manager.GetGame() == null) {
      return;
    }
    if (event.getPlayer().getItemInHand() == null) {
      return;
    }
    if ((event.getClickedBlock() != null) && 
      (UtilBlock.usable(event.getClickedBlock()))) {
      return;
    }
    Material type = event.getPlayer().getItemInHand().getType();
    
    if (!this._blockMap.containsKey(type)) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this.Kit.HasKit(player)) {
      return;
    }
    String oreType = ItemStackFactory.Instance.GetName((Material)this._blockMap.get(type), (byte)0, false);
    
    if (!Recharge.Instance.use(player, "Ore Scanner", 30000L, true, false)) {
      return;
    }
    Block bestBlock = null;
    double bestDist = 10.0D;
    
    double dist;
    for (Block block : UtilBlock.getInRadius(player.getLocation(), 8.0D).keySet())
    {
      if (block.getType() == this._blockMap.get(type))
      {

        dist = UtilMath.offset(block.getLocation(), player.getLocation());
        
        if ((bestBlock == null) || (dist < bestDist))
        {
          bestBlock = block;
          bestDist = dist;
        }
      }
    }
    
    if ((this.Manager.GetGame() instanceof OreObsfucation))
    {
      OreHider ore = ((OreObsfucation)this.Manager.GetGame()).GetOreHider();
      
      for (Location loc : ore.GetHiddenOre().keySet())
      {
        if (ore.GetHiddenOre().get(loc) == this._blockMap.get(type))
        {

          double dist = UtilMath.offset(loc, player.getLocation());
          
          if (dist <= 8.0D)
          {

            if ((bestBlock == null) || (dist < bestDist))
            {
              bestBlock = loc.getBlock();
              bestDist = dist;
            } }
        }
      }
    }
    UtilPlayer.message(player, F.main("Skill", "Scanning for " + F.skill(oreType) + "..."));
    
    if (bestBlock == null)
    {
      UtilPlayer.message(player, F.main("Skill", "No " + F.skill(oreType) + " found."));
    }
    else
    {
      org.bukkit.util.Vector vec = UtilAlg.getTrajectory(player.getEyeLocation(), bestBlock.getLocation().add(0.5D, 0.5D, 0.5D));
      
      Location loc = player.getLocation();
      loc.setPitch(UtilAlg.GetPitch(vec));
      loc.setYaw(UtilAlg.GetYaw(vec));
      
      player.teleport(loc);
      
      UtilPlayer.message(player, F.main("Skill", "Located nearby " + F.skill(oreType) + "!"));
    }
  }
}
