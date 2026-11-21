package net.dv8tion.jda.api.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Requester;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface AttachedFile extends Closeable {
   int MAX_DESCRIPTION_LENGTH = 1024;

   @Nonnull
   static FileUpload fromData(@Nonnull InputStream data, @Nonnull String name) {
      return FileUpload.fromData(data, name);
   }

   @Nonnull
   static FileUpload fromData(@Nonnull byte[] data, @Nonnull String name) {
      return FileUpload.fromData(data, name);
   }

   @Nonnull
   static FileUpload fromData(@Nonnull File file, @Nonnull String name) {
      return FileUpload.fromData(file, name);
   }

   @Nonnull
   static FileUpload fromData(@Nonnull File file) {
      return FileUpload.fromData(file);
   }

   @Nonnull
   static FileUpload fromData(@Nonnull Path path, @Nonnull String name, @Nonnull OpenOption... options) {
      return FileUpload.fromData(path, name, options);
   }

   @Nonnull
   static FileUpload fromData(@Nonnull Path path, @Nonnull OpenOption... options) {
      return FileUpload.fromData(path, options);
   }

   @Nonnull
   static AttachmentUpdate fromAttachment(long id) {
      return AttachmentUpdate.fromAttachment(id);
   }

   @Nonnull
   static AttachmentUpdate fromAttachment(@Nonnull String id) {
      return AttachmentUpdate.fromAttachment(id);
   }

   @Nonnull
   static AttachmentUpdate fromAttachment(@Nonnull Message.Attachment attachment) {
      return AttachmentUpdate.fromAttachment(attachment);
   }

   void addPart(@Nonnull MultipartBody.Builder var1, int var2);

   @Nonnull
   DataObject toAttachmentData(int var1);

   @Nonnull
   static MultipartBody.Builder createMultipartBody(@Nonnull Collection<? extends AttachedFile> files) {
      return createMultipartBody(files, (RequestBody)null);
   }

   @Nonnull
   static MultipartBody.Builder createMultipartBody(@Nonnull Collection<? extends AttachedFile> files, @Nullable DataObject payloadJson) {
      RequestBody body = payloadJson != null ? RequestBody.create(payloadJson.toJson(), Requester.MEDIA_TYPE_JSON) : null;
      return createMultipartBody(files, body);
   }

   @Nonnull
   static MultipartBody.Builder createMultipartBody(@Nonnull Collection<? extends AttachedFile> files, @Nullable RequestBody payloadJson) {
      MultipartBody.Builder builder = (new MultipartBody.Builder()).setType(MultipartBody.FORM);
      int i = 0;

      for(AttachedFile file : files) {
         file.addPart(builder, i++);
      }

      if (payloadJson != null) {
         builder.addFormDataPart("payload_json", (String)null, payloadJson);
      }

      return builder;
   }

   void forceClose() throws IOException;
}
