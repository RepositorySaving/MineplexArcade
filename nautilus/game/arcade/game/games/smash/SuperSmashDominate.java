package nautilus.game.arcade.game.games.smash;

import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.GameType;
import nautilus.game.arcade.game.games.common.Domination;
import nautilus.game.arcade.game.games.smash.kits.KitBlaze;
import nautilus.game.arcade.game.games.smash.kits.KitChicken;
import nautilus.game.arcade.game.games.smash.kits.KitCreeper;
import nautilus.game.arcade.game.games.smash.kits.KitEnderman;
import nautilus.game.arcade.game.games.smash.kits.KitGolem;
import nautilus.game.arcade.game.games.smash.kits.KitMagmaCube;
import nautilus.game.arcade.game.games.smash.kits.KitPig;
import nautilus.game.arcade.game.games.smash.kits.KitSkeletalHorse;
import nautilus.game.arcade.game.games.smash.kits.KitSkeleton;
import nautilus.game.arcade.game.games.smash.kits.KitSkySquid;
import nautilus.game.arcade.game.games.smash.kits.KitSlime;
import nautilus.game.arcade.game.games.smash.kits.KitSnowman;
import nautilus.game.arcade.game.games.smash.kits.KitSpider;
import nautilus.game.arcade.game.games.smash.kits.KitWitch;
import nautilus.game.arcade.game.games.smash.kits.KitWitherSkeleton;
import nautilus.game.arcade.game.games.smash.kits.KitWolf;
import nautilus.game.arcade.kit.Kit;








public class SuperSmashDominate
  extends Domination
{
  public SuperSmashDominate(ArcadeManager manager)
  {
    super(manager, GameType.SmashDomination, new Kit[] {new KitSkeleton(manager), new KitGolem(manager), new KitSpider(manager), new KitSlime(manager), new KitCreeper(manager), new KitEnderman(manager), new KitSnowman(manager), new KitWolf(manager), new KitBlaze(manager), new KitWitch(manager), new KitChicken(manager), new KitSkeletalHorse(manager), new KitPig(manager), new KitSkySquid(manager), new KitWitherSkeleton(manager), new KitMagmaCube(manager) });
  }
}
