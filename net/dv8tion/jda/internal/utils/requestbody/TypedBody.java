package net.dv8tion.jda.internal.utils.requestbody;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public abstract class TypedBody<T extends TypedBody<T>> extends RequestBody {
   protected final MediaType type;

   protected TypedBody(MediaType type) {
      this.type = type;
   }

   @Nonnull
   public abstract T withType(@Nonnull MediaType var1);

   @Nullable
   public MediaType contentType() {
      return this.type;
   }
}
