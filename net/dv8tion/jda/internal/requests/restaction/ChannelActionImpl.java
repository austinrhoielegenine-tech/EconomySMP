package net.dv8tion.jda.internal.requests.restaction;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.forums.BaseForumTag;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.utils.ChannelUtil;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import okhttp3.RequestBody;

public class ChannelActionImpl<T extends GuildChannel> extends AuditableRestActionImpl<T> implements ChannelAction<T> {
   protected final TLongObjectMap<PermOverrideData> overrides = new TLongObjectHashMap<PermOverrideData>();
   protected final Guild guild;
   protected final Class<T> clazz;
   protected final ChannelType type;
   protected String name;
   protected Category parent;
   protected Integer position;
   protected List<? extends BaseForumTag> availableTags;
   protected Emoji defaultReactionEmoji;
   protected Integer slowmode = null;
   protected Integer defaultThreadSlowmode = null;
   protected String topic = null;
   protected Boolean nsfw = null;
   protected Integer userlimit = null;
   protected Integer bitrate = null;
   protected Region region = null;
   protected Integer defaultLayout = null;
   protected Integer defaultSortOrder = null;

   public ChannelActionImpl(Class<T> clazz, String name, Guild guild, ChannelType type) {
      super(guild.getJDA(), Route.Guilds.CREATE_CHANNEL.compile(guild.getId()));
      this.clazz = clazz;
      this.guild = guild;
      this.type = type;
      this.name = name;
   }

   @Nonnull
   public ChannelActionImpl<T> reason(@Nullable String reason) {
      return (ChannelActionImpl)super.reason(reason);
   }

   @Nonnull
   public ChannelActionImpl<T> setCheck(BooleanSupplier checks) {
      return (ChannelActionImpl)super.setCheck(checks);
   }

   @Nonnull
   public ChannelActionImpl<T> timeout(long timeout, @Nonnull TimeUnit unit) {
      return (ChannelActionImpl)super.timeout(timeout, unit);
   }

