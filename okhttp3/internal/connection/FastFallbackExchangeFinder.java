package okhttp3.internal.connection;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.Task;
import okhttp3.internal.concurrent.TaskQueue;
import okhttp3.internal.concurrent.TaskRunner;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\u0014\u001a\u00020\u0015H\u0016J\n\u0010\u0016\u001a\u0004\u0018\u00010\u0012H\u0002J\u001a\u0010\u0017\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0018\u001a\u00020\u000b2\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\b\u0010\u001b\u001a\u00020\u001cH\u0002R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u0010\u001a\u0010\u0012\f\u0012\n \u0013*\u0004\u0018\u00010\u00120\u00120\u0011X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001d"},
   d2 = {"Lokhttp3/internal/connection/FastFallbackExchangeFinder;", "Lokhttp3/internal/connection/ExchangeFinder;", "routePlanner", "Lokhttp3/internal/connection/RoutePlanner;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "<init>", "(Lokhttp3/internal/connection/RoutePlanner;Lokhttp3/internal/concurrent/TaskRunner;)V", "getRoutePlanner", "()Lokhttp3/internal/connection/RoutePlanner;", "connectDelayNanos", "", "nextTcpConnectAtNanos", "tcpConnectsInFlight", "Ljava/util/concurrent/CopyOnWriteArrayList;", "Lokhttp3/internal/connection/RoutePlanner$Plan;", "connectResults", "Ljava/util/concurrent/BlockingQueue;", "Lokhttp3/internal/connection/RoutePlanner$ConnectResult;", "kotlin.jvm.PlatformType", "find", "Lokhttp3/internal/connection/RealConnection;", "launchTcpConnect", "awaitTcpConnect", "timeout", "unit", "Ljava/util/concurrent/TimeUnit;", "cancelInFlightConnects", "", "okhttp"}
)
public final class FastFallbackExchangeFinder implements ExchangeFinder {
   @NotNull
   private final RoutePlanner routePlanner;
   @NotNull
   private final TaskRunner taskRunner;
   private final long connectDelayNanos;
   private long nextTcpConnectAtNanos;
   @NotNull
   private final CopyOnWriteArrayList<RoutePlanner.Plan> tcpConnectsInFlight;
   @NotNull
   private final BlockingQueue<RoutePlanner.ConnectResult> connectResults;

   public FastFallbackExchangeFinder(@NotNull RoutePlanner routePlanner, @NotNull TaskRunner taskRunner) {
      Intrinsics.checkNotNullParameter(routePlanner, "routePlanner");
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      super();
      this.routePlanner = routePlanner;
      this.taskRunner = taskRunner;
      this.connectDelayNanos = TimeUnit.MILLISECONDS.toNanos(250L);
      this.nextTcpConnectAtNanos = Long.MIN_VALUE;
      this.tcpConnectsInFlight = new CopyOnWriteArrayList();
      this.connectResults = this.taskRunner.getBackend().<RoutePlanner.ConnectResult>decorate((BlockingQueue)(new LinkedBlockingDeque()));
   }

   @NotNull
   public RoutePlanner getRoutePlanner() {
      return this.routePlanner;
   }

