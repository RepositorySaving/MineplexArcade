package org.apache.http.impl.conn;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.http.conn.DnsResolver;

































public class SystemDefaultDnsResolver
  implements DnsResolver
{
  public InetAddress[] resolve(String host)
    throws UnknownHostException
  {
    return InetAddress.getAllByName(host);
  }
}
