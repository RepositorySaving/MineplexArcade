package org.apache.http.impl.client.cache;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;
import java.util.Locale;
import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;































@ThreadSafe
class BasicIdGenerator
{
  private final String hostname;
  private final SecureRandom rnd;
  @GuardedBy("this")
  private long count;
  
  public BasicIdGenerator()
  {
    String hostname;
    try
    {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException ex) {
      hostname = "localhost";
    }
    this.hostname = hostname;
    try {
      this.rnd = SecureRandom.getInstance("SHA1PRNG");
    } catch (NoSuchAlgorithmException ex) {
      throw new Error(ex);
    }
    this.rnd.setSeed(System.currentTimeMillis());
  }
  
  public synchronized void generate(StringBuilder buffer) {
    this.count += 1L;
    int rndnum = this.rnd.nextInt();
    buffer.append(System.currentTimeMillis());
    buffer.append('.');
    Formatter formatter = new Formatter(buffer, Locale.US);
    formatter.format("%1$016x-%2$08x", new Object[] { Long.valueOf(this.count), Integer.valueOf(rndnum) });
    buffer.append('.');
    buffer.append(this.hostname);
  }
  
  public String generate() {
    StringBuilder buffer = new StringBuilder();
    generate(buffer);
    return buffer.toString();
  }
}
