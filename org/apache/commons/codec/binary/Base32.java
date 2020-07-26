package org.apache.commons.codec.binary;














public class Base32
  extends BaseNCodec
{
  private static final int BITS_PER_ENCODED_BYTE = 5;
  












  private static final int BYTES_PER_ENCODED_BLOCK = 8;
  












  private static final int BYTES_PER_UNENCODED_BLOCK = 5;
  











  private static final byte[] CHUNK_SEPARATOR = { 13, 10 };
  






  private static final byte[] DECODE_TABLE = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 63, -1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25 };
  












  private static final byte[] ENCODE_TABLE = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 50, 51, 52, 53, 54, 55 };
  










  private static final byte[] HEX_DECODE_TABLE = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 63, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32 };
  












  private static final byte[] HEX_ENCODE_TABLE = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86 };
  





  private static final int MASK_5BITS = 31;
  





  private long bitWorkArea;
  





  private final int decodeSize;
  




  private final byte[] decodeTable;
  




  private final int encodeSize;
  




  private final byte[] encodeTable;
  




  private final byte[] lineSeparator;
  





  public Base32()
  {
    this(false);
  }
  






  public Base32(boolean useHex)
  {
    this(0, null, useHex);
  }
  









  public Base32(int lineLength)
  {
    this(lineLength, CHUNK_SEPARATOR);
  }
  
















  public Base32(int lineLength, byte[] lineSeparator)
  {
    this(lineLength, lineSeparator, false);
  }
  


















  public Base32(int lineLength, byte[] lineSeparator, boolean useHex)
  {
    super(5, 8, lineLength, lineSeparator == null ? 0 : lineSeparator.length);
    

    if (useHex) {
      this.encodeTable = HEX_ENCODE_TABLE;
      this.decodeTable = HEX_DECODE_TABLE;
    } else {
      this.encodeTable = ENCODE_TABLE;
      this.decodeTable = DECODE_TABLE;
    }
    if (lineLength > 0) {
      if (lineSeparator == null) {
        throw new IllegalArgumentException("lineLength " + lineLength + " > 0, but lineSeparator is null");
      }
      
      if (containsAlphabetOrPad(lineSeparator)) {
        String sep = StringUtils.newStringUtf8(lineSeparator);
        throw new IllegalArgumentException("lineSeparator must not contain Base32 characters: [" + sep + "]");
      }
      this.encodeSize = (8 + lineSeparator.length);
      this.lineSeparator = new byte[lineSeparator.length];
      System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
    } else {
      this.encodeSize = 8;
      this.lineSeparator = null;
    }
    this.decodeSize = (this.encodeSize - 1);
  }
  





















  void decode(byte[] in, int inPos, int inAvail)
  {
    if (this.eof) {
      return;
    }
    if (inAvail < 0) {
      this.eof = true;
    }
    for (int i = 0; i < inAvail; i++) {
      byte b = in[(inPos++)];
      if (b == 61)
      {
        this.eof = true;
        break;
      }
      ensureBufferSize(this.decodeSize);
      if ((b >= 0) && (b < this.decodeTable.length)) {
        int result = this.decodeTable[b];
        if (result >= 0) {
          this.modulus = ((this.modulus + 1) % 8);
          this.bitWorkArea = ((this.bitWorkArea << 5) + result);
          if (this.modulus == 0) {
            this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 32 & 0xFF));
            this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 24 & 0xFF));
            this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 16 & 0xFF));
            this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 8 & 0xFF));
            this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea & 0xFF));
          }
        }
      }
    }
    




    if ((this.eof) && (this.modulus >= 2)) {
      ensureBufferSize(this.decodeSize);
      

      switch (this.modulus) {
      case 2: 
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 2 & 0xFF));
        break;
      case 3: 
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 7 & 0xFF));
        break;
      case 4: 
        this.bitWorkArea >>= 4;
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 8 & 0xFF));
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea & 0xFF));
        break;
      case 5: 
        this.bitWorkArea >>= 1;
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 16 & 0xFF));
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 8 & 0xFF));
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea & 0xFF));
        break;
      case 6: 
        this.bitWorkArea >>= 6;
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 16 & 0xFF));
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 8 & 0xFF));
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea & 0xFF));
        break;
      case 7: 
        this.bitWorkArea >>= 3;
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 24 & 0xFF));
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 16 & 0xFF));
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea >> 8 & 0xFF));
        this.buffer[(this.pos++)] = ((byte)(int)(this.bitWorkArea & 0xFF));
      }
      
    }
  }
  














  void encode(byte[] in, int inPos, int inAvail)
  {
    if (this.eof) {
      return;
    }
    

    if (inAvail < 0) {
      this.eof = true;
      if ((0 == this.modulus) && (this.lineLength == 0)) {
        return;
      }
      ensureBufferSize(this.encodeSize);
      int savedPos = this.pos;
      switch (this.modulus) {
      case 1: 
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 3) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea << 2) & 0x1F)];
        this.buffer[(this.pos++)] = 61;
        this.buffer[(this.pos++)] = 61;
        this.buffer[(this.pos++)] = 61;
        this.buffer[(this.pos++)] = 61;
        this.buffer[(this.pos++)] = 61;
        this.buffer[(this.pos++)] = 61;
        break;
      
      case 2: 
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 11) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 6) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 1) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea << 4) & 0x1F)];
        this.buffer[(this.pos++)] = 61;
        this.buffer[(this.pos++)] = 61;
        this.buffer[(this.pos++)] = 61;
        this.buffer[(this.pos++)] = 61;
        break;
      case 3: 
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 19) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 14) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 9) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 4) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea << 1) & 0x1F)];
        this.buffer[(this.pos++)] = 61;
        this.buffer[(this.pos++)] = 61;
        this.buffer[(this.pos++)] = 61;
        break;
      case 4: 
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 27) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 22) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 17) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 12) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 7) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 2) & 0x1F)];
        this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea << 3) & 0x1F)];
        this.buffer[(this.pos++)] = 61;
      }
      
      this.currentLinePos += this.pos - savedPos;
      
      if ((this.lineLength > 0) && (this.currentLinePos > 0)) {
        System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
        this.pos += this.lineSeparator.length;
      }
    } else {
      for (int i = 0; i < inAvail; i++) {
        ensureBufferSize(this.encodeSize);
        this.modulus = ((this.modulus + 1) % 5);
        int b = in[(inPos++)];
        if (b < 0) {
          b += 256;
        }
        this.bitWorkArea = ((this.bitWorkArea << 8) + b);
        if (0 == this.modulus) {
          this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 35) & 0x1F)];
          this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 30) & 0x1F)];
          this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 25) & 0x1F)];
          this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 20) & 0x1F)];
          this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 15) & 0x1F)];
          this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 10) & 0x1F)];
          this.buffer[(this.pos++)] = this.encodeTable[((int)(this.bitWorkArea >> 5) & 0x1F)];
          this.buffer[(this.pos++)] = this.encodeTable[((int)this.bitWorkArea & 0x1F)];
          this.currentLinePos += 8;
          if ((this.lineLength > 0) && (this.lineLength <= this.currentLinePos)) {
            System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
            this.pos += this.lineSeparator.length;
            this.currentLinePos = 0;
          }
        }
      }
    }
  }
  







  public boolean isInAlphabet(byte octet)
  {
    return (octet >= 0) && (octet < this.decodeTable.length) && (this.decodeTable[octet] != -1);
  }
}
