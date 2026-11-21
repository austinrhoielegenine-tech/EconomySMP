package okhttp3.internal.ws;

import java.io.Closeable;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okio.Buffer;
import okio.BufferedSource;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\u0018\u00002\u00020\u0001:\u0001'B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003¢\u0006\u0004\b\n\u0010\u000bJ\u0006\u0010\u001f\u001a\u00020 J\b\u0010!\u001a\u00020 H\u0002J\b\u0010\"\u001a\u00020 H\u0002J\b\u0010#\u001a\u00020 H\u0002J\b\u0010$\u001a\u00020 H\u0002J\b\u0010%\u001a\u00020 H\u0002J\b\u0010&\u001a\u00020 H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0017X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u001cX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u001eX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006("},
   d2 = {"Lokhttp3/internal/ws/WebSocketReader;", "Ljava/io/Closeable;", "isClient", "", "source", "Lokio/BufferedSource;", "frameCallback", "Lokhttp3/internal/ws/WebSocketReader$FrameCallback;", "perMessageDeflate", "noContextTakeover", "<init>", "(ZLokio/BufferedSource;Lokhttp3/internal/ws/WebSocketReader$FrameCallback;ZZ)V", "getSource", "()Lokio/BufferedSource;", "closed", "opcode", "", "frameLength", "", "isFinalFrame", "isControlFrame", "readingCompressedMessage", "controlFrameBuffer", "Lokio/Buffer;", "messageFrameBuffer", "messageInflater", "Lokhttp3/internal/ws/MessageInflater;", "maskKey", "", "maskCursor", "Lokio/Buffer$UnsafeCursor;", "processNextFrame", "", "readHeader", "readControlFrame", "readMessageFrame", "readUntilNonControlFrame", "readMessage", "close", "FrameCallback", "okhttp"}
)
@SourceDebugExtension({"SMAP\nWebSocketReader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WebSocketReader.kt\nokhttp3/internal/ws/WebSocketReader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,310:1\n1#2:311\n*E\n"})
public final class WebSocketReader implements Closeable {
   private final boolean isClient;
   @NotNull
   private final BufferedSource source;
   @NotNull
   private final FrameCallback frameCallback;
   private final boolean perMessageDeflate;
   private final boolean noContextTakeover;
   private boolean closed;
   private int opcode;
   private long frameLength;
   private boolean isFinalFrame;
   private boolean isControlFrame;
   private boolean readingCompressedMessage;
   @NotNull
   private final Buffer controlFrameBuffer;
   @NotNull
   private final Buffer messageFrameBuffer;
   @Nullable
   private MessageInflater messageInflater;
   @Nullable
   private final byte[] maskKey;
   @Nullable
   private final Buffer.UnsafeCursor maskCursor;

   public WebSocketReader(boolean isClient, @NotNull BufferedSource source, @NotNull FrameCallback frameCallback, boolean perMessageDeflate, boolean noContextTakeover) {
      Intrinsics.checkNotNullParameter(source, "source");
      Intrinsics.checkNotNullParameter(frameCallback, "frameCallback");
      super();
      this.isClient = isClient;
      this.source = source;
      this.frameCallback = frameCallback;
      this.perMessageDeflate = perMessageDeflate;
      this.noContextTakeover = noContextTakeover;
      this.controlFrameBuffer = new Buffer();
      this.messageFrameBuffer = new Buffer();
      this.maskKey = this.isClient ? null : new byte[4];
      this.maskCursor = this.isClient ? null : new Buffer.UnsafeCursor();
   }

   @NotNull
   public final BufferedSource getSource() {
      return this.source;
   }

   public final void processNextFrame() throws IOException {
      this.readHeader();
      if (this.isControlFrame) {
         this.readControlFrame();
      } else {
         this.readMessageFrame();
      }

   }

