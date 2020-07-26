package org.apache.http.conn.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpInetSocketAddress;
import org.apache.http.conn.scheme.HostNameResolver;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


















































































































@ThreadSafe
public class SSLSocketFactory
  implements SchemeLayeredSocketFactory, LayeredSchemeSocketFactory, LayeredSocketFactory
{
  public static final String TLS = "TLS";
  public static final String SSL = "SSL";
  public static final String SSLV2 = "SSLv2";
  public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();
  

  public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = new BrowserCompatHostnameVerifier();
  

  public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = new StrictHostnameVerifier();
  

  private static final char[] EMPTY_PASSWORD = "".toCharArray();
  
  private final javax.net.ssl.SSLSocketFactory socketfactory;
  private final HostNameResolver nameResolver;
  private volatile X509HostnameVerifier hostnameVerifier;
  
  public static SSLSocketFactory getSocketFactory()
    throws SSLInitializationException
  {
    return new SSLSocketFactory(createDefaultSSLContext());
  }
  























  public static SSLSocketFactory getSystemSocketFactory()
    throws SSLInitializationException
  {
    return new SSLSocketFactory(createSystemSSLContext());
  }
  










  private static SSLContext createSSLContext(String algorithm, KeyStore keystore, String keystorePassword, KeyStore truststore, SecureRandom random, TrustStrategy trustStrategy)
    throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException, KeyManagementException
  {
    if (algorithm == null) {
      algorithm = "TLS";
    }
    KeyManagerFactory kmfactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    
    kmfactory.init(keystore, keystorePassword != null ? keystorePassword.toCharArray() : null);
    KeyManager[] keymanagers = kmfactory.getKeyManagers();
    TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    
    tmfactory.init(truststore);
    TrustManager[] trustmanagers = tmfactory.getTrustManagers();
    if ((trustmanagers != null) && (trustStrategy != null)) {
      for (int i = 0; i < trustmanagers.length; i++) {
        TrustManager tm = trustmanagers[i];
        if ((tm instanceof X509TrustManager)) {
          trustmanagers[i] = new TrustManagerDecorator((X509TrustManager)tm, trustStrategy);
        }
      }
    }
    

    SSLContext sslcontext = SSLContext.getInstance(algorithm);
    sslcontext.init(keymanagers, trustmanagers, random);
    return sslcontext;
  }
  

  private static SSLContext createSystemSSLContext(String algorithm, SecureRandom random)
    throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException
  {
    if (algorithm == null) {
      algorithm = "TLS";
    }
    TrustManagerFactory tmfactory = null;
    
    String trustAlgorithm = System.getProperty("ssl.TrustManagerFactory.algorithm");
    if (trustAlgorithm == null) {
      trustAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
    }
    String trustStoreType = System.getProperty("javax.net.ssl.trustStoreType");
    if (trustStoreType == null) {
      trustStoreType = KeyStore.getDefaultType();
    }
    if ("none".equalsIgnoreCase(trustStoreType)) {
      tmfactory = TrustManagerFactory.getInstance(trustAlgorithm);
    } else {
      File trustStoreFile = null;
      String s = System.getProperty("javax.net.ssl.trustStore");
      if (s != null) {
        trustStoreFile = new File(s);
        tmfactory = TrustManagerFactory.getInstance(trustAlgorithm);
        String trustStoreProvider = System.getProperty("javax.net.ssl.trustStoreProvider");
        KeyStore trustStore;
        KeyStore trustStore; if (trustStoreProvider != null) {
          trustStore = KeyStore.getInstance(trustStoreType, trustStoreProvider);
        } else {
          trustStore = KeyStore.getInstance(trustStoreType);
        }
        String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        FileInputStream instream = new FileInputStream(trustStoreFile);
        try {
          trustStore.load(instream, trustStorePassword != null ? trustStorePassword.toCharArray() : EMPTY_PASSWORD);
        }
        finally {
          instream.close();
        }
        tmfactory.init(trustStore);
      } else {
        File javaHome = new File(System.getProperty("java.home"));
        File file = new File(javaHome, "lib/security/jssecacerts");
        if (!file.exists()) {
          file = new File(javaHome, "lib/security/cacerts");
          trustStoreFile = file;
        } else {
          trustStoreFile = file;
        }
        tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        FileInputStream instream = new FileInputStream(trustStoreFile);
        try {
          trustStore.load(instream, trustStorePassword != null ? trustStorePassword.toCharArray() : null);
        } finally {
          instream.close();
        }
        tmfactory.init(trustStore);
      }
    }
    
    KeyManagerFactory kmfactory = null;
    String keyAlgorithm = System.getProperty("ssl.KeyManagerFactory.algorithm");
    if (keyAlgorithm == null) {
      keyAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
    }
    String keyStoreType = System.getProperty("javax.net.ssl.keyStoreType");
    if (keyStoreType == null) {
      keyStoreType = KeyStore.getDefaultType();
    }
    if ("none".equalsIgnoreCase(keyStoreType)) {
      kmfactory = KeyManagerFactory.getInstance(keyAlgorithm);
    } else {
      File keyStoreFile = null;
      String s = System.getProperty("javax.net.ssl.keyStore");
      if (s != null) {
        keyStoreFile = new File(s);
      }
      if (keyStoreFile != null) {
        kmfactory = KeyManagerFactory.getInstance(keyAlgorithm);
        String keyStoreProvider = System.getProperty("javax.net.ssl.keyStoreProvider");
        KeyStore keyStore;
        KeyStore keyStore; if (keyStoreProvider != null) {
          keyStore = KeyStore.getInstance(keyStoreType, keyStoreProvider);
        } else {
          keyStore = KeyStore.getInstance(keyStoreType);
        }
        String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        FileInputStream instream = new FileInputStream(keyStoreFile);
        try {
          keyStore.load(instream, keyStorePassword != null ? keyStorePassword.toCharArray() : EMPTY_PASSWORD);
        }
        finally {
          instream.close();
        }
        kmfactory.init(keyStore, keyStorePassword != null ? keyStorePassword.toCharArray() : EMPTY_PASSWORD);
      }
    }
    

    SSLContext sslcontext = SSLContext.getInstance(algorithm);
    sslcontext.init(kmfactory != null ? kmfactory.getKeyManagers() : null, tmfactory != null ? tmfactory.getTrustManagers() : null, random);
    


    return sslcontext;
  }
  
  private static SSLContext createDefaultSSLContext() throws SSLInitializationException {
    try {
      return createSSLContext("TLS", null, null, null, null, null);
    } catch (Exception ex) {
      throw new SSLInitializationException("Failure initializing default SSL context", ex);
    }
  }
  
  private static SSLContext createSystemSSLContext() throws SSLInitializationException {
    try {
      return createSystemSSLContext("TLS", null);
    } catch (Exception ex) {
      throw new SSLInitializationException("Failure initializing default system SSL context", ex);
    }
  }
  








  @Deprecated
  public SSLSocketFactory(String algorithm, KeyStore keystore, String keystorePassword, KeyStore truststore, SecureRandom random, HostNameResolver nameResolver)
    throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
  {
    this(createSSLContext(algorithm, keystore, keystorePassword, truststore, random, null), nameResolver);
  }
  










  public SSLSocketFactory(String algorithm, KeyStore keystore, String keystorePassword, KeyStore truststore, SecureRandom random, X509HostnameVerifier hostnameVerifier)
    throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
  {
    this(createSSLContext(algorithm, keystore, keystorePassword, truststore, random, null), hostnameVerifier);
  }
  











  public SSLSocketFactory(String algorithm, KeyStore keystore, String keystorePassword, KeyStore truststore, SecureRandom random, TrustStrategy trustStrategy, X509HostnameVerifier hostnameVerifier)
    throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
  {
    this(createSSLContext(algorithm, keystore, keystorePassword, truststore, random, trustStrategy), hostnameVerifier);
  }
  




  public SSLSocketFactory(KeyStore keystore, String keystorePassword, KeyStore truststore)
    throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
  {
    this("TLS", keystore, keystorePassword, truststore, null, null, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
  }
  

  public SSLSocketFactory(KeyStore keystore, String keystorePassword)
    throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
  {
    this("TLS", keystore, keystorePassword, null, null, null, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
  }
  
  public SSLSocketFactory(KeyStore truststore)
    throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
  {
    this("TLS", null, null, truststore, null, null, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
  }
  




  public SSLSocketFactory(TrustStrategy trustStrategy, X509HostnameVerifier hostnameVerifier)
    throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
  {
    this("TLS", null, null, null, null, trustStrategy, hostnameVerifier);
  }
  



  public SSLSocketFactory(TrustStrategy trustStrategy)
    throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException
  {
    this("TLS", null, null, null, null, trustStrategy, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
  }
  
  public SSLSocketFactory(SSLContext sslContext) {
    this(sslContext, BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
  }
  




  @Deprecated
  public SSLSocketFactory(SSLContext sslContext, HostNameResolver nameResolver)
  {
    this.socketfactory = sslContext.getSocketFactory();
    this.hostnameVerifier = BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
    this.nameResolver = nameResolver;
  }
  




  public SSLSocketFactory(SSLContext sslContext, X509HostnameVerifier hostnameVerifier)
  {
    if (sslContext == null) {
      throw new IllegalArgumentException("SSL context may not be null");
    }
    this.socketfactory = sslContext.getSocketFactory();
    this.hostnameVerifier = hostnameVerifier;
    this.nameResolver = null;
  }
  




  public SSLSocketFactory(javax.net.ssl.SSLSocketFactory socketfactory, X509HostnameVerifier hostnameVerifier)
  {
    if (socketfactory == null) {
      throw new IllegalArgumentException("SSL socket factory may not be null");
    }
    this.socketfactory = socketfactory;
    this.hostnameVerifier = hostnameVerifier;
    this.nameResolver = null;
  }
  



  public Socket createSocket(HttpParams params)
    throws IOException
  {
    SSLSocket sock = (SSLSocket)this.socketfactory.createSocket();
    prepareSocket(sock);
    return sock;
  }
  
  @Deprecated
  public Socket createSocket() throws IOException {
    SSLSocket sock = (SSLSocket)this.socketfactory.createSocket();
    prepareSocket(sock);
    return sock;
  }
  





  public Socket connectSocket(Socket socket, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params)
    throws IOException, UnknownHostException, ConnectTimeoutException
  {
    if (remoteAddress == null) {
      throw new IllegalArgumentException("Remote address may not be null");
    }
    if (params == null) {
      throw new IllegalArgumentException("HTTP parameters may not be null");
    }
    Socket sock = socket != null ? socket : this.socketfactory.createSocket();
    if (localAddress != null) {
      sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
      sock.bind(localAddress);
    }
    
    int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
    int soTimeout = HttpConnectionParams.getSoTimeout(params);
    try
    {
      sock.setSoTimeout(soTimeout);
      sock.connect(remoteAddress, connTimeout);
    } catch (SocketTimeoutException ex) {
      throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
    }
    String hostname;
    String hostname;
    if ((remoteAddress instanceof HttpInetSocketAddress)) {
      hostname = ((HttpInetSocketAddress)remoteAddress).getHttpHost().getHostName();
    } else {
      hostname = remoteAddress.getHostName();
    }
    
    SSLSocket sslsock;
    SSLSocket sslsock;
    if ((sock instanceof SSLSocket)) {
      sslsock = (SSLSocket)sock;
    } else {
      int port = remoteAddress.getPort();
      sslsock = (SSLSocket)this.socketfactory.createSocket(sock, hostname, port, true);
      prepareSocket(sslsock);
    }
    if (this.hostnameVerifier != null) {
      try {
        this.hostnameVerifier.verify(hostname, sslsock);
      }
      catch (IOException iox) {
        try {
          sslsock.close(); } catch (Exception x) {}
        throw iox;
      }
    }
    return sslsock;
  }
  













  public boolean isSecure(Socket sock)
    throws IllegalArgumentException
  {
    if (sock == null) {
      throw new IllegalArgumentException("Socket may not be null");
    }
    
    if (!(sock instanceof SSLSocket)) {
      throw new IllegalArgumentException("Socket not created by this factory");
    }
    
    if (sock.isClosed()) {
      throw new IllegalArgumentException("Socket is closed");
    }
    return true;
  }
  





  public Socket createLayeredSocket(Socket socket, String host, int port, HttpParams params)
    throws IOException, UnknownHostException
  {
    SSLSocket sslSocket = (SSLSocket)this.socketfactory.createSocket(socket, host, port, true);
    



    prepareSocket(sslSocket);
    if (this.hostnameVerifier != null) {
      this.hostnameVerifier.verify(host, sslSocket);
    }
    
    return sslSocket;
  }
  


  /**
   * @deprecated
   */
  public Socket createLayeredSocket(Socket socket, String host, int port, boolean autoClose)
    throws IOException, UnknownHostException
  {
    SSLSocket sslSocket = (SSLSocket)this.socketfactory.createSocket(socket, host, port, autoClose);
    




    prepareSocket(sslSocket);
    if (this.hostnameVerifier != null) {
      this.hostnameVerifier.verify(host, sslSocket);
    }
    
    return sslSocket;
  }
  
  @Deprecated
  public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {
    if (hostnameVerifier == null) {
      throw new IllegalArgumentException("Hostname verifier may not be null");
    }
    this.hostnameVerifier = hostnameVerifier;
  }
  
  public X509HostnameVerifier getHostnameVerifier() {
    return this.hostnameVerifier;
  }
  





  @Deprecated
  public Socket connectSocket(Socket socket, String host, int port, InetAddress localAddress, int localPort, HttpParams params)
    throws IOException, UnknownHostException, ConnectTimeoutException
  {
    InetSocketAddress local = null;
    if ((localAddress != null) || (localPort > 0))
    {
      if (localPort < 0) {
        localPort = 0;
      }
      local = new InetSocketAddress(localAddress, localPort); }
    InetAddress remoteAddress;
    InetAddress remoteAddress;
    if (this.nameResolver != null) {
      remoteAddress = this.nameResolver.resolve(host);
    } else {
      remoteAddress = InetAddress.getByName(host);
    }
    InetSocketAddress remote = new HttpInetSocketAddress(new HttpHost(host, port), remoteAddress, port);
    return connectSocket(socket, remote, local, params);
  }
  




  @Deprecated
  public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
    throws IOException, UnknownHostException
  {
    return createLayeredSocket(socket, host, port, autoClose);
  }
  
  protected void prepareSocket(SSLSocket socket)
    throws IOException
  {}
}
