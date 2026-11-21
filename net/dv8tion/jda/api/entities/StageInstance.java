package net.dv8tion.jda.api.entities;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.managers.StageInstanceManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface StageInstance extends ISnowflake {
   @Nonnull
   Guild getGuild();

   @Nonnull
   StageChannel getChannel();

   @Nonnull
   String getTopic();

   @Nonnull
   PrivacyLevel getPrivacyLevel();

   @Nonnull
   default @Unmodifiable List<Member> getSpeakers() {
      return (List)this.getChannel().getMembers().stream().filter((member) -> !member.getVoiceState().isSuppressed()).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   default @Unmodifiable List<Member> getAudience() {
      return (List)this.getChannel().getMembers().stream().filter((member) -> member.getVoiceState().isSuppressed()).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   @CheckReturnValue
   RestAction<Void> delete();

   @Nonnull
   @CheckReturnValue
   StageInstanceManager getManager();

   public static enum PrivacyLevel {
      UNKNOWN(-1),
      GUILD_ONLY(2);

      private final int key;

      private PrivacyLevel(int key) {
         this.key = key;
      }

      public int getKey() {
         return this.key;
      }

      @Nonnull
      public static PrivacyLevel fromKey(int key) {
         for(PrivacyLevel level : values()) {
            if (level.key == key) {
               return level;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static PrivacyLevel[] $values() {
         return new PrivacyLevel[]{UNKNOWN, GUILD_ONLY};
      }
   }
}
