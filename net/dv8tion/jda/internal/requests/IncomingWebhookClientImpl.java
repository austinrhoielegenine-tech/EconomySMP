package net.dv8tion.jda.internal.requests;

import java.util.function.Function;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IncomingWebhookClient;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageDeleteAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageRetrieveAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.AbstractWebhookClient;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.ReceivedMessage;
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageCreateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageDeleteActionImpl;
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageEditActionImpl;
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageRetrieveActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public class IncomingWebhookClientImpl extends AbstractWebhookClient<Message> implements IncomingWebhookClient {
   public IncomingWebhookClientImpl(long webhookId, String webhookToken, JDA api) {
      super(webhookId, webhookToken, api);
   }

   public WebhookMessageCreateActionImpl<Message> sendRequest() {
      Route.CompiledRoute route = Route.Webhooks.EXECUTE_WEBHOOK.compile(Long.toUnsignedString(this.id), this.token);
      route = route.withQueryParams("wait", "true");
      route = route.withQueryParams("with_components", "true");
      WebhookMessageCreateActionImpl<Message> action = new WebhookMessageCreateActionImpl<Message>(this.api, route, this.builder());
      action.run();
      action.setInteraction(false);
      return action;
   }

   public WebhookMessageEditActionImpl<Message> editRequest(@Nonnull String messageId) {
      if (!"@original".equals(messageId)) {
         Checks.isSnowflake(messageId);
      }

      Route.CompiledRoute route = Route.Webhooks.EXECUTE_WEBHOOK_EDIT.compile(Long.toUnsignedString(this.id), this.token, messageId);
      route = route.withQueryParams("wait", "true");
      route = route.withQueryParams("with_components", "true");
      WebhookMessageEditActionImpl<Message> action = new WebhookMessageEditActionImpl<Message>(this.api, route, this.builder());
      action.run();
      return action;
   }

   @Nonnull
   public WebhookMessageRetrieveAction retrieveMessageById(@Nonnull String messageId) {
      if (!"@original".equals(messageId)) {
         Checks.isSnowflake(messageId);
      }

      Route.CompiledRoute route = Route.Webhooks.EXECUTE_WEBHOOK_FETCH.compile(Long.toUnsignedString(this.id), this.token, messageId);
      WebhookMessageRetrieveActionImpl action = new WebhookMessageRetrieveActionImpl(this.api, route, (response, request) -> (Message)this.builder().apply(response.getObject()));
      action.run();
      return action;
   }

   @Nonnull
   public WebhookMessageDeleteAction deleteMessageById(@Nonnull String messageId) {
      WebhookMessageDeleteActionImpl action = (WebhookMessageDeleteActionImpl)super.deleteMessageById(messageId);
      action.run();
      return action;
   }

   private Function<DataObject, Message> builder() {
      return (data) -> {
         JDAImpl jda = (JDAImpl)this.api;
         long channelId = data.getUnsignedLong("channel_id");
         MessageChannel channel = (MessageChannel)this.api.getChannelById(MessageChannel.class, channelId);
         EntityBuilder entityBuilder = jda.getEntityBuilder();
         ReceivedMessage message = entityBuilder.createMessageBestEffort(data, channel, (Guild)null);
         message.withHook(this);
         return message;
      };
   }
}