   @NotNull
   public RealConnection find() {
      IOException firstException = null;

      try {
         while(!((Collection)this.tcpConnectsInFlight).isEmpty() || RoutePlanner.hasNext$default(this.getRoutePlanner(), (RealConnection)null, 1, (Object)null)) {
            if (this.getRoutePlanner().isCanceled()) {
               throw new IOException("Canceled");
            }

            long now = this.taskRunner.getBackend().nanoTime();
            long awaitTimeoutNanos = this.nextTcpConnectAtNanos - now;
            RoutePlanner.ConnectResult connectResult = null;
            if (this.tcpConnectsInFlight.isEmpty() || awaitTimeoutNanos <= 0L) {
               connectResult = this.launchTcpConnect();
               this.nextTcpConnectAtNanos = now + this.connectDelayNanos;
               awaitTimeoutNanos = this.connectDelayNanos;
            }

            if (connectResult == null) {
               RoutePlanner.ConnectResult var10000 = this.awaitTcpConnect(awaitTimeoutNanos, TimeUnit.NANOSECONDS);
               if (var10000 == null) {
                  continue;
               }

               connectResult = var10000;
            }

            if (connectResult.isSuccess()) {
               this.cancelInFlightConnects();
               if (!connectResult.getPlan().isReady()) {
                  connectResult = connectResult.getPlan().connectTlsEtc();
               }

               if (connectResult.isSuccess()) {
                  RealConnection var11 = connectResult.getPlan().handleSuccess();
                  return var11;
               }
            }

            Throwable throwable = connectResult.getThrowable();
            if (throwable != null) {
               if (!(throwable instanceof IOException)) {
                  throw throwable;
               }

               if (firstException == null) {
                  firstException = (IOException)throwable;
               } else {
                  ExceptionsKt.addSuppressed((Throwable)firstException, throwable);
               }
            }

            RoutePlanner.Plan nextPlan = connectResult.getNextPlan();
            if (nextPlan != null) {
               this.getRoutePlanner().getDeferredPlans().addFirst(nextPlan);
            }
         }
      } finally {
         this.cancelInFlightConnects();
      }

      Intrinsics.checkNotNull(firstException);
      throw firstException;
   }

   private final RoutePlanner.ConnectResult launchTcpConnect() {
      if (RoutePlanner.hasNext$default(this.getRoutePlanner(), (RealConnection)null, 1, (Object)null)) {
         RoutePlanner.Plan var2;
         try {
            var2 = this.getRoutePlanner().plan();
         } catch (Throwable e) {
            var2 = new FailedPlan(e);
         }

         final RoutePlanner.Plan plan = var2;
         if (var2.isReady()) {
            return new RoutePlanner.ConnectResult(var2, (RoutePlanner.Plan)null, (Throwable)null, 6, (DefaultConstructorMarker)null);
         } else if (var2 instanceof FailedPlan) {
            return ((FailedPlan)var2).getResult();
         } else {
            ((Collection)this.tcpConnectsInFlight).add(var2);
            String taskName = _UtilJvmKt.okHttpName + " connect " + this.getRoutePlanner().getAddress().url().redact();
            TaskQueue.schedule$default(this.taskRunner.newQueue(), new Task(taskName) {
               public long runOnce() {
                  RoutePlanner.ConnectResult connectResult;
                  try {
                     connectResult = plan.connectTcp();
                  } catch (Throwable e) {
                     connectResult = new RoutePlanner.ConnectResult(plan, (RoutePlanner.Plan)null, e, 2, (DefaultConstructorMarker)null);
                  }

                  if (FastFallbackExchangeFinder.this.tcpConnectsInFlight.contains(plan)) {
                     FastFallbackExchangeFinder.this.connectResults.put(connectResult);
                  }

                  return -1L;
               }
            }, 0L, 2, (Object)null);
            return null;
         }
      } else {
         return null;
      }
   }

   private final RoutePlanner.ConnectResult awaitTcpConnect(long timeout, TimeUnit unit) {
      if (this.tcpConnectsInFlight.isEmpty()) {
         return null;
      } else {
         RoutePlanner.ConnectResult var10000 = (RoutePlanner.ConnectResult)this.connectResults.poll(timeout, unit);
         if (var10000 == null) {
            return null;
         } else {
            RoutePlanner.ConnectResult result = var10000;
            this.tcpConnectsInFlight.remove(result.getPlan());
            return result;
         }
      }
   }

   private final void cancelInFlightConnects() {
      Iterator var10000 = this.tcpConnectsInFlight.iterator();
      Intrinsics.checkNotNullExpressionValue(var10000, "iterator(...)");
      Iterator var1 = var10000;

      while(var1.hasNext()) {
         RoutePlanner.Plan plan = (RoutePlanner.Plan)var1.next();
         plan.cancel();
         RoutePlanner.Plan var4 = plan.retry();
         if (var4 != null) {
            RoutePlanner.Plan retry = var4;
            this.getRoutePlanner().getDeferredPlans().addLast(retry);
         }
      }

      this.tcpConnectsInFlight.clear();
   }
}
