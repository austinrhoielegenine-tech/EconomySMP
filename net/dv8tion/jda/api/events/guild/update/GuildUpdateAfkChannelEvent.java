package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class GuildUpdateAfkChannelEvent extends GenericGuildUpdateEvent<VoiceChannel> {
   public static final String IDENTIFIER = "afk_channel";

   public GuildUpdateAfkChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable VoiceChannel oldAfkChannel) {
      super(api, responseNumber, guild, oldAfkChannel, guild.getAfkChannel(), "afk_channel");
   }

   @Nullable
   public VoiceChannel getOldAfkChannel() {
      return (VoiceChannel)this.getOldValue();
   }

   @Nullable
   public VoiceChannel getNewAfkChannel() {
      return (VoiceChannel)this.getNewValue();
   }
}
