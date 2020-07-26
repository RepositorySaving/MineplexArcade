package nautilus.game.arcade.game.games.draw;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilDisplay;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilText;
import mineplex.core.common.util.UtilText.TextAlign;
import mineplex.core.common.util.UtilTime;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import nautilus.game.arcade.ArcadeManager;
import nautilus.game.arcade.events.GameStateChangeEvent;
import nautilus.game.arcade.game.Game.GameState;
import nautilus.game.arcade.game.GameTeam;
import nautilus.game.arcade.game.SoloGame;
import nautilus.game.arcade.game.games.GameScore;
import nautilus.game.arcade.game.games.draw.kits.KitPlayer;
import nautilus.game.arcade.kit.Kit;
import nautilus.game.arcade.world.WorldData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class Draw extends SoloGame
{
  private ArrayList<GameScore> _ranks = new ArrayList();
  private ArrayList<String> _lastScoreboard = new ArrayList();
  
  private GameTeam _drawers = null;
  private GameTeam _guessers = null;
  

  private byte _brushColor = 15;
  private Location _brushPrevious = null;
  
  private boolean _lockDrawer = true;
  

  private int _roundCount = 0;
  private int _roundMax = 0;
  private ArrayList<Player> _roundPlayer = new ArrayList();
  private long _roundTime = 0L;
  private DrawRound _round = null;
  
  private ArrayList<Block> _canvas = new ArrayList();
  private Location _drawerLocation = null;
  private Location _textLocation = null;
  
  private Collection<Block> _textBlocks = null;
  
  private String[] _words;
  private HashSet<String> _usedWords = new HashSet();
  











  public Draw(ArcadeManager manager)
  {
    super(manager, nautilus.game.arcade.GameType.Draw, new Kit[] {new KitPlayer(manager) }, new String[] {"Take turns to draw something", "Right-Click with Swords to draw", "Hints are given at top of screen" });
    

    this.Damage = false;
    this.HungerSet = 20;
    this.WorldTimeSet = 8000;
    
    this._words = 
      new String[] {
      "Bird", "Volcano", "Love", "Dance", "Hair", "Glasses", "Domino", "Dice", "Computer", "Top Hat", "Beard", "Wind", "Rain", "Minecraft", "Push", "Fighting", "Juggle", "Clown", "Miner", "Creeper", "Ghast", "Spider", "Punch", "Roll", "River", "Desert", "Cold", "Pregnant", "Photo", "Quick", "Mario", "Luigi", "Bridge", "Turtle", "Door Knob", "Mineplex", "Binoculars", "Telescope", "Planet", "Mountain Bike", "Moon", "Comet", "Flower", "Squirrel", "Horse Riding", "Chef", "Elephant", "Yoshi", "Shotgun", "Pistol", "James Bond", "Money", "Salt and Pepper", "Truck", "Helicopter", "Hot Air Balloon", "Sprout", "Yelling", "Muscles", "Skinny", "Zombie", "Lava", "Snake", "Motorbike", "Whale", "Boat", "Letterbox", "Window", "Lollipop", "Handcuffs", "Police", "Uppercut", "Windmill", "Eyepatch", "Campfire", "Rainbow", "Storm", "Pikachu", "Charmander", "Tornado", "Crying", "King", "Hobo", "Worm", "Snail", "XBox", "Playstation", "Nintendo", "Duck", "Pull", "Dinosaur", "Alligator", "Ankle", "Angel", "Acorn", "Bread", "Booty", "Bacon", "Crown", "Donut", "Drill", "Crack", "Leash", "Magic", "Wizard", "Igloo", "Plant", "Screw", "Rifle", "Puppy", "Stool", "Stamp", "Letter", "Witch", "Zebra", "Wagon", "Compass", "Watch", "Clock", "Time", "Cyclops", "Coconut", "Hang", "Penguin", "Confused", "Bucket", "Lion", "Rubbish", "Spaceship", "Bowl", "Shark", "Pizza", "Pyramid", "Dress", "Pants", "Shorts", "Boots", "Boy", "Girl", "Math", "Sunglasses", "Frog", "Chair", "Cake", "Grapes", "Kiss", "Snorlax", "Earth", "Spaghetti", "Couch", "Family", "Milk", "Blood", "Pig", "Giraffe", "Mouse", "Couch", "Fat", "Chocolate", "Camel", "Cheese", "Beans", "Water", "Chicken", "Cannibal", "Zipper", "Book", "Swimming", "Horse", "Paper", "Toaster", "Television", "Hammer", "Piano", "Sleeping", "Yawn", "Sheep", "Night", "Chest", "Lamp", "Redstone", "Grass", "Plane", "Ocean", "Lake", "Melon", "Pumpkin", "Gift", "Fishing", "Pirate", "Lightning", "Stomach", "Belly Button", "Fishing Rod", "Iron Ore", "Diamonds", "Emeralds", "Nether Portal", "Ender Dragon", "Rabbit", "Harry Potter", "Torch", "Light", "Battery", "Zombie Pigman", "Telephone", "Tent", "Hand", "Traffic Lights", "Anvil", "Tail", "Umbrella", "Piston", "Skeleton", "Spikes", "Bridge", "Bomb", "Spoon", "Rainbow", "Staircase", "Poop", "Dragon", "Fire", "Apple", "Shoe", "Squid", "Cookie", "Tooth", "Camera", "Sock", "Monkey", "Unicorn", "Smile", "Pool", "Rabbit", "Cupcake", "Pancake", "Princess", "Castle", "Flag", "Planet", "Stars", "Camp Fire", "Rose", "Spray", "Pencil", "Ice Cream", "Toilet", "Moose", "Bear", "Beer", "Batman", "Eggs", "Teapot", "Golf Club", "Tennis Racket", "Shield", "Crab", "Pot of Gold", "Cactus", "Television", "Pumpkin Pie", "Chimney", "Stable", "Nether", "Wither", "Beach", "Stop Sign", "Chestplate", "Pokeball", "Christmas Tree", "Present", "Snowflake", "Laptop", "Superman", "Football", "Basketball", "Creeper", "Tetris", "Jump", "Ninja", "Baby", "Troll Face", "Grim Reaper", "Temple", "Explosion", "Vomit", "Ants", "Barn", "Burn", "Baggage", "Frisbee", "Iceberg", "Sleeping", "Dream", "Snorlax", "Balloons", "Elevator", "Alligator", "Bikini", "Butterfly", "Bumblebee", "Pizza", "Jellyfish", "Sideburns", "Speedboat", "Treehouse", "Water Gun", "Drink", "Hook", "Dance", "Fall", "Summer", "Autumn", "Spring", "Winter", "Night Time", "Galaxy", "Sunrise", "Sunset", "Picnic", "Snowflake", "Holding Hands", "America", "Laptop", "Anvil", "Bagel", "Bench", "Cigar", "Darts", "Muffin", "Queen", "Wheat", "Dolphin", "Scarf", "Swing", "Thumb", "Tomato", "Alcohol", "Armor", "Alien", "Beans", "Cheek", "Phone", "Keyboard", "Orange", "Calculator", "Paper", "Desk", "Disco", "Elbow", "Drool", "Giant", "Golem", "Grave", "Llama", "Moose", "Party", "Panda", "Plumber", "Salsa", "Salad", "Skunk", "Skull", "Stump", "Sugar", "Ruler", "Bookcase", "Hamster", "Soup", "Teapot", "Towel", "Waist", "Archer", "Anchor", "Bamboo", "Branch", "Booger", "Carrot", "Cereal", "Coffee", "Wolf", "Crayon", "Finger", "Forest", "Hotdog", "Burger", "Obsidian", "Pillow", "Swing", "YouTube", "Farm", "Rain", "Cloud", "Frozen", "Garbage", "Music", "Twitter", "Facebook", "Santa Hat", "Rope", "Neck", "Sponge", "Sushi", "Noodles", "Soup", "Tower", "Berry", "Capture", "Prison", "Robot", "Trash", "School", "Skype", "Snowman", "Crowd", "Bank", "Mudkip", "Joker", "Lizard", "Tiger", "Royal", "Erupt", "Wizard", "Stain", "Cinema", "Notebook", "Blanket", "Paint", "Guard", "Astronaut", "Slime", "Mansion", "Radar", "Thorn", "Tears", "Tiny", "Candy", "Pepsi", "Flint", "Draw My Thing", "Rice", "Shout", "Prize", "Skirt", "Thief", "Syrup", "Kirby", "Brush", "Violin" };
  }
  


  public void ParseData()
  {
    for (Location loc : this.WorldData.GetCustomLocs("159")) {
      this._canvas.add(loc.getBlock());
    }
    this._drawerLocation = ((Location)this.WorldData.GetDataLocs("RED").get(0));
    this._textLocation = ((Location)this.WorldData.GetDataLocs("YELLOW").get(0));
    
    Reset();
  }
  

  @EventHandler
  public void CustomTeamGeneration(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Recruit) {
      return;
    }
    this._guessers = ((GameTeam)GetTeamList().get(0));
    this._guessers.SetName("Guessers");
    this._guessers.SetColor(ChatColor.GREEN);
  }
  
  @EventHandler
  public void CreateDrawers(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    
    this._drawers = new GameTeam(this, "Drawer", ChatColor.RED, this.WorldData.GetDataLocs("RED"));
    GetTeamList().add(this._drawers);
  }
  
  @EventHandler
  public void AddDrawers(GameStateChangeEvent event)
  {
    if (event.GetState() != Game.GameState.Prepare) {
      return;
    }
    ArrayList<Player> players = GetPlayers(true);
    
    for (int i = 0; i < 2; i++) {
      for (Player player : players)
      {
        this._roundPlayer.add(player);
        this._roundMax += 1;
      }
    }
  }
  
  @EventHandler
  public void RemoveDrawer(PlayerQuitEvent event) {
    while (this._roundPlayer.contains(event.getPlayer()))
    {
      this._roundPlayer.remove(event.getPlayer());
      this._roundMax -= 1;
    }
    

    Iterator<GameScore> scoreIterator = this._ranks.iterator();
    
    while (scoreIterator.hasNext())
    {
      GameScore score = (GameScore)scoreIterator.next();
      
      if (score.Player.equals(event.getPlayer())) {
        scoreIterator.remove();
      }
    }
  }
  
  @EventHandler
  public void RoundUpdate(UpdateEvent event) {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    
    if ((this._round != null) && ((this._round.IsDone()) || (this._drawers.GetPlayers(true).isEmpty()) || (this._round.AllGuessed(this._guessers.GetPlayers(true)))))
    {
      Announce(C.cGold + C.Bold + "Round " + (this._roundCount + 1) + " Ended: " + C.cYellow + C.Bold + "The word was " + this._round.Word + "!");
      this._textBlocks = UtilText.MakeText(this._round.Word, this._textLocation, org.bukkit.block.BlockFace.WEST, 159, (byte)15, UtilText.TextAlign.CENTER);
      
      this._roundTime = System.currentTimeMillis();
      this._round = null;
      

      for (Player player : this._drawers.GetPlayers(false))
      {
        this._drawers.RemovePlayer(player);
        this._guessers.AddPlayer(player);
        mineplex.core.common.util.UtilInv.Clear(player);
        
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFlySpeed(0.1F);
        
        player.teleport(this._guessers.GetSpawn());
      }
      
      this._roundCount += 1;
      
      EndCheck();
    }
    
    if ((this._round == null) && (UtilTime.elapsed(this._roundTime, 5000L)) && (!this._roundPlayer.isEmpty()))
    {
      Reset();
      

      Player drawer = (Player)this._roundPlayer.remove(0);
      this._guessers.RemovePlayer(drawer);
      this._drawers.AddPlayer(drawer);
      

      String word = this._words[UtilMath.r(this._words.length)];
      while (!this._usedWords.add(word)) {
        word = this._words[UtilMath.r(this._words.length)];
      }
      
      this._round = new DrawRound(this, drawer, word);
      

      drawer.teleport(this._drawerLocation);
      
      drawer.setAllowFlight(true);
      drawer.setFlying(true);
      drawer.setFlySpeed(0.4F);
      
      drawer.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.WOOD_SWORD, 0, 1, "Thin Paint Brush") });
      drawer.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD, 0, 1, "Thick Paint Brush") });
      drawer.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.BUCKET, 0, 1, "Paint Bucket") });
      drawer.getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.TNT, 0, 1, "Clear Canvas") });
      
      Announce(C.cGold + C.Bold + "Round " + (this._roundCount + 1) + ": " + C.cYellow + C.Bold + drawer.getName() + " is drawing!");
      
      UtilPlayer.message(drawer, C.cWhite + C.Bold + "You must draw: " + C.cGreen + C.Bold + this._round.Word);
    }
  }
  
  @EventHandler(priority=org.bukkit.event.EventPriority.LOWEST)
  public void Guess(AsyncPlayerChatEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (this._round == null) {
      return;
    }
    Player player = event.getPlayer();
    
    if (event.getMessage().toLowerCase().contains(this._round.Word.toLowerCase()))
    {
      if (this._guessers.HasPlayer(player))
      {

        int score = 1;
        if (this._round.Guessed.isEmpty())
        {
          score = 3;
          

          AddScore(this._round.Drawer, 2.0D);
          
          AddGems(this._round.Drawer, 2.0D, "Drawings Guessed", true);
        }
        
        if (this._round.Guessed(player))
        {
          AddScore(player, score);
          Announce(C.cYellow + C.Bold + "+" + score + " " + C.cGreen + C.Bold + player.getName() + " has guessed the word!");
          
          if (score == 1) {
            AddGems(player, 1.0D, "Words Guessed", true);
          } else {
            AddGems(player, 4.0D, "Words Guessed First", true);
          }
        }
        else {
          UtilPlayer.message(player, mineplex.core.common.util.F.main("Game", "You have already guessed the word!"));
        }
      }
      
      event.setCancelled(true);
      return;
    }
  }
  
  @EventHandler
  public void TextUpdate(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    if (this._round == null) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (this._drawers.HasPlayer(player))
      {
        UtilDisplay.displayTextBar(this.Manager.GetPlugin(), player, this._round.GetTimePercent(), C.cYellow + C.Bold + "Draw  " + ChatColor.RESET + C.Bold + this._round.Word);
      }
      else
      {
        UtilDisplay.displayTextBar(this.Manager.GetPlugin(), player, this._round.GetTimePercent(), C.cYellow + C.Bold + "Guess  " + ChatColor.RESET + C.Bold + this._round.GetRevealedWord());
      }
    }
  }
  
  @EventHandler
  public void DrawerMove(PlayerMoveEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (!this._lockDrawer) {
      return;
    }
    if (!this._drawers.HasPlayer(event.getPlayer())) {
      return;
    }
    if (UtilMath.offset(event.getFrom(), event.getTo()) > 0.0D)
    {
      event.setTo(event.getFrom());
      
      Player player = event.getPlayer();
      
      if (Recharge.Instance.use(player, "Instruct", 1000L, false, false))
      {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 0.5F);
        UtilPlayer.message(player, C.cRed + C.Bold + "Block with your Sword to Draw!");
      }
    }
  }
  
  @EventHandler
  public void Paint(UpdateEvent event)
  {
    if (!IsLive()) {
      return;
    }
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : this._drawers.GetPlayers(true))
    {
      if (!player.isBlocking())
      {
        this._brushPrevious = null;
      }
      else
      {
        Block block = player.getTargetBlock(null, 200);
        if ((block != null) && (this._canvas.contains(block)))
        {

          if (block.getData() != this._brushColor)
          {


            block.setData(this._brushColor);
            

            if (UtilGear.isMat(player.getItemInHand(), Material.IRON_SWORD))
            {
              for (Block other : UtilBlock.getSurrounding(block, false))
              {
                if (this._canvas.contains(other))
                {

                  other.setData(this._brushColor);
                }
              }
            }
            Block other;
            if (this._brushPrevious != null)
            {
              while (UtilMath.offset(this._brushPrevious, block.getLocation().add(0.5D, 0.5D, 0.5D)) > 0.5D)
              {
                this._brushPrevious.add(UtilAlg.getTrajectory(this._brushPrevious, block.getLocation().add(0.5D, 0.5D, 0.5D)).multiply(0.5D));
                
                Block fixBlock = this._brushPrevious.getBlock();
                
                if (this._canvas.contains(fixBlock))
                {

                  fixBlock.setData(this._brushColor);
                  

                  if (UtilGear.isMat(player.getItemInHand(), Material.IRON_SWORD))
                  {
                    for (Iterator localIterator3 = UtilBlock.getSurrounding(fixBlock, false).iterator(); localIterator3.hasNext();) { other = (Block)localIterator3.next();
                      
                      if (this._canvas.contains(other))
                      {

                        other.setData(this._brushColor); }
                    }
                  }
                }
              }
            }
            for (Player other : UtilServer.getPlayers()) {
              other.playSound(other.getLocation(), Sound.FIZZ, 0.2F, 2.0F);
            }
            this._lockDrawer = false;
            
            this._brushPrevious = block.getLocation().add(0.5D, 0.5D, 0.5D);
          } }
      }
    }
  }
  
  @EventHandler
  public void PaintReset(PlayerInteractEvent event) {
    if (!IsLive()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!UtilGear.isMat(player.getItemInHand(), Material.TNT)) {
      return;
    }
    if (!this._drawers.HasPlayer(player)) {
      return;
    }
    if (!Recharge.Instance.use(player, "Clear Canvas", 5000L, true, false)) {
      return;
    }
    byte color = this._brushColor;
    
    Reset();
    

    this._brushColor = color;
    this._lockDrawer = false;
    
    for (Player other : UtilServer.getPlayers()) {
      other.playSound(other.getLocation(), Sound.EXPLODE, 0.5F, 1.5F);
    }
  }
  
  @EventHandler
  public void PaintBucket(PlayerInteractEvent event) {
    if (!IsLive()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!UtilGear.isMat(player.getItemInHand(), Material.BUCKET)) {
      return;
    }
    if (!this._drawers.HasPlayer(player)) {
      return;
    }
    Block block = player.getTargetBlock(null, 200);
    if ((block == null) || (!this._canvas.contains(block))) {
      return;
    }
    
    byte color = block.getData();
    
    if (color == this._brushColor) {
      return;
    }
    FillRecurse(block, color);
    
    for (Player other : UtilServer.getPlayers()) {
      other.playSound(other.getLocation(), Sound.SPLASH, 0.4F, 1.5F);
    }
  }
  
  public void FillRecurse(Block block, byte color) {
    if (block.getData() != color) {
      return;
    }
    if (!this._canvas.contains(block)) {
      return;
    }
    block.setData(this._brushColor);
    
    for (Block other : UtilBlock.getSurrounding(block, false))
    {
      FillRecurse(other, color);
    }
  }
  
  @EventHandler
  public void ColorSelect(PlayerInteractEvent event)
  {
    if (!IsLive()) {
      return;
    }
    Player player = event.getPlayer();
    
    if (!this._drawers.HasPlayer(player)) {
      return;
    }
    Block block = player.getTargetBlock(null, 200);
    if ((block == null) || (block.getType() != Material.WOOL) || (this._canvas.contains(block))) {
      return;
    }
    this._brushColor = block.getData();
    
    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 2.0F, 1.0F);
  }
  
  private void Reset()
  {
    for (Block block : this._canvas)
    {
      if ((block.getTypeId() != 35) || (block.getData() != 0)) {
        block.setTypeIdAndData(35, (byte)0, false);
      }
    }
    this._brushColor = 15;
    
    if (this._textBlocks != null)
    {
      for (Block block : this._textBlocks) {
        block.setType(Material.AIR);
      }
      this._textBlocks.clear();
      this._textBlocks = null;
    }
    
    this._lockDrawer = true;
  }
  
  public void AddScore(Player player, double amount)
  {
    for (GameScore score : this._ranks)
    {
      if (score.Player.equals(player))
      {
        score.Score += amount;
        EndCheck();
        return;
      }
    }
    
    this._ranks.add(new GameScore(player, amount));
  }
  
  private void SortScores()
  {
    for (int i = 0; i < this._ranks.size(); i++)
    {
      for (int j = this._ranks.size() - 1; j > 0; j--)
      {
        if (((GameScore)this._ranks.get(j)).Score > ((GameScore)this._ranks.get(j - 1)).Score)
        {
          GameScore temp = (GameScore)this._ranks.get(j);
          this._ranks.set(j, (GameScore)this._ranks.get(j - 1));
          this._ranks.set(j - 1, temp);
        }
      }
    }
  }
  

  public void EndCheck()
  {
    if (!IsLive()) {
      return;
    }
    if (((this._roundCount == this._roundMax) && (UtilTime.elapsed(this._roundTime, 5000L))) || (GetPlayers(true).size() <= 1))
    {
      SortScores();
      

      this._places.clear();
      for (int i = 0; i < this._ranks.size(); i++) {
        this._places.add(i, ((GameScore)this._ranks.get(i)).Player);
      }
      
      if (this._ranks.size() >= 1) {
        AddGems(((GameScore)this._ranks.get(0)).Player, 20.0D, "1st Place", false);
      }
      if (this._ranks.size() >= 2) {
        AddGems(((GameScore)this._ranks.get(1)).Player, 15.0D, "2nd Place", false);
      }
      if (this._ranks.size() >= 3) {
        AddGems(((GameScore)this._ranks.get(2)).Player, 10.0D, "3rd Place", false);
      }
      
      for (Player player : GetPlayers(false)) {
        if (player.isOnline())
          AddGems(player, 10.0D, "Participation", false);
      }
      SetState(Game.GameState.End);
      AnnounceEnd(this._places);
    }
  }
  

  @EventHandler
  public void ScoreboardUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    
    for (String string : this._lastScoreboard)
      GetScoreboard().resetScores(string);
    this._lastScoreboard.clear();
    
    SortScores();
    
    int index = 15;
    

    String out = C.cRed + " ";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cRed + " ";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cWhite + "Round:";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cYellow + Math.min(this._roundCount + 1, this._roundMax) + " of " + this._roundMax;
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    

    out = C.cRed + "  ";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cWhite + "Drawer:";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cYellow + "None";
    if (this._round != null)
      out = C.cYellow + this._round.Drawer.getName();
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    
    out = C.cRed + "   ";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    

    out = C.cWhite + "Scores:";
    if (out.length() >= 16)
      out = out.substring(0, 15);
    this._lastScoreboard.add(out);
    GetObjectiveSide().getScore(out).setScore(index--);
    

    for (int i = 0; (i < this._ranks.size()) && (index > 0); i++)
    {
      GameScore score = (GameScore)this._ranks.get(i);
      
      out = (int)score.Score + " " + C.cYellow + score.Player.getName();
      
      if (out.length() >= 16) {
        out = out.substring(0, 15);
      }
      this._lastScoreboard.add(out);
      
      GetObjectiveSide().getScore(out).setScore(index--);
    }
  }
}
