package net.dv8tion.jda.internal.audio;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.dv8tion.jda.internal.utils.ResizingByteBuffer;
import org.slf4j.Logger;

public class AudioPacket {
   public static final byte RTP_VERSION_PAD_EXTEND = -128;
   public static final byte RTP_PAYLOAD_TYPE = 120;
   private static final int RTP_HEADER_SIZE = 12;
   private static final Logger log = JDALogger.getLog(AudioPacket.class);
   private final byte type;
   private final char seq;
   private final int timestamp;
   private final int ssrc;
   private final short extensionLength;
   private final boolean hasExtension;
   private final int[] csrc;
   private final ByteBuffer encodedAudio;

   public AudioPacket(DatagramPacket packet) {
      this(ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength()));
   }

   public AudioPacket(byte[] rawPacket) {
      this(ByteBuffer.wrap(rawPacket));
   }

   public AudioPacket(ByteBuffer buffer) {
      byte first = buffer.get();
      this.hasExtension = (first & 16) != 0;
      int cc = first & 15;
      this.type = buffer.get();
      this.seq = buffer.getChar();
      this.timestamp = buffer.getInt();
      this.ssrc = buffer.getInt();
      this.csrc = new int[cc];

      for(int i = 0; i < cc; ++i) {
         this.csrc[i] = buffer.getInt();
      }

      if (this.hasExtension) {
         this.extensionLength = (short)buffer.getInt();
      } else {
         this.extensionLength = 0;
      }

      this.encodedAudio = buffer;
   }

   public AudioPacket(char seq, int timestamp, int ssrc, ByteBuffer encodedAudio) {
      this.seq = seq;
      this.ssrc = ssrc;
      this.timestamp = timestamp;
      this.csrc = new int[0];
      this.extensionLength = 0;
      this.hasExtension = false;
      this.type = 120;
      this.encodedAudio = encodedAudio;
   }

   public ByteBuffer getEncodedAudio() {
      return this.encodedAudio;
   }

   public char getSequence() {
      return this.seq;
   }

   public int getSSRC() {
      return this.ssrc;
   }

   public int getTimestamp() {
      return this.timestamp;
   }

   public void asEncryptedPacket(CryptoAdapter crypto, ResizingByteBuffer buffer) {
      buffer.prepareWrite(12);
      writeHeader(this.seq, this.timestamp, this.ssrc, buffer.buffer());
      crypto.encrypt(buffer, this.encodedAudio);
   }

   @Nullable
   public AudioPacket asDecryptAudioPacket(CryptoAdapter crypto, long userId, ResizingByteBuffer decryptBuffer) {
      if (this.type != 120) {
         return null;
      } else {
         boolean success = crypto.decrypt(this.extensionLength, userId, this.encodedAudio, decryptBuffer);
         if (!success) {
            log.warn((String)"Failed to decrypt audio packet for user {}", (Object)userId);
            return null;
         } else {
            return new AudioPacket(this.seq, this.timestamp, this.ssrc, decryptBuffer.buffer());
         }
      }
   }

   private static void writeHeader(char seq, int timestamp, int ssrc, ByteBuffer buffer) {
      buffer.put((byte)-128);
      buffer.put((byte)120);
      buffer.putChar(seq);
      buffer.putInt(timestamp);
      buffer.putInt(ssrc);
   }
}
