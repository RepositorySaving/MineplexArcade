package mineplex.core.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import mineplex.core.server.packet.Packet;
import mineplex.core.server.remotecall.JsonWebCall;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;

public class ServerBroadcaster extends Thread
{
  private static Object _queueLock = new Object();
  private static Object _serverMapLock = new Object();
  
  private HashSet<String> _serverMap = new HashSet();
  private List<Packet> _queue = new ArrayList();
  
  private String _webAddress;
  private boolean _running = true;
  private boolean _retrievingServers = false;
  
  private long _updateInterval = 15000L;
  
  private long _lastUpdate;
  private boolean _debug = false;
  
  public ServerBroadcaster(String webAddress)
  {
    this._webAddress = webAddress;
  }
  
  public void QueuePacket(Packet packet)
  {
    synchronized (_queueLock)
    {
      this._queue.add(packet);
    }
  }
  

  public void run()
  {
    while (this._running)
    {
      if ((!HasPackets()) || (!HasServers()))
      {
        try
        {
          Thread.sleep(25L);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
        
        if (System.currentTimeMillis() - this._lastUpdate > this._updateInterval)
        {
          RetrieveActiveServers();
        }
        
      }
      else
      {
        Packet packet = null;
        
        synchronized (_queueLock)
        {
          packet = (Packet)this._queue.remove(0);
        }
        
        synchronized (_serverMapLock)
        {
          for (String server : this._serverMap)
          {
            Socket socket = null;
            DataOutputStream dataOutput = null;
            
            try
            {
              socket = new Socket(server.split(":")[0], Integer.parseInt(server.split(":")[1]));
              dataOutput = new DataOutputStream(socket.getOutputStream());
              
              packet.Write(dataOutput);
              dataOutput.flush();
              
              if (this._debug) {
                System.out.println("Sent packet to : " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
              }
            }
            catch (Exception ex) {
              System.out.println("ServerTalker.run Exception(" + server + ") : " + ex.getMessage());
              


              try
              {
                if (dataOutput != null) {
                  dataOutput.close();
                }
              }
              catch (IOException e) {
                e.printStackTrace();
              }
              
              try
              {
                if (socket != null) {
                  socket.close();
                }
              }
              catch (IOException e) {
                e.printStackTrace();
              }
            }
            finally
            {
              try
              {
                if (dataOutput != null) {
                  dataOutput.close();
                }
              }
              catch (IOException e) {
                e.printStackTrace();
              }
              
              try
              {
                if (socket != null) {
                  socket.close();
                }
              }
              catch (IOException e) {
                e.printStackTrace();
              }
            }
          }
        }
      }
    }
  }
  
  public boolean HasPackets() {
    synchronized (_queueLock)
    {
      return this._queue.size() != 0;
    }
  }
  
  public boolean HasServers()
  {
    synchronized (_serverMapLock)
    {
      return this._serverMap.size() != 0;
    }
  }
  
  public void PrintPackets()
  {
    System.out.println("Listing Packets:");
    
    synchronized (_queueLock)
    {
      if (this._queue.isEmpty())
      {
        System.out.println("Packet queue empty!");
      }
      else
      {
        for (Packet packet : this._queue)
        {
          System.out.println(packet.getClass());
        }
      }
    }
  }
  
  public void PrintServers()
  {
    System.out.println("Listing Servers:");
    
    if (this._retrievingServers)
    {
      System.out.println("Retrieving servers.  Please check again in a few seconds.");
    }
    
    synchronized (_serverMapLock)
    {
      if (this._serverMap.isEmpty())
      {
        System.out.println("Server list empty!");
      }
      else
      {
        for (String server : this._serverMap)
        {
          System.out.println(server);
        }
      }
    }
  }
  
  private void RetrieveActiveServers()
  {
    if (this._debug) {
      System.out.println("Updating servers...");
    }
    List<String> servers = (List)new JsonWebCall(this._webAddress + "Servers/GetServers").Execute(new TypeToken() {}.getType(), null);
    
    synchronized (_serverMapLock)
    {
      this._serverMap.clear();
      
      if (servers.size() > 0)
      {
        for (String server : servers)
        {
          this._serverMap.add(server);
        }
        
      }
      else {
        System.out.println("No servers registered at '" + this._webAddress + "'!");
      }
    }
    
    this._lastUpdate = System.currentTimeMillis();
  }
}
