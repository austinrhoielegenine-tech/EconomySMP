package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.entities.channel.VoiceChannelEffect;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.channel.VoiceChannelEffectSendEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class VoiceChannelEffectSendHandler extends SocketHandler {
   public VoiceChannelEffectSendHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getLong("guild_id");
      if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
         if (guild == null) {
            this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            return null;
         } else {
            long channelId = content.getUnsignedLong("channel_id");
            VoiceChannel channel = guild.getVoiceChannelById(channelId);
            if (channel == null) {
               this.getJDA().getEventCache().cache(EventCache.Type.CHANNEL, channelId, this.responseNumber, this.allContent, this::handle);
               return null;
            } else {
               long userId = content.getUnsignedLong("user_id");
               EmojiUnion emoji = (EmojiUnion)content.optObject("emoji").map(EntityBuilder::createEmoji).orElse((Object)null);
               VoiceChannelEffect.Animation animation = (VoiceChannelEffect.Animation)content.opt("animation_type").map((rawAnimationType) -> ((Number)rawAnimationType).intValue()).map((rawAnimationType) -> {
                  long animationId = content.getUnsignedLong("animation_id");
                  VoiceChannelEffect.Animation.Type type = VoiceChannelEffect.Animation.Type.fromValue(rawAnimationType);
                  return new VoiceChannelEffect.Animation(animationId, type);
               }).orElse((Object)null);
               SoundboardSound soundboardSound = (SoundboardSound)content.opt("sound_id").map((id) -> guild.getSoundboardSoundById((String)id)).orElse((Object)null);
               double soundVolume = content.getDouble("sound_volume", (double)0.0F);
               VoiceChannelEffect effect = new VoiceChannelEffect(channel, userId, emoji, animation, soundboardSound, soundVolume);
               this.api.handleEvent(new VoiceChannelEffectSendEvent(this.api, this.responseNumber, effect));
               return null;
            }
         }
      }
   }
}
