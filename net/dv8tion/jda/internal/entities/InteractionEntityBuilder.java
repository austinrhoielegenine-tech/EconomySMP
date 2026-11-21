package net.dv8tion.jda.internal.entities;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.GroupChannel;
import net.dv8tion.jda.api.entities.channel.concrete.MediaChannel;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.PrivateChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.detached.DetachedCategoryImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.detached.DetachedForumChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.detached.DetachedGroupChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.detached.DetachedMediaChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.detached.DetachedNewsChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.detached.DetachedPrivateChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.detached.DetachedStageChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.detached.DetachedTextChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.detached.DetachedThreadChannelImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.detached.DetachedVoiceChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IInteractionPermissionMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.PrivateChannelMixin;
import net.dv8tion.jda.internal.entities.detached.DetachedGuildImpl;
import net.dv8tion.jda.internal.entities.detached.DetachedMemberImpl;
import net.dv8tion.jda.internal.entities.detached.DetachedRoleImpl;
import net.dv8tion.jda.internal.interactions.ChannelInteractionPermissions;
import net.dv8tion.jda.internal.interactions.MemberInteractionPermissions;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;

public class InteractionEntityBuilder extends AbstractEntityBuilder {
   private static final Logger LOG = JDALogger.getLog(InteractionEntityBuilder.class);
   private final EntityBuilder entityBuilder;
   private final long interactionChannelId;
   private final long interactionUserId;

   public InteractionEntityBuilder(JDAImpl api, long interactionChannelId, long interactionUserId) {
      super(api);
      this.interactionChannelId = interactionChannelId;
      this.interactionUserId = interactionUserId;
      this.entityBuilder = api.getEntityBuilder();
   }

   public Guild getOrCreateGuild(DataObject guildJson) {
      long guildId = guildJson.getUnsignedLong("id");
      Guild guild = this.api.getGuildById(guildId);
      if (guild != null) {
         return guild;
      } else {
         Optional<DataArray> featuresArray = guildJson.optArray("features");
         String locale = guildJson.getString("preferred_locale", "en-US");
         DetachedGuildImpl detachedGuild = new DetachedGuildImpl(this.api, guildId);
         detachedGuild.setLocale(DiscordLocale.from(locale));
         detachedGuild.setFeatures((Set)featuresArray.map((array) -> (Set)array.stream(DataArray::getString).map(String::intern).collect(Collectors.toSet())).orElse(Collections.emptySet()));
         return detachedGuild;
      }
   }

   public GroupChannel createGroupChannel(DataObject channelData) {
      long id = channelData.getLong("id");
      DetachedGroupChannelImpl channel = new DetachedGroupChannelImpl(this.api, id);
      this.configureGroupChannel(channelData, channel);
      return channel;
   }

   public GuildChannel createGuildChannel(@Nonnull Guild guild, DataObject channelData) {
      ChannelType channelType = ChannelType.fromId(channelData.getInt("type"));
      switch (channelType) {
         case TEXT:
            return this.createTextChannel(guild, channelData);
         case NEWS:
            return this.createNewsChannel(guild, channelData);
         case STAGE:
            return this.createStageChannel(guild, channelData);
         case VOICE:
            return this.createVoiceChannel(guild, channelData);
         case CATEGORY:
            return this.createCategory(guild, channelData);
         case FORUM:
            return this.createForumChannel(guild, channelData);
         case MEDIA:
            return this.createMediaChannel(guild, channelData);
         default:
            LOG.debug("Cannot create channel for type " + channelData.getInt("type"));
            return null;
      }
   }

   public Category createCategory(@Nonnull Guild guild, DataObject json) {
      if (!guild.isDetached()) {
         return guild.getCategoryById(json.getLong("id"));
      } else {
         long id = json.getLong("id");
         DetachedCategoryImpl channel = new DetachedCategoryImpl(id, (DetachedGuildImpl)guild);
         this.configureCategory(json, channel);
         this.configureChannelInteractionPermissions(channel, json);
         return channel;
      }
   }

   public TextChannel createTextChannel(@Nonnull Guild guild, DataObject json) {
      if (!guild.isDetached()) {
         return guild.getTextChannelById(json.getLong("id"));
      } else {
         long id = json.getLong("id");
         DetachedTextChannelImpl channel = new DetachedTextChannelImpl(id, (DetachedGuildImpl)guild);
         this.configureTextChannel(json, channel);
         this.configureChannelInteractionPermissions(channel, json);
         return channel;
      }
   }

   public NewsChannel createNewsChannel(@Nonnull Guild guild, DataObject json) {
      if (!guild.isDetached()) {
         return guild.getNewsChannelById(json.getLong("id"));
      } else {
         long id = json.getLong("id");
         DetachedNewsChannelImpl channel = new DetachedNewsChannelImpl(id, (DetachedGuildImpl)guild);
         this.configureNewsChannel(json, channel);
         this.configureChannelInteractionPermissions(channel, json);
         return channel;
      }
   }

