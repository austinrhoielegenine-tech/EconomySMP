package net.dv8tion.jda.api.audio;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioFormat;
import net.dv8tion.jda.api.entities.User;

public interface AudioReceiveHandler {
   AudioFormat OUTPUT_FORMAT = new AudioFormat(48000.0F, 16, 2, true, true);

   default boolean canReceiveCombined() {
      return false;
   }

   default boolean canReceiveUser() {
      return false;
   }

   default boolean canReceiveEncoded() {
      return false;
   }

   default void handleEncodedAudio(@Nonnull OpusPacket packet) {
   }

   default void handleCombinedAudio(@Nonnull CombinedAudio combinedAudio) {
   }

   default void handleUserAudio(@Nonnull UserAudio userAudio) {
   }

   default boolean includeUserInCombinedAudio(@Nonnull User user) {
      return true;
   }
}
