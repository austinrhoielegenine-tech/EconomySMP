package net.dv8tion.jda.api.entities.channel.concrete;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.ICopyableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IMemberContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IPositionableChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.managers.channel.concrete.CategoryManager;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.order.CategoryOrderAction;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.cache.SortedChannelCacheView;
import net.dv8tion.jda.api.utils.cache.SortedSnowflakeCacheView;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface Category extends GuildChannel, ICopyableChannel, IPositionableChannel, IPermissionContainer, IMemberContainer {
   @Nonnull
   default @Unmodifiable List<GuildChannel> getChannels() {
      SortedChannelCacheView<ICategorizableChannel> filtered = this.getGuild().getChannelCache().ofType(ICategorizableChannel.class);
      return (List)filtered.applyStream((stream) -> (List)stream.filter((it) -> this.equals(it.getParentCategory())).sorted().collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   default @Unmodifiable List<TextChannel> getTextChannels() {
      SortedSnowflakeCacheView<TextChannel> filtered = this.getGuild().getTextChannelCache();
      return (List)filtered.applyStream((stream) -> (List)stream.filter((channel) -> this.equals(channel.getParentCategory())).sorted().collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   default @Unmodifiable List<NewsChannel> getNewsChannels() {
      SortedSnowflakeCacheView<NewsChannel> filtered = this.getGuild().getNewsChannelCache();
      return (List)filtered.applyStream((stream) -> (List)stream.filter((channel) -> this.equals(channel.getParentCategory())).sorted().collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   default @Unmodifiable List<ForumChannel> getForumChannels() {
      SortedSnowflakeCacheView<ForumChannel> filtered = this.getGuild().getForumChannelCache();
      return (List)filtered.applyStream((stream) -> (List)stream.filter((channel) -> this.equals(channel.getParentCategory())).sorted().collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   default @Unmodifiable List<MediaChannel> getMediaChannels() {
      SnowflakeCacheView<MediaChannel> filtered = this.getGuild().getMediaChannelCache();
      return (List)filtered.applyStream((stream) -> (List)stream.filter((channel) -> this.equals(channel.getParentCategory())).sorted().collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   default @Unmodifiable List<VoiceChannel> getVoiceChannels() {
      SortedSnowflakeCacheView<VoiceChannel> filtered = this.getGuild().getVoiceChannelCache();
      return (List)filtered.applyStream((stream) -> (List)stream.filter((channel) -> this.equals(channel.getParentCategory())).sorted().collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   default @Unmodifiable List<StageChannel> getStageChannels() {
      SortedSnowflakeCacheView<StageChannel> filtered = this.getGuild().getStageChannelCache();
      return (List)filtered.applyStream((stream) -> (List)stream.filter((channel) -> this.equals(channel.getParentCategory())).sorted().collect(Helpers.toUnmodifiableList()));
   }

   @Nonnull
   @CheckReturnValue
   ChannelAction<TextChannel> createTextChannel(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   ChannelAction<NewsChannel> createNewsChannel(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   ChannelAction<VoiceChannel> createVoiceChannel(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   ChannelAction<StageChannel> createStageChannel(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   ChannelAction<ForumChannel> createForumChannel(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   ChannelAction<MediaChannel> createMediaChannel(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   CategoryOrderAction modifyTextChannelPositions();

   @Nonnull
   @CheckReturnValue
   CategoryOrderAction modifyVoiceChannelPositions();

   @Nonnull
   default @Unmodifiable List<Member> getMembers() {
      Stream var10000 = this.getChannels().stream();
      Objects.requireNonNull(IMemberContainer.class);
      var10000 = var10000.filter(IMemberContainer.class::isInstance);
      Objects.requireNonNull(IMemberContainer.class);
      return (List)var10000.map(IMemberContainer.class::cast).map(IMemberContainer::getMembers).flatMap(Collection::stream).distinct().collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   @CheckReturnValue
   ChannelAction<Category> createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   ChannelAction<Category> createCopy();

   @Nonnull
   @CheckReturnValue
   CategoryManager getManager();
}
