package okio;

import java.io.IOException;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b&\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0001¢\u0006\u0004\b\u0003\u0010\u0004J\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0016J\b\u0010\f\u001a\u00020\u0007H\u0016J\b\u0010\r\u001a\u00020\u000eH\u0016J\b\u0010\u000f\u001a\u00020\u0007H\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016J\r\u0010\u0002\u001a\u00020\u0001H\u0007¢\u0006\u0002\b\u0012R\u0013\u0010\u0002\u001a\u00020\u00018\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0005¨\u0006\u0013"},
   d2 = {"Lokio/ForwardingSink;", "Lokio/Sink;", "delegate", "<init>", "(Lokio/Sink;)V", "()Lokio/Sink;", "write", "", "source", "Lokio/Buffer;", "byteCount", "", "flush", "timeout", "Lokio/Timeout;", "close", "toString", "", "-deprecated_delegate", "okio"}
)
public abstract class ForwardingSink implements Sink {
   @NotNull
   private final Sink delegate;

   public ForwardingSink(@NotNull Sink delegate) {
      Intrinsics.checkNotNullParameter(delegate, "delegate");
      super();
      this.delegate = delegate;
   }

   @JvmName(
      name = "delegate"
   )
   @NotNull
   public final Sink delegate() {
      return this.delegate;
   }

   public void write(@NotNull Buffer source, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      this.delegate.write(source, byteCount);
   }

   public void flush() throws IOException {
      this.delegate.flush();
   }

   @NotNull
   public Timeout timeout() {
      return this.delegate.timeout();
   }

   public void close() throws IOException {
      this.delegate.close();
   }

   @NotNull
   public String toString() {
      return this.getClass().getSimpleName() + '(' + this.delegate + ')';
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "delegate",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_delegate"
   )
   @NotNull
   public final Sink _deprecated_delegate/* $FF was: -deprecated_delegate*/() {
      return this.delegate;
   }
}
