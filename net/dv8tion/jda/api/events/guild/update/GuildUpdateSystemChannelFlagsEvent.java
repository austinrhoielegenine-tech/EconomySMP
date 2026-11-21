package net.dv8tion.jda.api.events.guild.update;

import java.util.Set;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.guild.SystemChannelFlag;

public class GuildUpdateSystemChannelFlagsEvent extends GenericGuildUpdateEvent<Set<SystemChannelFlag>> {
   public static final String IDENTIFIER = "system_channel_flags";

   public GuildUpdateSystemChannelFlagsEvent(@Nonnull JDA api, long responseNumber, @Nonnull Guild guild, @Nonnull Set<SystemChannelFlag> oldFlags, @Nonnull Set<SystemChannelFlag> newFlags) {
      super(api, responseNumber, guild, oldFlags, newFlags, "system_channel_flags");
   }

   @Nonnull
   public Set<SystemChannelFlag> getOldFlags() {
      return (Set)this.getOldValue();
   }

   @Nonnull
   public Set<SystemChannelFlag> getNewFlags() {
      return (Set)this.getNewValue();
   }
}