   public VoiceChannel createVoiceChannel(@Nonnull Guild guild, DataObject json) {
      if (!guild.isDetached()) {
         return guild.getVoiceChannelById(json.getLong("id"));
      } else {
         long id = json.getLong("id");
         DetachedVoiceChannelImpl channel = new DetachedVoiceChannelImpl(id, (DetachedGuildImpl)guild);
         this.configureVoiceChannel(json, channel);
         this.configureChannelInteractionPermissions(channel, json);
         return channel;
      }
   }

   public StageChannel createStageChannel(@Nonnull Guild guild, DataObject json) {
      if (!guild.isDetached()) {
         return guild.getStageChannelById(json.getLong("id"));
      } else {
         long id = json.getLong("id");
         DetachedStageChannelImpl channel = new DetachedStageChannelImpl(id, (DetachedGuildImpl)guild);
         this.configureStageChannel(json, channel);
         this.configureChannelInteractionPermissions(channel, json);
         return channel;
      }
   }

   public MediaChannel createMediaChannel(@Nonnull Guild guild, DataObject json) {
      if (!guild.isDetached()) {
         return guild.getMediaChannelById(json.getLong("id"));
      } else {
         long id = json.getLong("id");
         DetachedMediaChannelImpl channel = new DetachedMediaChannelImpl(id, (DetachedGuildImpl)guild);
         this.configureMediaChannel(json, channel);
         this.configureChannelInteractionPermissions(channel, json);
         return channel;
      }
   }

   public ThreadChannel createThreadChannel(@Nonnull Guild guild, DataObject json) {
      if (!guild.isDetached()) {
         ThreadChannel threadChannel = guild.getThreadChannelById(json.getLong("id"));
         return threadChannel != null ? threadChannel : this.entityBuilder.createThreadChannel((GuildImpl)guild, json, guild.getIdLong(), false);
      } else {
         long id = json.getUnsignedLong("id");
         ChannelType type = ChannelType.fromId(json.getInt("type"));
         DetachedThreadChannelImpl channel = new DetachedThreadChannelImpl(id, (DetachedGuildImpl)guild, type);
         this.configureThreadChannel(json, channel);
         this.configureChannelInteractionPermissions(channel, json);
         return channel;
      }
   }

   public ForumChannel createForumChannel(@Nonnull Guild guild, DataObject json) {
      if (!guild.isDetached()) {
         return guild.getForumChannelById(json.getLong("id"));
      } else {
         long id = json.getLong("id");
         DetachedForumChannelImpl channel = new DetachedForumChannelImpl(id, (DetachedGuildImpl)guild);
         this.configureForumChannel(json, channel);
         this.configureChannelInteractionPermissions(channel, json);
         return channel;
      }
   }

   private void configureChannelInteractionPermissions(IInteractionPermissionMixin<?> channel, DataObject json) {
      channel.setInteractionPermissions(new ChannelInteractionPermissions(this.interactionUserId, json.getLong("permissions")));
   }

   public Member createMember(@Nonnull Guild guild, DataObject memberJson) {
      if (!guild.isDetached()) {
         return this.entityBuilder.createMember((GuildImpl)guild, memberJson);
      } else {
         User user = this.entityBuilder.createUser(memberJson.getObject("user"));
         DetachedMemberImpl member = new DetachedMemberImpl((DetachedGuildImpl)guild, user);
         this.configureMember(memberJson, member);
         if (memberJson.hasKey("permissions")) {
            member.setInteractionPermissions(new MemberInteractionPermissions(this.interactionChannelId, memberJson.getLong("permissions")));
         }

         return member;
      }
   }

   public Role createRole(@Nonnull Guild guild, DataObject roleJson) {
      if (!guild.isDetached()) {
         return guild.getRoleById(roleJson.getLong("id"));
      } else {
         long id = roleJson.getLong("id");
         DetachedRoleImpl role = new DetachedRoleImpl(id, (DetachedGuildImpl)guild);
         this.configureRole(roleJson, role, id);
         return role;
      }
   }

   public PrivateChannel createPrivateChannel(DataObject json, User interactionUser) {
      long channelId = json.getUnsignedLong("id");
      DataObject recipientObj = (DataObject)json.optArray("recipients").filter((d) -> !d.isEmpty()).map((d) -> d.getObject(0)).orElse((Object)null);
      PrivateChannelMixin<?> channel;
      if (recipientObj != null) {
         if (this.api.getSelfUser().getIdLong() == recipientObj.getLong("id")) {
            channel = new PrivateChannelImpl(this.getJDA(), channelId, interactionUser);
         } else {
            channel = new DetachedPrivateChannelImpl(this.getJDA(), channelId, this.entityBuilder.createUser(recipientObj));
         }
      } else {
         LOG.warn((String)"Private channel has no recipient and will fallback to a detached PrivateChannel with no user, please report to the devs, channel JSON: {}", (Object)json.toPrettyString());
         channel = new DetachedPrivateChannelImpl(this.getJDA(), channelId, (User)null);
      }

      this.configurePrivateChannel(json, channel);
      return channel;
   }
}
