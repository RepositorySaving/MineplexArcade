package org.apache.commons.logging.impl;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.LogFactory;










































public class ServletContextCleaner
  implements ServletContextListener
{
  private Class[] RELEASE_SIGNATURE = { ClassLoader.class };
  




  public void contextDestroyed(ServletContextEvent sce)
  {
    ClassLoader tccl = Thread.currentThread().getContextClassLoader();
    
    Object[] params = new Object[1];
    params[0] = tccl;
    






























    ClassLoader loader = tccl;
    while (loader != null)
    {
      try
      {

        Class logFactoryClass = loader.loadClass("org.apache.commons.logging.LogFactory");
        Method releaseMethod = logFactoryClass.getMethod("release", this.RELEASE_SIGNATURE);
        releaseMethod.invoke(null, params);
        loader = logFactoryClass.getClassLoader().getParent();
      }
      catch (ClassNotFoundException ex)
      {
        loader = null;
      }
      catch (NoSuchMethodException ex) {
        System.err.println("LogFactory instance found which does not support release method!");
        loader = null;
      }
      catch (IllegalAccessException ex) {
        System.err.println("LogFactory instance found which is not accessable!");
        loader = null;
      }
      catch (InvocationTargetException ex) {
        System.err.println("LogFactory instance release method failed!");
        loader = null;
      }
    }
    



    LogFactory.release(tccl);
  }
  
  public void contextInitialized(ServletContextEvent sce) {}
}
