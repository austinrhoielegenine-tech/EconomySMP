package okhttp3.internal.ws;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilCommonKt;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\b\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rJ\u000e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fJ\u000e\u0010 \u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fJ\u0018\u0010!\u001a\u00020\u001d2\u0006\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u001fJ\u0018\u0010%\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020#2\u0006\u0010\u001e\u001a\u00020\u001fH\u0002J\u0016\u0010'\u001a\u00020\u001d2\u0006\u0010(\u001a\u00020#2\u0006\u0010)\u001a\u00020\u001fJ\b\u0010*\u001a\u00020\u001dH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\b\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006+"},
   d2 = {"Lokhttp3/internal/ws/WebSocketWriter;", "Ljava/io/Closeable;", "isClient", "", "sink", "Lokio/BufferedSink;", "random", "Ljava/util/Random;", "perMessageDeflate", "noContextTakeover", "minimumDeflateSize", "", "<init>", "(ZLokio/BufferedSink;Ljava/util/Random;ZZJ)V", "getSink", "()Lokio/BufferedSink;", "getRandom", "()Ljava/util/Random;", "messageBuffer", "Lokio/Buffer;", "sinkBuffer", "writerClosed", "messageDeflater", "Lokhttp3/internal/ws/MessageDeflater;", "maskKey", "", "maskCursor", "Lokio/Buffer$UnsafeCursor;", "writePing", "", "payload", "Lokio/ByteString;", "writePong", "writeClose", "code", "", "reason", "writeControlFrame", "opcode", "writeMessageFrame", "formatOpcode", "data", "close", "okhttp"}
)
@SourceDebugExtension({"SMAP\nWebSocketWriter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WebSocketWriter.kt\nokhttp3/internal/ws/WebSocketWriter\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,215:1\n1#2:216\n*E\n"})
public final class WebSocketWriter implements Closeable {
   private final boolean isClient;
   @NotNull
   private final BufferedSink sink;
   @NotNull
   private final Random random;
   private final boolean perMessageDeflate;
   private final boolean noContextTakeover;
   private final long minimumDeflateSize;
   @NotNull
   private final Buffer messageBuffer;
   @NotNull
   private final Buffer sinkBuffer;
   private boolean writerClosed;
   @Nullable
   private MessageDeflater messageDeflater;
   @Nullable
   private final byte[] maskKey;
   @Nullable
   private final Buffer.UnsafeCursor maskCursor;

