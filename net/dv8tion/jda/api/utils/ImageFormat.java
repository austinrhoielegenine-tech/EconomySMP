package net.dv8tion.jda.api.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.Unmodifiable;

public final class ImageFormat {
   public static final ImageFormat JPG = new ImageFormat("jpg", Collections.emptyList());
   public static final ImageFormat PNG = new ImageFormat("png", Collections.emptyList());
   public static final ImageFormat GIF = new ImageFormat("gif", Collections.emptyList());
   public static final ImageFormat STATIC_WEBP = new ImageFormat("webp", Collections.emptyList());
   public static final ImageFormat ANIMATED_WEBP = new ImageFormat("webp", Arrays.asList("animated", "true"));
   private final String extension;
   private final List<String> queryParameters;

   private ImageFormat(String extension, List<String> queryParameters) {
      this.extension = extension;
      this.queryParameters = Helpers.<String>copyAsUnmodifiableList(queryParameters);
   }

   @Nonnull
   public static ImageFormat of(@Nonnull String extension) {
      Checks.notBlank(extension, "Extension");
      return new ImageFormat(extension, Collections.emptyList());
   }

   @Nonnull
   public static ImageFormat of(@Nonnull String extension, @Nonnull List<String> queryParameters) {
      Checks.notBlank(extension, "Extension");
      Checks.notNull(queryParameters, "Query parameters");
      Checks.check((queryParameters.size() & 1) == 0, "Query parameters length must be a multiple of 2");
      return new ImageFormat(extension, queryParameters);
   }

   @Nonnull
   public String getExtension() {
      return this.extension;
   }

   @Nonnull
   public @Unmodifiable List<String> getQueryParameters() {
      return this.queryParameters;
   }

   @Nonnull
   ImageProxy finishProxy(@Nonnull HttpUrl.Builder builder, @Nonnull String lastSegment) {
      builder.addPathSegment(lastSegment + "." + this.extension);
      Iterator<String> it = this.queryParameters.iterator();

      while(it.hasNext()) {
         builder.addQueryParameter((String)it.next(), (String)it.next());
      }

      return new ImageProxy(builder.toString());
   }
}
