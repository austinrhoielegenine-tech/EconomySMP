package okhttp3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.connection.RealCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003B\u0013\b\u0016\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0002\u0010\u0006J\u0019\u0010\u001e\u001a\u00020\u001f2\n\u0010 \u001a\u00060\u001aR\u00020\u001bH\u0000¢\u0006\u0002\b!J\u0016\u0010\"\u001a\b\u0018\u00010\u001aR\u00020\u001b2\u0006\u0010#\u001a\u00020$H\u0002J\u0006\u0010%\u001a\u00020\u001fJ4\u0010&\u001a\u00020\u001f2\u000e\b\u0002\u0010'\u001a\b\u0018\u00010\u001aR\u00020\u001b2\n\b\u0002\u0010(\u001a\u0004\u0018\u00010\u001b2\u000e\b\u0002\u0010)\u001a\b\u0018\u00010\u001aR\u00020\u001bH\u0002J\u0015\u0010*\u001a\u00020+2\u0006\u0010 \u001a\u00020\u001bH\u0000¢\u0006\u0002\b,J\u0019\u0010-\u001a\u00020\u001f2\n\u0010 \u001a\u00060\u001aR\u00020\u001bH\u0000¢\u0006\u0002\b.J\u0015\u0010-\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001bH\u0000¢\u0006\u0002\b.J\f\u0010/\u001a\b\u0012\u0004\u0012\u00020100J\f\u00102\u001a\b\u0012\u0004\u0012\u00020100J\u0006\u00103\u001a\u00020\bJ\u0006\u00104\u001a\u00020\bJ\r\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\b5R$\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\b@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR$\u0010\r\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\b@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\n\"\u0004\b\u000f\u0010\fR\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u00058G¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0017R\u0018\u0010\u0018\u001a\f\u0012\b\u0012\u00060\u001aR\u00020\u001b0\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u001c\u001a\f\u0012\b\u0012\u00060\u001aR\u00020\u001b0\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0019X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00066"},
   d2 = {"Lokhttp3/Dispatcher;", "", "<init>", "()V", "executorService", "Ljava/util/concurrent/ExecutorService;", "(Ljava/util/concurrent/ExecutorService;)V", "maxRequests", "", "getMaxRequests", "()I", "setMaxRequests", "(I)V", "maxRequestsPerHost", "getMaxRequestsPerHost", "setMaxRequestsPerHost", "idleCallback", "Ljava/lang/Runnable;", "getIdleCallback", "()Ljava/lang/Runnable;", "setIdleCallback", "(Ljava/lang/Runnable;)V", "executorServiceOrNull", "()Ljava/util/concurrent/ExecutorService;", "readyAsyncCalls", "Ljava/util/ArrayDeque;", "Lokhttp3/internal/connection/RealCall$AsyncCall;", "Lokhttp3/internal/connection/RealCall;", "runningAsyncCalls", "runningSyncCalls", "enqueue", "", "call", "enqueue$okhttp", "findExistingCallWithHost", "host", "", "cancelAll", "promoteAndExecute", "enqueuedCall", "finishedCall", "finishedAsyncCall", "executed", "", "executed$okhttp", "finished", "finished$okhttp", "queuedCalls", "", "Lokhttp3/Call;", "runningCalls", "queuedCallsCount", "runningCallsCount", "-deprecated_executorService", "okhttp"}
)
@SourceDebugExtension({"SMAP\nDispatcher.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Dispatcher.kt\nokhttp3/Dispatcher\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n*L\n1#1,299:1\n1#2:300\n1563#3:301\n1634#3,3:302\n1563#3:306\n1634#3,3:307\n242#4:305\n242#4:310\n*S KotlinDebug\n*F\n+ 1 Dispatcher.kt\nokhttp3/Dispatcher\n*L\n279#1:301\n279#1:302,3\n283#1:306\n283#1:307,3\n279#1:305\n283#1:310\n*E\n"})
public final class Dispatcher {
   private int maxRequests;
   private int maxRequestsPerHost;
   @Nullable
   private Runnable idleCallback;
   @Nullable
   private ExecutorService executorServiceOrNull;
   @NotNull
   private final ArrayDeque<RealCall.AsyncCall> readyAsyncCalls;
   @NotNull
   private final ArrayDeque<RealCall.AsyncCall> runningAsyncCalls;
   @NotNull
   private final ArrayDeque<RealCall> runningSyncCalls;

