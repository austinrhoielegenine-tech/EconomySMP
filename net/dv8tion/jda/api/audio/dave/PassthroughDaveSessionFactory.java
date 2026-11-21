package net.dv8tion.jda.api.audio.dave;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public class PassthroughDaveSessionFactory implements DaveSessionFactory {
   private static final Logger LOG = JDALogger.getLog(DaveSessionFactory.class);
   private static final LocalDate daveDeadline = LocalDate.of(2026, 3, 1);
   private static boolean isWarningPrinted = false;

   private static void printWarning() {
      if (!isWarningPrinted) {
         isWarningPrinted = true;
         LOG.warn((String)"Using passthrough dave session. Please migrate to an implementation of libdave! Your audio connections will stop working on {}.\nRead more: https://jda.wiki/using-jda/troubleshooting/#using-passthrough-dave-session-please-migrate-to-an-implementation-of-libdave", (Object)daveDeadline, (Object)(new IllegalStateException()));
      }
   }

   @Nonnull
   public DaveSession createDaveSession(@Nonnull DaveProtocolCallbacks callbacks, long userId, long channelId) {
      printWarning();
      return new PassthroughDaveSession();
   }

   private static class PassthroughDaveSession implements DaveSession {
      private PassthroughDaveSession() {
      }

      public int getMaxProtocolVersion() {
         return 0;
      }

      public int getMaxEncryptedFrameSize(@Nonnull DaveSession.MediaType type, int frameSize) {
         return frameSize;
      }

      public int getMaxDecryptedFrameSize(@Nonnull DaveSession.MediaType type, long userId, int frameSize) {
         return frameSize;
      }

      public void assignSsrcToCodec(@Nonnull DaveSession.Codec codec, int ssrc) {
      }

      public boolean encrypt(@Nonnull DaveSession.MediaType mediaType, int ssrc, @Nonnull ByteBuffer audio, @Nonnull ByteBuffer encrypted) {
         encrypted.put(audio);
         encrypted.flip();
         return true;
      }

      public boolean decrypt(@Nonnull DaveSession.MediaType mediaType, long userId, @Nonnull ByteBuffer encrypted, @Nonnull ByteBuffer decrypted) {
         decrypted.put(encrypted);
         decrypted.flip();
         return true;
      }

      public void addUser(long userId) {
      }

      public void removeUser(long userId) {
      }

      public void initialize() {
      }

      public void destroy() {
      }

      public void onSelectProtocolAck(int protocolVersion) {
      }

      public void onDaveProtocolPrepareTransition(int transitionId, int protocolVersion) {
      }

      public void onDaveProtocolExecuteTransition(int transitionId) {
      }

      public void onDaveProtocolPrepareEpoch(long epoch, int protocolVersion) {
      }

      public void onDaveProtocolMLSExternalSenderPackage(@Nonnull ByteBuffer externalSenderPackage) {
      }

      public void onMLSProposals(@Nonnull ByteBuffer proposals) {
      }

      public void onMLSPrepareCommitTransition(int transitionId, @Nonnull ByteBuffer commit) {
      }

      public void onMLSWelcome(int transitionId, @Nonnull ByteBuffer welcome) {
      }
   }
}
