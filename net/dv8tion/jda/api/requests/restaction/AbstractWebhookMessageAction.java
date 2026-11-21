package net.dv8tion.jda.api.requests.restaction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.FluentRestAction;

public interface AbstractWebhookMessageAction<T, R extends AbstractWebhookMessageAction<T, R>> extends FluentRestAction<T, R> {
   @Nonnull
   @CheckReturnValue
   R setThreadId(@Nullable String var1);

   @Nonnull
   @CheckReturnValue
   default R setThreadId(long threadId) {
      return (R)this.setThreadId(threadId == 0L ? null : Long.toUnsignedString(threadId));
   }

   @Nonnull
   @CheckReturnValue
   default R setThread(@Nullable ThreadChannel channel) {
      return (R)this.setThreadId(channel == null ? null : channel.getId());
   }
}
