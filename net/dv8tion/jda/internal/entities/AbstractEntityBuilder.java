package net.dv8tion.jda.internal.entities;

import net.dv8tion.jda.api.entities.RoleColors;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPostContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.CategoryMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.ForumChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.GroupChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.MediaChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.NewsChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.PrivateChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.StageChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.TextChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.ThreadChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.VoiceChannelMixin;
import net.dv8tion.jda.internal.entities.mixin.MemberMixin;
import net.dv8tion.jda.internal.entities.mixin.RoleMixin;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.SortedSnowflakeCacheViewImpl;

public abstract class AbstractEntityBuilder {
   protected final JDAImpl api;

   AbstractEntityBuilder(JDAImpl api) {
      this.api = api;
   }

   public JDAImpl getJDA() {
      return this.api;
   }

   protected void configureCategory(DataObject json, CategoryMixin<?> channel) {
      ((CategoryMixin)channel.setName(json.getString("name"))).setPosition(json.getInt("position"));
   }

   protected void configureTextChannel(DataObject json, TextChannelMixin<?> channel) {
      ((TextChannelMixin)((TextChannelMixin)((TextChannelMixin)((TextChannelMixin)((TextChannelMixin)((TextChannelMixin)((TextChannelMixin)channel.setParentCategory(json.getLong("parent_id", 0L))).setLatestMessageIdLong(json.getLong("last_message_id", 0L))).setName(json.getString("name"))).setTopic(json.getString("topic", (String)null))).setPosition(json.getInt("position"))).setNSFW(json.getBoolean("nsfw"))).setDefaultThreadSlowmode(json.getInt("default_thread_rate_limit_per_user", 0))).setSlowmode(json.getInt("rate_limit_per_user", 0));
   }

   protected void configureNewsChannel(DataObject json, NewsChannelMixin<?> channel) {
      ((NewsChannelMixin)((NewsChannelMixin)((NewsChannelMixin)((NewsChannelMixin)((NewsChannelMixin)channel.setParentCategory(json.getLong("parent_id", 0L))).setLatestMessageIdLong(json.getLong("last_message_id", 0L))).setName(json.getString("name"))).setTopic(json.getString("topic", (String)null))).setPosition(json.getInt("position"))).setNSFW(json.getBoolean("nsfw"));
   }

   protected void configureVoiceChannel(DataObject json, VoiceChannelMixin<?> channel) {
      ((VoiceChannelMixin)((VoiceChannelMixin)((VoiceChannelMixin)((VoiceChannelMixin)((VoiceChannelMixin)((VoiceChannelMixin)((VoiceChannelMixin)((VoiceChannelMixin)channel.setParentCategory(json.getLong("parent_id", 0L))).setLatestMessageIdLong(json.getLong("last_message_id", 0L))).setName(json.getString("name"))).setStatus(json.getString("status", "")).setPosition(json.getInt("position"))).setUserLimit(json.getInt("user_limit", 0))).setNSFW(json.getBoolean("nsfw"))).setBitrate(json.getInt("bitrate"))).setRegion(json.getString("rtc_region", (String)null))).setSlowmode(json.getInt("rate_limit_per_user", 0));
   }

   protected void configureStageChannel(DataObject json, StageChannelMixin<?> channel) {
      ((StageChannelMixin)((StageChannelMixin)((StageChannelMixin)((StageChannelMixin)((StageChannelMixin)((StageChannelMixin)((StageChannelMixin)((StageChannelMixin)channel.setParentCategory(json.getLong("parent_id", 0L))).setLatestMessageIdLong(json.getLong("last_message_id", 0L))).setName(json.getString("name"))).setPosition(json.getInt("position"))).setBitrate(json.getInt("bitrate"))).setUserLimit(json.getInt("user_limit", 0))).setNSFW(json.getBoolean("nsfw"))).setRegion(json.getString("rtc_region", (String)null))).setSlowmode(json.getInt("rate_limit_per_user", 0));
   }

