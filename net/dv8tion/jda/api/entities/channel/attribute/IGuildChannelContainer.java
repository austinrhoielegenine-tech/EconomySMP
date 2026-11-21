package net.dv8tion.jda.api.entities.channel.attribute;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.cache.ChannelCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Unmodifiable;

public interface IGuildChannelContainer<C extends Channel> {
   @Nonnull
   ChannelCacheView<C> getChannelCache();

   @Nullable
   default <T extends C> T getChannelById(@Nonnull Class<T> type, @Nonnull String id) {
      return (T)this.getChannelById(type, MiscUtil.parseSnowflake(id));
   }

   @Nullable
   default <T extends C> T getChannelById(@Nonnull Class<T> type, long id) {
      Checks.notNull(type, "Class");
      return (T)((Channel)this.getChannelCache().ofType(type).getElementById(id));
   }

   @Nullable
   default GuildChannel getGuildChannelById(@Nonnull String id) {
      return this.getGuildChannelById(MiscUtil.parseSnowflake(id));
   }

   @Nullable
   default GuildChannel getGuildChannelById(long id) {
      C channel = (C)(this.getChannelCache().getElementById(id));
      return channel instanceof GuildChannel ? (GuildChannel)channel : null;
   }

   @Nullable
   default GuildChannel getGuildChannelById(@Nonnull ChannelType type, @Nonnull String id) {
      return this.getGuildChannelById(type, MiscUtil.parseSnowflake(id));
   }

   @Nullable
   default GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id) {
      C channel = this.getChannelCache().getElementById(type, id);
      return channel instanceof GuildChannel ? (GuildChannel)channel : null;
   }

   @Nonnull
   SnowflakeCacheView<StageChannel> getStageChannelCache();

   @Nonnull
   default @Unmodifiable List<StageChannel> getStageChannelsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getStageChannelCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default StageChannel getStageChannelById(@Nonnull String id) {
      return (StageChannel)this.getChannelCache().getElementById(ChannelType.STAGE, id);
   }

   @Nullable
   default StageChannel getStageChannelById(long id) {
      return (StageChannel)this.getChannelCache().getElementById(ChannelType.STAGE, id);
   }

   @Nonnull
   default @Unmodifiable List<StageChannel> getStageChannels() {
      return this.getStageChannelCache().asList();
   }

   @Nonnull
   SnowflakeCacheView<ThreadChannel> getThreadChannelCache();

   @Nonnull
   default @Unmodifiable List<ThreadChannel> getThreadChannelsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getThreadChannelCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default ThreadChannel getThreadChannelById(@Nonnull String id) {
      return (ThreadChannel)this.getChannelCache().getElementById(ChannelType.GUILD_PUBLIC_THREAD, id);
   }

   @Nullable
   default ThreadChannel getThreadChannelById(long id) {
      return (ThreadChannel)this.getChannelCache().getElementById(ChannelType.GUILD_PUBLIC_THREAD, id);
   }

   @Nonnull
   default @Unmodifiable List<ThreadChannel> getThreadChannels() {
      return this.getThreadChannelCache().asList();
   }

   @Nonnull
   SnowflakeCacheView<Category> getCategoryCache();

   @Nonnull
   default @Unmodifiable List<Category> getCategoriesByName(@Nonnull String name, boolean ignoreCase) {
      return this.getCategoryCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default Category getCategoryById(@Nonnull String id) {
      return (Category)this.getChannelCache().getElementById(ChannelType.CATEGORY, id);
   }

   @Nullable
   default Category getCategoryById(long id) {
      return (Category)this.getChannelCache().getElementById(ChannelType.CATEGORY, id);
   }

   @Nonnull
   default @Unmodifiable List<Category> getCategories() {
      return this.getCategoryCache().asList();
   }

   @Nonnull
   SnowflakeCacheView<TextChannel> getTextChannelCache();

   @Nonnull
   default @Unmodifiable List<TextChannel> getTextChannelsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getTextChannelCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default TextChannel getTextChannelById(@Nonnull String id) {
      return (TextChannel)this.getChannelCache().getElementById(ChannelType.TEXT, id);
   }

   @Nullable
   default TextChannel getTextChannelById(long id) {
      return (TextChannel)this.getChannelCache().getElementById(ChannelType.TEXT, id);
   }

   @Nonnull
   default @Unmodifiable List<TextChannel> getTextChannels() {
      return this.getTextChannelCache().asList();
   }

   @Nonnull
   SnowflakeCacheView<NewsChannel> getNewsChannelCache();

   @Nonnull
   default @Unmodifiable List<NewsChannel> getNewsChannelsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getNewsChannelCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default NewsChannel getNewsChannelById(@Nonnull String id) {
      return (NewsChannel)this.getChannelCache().getElementById(ChannelType.NEWS, id);
   }

   @Nullable
   default NewsChannel getNewsChannelById(long id) {
      return (NewsChannel)this.getChannelCache().getElementById(ChannelType.NEWS, id);
   }

   @Nonnull
   default @Unmodifiable List<NewsChannel> getNewsChannels() {
      return this.getNewsChannelCache().asList();
   }

   @Nonnull
   SnowflakeCacheView<VoiceChannel> getVoiceChannelCache();

   @Nonnull
   default @Unmodifiable List<VoiceChannel> getVoiceChannelsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getVoiceChannelCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default VoiceChannel getVoiceChannelById(@Nonnull String id) {
      return (VoiceChannel)this.getChannelCache().getElementById(ChannelType.VOICE, id);
   }

   @Nullable
   default VoiceChannel getVoiceChannelById(long id) {
      return (VoiceChannel)this.getChannelCache().getElementById(ChannelType.VOICE, id);
   }

   @Nonnull
   default @Unmodifiable List<VoiceChannel> getVoiceChannels() {
      return this.getVoiceChannelCache().asList();
   }

   @Nonnull
   SnowflakeCacheView<ForumChannel> getForumChannelCache();

   @Nonnull
   default @Unmodifiable List<ForumChannel> getForumChannelsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getForumChannelCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default ForumChannel getForumChannelById(@Nonnull String id) {
      return (ForumChannel)this.getChannelCache().getElementById(ChannelType.FORUM, id);
   }

   @Nullable
   default ForumChannel getForumChannelById(long id) {
      return (ForumChannel)this.getChannelCache().getElementById(ChannelType.FORUM, id);
   }

   @Nonnull
   default @Unmodifiable List<ForumChannel> getForumChannels() {
      return this.getForumChannelCache().asList();
   }

   @Nonnull
   SnowflakeCacheView<MediaChannel> getMediaChannelCache();

   @Nonnull
   default @Unmodifiable List<MediaChannel> getMediaChannelsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getMediaChannelCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default MediaChannel getMediaChannelById(@Nonnull String id) {
      return (MediaChannel)this.getChannelCache().getElementById(ChannelType.MEDIA, id);
   }

   @Nullable
   default MediaChannel getMediaChannelById(long id) {
      return (MediaChannel)this.getChannelCache().getElementById(ChannelType.MEDIA, id);
   }

   @Nonnull
   default @Unmodifiable List<MediaChannel> getMediaChannels() {
      return this.getMediaChannelCache().asList();
   }
}
