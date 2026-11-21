package com.neovisionaries.ws.client;

class FixedLiteralLengthHuffman extends Huffman {
   private static final FixedLiteralLengthHuffman INSTANCE = new FixedLiteralLengthHuffman();

   private FixedLiteralLengthHuffman() {
      super(buildCodeLensFromSym());
   }

   private static int[] buildCodeLensFromSym() {
      int[] codeLengths = new int[288];

      int symbol;
      for(symbol = 0; symbol < 144; ++symbol) {
         codeLengths[symbol] = 8;
      }

      while(symbol < 256) {
         codeLengths[symbol] = 9;
         ++symbol;
      }

      while(symbol < 280) {
         codeLengths[symbol] = 7;
         ++symbol;
      }

      while(symbol < 288) {
         codeLengths[symbol] = 8;
         ++symbol;
      }

      return codeLengths;
   }

   public static FixedLiteralLengthHuffman getInstance() {
      return INSTANCE;
   }
}
