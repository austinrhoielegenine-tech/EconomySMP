package net.dv8tion.jda.api.events.channel;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.VoiceChannelEffect;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class VoiceChannelEffectSendEvent extends GenericChannelEvent {
   private final VoiceChannelEffect effect;

   public VoiceChannelEffectSendEvent(@Nonnull JDA api, long responseNumber, VoiceChannelEffect effect) {
      super(api, responseNumber, effect.getChannel());
      this.effect = effect;
   }

   @Nonnull
   public VoiceChannel getVoiceChannel() {
      return this.effect.getChannel();
   }

   @Nonnull
   public VoiceChannelEffect getEffect() {
      return this.effect;
   }
}
