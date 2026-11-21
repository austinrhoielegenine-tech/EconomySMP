package net.dv8tion.jda.api.utils.messages;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.internal.utils.Checks;

public interface MessageEditRequest<R extends MessageEditRequest<R>> extends MessageRequest<R> {
   @Nonnull
   R setAttachments(@Nullable Collection<? extends AttachedFile> var1);

   @Nonnull
   default R setAttachments(@Nonnull AttachedFile... attachments) {
      Checks.noneNull(attachments, "Attachments");
      return (R)this.setAttachments(Arrays.asList(attachments));
   }

   @Nonnull
   default R setFiles(@Nullable Collection<? extends FileUpload> files) {
      return (R)this.setAttachments(files);
   }

   @Nonnull
   R setReplace(boolean var1);

   boolean isReplace();

   @Nonnull
   R applyData(@Nonnull MessageEditData var1);

   @Nonnull
   default R applyCreateData(@Nonnull MessageCreateData data) {
      return (R)((MessageEditRequest)((MessageEditRequest)((MessageEditRequest)((MessageEditRequest)((MessageEditRequest)((MessageEditRequest)((MessageEditRequest)((MessageEditRequest)this.setReplace(true).setContent(data.getContent())).setAllowedMentions(data.getAllowedMentions())).mentionUsers(data.getMentionedUsers())).mentionRoles(data.getMentionedRoles())).mentionRepliedUser(data.isMentionRepliedUser())).setComponents(data.getComponents())).useComponentsV2(data.isUsingComponentsV2())).setEmbeds(data.getEmbeds())).setFiles(data.getFiles());
   }

   @Nonnull
   default R applyMessage(@Nonnull Message message) {
      return (R)(MessageRequest.super.applyMessage(message)).setReplace(true);
   }
}
