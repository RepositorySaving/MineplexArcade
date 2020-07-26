package nautilus.game.arcade;

public enum GameType
{
  BaconBrawl(
    "Bacon Brawl"), 
  Barbarians("A Barbarians Life"), 
  Bridge("The Bridges"), 
  CastleSiege("Castle Siege"), 
  ChampionsTDM("Champions TDM", "Champions"), 
  ChampionsDominate("Champions Domination", "Champions"), 
  ChampionsMOBA("Champions MOBA", "Champions"), 
  Christmas("Christmas Chaos"), 
  DeathTag("Death Tag"), 
  DragonEscape("Dragon Escape"), 
  DragonEscapeTeams("Dragon Escape Teams"), 
  DragonRiders("Dragon Riders"), 
  Dragons("Dragons"), 
  DragonsTeams("Dragons Teams"), 
  Draw("Draw My Thing"), 
  Evolution("Evolution"), 
  FlappyBird("Flappy Bird"), 
  Gravity("Gravity"), 
  Halloween("Halloween Horror"), 
  HideSeek("Block Hunt"), 
  Horse("Horseback"), 
  SurvivalGames("Survival Games"), 
  SurvivalGamesTeams("Survival Games Teams"), 
  MineWare("MineWare"), 
  MilkCow("Milk the Cow"), 
  Paintball("Super Paintball"), 
  Quiver("One in the Quiver"), 
  QuiverTeams("One in the Quiver Teams"), 
  Runner("Runner"), 
  Sheep("Sheep Quest"), 
  Smash("Super Smash Mobs"), 
  SmashTeams("Super Smash Mobs Teams", "Super Smash Mobs"), 
  SmashDomination("Super Smash Mobs Domination", "Super Smash Mobs"), 
  Snake("Snake"), 
  SnowFight("Snow Fight"), 
  Spleef("Super Spleef"), 
  SpleefTeams("Super Spleef Teams"), 
  Stacker("Super Stacker"), 
  SquidShooter("Squid Shooter"), 
  TurfWars("Turf Wars"), 
  UHC("Ultra Hardcore"), 
  ZombieSurvival("Zombie Survival");
  
  String _name;
  String _lobbyName;
  
  private GameType(String name)
  {
    this._name = name;
    this._lobbyName = name;
  }
  
  private GameType(String name, String lobbyName)
  {
    this._name = name;
    this._lobbyName = lobbyName;
  }
  
  public String GetName()
  {
    return this._name;
  }
  
  public String GetLobbyName()
  {
    return this._lobbyName;
  }
}