   private final void readHeader() throws IOException, ProtocolException {
      if (this.closed) {
         throw new IOException("closed");
      } else {
         int b0 = 0;
         long timeoutBefore = this.source.timeout().timeoutNanos();
         this.source.timeout().clearTimeout();

         try {
            b0 = _UtilCommonKt.and((byte)this.source.readByte(), 255);
         } finally {
            this.source.timeout().timeout(timeoutBefore, TimeUnit.NANOSECONDS);
         }

         this.opcode = b0 & 15;
         this.isFinalFrame = (b0 & 128) != 0;
         this.isControlFrame = (b0 & 8) != 0;
         if (this.isControlFrame && !this.isFinalFrame) {
            throw new ProtocolException("Control frames must be final.");
         } else {
            boolean reservedFlag1 = (b0 & 64) != 0;
            switch (this.opcode) {
               case 1:
               case 2:
                  boolean var10001;
                  if (reservedFlag1) {
                     if (!this.perMessageDeflate) {
                        throw new ProtocolException("Unexpected rsv1 flag");
                     }

                     var10001 = true;
                  } else {
                     var10001 = false;
                  }

                  this.readingCompressedMessage = var10001;
                  break;
               default:
                  if (reservedFlag1) {
                     throw new ProtocolException("Unexpected rsv1 flag");
                  }
            }

            boolean reservedFlag2 = (b0 & 32) != 0;
            if (reservedFlag2) {
               throw new ProtocolException("Unexpected rsv2 flag");
            } else {
               boolean reservedFlag3 = (b0 & 16) != 0;
               if (reservedFlag3) {
                  throw new ProtocolException("Unexpected rsv3 flag");
               } else {
                  int b1 = _UtilCommonKt.and((byte)this.source.readByte(), 255);
                  boolean isMasked = (b1 & 128) != 0;
                  if (isMasked == this.isClient) {
                     throw new ProtocolException(this.isClient ? "Server-sent frames must not be masked." : "Client-sent frames must be masked.");
                  } else {
                     this.frameLength = (long)(b1 & 127);
                     if (this.frameLength == 126L) {
                        this.frameLength = (long)_UtilCommonKt.and((short)this.source.readShort(), 65535);
                     } else if (this.frameLength == 127L) {
                        this.frameLength = this.source.readLong();
                        if (this.frameLength < 0L) {
                           throw new ProtocolException("Frame length 0x" + _UtilJvmKt.toHexString(this.frameLength) + " > 0x7FFFFFFFFFFFFFFF");
                        }
                     }

                     if (this.isControlFrame && this.frameLength > 125L) {
                        throw new ProtocolException("Control frame must be less than 125B.");
                     } else {
                        if (isMasked) {
                           BufferedSource var10000 = this.source;
                           byte[] var12 = this.maskKey;
                           Intrinsics.checkNotNull(var12);
                           var10000.readFully(var12);
                        }

                     }
                  }
               }
            }
         }
      }
   }

   private final void readControlFrame() throws IOException {
      if (this.frameLength > 0L) {
         this.source.readFully(this.controlFrameBuffer, this.frameLength);
         if (!this.isClient) {
            Buffer var10000 = this.controlFrameBuffer;
            Buffer.UnsafeCursor var10001 = this.maskCursor;
            Intrinsics.checkNotNull(var10001);
            var10000.readAndWriteUnsafe(var10001);
            this.maskCursor.seek(0L);
            WebSocketProtocol var6 = WebSocketProtocol.INSTANCE;
            var10001 = this.maskCursor;
            byte[] var10002 = this.maskKey;
            Intrinsics.checkNotNull(var10002);
            var6.toggleMask(var10001, var10002);
            this.maskCursor.close();
         }
      }

      switch (this.opcode) {
         case 8:
            int code = 1005;
            String reason = "";
            long bufferSize = this.controlFrameBuffer.size();
            if (bufferSize == 1L) {
               throw new ProtocolException("Malformed close payload length of 1.");
            }

            if (bufferSize != 0L) {
               code = this.controlFrameBuffer.readShort();
               reason = this.controlFrameBuffer.readUtf8();
               String codeExceptionMessage = WebSocketProtocol.INSTANCE.closeCodeExceptionMessage(code);
               if (codeExceptionMessage != null) {
                  throw new ProtocolException(codeExceptionMessage);
               }
            }

            this.frameCallback.onReadClose(code, reason);
            this.closed = true;
            break;
         case 9:
            this.frameCallback.onReadPing(this.controlFrameBuffer.readByteString());
            break;
         case 10:
            this.frameCallback.onReadPong(this.controlFrameBuffer.readByteString());
            break;
         default:
            throw new ProtocolException("Unknown control opcode: " + _UtilJvmKt.toHexString(this.opcode));
      }

   }

