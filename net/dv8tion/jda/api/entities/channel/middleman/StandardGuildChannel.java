package net.dv8tion.jda.api.entities.channel.middleman;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.ICopyableChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IInviteContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IMemberContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IPositionableChannel;
import net.dv8tion.jda.api.managers.channel.middleman.StandardGuildChannelManager;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public interface StandardGuildChannel extends GuildChannel, IPermissionContainer, IPositionableChannel, ICopyableChannel, IMemberContainer, IInviteContainer, ICategorizableChannel {
   @Nonnull
   @CheckReturnValue
   StandardGuildChannelManager<?, ?> getManager();

   @Nonnull
   @CheckReturnValue
   ChannelAction<? extends StandardGuildChannel> createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   ChannelAction<? extends StandardGuildChannel> createCopy();
}
