package net.dv8tion.jda.internal.entities.channel.mixin.concrete;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.SoundboardSoundSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IAgeRestrictedChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ISlowmodeChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IWebhookContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.AudioChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildMessageChannelMixin;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface VoiceChannelMixin<T extends VoiceChannelMixin<T>> extends VoiceChannel, GuildMessageChannelMixin<T>, AudioChannelMixin<T>, IWebhookContainerMixin<T>, IAgeRestrictedChannelMixin<T>, ISlowmodeChannelMixin<T> {
   default boolean canTalk(@Nonnull Member member) {
      Checks.notNull(member, "Member");
      return member.hasPermission(this, new Permission[]{Permission.MESSAGE_SEND});
   }

   @Nonnull
   default ChannelAction<VoiceChannel> createCopy(@Nonnull Guild guild) {
      Checks.notNull(guild, "Guild");
      ChannelAction<VoiceChannel> action = guild.createVoiceChannel(this.getName()).setBitrate(this.getBitrate()).setUserlimit(this.getUserLimit());
      if (this.getRegionRaw() != null) {
         action.setRegion(Region.fromKey(this.getRegionRaw()));
      }

      if (guild.equals(this.getGuild())) {
         Category parent = this.getParentCategory();
         if (parent != null) {
            action.setParent(parent);
         }

         for(PermissionOverride o : this.getPermissionOverrideMap().valueCollection()) {
            if (o.isMemberOverride()) {
               action.addMemberPermissionOverride(o.getIdLong(), o.getAllowedRaw(), o.getDeniedRaw());
            } else {
               action.addRolePermissionOverride(o.getIdLong(), o.getAllowedRaw(), o.getDeniedRaw());
            }
         }
      }

      return action;
   }

   @Nonnull
   default RestAction<Void> sendSoundboardSound(@Nonnull SoundboardSoundSnowflake sound, @Nullable String sourceGuildId) {
      Checks.notNull(sound, "Sound");
      if (sourceGuildId != null) {
         Checks.isSnowflake(sourceGuildId, "Source guild ID");
      }

      Guild targetGuild = this.getGuild();
      if (!targetGuild.getSelfMember().hasPermission(this, new Permission[]{Permission.VOICE_SPEAK})) {
         throw new InsufficientPermissionException(this, Permission.VOICE_SPEAK);
      } else if (!targetGuild.getSelfMember().hasPermission(this, new Permission[]{Permission.VOICE_USE_SOUNDBOARD})) {
         throw new InsufficientPermissionException(this, Permission.VOICE_USE_SOUNDBOARD);
      } else {
         GuildVoiceState voiceState = targetGuild.getSelfMember().getVoiceState();
         if (voiceState != null) {
            if (!this.equals(voiceState.getChannel())) {
               throw new IllegalStateException("You must be connected to the voice channel you want to send the sound effect to");
            }

            if (voiceState.isSuppressed()) {
               throw new IllegalStateException("You cannot send sound effects while you are being suppressed");
            }

            if (voiceState.isDeafened()) {
               throw new IllegalStateException("You cannot send sound effects while you are deafened");
            }

            if (voiceState.isMuted()) {
               throw new IllegalStateException("You cannot send sound effects while you are muted");
            }
         }

         DataObject data = DataObject.empty().put("sound_id", this.getId());
         if (sourceGuildId != null) {
            data.put("source_guild_id", sourceGuildId);
         }

         return new RestActionImpl<Void>(this.getJDA(), Route.SoundboardSounds.SEND_SOUNDBOARD_SOUND.compile(this.getId()), data);
      }
   }

   T setStatus(String var1);
}
