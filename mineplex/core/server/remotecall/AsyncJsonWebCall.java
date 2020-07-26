package mineplex.core.server.remotecall;

import mineplex.core.common.util.Callback;

public class AsyncJsonWebCall extends JsonWebCall
{
  public AsyncJsonWebCall(String url)
  {
    super(url);
  }
  
  public void Execute()
  {
    Thread asyncThread = new Thread(new Runnable()
    {
      public void run()
      {
        AsyncJsonWebCall.this.Execute();
      }
      
    });
    asyncThread.start();
  }
  
  public void Execute(final Object argument)
  {
    Thread asyncThread = new Thread(new Runnable()
    {
      public void run()
      {
        AsyncJsonWebCall.this.Execute(argument);
      }
      
    });
    asyncThread.start();
  }
  
  public <T> void Execute(final Class<T> callbackClass, final Callback<T> callback)
  {
    Thread asyncThread = new Thread(new Runnable()
    {
      public void run()
      {
        AsyncJsonWebCall.this.Execute(callbackClass, callback);
      }
      
    });
    asyncThread.start();
  }
  
  public <T> void Execute(final Class<T> callbackClass, final Callback<T> callback, final Object argument)
  {
    Thread asyncThread = new Thread(new Runnable()
    {
      public void run()
      {
        AsyncJsonWebCall.this.Execute(callbackClass, callback, argument);
      }
      
    });
    asyncThread.start();
  }
}
