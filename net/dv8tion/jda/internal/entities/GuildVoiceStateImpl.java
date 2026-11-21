package net.dv8tion.jda.internal.entities;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.CompletedRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class GuildVoiceStateImpl implements GuildVoiceState {
   private final JDA api;
   private Guild guild;
   private Member member;
   private AudioChannel connectedChannel;
   private String sessionId;
   private long requestToSpeak;
   private boolean selfMuted = false;
   private boolean selfDeafened = false;
   private boolean guildMuted = false;
   private boolean guildDeafened = false;
   private boolean suppressed = false;
   private boolean stream = false;
   private boolean video = false;

   public GuildVoiceStateImpl(Member member) {
      this.api = member.getJDA();
      this.guild = member.getGuild();
      this.member = member;
   }

   public boolean isSelfMuted() {
      return this.selfMuted;
   }

   public boolean isSelfDeafened() {
      return this.selfDeafened;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   public String getSessionId() {
      return this.sessionId;
   }

   public long getRequestToSpeak() {
      return this.requestToSpeak;
   }

   public OffsetDateTime getRequestToSpeakTimestamp() {
      return this.requestToSpeak == 0L ? null : Helpers.toOffset(this.requestToSpeak);
   }

   @Nonnull
   public RestAction<Void> approveSpeaker() {
      return this.update(false);
   }

   @Nonnull
   public RestAction<Void> declineSpeaker() {
      return this.update(true);
   }

   private RestAction<Void> update(boolean suppress) {
      if (this.connectedChannel instanceof StageChannel && suppress != this.isSuppressed()) {
         Member selfMember = this.getGuild().getSelfMember();
         boolean isSelf = selfMember.equals(this.member);
         if (!isSelf && !selfMember.hasPermission(this.connectedChannel, new Permission[]{Permission.VOICE_MUTE_OTHERS})) {
            throw new InsufficientPermissionException(this.connectedChannel, Permission.VOICE_MUTE_OTHERS);
         } else {
            Route.CompiledRoute route = Route.Guilds.UPDATE_VOICE_STATE.compile(this.guild.getId(), isSelf ? "@me" : this.getId());
            DataObject body = DataObject.empty().put("channel_id", this.connectedChannel.getId()).put("suppress", suppress);
            return new RestActionImpl<Void>(this.getJDA(), route, body);
         }
      } else {
         return new CompletedRestAction<Void>(this.api, (Throwable)null);
      }
   }

   @Nonnull
   public RestAction<Void> inviteSpeaker() {
      if (!(this.connectedChannel instanceof StageChannel)) {
         return new CompletedRestAction<Void>(this.api, (Throwable)null);
      } else if (!this.getGuild().getSelfMember().hasPermission(this.connectedChannel, new Permission[]{Permission.VOICE_MUTE_OTHERS})) {
         throw new InsufficientPermissionException(this.connectedChannel, Permission.VOICE_MUTE_OTHERS);
      } else {
         Route.CompiledRoute route = Route.Guilds.UPDATE_VOICE_STATE.compile(this.guild.getId(), this.getId());
         DataObject body = DataObject.empty().put("channel_id", this.connectedChannel.getId()).put("suppress", false).put("request_to_speak_timestamp", OffsetDateTime.now().toString());
         return new RestActionImpl<Void>(this.getJDA(), route, body);
      }
   }

   public boolean isMuted() {
      return this.isSelfMuted() || this.isGuildMuted();
   }

   public boolean isDeafened() {
      return this.isSelfDeafened() || this.isGuildDeafened();
   }

   public boolean isGuildMuted() {
      return this.guildMuted;
   }

   public boolean isGuildDeafened() {
      return this.guildDeafened;
   }

   public boolean isSuppressed() {
      return this.suppressed;
   }

   public boolean isStream() {
      return this.stream;
   }

   public boolean isSendingVideo() {
      return this.video;
   }

   public AudioChannelUnion getChannel() {
      return (AudioChannelUnion)this.connectedChannel;
   }

   @Nonnull
   public Guild getGuild() {
      Guild realGuild = this.api.getGuildById(this.guild.getIdLong());
      if (realGuild != null) {
         this.guild = realGuild;
      }

      return this.guild;
   }

   @Nonnull
   public Member getMember() {
      Member realMember = this.getGuild().getMemberById(this.member.getIdLong());
      if (realMember != null) {
         this.member = realMember;
      }

      return this.member;
   }

   public boolean inAudioChannel() {
      return this.getChannel() != null;
   }

   public long getIdLong() {
      return this.member.getIdLong();
   }

   public int hashCode() {
      return this.member.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof GuildVoiceState)) {
         return false;
      } else {
         GuildVoiceState oStatus = (GuildVoiceState)obj;
         return this.member.equals(oStatus.getMember());
      }
   }

   public String toString() {
      return (new EntityString(this)).addMetadata("member", this.getMember()).toString();
   }

   public void setMember(Member member) {
      this.member = member;
   }

   public GuildVoiceStateImpl updateConnectedChannel(AudioChannel connectedChannel) {
      this.connectedChannel = connectedChannel;
      ((GuildImpl)this.guild).handleVoiceStateUpdate(this);
      return this;
   }

   public GuildVoiceStateImpl setSessionId(String sessionId) {
      this.sessionId = sessionId;
      return this;
   }

   public GuildVoiceStateImpl setSelfMuted(boolean selfMuted) {
      this.selfMuted = selfMuted;
      return this;
   }

   public GuildVoiceStateImpl setSelfDeafened(boolean selfDeafened) {
      this.selfDeafened = selfDeafened;
      return this;
   }

   public GuildVoiceStateImpl setGuildMuted(boolean guildMuted) {
      this.guildMuted = guildMuted;
      return this;
   }

   public GuildVoiceStateImpl setGuildDeafened(boolean guildDeafened) {
      this.guildDeafened = guildDeafened;
      return this;
   }

   public GuildVoiceStateImpl setSuppressed(boolean suppressed) {
      this.suppressed = suppressed;
      return this;
   }

   public GuildVoiceStateImpl setStream(boolean stream) {
      this.stream = stream;
      return this;
   }

   public GuildVoiceStateImpl setVideo(boolean video) {
      this.video = video;
      return this;
   }

   public GuildVoiceStateImpl setRequestToSpeak(OffsetDateTime timestamp) {
      this.requestToSpeak = timestamp == null ? 0L : timestamp.toInstant().toEpochMilli();
      return this;
   }
}