   private final void readMessageFrame() throws IOException {
      int opcode = this.opcode;
      if (opcode != 1 && opcode != 2) {
         throw new ProtocolException("Unknown opcode: " + _UtilJvmKt.toHexString(opcode));
      } else {
         this.readMessage();
         if (this.readingCompressedMessage) {
            MessageInflater var10000 = this.messageInflater;
            if (var10000 == null) {
               MessageInflater it = new MessageInflater(this.noContextTakeover);
               int var5 = 0;
               this.messageInflater = it;
               var10000 = it;
            }

            MessageInflater messageInflater = var10000;
            messageInflater.inflate(this.messageFrameBuffer);
         }

         if (opcode == 1) {
            this.frameCallback.onReadMessage(this.messageFrameBuffer.readUtf8());
         } else {
            this.frameCallback.onReadMessage(this.messageFrameBuffer.readByteString());
         }

      }
   }

   private final void readUntilNonControlFrame() throws IOException {
      while(true) {
         if (!this.closed) {
            this.readHeader();
            if (this.isControlFrame) {
               this.readControlFrame();
               continue;
            }
         }

         return;
      }
   }

   private final void readMessage() throws IOException {
      while(!this.closed) {
         if (this.frameLength > 0L) {
            this.source.readFully(this.messageFrameBuffer, this.frameLength);
            if (!this.isClient) {
               Buffer var10000 = this.messageFrameBuffer;
               Buffer.UnsafeCursor var10001 = this.maskCursor;
               Intrinsics.checkNotNull(var10001);
               var10000.readAndWriteUnsafe(var10001);
               this.maskCursor.seek(this.messageFrameBuffer.size() - this.frameLength);
               WebSocketProtocol var1 = WebSocketProtocol.INSTANCE;
               var10001 = this.maskCursor;
               byte[] var10002 = this.maskKey;
               Intrinsics.checkNotNull(var10002);
               var1.toggleMask(var10001, var10002);
               this.maskCursor.close();
            }
         }

         if (!this.isFinalFrame) {
            this.readUntilNonControlFrame();
            if (this.opcode == 0) {
               continue;
            }

            throw new ProtocolException("Expected continuation opcode. Got: " + _UtilJvmKt.toHexString(this.opcode));
         }

         return;
      }

      throw new IOException("closed");
   }

   public void close() throws IOException {
      MessageInflater var10000 = this.messageInflater;
      if (var10000 != null) {
         _UtilCommonKt.closeQuietly(var10000);
      }

      _UtilCommonKt.closeQuietly(this.source);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H&J\u0010\u0010\b\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u0007H&J\u0010\u0010\n\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u0007H&J\u0018\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0005H&¨\u0006\u000fÀ\u0006\u0003"},
      d2 = {"Lokhttp3/internal/ws/WebSocketReader$FrameCallback;", "", "onReadMessage", "", "text", "", "bytes", "Lokio/ByteString;", "onReadPing", "payload", "onReadPong", "onReadClose", "code", "", "reason", "okhttp"}
   )
   public interface FrameCallback {
      void onReadMessage(@NotNull String var1) throws IOException;

      void onReadMessage(@NotNull ByteString var1) throws IOException;

      void onReadPing(@NotNull ByteString var1);

      void onReadPong(@NotNull ByteString var1);

      void onReadClose(int var1, @NotNull String var2);
   }
}
