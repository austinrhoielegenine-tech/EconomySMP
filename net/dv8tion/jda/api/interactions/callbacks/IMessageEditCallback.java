package net.dv8tion.jda.api.interactions.callbacks;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.requests.restaction.interactions.MessageEditCallbackActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface IMessageEditCallback extends IDeferrableCallback {
   @Nonnull
   @CheckReturnValue
   MessageEditCallbackAction deferEdit();

   @Nonnull
   @CheckReturnValue
   default MessageEditCallbackAction editMessage(@Nonnull MessageEditData message) {
      Checks.notNull(message, "Message");
      MessageEditCallbackActionImpl action = (MessageEditCallbackActionImpl)this.deferEdit();
      return (MessageEditCallbackAction)action.applyData(message);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditCallbackAction editMessage(@Nonnull String content) {
      Checks.notNull(content, "Content");
      return (MessageEditCallbackAction)this.deferEdit().setContent(content);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditCallbackAction editComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      Checks.noneNull(components, "Components");
      return (MessageEditCallbackAction)this.deferEdit().setComponents(components);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditCallbackAction editComponents(@Nonnull MessageTopLevelComponent... components) {
      Checks.noneNull(components, "components");
      return this.editComponents(Arrays.asList(components));
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditCallbackAction editComponents(@Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return this.editComponents(tree.getComponents());
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditCallbackAction editMessageEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      Checks.noneNull(embeds, "MessageEmbed");
      return (MessageEditCallbackAction)this.deferEdit().setEmbeds(embeds);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditCallbackAction editMessageEmbeds(@Nonnull MessageEmbed... embeds) {
      Checks.noneNull(embeds, "MessageEmbed");
      return (MessageEditCallbackAction)this.deferEdit().setEmbeds(embeds);
   }

   @Nonnull
   @CheckReturnValue
   @FormatMethod
   default MessageEditCallbackAction editMessageFormat(@Nonnull @FormatString String format, @Nonnull Object... args) {
      Checks.notNull(format, "Format String");
      return this.editMessage(String.format(format, args));
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditCallbackAction editMessageAttachments(@Nonnull Collection<? extends AttachedFile> attachments) {
      Checks.noneNull(attachments, "Attachments");
      return (MessageEditCallbackAction)this.deferEdit().setAttachments(attachments);
   }

   @Nonnull
   @CheckReturnValue
   default MessageEditCallbackAction editMessageAttachments(@Nonnull AttachedFile... attachments) {
      Checks.noneNull(attachments, "Attachments");
      return (MessageEditCallbackAction)this.deferEdit().setAttachments(attachments);
   }
}
