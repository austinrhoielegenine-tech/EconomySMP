package okhttp3.internal.concurrent;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\t\n\u0000\u001a.\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\u0080\bø\u0001\u0000\u001a9\u0010\n\u001a\u0002H\u000b\"\u0004\b\u0000\u0010\u000b*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\f\u001a\b\u0012\u0004\u0012\u0002H\u000b0\bH\u0080\bø\u0001\u0000¢\u0006\u0002\u0010\r\u001a$\u0010\u000e\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\tH\u0002\u001a\u000e\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\u0012\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u0013"},
   d2 = {"taskLog", "", "Ljava/util/logging/Logger;", "task", "Lokhttp3/internal/concurrent/Task;", "queue", "Lokhttp3/internal/concurrent/TaskQueue;", "messageBlock", "Lkotlin/Function0;", "", "logElapsed", "T", "block", "(Ljava/util/logging/Logger;Lokhttp3/internal/concurrent/Task;Lokhttp3/internal/concurrent/TaskQueue;Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "log", "message", "formatDuration", "ns", "", "okhttp"}
)
public final class TaskLoggerKt {
   public static final void taskLog(@NotNull Logger $this$taskLog, @NotNull Task task, @NotNull TaskQueue queue, @NotNull Function0<String> messageBlock) {
      Intrinsics.checkNotNullParameter($this$taskLog, "<this>");
      Intrinsics.checkNotNullParameter(task, "task");
      Intrinsics.checkNotNullParameter(queue, "queue");
      Intrinsics.checkNotNullParameter(messageBlock, "messageBlock");
      int $i$f$taskLog = 0;
      if ($this$taskLog.isLoggable(Level.FINE)) {
         access$log($this$taskLog, task, queue, (String)messageBlock.invoke());
      }

   }

   public static final <T> T logElapsed(@NotNull Logger $this$logElapsed, @NotNull Task task, @NotNull TaskQueue queue, @NotNull Function0<? extends T> block) {
      Intrinsics.checkNotNullParameter($this$logElapsed, "<this>");
      Intrinsics.checkNotNullParameter(task, "task");
      Intrinsics.checkNotNullParameter(queue, "queue");
      Intrinsics.checkNotNullParameter(block, "block");
      int $i$f$logElapsed = 0;
      long startNs = -1L;
      boolean loggingEnabled = $this$logElapsed.isLoggable(Level.FINE);
      if (loggingEnabled) {
         startNs = queue.getTaskRunner$okhttp().getBackend().nanoTime();
         access$log($this$logElapsed, task, queue, "starting");
      }

      boolean completedNormally = false;
      boolean var14 = false;

      Object var10;
      try {
         var14 = true;
         Object result = block.invoke();
         completedNormally = true;
         var10 = result;
         var14 = false;
      } finally {
         if (var14) {
            InlineMarker.finallyStart(1);
            if (loggingEnabled) {
               long elapsedNs = queue.getTaskRunner$okhttp().getBackend().nanoTime() - startNs;
               if (completedNormally) {
                  access$log($this$logElapsed, task, queue, "finished run in " + formatDuration(elapsedNs));
               } else {
                  access$log($this$logElapsed, task, queue, "failed a run in " + formatDuration(elapsedNs));
               }
            }

            InlineMarker.finallyEnd(1);
         }
      }

      InlineMarker.finallyStart(1);
      if (loggingEnabled) {
         long elapsedNs = queue.getTaskRunner$okhttp().getBackend().nanoTime() - startNs;
         access$log($this$logElapsed, task, queue, "finished run in " + formatDuration(elapsedNs));
      }

      InlineMarker.finallyEnd(1);
      return (T)var10;
   }

   private static final void log(Logger $this$log, Task task, TaskQueue queue, String message) {
      StringBuilder var10001 = (new StringBuilder()).append(queue.getName$okhttp()).append(' ');
      StringCompanionObject var10002 = StringCompanionObject.INSTANCE;
      String var4 = "%-22s";
      Object[] var5 = new Object[]{message};
      String var6 = String.format(var4, Arrays.copyOf(var5, var5.length));
      Intrinsics.checkNotNullExpressionValue(var6, "format(...)");
      $this$log.fine(var10001.append(var6).append(": ").append(task.getName()).toString());
   }

   @NotNull
   public static final String formatDuration(long ns) {
      String s = ns <= -999500000L ? (ns - (long)500000000) / (long)1000000000 + " s " : (ns <= -999500L ? (ns - (long)500000) / (long)1000000 + " ms" : (ns <= 0L ? (ns - (long)500) / (long)1000 + " µs" : (ns < 999500L ? (ns + (long)500) / (long)1000 + " µs" : (ns < 999500000L ? (ns + (long)500000) / (long)1000000 + " ms" : (ns + (long)500000000) / (long)1000000000 + " s "))));
      StringCompanionObject var10000 = StringCompanionObject.INSTANCE;
      String var3 = "%6s";
      Object[] var4 = new Object[]{s};
      String var5 = String.format(var3, Arrays.copyOf(var4, var4.length));
      Intrinsics.checkNotNullExpressionValue(var5, "format(...)");
      return var5;
   }

   // $FF: synthetic method
   public static final void access$log(Logger $receiver, Task task, TaskQueue queue, String message) {
      log($receiver, task, queue, message);
   }
}
