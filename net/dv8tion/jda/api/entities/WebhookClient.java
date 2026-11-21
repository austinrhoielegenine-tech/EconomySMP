package net.dv8tion.jda.api.entities;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageDeleteAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageRetrieveAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import net.dv8tion.jda.internal.requests.IncomingWebhookClientImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public interface WebhookClient<T> extends ISnowflake {
   @Nullable
   String getToken();

   @Nonnull
   JDA getJDA();

   @Nonnull
   @CheckReturnValue
   WebhookMessageCreateAction<T> sendMessage(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   WebhookMessageCreateAction<T> sendMessage(@Nonnull MessageCreateData var1);

   @Nonnull
   @CheckReturnValue
   WebhookMessageCreateAction<T> sendMessagePoll(@Nonnull MessagePollData var1);

   @Nonnull
   @CheckReturnValue
   @FormatMethod
   default WebhookMessageCreateAction<T> sendMessageFormat(@Nonnull @FormatString String format, @Nonnull Object... args) {
      Checks.notNull(format, "Format String");
      return this.sendMessage(String.format(format, args));
   }

   @Nonnull
   @CheckReturnValue
   WebhookMessageCreateAction<T> sendMessageEmbeds(@Nonnull Collection<? extends MessageEmbed> var1);

   @Nonnull
   @CheckReturnValue
   default WebhookMessageCreateAction<T> sendMessageEmbeds(@Nonnull MessageEmbed embed, @Nonnull MessageEmbed... embeds) {
      Checks.notNull(embed, "MessageEmbeds");
      Checks.noneNull(embeds, "MessageEmbeds");
      List<MessageEmbed> embedList = new ArrayList();
      embedList.add(embed);
      Collections.addAll(embedList, embeds);
      return this.sendMessageEmbeds(embedList);
   }

   @Nonnull
   @CheckReturnValue
   WebhookMessageCreateAction<T> sendMessageComponents(@Nonnull Collection<? extends MessageTopLevelComponent> var1);

   @Nonnull
   @CheckReturnValue
   default WebhookMessageCreateAction<T> sendMessageComponents(@Nonnull MessageTopLevelComponent component, @Nonnull MessageTopLevelComponent... other) {
      Checks.notNull(component, "MessageTopLevelComponent");
      Checks.noneNull(other, "MessageTopLevelComponents");
      return this.sendMessageComponents(Helpers.mergeVararg(component, other));
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageCreateAction<T> sendMessageComponents(@Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return this.sendMessageComponents(tree.getComponents());
   }

   @Nonnull
   @CheckReturnValue
   WebhookMessageCreateAction<T> sendFiles(@Nonnull Collection<? extends FileUpload> var1);

   @Nonnull
   @CheckReturnValue
   default WebhookMessageCreateAction<T> sendFiles(@Nonnull FileUpload... files) {
      Checks.noneNull(files, "Files");
      Checks.notEmpty((Object[])files, "Files");
      return this.sendFiles(Arrays.asList(files));
   }

   @Nonnull
   @CheckReturnValue
   WebhookMessageEditAction<T> editMessageById(@Nonnull String var1, @Nonnull String var2);

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageById(long messageId, @Nonnull String content) {
      return this.editMessageById(Long.toUnsignedString(messageId), content);
   }

   @Nonnull
   @CheckReturnValue
   WebhookMessageEditAction<T> editMessageById(@Nonnull String var1, @Nonnull MessageEditData var2);

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageById(long messageId, @Nonnull MessageEditData message) {
      return this.editMessageById(Long.toUnsignedString(messageId), message);
   }

   @Nonnull
   @CheckReturnValue
   @FormatMethod
   default WebhookMessageEditAction<T> editMessageFormatById(@Nonnull String messageId, @Nonnull @FormatString String format, @Nonnull Object... args) {
      Checks.notNull(format, "Format String");
      return this.editMessageById(messageId, String.format(format, args));
   }

   @Nonnull
   @CheckReturnValue
   @FormatMethod
   default WebhookMessageEditAction<T> editMessageFormatById(long messageId, @Nonnull @FormatString String format, @Nonnull Object... args) {
      return this.editMessageFormatById(Long.toUnsignedString(messageId), format, args);
   }

   @Nonnull
   @CheckReturnValue
   WebhookMessageEditAction<T> editMessageEmbedsById(@Nonnull String var1, @Nonnull Collection<? extends MessageEmbed> var2);

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageEmbedsById(long messageId, @Nonnull Collection<? extends MessageEmbed> embeds) {
      return this.editMessageEmbedsById(Long.toUnsignedString(messageId), embeds);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageEmbedsById(@Nonnull String messageId, @Nonnull MessageEmbed... embeds) {
      Checks.noneNull(embeds, "MessageEmbeds");
      return this.editMessageEmbedsById(messageId, Arrays.asList(embeds));
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageEmbedsById(long messageId, @Nonnull MessageEmbed... embeds) {
      return this.editMessageEmbedsById(Long.toUnsignedString(messageId), embeds);
   }

   @Nonnull
   @CheckReturnValue
   WebhookMessageEditAction<T> editMessageComponentsById(@Nonnull String var1, @Nonnull Collection<? extends MessageTopLevelComponent> var2);

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageComponentsById(long messageId, @Nonnull Collection<? extends MessageTopLevelComponent> components) {
      return this.editMessageComponentsById(Long.toUnsignedString(messageId), components);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageComponentsById(@Nonnull String messageId, @Nonnull MessageTopLevelComponent... components) {
      Checks.noneNull(components, "MessageTopLevelComponents");
      return this.editMessageComponentsById(messageId, Arrays.asList(components));
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageComponentsById(long messageId, @Nonnull MessageTopLevelComponent... components) {
      return this.editMessageComponentsById(Long.toUnsignedString(messageId), components);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageComponentsById(@Nonnull String messageId, @Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return this.editMessageComponentsById(messageId, tree.getComponents());
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageComponentsById(long messageId, @Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return this.editMessageComponentsById(messageId, tree.getComponents());
   }

   @Nonnull
   @CheckReturnValue
   WebhookMessageEditAction<T> editMessageAttachmentsById(@Nonnull String var1, @Nonnull Collection<? extends AttachedFile> var2);

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageAttachmentsById(@Nonnull String messageId, @Nonnull AttachedFile... attachments) {
      Checks.noneNull(attachments, "Attachments");
      return this.editMessageAttachmentsById(messageId, Arrays.asList(attachments));
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageAttachmentsById(long messageId, @Nonnull Collection<? extends AttachedFile> attachments) {
      return this.editMessageAttachmentsById(Long.toUnsignedString(messageId), attachments);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<T> editMessageAttachmentsById(long messageId, @Nonnull AttachedFile... attachments) {
      return this.editMessageAttachmentsById(Long.toUnsignedString(messageId), attachments);
   }

   @Nonnull
   @CheckReturnValue
   WebhookMessageDeleteAction deleteMessageById(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default WebhookMessageDeleteAction deleteMessageById(long messageId) {
      return this.deleteMessageById(Long.toUnsignedString(messageId));
   }

   @Nonnull
   @CheckReturnValue
   WebhookMessageRetrieveAction retrieveMessageById(@Nonnull String var1);

   @Nonnull
   static IncomingWebhookClient createClient(@Nonnull JDA api, @Nonnull String url) {
      Checks.notNull(url, "URL");
      Matcher matcher = Webhook.WEBHOOK_URL.matcher(url);
      if (!matcher.matches()) {
         throw new IllegalArgumentException("Provided invalid webhook URL");
      } else {
         String id = matcher.group(1);
         String token = matcher.group(2);
         return createClient(api, id, token);
      }
   }

   @Nonnull
   static IncomingWebhookClient createClient(@Nonnull JDA api, @Nonnull String webhookId, @Nonnull String webhookToken) {
      Checks.notNull(api, "JDA");
      Checks.notBlank(webhookToken, "Token");
      return new IncomingWebhookClientImpl(MiscUtil.parseSnowflake(webhookId), webhookToken, api);
   }
}
