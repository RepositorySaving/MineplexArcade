package nautilus.game.arcade.game.games.hideseek.forms;

import java.io.PrintStream;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.MapUtil;
import mineplex.core.common.util.UtilInv;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseCat;
import mineplex.core.disguise.disguises.DisguiseChicken;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.game.games.hideseek.HideSeek;
import nautilus.game.arcade.game.games.hideseek.kits.KitHiderQuick;
import net.minecraft.server.v1_7_R3.DataWatcher;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftFallingSand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BlockForm extends Form
{
  private Material _mat;
  private Block _block;
  private Location _loc;
  
  public BlockForm(HideSeek host, Player player, Material mat)
  {
    super(host, player);
    
    this._mat = mat;
    this._loc = player.getLocation();
    
    System.out.println("Block Form: " + this._mat + " " + this._mat.getId());
    
    Apply();
  }
  


  public void Apply()
  {
    if (this.Player.getPassenger() != null)
    {
      Recharge.Instance.useForce(this.Player, "PassengerChange", 100L);
      
      this.Player.getPassenger().remove();
      this.Player.eject();
    }
    
    ((CraftEntity)this.Player).getHandle().getDataWatcher().watch(0, Byte.valueOf((byte)32));
    

    DisguiseChicken disguise = new DisguiseChicken(this.Player);
    disguise.setBaby();
    disguise.setSoundDisguise(new DisguiseCat(this.Player));
    this.Host.Manager.GetDisguise().disguise(disguise);
    

    FallingBlockCheck();
    

    String blockName = F.elem(ItemStackFactory.Instance.GetName(this._mat, (byte)0, false));
    if (!blockName.contains("Block")) {
      UtilPlayer.message(this.Player, F.main("Game", C.cWhite + "You are now a " + F.elem(new StringBuilder(String.valueOf(ItemStackFactory.Instance.GetName(this._mat, (byte)0, false))).append(" Block").toString()) + "!"));
    } else {
      UtilPlayer.message(this.Player, F.main("Game", C.cWhite + "You are now a " + F.elem(ItemStackFactory.Instance.GetName(this._mat, (byte)0, false)) + "!"));
    }
    
    this.Player.getInventory().setItem(8, new ItemStack(this.Host.GetItemEquivilent(this._mat)));
    UtilInv.Update(this.Player);
    

    this.Player.playSound(this.Player.getLocation(), Sound.ZOMBIE_UNFECT, 2.0F, 2.0F);
  }
  

  public void Remove()
  {
    SolidifyRemove();
    
    this.Host.Manager.GetDisguise().undisguise(this.Player);
    

    if (this.Player.getPassenger() != null)
    {
      Recharge.Instance.useForce(this.Player, "PassengerChange", 100L);
      
      this.Player.getPassenger().remove();
      this.Player.eject();
    }
    
    ((CraftEntity)this.Player).getHandle().getDataWatcher().watch(0, Byte.valueOf((byte)0));
  }
  
  public void SolidifyUpdate()
  {
    if (!this.Player.isSprinting()) {
      ((CraftEntity)this.Player).getHandle().getDataWatcher().watch(0, Byte.valueOf((byte)32));
    }
    
    if (this._block == null)
    {

      if (!this._loc.getBlock().equals(this.Player.getLocation().getBlock()))
      {
        this.Player.setExp(0.0F);
        this._loc = this.Player.getLocation();

      }
      else
      {
        double hideBoost = 0.025D;
        if ((this.Host.GetKit(this.Player) instanceof KitHiderQuick)) {
          hideBoost = 0.1D;
        }
        this.Player.setExp((float)Math.min(0.9990000128746033D, this.Player.getExp() + hideBoost));
        

        if (this.Player.getExp() >= 0.999F)
        {
          Block block = this.Player.getLocation().getBlock();
          

          if ((block.getType() != Material.AIR) || (!mineplex.core.common.util.UtilBlock.solid(block.getRelative(BlockFace.DOWN))))
          {
            UtilPlayer.message(this.Player, F.main("Game", "You cannot become a Solid Block here."));
            this.Player.setExp(0.0F);
            return;
          }
          

          this._block = block;
          

          this.Player.playEffect(this.Player.getLocation(), Effect.STEP_SOUND, this._mat);
          


          SolidifyVisual();
          




          this.Player.playSound(this.Player.getLocation(), Sound.NOTE_PLING, 1.0F, 2.0F);

        }
        
      }
      

    }
    else if (!this._loc.getBlock().equals(this.Player.getLocation().getBlock()))
    {
      SolidifyRemove();

    }
    else
    {
      SolidifyVisual();
    }
  }
  

  public void SolidifyRemove()
  {
    if (this._block != null)
    {
      MapUtil.QuickChangeBlockAt(this._block.getLocation(), 0, (byte)0);
      this._block = null;
    }
    
    this.Player.setExp(0.0F);
    



    this.Player.playSound(this.Player.getLocation(), Sound.NOTE_PLING, 1.0F, 0.5F);
    
    FallingBlockCheck();
  }
  

  public void SolidifyVisual()
  {
    if (this._block == null) {
      return;
    }
    
    if (this.Player.getPassenger() != null)
    {
      Recharge.Instance.useForce(this.Player, "PassengerChange", 100L);
      
      this.Player.getPassenger().remove();
      this.Player.eject();
    }
    

    for (Player other : mineplex.core.common.util.UtilServer.getPlayers()) {
      other.sendBlockChange(this.Player.getLocation(), this._mat, (byte)0);
    }
    
    this.Player.sendBlockChange(this.Player.getLocation(), 36, (byte)0);
    
    FallingBlockCheck();
  }
  

  public void FallingBlockCheck()
  {
    if (this._block != null) {
      return;
    }
    
    if ((this.Player.getPassenger() == null) || (!this.Player.getPassenger().isValid()))
    {
      if (!Recharge.Instance.use(this.Player, "PassengerChange", 100L, false, false)) {
        return;
      }
      
      FallingBlock block = this.Player.getWorld().spawnFallingBlock(this.Player.getEyeLocation(), this._mat, (byte)0);
      

      ((CraftFallingSand)block).getHandle().spectating = true;
      
      this.Player.setPassenger(block);

    }
    else
    {

      ((CraftFallingSand)this.Player.getPassenger()).getHandle().b = 1;
      this.Player.getPassenger().setTicksLived(1);
    }
  }
  

  public Block GetBlock()
  {
    return this._block;
  }
}
