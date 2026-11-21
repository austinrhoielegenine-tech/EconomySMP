package net.dv8tion.jda.api.interactions;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.interactions.response.InteractionCallbackResponse;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface InteractionHook extends WebhookClient<Message> {
   @Nonnull
   Interaction getInteraction();

   @Nonnull
   InteractionCallbackResponse getCallbackResponse();

   boolean hasCallbackResponse();

   long getExpirationTimestamp();

   default boolean isExpired() {
      return System.currentTimeMillis() > this.getExpirationTimestamp();
   }

   @Nonnull
   InteractionHook setEphemeral(boolean var1);

   @Nonnull
   JDA getJDA();

   @Nonnull
   @CheckReturnValue
   default RestAction<Message> retrieveOriginal() {
      return this.retrieveMessageById("@original");
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<Message> editOriginal(@Nonnull String content) {
      return this.editMessageById("@original", content);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<Message> editOriginalComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      return this.editMessageComponentsById("@original", components);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<Message> editOriginalComponents(@Nonnull MessageTopLevelComponent... components) {
      return this.editMessageComponentsById("@original", components);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<Message> editOriginalComponents(@Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      return this.editMessageComponentsById("@original", tree);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<Message> editOriginalEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      return this.editMessageEmbedsById("@original", embeds);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<Message> editOriginalEmbeds(@Nonnull MessageEmbed... embeds) {
      return this.editMessageEmbedsById("@original", embeds);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<Message> editOriginal(@Nonnull MessageEditData message) {
      return this.editMessageById("@original", message);
   }

   @Nonnull
   @CheckReturnValue
   @FormatMethod
   default WebhookMessageEditAction<Message> editOriginalFormat(@Nonnull @FormatString String format, @Nonnull Object... args) {
      Checks.notNull(format, "Format String");
      return this.editOriginal(String.format(format, args));
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<Message> editOriginalAttachments(@Nonnull Collection<? extends AttachedFile> attachments) {
      return this.editMessageAttachmentsById("@original", attachments);
   }

   @Nonnull
   @CheckReturnValue
   default WebhookMessageEditAction<Message> editOriginalAttachments(@Nonnull AttachedFile... attachments) {
      Checks.noneNull(attachments, "Attachments");
      return this.editOriginalAttachments(Arrays.asList(attachments));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Void> deleteOriginal() {
      return this.deleteMessageById("@original");
   }

   @Nonnull
   static InteractionHook from(@Nonnull JDA jda, @Nonnull String token) {
      Checks.notNull(jda, "JDA");
      Checks.notBlank(token, "Token");
      return new InteractionHookImpl(jda, token);
   }
}
