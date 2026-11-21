package net.dv8tion.jda.internal.utils.requestbody;

import java.io.IOException;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import okhttp3.MediaType;
import okio.BufferedSink;
import okio.Source;

public class DataSupplierBody extends TypedBody<DataSupplierBody> {
   private final Supplier<? extends Source> streamSupply;

   public DataSupplierBody(MediaType type, Supplier<? extends Source> streamSupply) {
      super(type);
      this.streamSupply = streamSupply;
   }

   @Nonnull
   public DataSupplierBody withType(@Nonnull MediaType newType) {
      return this.type.equals(newType) ? this : new DataSupplierBody(newType, this.streamSupply);
   }

   public void writeTo(@Nonnull BufferedSink bufferedSink) throws IOException {
      synchronized(this.streamSupply) {
         Source stream = (Source)this.streamSupply.get();

         try {
            bufferedSink.writeAll(stream);
         } catch (Throwable var8) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (stream != null) {
            stream.close();
         }

      }
   }
}
