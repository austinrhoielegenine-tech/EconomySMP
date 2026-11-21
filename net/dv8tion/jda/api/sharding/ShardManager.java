package net.dv8tion.jda.api.sharding;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.ChannelCacheView;
import net.dv8tion.jda.api.utils.cache.ShardCacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.cache.UnifiedChannelCacheView;
import org.jetbrains.annotations.Unmodifiable;

public interface ShardManager extends IGuildChannelContainer<Channel> {
   default void addEventListener(@Nonnull Object... listeners) {
      Checks.noneNull(listeners, "listeners");
      this.getShardCache().forEach((jda) -> jda.addEventListener(listeners));
   }

   default void removeEventListener(@Nonnull Object... listeners) {
      Checks.noneNull(listeners, "listeners");
      this.getShardCache().forEach((jda) -> jda.removeEventListener(listeners));
   }

   default void addEventListeners(@Nonnull IntFunction<Object> eventListenerProvider) {
      Checks.notNull(eventListenerProvider, "event listener provider");
      this.getShardCache().forEach((jda) -> {
         Object listener = eventListenerProvider.apply(jda.getShardInfo().getShardId());
         if (listener != null) {
            jda.addEventListener(listener);
         }

      });
   }

   default void removeEventListeners(@Nonnull IntFunction<Collection<Object>> eventListenerProvider) {
      Checks.notNull(eventListenerProvider, "event listener provider");
      this.getShardCache().forEach((jda) -> jda.removeEventListener(eventListenerProvider.apply(jda.getShardInfo().getShardId())));
   }

   default void removeEventListenerProvider(@Nonnull IntFunction<Object> eventListenerProvider) {
   }

   int getShardsQueued();

   default int getShardsRunning() {
      return (int)this.getShardCache().size();
   }

   default int getShardsTotal() {
      return this.getShardsQueued() + this.getShardsRunning();
   }

