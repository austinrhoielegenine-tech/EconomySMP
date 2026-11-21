package com.neovisionaries.ws.client;

abstract class PerMessageCompressionExtension extends WebSocketExtension {
   public PerMessageCompressionExtension(String name) {
      super(name);
   }

   public PerMessageCompressionExtension(WebSocketExtension source) {
      super(source);
   }

   protected abstract byte[] decompress(byte[] var1) throws WebSocketException;

   protected abstract byte[] compress(byte[] var1) throws WebSocketException;
}
