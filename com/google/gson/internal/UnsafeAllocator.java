package com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;























public abstract class UnsafeAllocator
{
  public abstract <T> T newInstance(Class<T> paramClass)
    throws Exception;
  
  public static UnsafeAllocator create()
  {
    try
    {
      Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
      Field f = unsafeClass.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      final Object unsafe = f.get(null);
      Method allocateInstance = unsafeClass.getMethod("allocateInstance", new Class[] { Class.class });
      new UnsafeAllocator()
      {
        public <T> T newInstance(Class<T> c) throws Exception
        {
          return this.val$allocateInstance.invoke(unsafe, new Object[] { c });
        }
        

      };

    }
    catch (Exception ignored)
    {

      try
      {
        Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Class.class });
        
        newInstance.setAccessible(true);
        new UnsafeAllocator()
        {
          public <T> T newInstance(Class<T> c) throws Exception
          {
            return this.val$newInstance.invoke(null, new Object[] { c, Object.class });
          }
          

        };

      }
      catch (Exception ignored)
      {

        try
        {
          Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[] { Class.class });
          
          getConstructorId.setAccessible(true);
          final int constructorId = ((Integer)getConstructorId.invoke(null, new Object[] { Object.class })).intValue();
          Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Integer.TYPE });
          
          newInstance.setAccessible(true);
          new UnsafeAllocator()
          {
            public <T> T newInstance(Class<T> c) throws Exception
            {
              return this.val$newInstance.invoke(null, new Object[] { c, Integer.valueOf(constructorId) });
            }
          };
        }
        catch (Exception ignored) {}
      }
    }
    new UnsafeAllocator()
    {
      public <T> T newInstance(Class<T> c) {
        throw new UnsupportedOperationException("Cannot allocate " + c);
      }
    };
  }
}
