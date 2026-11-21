package net.dv8tion.jda.internal.entities;

import java.util.Collection;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageDeleteAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageCreateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageDeleteActionImpl;
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageEditActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public abstract class AbstractWebhookClient<T> implements WebhookClient<T> {
   protected final long id;
   protected final JDA api;
   protected String token;

   protected AbstractWebhookClient(long webhookId, String webhookToken, JDA api) {
      this.id = webhookId;
      this.token = webhookToken;
      this.api = api;
   }

   public long getIdLong() {
      return this.id;
   }

   public String getToken() {
      return this.token;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   public abstract WebhookMessageCreateActionImpl<T> sendRequest();

   public abstract WebhookMessageEditActionImpl<T> editRequest(String var1);

   @Nonnull
   public WebhookMessageCreateAction<T> sendMessage(@Nonnull String content) {
      return (WebhookMessageCreateAction)this.sendRequest().setContent(content);
   }

   @Nonnull
   public WebhookMessageCreateAction<T> sendMessageEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      return (WebhookMessageCreateAction)this.sendRequest().addEmbeds(embeds);
   }

   @Nonnull
   public WebhookMessageCreateAction<T> sendMessageComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      return (WebhookMessageCreateAction)this.sendRequest().setComponents(components);
   }

   @Nonnull
   public WebhookMessageCreateAction<T> sendMessage(@Nonnull MessageCreateData message) {
      return (WebhookMessageCreateAction)this.sendRequest().applyData(message);
   }

   @Nonnull
   public WebhookMessageCreateAction<T> sendMessagePoll(@Nonnull MessagePollData poll) {
      Checks.notNull(poll, "Message Poll");
      return (WebhookMessageCreateAction)this.sendRequest().setPoll(poll);
   }

   @Nonnull
   public WebhookMessageCreateAction<T> sendFiles(@Nonnull Collection<? extends FileUpload> files) {
      return (WebhookMessageCreateAction)this.sendRequest().addFiles(files);
   }

   @Nonnull
   public WebhookMessageEditActionImpl<T> editMessageById(@Nonnull String messageId, @Nonnull String content) {
      return (WebhookMessageEditActionImpl)this.editRequest(messageId).setContent(content);
   }

   @Nonnull
   public WebhookMessageEditAction<T> editMessageComponentsById(@Nonnull String messageId, @Nonnull Collection<? extends MessageTopLevelComponent> components) {
      Checks.noneNull(components, "Components");
      return (WebhookMessageEditAction)this.editRequest(messageId).setComponents(components);
   }

   @Nonnull
   public WebhookMessageEditActionImpl<T> editMessageEmbedsById(@Nonnull String messageId, @Nonnull Collection<? extends MessageEmbed> embeds) {
      return (WebhookMessageEditActionImpl)this.editRequest(messageId).setEmbeds(embeds);
   }

   @Nonnull
   public WebhookMessageEditActionImpl<T> editMessageById(@Nonnull String messageId, @Nonnull MessageEditData message) {
      return (WebhookMessageEditActionImpl)this.editRequest(messageId).applyData(message);
   }

   @Nonnull
   public WebhookMessageEditActionImpl<T> editMessageAttachmentsById(@Nonnull String messageId, @Nonnull Collection<? extends AttachedFile> attachments) {
      return (WebhookMessageEditActionImpl)this.editRequest(messageId).setAttachments(attachments);
   }

   @Nonnull
   public WebhookMessageDeleteAction deleteMessageById(@Nonnull String messageId) {
      if (!"@original".equals(messageId)) {
         Checks.isSnowflake(messageId);
      }

      Route.CompiledRoute route = Route.Webhooks.EXECUTE_WEBHOOK_DELETE.compile(Long.toUnsignedString(this.id), this.token, messageId);
      return new WebhookMessageDeleteActionImpl(this.api, route);
   }
}
