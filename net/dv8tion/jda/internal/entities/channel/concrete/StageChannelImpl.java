package net.dv8tion.jda.internal.entities.channel.concrete;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.channel.concrete.StageChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.StageInstanceAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractStandardGuildChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.StageChannelMixin;
import net.dv8tion.jda.internal.managers.channel.concrete.StageChannelManagerImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.StageInstanceActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public class StageChannelImpl extends AbstractStandardGuildChannelImpl<StageChannelImpl> implements StageChannel, StageChannelMixin<StageChannelImpl> {
   private StageInstance instance;
   private String region;
   private int bitrate;
   private int userlimit;
   private int slowmode;
   private boolean ageRestricted;
   private long latestMessageId;

   public StageChannelImpl(long id, GuildImpl guild) {
      super(id, guild);
   }

   public boolean isDetached() {
      return false;
   }

   @Nonnull
   public GuildImpl getGuild() {
      return (GuildImpl)super.getGuild();
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.STAGE;
   }

   public int getBitrate() {
      return this.bitrate;
   }

   public int getUserLimit() {
      return this.userlimit;
   }

   @Nullable
   public String getRegionRaw() {
      return this.region;
   }

   @Nullable
   public StageInstance getStageInstance() {
      return this.instance;
   }

   @Nonnull
   public List<Member> getMembers() {
      return this.getGuild().getConnectedMembers(this);
   }

   @Nonnull
   public StageInstanceAction createStageInstance(@Nonnull String topic) {
      EnumSet<Permission> permissions = this.getGuild().getSelfMember().getPermissions(this);

      for(Permission perm : EnumSet.of(Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_MOVE_OTHERS)) {
         if (!permissions.contains(perm)) {
            throw new InsufficientPermissionException(this, perm, "You must be a stage moderator to create a stage instance! Missing Permission: " + perm);
         }
      }

      return (new StageInstanceActionImpl(this)).setTopic(topic);
   }

   public int getSlowmode() {
      return this.slowmode;
   }

   public boolean isNSFW() {
      return this.ageRestricted;
   }

   public boolean canTalk(@Nonnull Member member) {
      Checks.notNull(member, "Member");
      return member.hasPermission(this, new Permission[]{Permission.MESSAGE_SEND});
   }

   public long getLatestMessageIdLong() {
      return this.latestMessageId;
   }

   @Nonnull
   public StageChannelManager getManager() {
      return new StageChannelManagerImpl(this);
   }

   @Nonnull
   public RestAction<Void> requestToSpeak() {
      Guild guild = this.getGuild();
      Route.CompiledRoute route = Route.Guilds.UPDATE_VOICE_STATE.compile(guild.getId(), "@me");
      DataObject body = DataObject.empty().put("channel_id", this.getId());
      if (guild.getSelfMember().hasPermission(this, new Permission[]{Permission.VOICE_MUTE_OTHERS})) {
         body.putNull("request_to_speak_timestamp").put("suppress", false);
      } else {
         body.put("request_to_speak_timestamp", OffsetDateTime.now().toString());
      }

      if (!this.equals(guild.getSelfMember().getVoiceState().getChannel())) {
         throw new IllegalStateException("Cannot request to speak without being connected to the stage channel!");
      } else {
         return new RestActionImpl<Void>(this.getJDA(), route, body);
      }
   }

   @Nonnull
   public RestAction<Void> cancelRequestToSpeak() {
      Guild guild = this.getGuild();
      Route.CompiledRoute route = Route.Guilds.UPDATE_VOICE_STATE.compile(guild.getId(), "@me");
      DataObject body = DataObject.empty().putNull("request_to_speak_timestamp").put("suppress", true).put("channel_id", this.getId());
      if (!this.equals(guild.getSelfMember().getVoiceState().getChannel())) {
         throw new IllegalStateException("Cannot cancel request to speak without being connected to the stage channel!");
      } else {
         return new RestActionImpl<Void>(this.getJDA(), route, body);
      }
   }

   public StageChannelImpl setBitrate(int bitrate) {
      this.bitrate = bitrate;
      return this;
   }

   public StageChannelImpl setUserLimit(int userlimit) {
      this.userlimit = userlimit;
      return this;
   }

   public StageChannelImpl setRegion(String region) {
      this.region = region;
      return this;
   }

   public StageChannelImpl setStageInstance(StageInstance instance) {
      this.instance = instance;
      return this;
   }

   public StageChannelImpl setNSFW(boolean ageRestricted) {
      this.ageRestricted = ageRestricted;
      return this;
   }

   public StageChannelImpl setSlowmode(int slowmode) {
      this.slowmode = slowmode;
      return this;
   }

   public StageChannelImpl setLatestMessageIdLong(long latestMessageId) {
      this.latestMessageId = latestMessageId;
      return this;
   }
}
