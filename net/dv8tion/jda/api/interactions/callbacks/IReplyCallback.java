package net.dv8tion.jda.api.interactions.callbacks;

import com.google.errorprone.annotations.FormatMethod;
import com.google.errorprone.annotations.FormatString;
import java.util.Collection;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyCallbackActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public interface IReplyCallback extends IDeferrableCallback {
   @Nonnull
   @CheckReturnValue
   ReplyCallbackAction deferReply();

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction deferReply(boolean ephemeral) {
      return this.deferReply().setEphemeral(ephemeral);
   }

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction reply(@Nonnull MessageCreateData message) {
      Checks.notNull(message, "Message");
      ReplyCallbackActionImpl action = (ReplyCallbackActionImpl)this.deferReply();
      return (ReplyCallbackAction)action.applyData(message);
   }

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction replyPoll(@Nonnull MessagePollData poll) {
      Checks.notNull(poll, "Message Poll");
      return (ReplyCallbackAction)this.deferReply().setPoll(poll);
   }

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction reply(@Nonnull String content) {
      Checks.notNull(content, "Content");
      return (ReplyCallbackAction)this.deferReply().setContent(content);
   }

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction replyEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      return (ReplyCallbackAction)this.deferReply().addEmbeds(embeds);
   }

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction replyEmbeds(@Nonnull MessageEmbed embed, @Nonnull MessageEmbed... embeds) {
      Checks.notNull(embed, "MessageEmbed");
      Checks.noneNull(embeds, "MessageEmbed");
      return (ReplyCallbackAction)((ReplyCallbackAction)this.deferReply().addEmbeds(new MessageEmbed[]{embed})).addEmbeds(embeds);
   }

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction replyComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      return (ReplyCallbackAction)this.deferReply().setComponents(components);
   }

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction replyComponents(@Nonnull MessageTopLevelComponent component, @Nonnull MessageTopLevelComponent... other) {
      Checks.notNull(component, "MessageTopLevelComponent");
      Checks.noneNull(other, "MessageTopLevelComponents");
      return this.replyComponents(Helpers.mergeVararg(component, other));
   }

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction replyComponents(@Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      Checks.notNull(tree, "ComponentTree");
      return this.replyComponents(tree.getComponents());
   }

   @Nonnull
   @CheckReturnValue
   @FormatMethod
   default ReplyCallbackAction replyFormat(@Nonnull @FormatString String format, @Nonnull Object... args) {
      Checks.notNull(format, "Format String");
      return this.reply(String.format(format, args));
   }

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction replyFiles(@Nonnull Collection<? extends FileUpload> files) {
      Checks.notEmpty(files, "File Collection");
      return (ReplyCallbackAction)this.deferReply().setFiles(files);
   }

   @Nonnull
   @CheckReturnValue
   default ReplyCallbackAction replyFiles(@Nonnull FileUpload... files) {
      Checks.notEmpty((Object[])files, "File Collection");
      Checks.noneNull(files, "FileUpload");
      return (ReplyCallbackAction)this.deferReply().setFiles(files);
   }
}
