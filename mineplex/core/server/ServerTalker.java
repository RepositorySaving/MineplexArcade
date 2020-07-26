package mineplex.core.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import mineplex.core.server.packet.Packet;

public class ServerTalker extends Thread
{
  private static Object _queueLock = new Object();
  
  private List<Packet> _queue = new ArrayList();
  
  private String _serverAddress;
  private boolean _running = true;
  
  private boolean _debug = false;
  
  public ServerTalker(String serverAddress)
  {
    this._serverAddress = serverAddress;
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
      if (!HasPackets())
      {
        try
        {
          Thread.sleep(25L);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
        
      }
      else
      {
        Packet packet = null;
        
        synchronized (_queueLock)
        {
          packet = (Packet)this._queue.remove(0);
        }
        
        Socket socket = null;
        DataOutputStream dataOutput = null;
        
        try
        {
          socket = new Socket(this._serverAddress.split(":")[0], Integer.parseInt(this._serverAddress.split(":")[1]));
          dataOutput = new DataOutputStream(socket.getOutputStream());
          
          packet.Write(dataOutput);
          dataOutput.flush();
          
          if (this._debug) {
            System.out.println("Sent packet to : " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
          }
        }
        catch (Exception ex) {
          System.out.println("ServerTalker.run Exception(" + this._serverAddress + ") : " + ex.getMessage());
          this._queue.add(packet);
          
          try
          {
            Thread.sleep(15000L);
          }
          catch (InterruptedException e)
          {
            e.printStackTrace();
          }
          


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
  
  public boolean HasPackets() {
    synchronized (_queueLock)
    {
      return this._queue.size() != 0;
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
}
