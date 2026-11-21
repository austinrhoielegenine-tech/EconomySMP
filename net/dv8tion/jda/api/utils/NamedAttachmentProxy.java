package net.dv8tion.jda.api.utils;

import javax.annotation.Nonnull;

public class NamedAttachmentProxy extends AttachmentProxy {
   private final String fileName;

   public NamedAttachmentProxy(@Nonnull String url, @Nonnull String fileName) {
      super(url);
      this.fileName = fileName;
   }

   @Nonnull
   public String getFileName() {
      return this.fileName;
   }

   @Nonnull
   public FileUpload downloadAsFileUpload(int width, int height) {
      return this.downloadAsFileUpload(this.fileName, width, height);
   }
}
