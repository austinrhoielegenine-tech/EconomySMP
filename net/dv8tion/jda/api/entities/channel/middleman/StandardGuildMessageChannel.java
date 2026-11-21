package net.dv8tion.jda.api.entities.channel.middleman;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.attribute.IAgeRestrictedChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.managers.channel.middleman.StandardGuildMessageChannelManager;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

public interface StandardGuildMessageChannel extends StandardGuildChannel, GuildMessageChannel, IThreadContainer, IWebhookContainer, IAgeRestrictedChannel {
   int MAX_TOPIC_LENGTH = 1024;

   @Nonnull
   @CheckReturnValue
   StandardGuildMessageChannelManager<?, ?> getManager();

   @Nullable
   String getTopic();

   @Nonnull
   @CheckReturnValue
   ChannelAction<? extends StandardGuildMessageChannel> createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   ChannelAction<? extends StandardGuildMessageChannel> createCopy();
}
