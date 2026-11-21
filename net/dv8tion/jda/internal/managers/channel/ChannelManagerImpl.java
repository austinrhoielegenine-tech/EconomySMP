package net.dv8tion.jda.internal.managers.channel;

import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelFlag;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.BaseForumTag;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.channel.ChannelManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPermissionContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;
import net.dv8tion.jda.internal.managers.ManagerBase;
import net.dv8tion.jda.internal.requests.restaction.PermOverrideData;
import net.dv8tion.jda.internal.utils.ChannelUtil;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import okhttp3.RequestBody;

public class ChannelManagerImpl<T extends GuildChannel, M extends ChannelManager<T, M>> extends ManagerBase<M> implements ChannelManager<T, M> {
   protected T channel;
   protected final EnumSet<ChannelFlag> flags;
   protected ThreadChannel.AutoArchiveDuration autoArchiveDuration;
   protected List<BaseForumTag> availableTags;
   protected List<String> appliedTags;
   protected Emoji defaultReactionEmoji;
   protected int defaultLayout;
   protected int defaultSortOrder;
   protected ChannelType type;
   protected String name;
   protected String parent;
   protected String topic;
   protected String region;
   protected boolean nsfw;
   protected boolean archived;
   protected boolean locked;
   protected boolean invitable;
   protected int position;
   protected int slowmode;
   protected int defaultThreadSlowmode;
   protected int userLimit;
   protected int bitrate;
   protected final Object lock = new Object();
   protected final TLongObjectHashMap<PermOverrideData> overridesAdd;
   protected final TLongSet overridesRem;

   public ChannelManagerImpl(T channel) {
      super(channel.getJDA(), Route.Channels.MODIFY_CHANNEL.compile(channel.getId()));
      this.channel = channel;
      this.type = channel.getType();
      this.flags = channel.getFlags();
      if (isPermissionChecksEnabled()) {
         this.checkPermissions();
      }

      this.overridesAdd = new TLongObjectHashMap<PermOverrideData>();
      this.overridesRem = new TLongHashSet();
   }

   @Nonnull
   public T getChannel() {
      T realChannel = this.api.getGuildChannelById(this.channel.getType(), this.channel.getIdLong());
      if (realChannel != null) {
         this.channel = realChannel;
      }

      return this.channel;
   }

