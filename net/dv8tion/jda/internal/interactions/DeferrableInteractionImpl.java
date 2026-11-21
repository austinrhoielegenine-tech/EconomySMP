package net.dv8tion.jda.internal.interactions;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.exceptions.InteractionFailureException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

public class DeferrableInteractionImpl extends InteractionImpl implements IDeferrableCallback {
   protected final InteractionHookImpl hook;

   public DeferrableInteractionImpl(JDAImpl jda, DataObject data) {
      super(jda, data);
      this.hook = new InteractionHookImpl(this, jda);
   }

   public synchronized void releaseHook(boolean success) {
      if (success) {
         this.hook.ready();
      } else {
         this.hook.fail(new InteractionFailureException());
      }

   }

   @Nonnull
   public InteractionHook getHook() {
      return this.hook;
   }
}
