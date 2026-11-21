package net.dv8tion.jda.api;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.annotations.Incubating;
import net.dv8tion.jda.api.entities.ApplicationInfo;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleConnectionMetadata;
import net.dv8tion.jda.api.entities.SKU;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.attribute.IGuildChannelContainer;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.sticker.StickerPack;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.entities.sticker.StickerUnion;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.IEventManager;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.managers.ApplicationManager;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.DirectAudioController;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.requests.restaction.CommandEditAction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.requests.restaction.TestEntitlementCreateAction;
import net.dv8tion.jda.api.requests.restaction.pagination.EntitlementPaginationAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.Once;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.cache.CacheView;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.Unmodifiable;

public interface JDA extends IGuildChannelContainer<Channel> {
   @Nonnull
   Status getStatus();

   @Nonnull
   EnumSet<GatewayIntent> getGatewayIntents();

   @Nonnull
   EnumSet<CacheFlag> getCacheFlags();

   boolean unloadUser(long var1);

   long getGatewayPing();

   @Nonnull
   @CheckReturnValue
   default RestAction<Long> getRestPing() {
      AtomicLong time = new AtomicLong();
      Route.CompiledRoute route = Route.Self.GET_SELF.compile();
      RestActionImpl<Long> action = new RestActionImpl<Long>(this, route, (response, request) -> System.currentTimeMillis() - time.get());
      action.setCheck(() -> {
         time.set(System.currentTimeMillis());
         return true;
      });
      return action;
   }

   @Nonnull
   default JDA awaitStatus(@Nonnull Status status) throws InterruptedException {
      return this.awaitStatus(status);
   }

   @Nonnull
   JDA awaitStatus(@Nonnull Status var1, @Nonnull Status... var2) throws InterruptedException;

   @Nonnull
   default JDA awaitReady() throws InterruptedException {
      return this.awaitStatus(JDA.Status.CONNECTED);
   }

   @CheckReturnValue
   boolean awaitShutdown(long var1, @Nonnull TimeUnit var3) throws InterruptedException;

   @CheckReturnValue
   default boolean awaitShutdown(@Nonnull Duration timeout) throws InterruptedException {
      Checks.notNull(timeout, "Timeout");
      return this.awaitShutdown(timeout.toMillis(), TimeUnit.MILLISECONDS);
   }

   default boolean awaitShutdown() throws InterruptedException {
      return this.awaitShutdown(0L, TimeUnit.MILLISECONDS);
   }

   int cancelRequests();

   @Nonnull
   ScheduledExecutorService getRateLimitPool();

   @Nonnull
   ScheduledExecutorService getGatewayPool();

   @Nonnull
   ExecutorService getCallbackPool();

   @Nonnull
   OkHttpClient getHttpClient();

   @Nonnull
   DirectAudioController getDirectAudioController();

   void setEventManager(@Nullable IEventManager var1);

   void addEventListener(@Nonnull Object... var1);

   void removeEventListener(@Nonnull Object... var1);

   @Nonnull
   List<Object> getRegisteredListeners();

