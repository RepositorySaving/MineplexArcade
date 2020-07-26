package mineplex.core.common.util;

import java.io.PrintStream;

public class UtilSystem {
  public static void PrintStackTrace() {
    for (StackTraceElement trace : Thread.currentThread().getStackTrace())
    {
      System.out.println(trace.toString());
    }
  }
}
