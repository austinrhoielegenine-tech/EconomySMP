package okhttp3.internal.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilJvmKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010!\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\u00020\u0001:\u00014B\u0019\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0018\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u00132\b\b\u0002\u0010%\u001a\u00020&J&\u0010\"\u001a\u00020#2\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010%\u001a\u00020&2\f\u0010'\u001a\b\u0012\u0004\u0012\u00020&0(J0\u0010)\u001a\u00020#2\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010%\u001a\u00020&2\b\b\u0002\u0010*\u001a\u00020\r2\f\u0010'\u001a\b\u0012\u0004\u0012\u00020#0(J\u0006\u0010+\u001a\u00020,J%\u0010-\u001a\u00020\r2\u0006\u0010$\u001a\u00020\u00132\u0006\u0010%\u001a\u00020&2\u0006\u0010.\u001a\u00020\rH\u0000¢\u0006\u0002\b/J\u0006\u00100\u001a\u00020#J\u0006\u0010\f\u001a\u00020#J\r\u00101\u001a\u00020\rH\u0000¢\u0006\u0002\b2J\b\u00103\u001a\u00020\u0005H\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\u0004\u001a\u00020\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\u00020\rX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001c\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00130\u0019X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u001a\u0010\u001c\u001a\u00020\rX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001d\u0010\u000f\"\u0004\b\u001e\u0010\u0011R\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00130 8F¢\u0006\u0006\u001a\u0004\b!\u0010\u001b¨\u00065"},
   d2 = {"Lokhttp3/internal/concurrent/TaskQueue;", "", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "name", "", "<init>", "(Lokhttp3/internal/concurrent/TaskRunner;Ljava/lang/String;)V", "getTaskRunner$okhttp", "()Lokhttp3/internal/concurrent/TaskRunner;", "getName$okhttp", "()Ljava/lang/String;", "shutdown", "", "getShutdown$okhttp", "()Z", "setShutdown$okhttp", "(Z)V", "activeTask", "Lokhttp3/internal/concurrent/Task;", "getActiveTask$okhttp", "()Lokhttp3/internal/concurrent/Task;", "setActiveTask$okhttp", "(Lokhttp3/internal/concurrent/Task;)V", "futureTasks", "", "getFutureTasks$okhttp", "()Ljava/util/List;", "cancelActiveTask", "getCancelActiveTask$okhttp", "setCancelActiveTask$okhttp", "scheduledTasks", "", "getScheduledTasks", "schedule", "", "task", "delayNanos", "", "block", "Lkotlin/Function0;", "execute", "cancelable", "idleLatch", "Ljava/util/concurrent/CountDownLatch;", "scheduleAndDecide", "recurrence", "scheduleAndDecide$okhttp", "cancelAll", "cancelAllAndDecide", "cancelAllAndDecide$okhttp", "toString", "AwaitIdleTask", "okhttp"}
)
@SourceDebugExtension({"SMAP\nTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n+ 3 TaskLogger.kt\nokhttp3/internal/concurrent/TaskLoggerKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,241:1\n63#2:242\n63#2:243\n63#2:252\n49#2,4:268\n63#2:272\n49#2,4:273\n63#2:277\n26#3,4:244\n26#3,4:248\n26#3,4:253\n26#3,4:257\n26#3,4:278\n360#4,7:261\n*S KotlinDebug\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue\n*L\n48#1:242\n64#1:243\n122#1:252\n202#1:268,4\n204#1:272\n212#1:273,4\n214#1:277\n67#1:244,4\n70#1:248,4\n173#1:253,4\n179#1:257,4\n231#1:278,4\n188#1:261,7\n*E\n"})
public final class TaskQueue {
   @NotNull
   private final TaskRunner taskRunner;
   @NotNull
   private final String name;
   private boolean shutdown;
   @Nullable
   private Task activeTask;
   @NotNull
   private final List<Task> futureTasks;
   private boolean cancelActiveTask;

   public TaskQueue(@NotNull TaskRunner taskRunner, @NotNull String name) {
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      Intrinsics.checkNotNullParameter(name, "name");
      super();
      this.taskRunner = taskRunner;
      this.name = name;
      this.futureTasks = (List)(new ArrayList());
   }

   @NotNull
   public final TaskRunner getTaskRunner$okhttp() {
      return this.taskRunner;
   }

   @NotNull
   public final String getName$okhttp() {
      return this.name;
   }

   public final boolean getShutdown$okhttp() {
      return this.shutdown;
   }

   public final void setShutdown$okhttp(boolean var1) {
      this.shutdown = var1;
   }

   @Nullable
   public final Task getActiveTask$okhttp() {
      return this.activeTask;
   }

   public final void setActiveTask$okhttp(@Nullable Task var1) {
      this.activeTask = var1;
   }

