package okio;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\u0018\u0000 \u001b2\u00020\u00012\u00020\u0002:\u0001\u001bB\u0019\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007B\u0019\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\u0006\u0010\nB\u0019\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\u0006\u0010\rB!\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\u0006\u0010\u0010J\u0018\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J\r\u0010\u0018\u001a\u00020\u000fH\u0007¢\u0006\u0002\b\u001aR\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0018\u001a\u00020\u000f8G¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019¨\u0006\u001c"},
   d2 = {"Lokio/HashingSink;", "Lokio/ForwardingSink;", "Lokio/Sink;", "sink", "digest", "Ljava/security/MessageDigest;", "<init>", "(Lokio/Sink;Ljava/security/MessageDigest;)V", "algorithm", "", "(Lokio/Sink;Ljava/lang/String;)V", "mac", "Ljavax/crypto/Mac;", "(Lokio/Sink;Ljavax/crypto/Mac;)V", "key", "Lokio/ByteString;", "(Lokio/Sink;Lokio/ByteString;Ljava/lang/String;)V", "messageDigest", "write", "", "source", "Lokio/Buffer;", "byteCount", "", "hash", "()Lokio/ByteString;", "-deprecated_hash", "Companion", "okio"}
)
@SourceDebugExtension({"SMAP\nHashingSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HashingSink.kt\nokio/HashingSink\n+ 2 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,149:1\n85#2:150\n*S KotlinDebug\n*F\n+ 1 HashingSink.kt\nokio/HashingSink\n*L\n76#1:150\n*E\n"})
public final class HashingSink extends ForwardingSink implements Sink {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @Nullable
   private final MessageDigest messageDigest;
   @Nullable
   private final Mac mac;

