package okhttp3.internal;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.JvmField;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.EventListener;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.internal.http2.Header;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000´\u0001\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0000\u001a\u0016\u0010\b\u001a\u00020\u0005*\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u0007H\u0000\u001a)\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u00052\u0012\u0010\f\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000e0\r\"\u00020\u000eH\u0000¢\u0006\u0002\u0010\u000f\u001a\u0014\u0010\u0010\u001a\u00020\u0011*\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0011H\u0000\u001a \u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0000\u001a\u001f\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0016\u001a\u00020\u001aH\u0000¢\u0006\u0004\b\u001b\u0010\u001c\u001a\u0012\u0010\u001d\u001a\u00020\u001e*\b\u0012\u0004\u0012\u00020 0\u001fH\u0000\u001a\u0012\u0010!\u001a\b\u0012\u0004\u0012\u00020 0\u001f*\u00020\u001eH\u0000\u001a\u0014\u0010\"\u001a\u00020\u0007*\u00020\t2\u0006\u0010#\u001a\u00020\tH\u0000\u001a\f\u0010$\u001a\u00020%*\u00020&H\u0000\u001a\u001c\u0010'\u001a\u00020\u0007*\u00020(2\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010)\u001a\u00020\u0019H\u0000\u001a\f\u0010'\u001a\u00020**\u00020\u0012H\u0000\u001a\u001c\u0010+\u001a\u00020\u0007*\u00020(2\u0006\u0010,\u001a\u00020\u00152\u0006\u0010)\u001a\u00020\u0019H\u0000\u001a\u0014\u0010-\u001a\u00020\u0007*\u00020.2\u0006\u0010/\u001a\u00020\u0012H\u0000\u001a\"\u00100\u001a\u00020*2\u0006\u0010\u0004\u001a\u00020\u00052\f\u00101\u001a\b\u0012\u0004\u0012\u00020*02H\u0080\bø\u0001\u0000\u001a\f\u00103\u001a\u00020\u0017*\u000204H\u0000\u001a\u001f\u00105\u001a\b\u0012\u0004\u0012\u0002H60\u001f\"\u0004\b\u0000\u00106*\b\u0012\u0004\u0012\u0002H60\u001fH\u0080\b\u001a\u001f\u00105\u001a\b\u0012\u0004\u0012\u0002H607\"\u0004\b\u0000\u00106*\b\u0012\u0004\u0012\u0002H607H\u0080\b\u001a1\u00105\u001a\u000e\u0012\u0004\u0012\u0002H9\u0012\u0004\u0012\u0002H:08\"\u0004\b\u0000\u00109\"\u0004\b\u0001\u0010:*\u000e\u0012\u0004\u0012\u0002H9\u0012\u0004\u0012\u0002H:08H\u0080\b\u001a\u001e\u0010;\u001a\b\u0012\u0004\u0012\u0002H60\u001f\"\u0004\b\u0000\u00106*\b\u0012\u0004\u0012\u0002H60\u001fH\u0000\u001a-\u0010<\u001a\b\u0012\u0004\u0012\u0002H60\u001f\"\u0004\b\u0000\u001062\u0012\u0010=\u001a\n\u0012\u0006\b\u0001\u0012\u0002H60\r\"\u0002H6H\u0001¢\u0006\u0002\u0010>\u001a'\u0010;\u001a\b\u0012\u0004\u0012\u0002H60\u001f\"\u0004\b\u0000\u00106*\f\u0012\u0006\b\u0001\u0012\u0002H6\u0018\u00010\rH\u0000¢\u0006\u0002\u0010>\u001a\f\u0010?\u001a\u00020**\u00020.H\u0000\u001a\f\u0010?\u001a\u00020**\u00020@H\u0000\u001a\f\u0010A\u001a\u00020\u0005*\u00020\u0017H\u0000\u001a\f\u0010A\u001a\u00020\u0005*\u00020\u0015H\u0000\u001a3\u0010B\u001a\u0004\u0018\u0001H6\"\u0004\b\u0000\u001062\u0006\u0010C\u001a\u00020\u000e2\f\u0010D\u001a\b\u0012\u0004\u0012\u0002H60E2\u0006\u0010F\u001a\u00020\u0005H\u0000¢\u0006\u0002\u0010G\u001a\f\u0010I\u001a\u00020**\u00020JH\u0000\"\u0010\u0010\u0000\u001a\u00020\u00018\u0000X\u0081\u0004¢\u0006\u0002\n\u0000\"\u0010\u0010H\u001a\u00020\u00078\u0000X\u0081\u0004¢\u0006\u0002\n\u0000\"\u0010\u0010K\u001a\u00020\u00058\u0000X\u0081\u0004¢\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006L"},
   d2 = {"UTC", "Ljava/util/TimeZone;", "threadFactory", "Ljava/util/concurrent/ThreadFactory;", "name", "", "daemon", "", "toHostHeader", "Lokhttp3/HttpUrl;", "includeDefaultPort", "format", "args", "", "", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", "readBomAsCharset", "Ljava/nio/charset/Charset;", "Lokio/BufferedSource;", "default", "checkDuration", "", "duration", "", "unit", "Ljava/util/concurrent/TimeUnit;", "Lkotlin/time/Duration;", "checkDuration-HG0u8IE", "(Ljava/lang/String;J)I", "toHeaders", "Lokhttp3/Headers;", "", "Lokhttp3/internal/http2/Header;", "toHeaderList", "canReuseConnectionFor", "other", "asFactory", "Lokhttp3/EventListener$Factory;", "Lokhttp3/EventListener;", "skipAll", "Lokio/Source;", "timeUnit", "", "discard", "timeout", "isHealthy", "Ljava/net/Socket;", "source", "threadName", "block", "Lkotlin/Function0;", "headersContentLength", "Lokhttp3/Response;", "unmodifiable", "T", "", "", "K", "V", "toImmutableList", "immutableListOf", "elements", "([Ljava/lang/Object;)Ljava/util/List;", "closeQuietly", "Ljava/net/ServerSocket;", "toHexString", "readFieldOrNull", "instance", "fieldType", "Ljava/lang/Class;", "fieldName", "(Ljava/lang/Object;Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Object;", "assertionsEnabled", "assertLockNotHeld", "Lokhttp3/Dispatcher;", "okHttpName", "okhttp"}
)
@SourceDebugExtension({"SMAP\n-UtilJvm.kt\nKotlin\n*S Kotlin\n*F\n+ 1 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,355:1\n242#1:361\n242#1:362\n1#2:356\n1563#3:357\n1634#3,3:358\n*S KotlinDebug\n*F\n+ 1 -UtilJvm.kt\nokhttp3/internal/_UtilJvmKt\n*L\n260#1:361\n272#1:362\n131#1:357\n131#1:358,3\n*E\n"})
public final class _UtilJvmKt {
   @JvmField
   @NotNull
   public static final TimeZone UTC;
   @JvmField
   public static final boolean assertionsEnabled;
   @JvmField
   @NotNull
   public static final String okHttpName;

   @NotNull
   public static final ThreadFactory threadFactory(@NotNull String name, boolean daemon) {
      Intrinsics.checkNotNullParameter(name, "name");
      return _UtilJvmKt::threadFactory$lambda$0;
   }

   @NotNull
   public static final String toHostHeader(@NotNull HttpUrl $this$toHostHeader, boolean includeDefaultPort) {
      Intrinsics.checkNotNullParameter($this$toHostHeader, "<this>");
      String host = StringsKt.contains$default((CharSequence)$this$toHostHeader.host(), (CharSequence)":", false, 2, (Object)null) ? '[' + $this$toHostHeader.host() + ']' : $this$toHostHeader.host();
      return !includeDefaultPort && $this$toHostHeader.port() == HttpUrl.Companion.defaultPort($this$toHostHeader.scheme()) ? host : host + ':' + $this$toHostHeader.port();
   }

   // $FF: synthetic method
   public static String toHostHeader$default(HttpUrl var0, boolean var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = false;
      }

      return toHostHeader(var0, var1);
   }

   @NotNull
   public static final String format(@NotNull String format, @NotNull Object... args) {
      Intrinsics.checkNotNullParameter(format, "format");
      Intrinsics.checkNotNullParameter(args, "args");
      StringCompanionObject var10000 = StringCompanionObject.INSTANCE;
      Locale var2 = Locale.US;
      Object[] var3 = Arrays.copyOf(args, args.length);
      String var4 = String.format(var2, format, Arrays.copyOf(var3, var3.length));
      Intrinsics.checkNotNullExpressionValue(var4, "format(...)");
      return var4;
   }

   @NotNull
   public static final Charset readBomAsCharset(@NotNull BufferedSource $this$readBomAsCharset, @NotNull Charset var1) throws IOException {
      Intrinsics.checkNotNullParameter($this$readBomAsCharset, "<this>");
      Intrinsics.checkNotNullParameter(var1, "default");
      Charset var10000;
      switch ($this$readBomAsCharset.select(_UtilCommonKt.getUNICODE_BOMS())) {
         case -1:
            var10000 = var1;
            break;
         case 0:
            var10000 = Charsets.UTF_8;
            break;
         case 1:
            var10000 = Charsets.UTF_16BE;
            break;
         case 2:
            var10000 = Charsets.INSTANCE.UTF32_LE();
            break;
         case 3:
            var10000 = Charsets.UTF_16LE;
            break;
         case 4:
            var10000 = Charsets.INSTANCE.UTF32_BE();
            break;
         default:
            throw new AssertionError();
      }

      return var10000;
   }

   public static final int checkDuration(@NotNull String name, long duration, @NotNull TimeUnit unit) {
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(unit, "unit");
      if (duration < 0L) {
         int var5 = 0;
         String var7 = name + " < 0";
         throw new IllegalStateException(var7.toString());
      } else {
         long millis = unit.toMillis(duration);
         if (millis > 2147483647L) {
            int var9 = 0;
            String var10 = name + " too large";
            throw new IllegalArgumentException(var10.toString());
         } else if (millis == 0L && duration > 0L) {
            int var6 = 0;
            String var8 = name + " too small";
            throw new IllegalArgumentException(var8.toString());
         } else {
            return (int)millis;
         }
      }
   }

   public static final int checkDuration_HG0u8IE/* $FF was: checkDuration-HG0u8IE*/(@NotNull String name, long var1) {
      Intrinsics.checkNotNullParameter(name, "name");
      if (Duration.isNegative-impl(var1)) {
         int var4 = 0;
         String var6 = name + " < 0";
         throw new IllegalStateException(var6.toString());
      } else {
         long millis = Duration.getInWholeMilliseconds-impl(var1);
         if (millis > 2147483647L) {
            int var8 = 0;
            String var9 = name + " too large";
            throw new IllegalArgumentException(var9.toString());
         } else if (millis == 0L && Duration.isPositive-impl(var1)) {
            int var5 = 0;
            String var7 = name + " too small";
            throw new IllegalArgumentException(var7.toString());
         } else {
            return (int)millis;
         }
      }
   }

   @NotNull
   public static final Headers toHeaders(@NotNull List<Header> $this$toHeaders) {
      Intrinsics.checkNotNullParameter($this$toHeaders, "<this>");
      Headers.Builder builder = new Headers.Builder();

      for(Header var3 : $this$toHeaders) {
         ByteString name = var3.component1();
         ByteString value = var3.component2();
         builder.addLenient$okhttp(name.utf8(), value.utf8());
      }

      return builder.build();
   }

   @NotNull
   public static final List<Header> toHeaderList(@NotNull Headers $this$toHeaderList) {
      Intrinsics.checkNotNullParameter($this$toHeaderList, "<this>");
      Iterable $this$map$iv = RangesKt.until(0, $this$toHeaderList.size());
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
      int $i$f$mapTo = 0;
      Iterator var6 = $this$map$iv.iterator();

      while(var6.hasNext()) {
         int item$iv$iv = ((IntIterator)var6).nextInt();
         int var9 = 0;
         destination$iv$iv.add(new Header($this$toHeaderList.name(item$iv$iv), $this$toHeaderList.value(item$iv$iv)));
      }

      return (List)destination$iv$iv;
   }

   public static final boolean canReuseConnectionFor(@NotNull HttpUrl $this$canReuseConnectionFor, @NotNull HttpUrl other) {
      Intrinsics.checkNotNullParameter($this$canReuseConnectionFor, "<this>");
      Intrinsics.checkNotNullParameter(other, "other");
      return Intrinsics.areEqual((Object)$this$canReuseConnectionFor.host(), (Object)other.host()) && $this$canReuseConnectionFor.port() == other.port() && Intrinsics.areEqual((Object)$this$canReuseConnectionFor.scheme(), (Object)other.scheme());
   }

   @NotNull
   public static final EventListener.Factory asFactory(@NotNull EventListener $this$asFactory) {
      Intrinsics.checkNotNullParameter($this$asFactory, "<this>");
      return _UtilJvmKt::asFactory$lambda$0;
   }

   public static final boolean skipAll(@NotNull Source $this$skipAll, int duration, @NotNull TimeUnit timeUnit) throws IOException {
      Intrinsics.checkNotNullParameter($this$skipAll, "<this>");
      Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");
      long nowNs = System.nanoTime();
      long originalDurationNs = $this$skipAll.timeout().hasDeadline() ? $this$skipAll.timeout().deadlineNanoTime() - nowNs : Long.MAX_VALUE;
      $this$skipAll.timeout().deadlineNanoTime(nowNs + Math.min(originalDurationNs, timeUnit.toNanos((long)duration)));

      boolean skipBuffer;
      try {
         Buffer skipBuffer = new Buffer();

         while($this$skipAll.read(skipBuffer, 8192L) != -1L) {
            skipBuffer.clear();
         }

         skipBuffer = true;
      } catch (InterruptedIOException var11) {
         skipBuffer = false;
      } finally {
         if (originalDurationNs == Long.MAX_VALUE) {
            $this$skipAll.timeout().clearDeadline();
         } else {
            $this$skipAll.timeout().deadlineNanoTime(nowNs + originalDurationNs);
         }

      }

      return skipBuffer;
   }

   public static final void skipAll(@NotNull BufferedSource $this$skipAll) throws IOException {
      Intrinsics.checkNotNullParameter($this$skipAll, "<this>");

      while(!$this$skipAll.exhausted()) {
         $this$skipAll.skip($this$skipAll.getBuffer().size());
      }

   }

   public static final boolean discard(@NotNull Source $this$discard, int timeout, @NotNull TimeUnit timeUnit) {
      Intrinsics.checkNotNullParameter($this$discard, "<this>");
      Intrinsics.checkNotNullParameter(timeUnit, "timeUnit");

      boolean var3;
      try {
         var3 = skipAll($this$discard, timeout, timeUnit);
      } catch (IOException var5) {
         var3 = false;
      }

      return var3;
   }

   public static final boolean isHealthy(@NotNull Socket $this$isHealthy, @NotNull BufferedSource source) {
      Intrinsics.checkNotNullParameter($this$isHealthy, "<this>");
      Intrinsics.checkNotNullParameter(source, "source");

      int readTimeout;
      try {
         readTimeout = $this$isHealthy.getSoTimeout();

         boolean var3;
         try {
            $this$isHealthy.setSoTimeout(1);
            var3 = !source.exhausted();
         } finally {
            $this$isHealthy.setSoTimeout(readTimeout);
         }

         readTimeout = var3;
      } catch (SocketTimeoutException var9) {
         readTimeout = 1;
      } catch (IOException var10) {
         readTimeout = 0;
      }

      return (boolean)readTimeout;
   }

   public static final void threadName(@NotNull String name, @NotNull Function0<Unit> block) {
      Intrinsics.checkNotNullParameter(name, "name");
      Intrinsics.checkNotNullParameter(block, "block");
      int $i$f$threadName = 0;
      Thread currentThread = Thread.currentThread();
      String oldName = currentThread.getName();
      currentThread.setName(name);

      try {
         block.invoke();
      } finally {
         InlineMarker.finallyStart(1);
         currentThread.setName(oldName);
         InlineMarker.finallyEnd(1);
      }

   }

   public static final long headersContentLength(@NotNull Response $this$headersContentLength) {
      Intrinsics.checkNotNullParameter($this$headersContentLength, "<this>");
      String var10000 = $this$headersContentLength.headers().get("Content-Length");
      return var10000 != null ? _UtilCommonKt.toLongOrDefault(var10000, -1L) : -1L;
   }

   @NotNull
   public static final <T> List<T> unmodifiable(@NotNull List<? extends T> $this$unmodifiable) {
      Intrinsics.checkNotNullParameter($this$unmodifiable, "<this>");
      int $i$f$unmodifiable = 0;
      List var10000 = Collections.unmodifiableList($this$unmodifiable);
      Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableList(...)");
      return var10000;
   }

   @NotNull
   public static final <T> Set<T> unmodifiable(@NotNull Set<? extends T> $this$unmodifiable) {
      Intrinsics.checkNotNullParameter($this$unmodifiable, "<this>");
      int $i$f$unmodifiable = 0;
      Set var10000 = Collections.unmodifiableSet($this$unmodifiable);
      Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableSet(...)");
      return var10000;
   }

   @NotNull
   public static final <K, V> Map<K, V> unmodifiable(@NotNull Map<K, ? extends V> $this$unmodifiable) {
      Intrinsics.checkNotNullParameter($this$unmodifiable, "<this>");
      int $i$f$unmodifiable = 0;
      Map var10000 = Collections.unmodifiableMap($this$unmodifiable);
      Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableMap(...)");
      return var10000;
   }

   @NotNull
   public static final <T> List<T> toImmutableList(@NotNull List<? extends T> $this$toImmutableList) {
      Intrinsics.checkNotNullParameter($this$toImmutableList, "<this>");
      List var10000;
      if ($this$toImmutableList.isEmpty()) {
         var10000 = CollectionsKt.emptyList();
      } else if ($this$toImmutableList.size() == 1) {
         var10000 = Collections.singletonList($this$toImmutableList.get(0));
         Intrinsics.checkNotNullExpressionValue(var10000, "singletonList(...)");
      } else {
         Object[] var3 = ((Collection)$this$toImmutableList).toArray();
         Intrinsics.checkNotNullExpressionValue(var3, "toArray(...)");
         List $this$unmodifiable$iv = ArraysKt.asList(var3);
         int $i$f$unmodifiable = 0;
         var10000 = Collections.unmodifiableList($this$unmodifiable$iv);
         Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableList(...)");
         Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type kotlin.collections.List<T of okhttp3.internal._UtilJvmKt.toImmutableList>");
      }

      return var10000;
   }

   @SafeVarargs
   @NotNull
   public static final <T> List<T> immutableListOf(@NotNull T... elements) {
      Intrinsics.checkNotNullParameter(elements, "elements");
      return toImmutableList(elements);
   }

   @NotNull
   public static final <T> List<T> toImmutableList(@Nullable T[] $this$toImmutableList) {
      List var10000;
      if ($this$toImmutableList == null || $this$toImmutableList.length == 0) {
         var10000 = CollectionsKt.emptyList();
      } else if ($this$toImmutableList.length == 1) {
         var10000 = Collections.singletonList($this$toImmutableList[0]);
         Intrinsics.checkNotNullExpressionValue(var10000, "singletonList(...)");
      } else {
         List $this$unmodifiable$iv = ArraysKt.asList($this$toImmutableList.clone());
         int $i$f$unmodifiable = 0;
         var10000 = Collections.unmodifiableList($this$unmodifiable$iv);
         Intrinsics.checkNotNullExpressionValue(var10000, "unmodifiableList(...)");
      }

      return var10000;
   }

   public static final void closeQuietly(@NotNull Socket $this$closeQuietly) {
      Intrinsics.checkNotNullParameter($this$closeQuietly, "<this>");

      try {
         $this$closeQuietly.close();
      } catch (AssertionError e) {
         throw e;
      } catch (RuntimeException rethrown) {
         if (Intrinsics.areEqual((Object)rethrown.getMessage(), (Object)"bio == null")) {
            return;
         }

         throw rethrown;
      } catch (Exception var4) {
      }

   }

   public static final void closeQuietly(@NotNull ServerSocket $this$closeQuietly) {
      Intrinsics.checkNotNullParameter($this$closeQuietly, "<this>");

      try {
         $this$closeQuietly.close();
      } catch (RuntimeException rethrown) {
         throw rethrown;
      } catch (Exception var3) {
      }

   }

   @NotNull
   public static final String toHexString(long $this$toHexString) {
      String var10000 = Long.toHexString($this$toHexString);
      Intrinsics.checkNotNullExpressionValue(var10000, "toHexString(...)");
      return var10000;
   }

   @NotNull
   public static final String toHexString(int $this$toHexString) {
      String var10000 = Integer.toHexString($this$toHexString);
      Intrinsics.checkNotNullExpressionValue(var10000, "toHexString(...)");
      return var10000;
   }

   @Nullable
   public static final <T> T readFieldOrNull(@NotNull Object instance, @NotNull Class<T> fieldType, @NotNull String fieldName) {
      Intrinsics.checkNotNullParameter(instance, "instance");
      Intrinsics.checkNotNullParameter(fieldType, "fieldType");
      Intrinsics.checkNotNullParameter(fieldName, "fieldName");

      Class var10000;
      for(Class c = instance.getClass(); !Intrinsics.areEqual((Object)c, (Object)Object.class); c = var10000) {
         try {
            Field field = c.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(instance);
            return (T)(!fieldType.isInstance(value) ? null : fieldType.cast(value));
         } catch (NoSuchFieldException var6) {
            var10000 = c.getSuperclass();
            Intrinsics.checkNotNullExpressionValue(var10000, "getSuperclass(...)");
         }
      }

      if (!Intrinsics.areEqual((Object)fieldName, (Object)"delegate")) {
         Object delegate = readFieldOrNull(instance, Object.class, "delegate");
         if (delegate != null) {
            return (T)readFieldOrNull(delegate, fieldType, fieldName);
         }
      }

      return null;
   }

   public static final void assertLockNotHeld(@NotNull Dispatcher $this$assertLockNotHeld) {
      Intrinsics.checkNotNullParameter($this$assertLockNotHeld, "<this>");
      if (assertionsEnabled && Thread.holdsLock($this$assertLockNotHeld)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + $this$assertLockNotHeld);
      }
   }

   private static final Thread threadFactory$lambda$0(String $name, boolean $daemon, Runnable runnable) {
      Thread $this$threadFactory_u24lambda_u240_u240 = new Thread(runnable, $name);
      int var5 = 0;
      $this$threadFactory_u24lambda_u240_u240.setDaemon($daemon);
      return $this$threadFactory_u24lambda_u240_u240;
   }

   private static final EventListener asFactory$lambda$0(EventListener $this_asFactory, Call it) {
      Intrinsics.checkNotNullParameter(it, "it");
      return $this_asFactory;
   }

   static {
      TimeZone var10000 = TimeZone.getTimeZone("GMT");
      Intrinsics.checkNotNull(var10000);
      UTC = var10000;
      assertionsEnabled = OkHttpClient.class.desiredAssertionStatus();
      String var0 = OkHttpClient.class.getName();
      Intrinsics.checkNotNullExpressionValue(var0, "getName(...)");
      okHttpName = StringsKt.removeSuffix(StringsKt.removePrefix(var0, (CharSequence)"okhttp3."), (CharSequence)"Client");
   }
}
