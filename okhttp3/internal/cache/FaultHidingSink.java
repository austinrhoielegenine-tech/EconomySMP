package okhttp3.internal.cache;

import java.io.IOException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import okio.Buffer;
import okio.ForwardingSink;
import okio.Sink;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0010\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\u0004\b\b\u0010\tJ\u0018\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u0007H\u0016J\b\u0010\u0014\u001a\u00020\u0007H\u0016R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0015"},
   d2 = {"Lokhttp3/internal/cache/FaultHidingSink;", "Lokio/ForwardingSink;", "delegate", "Lokio/Sink;", "onException", "Lkotlin/Function1;", "Ljava/io/IOException;", "", "<init>", "(Lokio/Sink;Lkotlin/jvm/functions/Function1;)V", "getOnException", "()Lkotlin/jvm/functions/Function1;", "hasErrors", "", "write", "source", "Lokio/Buffer;", "byteCount", "", "flush", "close", "okhttp"}
)
public class FaultHidingSink extends ForwardingSink {
   @NotNull
   private final Function1<IOException, Unit> onException;
   private boolean hasErrors;

   public FaultHidingSink(@NotNull Sink delegate, @NotNull Function1<? super IOException, Unit> onException) {
      Intrinsics.checkNotNullParameter(delegate, "delegate");
      Intrinsics.checkNotNullParameter(onException, "onException");
      super(delegate);
      this.onException = onException;
   }

   @NotNull
   public final Function1<IOException, Unit> getOnException() {
      return this.onException;
   }

   public void write(@NotNull Buffer source, long byteCount) {
      Intrinsics.checkNotNullParameter(source, "source");
      if (this.hasErrors) {
         source.skip(byteCount);
      } else {
         try {
            super.write(source, byteCount);
         } catch (IOException e) {
            this.hasErrors = true;
            this.onException.invoke(e);
         }

      }
   }

   public void flush() {
      if (!this.hasErrors) {
         try {
            super.flush();
         } catch (IOException e) {
            this.hasErrors = true;
            this.onException.invoke(e);
         }

      }
   }

   public void close() {
      try {
         super.close();
      } catch (IOException e) {
         this.hasErrors = true;
         this.onException.invoke(e);
      }

   }
}
