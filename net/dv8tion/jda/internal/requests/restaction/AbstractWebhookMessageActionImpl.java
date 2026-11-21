package net.dv8tion.jda.internal.requests.restaction;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.internal.utils.Checks;

public abstract class AbstractWebhookMessageActionImpl<T, R extends AbstractWebhookMessageActionImpl<T, R>> extends TriggerRestAction<T> {
   protected String threadId;

   public AbstractWebhookMessageActionImpl(JDA api, Route.CompiledRoute route) {
      super(api, route);
   }

   public AbstractWebhookMessageActionImpl(JDA api, Route.CompiledRoute route, BiFunction<Response, Request<T>, T> handler) {
      super(api, route, handler);
   }

   @Nonnull
   public R setThreadId(@Nullable String threadId) {
      if (threadId != null) {
         Checks.isSnowflake(threadId, "Thread ID");
      }

      this.threadId = threadId;
      return (R)this;
   }

   @Nonnull
   public R setCheck(BooleanSupplier checks) {
      return (R)(super.setCheck(checks));
   }

   @Nonnull
   public R deadline(long timestamp) {
      return (R)(super.deadline(timestamp));
   }
}
