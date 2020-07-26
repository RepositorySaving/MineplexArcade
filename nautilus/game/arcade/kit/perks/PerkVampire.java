package nautilus.game.arcade.kit.perks; import mineplex.minecraft.game.core.combat.event.CombatDeathEvent;

public class PerkVampire extends nautilus.game.arcade.kit.Perk { public PerkVampire(int recover) { super(
    













      "Vampire", new String[] { mineplex.core.common.util.C.cGray + "You heal " + recover + "HP when you kill someone" });
    



    this._recover = recover;
  }
  
  private int _recover;
  @org.bukkit.event.EventHandler
  public void PlayerKillAward(CombatDeathEvent event) {
    nautilus.game.arcade.game.Game game = this.Manager.GetGame();
    if (game == null) { return;
    }
    if (!(event.GetEvent().getEntity() instanceof org.bukkit.entity.Player)) {
      return;
    }
    if (event.GetLog().GetKiller() == null) {
      return;
    }
    org.bukkit.entity.Player killer = mineplex.core.common.util.UtilPlayer.searchExact(event.GetLog().GetKiller().GetName());
    if (killer == null) {
      return;
    }
    mineplex.core.common.util.UtilPlayer.health(killer, this._recover);
  }
}
