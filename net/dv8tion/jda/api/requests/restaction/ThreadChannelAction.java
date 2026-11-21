package net.dv8tion.jda.api.requests.restaction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public interface ThreadChannelAction extends AbstractThreadCreateAction<ThreadChannel, ThreadChannelAction>, FluentAuditableRestAction<ThreadChannel, ThreadChannelAction> {
   @Nonnull
   @CheckReturnValue
   ThreadChannelAction setInvitable(boolean var1);
}
