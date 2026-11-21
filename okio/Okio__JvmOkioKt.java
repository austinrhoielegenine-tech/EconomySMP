package okio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okio.internal.DefaultSocket;
import okio.internal.PipeSocket;
import okio.internal.ResourceFileSystem;
import okio.internal.SocketAsyncTimeout;
import org.jetbrains.annotations.NotNull;

// $FF: synthetic class
@Metadata(
   mv = {2, 2, 0},
   k = 5,
   xi = 48,
   d1 = {"\u0000|\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002\u001a\n\u0010\u0003\u001a\u00020\u0004*\u00020\u0005\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0006\u001a\n\u0010\u0003\u001a\u00020\u0004*\u00020\u0006\u001a\u0011\u0010\u0007\u001a\u00020\b*\u00020\u0006H\u0007¢\u0006\u0002\b\t\u001a\u0019\u0010\n\u001a\b\u0012\u0004\u0012\u00020\b0\u000b2\u0006\u0010\f\u001a\u00020\r¢\u0006\u0002\u0010\u000e\u001a\u0016\u0010\u0000\u001a\u00020\u0001*\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0007\u001a\n\u0010\u0012\u001a\u00020\u0001*\u00020\u000f\u001a\n\u0010\u0003\u001a\u00020\u0004*\u00020\u000f\u001a#\u0010\u0000\u001a\u00020\u0001*\u00020\u00132\u0012\u0010\u0014\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00150\u000b\"\u00020\u0015¢\u0006\u0002\u0010\u0016\u001a#\u0010\u0003\u001a\u00020\u0004*\u00020\u00132\u0012\u0010\u0014\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00150\u000b\"\u00020\u0015¢\u0006\u0002\u0010\u0017\u001a\u0012\u0010\u0018\u001a\u00020\u0019*\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u001b\u001a\u0012\u0010\u001c\u001a\u00020\u001d*\u00020\u00042\u0006\u0010\u001a\u001a\u00020\u001b\u001a\u0012\u0010\u001e\u001a\u00020\u001f*\u00020\u00012\u0006\u0010 \u001a\u00020!\u001a\u0012\u0010\"\u001a\u00020#*\u00020\u00042\u0006\u0010 \u001a\u00020!\u001a\u0012\u0010\u001e\u001a\u00020\u001f*\u00020\u00012\u0006\u0010$\u001a\u00020%\u001a\u0012\u0010\"\u001a\u00020#*\u00020\u00042\u0006\u0010$\u001a\u00020%\u001a\n\u0010&\u001a\u00020'*\u00020(¨\u0006)"},
   d2 = {"sink", "Lokio/Sink;", "Ljava/io/OutputStream;", "source", "Lokio/Source;", "Ljava/io/InputStream;", "Ljava/net/Socket;", "asOkioSocket", "Lokio/Socket;", "socket", "inMemorySocketPair", "", "maxBufferSize", "", "(J)[Lokio/Socket;", "Ljava/io/File;", "append", "", "appendingSink", "Ljava/nio/file/Path;", "options", "Ljava/nio/file/OpenOption;", "(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Lokio/Sink;", "(Ljava/nio/file/Path;[Ljava/nio/file/OpenOption;)Lokio/Source;", "cipherSink", "Lokio/CipherSink;", "cipher", "Ljavax/crypto/Cipher;", "cipherSource", "Lokio/CipherSource;", "hashingSink", "Lokio/HashingSink;", "mac", "Ljavax/crypto/Mac;", "hashingSource", "Lokio/HashingSource;", "digest", "Ljava/security/MessageDigest;", "asResourceFileSystem", "Lokio/FileSystem;", "Ljava/lang/ClassLoader;", "okio"},
   xs = "okio/Okio"
)
final class Okio__JvmOkioKt {
   @NotNull
   public static final Sink sink(@NotNull OutputStream $this$sink) {
      Intrinsics.checkNotNullParameter($this$sink, "<this>");
      return new OutputStreamSink($this$sink, new Timeout());
   }

   @NotNull
   public static final Source source(@NotNull InputStream $this$source) {
      Intrinsics.checkNotNullParameter($this$source, "<this>");
      return new InputStreamSource($this$source, new Timeout());
   }

   @NotNull
   public static final Sink sink(@NotNull java.net.Socket $this$sink) throws IOException {
      Intrinsics.checkNotNullParameter($this$sink, "<this>");
      SocketAsyncTimeout timeout = new SocketAsyncTimeout($this$sink);
      OutputStream var10002 = $this$sink.getOutputStream();
      Intrinsics.checkNotNullExpressionValue(var10002, "getOutputStream(...)");
      OutputStreamSink sink = new OutputStreamSink(var10002, timeout);
      return timeout.sink(sink);
   }

   @NotNull
   public static final Source source(@NotNull java.net.Socket $this$source) throws IOException {
      Intrinsics.checkNotNullParameter($this$source, "<this>");
      SocketAsyncTimeout timeout = new SocketAsyncTimeout($this$source);
      InputStream var10002 = $this$source.getInputStream();
      Intrinsics.checkNotNullExpressionValue(var10002, "getInputStream(...)");
      InputStreamSource source = new InputStreamSource(var10002, timeout);
      return timeout.source(source);
   }

   @JvmName(
      name = "socket"
   )
   @NotNull
   public static final Socket socket(@NotNull java.net.Socket $this$asOkioSocket) {
      Intrinsics.checkNotNullParameter($this$asOkioSocket, "<this>");
      return new DefaultSocket($this$asOkioSocket);
   }

