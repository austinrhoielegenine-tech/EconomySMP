package net.dv8tion.jda.api.entities.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.Helpers;

public interface PrivateChannel extends MessageChannel {
   @Nullable
   User getUser();

   @Nonnull
   @CheckReturnValue
   RestAction<User> retrieveUser();

   @Nonnull
   String getName();

   @Nonnull
   default String getJumpUrl() {
      return Helpers.format("https://discord.com/channels/%s/%s", "@me", this.getId());
   }
}
