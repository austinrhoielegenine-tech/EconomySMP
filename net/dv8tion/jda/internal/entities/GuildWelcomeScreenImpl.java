package net.dv8tion.jda.internal.entities;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildWelcomeScreen;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.managers.GuildWelcomeScreenManager;
import net.dv8tion.jda.api.utils.data.DataObject;

public class GuildWelcomeScreenImpl implements GuildWelcomeScreen {
   private final Guild guild;
   private final String description;
   private final List<GuildWelcomeScreen.Channel> channels;

   public GuildWelcomeScreenImpl(@Nullable Guild guild, @Nullable String description, @Nonnull List<GuildWelcomeScreen.Channel> channels) {
      this.guild = guild;
      this.description = description;
      this.channels = channels;
   }

   @Nullable
   public Guild getGuild() {
      return this.guild;
   }

   @Nonnull
   public GuildWelcomeScreenManager getManager() {
      if (this.guild == null) {
         throw new IllegalStateException("Cannot modify a guild welcome screen from an Invite");
      } else {
         return this.guild.modifyWelcomeScreen();
      }
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Nonnull
   public List<GuildWelcomeScreen.Channel> getChannels() {
      return this.channels;
   }

   public static class ChannelImpl implements GuildWelcomeScreen.Channel {
      private final Guild guild;
      private final long id;
      private final String description;
      private final EmojiUnion emoji;

      public ChannelImpl(@Nullable Guild guild, long id, @Nonnull String description, @Nullable EmojiUnion emoji) {
         this.guild = guild;
         this.id = id;
         this.description = description;
         this.emoji = emoji;
      }

      @Nullable
      public Guild getGuild() {
         return this.guild;
      }

      public long getIdLong() {
         return this.id;
      }

      @Nullable
      public GuildChannel getChannel() {
         return this.guild == null ? null : this.guild.getGuildChannelById(this.id);
      }

      @Nonnull
      public String getDescription() {
         return this.description;
      }

      @Nullable
      public EmojiUnion getEmoji() {
         return this.emoji;
      }

      @Nonnull
      public DataObject toData() {
         DataObject data = DataObject.empty();
         data.put("channel_id", this.id);
         data.put("description", this.description);
         if (this.emoji != null) {
            if (this.emoji.getType() == Emoji.Type.CUSTOM) {
               data.put("emoji_id", ((CustomEmoji)this.emoji).getId());
            }

            data.put("emoji_name", this.emoji.getName());
         }

         return data;
      }
   }
}
