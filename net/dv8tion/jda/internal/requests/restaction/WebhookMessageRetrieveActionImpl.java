package net.dv8tion.jda.internal.requests.restaction;

import java.util.function.BiFunction;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageRetrieveAction;

public class WebhookMessageRetrieveActionImpl extends AbstractWebhookMessageActionImpl<Message, WebhookMessageRetrieveActionImpl> implements WebhookMessageRetrieveAction {
   public WebhookMessageRetrieveActionImpl(JDA api, Route.CompiledRoute route, BiFunction<Response, Request<Message>, Message> handler) {
      super(api, route, handler);
   }

   protected Route.CompiledRoute finalizeRoute() {
      Route.CompiledRoute route = super.finalizeRoute();
      if (this.threadId != null) {
         route = route.withQueryParams("thread_id", this.threadId);
      }

      return route;
   }
}