   @NotNull
   public static final Socket[] inMemorySocketPair(long maxBufferSize) {
      Pipe ab = new Pipe(maxBufferSize);
      Pipe ba = new Pipe(maxBufferSize);
      Socket[] var4 = new Socket[]{new PipeSocket(ab, ba), new PipeSocket(ba, ab)};
      return var4;
   }

   @JvmOverloads
   @NotNull
   public static final Sink sink(@NotNull File $this$sink, boolean append) throws FileNotFoundException {
      Intrinsics.checkNotNullParameter($this$sink, "<this>");
      return Okio.sink((OutputStream)(new FileOutputStream($this$sink, append)));
   }

   // $FF: synthetic method
   public static Sink sink$default(File var0, boolean var1, int var2, Object var3) throws FileNotFoundException {
      if ((var2 & 1) != 0) {
         var1 = false;
      }

      return Okio.sink(var0, var1);
   }

   @NotNull
   public static final Sink appendingSink(@NotNull File $this$appendingSink) throws FileNotFoundException {
      Intrinsics.checkNotNullParameter($this$appendingSink, "<this>");
      return Okio.sink((OutputStream)(new FileOutputStream($this$appendingSink, true)));
   }

   @NotNull
   public static final Source source(@NotNull File $this$source) throws FileNotFoundException {
      Intrinsics.checkNotNullParameter($this$source, "<this>");
      return new InputStreamSource((InputStream)(new FileInputStream($this$source)), Timeout.NONE);
   }

   @NotNull
   public static final Sink sink(@NotNull java.nio.file.Path $this$sink, @NotNull OpenOption... options) throws IOException {
      Intrinsics.checkNotNullParameter($this$sink, "<this>");
      Intrinsics.checkNotNullParameter(options, "options");
      OutputStream var10000 = Files.newOutputStream($this$sink, (OpenOption[])Arrays.copyOf(options, options.length));
      Intrinsics.checkNotNullExpressionValue(var10000, "newOutputStream(...)");
      return Okio.sink(var10000);
   }

   @NotNull
   public static final Source source(@NotNull java.nio.file.Path $this$source, @NotNull OpenOption... options) throws IOException {
      Intrinsics.checkNotNullParameter($this$source, "<this>");
      Intrinsics.checkNotNullParameter(options, "options");
      InputStream var10000 = Files.newInputStream($this$source, (OpenOption[])Arrays.copyOf(options, options.length));
      Intrinsics.checkNotNullExpressionValue(var10000, "newInputStream(...)");
      return Okio.source(var10000);
   }

   @NotNull
   public static final CipherSink cipherSink(@NotNull Sink $this$cipherSink, @NotNull Cipher cipher) {
      Intrinsics.checkNotNullParameter($this$cipherSink, "<this>");
      Intrinsics.checkNotNullParameter(cipher, "cipher");
      return new CipherSink(Okio.buffer($this$cipherSink), cipher);
   }

   @NotNull
   public static final CipherSource cipherSource(@NotNull Source $this$cipherSource, @NotNull Cipher cipher) {
      Intrinsics.checkNotNullParameter($this$cipherSource, "<this>");
      Intrinsics.checkNotNullParameter(cipher, "cipher");
      return new CipherSource(Okio.buffer($this$cipherSource), cipher);
   }

   @NotNull
   public static final HashingSink hashingSink(@NotNull Sink $this$hashingSink, @NotNull Mac mac) {
      Intrinsics.checkNotNullParameter($this$hashingSink, "<this>");
      Intrinsics.checkNotNullParameter(mac, "mac");
      return new HashingSink($this$hashingSink, mac);
   }

   @NotNull
   public static final HashingSource hashingSource(@NotNull Source $this$hashingSource, @NotNull Mac mac) {
      Intrinsics.checkNotNullParameter($this$hashingSource, "<this>");
      Intrinsics.checkNotNullParameter(mac, "mac");
      return new HashingSource($this$hashingSource, mac);
   }

   @NotNull
   public static final HashingSink hashingSink(@NotNull Sink $this$hashingSink, @NotNull MessageDigest digest) {
      Intrinsics.checkNotNullParameter($this$hashingSink, "<this>");
      Intrinsics.checkNotNullParameter(digest, "digest");
      return new HashingSink($this$hashingSink, digest);
   }

   @NotNull
   public static final HashingSource hashingSource(@NotNull Source $this$hashingSource, @NotNull MessageDigest digest) {
      Intrinsics.checkNotNullParameter($this$hashingSource, "<this>");
      Intrinsics.checkNotNullParameter(digest, "digest");
      return new HashingSource($this$hashingSource, digest);
   }

   @NotNull
   public static final FileSystem asResourceFileSystem(@NotNull ClassLoader $this$asResourceFileSystem) {
      Intrinsics.checkNotNullParameter($this$asResourceFileSystem, "<this>");
      return new ResourceFileSystem($this$asResourceFileSystem, true, (FileSystem)null, 4, (DefaultConstructorMarker)null);
   }

   @JvmOverloads
   @NotNull
   public static final Sink sink(@NotNull File $this$sink) throws FileNotFoundException {
      Intrinsics.checkNotNullParameter($this$sink, "<this>");
      return Okio.sink$default($this$sink, false, 1, (Object)null);
   }
}
