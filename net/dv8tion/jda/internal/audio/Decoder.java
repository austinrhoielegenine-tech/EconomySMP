package net.dv8tion.jda.internal.audio;

import com.sun.jna.ptr.PointerByReference;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import tomp2p.opuswrapper.Opus;

public class Decoder {
   protected int ssrc;
   protected char lastSeq;
   protected int lastTimestamp;
   protected PointerByReference opusDecoder;

   protected Decoder(int ssrc) {
      this.ssrc = ssrc;
      this.lastSeq = '\uffff';
      this.lastTimestamp = -1;
      IntBuffer error = IntBuffer.allocate(1);
      this.opusDecoder = Opus.INSTANCE.opus_decoder_create(48000, 2, error);
      if (error.get() != 0 && this.opusDecoder == null) {
         throw new IllegalStateException("Received error code from opus_decoder_create(...): " + error.get());
      }
   }

   public boolean isInOrder(char newSeq) {
      return this.lastSeq == '\uffff' || newSeq > this.lastSeq || this.lastSeq - newSeq > 10;
   }

   public boolean wasPacketLost(char newSeq) {
      return newSeq > this.lastSeq + 1;
   }

   public short[] decodeFromOpus(AudioPacket decryptedPacket) {
      ShortBuffer decoded = ShortBuffer.allocate(4096);
      int result;
      if (decryptedPacket == null) {
         result = Opus.INSTANCE.opus_decode(this.opusDecoder, (byte[])null, 0, decoded, 960, 0);
         this.lastSeq = '\uffff';
         this.lastTimestamp = -1;
      } else {
         this.lastSeq = decryptedPacket.getSequence();
         this.lastTimestamp = decryptedPacket.getTimestamp();
         ByteBuffer encodedAudio = decryptedPacket.getEncodedAudio();
         int length = encodedAudio.remaining();
         byte[] buf = new byte[length];
         encodedAudio.slice().get(buf);
         result = Opus.INSTANCE.opus_decode(this.opusDecoder, buf, buf.length, decoded, 960, 0);
      }

      if (result < 0) {
         this.handleDecodeError(result);
         return null;
      } else {
         short[] audio = new short[result * 2];
         decoded.get(audio);
         return audio;
      }
   }

   private void handleDecodeError(int result) {
      StringBuilder b = new StringBuilder("Decoder failed to decode audio from user with code ");
      switch (result) {
         case -7:
            b.append("OPUS_ALLOC_FAIL");
            break;
         case -6:
            b.append("OPUS_INVALID_STATE");
            break;
         case -5:
            b.append("OPUS_UNIMPLEMENTED");
            break;
         case -4:
            b.append("OPUS_INVALID_PACKET");
            break;
         case -3:
            b.append("OPUS_INTERNAL_ERROR");
            break;
         case -2:
            b.append("OPUS_BUFFER_TOO_SMALL");
            break;
         case -1:
            b.append("OPUS_BAD_ARG");
            break;
         default:
            b.append(result);
      }

      AudioConnection.LOG.debug((String)"{}", (Object)b);
   }

   protected synchronized void close() {
      if (this.opusDecoder != null) {
         Opus.INSTANCE.opus_decoder_destroy(this.opusDecoder);
         this.opusDecoder = null;
      }

   }

   protected void finalize() throws Throwable {
      super.finalize();
      this.close();
   }
}
