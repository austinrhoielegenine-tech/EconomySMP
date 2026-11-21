package net.dv8tion.jda.api.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.exceptions.HttpException;
import net.dv8tion.jda.api.requests.RestConfig;
import net.dv8tion.jda.internal.requests.FunctionalCallback;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.FutureUtil;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FileProxy {
   private static volatile OkHttpClient defaultHttpClient;
   private final String url;
   private OkHttpClient customHttpClient;

   public FileProxy(@Nonnull String url) {
      Checks.notNull(url, "URL");
      this.url = url;
   }

   public static void setDefaultHttpClient(@Nonnull OkHttpClient httpClient) {
      Checks.notNull(httpClient, "Default OkHttpClient");
      defaultHttpClient = httpClient;
   }

   @Nonnull
   public String getUrl() {
      return this.url;
   }

   @Nonnull
   public FileProxy withClient(@Nonnull OkHttpClient customHttpClient) {
      Checks.notNull(customHttpClient, "Custom HTTP client");
      this.customHttpClient = customHttpClient;
      return this;
   }

   protected OkHttpClient getHttpClient() {
      if (this.customHttpClient != null) {
         return this.customHttpClient;
      } else {
         if (defaultHttpClient == null) {
            defaultHttpClient = new OkHttpClient();
         }

         return defaultHttpClient;
      }
   }

   protected Request getRequest(String url) {
      return (new Request.Builder()).url(url).addHeader("user-agent", RestConfig.USER_AGENT).addHeader("accept-encoding", "gzip, deflate").build();
   }

   @Nonnull
   @CheckReturnValue
   protected CompletableFuture<InputStream> download(String url) {
      DownloadTask downloadTask = this.downloadInternal(url);
      CompletableFuture var10000 = downloadTask.getFuture();
      Function var10001 = Function.identity();
      Objects.requireNonNull(downloadTask);
      return FutureUtil.thenApplyCancellable(var10000, var10001, downloadTask::cancelCall);
   }

   private DownloadTask downloadInternal(String url) {
      CompletableFuture<InputStream> future = new CompletableFuture();
      Request req = this.getRequest(url);
      OkHttpClient httpClient = this.getHttpClient();
      Call newCall = httpClient.newCall(req);
      newCall.enqueue(FunctionalCallback.onFailure((call, e) -> future.completeExceptionally(new UncheckedIOException(e))).onSuccess((call, response) -> {
         if (response.isSuccessful()) {
            InputStream body = IOUtil.getBody(response);
            if (!future.complete(body)) {
               IOUtil.silentClose((Closeable)response);
            }
         } else {
            future.completeExceptionally(new HttpException(response.code() + ": " + response.message()));
            IOUtil.silentClose((Closeable)response);
         }

      }).build());
      return new DownloadTask(newCall, future);
   }

   @Nonnull
   @CheckReturnValue
   protected CompletableFuture<Icon> downloadAsIcon(String url) {
      CompletableFuture<InputStream> downloadFuture = this.download(url);
      return FutureUtil.thenApplyCancellable(downloadFuture, (stream) -> {
         try {
            InputStream ignored = stream;

            Icon var2;
            try {
               var2 = Icon.from(stream);
            } catch (Throwable var5) {
               if (stream != null) {
                  try {
                     ignored.close();
                  } catch (Throwable x2) {
                     var5.addSuppressed(x2);
                  }
               }

               throw var5;
            }

            if (stream != null) {
               stream.close();
            }

            return var2;
         } catch (IOException e) {
            throw new UncheckedIOException(e);
         }
      });
   }

   @Nonnull
   @CheckReturnValue
   protected CompletableFuture<Path> downloadToPath(String url) {
      HttpUrl parsedUrl = HttpUrl.parse(url);
      Checks.check(parsedUrl != null, "URL '%s' is invalid", (Object)url);
      List<String> segments = parsedUrl.pathSegments();
      String fileName = (String)segments.get(segments.size() - 1);
      return this.downloadToPath(Paths.get(fileName));
   }

   @Nonnull
   @CheckReturnValue
   protected CompletableFuture<Path> downloadToPath(String url, Path path) {
      Path absolute = path.toAbsolutePath();
      Path parent = absolute.getParent();
      Checks.check(parent != null && Files.exists(parent, new LinkOption[0]), "Parent folder of the file '%s' does not exist.", (Object)absolute);
      if (Files.exists(absolute, new LinkOption[0])) {
         Checks.check(Files.isRegularFile(absolute, new LinkOption[0]), "Path '%s' is not a regular file.", (Object)absolute);
         Checks.check(Files.isWritable(absolute), "File at '%s' is not writable.", (Object)absolute);
      }

      DownloadTask downloadTask = this.downloadInternal(url);
      CompletableFuture var10000 = downloadTask.getFuture();
      Function var10001 = (stream) -> {
         Path var3;
         try {
            Path tmpPath = Files.createTempFile(absolute.getFileName().toString(), ".part");
            Files.copy(stream, tmpPath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            Files.move(tmpPath, absolute, StandardCopyOption.REPLACE_EXISTING);
            var3 = absolute;
         } catch (IOException e) {
            throw new UncheckedIOException(e);
         } finally {
            IOUtil.silentClose((Closeable)stream);
         }

         return var3;
      };
      Objects.requireNonNull(downloadTask);
      return FutureUtil.thenApplyCancellable(var10000, var10001, downloadTask::cancelCall);
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<InputStream> download() {
      return this.download(this.url);
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<Path> downloadToPath() {
      return this.downloadToPath(this.url);
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<File> downloadToFile(@Nonnull File file) {
      Checks.notNull(file, "File");
      CompletableFuture<Path> downloadToPathFuture = this.downloadToPath(this.url, file.toPath());
      return FutureUtil.thenApplyCancellable(downloadToPathFuture, Path::toFile);
   }

   @Nonnull
   @CheckReturnValue
   public CompletableFuture<Path> downloadToPath(@Nonnull Path path) {
      Checks.notNull(path, "Path");
      return this.downloadToPath(this.url, path);
   }

   @Nonnull
   public FileUpload downloadAsFileUpload(@Nonnull String name) {
      return FileUpload.fromStreamSupplier(name, () -> (InputStream)this.download().join());
   }

   protected static class DownloadTask {
      private final Call call;
      private final CompletableFuture<InputStream> future;

      public DownloadTask(Call call, CompletableFuture<InputStream> future) {
         this.call = call;
         this.future = future;
      }

      protected void cancelCall() {
         this.call.cancel();
      }

      @Nonnull
      @CheckReturnValue
      protected CompletableFuture<InputStream> getFuture() {
         return this.future;
      }
   }
}
