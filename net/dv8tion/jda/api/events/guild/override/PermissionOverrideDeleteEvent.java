package net.dv8tion.jda.api.events.guild.override;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;

public class PermissionOverrideDeleteEvent extends GenericPermissionOverrideEvent {
   public PermissionOverrideDeleteEvent(@Nonnull JDA api, long responseNumber, @Nonnull IPermissionContainer channel, @Nonnull PermissionOverride override) {
      super(api, responseNumber, channel, override);
   }
}