   @Nonnull
   @CheckReturnValue
   public M reset(long fields) {
      super.reset(fields);
      if ((fields & 1L) == 1L) {
         this.name = null;
      }

      if ((fields & 512L) == 512L) {
         this.type = this.channel.getType();
      }

      if ((fields & 2L) == 2L) {
         this.parent = null;
      }

      if ((fields & 4L) == 4L) {
         this.topic = null;
      }

      if ((fields & 1024L) == 1024L) {
         this.region = null;
      }

      if ((fields & 32768L) == 32768L) {
         this.availableTags = null;
      }

      if ((fields & 65536L) == 65536L) {
         this.appliedTags = null;
      }

      if ((fields & 524288L) == 524288L) {
         this.defaultReactionEmoji = null;
      }

      if ((fields & 128L) == 128L) {
         this.withLock(this.lock, (lock) -> {
            this.overridesRem.clear();
            this.overridesAdd.clear();
         });
      }

      if ((fields & 131072L) == 131072L) {
         if (this.channel.getFlags().contains(ChannelFlag.PINNED)) {
            this.flags.add(ChannelFlag.PINNED);
         } else {
            this.flags.remove(ChannelFlag.PINNED);
         }
      }

      if ((fields & 262144L) == 262144L) {
         if (this.channel.getFlags().contains(ChannelFlag.REQUIRE_TAG)) {
            this.flags.add(ChannelFlag.REQUIRE_TAG);
         } else {
            this.flags.remove(ChannelFlag.REQUIRE_TAG);
         }
      }

      if ((fields & 4194304L) == 4194304L) {
         if (this.channel.getFlags().contains(ChannelFlag.HIDE_MEDIA_DOWNLOAD_OPTIONS)) {
            this.flags.add(ChannelFlag.HIDE_MEDIA_DOWNLOAD_OPTIONS);
         } else {
            this.flags.remove(ChannelFlag.HIDE_MEDIA_DOWNLOAD_OPTIONS);
         }
      }

      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M reset(@Nonnull long... fields) {
      super.reset(fields);
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M reset() {
      super.reset();
      this.name = null;
      this.type = this.channel.getType();
      this.parent = null;
      this.topic = null;
      this.region = null;
      this.availableTags = null;
      this.appliedTags = null;
      this.defaultReactionEmoji = null;
      this.flags.clear();
      this.flags.addAll(this.channel.getFlags());
      this.withLock(this.lock, (lock) -> {
         this.overridesRem.clear();
         this.overridesAdd.clear();
      });
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M clearOverridesAdded() {
      this.withLock(this.lock, (lock) -> {
         this.overridesAdd.clear();
         if (this.overridesRem.isEmpty()) {
            this.set &= -129L;
         }

      });
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M clearOverridesRemoved() {
      this.withLock(this.lock, (lock) -> {
         this.overridesRem.clear();
         if (this.overridesAdd.isEmpty()) {
            this.set &= -129L;
         }

      });
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M putPermissionOverride(@Nonnull IPermissionHolder permHolder, long allow, long deny) {
      if (!(this.channel instanceof IPermissionContainer)) {
         throw new IllegalStateException("Can only set permissions on Channels that implement IPermissionContainer");
      } else {
         Checks.notNull(permHolder, "PermissionHolder");
         Checks.check(permHolder.getGuild().equals(this.getGuild()), "PermissionHolder is not from the same Guild!");
         long id = permHolder.getIdLong();
         int type = permHolder instanceof Role ? 0 : 1;
         this.putPermissionOverride(new PermOverrideData(type, id, allow, deny));
         return (M)this;
      }
   }

   @Nonnull
   @CheckReturnValue
   public M putMemberPermissionOverride(long memberId, long allow, long deny) {
      this.putPermissionOverride(new PermOverrideData(1, memberId, allow, deny));
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M putRolePermissionOverride(long roleId, long allow, long deny) {
      this.putPermissionOverride(new PermOverrideData(0, roleId, allow, deny));
      return (M)this;
   }

   private void checkCanPutPermissions(long allow, long deny) {
      Member selfMember = this.getGuild().getSelfMember();
      if (isPermissionChecksEnabled() && !selfMember.hasPermission(new Permission[]{Permission.ADMINISTRATOR})) {
         if (!selfMember.hasPermission(this.channel, new Permission[]{Permission.MANAGE_ROLES})) {
            throw new InsufficientPermissionException(this.channel, Permission.MANAGE_PERMISSIONS);
         }

         long channelPermissions = PermissionUtil.getExplicitPermission(this.channel, selfMember, false);
         if ((channelPermissions & Permission.MANAGE_PERMISSIONS.getRawValue()) == 0L) {
            long botPerms = PermissionUtil.getEffectivePermission(this.channel, selfMember) & ~Permission.MANAGE_ROLES.getRawValue();
            EnumSet<Permission> missing = Permission.getPermissions((allow | deny) & ~botPerms);
            if (!missing.isEmpty()) {
               throw new InsufficientPermissionException(this.channel, Permission.MANAGE_PERMISSIONS, "You must have Permission.MANAGE_PERMISSIONS on the channel explicitly in order to set permissions you don't already have!");
            }
         }
      }

   }

   private void putPermissionOverride(@Nonnull PermOverrideData overrideData) {
      this.checkCanPutPermissions(overrideData.allow, overrideData.deny);
      this.withLock(this.lock, (lock) -> {
         this.overridesRem.remove(overrideData.id);
         this.overridesAdd.put(overrideData.id, overrideData);
         this.set |= 128L;
      });
   }

   @Nonnull
   @CheckReturnValue
   public M removePermissionOverride(@Nonnull IPermissionHolder permHolder) {
      if (!(this.channel instanceof IPermissionContainer)) {
         throw new IllegalStateException("Can only set permissions on Channels that implement IPermissionContainer");
      } else {
         Checks.notNull(permHolder, "PermissionHolder");
         Checks.check(permHolder.getGuild().equals(this.getGuild()), "PermissionHolder is not from the same Guild!");
         return (M)this.removePermissionOverride(permHolder.getIdLong());
      }
   }

   @Nonnull
   @CheckReturnValue
   public M removePermissionOverride(long id) {
      if (isPermissionChecksEnabled() && !this.getGuild().getSelfMember().hasPermission(this.getChannel(), new Permission[]{Permission.MANAGE_PERMISSIONS})) {
         throw new InsufficientPermissionException(this.getChannel(), Permission.MANAGE_PERMISSIONS);
      } else {
         this.withLock(this.lock, (lock) -> {
            this.overridesRem.add(id);
            this.overridesAdd.remove(id);
            this.set |= 128L;
         });
         return (M)this;
      }
   }

   @Nonnull
   @CheckReturnValue
   public M sync(@Nonnull IPermissionContainer syncSource) {
      if (!(this.channel instanceof IPermissionContainer)) {
         throw new IllegalStateException("Can only set permissions on Channels that implement IPermissionContainer");
      } else {
         Checks.notNull(syncSource, "SyncSource");
         Checks.check(this.getGuild().equals(syncSource.getGuild()), "Sync only works for channels of same guild");
         IPermissionContainer permChannel = (IPermissionContainer)this.channel;
         if (syncSource.equals(this.getChannel())) {
            return (M)this;
         } else {
            if (isPermissionChecksEnabled()) {
               Member selfMember = this.getGuild().getSelfMember();
               if (!selfMember.hasPermission(permChannel, new Permission[]{Permission.MANAGE_PERMISSIONS})) {
                  throw new InsufficientPermissionException(this.getChannel(), Permission.MANAGE_PERMISSIONS);
               }

               if (!selfMember.canSync(permChannel, syncSource)) {
                  throw new InsufficientPermissionException(this.getChannel(), Permission.MANAGE_PERMISSIONS, "Cannot sync channel with parent due to permission escalation issues. One of the overrides would set MANAGE_PERMISSIONS or a permission that the bot does not have. This is not possible without explicitly having MANAGE_PERMISSIONS on this channel or ADMINISTRATOR on a role.");
               }
            }

            this.withLock(this.lock, (lock) -> {
               this.overridesRem.clear();
               this.overridesAdd.clear();
               LongStream var10000 = permChannel.getPermissionOverrides().stream().mapToLong(ISnowflake::getIdLong);
               TLongSet var10001 = this.overridesRem;
               Objects.requireNonNull(var10001);
               var10000.forEach(var10001::add);
               syncSource.getPermissionOverrides().forEach((override) -> {
                  int type = override.isRoleOverride() ? 0 : 1;
                  long id = override.getIdLong();
                  this.overridesRem.remove(id);
                  this.overridesAdd.put(id, new PermOverrideData(type, id, override.getAllowedRaw(), override.getDeniedRaw()));
               });
               this.set |= 128L;
            });
            return (M)this;
         }
      }
   }

   @Nonnull
   @CheckReturnValue
   public M setName(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      this.name = name;
      this.set |= 1L;
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M setType(@Nonnull ChannelType type) {
      Checks.check(type == ChannelType.TEXT || type == ChannelType.NEWS, "Can only change ChannelType to TEXT or NEWS");
      if (this.type != ChannelType.TEXT && this.type != ChannelType.NEWS) {
         throw new UnsupportedOperationException("Can only set ChannelType for TextChannel and NewsChannels");
      } else if (type == ChannelType.NEWS && !this.getGuild().getFeatures().contains("NEWS")) {
         throw new IllegalStateException("Can only set ChannelType to NEWS for guilds with NEWS feature");
      } else {
         this.type = type;
         if (this.type == this.channel.getType()) {
            this.reset(512L);
         } else {
            this.set |= 512L;
         }

         if (type != ChannelType.TEXT) {
            this.reset(256L);
         }

         return (M)this;
      }
   }

   @Nonnull
   @CheckReturnValue
   public M setRegion(@Nonnull Region region) {
      Checks.notNull(region, "Region");
      if (!this.type.isAudio()) {
         throw new IllegalStateException("Can only change region on audio channels!");
      } else {
         this.region = region == Region.AUTOMATIC ? null : region.getKey();
         this.set |= 1024L;
         return (M)this;
      }
   }

   @Nonnull
   @CheckReturnValue
   public M setParent(Category category) {
      if (this.type == ChannelType.CATEGORY) {
         throw new IllegalStateException("Cannot set the parent of a category");
      } else {
         Checks.check(category == null || category.getGuild().equals(this.getGuild()), "Category is not from the same guild");
         this.parent = category == null ? null : category.getId();
         this.set |= 2L;
         return (M)this;
      }
   }

   @Nonnull
   @CheckReturnValue
   public M setPosition(int position) {
      this.position = position;
      this.set |= 8L;
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M setTopic(String topic) {
      Checks.checkSupportedChannelTypes(ChannelUtil.TOPIC_SUPPORTED, this.type, "topic");
      if (topic != null) {
         if (this.channel instanceof IPostContainer) {
            Checks.notLonger(topic, 4096, "Topic");
         } else {
            Checks.notLonger(topic, 1024, "Topic");
         }
      }

      this.topic = topic;
      this.set |= 4L;
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M setNSFW(boolean nsfw) {
      Checks.checkSupportedChannelTypes(ChannelUtil.NSFW_SUPPORTED, this.type, "NSFW (age-restriction)");
      this.nsfw = nsfw;
      this.set |= 16L;
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M setSlowmode(int slowmode) {
      Checks.checkSupportedChannelTypes(ChannelUtil.SLOWMODE_SUPPORTED, this.type, "slowmode");
      Checks.check(slowmode <= 21600 && slowmode >= 0, "Slowmode per user must be between 0 and %d (seconds)!", (int)21600);
      this.slowmode = slowmode;
      this.set |= 256L;
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M setDefaultThreadSlowmode(int slowmode) {
      Checks.check(this.channel instanceof IThreadContainer, "Cannot set default thread slowmode on channels of type %s!", (Object)this.channel.getType());
      Checks.check(slowmode <= 21600 && slowmode >= 0, "Slowmode per user must be between 0 and %d (seconds)!", (int)21600);
      this.defaultThreadSlowmode = slowmode;
      this.set |= 8388608L;
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M setUserLimit(int userLimit) {
      Checks.notNegative(userLimit, "Userlimit");
      if (this.type == ChannelType.VOICE) {
         Checks.check(userLimit <= 99, "Userlimit may not be greater than %d for voice channels", (int)99);
      } else {
         if (this.type != ChannelType.STAGE) {
            throw new IllegalStateException("Can only set userlimit on audio channels");
         }

         Checks.check(userLimit <= 10000, "Userlimit may not be greater than %d for stage channels", (int)10000);
      }

      this.userLimit = userLimit;
      this.set |= 32L;
      return (M)this;
   }

   @Nonnull
   @CheckReturnValue
   public M setBitrate(int bitrate) {
      if (!this.type.isAudio()) {
         throw new IllegalStateException("Can only set bitrate on voice channels");
      } else {
         int maxBitrate = this.getGuild().getMaxBitrate();
         Checks.check(bitrate >= 8000, "Bitrate must be greater or equal to 8000");
         Checks.check(bitrate <= maxBitrate, "Bitrate must be less or equal to %s", (Object)maxBitrate);
         this.bitrate = bitrate;
         this.set |= 64L;
         return (M)this;
      }
   }

   public M setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration autoArchiveDuration) {
      Checks.notNull(autoArchiveDuration, "autoArchiveDuration");
      if (!this.type.isThread()) {
         throw new IllegalStateException("Can only set autoArchiveDuration on threads");
      } else {
         this.autoArchiveDuration = autoArchiveDuration;
         this.set |= 2048L;
         return (M)this;
      }
   }

   public M setArchived(boolean archived) {
      if (!this.type.isThread()) {
         throw new IllegalStateException("Can only set archived on threads");
      } else {
         if (isPermissionChecksEnabled()) {
            ThreadChannel thread = (ThreadChannel)this.channel;
            if (!thread.isOwner()) {
               this.checkPermission(Permission.MANAGE_THREADS, "Cannot unarchive a thread without MANAGE_THREADS if not the thread owner");
            }

            if (thread.isLocked()) {
               this.checkPermission(Permission.MANAGE_THREADS, "Cannot unarchive a thread that is locked without MANAGE_THREADS");
            }
         }

         this.archived = archived;
         this.set |= 4096L;
         return (M)this;
      }
   }

   public M setLocked(boolean locked) {
      if (!this.type.isThread()) {
         throw new IllegalStateException("Can only set locked on threads");
      } else {
         if (isPermissionChecksEnabled()) {
            this.checkPermission(Permission.MANAGE_THREADS, "Cannot modified a thread's locked status without MANAGE_THREADS");
         }

         this.locked = locked;
         this.set |= 8192L;
         return (M)this;
      }
   }

   public M setInvitable(boolean invitable) {
      if (this.type != ChannelType.GUILD_PRIVATE_THREAD) {
         throw new IllegalStateException("Can only set invitable on private threads.");
      } else {
         if (isPermissionChecksEnabled()) {
            ThreadChannel thread = (ThreadChannel)this.channel;
            if (!thread.isOwner()) {
               this.checkPermission(Permission.MANAGE_THREADS, "Cannot modify a thread's invitable status without MANAGE_THREADS if not the thread owner");
            }
         }

         this.invitable = invitable;
         this.set |= 16384L;
         return (M)this;
      }
   }

   public M setPinned(boolean pinned) {
      if (!this.type.isThread()) {
         throw new IllegalStateException("Can only pin threads.");
      } else {
         if (pinned) {
            this.flags.add(ChannelFlag.PINNED);
         } else {
            this.flags.remove(ChannelFlag.PINNED);
         }

         this.set |= 131072L;
         return (M)this;
      }
   }

   public M setTagRequired(boolean requireTag) {
      if (!(this.channel instanceof IPostContainer)) {
         throw new IllegalStateException("Can only set tag required flag on forum/media channels.");
      } else {
         if (requireTag) {
            this.flags.add(ChannelFlag.REQUIRE_TAG);
         } else {
            this.flags.remove(ChannelFlag.REQUIRE_TAG);
         }

         this.set |= 262144L;
         return (M)this;
      }
   }

   public M setHideMediaDownloadOption(boolean hideOption) {
      if (!(this.channel instanceof MediaChannel)) {
         throw new IllegalStateException("Can only set hide media download flag on media channels.");
      } else {
         if (hideOption) {
            this.flags.add(ChannelFlag.HIDE_MEDIA_DOWNLOAD_OPTIONS);
         } else {
            this.flags.remove(ChannelFlag.HIDE_MEDIA_DOWNLOAD_OPTIONS);
         }

         this.set |= 4194304L;
         return (M)this;
      }
   }

   public M setAvailableTags(List<? extends BaseForumTag> tags) {
      if (!(this.channel instanceof IPostContainer)) {
         throw new IllegalStateException("Can only set available tags on forum/media channels.");
      } else {
         Checks.noneNull(tags, "Available Tags");
         this.availableTags = new ArrayList(tags);
         this.set |= 32768L;
         return (M)this;
      }
   }

   public M setAppliedTags(Collection<? extends ForumTagSnowflake> tags) {
      if (this.type != ChannelType.GUILD_PUBLIC_THREAD) {
         throw new IllegalStateException("Can only set applied tags on forum post thread channels.");
      } else {
         Checks.noneNull(tags, "Applied Tags");
         Checks.check(tags.size() <= 5, "Cannot apply more than %d tags to a post thread!", (int)5);
         ThreadChannel thread = (ThreadChannel)this.getChannel();
         IThreadContainerUnion parentChannel = thread.getParentChannel();
         if (!(parentChannel instanceof IPostContainer)) {
            throw new IllegalStateException("Cannot apply tags to threads outside of forum/media channels.");
         } else if (tags.isEmpty() && parentChannel.asForumChannel().isTagRequired()) {
            throw new IllegalArgumentException("Cannot remove all tags from a forum post which requires at least one tag! See IPostContainer#isRequireTag()");
         } else {
            this.appliedTags = (List)tags.stream().map(ISnowflake::getId).collect(Collectors.toList());
            this.set |= 65536L;
            return (M)this;
         }
      }
   }

   public M setDefaultReaction(Emoji emoji) {
      if (!(this.channel instanceof IPostContainer)) {
         throw new IllegalStateException("Can only set default reaction on forum/media channels.");
      } else {
         this.defaultReactionEmoji = emoji;
         this.set |= 524288L;
         return (M)this;
      }
   }

   public M setDefaultLayout(ForumChannel.Layout layout) {
      if (this.type != ChannelType.FORUM) {
         throw new IllegalStateException("Can only set default layout on forum channels.");
      } else {
         Checks.notNull(layout, "layout");
         if (layout == ForumChannel.Layout.UNKNOWN) {
            throw new IllegalStateException("Layout type cannot be UNKNOWN.");
         } else {
            this.defaultLayout = layout.getKey();
            this.set |= 1048576L;
            return (M)this;
         }
      }
   }

   public M setDefaultSortOrder(IPostContainer.SortOrder sortOrder) {
      if (!(this.channel instanceof IPostContainer)) {
         throw new IllegalStateException("Can only set default layout on forum/media channels.");
      } else {
         Checks.notNull(sortOrder, "SortOrder");
         if (sortOrder == IPostContainer.SortOrder.UNKNOWN) {
            throw new IllegalStateException("SortOrder type cannot be UNKNOWN.");
         } else {
            this.defaultSortOrder = sortOrder.getKey();
            this.set |= 2097152L;
            return (M)this;
         }
      }
   }

   protected RequestBody finalizeData() {
      DataObject frame = DataObject.empty();
      if (this.shouldUpdate(1L)) {
         frame.put("name", this.name);
      }

      if (this.shouldUpdate(512L)) {
         frame.put("type", this.type.getId());
      }

      if (this.shouldUpdate(8L)) {
         frame.put("position", this.position);
      }

      if (this.shouldUpdate(4L)) {
         frame.put("topic", this.topic);
      }

      if (this.shouldUpdate(16L)) {
         frame.put("nsfw", this.nsfw);
      }

      if (this.shouldUpdate(256L)) {
         frame.put("rate_limit_per_user", this.slowmode);
      }

      if (this.shouldUpdate(8388608L)) {
         frame.put("default_thread_rate_limit_per_user", this.defaultThreadSlowmode);
      }

      if (this.shouldUpdate(32L)) {
         frame.put("user_limit", this.userLimit);
      }

      if (this.shouldUpdate(64L)) {
         frame.put("bitrate", this.bitrate);
      }

      if (this.shouldUpdate(2L)) {
         frame.put("parent_id", this.parent);
      }

      if (this.shouldUpdate(1024L)) {
         frame.put("rtc_region", this.region);
      }

      if (this.shouldUpdate(2048L)) {
         frame.put("auto_archive_duration", this.autoArchiveDuration.getMinutes());
      }

      if (this.shouldUpdate(4096L)) {
         frame.put("archived", this.archived);
      }

      if (this.shouldUpdate(8192L)) {
         frame.put("locked", this.locked);
      }

      if (this.shouldUpdate(16384L)) {
         frame.put("invitable", this.invitable);
      }

      if (this.shouldUpdate(32768L)) {
         frame.put("available_tags", DataArray.fromCollection(this.availableTags));
      }

      if (this.shouldUpdate(65536L)) {
         frame.put("applied_tags", DataArray.fromCollection(this.appliedTags));
      }

      if (this.shouldUpdate(4587520L)) {
         frame.put("flags", ChannelFlag.getRaw(this.flags));
      }

      if (this.shouldUpdate(524288L)) {
         if (this.defaultReactionEmoji instanceof CustomEmoji) {
            frame.put("default_reaction_emoji", DataObject.empty().put("emoji_id", ((CustomEmoji)this.defaultReactionEmoji).getId()));
         } else if (this.defaultReactionEmoji instanceof UnicodeEmoji) {
            frame.put("default_reaction_emoji", DataObject.empty().put("emoji_name", this.defaultReactionEmoji.getName()));
         } else {
            frame.put("default_reaction_emoji", (Object)null);
         }
      }

      if (this.shouldUpdate(1048576L)) {
         frame.put("default_forum_layout", this.defaultLayout);
      }

      if (this.shouldUpdate(2097152L)) {
         frame.put("default_sort_order", this.defaultSortOrder);
      }

      this.withLock(this.lock, (lock) -> {
         if (this.shouldUpdate(128L)) {
            frame.put("permission_overwrites", this.getOverrides());
         }

      });
      this.reset();
      return this.getRequestBody(frame);
   }

   protected boolean checkPermissions() {
      Member selfMember = this.getGuild().getSelfMember();
      Checks.checkAccess(selfMember, this.channel);
      ((GuildChannelMixin)this.channel).checkCanManage();
      return super.checkPermissions();
   }

   protected void checkPermission(Permission permission, String errMessage) {
      if (!this.getGuild().getSelfMember().hasPermission(this.getChannel(), new Permission[]{permission})) {
         throw new InsufficientPermissionException(this.getChannel(), permission, errMessage);
      }
   }

   protected Collection<PermOverrideData> getOverrides() {
      TLongObjectHashMap<PermOverrideData> data = new TLongObjectHashMap<PermOverrideData>(this.overridesAdd);
      IPermissionContainerMixin<?> impl = (IPermissionContainerMixin)this.getChannel();
      impl.getPermissionOverrideMap().forEachEntry((id, override) -> {
         if (!this.overridesRem.remove(id) && !data.containsKey(id)) {
            data.put(id, new PermOverrideData(override));
         }

         return true;
      });
      return data.valueCollection();
   }
}
