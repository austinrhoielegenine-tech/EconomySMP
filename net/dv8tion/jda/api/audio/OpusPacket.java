package net.dv8tion.jda.api.audio;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.internal.audio.AudioPacket;
import net.dv8tion.jda.internal.audio.Decoder;

public final class OpusPacket implements Comparable<OpusPacket> {
   public static final int OPUS_SAMPLE_RATE = 48000;
   public static final int OPUS_FRAME_SIZE = 960;
   public static final int OPUS_FRAME_TIME_AMOUNT = 20;
   public static final int OPUS_CHANNEL_COUNT = 2;
   private final long userId;
   private final byte[] opusAudio;
   private final Decoder decoder;
   private final AudioPacket rawPacket;
   private short[] decoded;
   private boolean triedDecode;

   public OpusPacket(@Nonnull AudioPacket packet, long userId, @Nullable Decoder decoder) {
      this.rawPacket = packet;
      this.userId = userId;
      this.decoder = decoder;
      this.opusAudio = new byte[packet.getEncodedAudio().remaining()];
      packet.getEncodedAudio().slice().get(this.opusAudio);
   }

   public char getSequence() {
      return this.rawPacket.getSequence();
   }

   public int getTimestamp() {
      return this.rawPacket.getTimestamp();
   }

   public int getSSRC() {
      return this.rawPacket.getSSRC();
   }

   public long getUserId() {
      return this.userId;
   }

   public boolean canDecode() {
      return this.decoder != null && this.decoder.isInOrder(this.getSequence());
   }

   @Nonnull
   public byte[] getOpusAudio() {
      return Arrays.copyOf(this.opusAudio, this.opusAudio.length);
   }

   @Nullable
   public synchronized short[] decode() {
      if (this.triedDecode) {
         return this.decoded;
      } else if (this.decoder == null) {
         throw new IllegalStateException("No decoder available");
      } else if (!this.decoder.isInOrder(this.getSequence())) {
         throw new IllegalStateException("Packet is not in order");
      } else {
         this.triedDecode = true;
         return this.decoded = this.decoder.decodeFromOpus(this.rawPacket);
      }
   }

   @Nonnull
   public byte[] getAudioData(double volume) {
      return getAudioData(this.decode(), volume);
   }

   @Nonnull
   public static byte[] getAudioData(@Nonnull short[] decoded, double volume) {
      if (decoded == null) {
         throw new IllegalArgumentException("Cannot get audio data from null");
      } else {
         int byteIndex = 0;
         byte[] audio = new byte[decoded.length * 2];

         for(short s : decoded) {
            if (volume != (double)1.0F) {
               s = (short)((int)((double)s * volume));
            }

            byte leftByte = (byte)(s >>> 8 & 255);
            byte rightByte = (byte)(s & 255);
            audio[byteIndex] = leftByte;
            audio[byteIndex + 1] = rightByte;
            byteIndex += 2;
         }

         return audio;
      }
   }

   public int compareTo(@Nonnull OpusPacket o) {
      return this.getSequence() - o.getSequence();
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.getSequence(), this.getTimestamp(), Arrays.hashCode(this.getOpusAudio())});
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof OpusPacket)) {
         return false;
      } else {
         OpusPacket other = (OpusPacket)obj;
         return this.getSequence() == other.getSequence() && this.getTimestamp() == other.getTimestamp() && this.getSSRC() == other.getSSRC();
      }
   }
}
