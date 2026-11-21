package net.dv8tion.jda.internal.entities;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Widget;
import net.dv8tion.jda.api.utils.DiscordAssets;
import net.dv8tion.jda.api.utils.ImageFormat;
import net.dv8tion.jda.api.utils.ImageProxy;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.EntityString;

public class WidgetImpl implements Widget {
   private final boolean isAvailable;
   private final long id;
   private final String name;
   private final String invite;
   private final TLongObjectMap<VoiceChannelImpl> channels;
   private final TLongObjectMap<Widget.Member> members;

   public WidgetImpl(long guildId) {
      this.isAvailable = false;
      this.id = guildId;
      this.name = null;
      this.invite = null;
      this.channels = new TLongObjectHashMap<VoiceChannelImpl>();
      this.members = new TLongObjectHashMap<Widget.Member>();
   }

   public WidgetImpl(@Nonnull DataObject json) {
      String inviteCode = json.getString("instant_invite", (String)null);
      if (inviteCode != null) {
         inviteCode = inviteCode.substring(inviteCode.lastIndexOf("/") + 1);
      }

      this.isAvailable = true;
      this.id = json.getLong("id");
      this.name = json.getString("name");
      this.invite = inviteCode;
      this.channels = MiscUtil.<VoiceChannelImpl>newLongMap();
      this.members = MiscUtil.<Widget.Member>newLongMap();
      DataArray channelsJson = json.getArray("channels");

      for(int i = 0; i < channelsJson.length(); ++i) {
         DataObject channel = channelsJson.getObject(i);
         this.channels.put(channel.getLong("id"), new VoiceChannelImpl(channel, this));
      }

      DataArray membersJson = json.getArray("members");

      for(int i = 0; i < membersJson.length(); ++i) {
         DataObject memberJson = membersJson.getObject(i);
         MemberImpl member = new MemberImpl(memberJson, this);
         if (!memberJson.isNull("channel_id")) {
            VoiceChannelImpl channel = this.channels.get(memberJson.getLong("channel_id"));
            member.setVoiceState(new VoiceStateImpl(channel, memberJson.getBoolean("mute"), memberJson.getBoolean("deaf"), memberJson.getBoolean("suppress"), memberJson.getBoolean("self_mute"), memberJson.getBoolean("self_deaf"), member, this));
            channel.addMember(member);
         }

         this.members.put(member.getIdLong(), member);
      }

   }

