package org.apache.http.impl.auth;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.util.EncodingUtils;










































final class NTLMEngineImpl
  implements NTLMEngine
{
  protected static final int FLAG_UNICODE_ENCODING = 1;
  protected static final int FLAG_TARGET_DESIRED = 4;
  protected static final int FLAG_NEGOTIATE_SIGN = 16;
  protected static final int FLAG_NEGOTIATE_SEAL = 32;
  protected static final int FLAG_NEGOTIATE_NTLM = 512;
  protected static final int FLAG_NEGOTIATE_ALWAYS_SIGN = 32768;
  protected static final int FLAG_NEGOTIATE_NTLM2 = 524288;
  protected static final int FLAG_NEGOTIATE_128 = 536870912;
  protected static final int FLAG_NEGOTIATE_KEY_EXCH = 1073741824;
  private static final SecureRandom RND_GEN;
  static final String DEFAULT_CHARSET = "ASCII";
  private String credentialCharset;
  private static byte[] SIGNATURE;
  
  NTLMEngineImpl()
  {
    this.credentialCharset = "ASCII";
  }
  
  static
  {
    SecureRandom rnd = null;
    try {
      rnd = SecureRandom.getInstance("SHA1PRNG");
    }
    catch (Exception e) {}
    RND_GEN = rnd;
    











    byte[] bytesWithoutNull = EncodingUtils.getBytes("NTLMSSP", "ASCII");
    SIGNATURE = new byte[bytesWithoutNull.length + 1];
    System.arraycopy(bytesWithoutNull, 0, SIGNATURE, 0, bytesWithoutNull.length);
    SIGNATURE[bytesWithoutNull.length] = 0;
  }
  






  final String getResponseFor(String message, String username, String password, String host, String domain)
    throws NTLMEngineException
  {
    String response;
    




    String response;
    




    if ((message == null) || (message.trim().equals(""))) {
      response = getType1Message(host, domain);
    } else {
      Type2Message t2m = new Type2Message(message);
      response = getType3Message(username, password, host, domain, t2m.getChallenge(), t2m.getFlags(), t2m.getTarget(), t2m.getTargetInfo());
    }
    
    return response;
  }
  









  String getType1Message(String host, String domain)
    throws NTLMEngineException
  {
    return new Type1Message(domain, host).getResponse();
  }
  




















  String getType3Message(String user, String password, String host, String domain, byte[] nonce, int type2Flags, String target, byte[] targetInformation)
    throws NTLMEngineException
  {
    return new Type3Message(domain, host, user, password, nonce, type2Flags, target, targetInformation).getResponse();
  }
  



  String getCredentialCharset()
  {
    return this.credentialCharset;
  }
  



  void setCredentialCharset(String credentialCharset)
  {
    this.credentialCharset = credentialCharset;
  }
  
  private static String stripDotSuffix(String value)
  {
    int index = value.indexOf(".");
    if (index != -1)
      return value.substring(0, index);
    return value;
  }
  
  private static String convertHost(String host)
  {
    return stripDotSuffix(host);
  }
  
  private static String convertDomain(String domain)
  {
    return stripDotSuffix(domain);
  }
  
  private static int readULong(byte[] src, int index) throws NTLMEngineException {
    if (src.length < index + 4)
      throw new NTLMEngineException("NTLM authentication - buffer too small for DWORD");
    return src[index] & 0xFF | (src[(index + 1)] & 0xFF) << 8 | (src[(index + 2)] & 0xFF) << 16 | (src[(index + 3)] & 0xFF) << 24;
  }
  
  private static int readUShort(byte[] src, int index) throws NTLMEngineException
  {
    if (src.length < index + 2)
      throw new NTLMEngineException("NTLM authentication - buffer too small for WORD");
    return src[index] & 0xFF | (src[(index + 1)] & 0xFF) << 8;
  }
  
  private static byte[] readSecurityBuffer(byte[] src, int index) throws NTLMEngineException {
    int length = readUShort(src, index);
    int offset = readULong(src, index + 4);
    if (src.length < offset + length) {
      throw new NTLMEngineException("NTLM authentication - buffer too small for data item");
    }
    byte[] buffer = new byte[length];
    System.arraycopy(src, offset, buffer, 0, length);
    return buffer;
  }
  
  private static byte[] makeRandomChallenge() throws NTLMEngineException
  {
    if (RND_GEN == null) {
      throw new NTLMEngineException("Random generator not available");
    }
    byte[] rval = new byte[8];
    synchronized (RND_GEN) {
      RND_GEN.nextBytes(rval);
    }
    return rval;
  }
  
  private static byte[] makeNTLM2RandomChallenge() throws NTLMEngineException
  {
    if (RND_GEN == null) {
      throw new NTLMEngineException("Random generator not available");
    }
    byte[] rval = new byte[24];
    synchronized (RND_GEN) {
      RND_GEN.nextBytes(rval);
    }
    
    Arrays.fill(rval, 8, 24, (byte)0);
    return rval;
  }
  










  static byte[] getLMResponse(String password, byte[] challenge)
    throws NTLMEngineException
  {
    byte[] lmHash = lmHash(password);
    return lmResponse(lmHash, challenge);
  }
  










  static byte[] getNTLMResponse(String password, byte[] challenge)
    throws NTLMEngineException
  {
    byte[] ntlmHash = ntlmHash(password);
    return lmResponse(ntlmHash, challenge);
  }
  




















  static byte[] getNTLMv2Response(String target, String user, String password, byte[] challenge, byte[] clientChallenge, byte[] targetInformation)
    throws NTLMEngineException
  {
    byte[] ntlmv2Hash = ntlmv2Hash(target, user, password);
    byte[] blob = createBlob(clientChallenge, targetInformation);
    return lmv2Response(ntlmv2Hash, challenge, blob);
  }
  
















  static byte[] getLMv2Response(String target, String user, String password, byte[] challenge, byte[] clientChallenge)
    throws NTLMEngineException
  {
    byte[] ntlmv2Hash = ntlmv2Hash(target, user, password);
    return lmv2Response(ntlmv2Hash, challenge, clientChallenge);
  }
  













  static byte[] getNTLM2SessionResponse(String password, byte[] challenge, byte[] clientChallenge)
    throws NTLMEngineException
  {
    try
    {
      byte[] ntlmHash = ntlmHash(password);
      













      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(challenge);
      md5.update(clientChallenge);
      byte[] digest = md5.digest();
      
      byte[] sessionHash = new byte[8];
      System.arraycopy(digest, 0, sessionHash, 0, 8);
      return lmResponse(ntlmHash, sessionHash);
    } catch (Exception e) {
      if ((e instanceof NTLMEngineException))
        throw ((NTLMEngineException)e);
      throw new NTLMEngineException(e.getMessage(), e);
    }
  }
  






  private static byte[] lmHash(String password)
    throws NTLMEngineException
  {
    try
    {
      byte[] oemPassword = password.toUpperCase().getBytes("US-ASCII");
      int length = Math.min(oemPassword.length, 14);
      byte[] keyBytes = new byte[14];
      System.arraycopy(oemPassword, 0, keyBytes, 0, length);
      Key lowKey = createDESKey(keyBytes, 0);
      Key highKey = createDESKey(keyBytes, 7);
      byte[] magicConstant = "KGS!@#$%".getBytes("US-ASCII");
      Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
      des.init(1, lowKey);
      byte[] lowHash = des.doFinal(magicConstant);
      des.init(1, highKey);
      byte[] highHash = des.doFinal(magicConstant);
      byte[] lmHash = new byte[16];
      System.arraycopy(lowHash, 0, lmHash, 0, 8);
      System.arraycopy(highHash, 0, lmHash, 8, 8);
      return lmHash;
    } catch (Exception e) {
      throw new NTLMEngineException(e.getMessage(), e);
    }
  }
  






  private static byte[] ntlmHash(String password)
    throws NTLMEngineException
  {
    try
    {
      byte[] unicodePassword = password.getBytes("UnicodeLittleUnmarked");
      MD4 md4 = new MD4();
      md4.update(unicodePassword);
      return md4.getOutput();
    } catch (UnsupportedEncodingException e) {
      throw new NTLMEngineException("Unicode not supported: " + e.getMessage(), e);
    }
  }
  











  private static byte[] ntlmv2Hash(String target, String user, String password)
    throws NTLMEngineException
  {
    try
    {
      byte[] ntlmHash = ntlmHash(password);
      HMACMD5 hmacMD5 = new HMACMD5(ntlmHash);
      
      hmacMD5.update(user.toUpperCase().getBytes("UnicodeLittleUnmarked"));
      hmacMD5.update(target.getBytes("UnicodeLittleUnmarked"));
      return hmacMD5.getOutput();
    } catch (UnsupportedEncodingException e) {
      throw new NTLMEngineException("Unicode not supported! " + e.getMessage(), e);
    }
  }
  







  private static byte[] lmResponse(byte[] hash, byte[] challenge)
    throws NTLMEngineException
  {
    try
    {
      byte[] keyBytes = new byte[21];
      System.arraycopy(hash, 0, keyBytes, 0, 16);
      Key lowKey = createDESKey(keyBytes, 0);
      Key middleKey = createDESKey(keyBytes, 7);
      Key highKey = createDESKey(keyBytes, 14);
      Cipher des = Cipher.getInstance("DES/ECB/NoPadding");
      des.init(1, lowKey);
      byte[] lowResponse = des.doFinal(challenge);
      des.init(1, middleKey);
      byte[] middleResponse = des.doFinal(challenge);
      des.init(1, highKey);
      byte[] highResponse = des.doFinal(challenge);
      byte[] lmResponse = new byte[24];
      System.arraycopy(lowResponse, 0, lmResponse, 0, 8);
      System.arraycopy(middleResponse, 0, lmResponse, 8, 8);
      System.arraycopy(highResponse, 0, lmResponse, 16, 8);
      return lmResponse;
    } catch (Exception e) {
      throw new NTLMEngineException(e.getMessage(), e);
    }
  }
  













  private static byte[] lmv2Response(byte[] hash, byte[] challenge, byte[] clientData)
    throws NTLMEngineException
  {
    HMACMD5 hmacMD5 = new HMACMD5(hash);
    hmacMD5.update(challenge);
    hmacMD5.update(clientData);
    byte[] mac = hmacMD5.getOutput();
    byte[] lmv2Response = new byte[mac.length + clientData.length];
    System.arraycopy(mac, 0, lmv2Response, 0, mac.length);
    System.arraycopy(clientData, 0, lmv2Response, mac.length, clientData.length);
    return lmv2Response;
  }
  










  private static byte[] createBlob(byte[] clientChallenge, byte[] targetInformation)
  {
    byte[] blobSignature = { 1, 1, 0, 0 };
    byte[] reserved = { 0, 0, 0, 0 };
    byte[] unknown1 = { 0, 0, 0, 0 };
    long time = System.currentTimeMillis();
    time += 11644473600000L;
    time *= 10000L;
    
    byte[] timestamp = new byte[8];
    for (int i = 0; i < 8; i++) {
      timestamp[i] = ((byte)(int)time);
      time >>>= 8;
    }
    byte[] blob = new byte[blobSignature.length + reserved.length + timestamp.length + 8 + unknown1.length + targetInformation.length];
    
    int offset = 0;
    System.arraycopy(blobSignature, 0, blob, offset, blobSignature.length);
    offset += blobSignature.length;
    System.arraycopy(reserved, 0, blob, offset, reserved.length);
    offset += reserved.length;
    System.arraycopy(timestamp, 0, blob, offset, timestamp.length);
    offset += timestamp.length;
    System.arraycopy(clientChallenge, 0, blob, offset, 8);
    offset += 8;
    System.arraycopy(unknown1, 0, blob, offset, unknown1.length);
    offset += unknown1.length;
    System.arraycopy(targetInformation, 0, blob, offset, targetInformation.length);
    return blob;
  }
  











  private static Key createDESKey(byte[] bytes, int offset)
  {
    byte[] keyBytes = new byte[7];
    System.arraycopy(bytes, offset, keyBytes, 0, 7);
    byte[] material = new byte[8];
    material[0] = keyBytes[0];
    material[1] = ((byte)(keyBytes[0] << 7 | (keyBytes[1] & 0xFF) >>> 1));
    material[2] = ((byte)(keyBytes[1] << 6 | (keyBytes[2] & 0xFF) >>> 2));
    material[3] = ((byte)(keyBytes[2] << 5 | (keyBytes[3] & 0xFF) >>> 3));
    material[4] = ((byte)(keyBytes[3] << 4 | (keyBytes[4] & 0xFF) >>> 4));
    material[5] = ((byte)(keyBytes[4] << 3 | (keyBytes[5] & 0xFF) >>> 5));
    material[6] = ((byte)(keyBytes[5] << 2 | (keyBytes[6] & 0xFF) >>> 6));
    material[7] = ((byte)(keyBytes[6] << 1));
    oddParity(material);
    return new SecretKeySpec(material, "DES");
  }
  





  private static void oddParity(byte[] bytes)
  {
    for (int i = 0; i < bytes.length; i++) {
      byte b = bytes[i];
      boolean needsParity = ((b >>> 7 ^ b >>> 6 ^ b >>> 5 ^ b >>> 4 ^ b >>> 3 ^ b >>> 2 ^ b >>> 1) & 0x1) == 0;
      
      if (needsParity) {
        int tmp58_57 = i;bytes[tmp58_57] = ((byte)(bytes[tmp58_57] | 0x1));
      } else {
        int tmp69_68 = i;bytes[tmp69_68] = ((byte)(bytes[tmp69_68] & 0xFFFFFFFE));
      }
    }
  }
  

  static class NTLMMessage
  {
    private byte[] messageContents = null;
    

    private int currentOutputPosition = 0;
    

    NTLMMessage() {}
    
    NTLMMessage(String messageBody, int expectedType)
      throws NTLMEngineException
    {
      this.messageContents = Base64.decodeBase64(EncodingUtils.getBytes(messageBody, "ASCII"));
      

      if (this.messageContents.length < NTLMEngineImpl.SIGNATURE.length)
        throw new NTLMEngineException("NTLM message decoding error - packet too short");
      int i = 0;
      while (i < NTLMEngineImpl.SIGNATURE.length) {
        if (this.messageContents[i] != NTLMEngineImpl.SIGNATURE[i]) {
          throw new NTLMEngineException("NTLM message expected - instead got unrecognized bytes");
        }
        i++;
      }
      

      int type = readULong(NTLMEngineImpl.SIGNATURE.length);
      if (type != expectedType) {
        throw new NTLMEngineException("NTLM type " + Integer.toString(expectedType) + " message expected - instead got type " + Integer.toString(type));
      }
      
      this.currentOutputPosition = this.messageContents.length;
    }
    



    protected int getPreambleLength()
    {
      return NTLMEngineImpl.SIGNATURE.length + 4;
    }
    
    protected int getMessageLength()
    {
      return this.currentOutputPosition;
    }
    
    protected byte readByte(int position) throws NTLMEngineException
    {
      if (this.messageContents.length < position + 1)
        throw new NTLMEngineException("NTLM: Message too short");
      return this.messageContents[position];
    }
    
    protected void readBytes(byte[] buffer, int position) throws NTLMEngineException
    {
      if (this.messageContents.length < position + buffer.length)
        throw new NTLMEngineException("NTLM: Message too short");
      System.arraycopy(this.messageContents, position, buffer, 0, buffer.length);
    }
    
    protected int readUShort(int position) throws NTLMEngineException
    {
      return NTLMEngineImpl.readUShort(this.messageContents, position);
    }
    
    protected int readULong(int position) throws NTLMEngineException
    {
      return NTLMEngineImpl.readULong(this.messageContents, position);
    }
    
    protected byte[] readSecurityBuffer(int position) throws NTLMEngineException
    {
      return NTLMEngineImpl.readSecurityBuffer(this.messageContents, position);
    }
    







    protected void prepareResponse(int maxlength, int messageType)
    {
      this.messageContents = new byte[maxlength];
      this.currentOutputPosition = 0;
      addBytes(NTLMEngineImpl.SIGNATURE);
      addULong(messageType);
    }
    





    protected void addByte(byte b)
    {
      this.messageContents[this.currentOutputPosition] = b;
      this.currentOutputPosition += 1;
    }
    





    protected void addBytes(byte[] bytes)
    {
      for (int i = 0; i < bytes.length; i++) {
        this.messageContents[this.currentOutputPosition] = bytes[i];
        this.currentOutputPosition += 1;
      }
    }
    
    protected void addUShort(int value)
    {
      addByte((byte)(value & 0xFF));
      addByte((byte)(value >> 8 & 0xFF));
    }
    
    protected void addULong(int value)
    {
      addByte((byte)(value & 0xFF));
      addByte((byte)(value >> 8 & 0xFF));
      addByte((byte)(value >> 16 & 0xFF));
      addByte((byte)(value >> 24 & 0xFF));
    }
    


    String getResponse()
    {
      byte[] resp;
      
      byte[] resp;
      
      if (this.messageContents.length > this.currentOutputPosition) {
        byte[] tmp = new byte[this.currentOutputPosition];
        for (int i = 0; i < this.currentOutputPosition; i++) {
          tmp[i] = this.messageContents[i];
        }
        resp = tmp;
      } else {
        resp = this.messageContents;
      }
      return EncodingUtils.getAsciiString(Base64.encodeBase64(resp));
    }
  }
  
  static class Type1Message
    extends NTLMEngineImpl.NTLMMessage
  {
    protected byte[] hostBytes;
    protected byte[] domainBytes;
    
    Type1Message(String domain, String host)
      throws NTLMEngineException
    {
      try
      {
        host = NTLMEngineImpl.convertHost(host);
        
        domain = NTLMEngineImpl.convertDomain(domain);
        
        this.hostBytes = host.getBytes("UnicodeLittleUnmarked");
        this.domainBytes = domain.toUpperCase().getBytes("UnicodeLittleUnmarked");
      } catch (UnsupportedEncodingException e) {
        throw new NTLMEngineException("Unicode unsupported: " + e.getMessage(), e);
      }
    }
    






    String getResponse()
    {
      int finalLength = 32 + this.hostBytes.length + this.domainBytes.length;
      


      prepareResponse(finalLength, 1);
      

      addULong(537395765);
      






      addUShort(this.domainBytes.length);
      addUShort(this.domainBytes.length);
      

      addULong(this.hostBytes.length + 32);
      

      addUShort(this.hostBytes.length);
      addUShort(this.hostBytes.length);
      

      addULong(32);
      

      addBytes(this.hostBytes);
      

      addBytes(this.domainBytes);
      
      return super.getResponse();
    }
  }
  
  static class Type2Message extends NTLMEngineImpl.NTLMMessage
  {
    protected byte[] challenge;
    protected String target;
    protected byte[] targetInfo;
    protected int flags;
    
    Type2Message(String message) throws NTLMEngineException
    {
      super(2);
      


      this.challenge = new byte[8];
      readBytes(this.challenge, 24);
      
      this.flags = readULong(20);
      if ((this.flags & 0x1) == 0) {
        throw new NTLMEngineException("NTLM type 2 message has flags that make no sense: " + Integer.toString(this.flags));
      }
      

      this.target = null;
      


      if (getMessageLength() >= 20) {
        byte[] bytes = readSecurityBuffer(12);
        if (bytes.length != 0) {
          try {
            this.target = new String(bytes, "UnicodeLittleUnmarked");
          } catch (UnsupportedEncodingException e) {
            throw new NTLMEngineException(e.getMessage(), e);
          }
        }
      }
      

      this.targetInfo = null;
      
      if (getMessageLength() >= 48) {
        byte[] bytes = readSecurityBuffer(40);
        if (bytes.length != 0) {
          this.targetInfo = bytes;
        }
      }
    }
    
    byte[] getChallenge()
    {
      return this.challenge;
    }
    
    String getTarget()
    {
      return this.target;
    }
    
    byte[] getTargetInfo()
    {
      return this.targetInfo;
    }
    
    int getFlags()
    {
      return this.flags;
    }
  }
  

  static class Type3Message
    extends NTLMEngineImpl.NTLMMessage
  {
    protected int type2Flags;
    
    protected byte[] domainBytes;
    
    protected byte[] hostBytes;
    
    protected byte[] userBytes;
    
    protected byte[] lmResp;
    protected byte[] ntResp;
    
    Type3Message(String domain, String host, String user, String password, byte[] nonce, int type2Flags, String target, byte[] targetInformation)
      throws NTLMEngineException
    {
      this.type2Flags = type2Flags;
      

      host = NTLMEngineImpl.convertHost(host);
      
      domain = NTLMEngineImpl.convertDomain(domain);
      

      try
      {
        if ((targetInformation != null) && (target != null)) {
          byte[] clientChallenge = NTLMEngineImpl.access$600();
          this.ntResp = NTLMEngineImpl.getNTLMv2Response(target, user, password, nonce, clientChallenge, targetInformation);
          
          this.lmResp = NTLMEngineImpl.getLMv2Response(target, user, password, nonce, clientChallenge);
        }
        else if ((type2Flags & 0x80000) != 0)
        {
          byte[] clientChallenge = NTLMEngineImpl.access$700();
          
          this.ntResp = NTLMEngineImpl.getNTLM2SessionResponse(password, nonce, clientChallenge);
          this.lmResp = clientChallenge;


        }
        else
        {

          this.ntResp = NTLMEngineImpl.getNTLMResponse(password, nonce);
          this.lmResp = NTLMEngineImpl.getLMResponse(password, nonce);
        }
        
      }
      catch (NTLMEngineException e)
      {
        this.ntResp = new byte[0];
        this.lmResp = NTLMEngineImpl.getLMResponse(password, nonce);
      }
      try
      {
        this.domainBytes = domain.toUpperCase().getBytes("UnicodeLittleUnmarked");
        this.hostBytes = host.getBytes("UnicodeLittleUnmarked");
        this.userBytes = user.getBytes("UnicodeLittleUnmarked");
      } catch (UnsupportedEncodingException e) {
        throw new NTLMEngineException("Unicode not supported: " + e.getMessage(), e);
      }
    }
    

    String getResponse()
    {
      int ntRespLen = this.ntResp.length;
      int lmRespLen = this.lmResp.length;
      
      int domainLen = this.domainBytes.length;
      int hostLen = this.hostBytes.length;
      int userLen = this.userBytes.length;
      

      int lmRespOffset = 64;
      int ntRespOffset = lmRespOffset + lmRespLen;
      int domainOffset = ntRespOffset + ntRespLen;
      int userOffset = domainOffset + domainLen;
      int hostOffset = userOffset + userLen;
      int sessionKeyOffset = hostOffset + hostLen;
      int finalLength = sessionKeyOffset + 0;
      

      prepareResponse(finalLength, 3);
      

      addUShort(lmRespLen);
      addUShort(lmRespLen);
      

      addULong(lmRespOffset);
      

      addUShort(ntRespLen);
      addUShort(ntRespLen);
      

      addULong(ntRespOffset);
      

      addUShort(domainLen);
      addUShort(domainLen);
      

      addULong(domainOffset);
      

      addUShort(userLen);
      addUShort(userLen);
      

      addULong(userOffset);
      

      addUShort(hostLen);
      addUShort(hostLen);
      

      addULong(hostOffset);
      

      addULong(0);
      

      addULong(finalLength);
      


      addULong(0x20000205 | this.type2Flags & 0x80000 | this.type2Flags & 0x10 | this.type2Flags & 0x20 | this.type2Flags & 0x40000000 | this.type2Flags & 0x8000);
      





      addBytes(this.lmResp);
      addBytes(this.ntResp);
      addBytes(this.domainBytes);
      addBytes(this.userBytes);
      addBytes(this.hostBytes);
      
      return super.getResponse();
    }
  }
  
  static void writeULong(byte[] buffer, int value, int offset) {
    buffer[offset] = ((byte)(value & 0xFF));
    buffer[(offset + 1)] = ((byte)(value >> 8 & 0xFF));
    buffer[(offset + 2)] = ((byte)(value >> 16 & 0xFF));
    buffer[(offset + 3)] = ((byte)(value >> 24 & 0xFF));
  }
  
  static int F(int x, int y, int z) {
    return x & y | (x ^ 0xFFFFFFFF) & z;
  }
  
  static int G(int x, int y, int z) {
    return x & y | x & z | y & z;
  }
  
  static int H(int x, int y, int z) {
    return x ^ y ^ z;
  }
  
  static int rotintlft(int val, int numbits) {
    return val << numbits | val >>> 32 - numbits;
  }
  






  static class MD4
  {
    protected int A = 1732584193;
    protected int B = -271733879;
    protected int C = -1732584194;
    protected int D = 271733878;
    protected long count = 0L;
    protected byte[] dataBuffer = new byte[64];
    





    void update(byte[] input)
    {
      int curBufferPos = (int)(this.count & 0x3F);
      int inputIndex = 0;
      while (input.length - inputIndex + curBufferPos >= this.dataBuffer.length)
      {


        int transferAmt = this.dataBuffer.length - curBufferPos;
        System.arraycopy(input, inputIndex, this.dataBuffer, curBufferPos, transferAmt);
        this.count += transferAmt;
        curBufferPos = 0;
        inputIndex += transferAmt;
        processBuffer();
      }
      


      if (inputIndex < input.length) {
        int transferAmt = input.length - inputIndex;
        System.arraycopy(input, inputIndex, this.dataBuffer, curBufferPos, transferAmt);
        this.count += transferAmt;
        curBufferPos += transferAmt;
      }
    }
    

    byte[] getOutput()
    {
      int bufferIndex = (int)(this.count & 0x3F);
      int padLen = bufferIndex < 56 ? 56 - bufferIndex : 120 - bufferIndex;
      byte[] postBytes = new byte[padLen + 8];
      

      postBytes[0] = -128;
      
      for (int i = 0; i < 8; i++) {
        postBytes[(padLen + i)] = ((byte)(int)(this.count * 8L >>> 8 * i));
      }
      

      update(postBytes);
      

      byte[] result = new byte[16];
      NTLMEngineImpl.writeULong(result, this.A, 0);
      NTLMEngineImpl.writeULong(result, this.B, 4);
      NTLMEngineImpl.writeULong(result, this.C, 8);
      NTLMEngineImpl.writeULong(result, this.D, 12);
      return result;
    }
    
    protected void processBuffer()
    {
      int[] d = new int[16];
      
      for (int i = 0; i < 16; i++) {
        d[i] = ((this.dataBuffer[(i * 4)] & 0xFF) + ((this.dataBuffer[(i * 4 + 1)] & 0xFF) << 8) + ((this.dataBuffer[(i * 4 + 2)] & 0xFF) << 16) + ((this.dataBuffer[(i * 4 + 3)] & 0xFF) << 24));
      }
      



      int AA = this.A;
      int BB = this.B;
      int CC = this.C;
      int DD = this.D;
      round1(d);
      round2(d);
      round3(d);
      this.A += AA;
      this.B += BB;
      this.C += CC;
      this.D += DD;
    }
    
    protected void round1(int[] d)
    {
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[0], 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[1], 7);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[2], 11);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[3], 19);
      
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[4], 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[5], 7);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[6], 11);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[7], 19);
      
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[8], 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[9], 7);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[10], 11);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[11], 19);
      
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.F(this.B, this.C, this.D) + d[12], 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.F(this.A, this.B, this.C) + d[13], 7);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.F(this.D, this.A, this.B) + d[14], 11);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.F(this.C, this.D, this.A) + d[15], 19);
    }
    
    protected void round2(int[] d) {
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[0] + 1518500249, 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[4] + 1518500249, 5);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[8] + 1518500249, 9);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[12] + 1518500249, 13);
      
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[1] + 1518500249, 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[5] + 1518500249, 5);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[9] + 1518500249, 9);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[13] + 1518500249, 13);
      
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[2] + 1518500249, 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[6] + 1518500249, 5);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[10] + 1518500249, 9);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[14] + 1518500249, 13);
      
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.G(this.B, this.C, this.D) + d[3] + 1518500249, 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.G(this.A, this.B, this.C) + d[7] + 1518500249, 5);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.G(this.D, this.A, this.B) + d[11] + 1518500249, 9);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.G(this.C, this.D, this.A) + d[15] + 1518500249, 13);
    }
    
    protected void round3(int[] d)
    {
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[0] + 1859775393, 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[8] + 1859775393, 9);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[4] + 1859775393, 11);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[12] + 1859775393, 15);
      
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[2] + 1859775393, 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[10] + 1859775393, 9);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[6] + 1859775393, 11);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[14] + 1859775393, 15);
      
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[1] + 1859775393, 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[9] + 1859775393, 9);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[5] + 1859775393, 11);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[13] + 1859775393, 15);
      
      this.A = NTLMEngineImpl.rotintlft(this.A + NTLMEngineImpl.H(this.B, this.C, this.D) + d[3] + 1859775393, 3);
      this.D = NTLMEngineImpl.rotintlft(this.D + NTLMEngineImpl.H(this.A, this.B, this.C) + d[11] + 1859775393, 9);
      this.C = NTLMEngineImpl.rotintlft(this.C + NTLMEngineImpl.H(this.D, this.A, this.B) + d[7] + 1859775393, 11);
      this.B = NTLMEngineImpl.rotintlft(this.B + NTLMEngineImpl.H(this.C, this.D, this.A) + d[15] + 1859775393, 15);
    }
  }
  

  static class HMACMD5
  {
    protected byte[] ipad;
    
    protected byte[] opad;
    protected MessageDigest md5;
    
    HMACMD5(byte[] key)
      throws NTLMEngineException
    {
      try
      {
        this.md5 = MessageDigest.getInstance("MD5");
      }
      catch (Exception ex)
      {
        throw new NTLMEngineException("Error getting md5 message digest implementation: " + ex.getMessage(), ex);
      }
      


      this.ipad = new byte[64];
      this.opad = new byte[64];
      
      int keyLength = key.length;
      if (keyLength > 64)
      {
        this.md5.update(key);
        key = this.md5.digest();
        keyLength = key.length;
      }
      int i = 0;
      while (i < keyLength) {
        this.ipad[i] = ((byte)(key[i] ^ 0x36));
        this.opad[i] = ((byte)(key[i] ^ 0x5C));
        i++;
      }
      while (i < 64) {
        this.ipad[i] = 54;
        this.opad[i] = 92;
        i++;
      }
      

      this.md5.reset();
      this.md5.update(this.ipad);
    }
    

    byte[] getOutput()
    {
      byte[] digest = this.md5.digest();
      this.md5.update(this.opad);
      return this.md5.digest(digest);
    }
    
    void update(byte[] input)
    {
      this.md5.update(input);
    }
    
    void update(byte[] input, int offset, int length)
    {
      this.md5.update(input, offset, length);
    }
  }
  

  public String generateType1Msg(String domain, String workstation)
    throws NTLMEngineException
  {
    return getType1Message(workstation, domain);
  }
  



  public String generateType3Msg(String username, String password, String domain, String workstation, String challenge)
    throws NTLMEngineException
  {
    Type2Message t2m = new Type2Message(challenge);
    return getType3Message(username, password, workstation, domain, t2m.getChallenge(), t2m.getFlags(), t2m.getTarget(), t2m.getTargetInfo());
  }
}
