package net.dv8tion.jda.api.requests.restaction.interactions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.FluentRestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;

public interface ReplyCallbackAction extends InteractionCallbackAction<InteractionHook>, MessageCreateRequest<ReplyCallbackAction>, FluentRestAction<InteractionHook, ReplyCallbackAction> {
   @Nonnull
   @CheckReturnValue
   ReplyCallbackAction closeResources();

   @Nonnull
   @CheckReturnValue
   ReplyCallbackAction setEphemeral(boolean var1);
}