   public WebSocketWriter(boolean isClient, @NotNull BufferedSink sink, @NotNull Random random, boolean perMessageDeflate, boolean noContextTakeover, long minimumDeflateSize) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Intrinsics.checkNotNullParameter(random, "random");
      super();
      this.isClient = isClient;
      this.sink = sink;
      this.random = random;
      this.perMessageDeflate = perMessageDeflate;
      this.noContextTakeover = noContextTakeover;
      this.minimumDeflateSize = minimumDeflateSize;
      this.messageBuffer = new Buffer();
      this.sinkBuffer = this.sink.getBuffer();
      this.maskKey = this.isClient ? new byte[4] : null;
      this.maskCursor = this.isClient ? new Buffer.UnsafeCursor() : null;
   }

   @NotNull
   public final BufferedSink getSink() {
      return this.sink;
   }

   @NotNull
   public final Random getRandom() {
      return this.random;
   }

   public final void writePing(@NotNull ByteString payload) throws IOException {
      Intrinsics.checkNotNullParameter(payload, "payload");
      this.writeControlFrame(9, payload);
   }

   public final void writePong(@NotNull ByteString payload) throws IOException {
      Intrinsics.checkNotNullParameter(payload, "payload");
      this.writeControlFrame(10, payload);
   }

   public final void writeClose(int code, @Nullable ByteString reason) throws IOException {
      ByteString payload = ByteString.EMPTY;
      if (code != 0 || reason != null) {
         if (code != 0) {
            WebSocketProtocol.INSTANCE.validateCloseCode(code);
         }

         Buffer $this$writeClose_u24lambda_u240 = new Buffer();
         int var6 = 0;
         $this$writeClose_u24lambda_u240.writeShort(code);
         if (reason != null) {
            $this$writeClose_u24lambda_u240.write(reason);
         }

         payload = $this$writeClose_u24lambda_u240.readByteString();
      }

      try {
         this.writeControlFrame(8, payload);
      } finally {
         this.writerClosed = true;
      }

   }

   private final void writeControlFrame(int opcode, ByteString payload) throws IOException {
      if (this.writerClosed) {
         throw new IOException("closed");
      } else {
         int length = payload.size();
         if ((long)length > 125L) {
            int b1 = 0;
            String var9 = "Payload size must be less than or equal to 125";
            throw new IllegalArgumentException(var9.toString());
         } else {
            int b0 = 128 | opcode;
            this.sinkBuffer.writeByte(b0);
            if (this.isClient) {
               int var5 = length | 128;
               this.sinkBuffer.writeByte(var5);
               Random var10000 = this.random;
               byte[] var10001 = this.maskKey;
               Intrinsics.checkNotNull(var10001);
               var10000.nextBytes(var10001);
               this.sinkBuffer.write(this.maskKey);
               if (length > 0) {
                  long payloadStart = this.sinkBuffer.size();
                  this.sinkBuffer.write(payload);
                  Buffer var10 = this.sinkBuffer;
                  Buffer.UnsafeCursor var11 = this.maskCursor;
                  Intrinsics.checkNotNull(var11);
                  var10.readAndWriteUnsafe(var11);
                  this.maskCursor.seek(payloadStart);
                  WebSocketProtocol.INSTANCE.toggleMask(this.maskCursor, this.maskKey);
                  this.maskCursor.close();
               }
            } else {
               this.sinkBuffer.writeByte(length);
               this.sinkBuffer.write(payload);
            }

            this.sink.flush();
         }
      }
   }

   public final void writeMessageFrame(int formatOpcode, @NotNull ByteString data) throws IOException {
      Intrinsics.checkNotNullParameter(data, "data");
      if (this.writerClosed) {
         throw new IOException("closed");
      } else {
         this.messageBuffer.write(data);
         int b0 = formatOpcode | 128;
         if (this.perMessageDeflate && (long)data.size() >= this.minimumDeflateSize) {
            MessageDeflater var10000 = this.messageDeflater;
            if (var10000 == null) {
               MessageDeflater it = new MessageDeflater(this.noContextTakeover);
               int var8 = 0;
               this.messageDeflater = it;
               var10000 = it;
            }

            MessageDeflater messageDeflater = var10000;
            messageDeflater.deflate(this.messageBuffer);
            b0 |= 64;
         }

         long dataSize = this.messageBuffer.size();
         this.sinkBuffer.writeByte(b0);
         int b1 = 0;
         if (this.isClient) {
            b1 |= 128;
         }

         if (dataSize <= 125L) {
            b1 |= (int)dataSize;
            this.sinkBuffer.writeByte(b1);
         } else if (dataSize <= 65535L) {
            b1 |= 126;
            this.sinkBuffer.writeByte(b1);
            this.sinkBuffer.writeShort((int)dataSize);
         } else {
            b1 |= 127;
            this.sinkBuffer.writeByte(b1);
            this.sinkBuffer.writeLong(dataSize);
         }

         if (this.isClient) {
            Random var14 = this.random;
            byte[] var10001 = this.maskKey;
            Intrinsics.checkNotNull(var10001);
            var14.nextBytes(var10001);
            this.sinkBuffer.write(this.maskKey);
            if (dataSize > 0L) {
               Buffer var15 = this.messageBuffer;
               Buffer.UnsafeCursor var16 = this.maskCursor;
               Intrinsics.checkNotNull(var16);
               var15.readAndWriteUnsafe(var16);
               this.maskCursor.seek(0L);
               WebSocketProtocol.INSTANCE.toggleMask(this.maskCursor, this.maskKey);
               this.maskCursor.close();
            }
         }

         this.sinkBuffer.write(this.messageBuffer, dataSize);
         this.sink.flush();
      }
   }

   public void close() {
      MessageDeflater var10000 = this.messageDeflater;
      if (var10000 != null) {
         _UtilCommonKt.closeQuietly(var10000);
      }

      _UtilCommonKt.closeQuietly(this.sink);
   }
}
