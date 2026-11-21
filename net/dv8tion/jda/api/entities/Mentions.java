package net.dv8tion.jda.api.entities;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.interactions.commands.SlashCommandReference;
import org.apache.commons.collections4.Bag;
import org.jetbrains.annotations.Unmodifiable;

public interface Mentions {
   @Nonnull
   JDA getJDA();

   boolean mentionsEveryone();

   @Nonnull
   @Unmodifiable List<User> getUsers();

   @Nonnull
   Bag<User> getUsersBag();

   @Nonnull
   @Unmodifiable List<GuildChannel> getChannels();

   @Nonnull
   Bag<GuildChannel> getChannelsBag();

   @Nonnull
   <T extends GuildChannel> @Unmodifiable List<T> getChannels(@Nonnull Class<T> var1);

   @Nonnull
   <T extends GuildChannel> Bag<T> getChannelsBag(@Nonnull Class<T> var1);

   @Nonnull
   @Unmodifiable List<Role> getRoles();

   @Nonnull
   Bag<Role> getRolesBag();

   @Nonnull
   @Unmodifiable List<CustomEmoji> getCustomEmojis();

   @Nonnull
   Bag<CustomEmoji> getCustomEmojisBag();

   @Nonnull
   List<Member> getMembers();

   @Nonnull
   Bag<Member> getMembersBag();

   @Nonnull
   @Unmodifiable List<SlashCommandReference> getSlashCommands();

   @Nonnull
   Bag<SlashCommandReference> getSlashCommandsBag();

   @Nonnull
   @Unmodifiable List<IMentionable> getMentions(@Nonnull Message.MentionType... var1);

   boolean isMentioned(@Nonnull IMentionable var1, @Nonnull Message.MentionType... var2);
}
