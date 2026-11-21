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

public class ImageProxy extends FileProxy {
   public ImageProxy(@Nonnull String url) {
      super(url);
   }

   @Nonnull
   public ImageProxy withClient(@Nonnull OkHttpClient customHttpClient) {
      return (ImageProxy)super.withClient(customHttpClient);
   }

   @Nonnull
   public String getUrl(int size) {
      Checks.positive(size, "Image size");
      return IOUtil.addQuery(this.getUrl(), "size", size);
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<InputStream> download(int size) {
      return this.download(this.getUrl(size));
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<Path> downloadToPath(int size) {
      return this.downloadToPath(this.getUrl(size));
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<File> downloadToFile(@Nonnull File file, int size) {
      Checks.notNull(file, "File");
      CompletableFuture<Path> downloadToPathFuture = this.downloadToPath(this.getUrl(size), file.toPath());
      return FutureUtil.thenApplyCancellable(downloadToPathFuture, Path::toFile);
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<Path> downloadToPath(@Nonnull Path path, int size) {
      Checks.notNull(path, "Path");
      return this.downloadToPath(this.getUrl(size), path);
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<Icon> downloadAsIcon() {
      return this.downloadAsIcon(this.getUrl());
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<Icon> downloadAsIcon(int size) {
      return this.downloadAsIcon(this.getUrl(size));
   }

   @Nonnull
   public FileUpload downloadAsFileUpload(@Nonnull String name, int size) {
      String url = this.getUrl(size);
      return FileUpload.fromStreamSupplier(name, () -> (InputStream)this.download(url).join());
   }
}
