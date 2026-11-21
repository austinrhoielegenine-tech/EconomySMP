package net.dv8tion.jda.api.events.guild.update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class GuildUpdateSystemChannelEvent extends GenericGuildUpdateEvent<TextChannel> {
   public static final String IDENTIFIER = "system_channel";

   public GuildUpdateSystemChannelEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nullable TextChannel oldSystemChannel) {
      super(api, responseNumber, guild, oldSystemChannel, guild.getSystemChannel(), "system_channel");
   }

   @Nullable
   public TextChannel getOldSystemChannel() {
      return (TextChannel)this.getOldValue();
   }

   @Nullable
   public TextChannel getNewSystemChannel() {
      return (TextChannel)this.getNewValue();
   }
}
