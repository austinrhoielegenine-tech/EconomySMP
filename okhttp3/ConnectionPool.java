package okhttp3;

import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.connection.ConnectionListener;
import okhttp3.internal.connection.RealConnectionPool;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0011\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B;\b\u0010\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f¢\u0006\u0004\b\u0004\u0010\u0010B1\b\u0010\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\u000e\u001a\u00020\u000f¢\u0006\u0004\b\u0004\u0010\u0011B!\b\u0016\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\u0004\u0010\u0012B\t\b\u0016¢\u0006\u0004\b\u0004\u0010\u0013J\u0006\u0010\u0016\u001a\u00020\u0007J\u0006\u0010\u0017\u001a\u00020\u0007J\u0006\u0010\u001a\u001a\u00020\u001bR\u0014\u0010\u0002\u001a\u00020\u0003X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0014\u0010\u000e\u001a\u00020\u000f8@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019¨\u0006\u001c"},
   d2 = {"Lokhttp3/ConnectionPool;", "", "delegate", "Lokhttp3/internal/connection/RealConnectionPool;", "<init>", "(Lokhttp3/internal/connection/RealConnectionPool;)V", "maxIdleConnections", "", "keepAliveDuration", "", "timeUnit", "Ljava/util/concurrent/TimeUnit;", "taskRunner", "Lokhttp3/internal/concurrent/TaskRunner;", "connectionListener", "Lokhttp3/internal/connection/ConnectionListener;", "(IJLjava/util/concurrent/TimeUnit;Lokhttp3/internal/concurrent/TaskRunner;Lokhttp3/internal/connection/ConnectionListener;)V", "(IJLjava/util/concurrent/TimeUnit;Lokhttp3/internal/connection/ConnectionListener;)V", "(IJLjava/util/concurrent/TimeUnit;)V", "()V", "getDelegate$okhttp", "()Lokhttp3/internal/connection/RealConnectionPool;", "idleConnectionCount", "connectionCount", "getConnectionListener$okhttp", "()Lokhttp3/internal/connection/ConnectionListener;", "evictAll", "", "okhttp"}
)
public final class ConnectionPool {
   @NotNull
   private final RealConnectionPool delegate;

   public ConnectionPool(@NotNull RealConnectionPool delegate) {
      Intrinsics.checkNotNullParameter(delegate, "delegate");
      super();
      this.delegate = delegate;
   }

   @NotNull
   public final RealConnectionPool getDelegate$okhttp() {
      return this.delegate;
   }

   public ConnectionPool(int maxIdleConnections, long keepAliveDuration, @NotNull TimeUnit timeUnit, @NotNull TaskRunner taskRunner, @NotNull ConnectionListener connectionListener) {
      Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
      Intrinsics.checkNotNullParameter(taskRunner, "taskRunner");
      Intrinsics.checkNotNullParameter(connectionListener, "connectionListener");
      this(new RealConnectionPool(taskRunner, maxIdleConnections, keepAliveDuration, timeUnit, connectionListener));
   }

   // $FF: synthetic method
   public ConnectionPool(int var1, long var2, TimeUnit var4, TaskRunner var5, ConnectionListener var6, int var7, DefaultConstructorMarker var8) {
      if ((var7 & 1) != 0) {
         var1 = 5;
      }

      if ((var7 & 2) != 0) {
         var2 = 5L;
      }

      if ((var7 & 4) != 0) {
         var4 = TimeUnit.MINUTES;
      }

      if ((var7 & 8) != 0) {
         var5 = TaskRunner.INSTANCE;
      }

      if ((var7 & 16) != 0) {
         var6 = ConnectionListener.Companion.getNONE();
      }

      this(var1, var2, var4, var5, var6);
   }

   public ConnectionPool(int maxIdleConnections, long keepAliveDuration, @NotNull TimeUnit timeUnit, @NotNull ConnectionListener connectionListener) {
      Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
      Intrinsics.checkNotNullParameter(connectionListener, "connectionListener");
      TaskRunner var6 = TaskRunner.INSTANCE;
      this(maxIdleConnections, keepAliveDuration, timeUnit, var6, connectionListener);
   }

   // $FF: synthetic method
   public ConnectionPool(int var1, long var2, TimeUnit var4, ConnectionListener var5, int var6, DefaultConstructorMarker var7) {
      if ((var6 & 1) != 0) {
         var1 = 5;
      }

      if ((var6 & 2) != 0) {
         var2 = 5L;
      }

      if ((var6 & 4) != 0) {
         var4 = TimeUnit.MINUTES;
      }

      if ((var6 & 8) != 0) {
         var5 = ConnectionListener.Companion.getNONE();
      }

      this(var1, var2, var4, var5);
   }

   public ConnectionPool(int maxIdleConnections, long keepAliveDuration, @NotNull TimeUnit timeUnit) {
      Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
      this(maxIdleConnections, keepAliveDuration, timeUnit, TaskRunner.INSTANCE, ConnectionListener.Companion.getNONE());
   }

   public ConnectionPool() {
      this(5, 5L, TimeUnit.MINUTES);
   }

   public final int idleConnectionCount() {
      return this.delegate.idleConnectionCount();
   }

   public final int connectionCount() {
      return this.delegate.connectionCount();
   }

   @NotNull
   public final ConnectionListener getConnectionListener$okhttp() {
      return this.delegate.getConnectionListener$okhttp();
   }

   public final void evictAll() {
      this.delegate.evictAll();
   }
}
