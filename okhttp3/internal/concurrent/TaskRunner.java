package okhttp3.internal.concurrent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0002\b\u0005\u0018\u0000 -2\u00020\u0001:\u0003+,-B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0015\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u0017H\u0000¢\u0006\u0002\b\u001eJ\u0010\u0010\u001f\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020!H\u0002J \u0010\"\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020!2\u0006\u0010#\u001a\u00020\u00122\u0006\u0010$\u001a\u00020\u0010H\u0002J\b\u0010%\u001a\u0004\u0018\u00010!J\b\u0010&\u001a\u00020\u001cH\u0002J\u0006\u0010'\u001a\u00020\u0017J\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00170)J\u0006\u0010*\u001a\u00020\u001cR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0004\u001a\u00020\u0005X\u0080\u0004¢\u0006\n\n\u0002\b\f\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006."},
   d2 = {"Lokhttp3/internal/concurrent/TaskRunner;", "Lokhttp3/internal/concurrent/Lockable;", "backend", "Lokhttp3/internal/concurrent/TaskRunner$Backend;", "logger", "Ljava/util/logging/Logger;", "<init>", "(Lokhttp3/internal/concurrent/TaskRunner$Backend;Ljava/util/logging/Logger;)V", "getBackend", "()Lokhttp3/internal/concurrent/TaskRunner$Backend;", "getLogger$okhttp", "()Ljava/util/logging/Logger;", "logger$1", "nextQueueName", "", "coordinatorWaiting", "", "coordinatorWakeUpAt", "", "executeCallCount", "runCallCount", "busyQueues", "", "Lokhttp3/internal/concurrent/TaskQueue;", "readyQueues", "runnable", "Ljava/lang/Runnable;", "kickCoordinator", "", "taskQueue", "kickCoordinator$okhttp", "beforeRun", "task", "Lokhttp3/internal/concurrent/Task;", "afterRun", "delayNanos", "completedNormally", "awaitTaskToRun", "startAnotherThread", "newQueue", "activeQueues", "", "cancelAll", "Backend", "RealBackend", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nTaskRunner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskRunner.kt\nokhttp3/internal/concurrent/TaskRunner\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n*L\n1#1,364:1\n55#2,4:365\n55#2,4:369\n55#2,4:373\n55#2,4:377\n55#2,4:381\n63#2:385\n63#2:386\n55#2,4:387\n*S KotlinDebug\n*F\n+ 1 TaskRunner.kt\nokhttp3/internal/concurrent/TaskRunner\n*L\n107#1:365,4\n125#1:369,4\n140#1:373,4\n171#1:377,4\n251#1:381,4\n259#1:385\n268#1:386\n274#1:387,4\n*E\n"})
public final class TaskRunner implements Lockable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final Backend backend;
   @NotNull
   private final Logger logger$1;
   private int nextQueueName;
   private boolean coordinatorWaiting;
   private long coordinatorWakeUpAt;
   private int executeCallCount;
   private int runCallCount;
   @NotNull
   private final List<TaskQueue> busyQueues;
   @NotNull
   private final List<TaskQueue> readyQueues;
   @NotNull
   private final Runnable runnable;
   @NotNull
   private static final Logger logger;
   @JvmField
   @NotNull
   public static final TaskRunner INSTANCE;

   public TaskRunner(@NotNull Backend backend, @NotNull Logger logger) {
      Intrinsics.checkNotNullParameter(backend, "backend");
      Intrinsics.checkNotNullParameter(logger, "logger");
      super();
      this.backend = backend;
      this.logger$1 = logger;
      this.nextQueueName = 10000;
      this.busyQueues = (List)(new ArrayList());
      this.readyQueues = (List)(new ArrayList());
      this.runnable = new Runnable() {
         public void run() {
            // $FF: Couldn't be decompiled
         }
      };
   }

   // $FF: synthetic method
   public TaskRunner(Backend var1, Logger var2, int var3, DefaultConstructorMarker var4) {
      if ((var3 & 2) != 0) {
         var2 = logger;
      }

      this(var1, var2);
   }

   @NotNull
   public final Backend getBackend() {
      return this.backend;
   }

   @NotNull
   public final Logger getLogger$okhttp() {
      return this.logger$1;
   }

   public final void kickCoordinator$okhttp(@NotNull TaskQueue taskQueue) {
      Intrinsics.checkNotNullParameter(taskQueue, "taskQueue");
      Lockable $this$assertLockHeld$iv = this;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         if (taskQueue.getActiveTask$okhttp() == null) {
            if (!((Collection)taskQueue.getFutureTasks$okhttp()).isEmpty()) {
               _UtilCommonKt.addIfAbsent(this.readyQueues, taskQueue);
            } else {
               this.readyQueues.remove(taskQueue);
            }
         }

         if (this.coordinatorWaiting) {
            this.backend.coordinatorNotify(this);
         } else {
            this.startAnotherThread();
         }

      }
   }

   private final void beforeRun(Task task) {
      Lockable $this$assertLockHeld$iv = this;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         task.setNextExecuteNanoTime$okhttp(-1L);
         TaskQueue var10000 = task.getQueue$okhttp();
         Intrinsics.checkNotNull(var10000);
         TaskQueue queue = var10000;
         queue.getFutureTasks$okhttp().remove(task);
         this.readyQueues.remove(queue);
         queue.setActiveTask$okhttp(task);
         this.busyQueues.add(queue);
      }
   }

   private final void afterRun(Task task, long delayNanos, boolean completedNormally) {
      Lockable $this$assertLockHeld$iv = this;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         TaskQueue var10000 = task.getQueue$okhttp();
         Intrinsics.checkNotNull(var10000);
         TaskQueue queue = var10000;
         if (queue.getActiveTask$okhttp() != task) {
            throw new IllegalStateException("Check failed.");
         } else {
            $i$f$assertLockHeld = queue.getCancelActiveTask$okhttp();
            queue.setCancelActiveTask$okhttp(false);
            queue.setActiveTask$okhttp((Task)null);
            this.busyQueues.remove(queue);
            if (delayNanos != -1L && !$i$f$assertLockHeld && !queue.getShutdown$okhttp()) {
               queue.scheduleAndDecide$okhttp(task, delayNanos, true);
            }

            if (!((Collection)queue.getFutureTasks$okhttp()).isEmpty()) {
               this.readyQueues.add(queue);
               if (!completedNormally) {
                  this.startAnotherThread();
               }
            }

         }
      }
   }

   @Nullable
   public final Task awaitTaskToRun() {
      Lockable $this$assertLockHeld$iv = this;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         while(!this.readyQueues.isEmpty()) {
            long now = this.backend.nanoTime();
            long minDelayNanos = Long.MAX_VALUE;
            Task readyTask = null;
            boolean multipleReadyTasks = false;

            for(TaskQueue queue : this.readyQueues) {
               Task candidate = (Task)queue.getFutureTasks$okhttp().get(0);
               long candidateDelay = Math.max(0L, candidate.getNextExecuteNanoTime$okhttp() - now);
               if (candidateDelay > 0L) {
                  minDelayNanos = Math.min(candidateDelay, minDelayNanos);
               } else {
                  if (readyTask != null) {
                     multipleReadyTasks = true;
                     break;
                  }

                  readyTask = candidate;
               }
            }

            if (readyTask != null) {
               this.beforeRun(readyTask);
               if (multipleReadyTasks || !this.coordinatorWaiting && !((Collection)this.readyQueues).isEmpty()) {
                  this.startAnotherThread();
               }

               return readyTask;
            }

            if (this.coordinatorWaiting) {
               if (minDelayNanos < this.coordinatorWakeUpAt - now) {
                  this.backend.coordinatorNotify(this);
               }

               return null;
            }

            this.coordinatorWaiting = true;
            this.coordinatorWakeUpAt = now + minDelayNanos;

            try {
               this.backend.coordinatorWait(this, minDelayNanos);
            } catch (InterruptedException var14) {
               this.cancelAll();
            } finally {
               this.coordinatorWaiting = false;
            }
         }

         return null;
      }
   }

   private final void startAnotherThread() {
      Lockable $this$assertLockHeld$iv = this;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else if (this.executeCallCount <= this.runCallCount) {
         int var3 = this.executeCallCount++;
         this.backend.execute(this, this.runnable);
      }
   }

   @NotNull
   public final TaskQueue newQueue() {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public final List<TaskQueue> activeQueues() {
      // $FF: Couldn't be decompiled
   }

   public final void cancelAll() {
      Lockable $this$assertLockHeld$iv = this;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         for(int i = this.busyQueues.size() - 1; -1 < i; --i) {
            ((TaskQueue)this.busyQueues.get(i)).cancelAllAndDecide$okhttp();
         }

         for(int i = this.readyQueues.size() - 1; -1 < i; --i) {
            TaskQueue queue = (TaskQueue)this.readyQueues.get(i);
            queue.cancelAllAndDecide$okhttp();
            if (queue.getFutureTasks$okhttp().isEmpty()) {
               this.readyQueues.remove(i);
            }
         }

      }
   }

   // $FF: synthetic method
   public static final int access$getRunCallCount$p(TaskRunner $this) {
      return $this.runCallCount;
   }

   // $FF: synthetic method
   public static final void access$setRunCallCount$p(TaskRunner $this, int var1) {
      $this.runCallCount = var1;
   }

   // $FF: synthetic method
   public static final void access$afterRun(TaskRunner $this, Task task, long delayNanos, boolean completedNormally) {
      $this.afterRun(task, delayNanos, completedNormally);
   }

   static {
      Logger var10000 = Logger.getLogger(TaskRunner.class.getName());
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      logger = var10000;
      INSTANCE = new TaskRunner(new RealBackend(_UtilJvmKt.threadFactory(_UtilJvmKt.okHttpName + " TaskRunner", true)), (Logger)null, 2, (DefaultConstructorMarker)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\n\u001a\u00020\u000bH\u0016J\u0010\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fH\u0016J\u0018\u0010\u0010\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u000bH\u0016J\"\u0010\u0012\u001a\b\u0012\u0004\u0012\u0002H\u00140\u0013\"\u0004\b\u0000\u0010\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u0002H\u00140\u0013H\u0016J\u0018\u0010\u0016\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\u0006\u0010\u0019\u001a\u00020\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\u001a"},
      d2 = {"Lokhttp3/internal/concurrent/TaskRunner$RealBackend;", "Lokhttp3/internal/concurrent/TaskRunner$Backend;", "threadFactory", "Ljava/util/concurrent/ThreadFactory;", "<init>", "(Ljava/util/concurrent/ThreadFactory;)V", "executor", "Ljava/util/concurrent/ThreadPoolExecutor;", "getExecutor", "()Ljava/util/concurrent/ThreadPoolExecutor;", "nanoTime", "", "coordinatorNotify", "", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "coordinatorWait", "nanos", "decorate", "Ljava/util/concurrent/BlockingQueue;", "T", "queue", "execute", "runnable", "Ljava/lang/Runnable;", "shutdown", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nTaskRunner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskRunner.kt\nokhttp3/internal/concurrent/TaskRunner$RealBackend\n+ 2 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n*L\n1#1,364:1\n36#2:365\n55#2,4:366\n41#2,6:370\n*S KotlinDebug\n*F\n+ 1 TaskRunner.kt\nokhttp3/internal/concurrent/TaskRunner$RealBackend\n*L\n324#1:365\n337#1:366,4\n339#1:370,6\n*E\n"})
   public static final class RealBackend implements Backend {
      @NotNull
      private final ThreadPoolExecutor executor;

      public RealBackend(@NotNull ThreadFactory threadFactory) {
         Intrinsics.checkNotNullParameter(threadFactory, "threadFactory");
         super();
         this.executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, (BlockingQueue)(new SynchronousQueue()), threadFactory);
      }

      @NotNull
      public final ThreadPoolExecutor getExecutor() {
         return this.executor;
      }

      public long nanoTime() {
         return System.nanoTime();
      }

      public void coordinatorNotify(@NotNull TaskRunner taskRunner) {
         Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
         Lockable $this$notify$iv = taskRunner;
         int $i$f$notify = 0;
         ((Object)$this$notify$iv).notify();
      }

      public void coordinatorWait(@NotNull TaskRunner taskRunner, long nanos) throws InterruptedException {
         Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
         Lockable $this$assertLockHeld$iv = taskRunner;
         int $i$f$assertLockHeld = 0;
         if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
            throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
         } else {
            if (nanos > 0L) {
               $this$assertLockHeld$iv = taskRunner;
               int $i$f$awaitNanos = 0;
               long ms$iv = nanos / 1000000L;
               long ns$iv = nanos - ms$iv * 1000000L;
               if (ms$iv > 0L || nanos > 0L) {
                  ((Object)$this$assertLockHeld$iv).wait(ms$iv, (int)ns$iv);
               }
            }

         }
      }

      @NotNull
      public <T> BlockingQueue<T> decorate(@NotNull BlockingQueue<T> queue) {
         Intrinsics.checkNotNullParameter(queue, "queue");
         return queue;
      }

      public void execute(@NotNull TaskRunner taskRunner, @NotNull Runnable runnable) {
         Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
         Intrinsics.checkNotNullParameter(runnable, "runnable");
         this.executor.execute(runnable);
      }

      public final void shutdown() {
         this.executor.shutdown();
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0010\u0010\b\u001a\u00020\t8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\n"},
      d2 = {"Lokhttp3/internal/concurrent/TaskRunner$Companion;", "", "<init>", "()V", "logger", "Ljava/util/logging/Logger;", "getLogger", "()Ljava/util/logging/Logger;", "INSTANCE", "Lokhttp3/internal/concurrent/TaskRunner;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Logger getLogger() {
         return TaskRunner.logger;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0018\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\u0003H&J\"\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\f0\u000b\"\u0004\b\u0000\u0010\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u0002H\f0\u000bH&J\u0018\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u0010H&¨\u0006\u0011À\u0006\u0003"},
      d2 = {"Lokhttp3/internal/concurrent/TaskRunner$Backend;", "", "nanoTime", "", "coordinatorNotify", "", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "coordinatorWait", "nanos", "decorate", "Ljava/util/concurrent/BlockingQueue;", "T", "queue", "execute", "runnable", "Ljava/lang/Runnable;", "okhttp"}
   )
   public interface Backend {
      long nanoTime();

      void coordinatorNotify(@NotNull TaskRunner var1);

      void coordinatorWait(@NotNull TaskRunner var1, long var2);

      @NotNull
      <T> BlockingQueue<T> decorate(@NotNull BlockingQueue<T> var1);

      void execute(@NotNull TaskRunner var1, @NotNull Runnable var2);
   }
}
