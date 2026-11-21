package net.dv8tion.jda.api.entities.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.attribute.IAgeRestrictedChannel;
import net.dv8tion.jda.api.entities.channel.attribute.ISlowmodeChannel;
import net.dv8tion.jda.api.entities.channel.attribute.ISoundboardSoundChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IVoiceStatusChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public interface VoiceChannel extends StandardGuildChannel, GuildMessageChannel, AudioChannel, IWebhookContainer, IAgeRestrictedChannel, ISlowmodeChannel, IVoiceStatusChannel, ISoundboardSoundChannel {
   int MAX_USERLIMIT = 99;

   @Nonnull
   @CheckReturnValue
   ChannelAction<VoiceChannel> createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<VoiceChannel> createCopy() {
      return this.createCopy(this.getGuild());
   }

   @Nonnull
   @CheckReturnValue
   VoiceChannelManager getManager();
}