   public boolean isAvailable() {
      return this.isAvailable;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public String getName() {
      this.checkAvailable();
      return this.name;
   }

   @Nullable
   public String getInviteCode() {
      this.checkAvailable();
      return this.invite;
   }

   @Nonnull
   public List<Widget.VoiceChannel> getVoiceChannels() {
      this.checkAvailable();
      return Collections.unmodifiableList(new ArrayList(this.channels.valueCollection()));
   }

   @Nullable
   public Widget.VoiceChannel getVoiceChannelById(@Nonnull String id) {
      this.checkAvailable();
      return this.channels.get(MiscUtil.parseSnowflake(id));
   }

   @Nullable
   public Widget.VoiceChannel getVoiceChannelById(long id) {
      this.checkAvailable();
      return this.channels.get(id);
   }

   @Nonnull
   public List<Widget.Member> getMembers() {
      this.checkAvailable();
      return Collections.unmodifiableList(new ArrayList(this.members.valueCollection()));
   }

   @Nullable
   public Widget.Member getMemberById(@Nonnull String id) {
      this.checkAvailable();
      return this.members.get(MiscUtil.parseSnowflake(id));
   }

   @Nullable
   public Widget.Member getMemberById(long id) {
      this.checkAvailable();
      return this.members.get(id);
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof WidgetImpl)) {
         return false;
      } else {
         WidgetImpl oWidget = (WidgetImpl)obj;
         return this == oWidget || this.id == oWidget.getIdLong();
      }
   }

   public String toString() {
      EntityString entityString = new EntityString(this);
      if (this.isAvailable()) {
         entityString.setName(this.getName());
      }

      return entityString.toString();
   }

   private void checkAvailable() {
      if (!this.isAvailable) {
         throw new IllegalStateException("The widget for this Guild is unavailable!");
      }
   }

   public static class MemberImpl implements Widget.Member {
      private final boolean bot;
      private final long id;
      private final String username;
      private final String discriminator;
      private final String avatar;
      private final String nickname;
      private final OnlineStatus status;
      private final Activity game;
      private final WidgetImpl widget;
      private Widget.VoiceState state;

      private MemberImpl(@Nonnull DataObject json, @Nonnull WidgetImpl widget) {
         this.widget = widget;
         this.bot = json.getBoolean("bot");
         this.id = json.getLong("id");
         this.username = json.getString("username");
         this.discriminator = json.getString("discriminator");
         this.avatar = json.getString("avatar", (String)null);
         this.nickname = json.getString("nick", (String)null);
         this.status = OnlineStatus.fromKey(json.getString("status"));
         this.game = json.isNull("game") ? null : EntityBuilder.createActivity(json.getObject("game"));
      }

      private void setVoiceState(Widget.VoiceState voiceState) {
         this.state = voiceState;
      }

      public boolean isBot() {
         return this.bot;
      }

      @Nonnull
      public String getName() {
         return this.username;
      }

      public long getIdLong() {
         return this.id;
      }

      @Nonnull
      public String getAsMention() {
         return "<@" + this.getId() + ">";
      }

      @Nonnull
      public String getDiscriminator() {
         return this.discriminator;
      }

      @Nullable
      public String getAvatarId() {
         return this.avatar;
      }

      @Nullable
      public String getAvatarUrl() {
         String avatarId = this.getAvatarId();
         return avatarId == null ? null : this.getAvatarUrl(avatarId.startsWith("a_") ? ImageFormat.ANIMATED_WEBP : ImageFormat.PNG);
      }

      @Nullable
      public String getAvatarUrl(@Nonnull ImageFormat format) {
         ImageProxy proxy = this.getAvatar(format);
         return proxy == null ? null : proxy.getUrl();
      }

      @Nullable
      public ImageProxy getAvatar() {
         String avatarUrl = this.getAvatarUrl();
         return avatarUrl == null ? null : new ImageProxy(avatarUrl);
      }

      @Nullable
      public ImageProxy getAvatar(@Nonnull ImageFormat format) {
         return DiscordAssets.userAvatar(format, this.getId(), this.avatar);
      }

      @Nonnull
      public String getDefaultAvatarId() {
         return String.valueOf(Integer.parseInt(this.getDiscriminator()) % 5);
      }

      @Nonnull
      public String getDefaultAvatarUrl() {
         return this.getDefaultAvatar().getUrl();
      }

      @Nonnull
      public ImageProxy getDefaultAvatar() {
         return DiscordAssets.userDefaultAvatar(ImageFormat.PNG, this.getDefaultAvatarId());
      }

      @Nonnull
      public String getEffectiveAvatarUrl() {
         String avatarUrl = this.getAvatarUrl();
         return avatarUrl == null ? this.getDefaultAvatarUrl() : avatarUrl;
      }

      @Nonnull
      public String getEffectiveAvatarUrl(@Nonnull ImageFormat preferredFormat) {
         String avatarUrl = this.getAvatarUrl(preferredFormat);
         return avatarUrl == null ? this.getDefaultAvatarUrl() : this.avatar;
      }

      @Nonnull
      public ImageProxy getEffectiveAvatar() {
         return new ImageProxy(this.getEffectiveAvatarUrl());
      }

      @Nonnull
      public ImageProxy getEffectiveAvatar(@Nonnull ImageFormat preferredFormat) {
         ImageProxy avatar = this.getAvatar(preferredFormat);
         return avatar == null ? this.getDefaultAvatar() : avatar;
      }

      @Nullable
      public String getNickname() {
         return this.nickname;
      }

      @Nonnull
      public String getEffectiveName() {
         return this.nickname == null ? this.username : this.nickname;
      }

      @Nonnull
      public OnlineStatus getOnlineStatus() {
         return this.status;
      }

      @Nullable
      public Activity getActivity() {
         return this.game;
      }

      @Nonnull
      public Widget.VoiceState getVoiceState() {
         return (Widget.VoiceState)(this.state == null ? new VoiceStateImpl(this, this.widget) : this.state);
      }

      @Nonnull
      public WidgetImpl getWidget() {
         return this.widget;
      }

      public int hashCode() {
         return (this.widget.getId() + ' ' + this.id).hashCode();
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Widget.Member)) {
            return false;
         } else {
            Widget.Member oMember = (Widget.Member)obj;
            return this == oMember || this.id == oMember.getIdLong() && this.widget.getIdLong() == oMember.getWidget().getIdLong();
         }
      }

      public String toString() {
         return (new EntityString(this)).setName(this.getName()).toString();
      }
   }

   public static class VoiceChannelImpl implements Widget.VoiceChannel {
      private final int position;
      private final long id;
      private final String name;
      private final List<Widget.Member> members;
      private final Widget widget;

      private VoiceChannelImpl(@Nonnull DataObject json, @Nonnull Widget widget) {
         this.widget = widget;
         this.position = json.getInt("position");
         this.id = json.getLong("id");
         this.name = json.getString("name");
         this.members = new ArrayList();
      }

      private void addMember(@Nonnull Widget.Member member) {
         this.members.add(member);
      }

      public int getPosition() {
         return this.position;
      }

      public long getIdLong() {
         return this.id;
      }

      @Nonnull
      public String getName() {
         return this.name;
      }

      @Nonnull
      public List<Widget.Member> getMembers() {
         return this.members;
      }

      @Nonnull
      public Widget getWidget() {
         return this.widget;
      }

      public int hashCode() {
         return Long.hashCode(this.id);
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Widget.VoiceChannel)) {
            return false;
         } else {
            Widget.VoiceChannel oVChannel = (Widget.VoiceChannel)obj;
            return this == oVChannel || this.id == oVChannel.getIdLong();
         }
      }

      public String toString() {
         return (new EntityString(this)).setName(this.getName()).toString();
      }
   }

   public static class VoiceStateImpl implements Widget.VoiceState {
      private final Widget.VoiceChannel channel;
      private final boolean muted;
      private final boolean deafened;
      private final boolean suppress;
      private final boolean selfMute;
      private final boolean selfDeaf;
      private final Widget.Member member;
      private final Widget widget;

      private VoiceStateImpl(@Nonnull Widget.Member member, @Nonnull Widget widget) {
         this((Widget.VoiceChannel)null, false, false, false, false, false, member, widget);
      }

      private VoiceStateImpl(@Nullable Widget.VoiceChannel channel, boolean muted, boolean deafened, boolean suppress, boolean selfMute, boolean selfDeaf, @Nonnull Widget.Member member, @Nonnull Widget widget) {
         this.channel = channel;
         this.muted = muted;
         this.deafened = deafened;
         this.suppress = suppress;
         this.selfMute = selfMute;
         this.selfDeaf = selfDeaf;
         this.member = member;
         this.widget = widget;
      }

      @Nullable
      public Widget.VoiceChannel getChannel() {
         return this.channel;
      }

      public boolean inVoiceChannel() {
         return this.channel != null;
      }

      public boolean isGuildMuted() {
         return this.muted;
      }

      public boolean isGuildDeafened() {
         return this.deafened;
      }

      public boolean isSuppressed() {
         return this.suppress;
      }

      public boolean isSelfMuted() {
         return this.selfMute;
      }

      public boolean isSelfDeafened() {
         return this.selfDeaf;
      }

      public boolean isMuted() {
         return this.selfMute || this.muted;
      }

      public boolean isDeafened() {
         return this.selfDeaf || this.deafened;
      }

      @Nonnull
      public Widget.Member getMember() {
         return this.member;
      }

      @Nonnull
      public Widget getWidget() {
         return this.widget;
      }

      public int hashCode() {
         return this.member.hashCode();
      }

      public boolean equals(Object obj) {
         if (!(obj instanceof Widget.VoiceState)) {
            return false;
         } else {
            Widget.VoiceState oState = (Widget.VoiceState)obj;
            return this == oState || this.member.equals(oState.getMember()) && this.widget.equals(oState.getWidget());
         }
      }

      public String toString() {
         return (new EntityString(this)).setName(this.widget.getName()).addMetadata("memberName", this.member.getEffectiveName()).toString();
      }
   }
}
