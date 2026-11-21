package net.dv8tion.jda.api.audio.dave;

import java.nio.ByteBuffer;
import javax.annotation.Nonnull;

public interface DaveSession {
   int getMaxProtocolVersion();

   int getMaxEncryptedFrameSize(@Nonnull MediaType var1, int var2);

   int getMaxDecryptedFrameSize(@Nonnull MediaType var1, long var2, int var4);

   void assignSsrcToCodec(@Nonnull Codec var1, int var2);

   boolean encrypt(@Nonnull MediaType var1, int var2, @Nonnull ByteBuffer var3, @Nonnull ByteBuffer var4);

   boolean decrypt(@Nonnull MediaType var1, long var2, @Nonnull ByteBuffer var4, @Nonnull ByteBuffer var5);

   void addUser(long var1);

   void removeUser(long var1);

   void initialize();

   void destroy();

   void onSelectProtocolAck(int var1);

   void onDaveProtocolPrepareTransition(int var1, int var2);

   void onDaveProtocolExecuteTransition(int var1);

   void onDaveProtocolPrepareEpoch(long var1, int var3);

   void onDaveProtocolMLSExternalSenderPackage(@Nonnull ByteBuffer var1);

   void onMLSProposals(@Nonnull ByteBuffer var1);

   void onMLSPrepareCommitTransition(int var1, @Nonnull ByteBuffer var2);

   void onMLSWelcome(int var1, @Nonnull ByteBuffer var2);

   public static enum MediaType {
      AUDIO;

      // $FF: synthetic method
      private static MediaType[] $values() {
         return new MediaType[]{AUDIO};
      }
   }

   public static enum Codec {
      OPUS;

      // $FF: synthetic method
      private static Codec[] $values() {
         return new Codec[]{OPUS};
      }
   }
}
