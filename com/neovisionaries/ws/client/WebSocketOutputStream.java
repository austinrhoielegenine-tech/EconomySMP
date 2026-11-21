package com.neovisionaries.ws.client;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class WebSocketOutputStream extends BufferedOutputStream {
   public WebSocketOutputStream(OutputStream out) {
      super(out);
   }

   public void write(String string) throws IOException {
      byte[] bytes = Misc.getBytesUTF8(string);
      this.write((byte[])bytes);
   }

   public void write(WebSocketFrame frame) throws IOException {
      this.writeFrame0(frame);
      this.writeFrame1(frame);
      this.writeFrameExtendedPayloadLength(frame);
      byte[] maskingKey = Misc.nextBytes(4);
      this.write((byte[])maskingKey);
      this.writeFramePayload(frame, maskingKey);
   }

   private void writeFrame0(WebSocketFrame frame) throws IOException {
      int b = (frame.getFin() ? 128 : 0) | (frame.getRsv1() ? 64 : 0) | (frame.getRsv2() ? 32 : 0) | (frame.getRsv3() ? 16 : 0) | frame.getOpcode() & 15;
      this.write(b);
   }

   private void writeFrame1(WebSocketFrame frame) throws IOException {
      int b = 128;
      int len = frame.getPayloadLength();
      if (len <= 125) {
         b |= len;
      } else if (len <= 65535) {
         b |= 126;
      } else {
         b |= 127;
      }

      this.write(b);
   }

   private void writeFrameExtendedPayloadLength(WebSocketFrame frame) throws IOException {
      int len = frame.getPayloadLength();
      if (len > 125) {
         byte[] buf;
         if (len <= 65535) {
            buf = new byte[]{(byte)(len >> 8 & 255), (byte)(len & 255)};
         } else {
            buf = new byte[8];

            for(int i = 7; i >= 0; --i) {
               buf[i] = (byte)(len & 255);
               len >>>= 8;
            }
         }

         this.write((byte[])buf);
      }
   }

   private void writeFramePayload(WebSocketFrame frame, byte[] maskingKey) throws IOException {
      byte[] payload = frame.getPayload();
      if (payload != null) {
         byte[] masked = new byte[payload.length];

         for(int i = 0; i < payload.length; ++i) {
            masked[i] = (byte)((payload[i] ^ maskingKey[i % 4]) & 255);
         }

         this.write((byte[])masked);
      }
   }
}
