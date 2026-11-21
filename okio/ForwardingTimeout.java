package okio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\b\u0016\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0001¢\u0006\u0004\b\u0003\u0010\u0004J\u000e\u0010\u0006\u001a\u00020\u00002\u0006\u0010\u0002\u001a\u00020\u0001J\u0018\u0010\u0007\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\b\u0010\u000b\u001a\u00020\bH\u0016J\b\u0010\f\u001a\u00020\rH\u0016J\b\u0010\u000e\u001a\u00020\bH\u0016J\u0010\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\bH\u0016J\b\u0010\u000f\u001a\u00020\u0001H\u0016J\b\u0010\u0010\u001a\u00020\u0001H\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u0012H\u0016J\u0010\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0016H\u0016J\u0010\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u0019H\u0016R\u001c\u0010\u0002\u001a\u00020\u00018\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0002\u0010\u0005\"\u0004\b\u0006\u0010\u0004¨\u0006\u001a"},
   d2 = {"Lokio/ForwardingTimeout;", "Lokio/Timeout;", "delegate", "<init>", "(Lokio/Timeout;)V", "()Lokio/Timeout;", "setDelegate", "timeout", "", "unit", "Ljava/util/concurrent/TimeUnit;", "timeoutNanos", "hasDeadline", "", "deadlineNanoTime", "clearTimeout", "clearDeadline", "throwIfReached", "", "cancel", "awaitSignal", "condition", "Ljava/util/concurrent/locks/Condition;", "waitUntilNotified", "monitor", "", "okio"}
)
public class ForwardingTimeout extends Timeout {
   @NotNull
   private Timeout delegate;

   public ForwardingTimeout(@NotNull Timeout delegate) {
      Intrinsics.checkNotNullParameter(delegate, "delegate");
      super();
      this.delegate = delegate;
   }

   @JvmName(
      name = "delegate"
   )
   @NotNull
   public final Timeout delegate() {
      return this.delegate;
   }

   // $FF: synthetic method
   public final void setDelegate(Timeout var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.delegate = var1;
   }

   @NotNull
   public final ForwardingTimeout setDelegate(@NotNull Timeout delegate) {
      Intrinsics.checkNotNullParameter(delegate, "delegate");
      this.delegate = delegate;
      return this;
   }

   @NotNull
   public Timeout timeout(long timeout, @NotNull TimeUnit unit) {
      Intrinsics.checkNotNullParameter(unit, "unit");
      return this.delegate.timeout(timeout, unit);
   }

   public long timeoutNanos() {
      return this.delegate.timeoutNanos();
   }

   public boolean hasDeadline() {
      return this.delegate.hasDeadline();
   }

   public long deadlineNanoTime() {
      return this.delegate.deadlineNanoTime();
   }

   @NotNull
   public Timeout deadlineNanoTime(long deadlineNanoTime) {
      return this.delegate.deadlineNanoTime(deadlineNanoTime);
   }

   @NotNull
   public Timeout clearTimeout() {
      return this.delegate.clearTimeout();
   }

   @NotNull
   public Timeout clearDeadline() {
      return this.delegate.clearDeadline();
   }

   public void throwIfReached() throws IOException {
      this.delegate.throwIfReached();
   }

   public void cancel() {
      this.delegate.cancel();
   }

   public void awaitSignal(@NotNull Condition condition) {
      Intrinsics.checkNotNullParameter(condition, "condition");
      this.delegate.awaitSignal(condition);
   }

   public void waitUntilNotified(@NotNull Object monitor) {
      Intrinsics.checkNotNullParameter(monitor, "monitor");
      this.delegate.waitUntilNotified(monitor);
   }
}
