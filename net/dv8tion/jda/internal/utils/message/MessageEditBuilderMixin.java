package net.dv8tion.jda.internal.utils.message;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.api.utils.messages.MessageEditRequest;

public interface MessageEditBuilderMixin<R extends MessageEditRequest<R>> extends AbstractMessageBuilderMixin<R, MessageEditBuilder>, MessageEditRequest<R> {
   @Nonnull
   default R setAttachments(@Nullable Collection<? extends AttachedFile> attachments) {
      ((MessageEditBuilder)this.getBuilder()).setAttachments(attachments);
      return (R)this;
   }

   @Nonnull
   default R setReplace(boolean isReplace) {
      ((MessageEditBuilder)this.getBuilder()).setReplace(isReplace);
      return (R)this;
   }

   @Nonnull
   default R setFiles(@Nullable Collection<? extends FileUpload> files) {
      ((MessageEditBuilder)this.getBuilder()).setFiles(files);
      return (R)this;
   }

   @Nonnull
   default R applyData(@Nonnull MessageEditData data) {
      ((MessageEditBuilder)this.getBuilder()).applyData(data);
      return (R)this;
   }

   default boolean isReplace() {
      return ((MessageEditBuilder)this.getBuilder()).isReplace();
   }
}
