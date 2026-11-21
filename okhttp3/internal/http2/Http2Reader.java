package okhttp3.internal.http2;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntProgression;
import kotlin.ranges.RangesKt;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import okio.Source;
import okio.Timeout;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000e\u0018\u0000 &2\u00020\u0001:\u0003$%&B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0016\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fJ(\u0010\u0012\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J.\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00190\u00182\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J(\u0010\u001b\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J(\u0010\u001c\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J\u0018\u0010\u001c\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J(\u0010\u001d\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J(\u0010\u001e\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J(\u0010\u001f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J(\u0010 \u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J(\u0010!\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J(\u0010\"\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u0014H\u0002J\b\u0010#\u001a\u00020\rH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006'"},
   d2 = {"Lokhttp3/internal/http2/Http2Reader;", "Ljava/io/Closeable;", "source", "Lokio/BufferedSource;", "client", "", "<init>", "(Lokio/BufferedSource;Z)V", "continuation", "Lokhttp3/internal/http2/Http2Reader$ContinuationSource;", "hpackReader", "Lokhttp3/internal/http2/Hpack$Reader;", "readConnectionPreface", "", "handler", "Lokhttp3/internal/http2/Http2Reader$Handler;", "nextFrame", "requireSettings", "readHeaders", "length", "", "flags", "streamId", "readHeaderBlock", "", "Lokhttp3/internal/http2/Header;", "padding", "readData", "readPriority", "readRstStream", "readSettings", "readPushPromise", "readPing", "readGoAway", "readWindowUpdate", "close", "ContinuationSource", "Handler", "Companion", "okhttp"}
)
public final class Http2Reader implements Closeable {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final BufferedSource source;
   private final boolean client;
   @NotNull
   private final ContinuationSource continuation;
   @NotNull
   private final Hpack.Reader hpackReader;
   @NotNull
   private static final Logger logger;

   public Http2Reader(@NotNull BufferedSource source, boolean client) {
      Intrinsics.checkNotNullParameter(source, "source");
      super();
      this.source = source;
      this.client = client;
      this.continuation = new ContinuationSource(this.source);
      this.hpackReader = new Hpack.Reader(this.continuation, 4096, 0, 4, (DefaultConstructorMarker)null);
   }

   public final void readConnectionPreface(@NotNull Handler handler) throws IOException {
      Intrinsics.checkNotNullParameter(handler, "handler");
      if (this.client) {
         if (!this.nextFrame(true, handler)) {
            throw new IOException("Required SETTINGS preface not received");
         }
      } else {
         ByteString connectionPreface = this.source.readByteString((long)Http2.CONNECTION_PREFACE.size());
         if (logger.isLoggable(Level.FINE)) {
            logger.fine(_UtilJvmKt.format("<< CONNECTION " + connectionPreface.hex()));
         }

         if (!Intrinsics.areEqual((Object)Http2.CONNECTION_PREFACE, (Object)connectionPreface)) {
            throw new IOException("Expected a connection header but was " + connectionPreface.utf8());
         }
      }

   }

   public final boolean nextFrame(boolean requireSettings, @NotNull Handler handler) throws IOException {
      Intrinsics.checkNotNullParameter(handler, "handler");

      try {
         this.source.require(9L);
      } catch (EOFException var7) {
         return false;
      }

      int length = _UtilCommonKt.readMedium(this.source);
      if (length > 16384) {
         throw new IOException("FRAME_SIZE_ERROR: " + length);
      } else {
         int type = _UtilCommonKt.and((byte)this.source.readByte(), 255);
         int flags = _UtilCommonKt.and((byte)this.source.readByte(), 255);
         int streamId = this.source.readInt() & Integer.MAX_VALUE;
         if (type != 8 && logger.isLoggable(Level.FINE)) {
            logger.fine(Http2.INSTANCE.frameLog(true, streamId, length, type, flags));
         }

         if (requireSettings && type != 4) {
            throw new IOException("Expected a SETTINGS frame but was " + Http2.INSTANCE.formattedType$okhttp(type));
         } else {
            switch (type) {
               case 0:
                  this.readData(handler, length, flags, streamId);
                  break;
               case 1:
                  this.readHeaders(handler, length, flags, streamId);
                  break;
               case 2:
                  this.readPriority(handler, length, flags, streamId);
                  break;
               case 3:
                  this.readRstStream(handler, length, flags, streamId);
                  break;
               case 4:
                  this.readSettings(handler, length, flags, streamId);
                  break;
               case 5:
                  this.readPushPromise(handler, length, flags, streamId);
                  break;
               case 6:
                  this.readPing(handler, length, flags, streamId);
                  break;
               case 7:
                  this.readGoAway(handler, length, flags, streamId);
                  break;
               case 8:
                  this.readWindowUpdate(handler, length, flags, streamId);
                  break;
               default:
                  this.source.skip((long)length);
            }

            return true;
         }
      }
   }

   private final void readHeaders(Handler handler, int length, int flags, int streamId) throws IOException {
      if (streamId == 0) {
         throw new IOException("PROTOCOL_ERROR: TYPE_HEADERS streamId == 0");
      } else {
         boolean endStream = (flags & 1) != 0;
         int padding = (flags & 8) != 0 ? _UtilCommonKt.and((byte)this.source.readByte(), 255) : 0;
         int headerBlockLength = length;
         if ((flags & 32) != 0) {
            this.readPriority(handler, streamId);
            headerBlockLength = length - 5;
         }

         headerBlockLength = Companion.lengthWithoutPadding(headerBlockLength, flags, padding);
         List headerBlock = this.readHeaderBlock(headerBlockLength, padding, flags, streamId);
         handler.headers(endStream, streamId, -1, headerBlock);
      }
   }

   private final List<Header> readHeaderBlock(int length, int padding, int flags, int streamId) throws IOException {
      this.continuation.setLeft(length);
      this.continuation.setLength(this.continuation.getLeft());
      this.continuation.setPadding(padding);
      this.continuation.setFlags(flags);
      this.continuation.setStreamId(streamId);
      this.hpackReader.readHeaders();
      return this.hpackReader.getAndResetHeaderList();
   }

   private final void readData(Handler handler, int length, int flags, int streamId) throws IOException {
      if (streamId == 0) {
         throw new IOException("PROTOCOL_ERROR: TYPE_DATA streamId == 0");
      } else {
         boolean inFinished = (flags & 1) != 0;
         boolean gzipped = (flags & 32) != 0;
         if (gzipped) {
            throw new IOException("PROTOCOL_ERROR: FLAG_COMPRESSED without SETTINGS_COMPRESS_DATA");
         } else {
            int padding = (flags & 8) != 0 ? _UtilCommonKt.and((byte)this.source.readByte(), 255) : 0;
            int dataLength = Companion.lengthWithoutPadding(length, flags, padding);
            handler.data(inFinished, streamId, this.source, dataLength);
            this.source.skip((long)padding);
         }
      }
   }

   private final void readPriority(Handler handler, int length, int flags, int streamId) throws IOException {
      if (length != 5) {
         throw new IOException("TYPE_PRIORITY length: " + length + " != 5");
      } else if (streamId == 0) {
         throw new IOException("TYPE_PRIORITY streamId == 0");
      } else {
         this.readPriority(handler, streamId);
      }
   }

   private final void readPriority(Handler handler, int streamId) throws IOException {
      int w1 = this.source.readInt();
      boolean exclusive = (w1 & Integer.MIN_VALUE) != 0;
      int streamDependency = w1 & Integer.MAX_VALUE;
      int weight = _UtilCommonKt.and((byte)this.source.readByte(), 255) + 1;
      handler.priority(streamId, streamDependency, weight, exclusive);
   }

   private final void readRstStream(Handler handler, int length, int flags, int streamId) throws IOException {
      if (length != 4) {
         throw new IOException("TYPE_RST_STREAM length: " + length + " != 4");
      } else if (streamId == 0) {
         throw new IOException("TYPE_RST_STREAM streamId == 0");
      } else {
         int errorCodeInt = this.source.readInt();
         ErrorCode var10000 = ErrorCode.Companion.fromHttp2(errorCodeInt);
         if (var10000 == null) {
            throw new IOException("TYPE_RST_STREAM unexpected error code: " + errorCodeInt);
         } else {
            ErrorCode errorCode = var10000;
            handler.rstStream(streamId, errorCode);
         }
      }
   }

   private final void readSettings(Handler handler, int length, int flags, int streamId) throws IOException {
      if (streamId != 0) {
         throw new IOException("TYPE_SETTINGS streamId != 0");
      } else if ((flags & 1) != 0) {
         if (length != 0) {
            throw new IOException("FRAME_SIZE_ERROR ack frame should be empty!");
         } else {
            handler.ackSettings();
         }
      } else if (length % 6 != 0) {
         throw new IOException("TYPE_SETTINGS length % 6 != 0: " + length);
      } else {
         Settings settings = new Settings();
         IntProgression var6 = RangesKt.step(RangesKt.until(0, length), 6);
         int i = var6.getFirst();
         int var8 = var6.getLast();
         int var9 = var6.getStep();
         if (var9 > 0 && i <= var8 || var9 < 0 && var8 <= i) {
            while(true) {
               int id = _UtilCommonKt.and((short)this.source.readShort(), 65535);
               int value = this.source.readInt();
               switch (id) {
                  case 1:
                  case 3:
                  case 6:
                  default:
                     break;
                  case 2:
                     if (value != 0 && value != 1) {
                        throw new IOException("PROTOCOL_ERROR SETTINGS_ENABLE_PUSH != 0 or 1");
                     }
                     break;
                  case 4:
                     if (value < 0) {
                        throw new IOException("PROTOCOL_ERROR SETTINGS_INITIAL_WINDOW_SIZE > 2^31 - 1");
                     }
                     break;
                  case 5:
                     if (value < 16384 || value > 16777215) {
                        throw new IOException("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: " + value);
                     }
               }

               settings.set(id, value);
               if (i == var8) {
                  break;
               }

               i += var9;
            }
         }

         handler.settings(false, settings);
      }
   }

   private final void readPushPromise(Handler handler, int length, int flags, int streamId) throws IOException {
      if (streamId == 0) {
         throw new IOException("PROTOCOL_ERROR: TYPE_PUSH_PROMISE streamId == 0");
      } else {
         int padding = (flags & 8) != 0 ? _UtilCommonKt.and((byte)this.source.readByte(), 255) : 0;
         int promisedStreamId = this.source.readInt() & Integer.MAX_VALUE;
         int headerBlockLength = Companion.lengthWithoutPadding(length - 4, flags, padding);
         List headerBlock = this.readHeaderBlock(headerBlockLength, padding, flags, streamId);
         handler.pushPromise(streamId, promisedStreamId, headerBlock);
      }
   }

   private final void readPing(Handler handler, int length, int flags, int streamId) throws IOException {
      if (length != 8) {
         throw new IOException("TYPE_PING length != 8: " + length);
      } else if (streamId != 0) {
         throw new IOException("TYPE_PING streamId != 0");
      } else {
         int payload1 = this.source.readInt();
         int payload2 = this.source.readInt();
         boolean ack = (flags & 1) != 0;
         handler.ping(ack, payload1, payload2);
      }
   }

   private final void readGoAway(Handler handler, int length, int flags, int streamId) throws IOException {
      if (length < 8) {
         throw new IOException("TYPE_GOAWAY length < 8: " + length);
      } else if (streamId != 0) {
         throw new IOException("TYPE_GOAWAY streamId != 0");
      } else {
         int lastStreamId = this.source.readInt();
         int errorCodeInt = this.source.readInt();
         int opaqueDataLength = length - 8;
         ErrorCode var10000 = ErrorCode.Companion.fromHttp2(errorCodeInt);
         if (var10000 == null) {
            throw new IOException("TYPE_GOAWAY unexpected error code: " + errorCodeInt);
         } else {
            ErrorCode errorCode = var10000;
            ByteString debugData = ByteString.EMPTY;
            if (opaqueDataLength > 0) {
               debugData = this.source.readByteString((long)opaqueDataLength);
            }

            handler.goAway(lastStreamId, errorCode, debugData);
         }
      }
   }

   private final void readWindowUpdate(Handler handler, int length, int flags, int streamId) throws IOException {
      long increment = 0L;

      try {
         if (length != 4) {
            throw new IOException("TYPE_WINDOW_UPDATE length !=4: " + length);
         }

         increment = _UtilCommonKt.and(this.source.readInt(), 2147483647L);
         if (increment == 0L) {
            throw new IOException("windowSizeIncrement was 0");
         }
      } catch (Exception e) {
         logger.fine(Http2.INSTANCE.frameLog(true, streamId, length, 8, flags));
         throw e;
      }

      if (logger.isLoggable(Level.FINE)) {
         logger.fine(Http2.INSTANCE.frameLogWindowUpdate(true, streamId, length, increment));
      }

      handler.windowUpdate(streamId, increment);
   }

   public void close() throws IOException {
      this.source.close();
   }

   static {
      Logger var10000 = Logger.getLogger(Http2.class.getName());
      Intrinsics.checkNotNullExpressionValue(var10000, "getLogger(...)");
      logger = var10000;
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0011\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0018\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0019H\u0016J\b\u0010\u001d\u001a\u00020\u001eH\u0016J\b\u0010\u001f\u001a\u00020 H\u0016J\b\u0010!\u001a\u00020 H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\t\"\u0004\b\u000e\u0010\u000bR\u001a\u0010\u000f\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\t\"\u0004\b\u0011\u0010\u000bR\u001a\u0010\u0012\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\t\"\u0004\b\u0014\u0010\u000bR\u001a\u0010\u0015\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\t\"\u0004\b\u0017\u0010\u000b¨\u0006\""},
      d2 = {"Lokhttp3/internal/http2/Http2Reader$ContinuationSource;", "Lokio/Source;", "source", "Lokio/BufferedSource;", "<init>", "(Lokio/BufferedSource;)V", "length", "", "getLength", "()I", "setLength", "(I)V", "flags", "getFlags", "setFlags", "streamId", "getStreamId", "setStreamId", "left", "getLeft", "setLeft", "padding", "getPadding", "setPadding", "read", "", "sink", "Lokio/Buffer;", "byteCount", "timeout", "Lokio/Timeout;", "close", "", "readContinuationHeader", "okhttp"}
   )
   public static final class ContinuationSource implements Source {
      @NotNull
      private final BufferedSource source;
      private int length;
      private int flags;
      private int streamId;
      private int left;
      private int padding;

      public ContinuationSource(@NotNull BufferedSource source) {
         Intrinsics.checkNotNullParameter(source, "source");
         super();
         this.source = source;
      }

      public final int getLength() {
         return this.length;
      }

      public final void setLength(int var1) {
         this.length = var1;
      }

      public final int getFlags() {
         return this.flags;
      }

      public final void setFlags(int var1) {
         this.flags = var1;
      }

      public final int getStreamId() {
         return this.streamId;
      }

      public final void setStreamId(int var1) {
         this.streamId = var1;
      }

      public final int getLeft() {
         return this.left;
      }

      public final void setLeft(int var1) {
         this.left = var1;
      }

      public final int getPadding() {
         return this.padding;
      }

      public final void setPadding(int var1) {
         this.padding = var1;
      }

      public long read(@NotNull Buffer sink, long byteCount) throws IOException {
         Intrinsics.checkNotNullParameter(sink, "sink");

         while(this.left == 0) {
            this.source.skip((long)this.padding);
            this.padding = 0;
            if ((this.flags & 4) != 0) {
               return -1L;
            }

            this.readContinuationHeader();
         }

         long read = this.source.read(sink, Math.min(byteCount, (long)this.left));
         if (read == -1L) {
            return -1L;
         } else {
            this.left -= (int)read;
            return read;
         }
      }

      @NotNull
      public Timeout timeout() {
         return this.source.timeout();
      }

      public void close() throws IOException {
      }

      private final void readContinuationHeader() throws IOException {
         int previousStreamId = this.streamId;
         this.left = _UtilCommonKt.readMedium(this.source);
         this.length = this.left;
         int type = _UtilCommonKt.and((byte)this.source.readByte(), 255);
         this.flags = _UtilCommonKt.and((byte)this.source.readByte(), 255);
         if (Http2Reader.Companion.getLogger().isLoggable(Level.FINE)) {
            Http2Reader.Companion.getLogger().fine(Http2.INSTANCE.frameLog(true, this.streamId, this.length, type, this.flags));
         }

         this.streamId = this.source.readInt() & Integer.MAX_VALUE;
         if (type != 9) {
            throw new IOException(type + " != TYPE_CONTINUATION");
         } else if (this.streamId != previousStreamId) {
            throw new IOException("TYPE_CONTINUATION streamId changed");
         }
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\r"},
      d2 = {"Lokhttp3/internal/http2/Http2Reader$Companion;", "", "<init>", "()V", "logger", "Ljava/util/logging/Logger;", "getLogger", "()Ljava/util/logging/Logger;", "lengthWithoutPadding", "", "length", "flags", "padding", "okhttp"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Logger getLogger() {
         return Http2Reader.logger;
      }

      public final int lengthWithoutPadding(int length, int flags, int padding) throws IOException {
         int result = length;
         if ((flags & 8) != 0) {
            result = length - 1;
         }

         if (padding > result) {
            throw new IOException("PROTOCOL_ERROR padding " + padding + " > remaining length " + result);
         } else {
            result -= padding;
            return result;
         }
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
      d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\t\n\u0002\u0010\u000e\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J(\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0007H&J.\u0010\u000b\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\u00072\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH&J\u0018\u0010\u0010\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u0012H&J\u0018\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0015H&J\b\u0010\u0016\u001a\u00020\u0003H&J \u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0018\u001a\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u0007H&J \u0010\u001b\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u001d\u001a\u00020\u001eH&J\u0018\u0010\u001f\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010 \u001a\u00020!H&J(\u0010\"\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\u00072\u0006\u0010$\u001a\u00020\u00072\u0006\u0010%\u001a\u00020\u0005H&J&\u0010&\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010'\u001a\u00020\u00072\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH&J8\u0010)\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020\u001e2\u0006\u0010-\u001a\u00020+2\u0006\u0010.\u001a\u00020\u00072\u0006\u0010/\u001a\u00020!H&¨\u00060À\u0006\u0003"},
      d2 = {"Lokhttp3/internal/http2/Http2Reader$Handler;", "", "data", "", "inFinished", "", "streamId", "", "source", "Lokio/BufferedSource;", "length", "headers", "associatedStreamId", "headerBlock", "", "Lokhttp3/internal/http2/Header;", "rstStream", "errorCode", "Lokhttp3/internal/http2/ErrorCode;", "settings", "clearPrevious", "Lokhttp3/internal/http2/Settings;", "ackSettings", "ping", "ack", "payload1", "payload2", "goAway", "lastGoodStreamId", "debugData", "Lokio/ByteString;", "windowUpdate", "windowSizeIncrement", "", "priority", "streamDependency", "weight", "exclusive", "pushPromise", "promisedStreamId", "requestHeaders", "alternateService", "origin", "", "protocol", "host", "port", "maxAge", "okhttp"}
   )
   public interface Handler {
      void data(boolean var1, int var2, @NotNull BufferedSource var3, int var4) throws IOException;

      void headers(boolean var1, int var2, int var3, @NotNull List<Header> var4);

      void rstStream(int var1, @NotNull ErrorCode var2);

      void settings(boolean var1, @NotNull Settings var2);

      void ackSettings();

      void ping(boolean var1, int var2, int var3);

      void goAway(int var1, @NotNull ErrorCode var2, @NotNull ByteString var3);

      void windowUpdate(int var1, long var2);

      void priority(int var1, int var2, int var3, boolean var4);

      void pushPromise(int var1, int var2, @NotNull List<Header> var3) throws IOException;

      void alternateService(int var1, @NotNull String var2, @NotNull ByteString var3, @NotNull String var4, int var5, long var6);
   }
}
