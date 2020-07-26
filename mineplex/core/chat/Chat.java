package mineplex.core.chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.UUID;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import mineplex.core.MiniClientPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.account.event.RetrieveClientInformationEvent;
import mineplex.core.chat.command.BroadcastCommand;
import mineplex.core.chat.command.FilterChatCommand;
import mineplex.core.chat.command.SilenceCommand;
import mineplex.core.chat.repository.ChatRepository;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.recharge.Recharge;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class Chat extends MiniClientPlugin<ChatClient>
{
  private CoreClientManager _clientManager;
  private ChatRepository _repository;
  private String _filterUrl = "https://mp9wbhy6.pottymouthfilter.com/v1/";
  private String _apiKey = "38SMwIOeymi8V3r2MVtJ";
  private String _authName = "";
  
  private String _serverName;
  private long _silenced = 0L;
  
  private NautHashMap<String, String> _playerLastMessage = new NautHashMap();
  
  public Chat(JavaPlugin plugin, CoreClientManager clientManager, String serverName)
  {
    super("Chat", plugin);
    
    this._clientManager = clientManager;
    this._serverName = serverName;
    setupConfigValues();
    
    this._repository = new ChatRepository(plugin.getConfig().getString("chat.connectionurl"));
    
    try
    {
      trustCert();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  private void setupConfigValues()
  {
    try
    {
      GetPlugin().getConfig().addDefault("chat.connectionurl", "jdbc:mysql://db.mineplex.com:3306/Account");
      GetPlugin().getConfig().set("chat.connectionurl", GetPlugin().getConfig().getString("chat.connectionurl"));
      GetPlugin().saveConfig();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  

  public void AddCommands()
  {
    AddCommand(new SilenceCommand(this));
    AddCommand(new BroadcastCommand(this));
    AddCommand(new FilterChatCommand(this));
  }
  






  public void Silence(long duration, boolean inform)
  {
    if (duration > 0L) {
      this._silenced = (System.currentTimeMillis() + duration);
    } else {
      this._silenced = duration;
    }
    if (!inform) {
      return;
    }
    
    if (duration == -1L) {
      UtilServer.broadcast(F.main("Chat", "Chat has been silenced for " + F.time("Permanent") + "."));
    } else if (duration == 0L) {
      UtilServer.broadcast(F.main("Chat", "Chat is no longer silenced."));
    } else {
      UtilServer.broadcast(F.main("Chat", "Chat has been silenced for " + F.time(UtilTime.MakeStr(duration, 1)) + 
        "."));
    }
  }
  
  @EventHandler
  public void preventMe(PlayerCommandPreprocessEvent event) {
    if ((event.getMessage().toLowerCase().startsWith("/me")) || 
      (event.getMessage().toLowerCase().startsWith("/bukkit")))
    {
      event.getPlayer().sendMessage(F.main(GetName(), "No, you!"));
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void lagTest(PlayerCommandPreprocessEvent event)
  {
    if ((event.getMessage().equals("lag")) || (event.getMessage().equals("ping")))
    {
      event.getPlayer().sendMessage(F.main(GetName(), "PONG!"));
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void SilenceUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    SilenceEnd();
  }
  
  public void SilenceEnd()
  {
    if (this._silenced <= 0L) {
      return;
    }
    if (System.currentTimeMillis() > this._silenced) {
      Silence(0L, true);
    }
  }
  
  public boolean SilenceCheck(Player player) {
    SilenceEnd();
    
    if (this._silenced == 0L) {
      return false;
    }
    if (this._clientManager.Get(player).GetRank().Has(player, Rank.MODERATOR, false)) {
      return false;
    }
    if (this._silenced == -1L) {
      UtilPlayer.message(player, F.main(GetName(), "Chat is silenced permanently."));
    } else {
      UtilPlayer.message(
        player, 
        F.main(GetName(), 
        "Chat is silenced for " + 
        F.time(UtilTime.MakeStr(this._silenced - System.currentTimeMillis(), 1)) + "."));
    }
    return true;
  }
  
  @EventHandler
  public void loadClientPrefence(RetrieveClientInformationEvent event)
  {
    ChatClient chatClient = new ChatClient();
    chatClient.SetFilterChat(this._repository.loadClientInformation(event.getUniqueId()));
    
    Set(event.getPlayerName(), chatClient);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void filterChat(AsyncPlayerChatEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (event.isAsynchronous())
    {
      Player player = event.getPlayer();
      String plyrname = player.toString();
      String msg = event.getMessage();
      String filtertype = "chat";
      String dname = player.getPlayerListName();
      
      JSONObject message = buildJsonChatObject("chat", dname, plyrname, msg, this._serverName, 1);
      String response = getResponseFromTwoHat(message, "chat");
      
      if (response == null)
      {
        System.out.println("[ERROR] Unable to filter chat message...thanks a lot TwoHat.");
        return;
      }
      
      int risk = Integer.parseInt(((JSONObject)JSONValue.parse(response)).get("risk").toString());
      
      if (risk >= 5)
      {
        String filteredMessage = event.getMessage();
        String newmessage;
        if (parseHashes(response) == null) {
          event.setMessage(ChatColor.RED + msg);
        }
        else {
          JSONArray hashindex = parseHashes(response);
          newmessage = hasher(hashindex, msg);
          String badmessage = newmessage;
          
          if (newmessage.contains("*"))
          {
            filteredMessage = badmessage;
          }
        }
        
        for (Player onlinePlayer : event.getRecipients())
        {
          if (((ChatClient)Get(onlinePlayer)).GetFilterChat())
          {
            onlinePlayer.sendMessage(String.format(event.getFormat(), new Object[] { event.getPlayer().getDisplayName(), filteredMessage }));
            
            if (onlinePlayer == event.getPlayer())
            {
              onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.NOTE_PIANO, 2.0F, 2.0F);
              onlinePlayer.sendMessage(F.main("Chat", "You have chat filtering turned on, type /filter to turn it off."));
            }
          }
          else
          {
            onlinePlayer.sendMessage(String.format(event.getFormat(), new Object[] { event.getPlayer().getDisplayName(), event.getMessage() }));
          }
        }
        
        event.setCancelled(true);
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void HandleChat(AsyncPlayerChatEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player sender = event.getPlayer();
    
    if (SilenceCheck(sender))
    {
      event.setCancelled(true);
      return;
    }
    if (!Recharge.Instance.use(sender, "Chat Message", 500L, false, false))
    {
      UtilPlayer.message(sender, F.main("Chat", "You are sending messages too fast."));
      event.setCancelled(true);
    }
    else if ((this._playerLastMessage.containsKey(sender.getName())) && 
      (((String)this._playerLastMessage.get(sender.getName())).equalsIgnoreCase(event.getMessage())))
    {
      UtilPlayer.message(sender, F.main("Chat", "You can't repeat the same message."));
      event.setCancelled(true);
    }
    else
    {
      this._playerLastMessage.put(sender.getName(), event.getMessage());
    }
  }
  
  public String hasher(JSONArray hasharray, String message)
  {
    StringBuilder newmsg = new StringBuilder(message);
    
    for (int i = 0; i < hasharray.size(); i++)
    {
      Long charindex = (Long)hasharray.get(i);
      int charidx = charindex.intValue();
      newmsg.setCharAt(charidx, '*');
    }
    
    return newmsg.toString();
  }
  
  public JSONArray parseHashes(String response)
  {
    JSONObject checkhash = (JSONObject)JSONValue.parse(response);
    
    JSONArray hasharray = (JSONArray)checkhash.get("hashes");
    
    return hasharray;
  }
  
  private JSONObject buildJsonChatObject(String filtertype, String name, String player, String msg, String server, int rule)
  {
    JSONObject message = new JSONObject();
    String str; switch ((str = filtertype).hashCode()) {case -265713450:  if (str.equals("username")) break;  case 3052376:  if ((goto 201) && (str.equals("chat")))
      {

        message.put("player_display_name", name);
        message.put("player", player);
        message.put("text", msg);
        message.put("server", "gamma");
        message.put("room", server);
        message.put("language", "en");
        message.put("rule", Integer.valueOf(rule));
        
        break label201;
        message.put("player_id", name);
        message.put("username", name);
        message.put("language", "en");
        message.put("rule", Integer.valueOf(rule)); }
      break; }
    label201:
    return message;
  }
  
  private String getResponseFromTwoHat(JSONObject message, String filtertype)
  {
    String authString = this._authName + ":" + this._apiKey;
    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
    String authStringEnc = new String(authEncBytes);
    String url = this._filterUrl + filtertype;
    
    StringBuffer response = null;
    
    HttpsURLConnection connection = null;
    DataOutputStream outputStream = null;
    BufferedReader bufferedReader = null;
    InputStreamReader inputStream = null;
    
    try
    {
      URL obj = new URL(url);
      
      connection = (HttpsURLConnection)obj.openConnection();
      

      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
      connection.setRequestProperty("Connection", "Keep-Alive");
      
      String urlParameters = message.toString();
      

      connection.setDoOutput(true);
      outputStream = new DataOutputStream(connection.getOutputStream());
      outputStream.writeBytes(urlParameters);
      outputStream.flush();
      outputStream.close();
      
      inputStream = new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8"));
      bufferedReader = new BufferedReader(inputStream);
      
      response = new StringBuffer();
      String inputLine;
      while ((inputLine = bufferedReader.readLine()) != null) {
        String inputLine;
        response.append(inputLine);
      }
      
      bufferedReader.close();
    }
    catch (Exception exception)
    {
      System.out.println("Error getting response from TwoHat : " + exception.getMessage());
      


      if (connection != null)
      {
        connection.disconnect();
      }
      
      if (outputStream != null)
      {
        try
        {
          outputStream.flush();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        
        try
        {
          outputStream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (bufferedReader != null)
      {
        try
        {
          bufferedReader.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (inputStream != null)
      {
        try
        {
          inputStream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (connection != null)
      {
        connection.disconnect();
      }
      
      if (outputStream != null)
      {
        try
        {
          outputStream.flush();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        
        try
        {
          outputStream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (bufferedReader != null)
      {
        try
        {
          bufferedReader.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (inputStream != null)
      {
        try
        {
          inputStream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    
    String pmresponse = null;
    
    if (response != null) {
      pmresponse = response.toString();
    }
    return pmresponse;
  }
  
  public static void trustCert() throws Exception
  {
    TrustManager[] trustAllCerts = { new X509TrustManager()
    {
      public X509Certificate[] getAcceptedIssuers()
      {
        return null;
      }
      



      public void checkClientTrusted(X509Certificate[] certs, String authType) {}
      



      public void checkServerTrusted(X509Certificate[] certs, String authType) {}
    } };
    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    

    HostnameVerifier allHostsValid = new HostnameVerifier()
    {
      public boolean verify(String hostname, SSLSession session)
      {
        return true;
      }
      

    };
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
  }
  
  public long Silenced()
  {
    return this._silenced;
  }
  
  @EventHandler
  public void playerQuit(PlayerQuitEvent event)
  {
    this._playerLastMessage.remove(event.getPlayer().getName());
  }
  

  protected ChatClient AddPlayer(String player)
  {
    return new ChatClient();
  }
  
  public void toggleFilterChat(Player caller)
  {
    this._repository.saveFilterChat(caller.getUniqueId().toString(), !((ChatClient)Get(caller)).GetFilterChat());
    ((ChatClient)Get(caller)).SetFilterChat(!((ChatClient)Get(caller)).GetFilterChat());
    
    if (((ChatClient)Get(caller)).GetFilterChat()) {
      caller.playSound(caller.getLocation(), Sound.NOTE_PIANO, 2.0F, 2.0F);
    } else {
      caller.playSound(caller.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
    }
    caller.sendMessage(F.main("Chat", "You have turned chat filtering " + ChatColor.YELLOW + (((ChatClient)Get(caller)).GetFilterChat() ? "ON" : "OFF") + ChatColor.GRAY + "."));
  }
}
