package net.dv8tion.jda.internal.utils.message;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.AbstractMessageBuilder;
import net.dv8tion.jda.api.utils.messages.MessageRequest;

public interface AbstractMessageBuilderMixin<R extends MessageRequest<R>, B extends AbstractMessageBuilder<?, B>> extends MessageRequest<R> {
   B getBuilder();

   @Nonnull
   default R setContent(@Nullable String content) {
      this.getBuilder().setContent(content);
      return (R)this;
   }

   @Nonnull
   default String getContent() {
      return this.getBuilder().getContent();
   }

   @Nonnull
   default R setEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
      this.getBuilder().setEmbeds(embeds);
      return (R)this;
   }

   @Nonnull
   default List<MessageEmbed> getEmbeds() {
      return this.getBuilder().getEmbeds();
   }

   @Nonnull
   default R setComponents(@Nonnull Collection<? extends MessageTopLevelComponent> components) {
      this.getBuilder().setComponents(components);
      return (R)this;
   }

   @Nonnull
   default R useComponentsV2(boolean use) {
      this.getBuilder().useComponentsV2(use);
      return (R)this;
   }

   @Nonnull
   default List<MessageTopLevelComponentUnion> getComponents() {
      return this.getBuilder().getComponents();
   }

   default boolean isUsingComponentsV2() {
      return this.getBuilder().isUsingComponentsV2();
   }

   @Nonnull
   default R setSuppressEmbeds(boolean suppress) {
      this.getBuilder().setSuppressEmbeds(suppress);
      return (R)this;
   }

   default boolean isSuppressEmbeds() {
      return this.getBuilder().isSuppressEmbeds();
   }

   @Nonnull
   default R setFiles(@Nullable Collection<? extends FileUpload> files) {
      this.getBuilder().setFiles(files);
      return (R)this;
   }

   @Nonnull
   default List<? extends AttachedFile> getAttachments() {
      return this.getBuilder().getAttachments();
   }

   @Nonnull
   default R mentionRepliedUser(boolean mention) {
      this.getBuilder().mentionRepliedUser(mention);
      return (R)this;
   }

   @Nonnull
   default R setAllowedMentions(@Nullable Collection<Message.MentionType> allowedMentions) {
      this.getBuilder().setAllowedMentions(allowedMentions);
      return (R)this;
   }

   @Nonnull
   default R mention(@Nonnull Collection<? extends IMentionable> mentions) {
      this.getBuilder().mention(mentions);
      return (R)this;
   }

   @Nonnull
   default R mentionUsers(@Nonnull Collection<String> userIds) {
      this.getBuilder().mentionUsers(userIds);
      return (R)this;
   }

   @Nonnull
   default R mentionRoles(@Nonnull Collection<String> roleIds) {
      this.getBuilder().mentionRoles(roleIds);
      return (R)this;
   }

   @Nonnull
   default Set<String> getMentionedUsers() {
      return this.getBuilder().getMentionedUsers();
   }

   @Nonnull
   default Set<String> getMentionedRoles() {
      return this.getBuilder().getMentionedRoles();
   }

   @Nonnull
   default EnumSet<Message.MentionType> getAllowedMentions() {
      return this.getBuilder().getAllowedMentions();
   }

   default boolean isMentionRepliedUser() {
      return this.getBuilder().isMentionRepliedUser();
   }
}