   @NotNull
   public final List<Task> getFutureTasks$okhttp() {
      return this.futureTasks;
   }

   public final boolean getCancelActiveTask$okhttp() {
      return this.cancelActiveTask;
   }

   public final void setCancelActiveTask$okhttp(boolean var1) {
      this.cancelActiveTask = var1;
   }

   @NotNull
   public final List<Task> getScheduledTasks() {
      // $FF: Couldn't be decompiled
   }

   public final void schedule(@NotNull Task task, long delayNanos) {
      Intrinsics.checkNotNullParameter(task, "task");
      Lockable $this$withLock$iv = this.taskRunner;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      try {
         int var7 = 0;
         if (!this.shutdown) {
            if (this.scheduleAndDecide$okhttp(task, delayNanos, false)) {
               this.taskRunner.kickCoordinator$okhttp(this);
            }

            Unit var17 = Unit.INSTANCE;
            return;
         }

         if (!task.getCancelable()) {
            Logger $this$taskLog$iv = this.taskRunner.getLogger$okhttp();
            int $i$f$taskLog = 0;
            if ($this$taskLog$iv.isLoggable(Level.FINE)) {
               int var23 = 0;
               String var24 = "schedule failed (queue is shutdown)";
               TaskLoggerKt.access$log($this$taskLog$iv, task, this, var24);
            }

            throw new RejectedExecutionException();
         }

         Logger $this$taskLog$iv = this.taskRunner.getLogger$okhttp();
         int $i$f$taskLog = 0;
         if ($this$taskLog$iv.isLoggable(Level.FINE)) {
            int var15 = 0;
            String var16 = "schedule canceled (queue is shutdown)";
            TaskLoggerKt.access$log($this$taskLog$iv, task, this, var16);
         }
      } finally {
         ;
      }

   }

   // $FF: synthetic method
   public static void schedule$default(TaskQueue var0, Task var1, long var2, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = 0L;
      }