   public HashingSink(@NotNull Sink sink, @NotNull MessageDigest digest) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Intrinsics.checkNotNullParameter(digest, "digest");
      super(sink);
      this.messageDigest = digest;
      this.mac = null;
   }

   public HashingSink(@NotNull Sink sink, @NotNull String algorithm) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Intrinsics.checkNotNullParameter(algorithm, "algorithm");
      MessageDigest var10002 = MessageDigest.getInstance(algorithm);
      Intrinsics.checkNotNullExpressionValue(var10002, "getInstance(...)");
      this(sink, var10002);
   }

   public HashingSink(@NotNull Sink sink, @NotNull Mac mac) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Intrinsics.checkNotNullParameter(mac, "mac");
      super(sink);
      this.mac = mac;
      this.messageDigest = null;
   }

   public HashingSink(@NotNull Sink sink, @NotNull ByteString key, @NotNull String algorithm) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      Intrinsics.checkNotNullParameter(key, "key");
      Intrinsics.checkNotNullParameter(algorithm, "algorithm");
      Sink var9 = sink;
      HashingSink var8 = this;

      Mac var14;
      HashingSink var10000;
      Sink var10001;
      try {
         var14 = Mac.getInstance(algorithm);
         int var7 = 0;
         var14.init((Key)(new SecretKeySpec(key.toByteArray(), algorithm)));
         Unit var12 = Unit.INSTANCE;
         var10000 = var8;
         var10001 = var9;
         var14 = var14;
      } catch (InvalidKeyException var13) {
         throw new IllegalArgumentException((Throwable)var13);
      }

      Intrinsics.checkNotNull(var14);
      var10000.<init>(var10001, var14);
   }

   public void write(@NotNull Buffer source, long byteCount) throws IOException {
      Intrinsics.checkNotNullParameter(source, "source");
      -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);
      long hashedCount = 0L;
      Segment var10000 = source.head;
      Intrinsics.checkNotNull(var10000);

      for(Segment s = var10000; hashedCount < byteCount; s = var10000) {
         long a$iv = byteCount - hashedCount;
         int b$iv = s.limit - s.pos;
         int $i$f$minOf = 0;
         int toHash = (int)Math.min(a$iv, (long)b$iv);
         if (this.messageDigest != null) {
            this.messageDigest.update(s.data, s.pos, toHash);
         } else {
            Mac var12 = this.mac;
            Intrinsics.checkNotNull(var12);
            var12.update(s.data, s.pos, toHash);
         }

         hashedCount += (long)toHash;
         var10000 = s.next;
         Intrinsics.checkNotNull(var10000);
      }

      super.write(source, byteCount);
   }

   @JvmName(
      name = "hash"
   )
   @NotNull
   public final ByteString hash() {
      byte[] var10000;
      if (this.messageDigest != null) {
         var10000 = this.messageDigest.digest();
      } else {
         Mac var2 = this.mac;
         Intrinsics.checkNotNull(var2);
         var10000 = var2.doFinal();
      }

      byte[] result = var10000;
      Intrinsics.checkNotNull(result);
      return new ByteString(result);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "hash",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_hash"
   )
   @NotNull
   public final ByteString _deprecated_hash/* $FF was: -deprecated_hash*/() {
      return this.hash();
   }

   @JvmStatic
   @NotNull
   public static final HashingSink md5(@NotNull Sink sink) {
      return Companion.md5(sink);
   }

   @JvmStatic
   @NotNull
   public static final HashingSink sha1(@NotNull Sink sink) {
      return Companion.sha1(sink);
   }

   @JvmStatic
   @NotNull
   public static final HashingSink sha256(@NotNull Sink sink) {
      return Companion.sha256(sink);
   }

   @JvmStatic
   @NotNull
   public static final HashingSink sha512(@NotNull Sink sink) {
      return Companion.sha512(sink);
   }

   @JvmStatic
   @NotNull
   public static final HashingSink hmacSha1(@NotNull Sink sink, @NotNull ByteString key) {
      return Companion.hmacSha1(sink, key);
   }

   @JvmStatic
   @NotNull
   public static final HashingSink hmacSha256(@NotNull Sink sink, @NotNull ByteString key) {
      return Companion.hmacSha256(sink, key);
   }

   @JvmStatic
   @NotNull
   public static final HashingSink hmacSha512(@NotNull Sink sink, @NotNull ByteString key) {
      return Companion.hmacSha512(sink, key);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0010\u0010\t\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0010\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007J\u0018\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\rH\u0007J\u0018\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\rH\u0007J\u0018\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\rH\u0007¨\u0006\u0010"},
      d2 = {"Lokio/HashingSink$Companion;", "", "<init>", "()V", "md5", "Lokio/HashingSink;", "sink", "Lokio/Sink;", "sha1", "sha256", "sha512", "hmacSha1", "key", "Lokio/ByteString;", "hmacSha256", "hmacSha512", "okio"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final HashingSink md5(@NotNull Sink sink) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         return new HashingSink(sink, "MD5");
      }

      @JvmStatic
      @NotNull
      public final HashingSink sha1(@NotNull Sink sink) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         return new HashingSink(sink, "SHA-1");
      }

      @JvmStatic
      @NotNull
      public final HashingSink sha256(@NotNull Sink sink) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         return new HashingSink(sink, "SHA-256");
      }

      @JvmStatic
      @NotNull
      public final HashingSink sha512(@NotNull Sink sink) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         return new HashingSink(sink, "SHA-512");
      }

      @JvmStatic
      @NotNull
      public final HashingSink hmacSha1(@NotNull Sink sink, @NotNull ByteString key) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         Intrinsics.checkNotNullParameter(key, "key");
         return new HashingSink(sink, key, "HmacSHA1");
      }

      @JvmStatic
      @NotNull
      public final HashingSink hmacSha256(@NotNull Sink sink, @NotNull ByteString key) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         Intrinsics.checkNotNullParameter(key, "key");
         return new HashingSink(sink, key, "HmacSHA256");
      }

      @JvmStatic
      @NotNull
      public final HashingSink hmacSha512(@NotNull Sink sink, @NotNull ByteString key) {
         Intrinsics.checkNotNullParameter(sink, "sink");
         Intrinsics.checkNotNullParameter(key, "key");
         return new HashingSink(sink, key, "HmacSHA512");
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
