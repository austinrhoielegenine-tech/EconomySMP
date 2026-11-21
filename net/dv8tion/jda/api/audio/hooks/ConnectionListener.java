package net.dv8tion.jda.api.audio.hooks;

import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.audio.SpeakingMode;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;

public interface ConnectionListener {
   default void onPing(long ping) {
   }

   default void onStatusChange(@Nonnull ConnectionStatus status) {
   }

   default void onUserSpeakingModeUpdate(@Nonnull User user, @Nonnull EnumSet<SpeakingMode> modes) {
   }

   default void onUserSpeakingModeUpdate(@Nonnull UserSnowflake user, @Nonnull EnumSet<SpeakingMode> modes) {
   }
}
