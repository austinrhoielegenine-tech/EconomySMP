package okhttp3.internal.concurrent;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0003\b&\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\u0018\u001a\u00020\u0013H&J\u0015\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\f\u001a\u00020\rH\u0000¢\u0006\u0002\b\u001bJ\b\u0010\u001c\u001a\u00020\u0003H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001c\u0010\f\u001a\u0004\u0018\u00010\rX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\u0013X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017¨\u0006\u001d"},
   d2 = {"Lokhttp3/internal/concurrent/Task;", "", "name", "", "cancelable", "", "<init>", "(Ljava/lang/String;Z)V", "getName", "()Ljava/lang/String;", "getCancelable", "()Z", "queue", "Lokhttp3/internal/concurrent/TaskQueue;", "getQueue$okhttp", "()Lokhttp3/internal/concurrent/TaskQueue;", "setQueue$okhttp", "(Lokhttp3/internal/concurrent/TaskQueue;)V", "nextExecuteNanoTime", "", "getNextExecuteNanoTime$okhttp", "()J", "setNextExecuteNanoTime$okhttp", "(J)V", "runOnce", "initQueue", "", "initQueue$okhttp", "toString", "okhttp"}
)
@SourceDebugExtension({"SMAP\nTask.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Task.kt\nokhttp3/internal/concurrent/Task\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,71:1\n1#2:72\n*E\n"})
public abstract class Task {
   @NotNull
   private final String name;
   private final boolean cancelable;
   @Nullable
   private TaskQueue queue;
   private long nextExecuteNanoTime;

   public Task(@NotNull String name, boolean cancelable) {
      Intrinsics.checkNotNullParameter(name, "name");
      super();
      this.name = name;
      this.cancelable = cancelable;
      this.nextExecuteNanoTime = -1L;
   }

   // $FF: synthetic method
   public Task(String var1, boolean var2, int var3, DefaultConstructorMarker var4) {
      if ((var3 & 2) != 0) {
         var2 = true;
      }

      this(var1, var2);
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   public final boolean getCancelable() {
      return this.cancelable;
   }

   @Nullable
   public final TaskQueue getQueue$okhttp() {
      return this.queue;
   }

   public final void setQueue$okhttp(@Nullable TaskQueue var1) {
      this.queue = var1;
   }

   public final long getNextExecuteNanoTime$okhttp() {
      return this.nextExecuteNanoTime;
   }

   public final void setNextExecuteNanoTime$okhttp(long var1) {
      this.nextExecuteNanoTime = var1;
   }

   public abstract long runOnce();

   public final void initQueue$okhttp(@NotNull TaskQueue queue) {
      Intrinsics.checkNotNullParameter(queue, "queue");
      if (this.queue != queue) {
         if (this.queue != null) {
            int var2 = 0;
            String var3 = "task is in multiple queues";
            throw new IllegalStateException(var3.toString());
         } else {
            this.queue = queue;
         }
      }
   }

   @NotNull
   public String toString() {
      return this.name;
   }
}
