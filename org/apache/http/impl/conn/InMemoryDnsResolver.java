package org.apache.http.impl.conn;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.DnsResolver;

































public class InMemoryDnsResolver
  implements DnsResolver
{
  private final Log log = LogFactory.getLog(InMemoryDnsResolver.class);
  



  private Map<String, InetAddress[]> dnsMap;
  




  public InMemoryDnsResolver()
  {
    this.dnsMap = new ConcurrentHashMap();
  }
  









  public void add(String host, InetAddress... ips)
  {
    if (host == null) {
      throw new IllegalArgumentException("Host name may not be null");
    }
    if (ips == null) {
      throw new IllegalArgumentException("Array of IP addresses may not be null");
    }
    this.dnsMap.put(host, ips);
  }
  

  public InetAddress[] resolve(String host)
    throws UnknownHostException
  {
    InetAddress[] resolvedAddresses = (InetAddress[])this.dnsMap.get(host);
    if (this.log.isInfoEnabled()) {
      this.log.info("Resolving " + host + " to " + Arrays.deepToString(resolvedAddresses));
    }
    if (resolvedAddresses == null) {
      throw new UnknownHostException(host + " cannot be resolved");
    }
    return resolvedAddresses;
  }
}