   @Nonnull
   @CheckReturnValue
   <E extends GenericEvent> Once.Builder<E> listenOnce(@Nonnull Class<E> var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<List<Command>> retrieveCommands() {
      return this.retrieveCommands(false);
   }

   @Nonnull
   @CheckReturnValue
   RestAction<List<Command>> retrieveCommands(boolean var1);

   @Nonnull
   @CheckReturnValue
   RestAction<Command> retrieveCommandById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<Command> retrieveCommandById(long id) {
      return this.retrieveCommandById(Long.toUnsignedString(id));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<Command> upsertCommand(@Nonnull CommandData var1);

   @Nonnull
   @CheckReturnValue
   default CommandCreateAction upsertCommand(@Nonnull String name, @Nonnull String description) {
      return (CommandCreateAction)this.upsertCommand(new CommandDataImpl(name, description));
   }

   @Nonnull
   @CheckReturnValue
   CommandListUpdateAction updateCommands();

   @Nonnull
   @CheckReturnValue
   CommandEditAction editCommandById(@Nonnull Command.Type var1, @Nonnull String var2);

   @Nonnull
   @CheckReturnValue
   default CommandEditAction editCommandById(@Nonnull Command.Type type, long id) {
      return this.editCommandById(type, Long.toUnsignedString(id));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<Void> deleteCommandById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> deleteCommandById(long commandId) {
      return this.deleteCommandById(Long.toUnsignedString(commandId));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<List<RoleConnectionMetadata>> retrieveRoleConnectionMetadata();

   @Nonnull
   @CheckReturnValue
   RestAction<List<RoleConnectionMetadata>> updateRoleConnectionMetadata(@Nonnull Collection<? extends RoleConnectionMetadata> var1);

   @Nonnull
   CacheView<AudioManager> getAudioManagerCache();

   @Nonnull
   default @Unmodifiable List<AudioManager> getAudioManagers() {
      return this.getAudioManagerCache().asList();
   }

   @Nonnull
   SnowflakeCacheView<User> getUserCache();

   @Nonnull
   default @Unmodifiable List<User> getUsers() {
      return this.getUserCache().asList();
   }

   @Nullable
   default User getUserById(@Nonnull String id) {
      return (User)this.getUserCache().getElementById(id);
   }

   @Nullable
   default User getUserById(long id) {
      return (User)this.getUserCache().getElementById(id);
   }

   @Nullable
   default User getUserByTag(@Nonnull String tag) {
      Checks.notNull(tag, "Tag");
      Matcher matcher = User.USER_TAG.matcher(tag);
      Checks.check(matcher.matches(), "Invalid tag format!");
      String username = matcher.group(1);
      String discriminator = matcher.group(2);
      return this.getUserByTag(username, discriminator);
   }

   @Nullable
   default User getUserByTag(@Nonnull String username, @Nullable String discriminator) {
      Checks.inRange(username, 2, 32, "Username");
      Checks.check(discriminator == null || discriminator.length() == 4 && Helpers.isNumeric(discriminator), "Invalid format for discriminator! Provided: %s", (Object)discriminator);
      String actualDiscriminator = discriminator == null ? "0000" : discriminator;
      return (User)this.getUserCache().applyStream((stream) -> (User)stream.filter((it) -> it.getDiscriminator().equals(actualDiscriminator)).filter((it) -> it.getName().equals(username)).findFirst().orElse((Object)null));
   }

   @Nonnull
   @Incubating
   default @Unmodifiable List<User> getUsersByName(@Nonnull String name, boolean ignoreCase) {
      return this.getUserCache().getElementsByName(name, ignoreCase);
   }

   @Nonnull
   @Unmodifiable List<Guild> getMutualGuilds(@Nonnull UserSnowflake... var1);

   @Nonnull
   @Unmodifiable List<Guild> getMutualGuilds(@Nonnull Collection<? extends UserSnowflake> var1);

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<User> retrieveUserById(@Nonnull String id) {
      return this.retrieveUserById(MiscUtil.parseSnowflake(id));
   }

   @Nonnull
   @CheckReturnValue
   CacheRestAction<User> retrieveUserById(long var1);

   @Nonnull
   SnowflakeCacheView<Guild> getGuildCache();

   @Nonnull
   default @Unmodifiable List<Guild> getGuilds() {
      return this.getGuildCache().asList();
   }

   @Nullable
   default Guild getGuildById(@Nonnull String id) {
      return (Guild)this.getGuildCache().getElementById(id);
   }

   @Nullable
   default Guild getGuildById(long id) {
      return (Guild)this.getGuildCache().getElementById(id);
   }

   @Nonnull
   default @Unmodifiable List<Guild> getGuildsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getGuildCache().getElementsByName(name, ignoreCase);
   }

   @Nonnull
   Set<String> getUnavailableGuilds();

   boolean isUnavailable(long var1);

   @Nonnull
   SnowflakeCacheView<Role> getRoleCache();

   @Nonnull
   default @Unmodifiable List<Role> getRoles() {
      return this.getRoleCache().asList();
   }

   @Nullable
   default Role getRoleById(@Nonnull String id) {
      return (Role)this.getRoleCache().getElementById(id);
   }

   @Nullable
   default Role getRoleById(long id) {
      return (Role)this.getRoleCache().getElementById(id);
   }

   @Nonnull
   default @Unmodifiable List<Role> getRolesByName(@Nonnull String name, boolean ignoreCase) {
      return this.getRoleCache().getElementsByName(name, ignoreCase);
   }

   @Nonnull
   SnowflakeCacheView<ScheduledEvent> getScheduledEventCache();

   @Nonnull
   default @Unmodifiable List<ScheduledEvent> getScheduledEvents() {
      return this.getScheduledEventCache().asList();
   }

   @Nullable
   default ScheduledEvent getScheduledEventById(@Nonnull String id) {
      return (ScheduledEvent)this.getScheduledEventCache().getElementById(id);
   }

   @Nullable
   default ScheduledEvent getScheduledEventById(long id) {
      return (ScheduledEvent)this.getScheduledEventCache().getElementById(id);
   }

   @Nonnull
   default @Unmodifiable List<ScheduledEvent> getScheduledEventsByName(@Nonnull String name, boolean ignoreCase) {
      return this.getScheduledEventCache().getElementsByName(name, ignoreCase);
   }

   @Nonnull
   SnowflakeCacheView<PrivateChannel> getPrivateChannelCache();

   @Nonnull
   default @Unmodifiable List<PrivateChannel> getPrivateChannels() {
      return this.getPrivateChannelCache().asList();
   }

   @Nullable
   default PrivateChannel getPrivateChannelById(@Nonnull String id) {
      return (PrivateChannel)this.getPrivateChannelCache().getElementById(id);
   }

   @Nullable
   default PrivateChannel getPrivateChannelById(long id) {
      return (PrivateChannel)this.getPrivateChannelCache().getElementById(id);
   }

   @Nonnull
   @CheckReturnValue
   CacheRestAction<PrivateChannel> openPrivateChannelById(long var1);

   @Nonnull
   @CheckReturnValue
   default CacheRestAction<PrivateChannel> openPrivateChannelById(@Nonnull String userId) {
      return this.openPrivateChannelById(MiscUtil.parseSnowflake(userId));
   }

   @Nonnull
   SnowflakeCacheView<RichCustomEmoji> getEmojiCache();

   @Nonnull
   default @Unmodifiable List<RichCustomEmoji> getEmojis() {
      return this.getEmojiCache().asList();
   }

   @Nullable
   default RichCustomEmoji getEmojiById(@Nonnull String id) {
      return (RichCustomEmoji)this.getEmojiCache().getElementById(id);
   }

   @Nullable
   default RichCustomEmoji getEmojiById(long id) {
      return (RichCustomEmoji)this.getEmojiCache().getElementById(id);
   }

   @Nonnull
   default @Unmodifiable List<RichCustomEmoji> getEmojisByName(@Nonnull String name, boolean ignoreCase) {
      return this.getEmojiCache().getElementsByName(name, ignoreCase);
   }

   @Nonnull
   @CheckReturnValue
   RestAction<ApplicationEmoji> createApplicationEmoji(@Nonnull String var1, @Nonnull Icon var2);

   @Nonnull
   @CheckReturnValue
   RestAction<List<ApplicationEmoji>> retrieveApplicationEmojis();

   @Nonnull
   @CheckReturnValue
   default RestAction<ApplicationEmoji> retrieveApplicationEmojiById(long emojiId) {
      return this.retrieveApplicationEmojiById(Long.toUnsignedString(emojiId));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<ApplicationEmoji> retrieveApplicationEmojiById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   RestAction<StickerUnion> retrieveSticker(@Nonnull StickerSnowflake var1);

   @Nonnull
   @CheckReturnValue
   RestAction<@Unmodifiable List<StickerPack>> retrieveNitroStickerPacks();

   @Nonnull
   @CheckReturnValue
   RestAction<@Unmodifiable List<SoundboardSound>> retrieveDefaultSoundboardSounds();

   @Nonnull
   IEventManager getEventManager();

   @Nonnull
   SelfUser getSelfUser();

   @Nonnull
   Presence getPresence();

   @Nonnull
   ShardInfo getShardInfo();

   @Nonnull
   String getToken();

   long getResponseTotal();

   int getMaxReconnectDelay();

   void setAutoReconnect(boolean var1);

   void setRequestTimeoutRetry(boolean var1);

   boolean isAutoReconnect();

   boolean isBulkDeleteSplittingEnabled();

   void shutdown();

   void shutdownNow();

   @Nonnull
   @CheckReturnValue
   RestAction<ApplicationInfo> retrieveApplicationInfo();

   @Nonnull
   @CheckReturnValue
   RestAction<List<SKU>> retrieveSKUList();

   @Nonnull
   @CheckReturnValue
   EntitlementPaginationAction retrieveEntitlements();

   @Nonnull
   @CheckReturnValue
   default RestAction<Entitlement> retrieveEntitlementById(@Nonnull String entitlementId) {
      return this.retrieveEntitlementById(MiscUtil.parseSnowflake(entitlementId));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<Entitlement> retrieveEntitlementById(long var1);

   @Nonnull
   @CheckReturnValue
   default TestEntitlementCreateAction createTestEntitlement(@Nonnull String skuId, @Nonnull String ownerId, @Nonnull TestEntitlementCreateAction.OwnerType ownerType) {
      return this.createTestEntitlement(MiscUtil.parseSnowflake(skuId), MiscUtil.parseSnowflake(ownerId), ownerType);
   }

   @Nonnull
   @CheckReturnValue
   TestEntitlementCreateAction createTestEntitlement(long var1, long var3, @Nonnull TestEntitlementCreateAction.OwnerType var5);

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> deleteTestEntitlement(@Nonnull String entitlementId) {
      return this.deleteTestEntitlement(MiscUtil.parseSnowflake(entitlementId));
   }

   @Nonnull
   @CheckReturnValue
   RestAction<Void> deleteTestEntitlement(long var1);

   @Nonnull
   default JDA setRequiredScopes(@Nonnull String... scopes) {
      Checks.noneNull(scopes, "Scopes");
      return this.setRequiredScopes((Collection)Arrays.asList(scopes));
   }

   @Nonnull
   JDA setRequiredScopes(@Nonnull Collection<String> var1);

   @Nonnull
   String getInviteUrl(@Nullable Permission... var1);

   @Nonnull
   String getInviteUrl(@Nullable Collection<Permission> var1);

   @Nullable
   ShardManager getShardManager();

   @Nonnull
   @CheckReturnValue
   RestAction<Webhook> retrieveWebhookById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<Webhook> retrieveWebhookById(long webhookId) {
      return this.retrieveWebhookById(Long.toUnsignedString(webhookId));
   }

   @Nonnull
   @CheckReturnValue
   default AuditableRestAction<Integer> installAuxiliaryPort() {
      int port = ThreadLocalRandom.current().nextInt();
      if (Desktop.isDesktopSupported()) {
         try {
            Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ"));
         } catch (URISyntaxException | IOException var3) {
            throw new IllegalStateException("No port available");
         }

         return new CompletedRestAction<Integer>(this, port);
      } else {
         throw new IllegalStateException("No port available");
      }
   }

   @Nonnull
   @CheckReturnValue
   ApplicationManager getApplicationManager();

   public static enum Status {
      INITIALIZING(true),
      INITIALIZED(true),
      LOGGING_IN(true),
      CONNECTING_TO_WEBSOCKET(true),
      IDENTIFYING_SESSION(true),
      AWAITING_LOGIN_CONFIRMATION(true),
      LOADING_SUBSYSTEMS(true),
      CONNECTED(true),
      DISCONNECTED,
      RECONNECT_QUEUED,
      WAITING_TO_RECONNECT,
      ATTEMPTING_TO_RECONNECT,
      SHUTTING_DOWN,
      SHUTDOWN,
      FAILED_TO_LOGIN;

      private final boolean isInit;

      private Status(boolean isInit) {
         this.isInit = isInit;
      }

      private Status() {
         this.isInit = false;
      }

      public boolean isInit() {
         return this.isInit;
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{INITIALIZING, INITIALIZED, LOGGING_IN, CONNECTING_TO_WEBSOCKET, IDENTIFYING_SESSION, AWAITING_LOGIN_CONFIRMATION, LOADING_SUBSYSTEMS, CONNECTED, DISCONNECTED, RECONNECT_QUEUED, WAITING_TO_RECONNECT, ATTEMPTING_TO_RECONNECT, SHUTTING_DOWN, SHUTDOWN, FAILED_TO_LOGIN};
      }
   }

   public static class ShardInfo {
      public static final ShardInfo SINGLE = new ShardInfo(0, 1);
      int shardId;
      int shardTotal;

      public ShardInfo(int shardId, int shardTotal) {
         this.shardId = shardId;
         this.shardTotal = shardTotal;
      }

      public int getShardId() {
         return this.shardId;
      }

      public int getShardTotal() {
         return this.shardTotal;
      }

      @Nonnull
      public String getShardString() {
         return "[" + this.shardId + " / " + this.shardTotal + "]";
      }

      @Nonnull
      public String toString() {
         return (new EntityString(this)).addMetadata("currentShard", this.getShardString()).addMetadata("totalShards", this.getShardTotal()).toString();
      }

      public boolean equals(Object o) {
         if (!(o instanceof ShardInfo)) {
            return false;
         } else {
            ShardInfo oInfo = (ShardInfo)o;
            return this.shardId == oInfo.getShardId() && this.shardTotal == oInfo.getShardTotal();
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.shardId, this.shardTotal});
      }
   }
}