   @Nonnull
   default EnumSet<GatewayIntent> getGatewayIntents() {
      return (EnumSet)this.getShardCache().applyStream((stream) -> (EnumSet)stream.map(JDA::getGatewayIntents).findAny().orElse(EnumSet.noneOf(GatewayIntent.class)));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<ApplicationInfo> retrieveApplicationInfo() {
      return ((JDA)this.getShardCache().stream().findAny().orElseThrow(() -> new IllegalStateException("no active shards"))).retrieveApplicationInfo();
   }

   default double getAverageGatewayPing() {
      return this.getShardCache().stream().mapToLong(JDA::getGatewayPing).filter((ping) -> ping != -1L).average().orElse((double)-1.0F);
   }

   @Nonnull
   default SnowflakeCacheView<Category> getCategoryCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(IGuildChannelContainer::getCategoryCache)));
   }

   @Nullable
   default RichCustomEmoji getEmojiById(long id) {
      return (RichCustomEmoji)this.getEmojiCache().getElementById(id);
   }

   @Nullable
   default RichCustomEmoji getEmojiById(@Nonnull String id) {
      return (RichCustomEmoji)this.getEmojiCache().getElementById(id);
   }

   @Nonnull
   default SnowflakeCacheView<RichCustomEmoji> getEmojiCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(JDA::getEmojiCache)));
   }

   @Nonnull
   default @Unmodifiable List<RichCustomEmoji> getEmojis() {
      return this.getEmojiCache().asList();
   }

   @Nonnull
   default @Unmodifiable List<RichCustomEmoji> getEmojisByName(@Nonnull String name, boolean ignoreCase) {
      return this.getEmojiCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default Guild getGuildById(long id) {
      return (Guild)this.getGuildCache().getElementById(id);
   }

   @Nullable
   default Guild getGuildById(@Nonnull String id) {
      return this.getGuildById(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   default @Unmodifiable List<Guild> getGuildsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getGuildCache().getElementsByName(name, ignoreCase);
   }

   @Nonnull
   default SnowflakeCacheView<Guild> getGuildCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(JDA::getGuildCache)));
   }

   @Nonnull
   default @Unmodifiable List<Guild> getGuilds() {
      return this.getGuildCache().asList();
   }

   @Nonnull
   default @Unmodifiable List<Guild> getMutualGuilds(@Nonnull Collection<? extends UserSnowflake> users) {
      Checks.noneNull(users, "users");
      return (List)this.getGuildCache().stream().filter((guild) -> {
         Stream var10000 = users.stream();
         Objects.requireNonNull(guild);
         return var10000.allMatch(guild::isMember);
      }).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   default @Unmodifiable List<Guild> getMutualGuilds(@Nonnull UserSnowflake... users) {
      Checks.notNull(users, "users");
      return this.getMutualGuilds(Arrays.asList(users));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<User> retrieveUserById(@Nonnull String id) {
      return this.retrieveUserById(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<User> retrieveUserById(long id) {
      JDA api = null;

      for(JDA shard : this.getShardCache()) {
         api = shard;
         EnumSet<GatewayIntent> intents = shard.getGatewayIntents();
         User user = shard.getUserById(id);
         boolean isUpdated = intents.contains(GatewayIntent.GUILD_PRESENCES) || intents.contains(GatewayIntent.GUILD_MEMBERS);
         if (user != null && isUpdated) {
            return new CompletedRestAction<User>(shard, user);
         }
      }

      if (api == null) {
         throw new IllegalStateException("no shards active");
      } else {
         JDAImpl jda = (JDAImpl)api;
         Route.CompiledRoute route = Route.Users.GET_USER.compile(Long.toUnsignedString(id));
         return new RestActionImpl<User>(jda, route, (response, request) -> jda.getEntityBuilder().createUser(response.getObject()));
      }
   }

   @Nullable
   default User getUserByTag(@Nonnull String tag) {
      return (User)this.getShardCache().applyStream((stream) -> (User)stream.map((jda) -> jda.getUserByTag(tag)).filter(Objects::nonNull).findFirst().orElse((Object)null));
   }

   @Nullable
   default User getUserByTag(@Nonnull String username, @Nonnull String discriminator) {
      return (User)this.getShardCache().applyStream((stream) -> (User)stream.map((jda) -> jda.getUserByTag(username, discriminator)).filter(Objects::nonNull).findFirst().orElse((Object)null));
   }

   @Nonnull
   default @Unmodifiable List<PrivateChannel> getPrivateChannels() {
      return this.getPrivateChannelCache().asList();
   }

   @Nullable
   default Role getRoleById(long id) {
      return (Role)this.getRoleCache().getElementById(id);
   }

   @Nullable
   default Role getRoleById(@Nonnull String id) {
      return (Role)this.getRoleCache().getElementById(id);
   }

   @Nonnull
   default SnowflakeCacheView<Role> getRoleCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(JDA::getRoleCache)));
   }

   @Nonnull
   default @Unmodifiable List<Role> getRoles() {
      return this.getRoleCache().asList();
   }

   @Nonnull
   default @Unmodifiable List<Role> getRolesByName(@Nonnull String name, boolean ignoreCase) {
      return this.getRoleCache().getElementsByName(name, ignoreCase);
   }

   @Nullable
   default PrivateChannel getPrivateChannelById(long id) {
      return (PrivateChannel)this.getPrivateChannelCache().getElementById(id);
   }

   @Nullable
   default PrivateChannel getPrivateChannelById(@Nonnull String id) {
      return (PrivateChannel)this.getPrivateChannelCache().getElementById(id);
   }

   @Nonnull
   default SnowflakeCacheView<PrivateChannel> getPrivateChannelCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(JDA::getPrivateChannelCache)));
   }

   @Nullable
   default GuildChannel getGuildChannelById(long id) {
      for(JDA shard : this.getShards()) {
         GuildChannel channel = shard.getGuildChannelById(id);
         if (channel != null) {
            return channel;
         }
      }

      return null;
   }

   @Nullable
   default GuildChannel getGuildChannelById(@Nonnull ChannelType type, long id) {
      Checks.notNull(type, "ChannelType");

      for(JDA shard : this.getShards()) {
         GuildChannel channel = shard.getGuildChannelById(type, id);
         if (channel != null) {
            return channel;
         }
      }

      return null;
   }

   @Nonnull
   default SnowflakeCacheView<TextChannel> getTextChannelCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(IGuildChannelContainer::getTextChannelCache)));
   }

   @Nonnull
   default SnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(IGuildChannelContainer::getVoiceChannelCache)));
   }

   @Nonnull
   default SnowflakeCacheView<StageChannel> getStageChannelCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(IGuildChannelContainer::getStageChannelCache)));
   }

   @Nonnull
   default SnowflakeCacheView<ThreadChannel> getThreadChannelCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(IGuildChannelContainer::getThreadChannelCache)));
   }

   @Nonnull
   default SnowflakeCacheView<NewsChannel> getNewsChannelCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(IGuildChannelContainer::getNewsChannelCache)));
   }

   @Nonnull
   default SnowflakeCacheView<ForumChannel> getForumChannelCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(IGuildChannelContainer::getForumChannelCache)));
   }

   @Nonnull
   default SnowflakeCacheView<MediaChannel> getMediaChannelCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(IGuildChannelContainer::getMediaChannelCache)));
   }

   @Nonnull
   default ChannelCacheView<Channel> getChannelCache() {
      return new UnifiedChannelCacheView<Channel>(() -> this.getShardCache().stream().map(IGuildChannelContainer::getChannelCache));
   }

   @Nullable
   default JDA getShardById(int id) {
      return this.getShardCache().getElementById(id);
   }

   @Nullable
   default JDA getShardById(@Nonnull String id) {
      return this.getShardCache().getElementById(id);
   }

   @Nonnull
   ShardCacheView getShardCache();

   @Nonnull
   default @Unmodifiable List<JDA> getShards() {
      return this.getShardCache().asList();
   }

   @Nullable
   default JDA.Status getStatus(int shardId) {
      JDA jda = this.getShardCache().getElementById(shardId);
      return jda == null ? null : jda.getStatus();
   }

   @Nonnull
   default @Unmodifiable Map<JDA, JDA.Status> getStatuses() {
      return Collections.unmodifiableMap((Map)this.getShardCache().stream().collect(Collectors.toMap(Function.identity(), JDA::getStatus)));
   }

   @Nullable
   default User getUserById(long id) {
      return (User)this.getUserCache().getElementById(id);
   }

   @Nullable
   default User getUserById(@Nonnull String id) {
      return (User)this.getUserCache().getElementById(id);
   }

   @Nonnull
   default SnowflakeCacheView<User> getUserCache() {
      return CacheView.allSnowflakes((Supplier)(() -> this.getShardCache().stream().map(JDA::getUserCache)));
   }

   @Nonnull
   default @Unmodifiable List<User> getUsers() {
      return this.getUserCache().asList();
   }

   void restart();

   void restart(int var1);

   default void setActivity(@Nullable Activity activity) {
      this.setActivityProvider((id) -> activity);
   }

   default void setActivityProvider(@Nullable IntFunction<? extends Activity> activityProvider) {
      this.getShardCache().forEach((jda) -> jda.getPresence().setActivity(activityProvider == null ? null : (Activity)activityProvider.apply(jda.getShardInfo().getShardId())));
   }

   default void setIdle(boolean idle) {
      this.setIdleProvider((id) -> idle);
   }

   default void setIdleProvider(@Nonnull IntFunction<Boolean> idleProvider) {
      this.getShardCache().forEach((jda) -> jda.getPresence().setIdle((Boolean)idleProvider.apply(jda.getShardInfo().getShardId())));
   }

   default void setPresence(@Nullable OnlineStatus status, @Nullable Activity activity) {
      this.setPresenceProvider((id) -> status, (id) -> activity);
   }

   default void setPresenceProvider(@Nullable IntFunction<OnlineStatus> statusProvider, @Nullable IntFunction<? extends Activity> activityProvider) {
      this.getShardCache().forEach((jda) -> jda.getPresence().setPresence(statusProvider == null ? null : (OnlineStatus)statusProvider.apply(jda.getShardInfo().getShardId()), activityProvider == null ? null : (Activity)activityProvider.apply(jda.getShardInfo().getShardId())));
   }

   default void setStatus(@Nullable OnlineStatus status) {
      this.setStatusProvider((id) -> status);
   }

   default void setStatusProvider(@Nullable IntFunction<OnlineStatus> statusProvider) {
      this.getShardCache().forEach((jda) -> jda.getPresence().setStatus(statusProvider == null ? null : (OnlineStatus)statusProvider.apply(jda.getShardInfo().getShardId())));
   }

   void shutdown();

   void shutdown(int var1);

   void start(int var1);

   void login();
}
