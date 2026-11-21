package net.dv8tion.jda.internal.requests.restaction;

import java.util.function.Function;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.utils.message.MessageEditBuilderMixin;
import okhttp3.RequestBody;

public class WebhookMessageEditActionImpl<T> extends AbstractWebhookMessageActionImpl<T, WebhookMessageEditActionImpl<T>> implements WebhookMessageEditAction<T>, MessageEditBuilderMixin<WebhookMessageEditAction<T>> {
   private final Function<DataObject, T> transformer;
   private final MessageEditBuilder builder = new MessageEditBuilder();

   public WebhookMessageEditActionImpl(JDA api, Route.CompiledRoute route, Function<DataObject, T> transformer) {
      super(api, route);
      this.transformer = transformer;
   }

   public MessageEditBuilder getBuilder() {
      return this.builder;
   }

   protected RequestBody finalizeData() {
      MessageEditData data = this.builder.build();

      RequestBody var3;
      try {
         DataObject payload = data.toData();
         var3 = this.getMultipartBody(data.getAllDistinctFiles(), payload);
      } catch (Throwable var5) {
         if (data != null) {
            try {
               data.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (data != null) {
         data.close();
      }

      return var3;
   }

   protected Route.CompiledRoute finalizeRoute() {
      Route.CompiledRoute route = super.finalizeRoute();
      if (this.threadId != null) {
         route = route.withQueryParams("thread_id", this.threadId);
      }

      return route;
   }

   protected void handleSuccess(Response response, Request<T> request) {
      T message = (T)this.transformer.apply(response.getObject());
      request.onSuccess(message);
   }
}
