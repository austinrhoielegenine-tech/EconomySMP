package net.dv8tion.jda.api.entities;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.managers.GuildWelcomeScreenManager;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.entities.GuildWelcomeScreenImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface GuildWelcomeScreen {
   int MAX_DESCRIPTION_LENGTH = 140;
   int MAX_WELCOME_CHANNELS = 5;

   @Nullable
   Guild getGuild();

   @Nonnull
   @CheckReturnValue
   GuildWelcomeScreenManager getManager();

   @Nullable
   String getDescription();

   @Nonnull
   List<Channel> getChannels();

   public interface Channel extends ISnowflake, SerializableData {
      int MAX_DESCRIPTION_LENGTH = 42;

      @Nonnull
      static Channel of(@Nonnull StandardGuildChannel channel, @Nonnull String description) {
         return of(channel, description, (Emoji)null);
      }

      @Nonnull
      static Channel of(@Nonnull StandardGuildChannel channel, @Nonnull String description, @Nullable Emoji emoji) {
         Checks.notNull(channel, "Channel");
         Checks.notBlank(description, "Description");
         Checks.notLonger(description, 42, "Description");
         return new GuildWelcomeScreenImpl.ChannelImpl(channel.getGuild(), channel.getIdLong(), description, (EmojiUnion)emoji);
      }

      @Nullable
      Guild getGuild();

      long getIdLong();

      @Nullable
      GuildChannel getChannel();

      @Nonnull
      String getDescription();

      @Nullable
      EmojiUnion getEmoji();
   }
}