   protected void configureThreadChannel(DataObject json, ThreadChannelMixin<?> channel) {
      DataObject threadMetadata = json.getObject("thread_metadata");
      ((ThreadChannelMixin)((ThreadChannelMixin)((ThreadChannelMixin)channel.setName(json.getString("name"))).setFlags(json.getInt("flags", 0)).setOwnerId(json.getLong("owner_id")).setMemberCount(json.getInt("member_count")).setMessageCount(json.getInt("message_count")).setTotalMessageCount(json.getInt("total_message_count", 0)).setLatestMessageIdLong(json.getLong("last_message_id", 0L))).setSlowmode(json.getInt("rate_limit_per_user", 0))).setLocked(threadMetadata.getBoolean("locked")).setArchived(threadMetadata.getBoolean("archived")).setInvitable(threadMetadata.getBoolean("invitable")).setArchiveTimestamp(Helpers.toTimestamp(threadMetadata.getString("archive_timestamp"))).setCreationTimestamp(threadMetadata.isNull("create_timestamp") ? 0L : Helpers.toTimestamp(threadMetadata.getString("create_timestamp"))).setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration.fromKey(threadMetadata.getInt("auto_archive_duration")));
   }

   protected void configureForumChannel(DataObject json, ForumChannelMixin<?> channel) {
      if (this.api.isCacheFlagSet(CacheFlag.FORUM_TAGS)) {
         json.optArray("available_tags").ifPresent((tags) -> {
            for(int i = 0; i < tags.length(); ++i) {
               this.createForumTag(channel, tags.getObject(i), i);
            }

         });
      }

      ((ForumChannelMixin)((ForumChannelMixin)((ForumChannelMixin)((ForumChannelMixin)((ForumChannelMixin)((ForumChannelMixin)((ForumChannelMixin)((ForumChannelMixin)((ForumChannelMixin)channel.setParentCategory(json.getLong("parent_id", 0L))).setFlags(json.getInt("flags", 0))).setDefaultReaction((DataObject)json.optObject("default_reaction_emoji").orElse((Object)null))).setDefaultSortOrder(json.getInt("default_sort_order", -1))).setDefaultLayout(json.getInt("default_forum_layout", -1)).setName(json.getString("name"))).setTopic(json.getString("topic", (String)null))).setPosition(json.getInt("position"))).setDefaultThreadSlowmode(json.getInt("default_thread_rate_limit_per_user", 0))).setSlowmode(json.getInt("rate_limit_per_user", 0))).setNSFW(json.getBoolean("nsfw"));
   }

   protected void configureMediaChannel(DataObject json, MediaChannelMixin<?> channel) {
      if (this.api.isCacheFlagSet(CacheFlag.FORUM_TAGS)) {
         json.optArray("available_tags").ifPresent((tags) -> {
            for(int i = 0; i < tags.length(); ++i) {
               this.createForumTag(channel, tags.getObject(i), i);
            }

         });
      }

      ((MediaChannelMixin)((MediaChannelMixin)((MediaChannelMixin)((MediaChannelMixin)((MediaChannelMixin)((MediaChannelMixin)((MediaChannelMixin)((MediaChannelMixin)((MediaChannelMixin)channel.setParentCategory(json.getLong("parent_id", 0L))).setFlags(json.getInt("flags", 0))).setDefaultReaction((DataObject)json.optObject("default_reaction_emoji").orElse((Object)null))).setDefaultSortOrder(json.getInt("default_sort_order", -1))).setName(json.getString("name"))).setTopic(json.getString("topic", (String)null))).setPosition(json.getInt("position"))).setDefaultThreadSlowmode(json.getInt("default_thread_rate_limit_per_user", 0))).setSlowmode(json.getInt("rate_limit_per_user", 0))).setNSFW(json.getBoolean("nsfw"));
   }

   public ForumTagImpl createForumTag(IPostContainerMixin<?> channel, DataObject json, int index) {
      long id = json.getUnsignedLong("id");
      SortedSnowflakeCacheViewImpl<ForumTag> cache = channel.getAvailableTagCache();
      ForumTagImpl tag = (ForumTagImpl)cache.get(id);
      if (tag == null) {
         UnlockHook lock = cache.writeLock();

         try {
            tag = new ForumTagImpl(id);
            cache.getMap().put(id, tag);
         } catch (Throwable var12) {
            if (lock != null) {
               try {
                  lock.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }
            }

            throw var12;
         }

         if (lock != null) {
            lock.close();
         }
      }

      tag.setName(json.getString("name")).setModerated(json.getBoolean("moderated")).setEmoji(json).setPosition(index);
      return tag;
   }

   protected void configurePrivateChannel(DataObject json, PrivateChannelMixin<?> channel) {
      channel.setLatestMessageIdLong(json.getLong("last_message_id", 0L));
   }

   protected void configureGroupChannel(DataObject json, GroupChannelMixin<?> channel) {
      ((GroupChannelMixin)((GroupChannelMixin)channel.setLatestMessageIdLong(json.getLong("last_message_id", 0L))).setName(json.getString("name", ""))).setOwnerId(json.getLong("owner_id")).setIcon(json.getString("icon", (String)null));
   }

   protected void configureMember(DataObject memberJson, MemberMixin<?> member) {
      member.setNickname(memberJson.getString("nick", (String)null));
      member.setAvatarId(memberJson.getString("avatar", (String)null));
      if (!memberJson.isNull("flags")) {
         member.setFlags(memberJson.getInt("flags"));
      }

      long boostTimestamp = memberJson.isNull("premium_since") ? 0L : Helpers.toTimestamp(memberJson.getString("premium_since"));
      member.setBoostDate(boostTimestamp);
      long timeOutTimestamp = memberJson.isNull("communication_disabled_until") ? 0L : Helpers.toTimestamp(memberJson.getString("communication_disabled_until"));
      member.setTimeOutEnd(timeOutTimestamp);
      if (!memberJson.isNull("pending")) {
         member.setPending(memberJson.getBoolean("pending"));
      }

      if (!memberJson.isNull("joined_at") && !member.hasTimeJoined()) {
         member.setJoinDate(Helpers.toTimestamp(memberJson.getString("joined_at")));
      }

   }

   protected void configureRole(DataObject roleJson, RoleMixin<?> role, long id) {
      RoleColors colors = createRoleColors(roleJson.getObject("colors"));
      role.setName(roleJson.getString("name")).setRawPosition(roleJson.getInt("position")).setRawPermissions(roleJson.getLong("permissions")).setManaged(roleJson.getBoolean("managed")).setHoisted(roleJson.getBoolean("hoist")).setPrimaryColor(colors.getPrimaryRaw()).setSecondaryColor(colors.getSecondaryRaw()).setTertiaryColor(colors.getTertiaryRaw()).setMentionable(roleJson.getBoolean("mentionable")).setTags((DataObject)roleJson.optObject("tags").orElseGet(DataObject::empty));
      String iconId = roleJson.getString("icon", (String)null);
      String emoji = roleJson.getString("unicode_emoji", (String)null);
      if (iconId == null && emoji == null) {
         role.setIcon((RoleIcon)null);
      } else {
         role.setIcon(new RoleIcon(iconId, emoji, id));
      }

   }

   public static RoleColors createRoleColors(DataObject colorsJson) {
      int primaryColor = colorsJson.getInt("primary_color");
      int secondaryColor = colorsJson.getInt("secondary_color", 536870911);
      int tertiaryColor = colorsJson.getInt("tertiary_color", 536870911);
      return new RoleColors(primaryColor == 0 ? 536870911 : primaryColor, secondaryColor, tertiaryColor);
   }
}
