package net.dv8tion.jda.api.requests.restaction.interactions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.FluentRestAction;
import net.dv8tion.jda.api.utils.messages.MessageEditRequest;

public interface MessageEditCallbackAction extends InteractionCallbackAction<InteractionHook>, MessageEditRequest<MessageEditCallbackAction>, FluentRestAction<InteractionHook, MessageEditCallbackAction> {
   @Nonnull
   @CheckReturnValue
   MessageEditCallbackAction closeResources();
}
