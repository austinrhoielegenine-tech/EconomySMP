package net.dv8tion.jda.api.requests.restaction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.RestAction;

public interface AbstractThreadCreateAction<T, R extends AbstractThreadCreateAction<T, R>> extends RestAction<T> {
   @Nonnull
   Guild getGuild();

   @Nonnull
   ChannelType getType();

   @Nonnull
   @CheckReturnValue
   R setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   R setAutoArchiveDuration(@Nonnull ThreadChannel.AutoArchiveDuration var1);

   @Nonnull
   @CheckReturnValue
   R setSlowmode(int var1);
}
