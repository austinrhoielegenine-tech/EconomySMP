package net.dv8tion.jda.api.components;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.AttachmentProxy;

public interface ResolvedMedia {
   @Nullable
   String getAttachmentId();

   @Nullable
   default Long getAttachmentIdLong() {
      String attachmentId = this.getAttachmentId();
      return attachmentId != null ? Long.parseUnsignedLong(attachmentId) : null;
   }

   @Nonnull
   String getUrl();

   @Nonnull
   String getProxyUrl();

   @Nonnull
   AttachmentProxy getProxy();

   int getWidth();

   int getHeight();

   @Nullable
   String getContentType();
}