   @Nonnull
   public ChannelActionImpl<T> deadline(long timestamp) {
      return (ChannelActionImpl)super.deadline(timestamp);
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   @Nonnull
   public ChannelType getType() {
      return this.type;
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> setName(@Nonnull String name) {
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      this.name = name;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> setParent(Category category) {
      if (category != null) {
         Checks.check(category.getGuild().equals(this.guild), "Category is not from same guild!");
         if (this.type == ChannelType.CATEGORY) {
            throw new UnsupportedOperationException("Cannot set a parent Category on a Category");
         }
      }

      this.parent = category;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> setPosition(Integer position) {
      Checks.check(position == null || position >= 0, "Position must be >= 0!");
      this.position = position;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> setTopic(String topic) {
      Checks.checkSupportedChannelTypes(ChannelUtil.TOPIC_SUPPORTED, this.type, "Topic");
      if (topic != null) {
         if (ChannelUtil.POST_CONTAINERS.contains(this.type)) {
            Checks.notLonger(topic, 4096, "Topic");
         } else {
            Checks.notLonger(topic, 1024, "Topic");
         }
      }

      this.topic = topic;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> setNSFW(boolean nsfw) {
      Checks.checkSupportedChannelTypes(ChannelUtil.NSFW_SUPPORTED, this.type, "NSFW (age-restricted)");
      this.nsfw = nsfw;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> setSlowmode(int slowmode) {
      Checks.checkSupportedChannelTypes(ChannelUtil.SLOWMODE_SUPPORTED, this.type, "Slowmode");
      Checks.check(slowmode <= 21600 && slowmode >= 0, "Slowmode must be between 0 and %d (seconds)!", (int)21600);
      this.slowmode = slowmode;
      return this;
   }

   @Nonnull
   public ChannelAction<T> setDefaultThreadSlowmode(int slowmode) {
      Checks.checkSupportedChannelTypes(ChannelUtil.THREAD_CONTAINERS, this.type, "Default Thread Slowmode");
      Checks.check(slowmode <= 21600 && slowmode >= 0, "Slowmode must be between 0 and %d (seconds)!", (int)21600);
      this.defaultThreadSlowmode = slowmode;
      return this;
   }

   @Nonnull
   public ChannelAction<T> setDefaultReaction(@Nullable Emoji emoji) {
      Checks.checkSupportedChannelTypes(ChannelUtil.POST_CONTAINERS, this.type, "Default Reaction");
      this.defaultReactionEmoji = emoji;
      return this;
   }

   @Nonnull
   public ChannelAction<T> setDefaultLayout(@Nonnull ForumChannel.Layout layout) {
      Checks.checkSupportedChannelTypes(EnumSet.of(ChannelType.FORUM), this.type, "Default Layout");
      Checks.notNull(layout, "layout");
      Checks.check(layout != ForumChannel.Layout.UNKNOWN, "Layout type cannot be UNKNOWN.");
      this.defaultLayout = layout.getKey();
      return this;
   }

   @Nonnull
   public ChannelAction<T> setDefaultSortOrder(@Nonnull IPostContainer.SortOrder sortOrder) {
      Checks.checkSupportedChannelTypes(ChannelUtil.POST_CONTAINERS, this.type, "Default Sort Order");
      Checks.notNull(sortOrder, "SortOrder");
      Checks.check(sortOrder != IPostContainer.SortOrder.UNKNOWN, "Sort Order cannot be UNKNOWN.");
      this.defaultSortOrder = sortOrder.getKey();
      return this;
   }

   @Nonnull
   public ChannelAction<T> setAvailableTags(@Nonnull List<? extends BaseForumTag> tags) {
      Checks.checkSupportedChannelTypes(ChannelUtil.POST_CONTAINERS, this.type, "Available Tags");
      Checks.noneNull(tags, "Tags");
      this.availableTags = new ArrayList(tags);
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> addMemberPermissionOverride(long userId, long allow, long deny) {
      return this.addOverride(userId, 1, allow, deny);
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> addRolePermissionOverride(long roleId, long allow, long deny) {
      return this.addOverride(roleId, 0, allow, deny);
   }

   @Nonnull
   public ChannelAction<T> removePermissionOverride(long id) {
      this.overrides.remove(id);
      return this;
   }

   @Nonnull
   public ChannelAction<T> clearPermissionOverrides() {
      this.overrides.clear();
      return this;
   }

   @Nonnull
   public ChannelAction<T> syncPermissionOverrides() {
      if (this.parent == null) {
         throw new IllegalStateException("Cannot sync overrides without parent category! Use setParent(category) first!");
      } else {
         this.clearPermissionOverrides();
         Member selfMember = this.getGuild().getSelfMember();
         boolean canSetRoles = selfMember.hasPermission(this.parent, new Permission[]{Permission.MANAGE_ROLES});
         long botPerms = PermissionUtil.getEffectivePermission(selfMember) & ~Permission.MANAGE_PERMISSIONS.getRawValue();
         this.parent.getRolePermissionOverrides().forEach((override) -> {
            long allow = override.getAllowedRaw();
            long deny = override.getDeniedRaw();
            if (!canSetRoles) {
               allow &= botPerms;
               deny &= botPerms;
            }

            this.addRolePermissionOverride(override.getIdLong(), allow, deny);
         });
         this.parent.getMemberPermissionOverrides().forEach((override) -> {
            long allow = override.getAllowedRaw();
            long deny = override.getDeniedRaw();
            if (!canSetRoles) {
               allow &= botPerms;
               deny &= botPerms;
            }

            this.addMemberPermissionOverride(override.getIdLong(), allow, deny);
         });
         return this;
      }
   }

   private ChannelActionImpl<T> addOverride(long targetId, int type, long allow, long deny) {
      Member selfMember = this.getGuild().getSelfMember();
      boolean canSetRoles = selfMember.hasPermission(new Permission[]{Permission.ADMINISTRATOR});
      if (!canSetRoles && this.parent != null) {
         canSetRoles = selfMember.hasPermission(this.parent, new Permission[]{Permission.MANAGE_ROLES});
      }

      if (!canSetRoles) {
         long botPerms = PermissionUtil.getEffectivePermission(selfMember) & ~Permission.MANAGE_PERMISSIONS.getRawValue();
         EnumSet<Permission> missingPerms = Permission.getPermissions((allow | deny) & ~botPerms);
         if (!missingPerms.isEmpty()) {
            throw new InsufficientPermissionException(this.guild, Permission.MANAGE_PERMISSIONS, "You must have Permission.MANAGE_PERMISSIONS on the channel explicitly in order to set permissions you don't already have!");
         }
      }

      this.overrides.put(targetId, new PermOverrideData(type, targetId, allow, deny));
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> setBitrate(Integer bitrate) {
      if (!this.type.isAudio()) {
         throw new UnsupportedOperationException("Can only set the bitrate for an Audio Channel!");
      } else {
         if (bitrate != null) {
            int maxBitrate = this.getGuild().getMaxBitrate();
            if (bitrate < 8000) {
               throw new IllegalArgumentException("Bitrate must be greater than 8000.");
            }

            if (bitrate > maxBitrate) {
               throw new IllegalArgumentException("Bitrate must be less than " + maxBitrate);
            }
         }

         this.bitrate = bitrate;
         return this;
      }
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> setUserlimit(Integer userlimit) {
      if (userlimit != null) {
         Checks.notNegative(userlimit, "Userlimit");
         if (this.type == ChannelType.VOICE) {
            Checks.check(userlimit <= 99, "Userlimit may not be greater than %d for voice channels", (int)99);
         } else {
            if (this.type != ChannelType.STAGE) {
               throw new IllegalStateException("Can only set userlimit on audio channels");
            }

            Checks.check(userlimit <= 10000, "Userlimit may not be greater than %d for stage channels", (int)10000);
         }
      }

      this.userlimit = userlimit;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public ChannelActionImpl<T> setRegion(@Nullable Region region) {
      if (!this.type.isAudio()) {
         throw new UnsupportedOperationException("Can only set the region for AudioChannels!");
      } else {
         this.region = region;
         return this;
      }
   }

   protected RequestBody finalizeData() {
      DataObject object = DataObject.empty();
      object.put("name", this.name);
      object.put("type", this.type.getId());
      object.put("permission_overwrites", DataArray.fromCollection(this.overrides.valueCollection()));
      if (this.position != null) {
         object.put("position", this.position);
      }

      if (this.parent != null) {
         object.put("parent_id", this.parent.getId());
      }

      if (this.slowmode != null) {
         object.put("rate_limit_per_user", this.slowmode);
      }

      if (this.defaultThreadSlowmode != null) {
         object.put("default_thread_rate_limit_per_user", this.defaultThreadSlowmode);
      }

      if (this.topic != null && !this.topic.isEmpty()) {
         object.put("topic", this.topic);
      }

      if (this.nsfw != null) {
         object.put("nsfw", this.nsfw);
      }

      if (this.defaultReactionEmoji instanceof CustomEmoji) {
         object.put("default_reaction_emoji", DataObject.empty().put("emoji_id", ((CustomEmoji)this.defaultReactionEmoji).getId()));
      } else if (this.defaultReactionEmoji instanceof UnicodeEmoji) {
         object.put("default_reaction_emoji", DataObject.empty().put("emoji_name", this.defaultReactionEmoji.getName()));
      }

      if (this.availableTags != null) {
         object.put("available_tags", DataArray.fromCollection(this.availableTags));
      }

      if (this.defaultSortOrder != null) {
         object.put("default_sort_order", this.defaultSortOrder);
      }

      if (this.defaultLayout != null) {
         object.put("default_forum_layout", this.defaultLayout);
      }

      if (this.userlimit != null) {
         object.put("user_limit", this.userlimit);
      }

      if (this.bitrate != null) {
         object.put("bitrate", this.bitrate);
      }

      if (this.region != null) {
         object.put("rtc_region", this.region.getKey());
      }

      return this.getRequestBody(object);
   }

   protected void handleSuccess(Response response, Request<T> request) {
      EntityBuilder builder = this.api.getEntityBuilder();
      GuildChannel channel = builder.createGuildChannel((GuildImpl)this.guild, response.getObject());
      if (channel == null) {
         request.onFailure((Throwable)(new IllegalStateException("Created channel of unknown type!")));
      } else {
         request.onSuccess((GuildChannel)this.clazz.cast(channel));
      }

   }
}
