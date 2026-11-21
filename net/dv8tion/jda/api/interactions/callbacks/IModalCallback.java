package net.dv8tion.jda.api.interactions.callbacks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;

public interface IModalCallback extends Interaction {
   @Nonnull
   @CheckReturnValue
   ModalCallbackAction replyModal(@Nonnull Modal var1);
}
