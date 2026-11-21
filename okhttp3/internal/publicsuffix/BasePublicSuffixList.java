package okhttp3.internal.publicsuffix;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\b \u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0013\u001a\u00020\u0014H\u0002J\b\u0010\u0015\u001a\u00020\u0016H&J\b\u0010\u0017\u001a\u00020\u0014H\u0016J\b\u0010\u001c\u001a\u00020\u0014H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u00020\tX\u0096.¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001a\u0010\u000e\u001a\u00020\tX\u0096.¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u000b\"\u0004\b\u0010\u0010\rR\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0018\u001a\u00020\u0019X¦\u0004¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001b¨\u0006\u001d"},
   d2 = {"Lokhttp3/internal/publicsuffix/BasePublicSuffixList;", "Lokhttp3/internal/publicsuffix/PublicSuffixList;", "<init>", "()V", "listRead", "Ljava/util/concurrent/atomic/AtomicBoolean;", "readCompleteLatch", "Ljava/util/concurrent/CountDownLatch;", "bytes", "Lokio/ByteString;", "getBytes", "()Lokio/ByteString;", "setBytes", "(Lokio/ByteString;)V", "exceptionBytes", "getExceptionBytes", "setExceptionBytes", "readFailure", "Ljava/io/IOException;", "readTheList", "", "listSource", "Lokio/Source;", "ensureLoaded", "path", "", "getPath", "()Ljava/lang/Object;", "readTheListUninterruptibly", "okhttp"}
)
public abstract class BasePublicSuffixList implements PublicSuffixList {
   @NotNull
   private final AtomicBoolean listRead = new AtomicBoolean(false);
   @NotNull
   private final CountDownLatch readCompleteLatch = new CountDownLatch(1);
   public ByteString bytes;
   public ByteString exceptionBytes;
   @Nullable
   private IOException readFailure;

   @NotNull
   public ByteString getBytes() {
      ByteString var10000 = this.bytes;
      if (var10000 != null) {
         return var10000;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("bytes");
         return null;
      }
   }

   public void setBytes(@NotNull ByteString var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.bytes = var1;
   }

   @NotNull
   public ByteString getExceptionBytes() {
      ByteString var10000 = this.exceptionBytes;
      if (var10000 != null) {
         return var10000;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("exceptionBytes");
         return null;
      }
   }

   public void setExceptionBytes(@NotNull ByteString var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.exceptionBytes = var1;
   }

   private final void readTheList() throws IOException {
      Object publicSuffixListBytes = null;
      Object publicSuffixExceptionListBytes = null;

      try {
         Closeable var3 = Okio.buffer(this.listSource());
         Throwable var4 = null;

         try {
            BufferedSource bufferedSource = (BufferedSource)var3;
            int var6 = 0;
            int totalBytes = bufferedSource.readInt();
            var21 = bufferedSource.readByteString((long)totalBytes);
            int totalExceptionBytes = bufferedSource.readInt();
            var22 = bufferedSource.readByteString((long)totalExceptionBytes);
            Unit var25 = Unit.INSTANCE;
         } catch (Throwable var18) {
            var4 = var18;
            throw var18;
         } finally {
            CloseableKt.closeFinally(var3, var4);
         }

         synchronized(this) {
            int var23 = 0;
            Intrinsics.checkNotNull(var21);
            this.setBytes(var21);
            Intrinsics.checkNotNull(var22);
            this.setExceptionBytes(var22);
            Unit var24 = Unit.INSTANCE;
         }
      } finally {
         this.readCompleteLatch.countDown();
      }

   }

   @NotNull
   public abstract Source listSource();

   public void ensureLoaded() {
      if (!this.listRead.get() && this.listRead.compareAndSet(false, true)) {
         this.readTheListUninterruptibly();
      } else {
         try {
            this.readCompleteLatch.await();
         } catch (InterruptedException var4) {
            Thread.currentThread().interrupt();
         }
      }

      if (this.bytes == null) {
         IllegalStateException $this$ensureLoaded_u24lambda_u240 = new IllegalStateException("Unable to load " + this.getPath() + " resource.");
         int var3 = 0;
         $this$ensureLoaded_u24lambda_u240.initCause((Throwable)this.readFailure);
         throw (Throwable)$this$ensureLoaded_u24lambda_u240;
      }
   }

   @NotNull
   public abstract Object getPath();

   private final void readTheListUninterruptibly() {
      boolean interrupted = false;

      try {
         while(true) {
            try {
               this.readTheList();
               return;
            } catch (InterruptedIOException var6) {
               Thread.interrupted();
               interrupted = true;
            } catch (IOException e) {
               this.readFailure = e;
               return;
            }
         }
      } finally {
         if (interrupted) {
            Thread.currentThread().interrupt();
         }

      }
   }
}
