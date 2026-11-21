package net.dv8tion.jda.api.entities.channel.middleman;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.managers.channel.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.utils.Helpers;

public interface GuildChannel extends Channel, Comparable<GuildChannel> {
   String JUMP_URL = "https://discord.com/channels/%s/%s";

   @Nonnull
   Guild getGuild();

   @Nonnull
   @CheckReturnValue
   ChannelManager<?, ?> getManager();

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> delete();

   @Nonnull
   IPermissionContainer getPermissionContainer();

   @Nonnull
   default String getJumpUrl() {
      return Helpers.format("https://discord.com/channels/%s/%s", this.getGuild().getId(), this.getId());
   }
}
