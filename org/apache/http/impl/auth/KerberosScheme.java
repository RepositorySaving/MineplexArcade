package org.apache.http.impl.auth;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.protocol.HttpContext;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;





























public class KerberosScheme
  extends GGSSchemeBase
{
  private static final String KERBEROS_OID = "1.2.840.113554.1.2.2";
  
  public KerberosScheme(boolean stripPort)
  {
    super(stripPort);
  }
  
  public KerberosScheme() {
    super(false);
  }
  
  public String getSchemeName() {
    return "Kerberos";
  }
  














  public Header authenticate(Credentials credentials, HttpRequest request, HttpContext context)
    throws AuthenticationException
  {
    return super.authenticate(credentials, request, context);
  }
  
  protected byte[] generateToken(byte[] input, String authServer) throws GSSException
  {
    return generateGSSToken(input, new Oid("1.2.840.113554.1.2.2"), authServer);
  }
  





  public String getParameter(String name)
  {
    if (name == null) {
      throw new IllegalArgumentException("Parameter name may not be null");
    }
    return null;
  }
  





  public String getRealm()
  {
    return null;
  }
  




  public boolean isConnectionBased()
  {
    return true;
  }
}
