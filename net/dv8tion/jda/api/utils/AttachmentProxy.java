package net.dv8tion.jda.api.utils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.FutureUtil;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.OkHttpClient;

public class AttachmentProxy extends FileProxy {
   public AttachmentProxy(@Nonnull String url) {
      super(url);
   }

   @Nonnull
   public AttachmentProxy withClient(@Nonnull OkHttpClient customHttpClient) {
      return (AttachmentProxy)super.withClient(customHttpClient);
   }

   @Nonnull
   public String getUrl(int width, int height) {
      Checks.positive(width, "Image width");
      Checks.positive(height, "Image height");
      return IOUtil.addQuery(this.getUrl(), "width", width, "height", height);
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<InputStream> download(int width, int height) {
      return this.download(this.getUrl(width, height));
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<Path> downloadToPath(int width, int height) {
      return this.downloadToPath(this.getUrl(width, height));
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<File> downloadToFile(@Nonnull File file, int width, int height) {
      Checks.notNull(file, "File");
      CompletableFuture<Path> downloadToPathFuture = this.downloadToPath(this.getUrl(width, height), file.toPath());
      return FutureUtil.thenApplyCancellable(downloadToPathFuture, Path::toFile);
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<Path> downloadToPath(@Nonnull Path path, int width, int height) {
      Checks.notNull(path, "Path");
      return this.downloadToPath(this.getUrl(width, height), path);
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<Icon> downloadAsIcon() {
      return this.downloadAsIcon(this.getUrl());
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<Icon> downloadAsIcon(int width, int height) {
      return this.downloadAsIcon(this.getUrl(width, height));
   }

   @Nonnull
   public FileUpload downloadAsFileUpload(@Nonnull String name, int width, int height) {
      String url = this.getUrl(width, height);
      return FileUpload.fromStreamSupplier(name, () -> (InputStream)this.download(url).join());
   }
}
