package org.apache.http.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



































public class BasicFuture<T>
  implements Future<T>, Cancellable
{
  private final FutureCallback<T> callback;
  private volatile boolean completed;
  private volatile boolean cancelled;
  private volatile T result;
  private volatile Exception ex;
  
  public BasicFuture(FutureCallback<T> callback)
  {
    this.callback = callback;
  }
  
  public boolean isCancelled() {
    return this.cancelled;
  }
  
  public boolean isDone() {
    return this.completed;
  }
  
  private T getResult() throws ExecutionException {
    if (this.ex != null) {
      throw new ExecutionException(this.ex);
    }
    return this.result;
  }
  
  public synchronized T get() throws InterruptedException, ExecutionException {
    while (!this.completed) {
      wait();
    }
    return getResult();
  }
  
  public synchronized T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
  {
    long msecs = unit.toMillis(timeout);
    long startTime = msecs <= 0L ? 0L : System.currentTimeMillis();
    long waitTime = msecs;
    if (this.completed)
      return getResult();
    if (waitTime <= 0L) {
      throw new TimeoutException();
    }
    do {
      wait(waitTime);
      if (this.completed) {
        return getResult();
      }
      waitTime = msecs - (System.currentTimeMillis() - startTime);
    } while (waitTime > 0L);
    throw new TimeoutException();
  }
  



  public synchronized boolean completed(T result)
  {
    if (this.completed) {
      return false;
    }
    this.completed = true;
    this.result = result;
    if (this.callback != null) {
      this.callback.completed(result);
    }
    notifyAll();
    return true;
  }
  
  public synchronized boolean failed(Exception exception) {
    if (this.completed) {
      return false;
    }
    this.completed = true;
    this.ex = exception;
    if (this.callback != null) {
      this.callback.failed(exception);
    }
    notifyAll();
    return true;
  }
  
  public synchronized boolean cancel(boolean mayInterruptIfRunning) {
    if (this.completed) {
      return false;
    }
    this.completed = true;
    this.cancelled = true;
    if (this.callback != null) {
      this.callback.cancelled();
    }
    notifyAll();
    return true;
  }
  
  public boolean cancel() {
    return cancel(true);
  }
}