   public Dispatcher() {
      this.maxRequests = 64;
      this.maxRequestsPerHost = 5;
      this.readyAsyncCalls = new ArrayDeque();
      this.runningAsyncCalls = new ArrayDeque();
      this.runningSyncCalls = new ArrayDeque();
   }

   public final synchronized int getMaxRequests() {
      return this.maxRequests;
   }

   public final void setMaxRequests(int maxRequests) {
      if (maxRequests < 1) {
         int var7 = 0;
         String var8 = "max < 1: " + maxRequests;
         throw new IllegalArgumentException(var8.toString());
      } else {
         synchronized(this) {
            int var3 = 0;
            this.maxRequests = maxRequests;
            Unit var6 = Unit.INSTANCE;
         }

         promoteAndExecute$default(this, (RealCall.AsyncCall)null, (RealCall)null, (RealCall.AsyncCall)null, 7, (Object)null);
      }
   }

   public final synchronized int getMaxRequestsPerHost() {
      return this.maxRequestsPerHost;
   }

   public final void setMaxRequestsPerHost(int maxRequestsPerHost) {
      if (maxRequestsPerHost < 1) {
         int var7 = 0;
         String var8 = "max < 1: " + maxRequestsPerHost;
         throw new IllegalArgumentException(var8.toString());
      } else {
         synchronized(this) {
            int var3 = 0;
            this.maxRequestsPerHost = maxRequestsPerHost;
            Unit var6 = Unit.INSTANCE;
         }

         promoteAndExecute$default(this, (RealCall.AsyncCall)null, (RealCall)null, (RealCall.AsyncCall)null, 7, (Object)null);
      }
   }

   @Nullable
   public final synchronized Runnable getIdleCallback() {
      return this.idleCallback;
   }

   public final synchronized void setIdleCallback(@Nullable Runnable var1) {
      this.idleCallback = var1;
   }

   @JvmName(
      name = "executorService"
   )
   @NotNull
   public final synchronized ExecutorService executorService() {
      if (this.executorServiceOrNull == null) {
         this.executorServiceOrNull = (ExecutorService)(new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, (BlockingQueue)(new SynchronousQueue()), _UtilJvmKt.threadFactory(_UtilJvmKt.okHttpName + " Dispatcher", false)));
      }

