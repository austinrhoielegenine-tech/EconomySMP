package okhttp3;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.http1.HeadersReader;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Okio;
import okio.Options;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u0000 \u001f2\u00020\u0001:\u0003\u001d\u001e\u001fB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007B\u0011\b\u0016\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\u0006\u0010\nJ\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0019H\u0002J\b\u0010\u001b\u001a\u00020\u001cH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0004\u001a\u00020\u00058\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0014\u001a\b\u0018\u00010\u0015R\u00020\u0000X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006 "},
   d2 = {"Lokhttp3/MultipartReader;", "Ljava/io/Closeable;", "source", "Lokio/BufferedSource;", "boundary", "", "<init>", "(Lokio/BufferedSource;Ljava/lang/String;)V", "response", "Lokhttp3/ResponseBody;", "(Lokhttp3/ResponseBody;)V", "()Ljava/lang/String;", "dashDashBoundary", "Lokio/ByteString;", "crlfDashDashBoundary", "partCount", "", "closed", "", "noMoreParts", "currentPart", "Lokhttp3/MultipartReader$PartSource;", "nextPart", "Lokhttp3/MultipartReader$Part;", "currentPartBytesRemaining", "", "maxByteCount", "close", "", "PartSource", "Part", "Companion", "okhttp"}
)
@SourceDebugExtension({"SMAP\nMultipartReader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartReader.kt\nokhttp3/MultipartReader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,228:1\n1#2:229\n*E\n"})
public final class MultipartReader implements Closeable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final BufferedSource source;
   @NotNull
   private final String boundary;
   @NotNull
   private final ByteString dashDashBoundary;
   @NotNull
   private final ByteString crlfDashDashBoundary;
   private int partCount;
   private boolean closed;
   private boolean noMoreParts;
   @Nullable
   private PartSource currentPart;
   @NotNull
   private static final Options afterBoundaryOptions;

   public MultipartReader(@NotNull BufferedSource source, @NotNull String boundary) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(boundary, "boundary");
      super();
      this.source = source;
      this.boundary = boundary;
      this.dashDashBoundary = (new Buffer()).writeUtf8("--").writeUtf8(this.boundary).readByteString();
      this.crlfDashDashBoundary = (new Buffer()).writeUtf8("\r\n--").writeUtf8(this.boundary).readByteString();
   }

   @JvmName(
      name = "boundary"
   )
   @NotNull
   public final String boundary() {
      return this.boundary;
   }

   public MultipartReader(@NotNull ResponseBody response) throws IOException {
      Intrinsics.checkNotNullParameter(response, "response");
      BufferedSource var10001 = response.source();
      MediaType var10002 = response.contentType();
      if (var10002 != null) {
         String var2 = var10002.parameter("boundary");
         if (var2 != null) {
            this(var10001, var2);
            return;
         }
      }

      throw new ProtocolException("expected the Content-Type to have a boundary parameter");
   }

   @Nullable
   public final Part nextPart() throws IOException {
      if (this.closed) {
         int headers = 0;
         String var6 = "closed";
         throw new IllegalStateException(var6.toString());
      } else if (this.noMoreParts) {
         return null;
      } else {
         if (this.partCount == 0 && this.source.rangeEquals(0L, this.dashDashBoundary)) {
            this.source.skip((long)this.dashDashBoundary.size());
         } else {
            while(true) {
               long toSkip = this.currentPartBytesRemaining(8192L);
               if (toSkip == 0L) {
                  this.source.skip((long)this.crlfDashDashBoundary.size());
                  break;
               }

               this.source.skip(toSkip);
            }
         }

         boolean whitespace = false;

         while(true) {
            switch (this.source.select(afterBoundaryOptions)) {
               case -1:
                  throw new ProtocolException("unexpected characters after boundary");
               case 0:
                  int var3 = this.partCount++;
                  Headers var2 = (new HeadersReader(this.source)).readHeaders();
                  PartSource var7 = new PartSource();
                  this.currentPart = var7;
                  return new Part(var2, Okio.buffer(var7));
               case 1:
                  if (whitespace) {
                     throw new ProtocolException("unexpected characters after boundary");
                  }

                  if (this.partCount == 0) {
                     throw new ProtocolException("expected at least 1 part");
                  }

                  this.noMoreParts = true;
                  return null;
               case 2:
               case 3:
                  whitespace = true;
            }
         }
      }
   }

   private final long currentPartBytesRemaining(long maxByteCount) {
      long toIndex = Math.min(this.source.getBuffer().size(), maxByteCount) + 1L;
      long boundaryIndex = this.source.indexOf(this.crlfDashDashBoundary, 0L, toIndex);
      long var10000;
      if (boundaryIndex != -1L) {
         var10000 = boundaryIndex;
      } else {
         if (this.source.getBuffer().size() < toIndex) {
            throw new EOFException();
         }

         var10000 = Math.min(toIndex, maxByteCount);
      }

      return var10000;
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         this.currentPart = null;
         this.source.close();
      }
   }

   static {
      Options.Companion var10000 = Options.Companion;
      ByteString[] var0 = new ByteString[]{ByteString.Companion.encodeUtf8("\r\n"), ByteString.Companion.encodeUtf8("--"), ByteString.Companion.encodeUtf8(" "), ByteString.Companion.encodeUtf8("\t")};
      afterBoundaryOptions = var10000.of(var0);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0006\u001a\u00020\u0007H\u0016J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\tH\u0016J\b\u0010\u0004\u001a\u00020\u0005H\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\r"},
      d2 = {"Lokhttp3/MultipartReader$PartSource;", "Lokio/Source;", "<init>", "(Lokhttp3/MultipartReader;)V", "timeout", "Lokio/Timeout;", "close", "", "read", "", "sink", "Lokio/Buffer;", "byteCount", "okhttp"}
   )
   @SourceDebugExtension({"SMAP\nMultipartReader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartReader.kt\nokhttp3/MultipartReader$PartSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Timeout.kt\nokio/Timeout\n*L\n1#1,228:1\n1#2:229\n302#3,26:230\n*S KotlinDebug\n*F\n+ 1 MultipartReader.kt\nokhttp3/MultipartReader$PartSource\n*L\n168#1:230,26\n*E\n"})
   private final class PartSource implements Source {
      @NotNull
      private final Timeout timeout = new Timeout();

      public PartSource() {
      }

      public void close() {
         if (Intrinsics.areEqual((Object)MultipartReader.this.currentPart, (Object)this)) {
            MultipartReader.this.currentPart = null;
         }

      }

      public long read(@NotNull Buffer sink, long byteCount) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         if (byteCount < 0L) {
            int var31 = 0;
            String var32 = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException(var32.toString());
         } else if (!Intrinsics.areEqual((Object)MultipartReader.this.currentPart, (Object)this)) {
            int var29 = 0;
            String other$iv = "closed";
            throw new IllegalStateException(other$iv.toString());
         } else {
            Timeout this_$iv = MultipartReader.this.source.timeout();
            Timeout var5 = this.timeout;
            MultipartReader var6 = MultipartReader.this;
            int $i$f$intersectWith = 0;
            long originalTimeout$iv = this_$iv.timeoutNanos();
            this_$iv.timeout(Timeout.Companion.minTimeout(var5.timeoutNanos(), this_$iv.timeoutNanos()), TimeUnit.NANOSECONDS);
            long var10000;
            if (this_$iv.hasDeadline()) {
               long originalDeadline$iv = this_$iv.deadlineNanoTime();
               if (var5.hasDeadline()) {
                  this_$iv.deadlineNanoTime(Math.min(this_$iv.deadlineNanoTime(), var5.deadlineNanoTime()));
               }

               long var33;
               try {
                  int var12 = 0;
                  long limit = var6.currentPartBytesRemaining(byteCount);
                  var33 = limit == 0L ? -1L : var6.source.read(sink, limit);
               } finally {
                  this_$iv.timeout(originalTimeout$iv, TimeUnit.NANOSECONDS);
                  if (var5.hasDeadline()) {
                     this_$iv.deadlineNanoTime(originalDeadline$iv);
                  }

               }

               var10000 = var33;
            } else {
               if (var5.hasDeadline()) {
                  this_$iv.deadlineNanoTime(var5.deadlineNanoTime());
               }

               long var20;
               try {
                  int var17 = 0;
                  long limit = var6.currentPartBytesRemaining(byteCount);
                  var20 = limit == 0L ? -1L : var6.source.read(sink, limit);
               } finally {
                  this_$iv.timeout(originalTimeout$iv, TimeUnit.NANOSECONDS);
                  if (var5.hasDeadline()) {
                     this_$iv.clearDeadline();
                  }

               }

               var10000 = var20;
            }

            return var10000;
         }
      }

      @NotNull
      public Timeout timeout() {
         return this.timeout;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\n\u001a\u00020\u000bH\u0096\u0001R\u0013\u0010\u0002\u001a\u00020\u00038\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\bR\u0013\u0010\u0004\u001a\u00020\u00058\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\t¨\u0006\f"},
      d2 = {"Lokhttp3/MultipartReader$Part;", "Ljava/io/Closeable;", "headers", "Lokhttp3/Headers;", "body", "Lokio/BufferedSource;", "<init>", "(Lokhttp3/Headers;Lokio/BufferedSource;)V", "()Lokhttp3/Headers;", "()Lokio/BufferedSource;", "close", "", "okhttp"}
   )
   public static final class Part implements Closeable {
      @NotNull
      private final Headers headers;
      @NotNull
      private final BufferedSource body;

      public Part(@NotNull Headers headers, @NotNull BufferedSource body) {
         Intrinsics.checkNotNullParameter(headers, "headers");
         Intrinsics.checkNotNullParameter(body, "body");
         super();
         this.headers = headers;
         this.body = body;
      }

      @JvmName(
         name = "headers"
      )
      @NotNull
      public final Headers headers() {
         return this.headers;
      }

      @JvmName(
         name = "body"
      )
      @NotNull
      public final BufferedSource body() {
         return this.body;
      }

      public void close() {
         this.body.close();
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0080\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"},
      d2 = {"Lokhttp3/MultipartReader$Companion;", "", "<init>", "()V", "afterBoundaryOptions", "Lokio/Options;", "getAfterBoundaryOptions", "()Lokio/Options;", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Options getAfterBoundaryOptions() {
         return MultipartReader.afterBoundaryOptions;
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
