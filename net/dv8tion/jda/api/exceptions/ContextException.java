package net.dv8tion.jda.api.exceptions;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Helpers;

public class ContextException extends Exception {
   @Nonnull
   public static Consumer<Throwable> herePrintingTrace() {
      return here(Throwable::printStackTrace);
   }

   @Nonnull
   public static Consumer<Throwable> here(@Nonnull Consumer<? super Throwable> acceptor) {
      return new ContextConsumer(new ContextException(), acceptor);
   }

   public static class ContextConsumer implements Consumer<Throwable> {
      private final ContextException context;
      private final Consumer<? super Throwable> callback;

      private ContextConsumer(ContextException context, Consumer<? super Throwable> callback) {
         this.context = context;
         this.callback = callback;
      }

      public void accept(Throwable throwable) {
         if (this.callback != null) {
            this.callback.accept(Helpers.appendCause(throwable, this.context));
         }

      }
   }
}
