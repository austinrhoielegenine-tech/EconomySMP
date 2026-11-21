package net.dv8tion.jda.api.utils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.Requester;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.requestbody.DataSupplierBody;
import net.dv8tion.jda.internal.utils.requestbody.TypedBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Okio;
import okio.Source;

public class FileUpload implements Closeable, AttachedFile {
   private final InputStream resource;
   private final Supplier<? extends Source> resourceSupplier;
   private String name;
   private TypedBody<?> body;
   private String description;
   private MediaType mediaType;
   private byte[] waveform;
   private double durationSeconds;

   protected FileUpload(InputStream resource, String name) {
      this.mediaType = Requester.MEDIA_TYPE_OCTET;
      this.resource = resource;
      this.resourceSupplier = null;
      this.name = name;
   }

   protected FileUpload(Supplier<? extends Source> resourceSupplier, String name) {
      this.mediaType = Requester.MEDIA_TYPE_OCTET;
      this.resourceSupplier = resourceSupplier;
      this.resource = null;
      this.name = name;
   }

   @Nonnull
   public static FileUpload fromStreamSupplier(@Nonnull String name, @Nonnull Supplier<? extends InputStream> supplier) {
      Checks.notNull(supplier, "Supplier");
      return fromSourceSupplier(name, () -> Okio.source((InputStream)supplier.get()));
   }

   @Nonnull
   public static FileUpload fromSourceSupplier(@Nonnull String name, @Nonnull Supplier<? extends Source> supplier) {
      Checks.notNull(supplier, "Supplier");
      Checks.notBlank(name, "Name");
      return new FileUpload(supplier, name);
   }

   @Nonnull
   public static FileUpload fromData(@Nonnull InputStream data, @Nonnull String name) {
      Checks.notNull(data, "Data");
      Checks.notBlank(name, "Name");
      return new FileUpload(data, name);
   }

   @Nonnull
   public static FileUpload fromData(@Nonnull byte[] data, @Nonnull String name) {
      Checks.notNull(data, "Data");
      Checks.notNull(name, "Name");
      return fromData((InputStream)(new ByteArrayInputStream(data)), (String)name);
   }

   @Nonnull
   public static FileUpload fromData(@Nonnull File file, @Nonnull String name) {
      Checks.notNull(file, "File");

      try {
         return fromData((InputStream)(new FileInputStream(file)), (String)name);
      } catch (FileNotFoundException e) {
         throw new UncheckedIOException(e);
      }
   }

   @Nonnull
   public static FileUpload fromData(@Nonnull File file) {
      Checks.notNull(file, "File");

      try {
         return fromData((InputStream)(new FileInputStream(file)), (String)file.getName());
      } catch (FileNotFoundException e) {
         throw new UncheckedIOException(e);
      }
   }

   @Nonnull
   public static FileUpload fromData(@Nonnull Path path, @Nonnull String name, @Nonnull OpenOption... options) {
      Checks.notNull(path, "Path");
      Checks.noneNull(options, "Options");
      Checks.check(Files.isReadable(path), "File for specified path cannot be read. Path: %s", (Object)path);

      try {
         return fromData(Files.newInputStream(path, options), name);
      } catch (IOException e) {
         throw new UncheckedIOException("Could not open file for specified path. Path: " + path, e);
      }
   }

   @Nonnull
   public static FileUpload fromData(@Nonnull Path path, @Nonnull OpenOption... options) {
      Checks.notNull(path, "Path");
      Path fileName = path.getFileName();
      Checks.check(fileName != null, "Path does not have a file name. Path: %s", (Object)path);
      return fromData(path, fileName.toString(), options);
   }

   @Nonnull
   public FileUpload asSpoiler() {
      return this.name.startsWith("SPOILER_") ? this : this.setName("SPOILER_" + this.name);
   }

   @Nonnull
   public FileUpload setName(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      this.name = name;
      return this;
   }

   @Nonnull
   public FileUpload setDescription(@Nullable String description) {
      if (description != null) {
         Checks.notLonger(description = description.trim(), 1024, "Description");
      }

      this.description = description;
      return this;
   }

   @Nonnull
   public FileUpload asVoiceMessage(@Nonnull MediaType mediaType, @Nonnull byte[] waveform, @Nonnull Duration duration) {
      Checks.notNull(duration, "Duration");
      return this.asVoiceMessage(mediaType, waveform, (double)duration.toNanos() / (double)1.0E9F);
   }

   @Nonnull
   public FileUpload asVoiceMessage(@Nonnull MediaType mediaType, @Nonnull byte[] waveform, double durationSeconds) {
      Checks.notNull(mediaType, "Media type");
      Checks.notNull(waveform, "Waveform");
      Checks.check(waveform.length > 0 && waveform.length <= 256, "Waveform must be between 1 and 256 bytes long");
      Checks.check(Double.isFinite(durationSeconds), "Duration must be a finite number");
      Checks.check(durationSeconds > (double)0.0F, "Duration must be positive");
      this.waveform = waveform;
      this.durationSeconds = durationSeconds;
      this.mediaType = mediaType;
      return this;
   }

   public boolean isVoiceMessage() {
      return this.mediaType.type().equals("audio") && this.durationSeconds > (double)0.0F && this.waveform != null && this.waveform.length > 0;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Nonnull
   public InputStream getData() {
      return this.resource != null ? this.resource : Okio.buffer((Source)this.resourceSupplier.get()).inputStream();
   }

   @Nonnull
   public synchronized RequestBody getRequestBody(@Nonnull MediaType type) {
      Checks.notNull(type, "Type");
      if (this.body != null) {
         return this.body.withType(type);
      } else {
         return this.resource == null ? (this.body = new DataSupplierBody(type, this.resourceSupplier)) : (this.body = IOUtil.createRequestBody(type, this.resource));
      }
   }

   public synchronized void addPart(@Nonnull MultipartBody.Builder builder, int index) {
      builder.addFormDataPart("files[" + index + "]", this.name, this.getRequestBody(this.mediaType));
   }

   @Nonnull
   public DataObject toAttachmentData(int index) {
      DataObject attachment = DataObject.empty().put("id", index).put("description", this.description == null ? "" : this.description).put("content_type", this.mediaType.toString()).put("filename", this.name);
      if (this.waveform != null && this.durationSeconds > (double)0.0F) {
         attachment.put("waveform", new String(Base64.getEncoder().encode(this.waveform), StandardCharsets.UTF_8));
         attachment.put("duration_secs", this.durationSeconds);
      }

      return attachment;
   }

   public synchronized void close() throws IOException {
      if (this.body == null) {
         this.forceClose();
      }

   }

   public void forceClose() throws IOException {
      if (this.resource != null) {
         this.resource.close();
      }

   }

   protected void finalize() {
      if (this.body == null && this.resource != null) {
         IOUtil.silentClose((Closeable)this.resource);
      }

   }

   public String toString() {
      return (new EntityString("AttachedFile")).setType("Data").setName(this.name).toString();
   }
}
