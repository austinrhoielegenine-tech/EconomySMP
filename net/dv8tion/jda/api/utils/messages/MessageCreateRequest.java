package net.dv8tion.jda.api.utils.messages;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.MessageTopLevelComponent;
import net.dv8tion.jda.api.components.tree.ComponentTree;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.utils.Checks;

public interface MessageCreateRequest<R extends MessageCreateRequest<R>> extends MessageRequest<R> {
   @Nonnull
   R addContent(@Nonnull String var1);

   @Nonnull
   R addEmbeds(@Nonnull Collection<? extends MessageEmbed> var1);

   @Nonnull
   default R addEmbeds(@Nonnull MessageEmbed... embeds) {
      return (R)this.addEmbeds(Arrays.asList(embeds));
   }

   @Nonnull
   R addComponents(@Nonnull Collection<? extends MessageTopLevelComponent> var1);

   @Nonnull
   default R addComponents(@Nonnull MessageTopLevelComponent... components) {
      return (R)this.addComponents(Arrays.asList(components));
   }

   @Nonnull
   default R addComponents(@Nonnull ComponentTree<? extends MessageTopLevelComponent> tree) {
      return (R)this.addComponents(tree.getComponents());
   }

   @Nonnull
   R addFiles(@Nonnull Collection<? extends FileUpload> var1);

   @Nonnull
   default R addFiles(@Nonnull FileUpload... files) {
      return (R)this.addFiles(Arrays.asList(files));
   }

   @Nonnull
   List<FileUpload> getAttachments();

   @Nullable
   MessagePollData getPoll();

   @Nonnull
   R setPoll(@Nullable MessagePollData var1);

   @Nonnull
   R setTTS(boolean var1);

   @Nonnull
   R setSuppressedNotifications(boolean var1);

   @Nonnull
   R setVoiceMessage(boolean var1);

   @Nonnull
   default R applyData(@Nonnull MessageCreateData data) {
      Checks.notNull(data, "MessageCreateData");
      return (R)(((MessageCreateRequest)((MessageCreateRequest)((MessageCreateRequest)((MessageCreateRequest)((MessageCreateRequest)((MessageCreateRequest)((MessageCreateRequest)((MessageCreateRequest)((MessageCreateRequest)this.setContent(data.getContent())).setAllowedMentions(data.getAllowedMentions())).mentionUsers(data.getMentionedUsers())).mentionRoles(data.getMentionedRoles())).mentionRepliedUser(data.isMentionRepliedUser())).setComponents(data.getComponents())).useComponentsV2(data.isUsingComponentsV2())).setEmbeds(data.getEmbeds())).setTTS(data.isTTS()).setSuppressEmbeds(data.isSuppressEmbeds())).setSuppressedNotifications(data.isSuppressedNotifications()).setVoiceMessage(data.isVoiceMessage()).setPoll(data.getPoll()).setFiles(data.getFiles()));
   }

   @Nonnull
   default R applyMessage(@Nonnull Message message) {
      return (R)(MessageRequest.super.applyMessage(message)).setTTS(message.isTTS()).setSuppressedNotifications(message.isSuppressedNotifications()).setVoiceMessage(message.isVoiceMessage()).setPoll(message.getPoll() != null ? MessagePollData.from(message.getPoll()) : null);
   }

   @Nonnull
   default R applyEditData(@Nonnull MessageEditData data) {
      Checks.notNull(data, "MessageEditData");
      if (data.isSet(1)) {
         this.setContent(data.getContent());
      }

      if (data.isSet(2)) {
         this.setEmbeds(data.getEmbeds());
      }

      if (data.isSet(4)) {
         this.setComponents(data.getComponents());
         this.useComponentsV2(data.isUsingComponentsV2());
      }

      if (data.isSet(8)) {
         this.setFiles(data.getFiles());
      }

      if (data.isSet(16)) {
         this.setAllowedMentions(data.getAllowedMentions());
         this.mentionUsers(data.getMentionedUsers());
         this.mentionRoles(data.getMentionedRoles());
         this.mentionRepliedUser(data.isMentionRepliedUser());
      }

      return (R)this;
   }
}