      ExecutorService var10000 = this.executorServiceOrNull;
      Intrinsics.checkNotNull(var10000);
      return var10000;
   }

   public Dispatcher(@Nullable ExecutorService executorService) {
      this();
      this.executorServiceOrNull = executorService;
   }

   public final void enqueue$okhttp(@NotNull RealCall.AsyncCall call) {
      Intrinsics.checkNotNullParameter(call, "call");
      promoteAndExecute$default(this, call, (RealCall)null, (RealCall.AsyncCall)null, 6, (Object)null);
   }

   private final RealCall.AsyncCall findExistingCallWithHost(String host) {
      Iterator var10000 = this.runningAsyncCalls.iterator();
      Intrinsics.checkNotNullExpressionValue(var10000, "iterator(...)");
      Iterator var2 = var10000;

      while(var2.hasNext()) {
         RealCall.AsyncCall existingCall = (RealCall.AsyncCall)var2.next();
         if (Intrinsics.areEqual((Object)existingCall.getHost(), (Object)host)) {
            return existingCall;
         }
      }

      var10000 = this.readyAsyncCalls.iterator();
      Intrinsics.checkNotNullExpressionValue(var10000, "iterator(...)");
      var2 = var10000;

      while(var2.hasNext()) {
         RealCall.AsyncCall existingCall = (RealCall.AsyncCall)var2.next();
         if (Intrinsics.areEqual((Object)existingCall.getHost(), (Object)host)) {
            return existingCall;
         }
      }

      return null;
   }

   public final synchronized void cancelAll() {
      Iterator var10000 = this.readyAsyncCalls.iterator();
      Intrinsics.checkNotNullExpressionValue(var10000, "iterator(...)");
      Iterator var1 = var10000;

      while(var1.hasNext()) {
         RealCall.AsyncCall call = (RealCall.AsyncCall)var1.next();
         call.getCall().cancel();
      }

      var10000 = this.runningAsyncCalls.iterator();
      Intrinsics.checkNotNullExpressionValue(var10000, "iterator(...)");
      var1 = var10000;

      while(var1.hasNext()) {
         RealCall.AsyncCall call = (RealCall.AsyncCall)var1.next();
         call.getCall().cancel();
      }

      var10000 = this.runningSyncCalls.iterator();
      Intrinsics.checkNotNullExpressionValue(var10000, "iterator(...)");
      var1 = var10000;

      while(var1.hasNext()) {
         RealCall call = (RealCall)var1.next();
         call.cancel();
      }

   }

   private final void promoteAndExecute(RealCall.AsyncCall enqueuedCall, RealCall finishedCall, RealCall.AsyncCall finishedAsyncCall) {
      _UtilJvmKt.assertLockNotHeld(this);
      boolean executorIsShutdown = this.executorService().isShutdown();
      synchronized(this){}

      Effects i;

      @Metadata(
         mv = {2, 2, 0},
         k = 1,
         xi = 48,
         d1 = {"\u0000!\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B#\u0012\u0010\u0010\u0002\u001a\f\u0012\b\u0012\u00060\u0004R\u00020\u00050\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tR\u001b\u0010\u0002\u001a\f\u0012\b\u0012\u00060\u0004R\u00020\u00050\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u000e"},
         d2 = {"okhttp3/Dispatcher$promoteAndExecute$Effects", "", "callsToExecute", "", "Lokhttp3/internal/connection/RealCall$AsyncCall;", "Lokhttp3/internal/connection/RealCall;", "idleCallbackToRun", "Ljava/lang/Runnable;", "<init>", "(Ljava/util/List;Ljava/lang/Runnable;)V", "getCallsToExecute", "()Ljava/util/List;", "getIdleCallbackToRun", "()Ljava/lang/Runnable;", "okhttp"}
      )
      final class Effects {
         private final List<RealCall.AsyncCall> callsToExecute;
         private final Runnable idleCallbackToRun;

         public Effects(List<RealCall.AsyncCall> callsToExecute, Runnable idleCallbackToRun) {
            Intrinsics.checkNotNullParameter(callsToExecute, "callsToExecute");
            super();
            this.callsToExecute = callsToExecute;
            this.idleCallbackToRun = idleCallbackToRun;
         }

         public final List<RealCall.AsyncCall> getCallsToExecute() {
            return this.callsToExecute;
         }

         public final Runnable getIdleCallbackToRun() {
            return this.idleCallbackToRun;
         }
      }

      try {
         int var7 = 0;
         if (finishedCall != null && !this.runningSyncCalls.remove(finishedCall)) {
            int var21 = 0;
            String var22 = "Call wasn't in-flight!";
            throw new IllegalStateException(var22.toString());
         }

         if (finishedAsyncCall != null) {
            finishedAsyncCall.getCallsPerHost().decrementAndGet();
            if (!this.runningAsyncCalls.remove(finishedAsyncCall)) {
               int var19 = 0;
               String idleCallbackToRun = "Call wasn't in-flight!";
               throw new IllegalStateException(idleCallbackToRun.toString());
            }
         }

         if (enqueuedCall != null) {
            this.readyAsyncCalls.add(enqueuedCall);
            if (!enqueuedCall.getCall().getForWebSocket()) {
               RealCall.AsyncCall existingCall = this.findExistingCallWithHost(enqueuedCall.getHost());
               if (existingCall != null) {
                  enqueuedCall.reuseCallsPerHostFrom(existingCall);
               }
            }
         }

         boolean becameIdle = (finishedCall != null || finishedAsyncCall != null) && (executorIsShutdown || this.runningAsyncCalls.isEmpty()) && this.runningSyncCalls.isEmpty();
         Runnable idleCallbackToRun = becameIdle ? this.idleCallback : null;
         Effects var28;
         if (executorIsShutdown) {
            List var26 = CollectionsKt.toList((Iterable)this.readyAsyncCalls);
            int asyncCall = 0;
            this.readyAsyncCalls.clear();
            var28 = new Effects(var26, idleCallbackToRun);
         } else {
            List callsToExecute = (List)(new ArrayList());
            Iterator var10000 = this.readyAsyncCalls.iterator();
            Intrinsics.checkNotNullExpressionValue(var10000, "iterator(...)");
            Iterator i = var10000;

            while(i.hasNext()) {
               RealCall.AsyncCall asyncCall = (RealCall.AsyncCall)i.next();
               if (this.runningAsyncCalls.size() >= this.maxRequests) {
                  break;
               }

               if (asyncCall.getCallsPerHost().get() < this.maxRequestsPerHost) {
                  i.remove();
                  asyncCall.getCallsPerHost().incrementAndGet();
                  Intrinsics.checkNotNull(asyncCall);
                  callsToExecute.add(asyncCall);
                  this.runningAsyncCalls.add(asyncCall);
               }
            }

            var28 = new Effects(callsToExecute, idleCallbackToRun);
         }

         i = var28;
      } finally {
         ;
      }

      Effects effects = i;
      boolean callDispatcherQueueStart = true;
      int var18 = 0;

      for(int var23 = i.getCallsToExecute().size(); var18 < var23; ++var18) {
         RealCall.AsyncCall call = (RealCall.AsyncCall)effects.getCallsToExecute().get(var18);
         if (call == enqueuedCall) {
            callDispatcherQueueStart = false;
         } else {
            call.getCall().getEventListener$okhttp().dispatcherQueueEnd(call.getCall(), this);
         }

         if (executorIsShutdown) {
            RealCall.AsyncCall.failRejected$okhttp$default(call, (RejectedExecutionException)null, 1, (Object)null);
         } else {
            call.executeOn(this.executorService());
         }
      }

      if (callDispatcherQueueStart && enqueuedCall != null) {
         enqueuedCall.getCall().getEventListener$okhttp().dispatcherQueueStart(enqueuedCall.getCall(), this);
      }

      Runnable var29 = effects.getIdleCallbackToRun();
      if (var29 != null) {
         var29.run();
      }

   }

   // $FF: synthetic method
   static void promoteAndExecute$default(Dispatcher var0, RealCall.AsyncCall var1, RealCall var2, RealCall.AsyncCall var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = null;
      }

      if ((var4 & 2) != 0) {
         var2 = null;
      }

      if ((var4 & 4) != 0) {
         var3 = null;
      }

      var0.promoteAndExecute(var1, var2, var3);
   }

   public final synchronized boolean executed$okhttp(@NotNull RealCall call) {
      Intrinsics.checkNotNullParameter(call, "call");
      return this.runningSyncCalls.add(call);
   }

   public final void finished$okhttp(@NotNull RealCall.AsyncCall call) {
      Intrinsics.checkNotNullParameter(call, "call");
      promoteAndExecute$default(this, (RealCall.AsyncCall)null, (RealCall)null, call, 3, (Object)null);
   }

   public final void finished$okhttp(@NotNull RealCall call) {
      Intrinsics.checkNotNullParameter(call, "call");
      promoteAndExecute$default(this, (RealCall.AsyncCall)null, call, (RealCall.AsyncCall)null, 5, (Object)null);
   }

   @NotNull
   public final synchronized List<Call> queuedCalls() {
      Iterable $this$map$iv = (Iterable)this.readyAsyncCalls;
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         RealCall.AsyncCall it = (RealCall.AsyncCall)item$iv$iv;
         int var9 = 0;
         destination$iv$iv.add(it.getCall());
      }

      List $this$unmodifiable$iv = (List)destination$iv$iv;
      $i$f$map = 0;
      List var10000 = Collections.unmodifiableList($this$unmodifiable$iv);
      Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableList(...)");
      return var10000;
   }

   @NotNull
   public final synchronized List<Call> runningCalls() {
      Collection var10000 = (Collection)this.runningSyncCalls;
      Iterable $this$map$iv = (Iterable)this.runningAsyncCalls;
      Collection var10 = var10000;
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$map$iv) {
         RealCall.AsyncCall it = (RealCall.AsyncCall)item$iv$iv;
         int var9 = 0;
         destination$iv$iv.add(it.getCall());
      }

      List $this$unmodifiable$iv = CollectionsKt.plus(var10, (Iterable)((List)destination$iv$iv));
      $i$f$map = 0;
      List var14 = Collections.unmodifiableList($this$unmodifiable$iv);
      Intrinsics.checkNotNullExpressionValue(var14, "unmodifiableList(...)");
      return var14;
   }

   public final synchronized int queuedCallsCount() {
      return this.readyAsyncCalls.size();
   }

   public final synchronized int runningCallsCount() {
      return this.runningAsyncCalls.size() + this.runningSyncCalls.size();
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "executorService",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_executorService"
   )
   @NotNull
   public final ExecutorService _deprecated_executorService/* $FF was: -deprecated_executorService*/() {
      return this.executorService();
   }
}
