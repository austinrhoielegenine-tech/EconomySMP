package okhttp3.internal.connection;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Address;
import okhttp3.ConnectionPool;
import okhttp3.Route;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.concurrent.Lockable;
import okhttp3.internal.concurrent.Task;
import okhttp3.internal.concurrent.TaskQueue;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.platform.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000i\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\t*\u0001\u0016\u0018\u0000 22\u00020\u0001:\u00012B1\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rJ\u0006\u0010\u001b\u001a\u00020\u0005J\u0006\u0010\u001c\u001a\u00020\u0005J?\u0010\u001d\u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u000e\u0010$\u001a\n\u0012\u0004\u0012\u00020&\u0018\u00010%2\u0006\u0010'\u001a\u00020\u001fH\u0000¢\u0006\u0002\b(J\u000e\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020\u001aJ\u000e\u0010,\u001a\u00020\u001f2\u0006\u0010+\u001a\u00020\u001aJ\u0006\u0010-\u001a\u00020*J\u000e\u0010.\u001a\u00020\u00072\u0006\u0010/\u001a\u00020\u0007J\u0018\u00100\u001a\u00020\u00052\u0006\u0010+\u001a\u00020\u001a2\u0006\u0010/\u001a\u00020\u0007H\u0002J\u0006\u00101\u001a\u00020*R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u00020\u000bX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\u0010\u001a\u00020\u0007X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u00020\u0016X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0017R\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0019X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00063"},
   d2 = {"Lokhttp3/internal/connection/RealConnectionPool;", "", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "maxIdleConnections", "", "keepAliveDuration", "", "timeUnit", "Ljava/util/concurrent/TimeUnit;", "connectionListener", "Lokhttp3/internal/connection/ConnectionListener;", "<init>", "(Lokhttp3/internal/concurrent/TaskRunner;IJLjava/util/concurrent/TimeUnit;Lokhttp3/internal/connection/ConnectionListener;)V", "getConnectionListener$okhttp", "()Lokhttp3/internal/connection/ConnectionListener;", "keepAliveDurationNs", "getKeepAliveDurationNs$okhttp", "()J", "cleanupQueue", "Lokhttp3/internal/concurrent/TaskQueue;", "cleanupTask", "okhttp3/internal/connection/RealConnectionPool$cleanupTask$1", "Lokhttp3/internal/connection/RealConnectionPool$cleanupTask$1;", "connections", "Ljava/util/concurrent/ConcurrentLinkedQueue;", "Lokhttp3/internal/connection/RealConnection;", "idleConnectionCount", "connectionCount", "callAcquirePooledConnection", "doExtensiveHealthChecks", "", "address", "Lokhttp3/Address;", "call", "Lokhttp3/internal/connection/RealCall;", "routes", "", "Lokhttp3/Route;", "requireMultiplexed", "callAcquirePooledConnection$okhttp", "put", "", "connection", "connectionBecameIdle", "evictAll", "closeConnections", "now", "pruneAndGetAllocationCount", "scheduleCloser", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nRealConnectionPool.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealConnectionPool.kt\nokhttp3/internal/connection/RealConnectionPool\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Lockable.kt\nokhttp3/internal/concurrent/LockableKt\n*L\n1#1,326:1\n1#2:327\n1788#3,3:328\n1791#3:332\n63#4:331\n63#4:333\n63#4:334\n55#4,4:335\n55#4,4:339\n63#4:343\n63#4:344\n63#4:345\n55#4,4:346\n*S KotlinDebug\n*F\n+ 1 RealConnectionPool.kt\nokhttp3/internal/connection/RealConnectionPool\n*L\n64#1:328,3\n64#1:332\n65#1:331\n92#1:333\n111#1:334\n127#1:335,4\n139#1:339,4\n157#1:343\n201#1:344\n247#1:345\n287#1:346,4\n*E\n"})
public final class RealConnectionPool {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   private final int maxIdleConnections;
   @NotNull
   private final ConnectionListener connectionListener;
   private final long keepAliveDurationNs;
   @NotNull
   private final TaskQueue cleanupQueue;
   @NotNull
   private final <undefinedtype> cleanupTask;
   @NotNull
   private final ConcurrentLinkedQueue<RealConnection> connections;

   public RealConnectionPool(@NotNull TaskRunner taskRunner, int maxIdleConnections, long keepAliveDuration, @NotNull TimeUnit timeUnit, @NotNull ConnectionListener connectionListener) {
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
      Intrinsics.checkNotNullParameter(connectionListener, "connectionListener");
      super();
      this.maxIdleConnections = maxIdleConnections;
      this.connectionListener = connectionListener;
      this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);
      this.cleanupQueue = taskRunner.newQueue();
      String var7 = _UtilJvmKt.okHttpName + " ConnectionPool connection closer";
      this.cleanupTask = new Task(var7) {
         public long runOnce() {
            return RealConnectionPool.this.closeConnections(System.nanoTime());
         }
      };
      this.connections = new ConcurrentLinkedQueue();
      if (keepAliveDuration <= 0L) {
         int var8 = 0;
         String var9 = "keepAliveDuration <= 0: " + keepAliveDuration;
         throw new IllegalArgumentException(var9.toString());
      }
   }

   @NotNull
   public final ConnectionListener getConnectionListener$okhttp() {
      return this.connectionListener;
   }

   public final long getKeepAliveDurationNs$okhttp() {
      return this.keepAliveDurationNs;
   }

   public final int idleConnectionCount() {
      // $FF: Couldn't be decompiled
   }

   public final int connectionCount() {
      return this.connections.size();
   }

   @Nullable
   public final RealConnection callAcquirePooledConnection$okhttp(boolean param1, @NotNull Address param2, @NotNull RealCall param3, @Nullable List<Route> param4, boolean param5) {
      // $FF: Couldn't be decompiled
   }

   public final void put(@NotNull RealConnection connection) {
      Intrinsics.checkNotNullParameter(connection, "connection");
      Lockable $this$assertLockHeld$iv = connection;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         this.connections.add(connection);
         this.scheduleCloser();
      }
   }

   public final boolean connectionBecameIdle(@NotNull RealConnection connection) {
      Intrinsics.checkNotNullParameter(connection, "connection");
      Lockable $this$assertLockHeld$iv = connection;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         boolean var10000;
         if (!connection.getNoNewExchanges() && this.maxIdleConnections != 0) {
            this.scheduleCloser();
            var10000 = false;
         } else {
            connection.setNoNewExchanges(true);
            this.connections.remove(connection);
            if (this.connections.isEmpty()) {
               this.cleanupQueue.cancelAll();
            }

            var10000 = true;
         }

         return var10000;
      }
   }

   public final void evictAll() {
      // $FF: Couldn't be decompiled
   }

   public final long closeConnections(long now) {
      long earliestOldIdleAtNs = 0L;
      earliestOldIdleAtNs = now - this.keepAliveDurationNs + 1L;
      Object earliestOldConnection = null;
      long earliestEvictableIdleAtNs = 0L;
      earliestEvictableIdleAtNs = Long.MAX_VALUE;
      Object earliestEvictableConnection = null;
      int inUseConnectionCount = 0;
      int evictableConnectionCount = 0;
      Iterator var10000 = this.connections.iterator();
      Intrinsics.checkNotNullExpressionValue(var10000, "iterator(...)");

      Unit var17;
      for(Iterator var7 = var10000; var7.hasNext(); var17 = Unit.INSTANCE) {
         RealConnection connection = (RealConnection)var7.next();
         Intrinsics.checkNotNull(connection);
         Lockable $this$withLock$iv = connection;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv) {
            int var12 = 0;
            if (this.pruneAndGetAllocationCount(connection, now) > 0) {
               ++inUseConnectionCount;
            } else {
               long idleAtNs = connection.getIdleAtNs();
               if (idleAtNs < earliestOldIdleAtNs) {
                  earliestOldIdleAtNs = idleAtNs;
                  earliestOldConnection = connection;
               }

               ++evictableConnectionCount;
               if (idleAtNs < earliestEvictableIdleAtNs) {
                  earliestEvictableIdleAtNs = idleAtNs;
                  earliestEvictableConnection = connection;
               }
            }
         }
      }

      RealConnection toEvict = null;
      long toEvictIdleAtNs = 0L;
      if (earliestOldConnection != null) {
         toEvict = (RealConnection)earliestOldConnection;
         toEvictIdleAtNs = earliestOldIdleAtNs;
      } else if (evictableConnectionCount > this.maxIdleConnections) {
         toEvict = (RealConnection)earliestEvictableConnection;
         toEvictIdleAtNs = earliestEvictableIdleAtNs;
      } else {
         toEvict = null;
         toEvictIdleAtNs = -1L;
      }

      if (toEvict != null) {
         Lockable $this$withLock$iv = toEvict;
         int $i$f$withLock = 0;
         synchronized($this$withLock$iv){}

         try {
            int var13 = 0;
            if (!((Collection)toEvict.getCalls()).isEmpty()) {
               long var18 = 0L;
               return var18;
            }

            if (toEvict.getIdleAtNs() != toEvictIdleAtNs) {
               long var16 = 0L;
               return var16;
            }

            toEvict.setNoNewExchanges(true);
            this.connections.remove(toEvict);
         } finally {
            ;
         }

         _UtilJvmKt.closeQuietly(toEvict.socket());
         this.connectionListener.connectionClosed(toEvict);
         if (this.connections.isEmpty()) {
            this.cleanupQueue.cancelAll();
         }

         return 0L;
      } else if (earliestEvictableConnection != null) {
         return earliestEvictableIdleAtNs + this.keepAliveDurationNs - now;
      } else {
         return inUseConnectionCount > 0 ? this.keepAliveDurationNs : -1L;
      }
   }

   private final int pruneAndGetAllocationCount(RealConnection connection, long now) {
      Lockable $this$assertLockHeld$iv = connection;
      int $i$f$assertLockHeld = 0;
      if (_UtilJvmKt.assertionsEnabled && !Thread.holdsLock($this$assertLockHeld$iv)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + $this$assertLockHeld$iv);
      } else {
         List references = connection.getCalls();
         $i$f$assertLockHeld = 0;

         while($i$f$assertLockHeld < references.size()) {
            Reference reference = (Reference)references.get($i$f$assertLockHeld);
            if (reference.get() != null) {
               ++$i$f$assertLockHeld;
            } else {
               Intrinsics.checkNotNull(reference, "null cannot be cast to non-null type okhttp3.internal.connection.RealCall.CallReference");
               RealCall.CallReference callReference = (RealCall.CallReference)reference;
               String message = "A connection to " + connection.route().address().url() + " was leaked. Did you forget to close a response body?";
               Platform.Companion.get().logCloseableLeak(message, callReference.getCallStackTrace());
               references.remove($i$f$assertLockHeld);
               if (references.isEmpty()) {
                  connection.setIdleAtNs(now - this.keepAliveDurationNs);
                  return 0;
               }
            }
         }

         return references.size();
      }
   }

   public final void scheduleCloser() {
      TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, (Object)null);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"},
      d2 = {"Lokhttp3/internal/connection/RealConnectionPool$Companion;", "", "<init>", "()V", "get", "Lokhttp3/internal/connection/RealConnectionPool;", "connectionPool", "Lokhttp3/ConnectionPool;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final RealConnectionPool get(@NotNull ConnectionPool connectionPool) {
         Intrinsics.checkNotNullParameter(connectionPool, "connectionPool");
         return connectionPool.getDelegate$okhttp();
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
