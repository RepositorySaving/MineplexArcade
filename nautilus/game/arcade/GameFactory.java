package nautilus.game.arcade;

import java.util.HashMap;
import nautilus.game.arcade.game.Game;
import nautilus.game.arcade.game.games.baconbrawl.BaconBrawl;
import nautilus.game.arcade.game.games.barbarians.Barbarians;
import nautilus.game.arcade.game.games.bridge.Bridge;
import nautilus.game.arcade.game.games.castlesiege.CastleSiege;
import nautilus.game.arcade.game.games.champions.ChampionsDominate;
import nautilus.game.arcade.game.games.champions.ChampionsTDM;
import nautilus.game.arcade.game.games.christmas.Christmas;
import nautilus.game.arcade.game.games.deathtag.DeathTag;
import nautilus.game.arcade.game.games.dragonescape.DragonEscape;
import nautilus.game.arcade.game.games.dragonescape.DragonEscapeTeams;
import nautilus.game.arcade.game.games.dragonriders.DragonRiders;
import nautilus.game.arcade.game.games.dragons.Dragons;
import nautilus.game.arcade.game.games.dragons.DragonsTeams;
import nautilus.game.arcade.game.games.draw.Draw;
import nautilus.game.arcade.game.games.evolution.Evolution;
import nautilus.game.arcade.game.games.gravity.Gravity;
import nautilus.game.arcade.game.games.halloween.Halloween;
import nautilus.game.arcade.game.games.hideseek.HideSeek;
import nautilus.game.arcade.game.games.milkcow.MilkCow;
import nautilus.game.arcade.game.games.mineware.MineWare;
import nautilus.game.arcade.game.games.paintball.Paintball;
import nautilus.game.arcade.game.games.quiver.Quiver;
import nautilus.game.arcade.game.games.quiver.QuiverTeams;
import nautilus.game.arcade.game.games.runner.Runner;
import nautilus.game.arcade.game.games.sheep.SheepGame;
import nautilus.game.arcade.game.games.smash.SuperSmash;
import nautilus.game.arcade.game.games.smash.SuperSmashDominate;
import nautilus.game.arcade.game.games.smash.SuperSmashTeam;
import nautilus.game.arcade.game.games.snake.Snake;
import nautilus.game.arcade.game.games.snowfight.SnowFight;
import nautilus.game.arcade.game.games.spleef.Spleef;
import nautilus.game.arcade.game.games.spleef.SpleefTeams;
import nautilus.game.arcade.game.games.squidshooter.SquidShooter;
import nautilus.game.arcade.game.games.stacker.Stacker;
import nautilus.game.arcade.game.games.survivalgames.SurvivalGames;
import nautilus.game.arcade.game.games.survivalgames.SurvivalGamesTeams;
import nautilus.game.arcade.game.games.turfforts.TurfForts;
import nautilus.game.arcade.game.games.uhc.UHC;
import nautilus.game.arcade.game.games.zombiesurvival.ZombieSurvival;
import org.bukkit.ChatColor;



public class GameFactory
{
  private ArcadeManager _manager;
  
  public GameFactory(ArcadeManager gameManager)
  {
    this._manager = gameManager;
  }
  
  public Game CreateGame(GameType gameType, HashMap<String, ChatColor> pastTeams)
  {
    if (gameType == GameType.Barbarians) return new Barbarians(this._manager);
    if (gameType == GameType.BaconBrawl) return new BaconBrawl(this._manager);
    if (gameType == GameType.Bridge) return new Bridge(this._manager);
    if (gameType == GameType.CastleSiege) return new CastleSiege(this._manager);
    if (gameType == GameType.Christmas) return new Christmas(this._manager);
    if (gameType == GameType.DeathTag) return new DeathTag(this._manager);
    if (gameType == GameType.ChampionsDominate) return new ChampionsDominate(this._manager);
    if (gameType == GameType.ChampionsTDM) return new ChampionsTDM(this._manager);
    if (gameType == GameType.Dragons) return new Dragons(this._manager);
    if (gameType == GameType.DragonsTeams) return new DragonsTeams(this._manager);
    if (gameType == GameType.DragonEscape) return new DragonEscape(this._manager);
    if (gameType == GameType.DragonEscapeTeams) return new DragonEscapeTeams(this._manager);
    if (gameType == GameType.DragonRiders) return new DragonRiders(this._manager);
    if (gameType == GameType.Draw) return new Draw(this._manager);
    if (gameType == GameType.Evolution) return new Evolution(this._manager);
    if (gameType == GameType.Gravity) return new Gravity(this._manager);
    if (gameType == GameType.Halloween) return new Halloween(this._manager);
    if (gameType == GameType.HideSeek) return new HideSeek(this._manager);
    if (gameType == GameType.MineWare) return new MineWare(this._manager);
    if (gameType == GameType.MilkCow) return new MilkCow(this._manager);
    if (gameType == GameType.Paintball) return new Paintball(this._manager);
    if (gameType == GameType.Quiver) return new Quiver(this._manager);
    if (gameType == GameType.QuiverTeams) return new QuiverTeams(this._manager);
    if (gameType == GameType.Runner) return new Runner(this._manager);
    if (gameType == GameType.SnowFight) return new SnowFight(this._manager);
    if (gameType == GameType.Sheep) return new SheepGame(this._manager);
    if (gameType == GameType.Smash) return new SuperSmash(this._manager);
    if (gameType == GameType.SmashTeams) return new SuperSmashTeam(this._manager);
    if (gameType == GameType.SmashDomination) return new SuperSmashDominate(this._manager);
    if (gameType == GameType.Snake) return new Snake(this._manager);
    if (gameType == GameType.Spleef) return new Spleef(this._manager);
    if (gameType == GameType.SpleefTeams) return new SpleefTeams(this._manager);
    if (gameType == GameType.SquidShooter) return new SquidShooter(this._manager);
    if (gameType == GameType.Stacker) return new Stacker(this._manager);
    if (gameType == GameType.SurvivalGames) return new SurvivalGames(this._manager);
    if (gameType == GameType.SurvivalGamesTeams) return new SurvivalGamesTeams(this._manager);
    if (gameType == GameType.TurfWars) return new TurfForts(this._manager);
    if (gameType == GameType.UHC) return new UHC(this._manager);
    if (gameType == GameType.ZombieSurvival) return new ZombieSurvival(this._manager);
    return null;
  }
}
