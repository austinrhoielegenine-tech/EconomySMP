package net.dv8tion.jda.internal.requests.restaction;

import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.ReceivedMessage;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.message.MessageEditBuilderMixin;
import okhttp3.RequestBody;

public class MessageEditActionImpl extends RestActionImpl<Message> implements MessageEditAction, MessageEditBuilderMixin<MessageEditAction> {
   private final String messageId;
   private final Guild guild;
   private final MessageChannel channel;
   private final MessageEditBuilder builder = new MessageEditBuilder();
   private WebhookClient<Message> webhook;
   private String threadId;

   public MessageEditActionImpl(@Nonnull JDA jda, @Nullable Guild guild, @Nonnull String channelId, @Nonnull String messageId) {
      super(jda, Route.Messages.EDIT_MESSAGE.compile(channelId, messageId));
      this.channel = null;
      this.guild = guild;
      this.messageId = messageId;
   }

   public MessageEditActionImpl(@Nonnull MessageChannel channel, @Nonnull String messageId) {
      super(channel.getJDA(), Route.Messages.EDIT_MESSAGE.compile(channel.getId(), messageId));
      this.channel = channel;
      this.guild = channel instanceof GuildChannel ? ((GuildChannel)channel).getGuild() : null;
      this.messageId = messageId;
   }

   public MessageEditActionImpl withHook(WebhookClient<Message> hook, ChannelType channelType, long channelId) {
      this.webhook = hook;
      if (!(hook instanceof InteractionHook) && channelType.isThread()) {
         this.threadId = Long.toUnsignedString(channelId);
      }

      return this;
   }

   public MessageEditBuilder getBuilder() {
      return this.builder;
   }

   protected Route.CompiledRoute finalizeRoute() {
      if (this.webhook != null && (!(this.webhook instanceof InteractionHook) || !((InteractionHook)this.webhook).isExpired())) {
         Route.CompiledRoute route = Route.Webhooks.EXECUTE_WEBHOOK_EDIT.compile(this.webhook.getId(), this.webhook.getToken(), this.messageId);
         if (this.threadId != null) {
            route = route.withQueryParams("thread_id", this.threadId);
         }

         return route;
      } else {
         return super.finalizeRoute();
      }
   }

   protected RequestBody finalizeData() {
      MessageEditData data = this.builder.build();

      RequestBody var2;
      try {
         var2 = this.getMultipartBody(data.getAllDistinctFiles(), data.toData());
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

      return var2;
   }

   protected void handleSuccess(Response response, Request<Message> request) {
      EntityBuilder entityBuilder = this.api.getEntityBuilder();
      DataObject json = response.getObject();
      ReceivedMessage message = entityBuilder.createMessageBestEffort(json, this.channel, this.guild);
      request.onSuccess(message.withHook(this.webhook));
   }

   @Nonnull
   public MessageEditAction setCheck(BooleanSupplier checks) {
      return (MessageEditAction)super.setCheck(checks);
   }

   @Nonnull
   public MessageEditAction deadline(long timestamp) {
      return (MessageEditAction)super.deadline(timestamp);
   }
}
