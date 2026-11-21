package com.neovisionaries.ws.client;

import java.util.Map;

class PerMessageDeflateExtension extends PerMessageCompressionExtension {
   private static final String SERVER_NO_CONTEXT_TAKEOVER = "server_no_context_takeover";
   private static final String CLIENT_NO_CONTEXT_TAKEOVER = "client_no_context_takeover";
   private static final String SERVER_MAX_WINDOW_BITS = "server_max_window_bits";
   private static final String CLIENT_MAX_WINDOW_BITS = "client_max_window_bits";
   private static final byte[] COMPRESSION_TERMINATOR = new byte[]{0, 0, -1, -1};
   private static final int MIN_BITS = 8;
   private static final int MAX_BITS = 15;
   private static final int MIN_WINDOW_SIZE = 256;
   private static final int MAX_WINDOW_SIZE = 32768;
   private static final int INCOMING_SLIDING_WINDOW_MARGIN = 1024;
   private boolean mServerNoContextTakeover;
   private boolean mClientNoContextTakeover;
   private int mServerWindowSize = 32768;
   private int mClientWindowSize = 32768;
   private int mIncomingSlidingWindowBufferSize;
   private ByteArray mIncomingSlidingWindow;

   public PerMessageDeflateExtension() {
      super("permessage-deflate");
   }

   public PerMessageDeflateExtension(String name) {
      super(name);
   }

   void validate() throws WebSocketException {
      for(Map.Entry<String, String> entry : this.getParameters().entrySet()) {
         this.validateParameter((String)entry.getKey(), (String)entry.getValue());
      }

      this.mIncomingSlidingWindowBufferSize = this.mServerWindowSize + 1024;
   }

   public boolean isServerNoContextTakeover() {
      return this.mServerNoContextTakeover;
   }

   public boolean isClientNoContextTakeover() {
      return this.mClientNoContextTakeover;
   }

   public int getServerWindowSize() {
      return this.mServerWindowSize;
   }

   public int getClientWindowSize() {
      return this.mClientWindowSize;
   }

   private void validateParameter(String key, String value) throws WebSocketException {
      if ("server_no_context_takeover".equals(key)) {
         this.mServerNoContextTakeover = true;
      } else if ("client_no_context_takeover".equals(key)) {
         this.mClientNoContextTakeover = true;
      } else if ("server_max_window_bits".equals(key)) {
         this.mServerWindowSize = this.computeWindowSize(key, value);
      } else {
         if (!"client_max_window_bits".equals(key)) {
            throw new WebSocketException(WebSocketError.PERMESSAGE_DEFLATE_UNSUPPORTED_PARAMETER, "permessage-deflate extension contains an unsupported parameter: " + key);
         }

         this.mClientWindowSize = this.computeWindowSize(key, value);
      }

   }

   private int computeWindowSize(String key, String value) throws WebSocketException {
      int bits = this.extractMaxWindowBits(key, value);
      int size = 256;

      for(int i = 8; i < bits; ++i) {
         size *= 2;
      }

      return size;
   }

   private int extractMaxWindowBits(String key, String value) throws WebSocketException {
      int bits = this.parseMaxWindowBits(value);
      if (bits < 0) {
         String message = String.format("The value of %s parameter of permessage-deflate extension is invalid: %s", key, value);
         throw new WebSocketException(WebSocketError.PERMESSAGE_DEFLATE_INVALID_MAX_WINDOW_BITS, message);
      } else {
         return bits;
      }
   }

   private int parseMaxWindowBits(String value) {
      if (value == null) {
         return -1;
      } else {
         int bits;
         try {
            bits = Integer.parseInt(value);
         } catch (NumberFormatException var4) {
            return -1;
         }

         return bits >= 8 && 15 >= bits ? bits : -1;
      }
   }

   protected byte[] decompress(byte[] compressed) throws WebSocketException {
      int inputLen = compressed.length + COMPRESSION_TERMINATOR.length;
      ByteArray input = new ByteArray(inputLen);
      input.put(compressed);
      input.put(COMPRESSION_TERMINATOR);
      if (this.mIncomingSlidingWindow == null) {
         this.mIncomingSlidingWindow = new ByteArray(this.mIncomingSlidingWindowBufferSize);
      }

      int outPos = this.mIncomingSlidingWindow.length();

      try {
         DeflateDecompressor.decompress(input, this.mIncomingSlidingWindow);
      } catch (Exception e) {
         throw new WebSocketException(WebSocketError.DECOMPRESSION_ERROR, String.format("Failed to decompress the message: %s", e.getMessage()), e);
      }

      byte[] output = this.mIncomingSlidingWindow.toBytes(outPos);
      this.mIncomingSlidingWindow.shrink(this.mIncomingSlidingWindowBufferSize);
      if (this.mServerNoContextTakeover) {
         this.mIncomingSlidingWindow.clear();
      }

      return output;
   }

