package net.dv8tion.jda.internal.utils.requestbody;

import java.io.Closeable;
import java.io.IOException;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.MediaType;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

public class BufferedRequestBody extends TypedBody<BufferedRequestBody> {
   private final Source source;
   private byte[] data;

   public BufferedRequestBody(Source source, MediaType type) {
      super(type);
      this.source = source;
   }

   @Nonnull
   public BufferedRequestBody withType(@Nonnull MediaType type) {
      if (type.equals(this.type)) {
         return this;
      } else {
         synchronized(this.source) {
            BufferedRequestBody copy = new BufferedRequestBody(this.source, type);
            copy.data = this.data;
            return copy;
         }
      }
   }

   public void writeTo(@Nonnull BufferedSink sink) throws IOException {
      synchronized(this.source) {
         if (this.data != null) {
            sink.write(this.data);
         } else {
            BufferedSource s = Okio.buffer(this.source);

            try {
               this.data = s.readByteArray();
               sink.write(this.data);
            } catch (Throwable var8) {
               if (s != null) {
                  try {
                     s.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (s != null) {
               s.close();
            }

         }
      }
   }

   protected void finalize() {
      IOUtil.silentClose((Closeable)this.source);
   }
}