      var0.schedule(var1, var2);
   }

   public final void schedule(@NotNull String name, long delayNanos, @NotNull final Function0<Long> block) {
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(block, "block");
      this.schedule(new Task(name) {
         public long runOnce() {
            return ((Number)block.invoke()).longValue();
         }
      }, delayNanos);
   }

   // $FF: synthetic method
   public static void schedule$default(TaskQueue var0, String var1, long var2, Function0 var4, int var5, Object var6) {
      if ((var5 & 2) != 0) {
         var2 = 0L;
      }

      var0.schedule(var1, var2, var4);
   }

   public final void execute(@NotNull String name, long delayNanos, boolean cancelable, @NotNull final Function0<Unit> block) {
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(block, "block");
      this.schedule(new Task(name, cancelable) {
         public long runOnce() {
            block.invoke();
            return -1L;
         }
      }, delayNanos);
   }

   // $FF: synthetic method
   public static void execute$default(TaskQueue var0, String var1, long var2, boolean var4, Function0 var5, int var6, Object var7) {
      if ((var6 & 2) != 0) {
         var2 = 0L;
      }

      if ((var6 & 4) != 0) {
         var4 = true;
      }

      var0.execute(var1, var2, var4, var5);
   }

   @NotNull
   public final CountDownLatch idleLatch() {
      Lockable $this$withLock$iv = this.taskRunner;
      int $i$f$withLock = 0;
      synchronized($this$withLock$iv){}

      CountDownLatch var11;
      try {
         int var4 = 0;
         if (this.activeTask != null || !this.futureTasks.isEmpty()) {
            Task existingTask = this.activeTask;
            if (existingTask instanceof AwaitIdleTask) {
               CountDownLatch var10 = ((AwaitIdleTask)existingTask).getLatch();
               return var10;
            }

            for(Task futureTask : this.futureTasks) {
               if (futureTask instanceof AwaitIdleTask) {
                  CountDownLatch var9 = ((AwaitIdleTask)futureTask).getLatch();
                  return var9;
               }
            }

            AwaitIdleTask newTask = new AwaitIdleTask();
            if (this.scheduleAndDecide$okhttp(newTask, 0L, false)) {
               this.taskRunner.kickCoordinator$okhttp(this);
            }

            CountDownLatch var8 = newTask.getLatch();
            return var8;
         }

         var11 = new CountDownLatch(0);
      } finally {
         ;
      }

      return var11;
   }

   public final boolean scheduleAndDecide$okhttp(@NotNull Task task, long delayNanos, boolean recurrence) {
      Intrinsics.checkNotNullParameter(task, "task");
      task.initQueue$okhttp(this);
      long now = this.taskRunner.getBackend().nanoTime();
      long executeNanoTime = now + delayNanos;
      int existingIndex = this.futureTasks.indexOf(task);
      if (existingIndex != -1) {
         if (task.getNextExecuteNanoTime$okhttp() <= executeNanoTime) {
            Logger var23 = this.taskRunner.getLogger$okhttp();
            int $i$f$taskLog = 0;
            if (var23.isLoggable(Level.FINE)) {
               int var27 = 0;
               String var28 = "already scheduled";
               TaskLoggerKt.access$log(var23, task, this, var28);
            }

            return false;
         }

         this.futureTasks.remove(existingIndex);
      }

      task.setNextExecuteNanoTime$okhttp(executeNanoTime);
      Logger $this$taskLog$iv = this.taskRunner.getLogger$okhttp();
      int $i$f$taskLog = 0;
      if ($this$taskLog$iv.isLoggable(Level.FINE)) {
         int var14 = 0;
         String var21 = recurrence ? "run again after " + TaskLoggerKt.formatDuration(executeNanoTime - now) : "scheduled after " + TaskLoggerKt.formatDuration(executeNanoTime - now);
         TaskLoggerKt.access$log($this$taskLog$iv, task, this, var21);
      }

      List $this$indexOfFirst$iv = this.futureTasks;
      int $i$f$indexOfFirst = 0;
      $i$f$taskLog = 0;
      Iterator var26 = $this$indexOfFirst$iv.iterator();

      int var10000;
      while(true) {
         if (!var26.hasNext()) {
            var10000 = -1;
            break;
         }

         Object item$iv = var26.next();
         Task it = (Task)item$iv;
         int var17 = 0;
         if (it.getNextExecuteNanoTime$okhttp() - now > delayNanos) {
            var10000 = $i$f$taskLog;
            break;
         }

         ++$i$f$taskLog;
      }

      int insertAt = var10000;
      if (insertAt == -1) {
         insertAt = this.futureTasks.size();
      }

      this.futureTasks.add(insertAt, task);
      return insertAt == 0;
   }

   public final void cancelAll() {
      Lockable $this$assertLockNotHeld$iv = this.taskRunner;
      int $i$f$assertLockNotHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
      } else {
         $this$assertLockNotHeld$iv = this.taskRunner;
         $i$f$assertLockNotHeld = 0;
         synchronized($this$assertLockNotHeld$iv) {
            int var4 = 0;
            if (this.cancelAllAndDecide$okhttp()) {
               this.taskRunner.kickCoordinator$okhttp(this);
            }

            Unit var9 = Unit.INSTANCE;
         }

      }
   }

   public final void shutdown() {
      Lockable $this$assertLockNotHeld$iv = this.taskRunner;
      int $i$f$assertLockNotHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld$iv);
      } else {
         $this$assertLockNotHeld$iv = this.taskRunner;
         $i$f$assertLockNotHeld = 0;
         synchronized($this$assertLockNotHeld$iv) {
            int var4 = 0;
            this.shutdown = true;
            if (this.cancelAllAndDecide$okhttp()) {
               this.taskRunner.kickCoordinator$okhttp(this);
            }

            Unit var9 = Unit.INSTANCE;
         }

      }
   }

   public final boolean cancelAllAndDecide$okhttp() {
      if (this.activeTask != null) {
         Task var10000 = this.activeTask;
         Intrinsics.checkNotNull(var10000);
         if (var10000.getCancelable()) {
            this.cancelActiveTask = true;
         }
      }

      boolean tasksCanceled = false;

      for(int i = this.futureTasks.size() - 1; -1 < i; --i) {
         if (((Task)this.futureTasks.get(i)).getCancelable()) {
            Logger $this$taskLog$iv = this.taskRunner.getLogger$okhttp();
            Task task$iv = (Task)this.futureTasks.get(i);
            int $i$f$taskLog = 0;
            if ($this$taskLog$iv.isLoggable(Level.FINE)) {
               int var7 = 0;
               String var11 = "canceled";
               TaskLoggerKt.access$log($this$taskLog$iv, task$iv, this, var11);
            }

            tasksCanceled = true;
            this.futureTasks.remove(i);
         }
      }

      return tasksCanceled;
   }

   @NotNull
   public String toString() {
      return this.name;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\b\u001a\u00020\tH\u0016R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\n"},
      d2 = {"Lokhttp3/internal/concurrent/TaskQueue$AwaitIdleTask;", "Lokhttp3/internal/concurrent/Task;", "<init>", "()V", "latch", "Ljava/util/concurrent/CountDownLatch;", "getLatch", "()Ljava/util/concurrent/CountDownLatch;", "runOnce", "", "okhttp"}
   )
   private static final class AwaitIdleTask extends Task {
      @NotNull
      private final CountDownLatch latch = new CountDownLatch(1);

      public AwaitIdleTask() {
         super(_UtilJvmKt.okHttpName + " awaitIdle", false);
      }

      @NotNull
      public final CountDownLatch getLatch() {
         return this.latch;
      }

      public long runOnce() {
         this.latch.countDown();
         return -1L;
      }
   }
}