   protected byte[] compress(byte[] plain) throws WebSocketException {
      if (!this.canCompress(plain)) {
         return plain;
      } else {
         try {
            byte[] compressed = DeflateCompressor.compress(plain);
            return adjustCompressedData(compressed);
         } catch (Exception e) {
            throw new WebSocketException(WebSocketError.COMPRESSION_ERROR, String.format("Failed to compress the message: %s", e.getMessage()), e);
         }
      }
   }

   private boolean canCompress(byte[] plain) {
      if (this.mClientWindowSize == 32768) {
         return true;
      } else {
         return plain.length < this.mClientWindowSize;
      }
   }

   private static byte[] adjustCompressedData(byte[] compressed) throws FormatException {
      ByteArray data = new ByteArray(compressed.length + 1);
      data.put(compressed);
      int[] bitIndex = new int[1];
      boolean[] hasEmptyBlock = new boolean[1];

      while(skipBlock(data, bitIndex, hasEmptyBlock)) {
      }

      if (hasEmptyBlock[0]) {
         return data.toBytes(0, (bitIndex[0] - 1) / 8 + 1 - 4);
      } else {
         appendEmptyBlock(data, bitIndex);
         return data.toBytes(0, (bitIndex[0] - 1) / 8 + 1);
      }
   }

   private static void appendEmptyBlock(ByteArray data, int[] bitIndex) {
      int shift = bitIndex[0] % 8;
      switch (shift) {
         case 0:
         case 6:
         case 7:
            data.put(0);
         default:
            bitIndex[0] += 3;
      }
   }

   private static boolean skipBlock(ByteArray input, int[] bitIndex, boolean[] hasEmptyBlock) throws FormatException {
      boolean last = input.readBit(bitIndex);
      if (last) {
         input.clearBit(bitIndex[0] - 1);
      }

      int type = input.readBits(bitIndex, 2);
      boolean plain0 = false;
      switch (type) {
         case 0:
            plain0 = skipPlainBlock(input, bitIndex) == 0;
            break;
         case 1:
            skipFixedBlock(input, bitIndex);
            break;
         case 2:
            skipDynamicBlock(input, bitIndex);
            break;
         default:
            String message = String.format("[%s] Bad compression type '11' at the bit index '%d'.", PerMessageDeflateExtension.class.getSimpleName(), bitIndex[0]);
            throw new FormatException(message);
      }

      if (input.length() <= bitIndex[0] / 8) {
         last = true;
      }

      if (last && plain0) {
         hasEmptyBlock[0] = true;
      }

      return !last;
   }

   private static int skipPlainBlock(ByteArray input, int[] bitIndex) {
      int bi = bitIndex[0] + 7 & -8;
      int index = bi / 8;
      int len = (input.get(index) & 255) + (input.get(index + 1) & 255) * 256;
      index += 4;
      bitIndex[0] = (index + len) * 8;
      return len;
   }

   private static void skipFixedBlock(ByteArray input, int[] bitIndex) throws FormatException {
      skipData(input, bitIndex, FixedLiteralLengthHuffman.getInstance(), FixedDistanceHuffman.getInstance());
   }

   private static void skipDynamicBlock(ByteArray input, int[] bitIndex) throws FormatException {
      Huffman[] tables = new Huffman[2];
      DeflateUtil.readDynamicTables(input, bitIndex, tables);
      skipData(input, bitIndex, tables[0], tables[1]);
   }

   private static void skipData(ByteArray input, int[] bitIndex, Huffman literalLengthHuffman, Huffman distanceHuffman) throws FormatException {
      while(true) {
         int literalLength = literalLengthHuffman.readSym(input, bitIndex);
         if (literalLength == 256) {
            return;
         }

         if (0 > literalLength || literalLength > 255) {
            DeflateUtil.readLength(input, bitIndex, literalLength);
            DeflateUtil.readDistance(input, bitIndex, distanceHuffman);
         }
      }
   }
}
