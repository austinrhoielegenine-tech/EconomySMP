package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.ArraysKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import okio.internal.-ByteString;
import okio.internal.-ByteStringNonJs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\u0005\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0016\u0018\u0000 ^2\u00020\u00012\b\u0012\u0004\u0012\u00020\u00000\u0002:\u0001^B\u0011\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\u0010\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J\b\u0010\u0018\u001a\u00020\u0010H\u0016J\u0006\u0010\u0019\u001a\u00020\u0000J\u0006\u0010\u001a\u001a\u00020\u0000J\u0006\u0010\u001b\u001a\u00020\u0000J\u0006\u0010\u001c\u001a\u00020\u0000J\u0015\u0010\u001d\u001a\u00020\u00002\u0006\u0010\u001e\u001a\u00020\u0010H\u0010¢\u0006\u0002\b\u001fJ\u0010\u0010 \u001a\u00020\u00002\u0006\u0010!\u001a\u00020\u0000H\u0016J\u0010\u0010\"\u001a\u00020\u00002\u0006\u0010!\u001a\u00020\u0000H\u0016J\u0010\u0010#\u001a\u00020\u00002\u0006\u0010!\u001a\u00020\u0000H\u0016J\u001d\u0010$\u001a\u00020\u00002\u0006\u0010\u001e\u001a\u00020\u00102\u0006\u0010!\u001a\u00020\u0000H\u0010¢\u0006\u0002\b%J\b\u0010&\u001a\u00020\u0010H\u0016J\b\u0010'\u001a\u00020\u0010H\u0016J\b\u0010(\u001a\u00020\u0000H\u0016J\b\u0010)\u001a\u00020\u0000H\u0016J\u001c\u0010*\u001a\u00020\u00002\b\b\u0002\u0010+\u001a\u00020\n2\b\b\u0002\u0010,\u001a\u00020\nH\u0017J\u0015\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u00020\nH\u0010¢\u0006\u0002\b0J\u0016\u00101\u001a\u00020.2\u0006\u00102\u001a\u00020\nH\u0087\u0002¢\u0006\u0002\b3J\r\u00105\u001a\u00020\nH\u0010¢\u0006\u0002\b6J\b\u00107\u001a\u00020\u0004H\u0016J\r\u00108\u001a\u00020\u0004H\u0010¢\u0006\u0002\b9J\b\u0010:\u001a\u00020;H\u0016J\u0010\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020?H\u0016J%\u0010<\u001a\u00020=2\u0006\u0010@\u001a\u00020A2\u0006\u0010B\u001a\u00020\n2\u0006\u0010C\u001a\u00020\nH\u0010¢\u0006\u0002\bDJ(\u0010E\u001a\u00020F2\u0006\u0010B\u001a\u00020\n2\u0006\u0010G\u001a\u00020\u00002\u0006\u0010H\u001a\u00020\n2\u0006\u0010C\u001a\u00020\nH\u0016J(\u0010E\u001a\u00020F2\u0006\u0010B\u001a\u00020\n2\u0006\u0010G\u001a\u00020\u00042\u0006\u0010H\u001a\u00020\n2\u0006\u0010C\u001a\u00020\nH\u0016J,\u0010I\u001a\u00020=2\b\b\u0002\u0010B\u001a\u00020\n2\u0006\u0010J\u001a\u00020\u00042\b\b\u0002\u0010K\u001a\u00020\n2\u0006\u0010C\u001a\u00020\nH\u0016J\u000e\u0010L\u001a\u00020F2\u0006\u0010M\u001a\u00020\u0000J\u000e\u0010L\u001a\u00020F2\u0006\u0010M\u001a\u00020\u0004J\u000e\u0010N\u001a\u00020F2\u0006\u0010O\u001a\u00020\u0000J\u000e\u0010N\u001a\u00020F2\u0006\u0010O\u001a\u00020\u0004J\u001a\u0010P\u001a\u00020\n2\u0006\u0010G\u001a\u00020\u00002\b\b\u0002\u0010Q\u001a\u00020\nH\u0007J\u001a\u0010P\u001a\u00020\n2\u0006\u0010G\u001a\u00020\u00042\b\b\u0002\u0010Q\u001a\u00020\nH\u0017J\u001a\u0010R\u001a\u00020\n2\u0006\u0010G\u001a\u00020\u00002\b\b\u0002\u0010Q\u001a\u00020\nH\u0007J\u001a\u0010R\u001a\u00020\n2\u0006\u0010G\u001a\u00020\u00042\b\b\u0002\u0010Q\u001a\u00020\nH\u0017J\u0013\u0010S\u001a\u00020F2\b\u0010G\u001a\u0004\u0018\u00010TH\u0096\u0002J\b\u0010\t\u001a\u00020\nH\u0016J\u0011\u0010U\u001a\u00020\n2\u0006\u0010G\u001a\u00020\u0000H\u0096\u0002J\b\u0010V\u001a\u00020\u0010H\u0016J\u0010\u0010W\u001a\u00020=2\u0006\u0010X\u001a\u00020YH\u0002J\u0010\u0010Z\u001a\u00020=2\u0006\u0010>\u001a\u00020[H\u0002J\u0015\u00103\u001a\u00020.2\u0006\u00102\u001a\u00020\nH\u0007¢\u0006\u0002\b\\J\r\u00104\u001a\u00020\nH\u0007¢\u0006\u0002\b]R\u0014\u0010\u0003\u001a\u00020\u0004X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001a\u0010\t\u001a\u00020\nX\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001c\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0080\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u0011\u00104\u001a\u00020\n8G¢\u0006\u0006\u001a\u0004\b4\u0010\f¨\u0006_"},
   d2 = {"Lokio/ByteString;", "Ljava/io/Serializable;", "", "data", "", "<init>", "([B)V", "getData$okio", "()[B", "hashCode", "", "getHashCode$okio", "()I", "setHashCode$okio", "(I)V", "utf8", "", "getUtf8$okio", "()Ljava/lang/String;", "setUtf8$okio", "(Ljava/lang/String;)V", "string", "charset", "Ljava/nio/charset/Charset;", "base64", "md5", "sha1", "sha256", "sha512", "digest", "algorithm", "digest$okio", "hmacSha1", "key", "hmacSha256", "hmacSha512", "hmac", "hmac$okio", "base64Url", "hex", "toAsciiLowercase", "toAsciiUppercase", "substring", "beginIndex", "endIndex", "internalGet", "", "pos", "internalGet$okio", "get", "index", "getByte", "size", "getSize", "getSize$okio", "toByteArray", "internalArray", "internalArray$okio", "asByteBuffer", "Ljava/nio/ByteBuffer;", "write", "", "out", "Ljava/io/OutputStream;", "buffer", "Lokio/Buffer;", "offset", "byteCount", "write$okio", "rangeEquals", "", "other", "otherOffset", "copyInto", "target", "targetOffset", "startsWith", "prefix", "endsWith", "suffix", "indexOf", "fromIndex", "lastIndexOf", "equals", "", "compareTo", "toString", "readObject", "in", "Ljava/io/ObjectInputStream;", "writeObject", "Ljava/io/ObjectOutputStream;", "-deprecated_getByte", "-deprecated_size", "Companion", "okio"}
)
@SourceDebugExtension({"SMAP\nByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ByteString.kt\nokio/ByteString\n+ 2 ByteString.kt\nokio/internal/-ByteString\n+ 3 Util.kt\nokio/-SegmentedByteString\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,364:1\n42#2,7:365\n52#2:372\n55#2:373\n62#2,4:374\n66#2:379\n68#2:381\n74#2,23:382\n102#2,23:405\n129#2,2:428\n131#2,9:431\n143#2:440\n146#2:441\n149#2:442\n152#2:443\n160#2:444\n170#2,3:445\n169#2:448\n183#2,2:449\n188#2:451\n192#2:452\n196#2:453\n200#2:454\n204#2,7:455\n217#2:462\n221#2,8:463\n233#2,4:471\n242#2,5:475\n251#2,6:480\n257#2,9:487\n301#2,8:496\n129#2,2:504\n131#2,9:507\n312#2,9:516\n67#3:378\n73#3:380\n73#3:486\n1#4:430\n1#4:506\n*S KotlinDebug\n*F\n+ 1 ByteString.kt\nokio/ByteString\n*L\n66#1:365,7\n71#1:372\n108#1:373\n110#1:374,4\n110#1:379\n110#1:381\n112#1:382,23\n114#1:405,23\n118#1:428,2\n118#1:431,9\n120#1:440\n129#1:441\n131#1:442\n133#1:443\n152#1:444\n159#1:445,3\n159#1:448\n166#1:449,2\n168#1:451\n170#1:452\n172#1:453\n174#1:454\n180#1:455,7\n183#1:462\n186#1:463,8\n188#1:471,4\n190#1:475,5\n192#1:480,6\n192#1:487,9\n194#1:496,8\n194#1:504,2\n194#1:507,9\n194#1:516,9\n110#1:378\n110#1:380\n192#1:486\n118#1:430\n194#1:506\n*E\n"})
public class ByteString implements Serializable, Comparable<ByteString> {
   @NotNull
   public static final Companion Companion = new Companion((DefaultConstructorMarker)null);
   @NotNull
   private final byte[] data;
   private transient int hashCode;
   @Nullable
   private transient String utf8;
   private static final long serialVersionUID = 1L;
   @JvmField
   @NotNull
   public static final ByteString EMPTY = new ByteString(new byte[0]);

   public ByteString(@NotNull byte[] data) {
      Intrinsics.checkNotNullParameter(data, "data");
      super();
      this.data = data;
   }

   @NotNull
   public final byte[] getData$okio() {
      return this.data;
   }

   public final int getHashCode$okio() {
      return this.hashCode;
   }

   public final void setHashCode$okio(int var1) {
      this.hashCode = var1;
   }

   @Nullable
   public final String getUtf8$okio() {
      return this.utf8;
   }

   public final void setUtf8$okio(@Nullable String var1) {
      this.utf8 = var1;
   }

   @NotNull
   public String utf8() {
      int $i$f$commonUtf8 = 0;
      String result$iv = this.getUtf8$okio();
      if (result$iv == null) {
         result$iv = _JvmPlatformKt.toUtf8String(this.internalArray$okio());
         this.setUtf8$okio(result$iv);
      }

      return result$iv;
   }

   @NotNull
   public String string(@NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(charset, "charset");
      return new String(this.data, charset);
   }

   @NotNull
   public String base64() {
      int $i$f$commonBase64 = 0;
      return -Base64.encodeBase64$default(this.getData$okio(), (byte[])null, 1, (Object)null);
   }

   @NotNull
   public final ByteString md5() {
      return this.digest$okio("MD5");
   }

   @NotNull
   public final ByteString sha1() {
      return this.digest$okio("SHA-1");
   }

   @NotNull
   public final ByteString sha256() {
      return this.digest$okio("SHA-256");
   }

   @NotNull
   public final ByteString sha512() {
      return this.digest$okio("SHA-512");
   }

   @NotNull
   public ByteString digest$okio(@NotNull String algorithm) {
      Intrinsics.checkNotNullParameter(algorithm, "algorithm");
      MessageDigest $this$digest_u24lambda_u240 = MessageDigest.getInstance(algorithm);
      int var4 = 0;
      $this$digest_u24lambda_u240.update(this.data, 0, this.size());
      byte[] digestBytes = $this$digest_u24lambda_u240.digest();
      Intrinsics.checkNotNull(digestBytes);
      return new ByteString(digestBytes);
   }

   @NotNull
   public ByteString hmacSha1(@NotNull ByteString key) {
      Intrinsics.checkNotNullParameter(key, "key");
      return this.hmac$okio("HmacSHA1", key);
   }

   @NotNull
   public ByteString hmacSha256(@NotNull ByteString key) {
      Intrinsics.checkNotNullParameter(key, "key");
      return this.hmac$okio("HmacSHA256", key);
   }

   @NotNull
   public ByteString hmacSha512(@NotNull ByteString key) {
      Intrinsics.checkNotNullParameter(key, "key");
      return this.hmac$okio("HmacSHA512", key);
   }

   @NotNull
   public ByteString hmac$okio(@NotNull String algorithm, @NotNull ByteString key) {
      Intrinsics.checkNotNullParameter(algorithm, "algorithm");
      Intrinsics.checkNotNullParameter(key, "key");

      try {
         Mac mac = Mac.getInstance(algorithm);
         mac.init((Key)(new SecretKeySpec(key.toByteArray(), algorithm)));
         byte[] var10002 = mac.doFinal(this.data);
         Intrinsics.checkNotNullExpressionValue(var10002, "doFinal(...)");
         return new ByteString(var10002);
      } catch (InvalidKeyException e) {
         throw new IllegalArgumentException((Throwable)e);
      }
   }

   @NotNull
   public String base64Url() {
      int $i$f$commonBase64Url = 0;
      return -Base64.encodeBase64(this.getData$okio(), -Base64.getBASE64_URL_SAFE());
   }

   @NotNull
   public String hex() {
      int $i$f$commonHex = 0;
      char[] result$iv = new char[this.getData$okio().length * 2];
      int c$iv = 0;
      byte[] var5 = this.getData$okio();
      int var6 = 0;

      for(int var7 = var5.length; var6 < var7; ++var6) {
         byte b$iv = var5[var6];
         int var10001 = c$iv++;
         char[] var10002 = -ByteString.getHEX_DIGIT_CHARS();
         int other$iv$iv = 4;
         int $i$f$shr = 0;
         result$iv[var10001] = var10002[b$iv >> other$iv$iv & 15];
         var10001 = c$iv++;
         var10002 = -ByteString.getHEX_DIGIT_CHARS();
         other$iv$iv = 15;
         $i$f$shr = 0;
         result$iv[var10001] = var10002[b$iv & other$iv$iv];
      }

      return StringsKt.concatToString(result$iv);
   }

   @NotNull
   public ByteString toAsciiLowercase() {
      ByteString $this$commonToAsciiLowercase$iv = this;
      int $i$f$commonToAsciiLowercase = 0;
      int i$iv = 0;

      ByteString var9;
      while(true) {
         if (i$iv >= $this$commonToAsciiLowercase$iv.getData$okio().length) {
            var9 = $this$commonToAsciiLowercase$iv;
            break;
         }

         byte c$iv = $this$commonToAsciiLowercase$iv.getData$okio()[i$iv];
         if (c$iv >= 65 && c$iv <= 90) {
            byte[] var10000 = $this$commonToAsciiLowercase$iv.getData$okio();
            var10000 = Arrays.copyOf(var10000, var10000.length);
            Intrinsics.checkNotNullExpressionValue(var10000, "copyOf(...)");
            byte[] lowercase$iv = var10000;
            lowercase$iv[i$iv++] = (byte)(c$iv - -32);

            while(i$iv < lowercase$iv.length) {
               c$iv = lowercase$iv[i$iv];
               if (c$iv >= 65 && c$iv <= 90) {
                  lowercase$iv[i$iv] = (byte)(c$iv - -32);
                  ++i$iv;
               } else {
                  ++i$iv;
               }
            }

            var9 = new ByteString(lowercase$iv);
            break;
         }

         ++i$iv;
      }

      return var9;
   }

   @NotNull
   public ByteString toAsciiUppercase() {
      ByteString $this$commonToAsciiUppercase$iv = this;
      int $i$f$commonToAsciiUppercase = 0;
      int i$iv = 0;

      ByteString var9;
      while(true) {
         if (i$iv >= $this$commonToAsciiUppercase$iv.getData$okio().length) {
            var9 = $this$commonToAsciiUppercase$iv;
            break;
         }

         byte c$iv = $this$commonToAsciiUppercase$iv.getData$okio()[i$iv];
         if (c$iv >= 97 && c$iv <= 122) {
            byte[] var10000 = $this$commonToAsciiUppercase$iv.getData$okio();
            var10000 = Arrays.copyOf(var10000, var10000.length);
            Intrinsics.checkNotNullExpressionValue(var10000, "copyOf(...)");
            byte[] lowercase$iv = var10000;
            lowercase$iv[i$iv++] = (byte)(c$iv - 32);

            while(i$iv < lowercase$iv.length) {
               c$iv = lowercase$iv[i$iv];
               if (c$iv >= 97 && c$iv <= 122) {
                  lowercase$iv[i$iv] = (byte)(c$iv - 32);
                  ++i$iv;
               } else {
                  ++i$iv;
               }
            }

            var9 = new ByteString(lowercase$iv);
            break;
         }

         ++i$iv;
      }

      return var9;
   }

   @JvmOverloads
   @NotNull
   public ByteString substring(int beginIndex, int endIndex) {
      int $i$f$commonSubstring = 0;
      int endIndex$iv = -SegmentedByteString.resolveDefaultParameter(this, endIndex);
      if (beginIndex < 0) {
         int var12 = 0;
         String var13 = "beginIndex < 0";
         throw new IllegalArgumentException(var13.toString());
      } else if (endIndex$iv > this.getData$okio().length) {
         int var8 = 0;
         String var11 = "endIndex > length(" + this.getData$okio().length + ')';
         throw new IllegalArgumentException(var11.toString());
      } else {
         int subLen$iv = endIndex$iv - beginIndex;
         if (subLen$iv < 0) {
            int var10 = 0;
            String var14 = "endIndex < beginIndex";
            throw new IllegalArgumentException(var14.toString());
         } else {
            return beginIndex == 0 && endIndex$iv == this.getData$okio().length ? this : new ByteString(ArraysKt.copyOfRange(this.getData$okio(), beginIndex, endIndex$iv));
         }
      }
   }

   // $FF: synthetic method
   public static ByteString substring$default(ByteString var0, int var1, int var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: substring");
      } else {
         if ((var3 & 1) != 0) {
            var1 = 0;
         }

         if ((var3 & 2) != 0) {
            var2 = -SegmentedByteString.getDEFAULT__ByteString_size();
         }

         return var0.substring(var1, var2);
      }
   }

   public byte internalGet$okio(int pos) {
      int $i$f$commonGetByte = 0;
      return this.getData$okio()[pos];
   }

   @JvmName(
      name = "getByte"
   )
   public final byte getByte(int index) {
      return this.internalGet$okio(index);
   }

   @JvmName(
      name = "size"
   )
   public final int size() {
      return this.getSize$okio();
   }

   public int getSize$okio() {
      int $i$f$commonGetSize = 0;
      return this.getData$okio().length;
   }

   @NotNull
   public byte[] toByteArray() {
      int $i$f$commonToByteArray = 0;
      byte[] var10000 = this.getData$okio();
      var10000 = Arrays.copyOf(var10000, var10000.length);
      Intrinsics.checkNotNullExpressionValue(var10000, "copyOf(...)");
      return var10000;
   }

   @NotNull
   public byte[] internalArray$okio() {
      int $i$f$commonInternalArray = 0;
      return this.getData$okio();
   }

   @NotNull
   public ByteBuffer asByteBuffer() {
      ByteBuffer var10000 = ByteBuffer.wrap(this.data).asReadOnlyBuffer();
      Intrinsics.checkNotNullExpressionValue(var10000, "asReadOnlyBuffer(...)");
      return var10000;
   }

   public void write(@NotNull OutputStream out) throws IOException {
      Intrinsics.checkNotNullParameter(out, "out");
      out.write(this.data);
   }

   public void write$okio(@NotNull Buffer buffer, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter(buffer, "buffer");
      -ByteString.commonWrite(this, buffer, offset, byteCount);
   }

   public boolean rangeEquals(int offset, @NotNull ByteString other, int otherOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonRangeEquals = 0;
      return other.rangeEquals(otherOffset, this.getData$okio(), offset, byteCount);
   }

   public boolean rangeEquals(int offset, @NotNull byte[] other, int otherOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonRangeEquals = 0;
      return offset >= 0 && offset <= this.getData$okio().length - byteCount && otherOffset >= 0 && otherOffset <= other.length - byteCount && -SegmentedByteString.arrayRangeEquals(this.getData$okio(), offset, other, otherOffset, byteCount);
   }

   public void copyInto(int offset, @NotNull byte[] target, int targetOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(target, "target");
      int $i$f$commonCopyInto = 0;
      ArraysKt.copyInto(this.getData$okio(), target, targetOffset, offset, offset + byteCount);
   }

   // $FF: synthetic method
   public static void copyInto$default(ByteString var0, int var1, byte[] var2, int var3, int var4, int var5, Object var6) {
      if (var6 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: copyInto");
      } else {
         if ((var5 & 1) != 0) {
            var1 = 0;
         }

         if ((var5 & 4) != 0) {
            var3 = 0;
         }

         var0.copyInto(var1, var2, var3, var4);
      }
   }

   public final boolean startsWith(@NotNull ByteString prefix) {
      Intrinsics.checkNotNullParameter(prefix, "prefix");
      int $i$f$commonStartsWith = 0;
      return this.rangeEquals(0, (ByteString)prefix, 0, prefix.size());
   }

   public final boolean startsWith(@NotNull byte[] prefix) {
      Intrinsics.checkNotNullParameter(prefix, "prefix");
      int $i$f$commonStartsWith = 0;
      return this.rangeEquals(0, (byte[])prefix, 0, prefix.length);
   }

   public final boolean endsWith(@NotNull ByteString suffix) {
      Intrinsics.checkNotNullParameter(suffix, "suffix");
      int $i$f$commonEndsWith = 0;
      return this.rangeEquals(this.size() - suffix.size(), (ByteString)suffix, 0, suffix.size());
   }

   public final boolean endsWith(@NotNull byte[] suffix) {
      Intrinsics.checkNotNullParameter(suffix, "suffix");
      int $i$f$commonEndsWith = 0;
      return this.rangeEquals(this.size() - suffix.length, (byte[])suffix, 0, suffix.length);
   }

   @JvmOverloads
   public final int indexOf(@NotNull ByteString other, int fromIndex) {
      Intrinsics.checkNotNullParameter(other, "other");
      return this.indexOf(other.internalArray$okio(), fromIndex);
   }

   // $FF: synthetic method
   public static int indexOf$default(ByteString var0, ByteString var1, int var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: indexOf");
      } else {
         if ((var3 & 2) != 0) {
            var2 = 0;
         }

         return var0.indexOf(var1, var2);
      }
   }

   @JvmOverloads
   public int indexOf(@NotNull byte[] other, int fromIndex) {
      Intrinsics.checkNotNullParameter(other, "other");
      ByteString $this$commonIndexOf$iv = this;
      byte[] other$iv = other;
      int $i$f$commonIndexOf = 0;
      int limit$iv = this.getData$okio().length - other.length;
      int i$iv = Math.max(fromIndex, 0);
      int var10000;
      if (i$iv <= limit$iv) {
         while(true) {
            if (-SegmentedByteString.arrayRangeEquals($this$commonIndexOf$iv.getData$okio(), i$iv, other$iv, 0, other$iv.length)) {
               var10000 = i$iv;
               return var10000;
            }

            if (i$iv == limit$iv) {
               break;
            }

            ++i$iv;
         }
      }

      var10000 = -1;
      return var10000;
   }

   // $FF: synthetic method
   public static int indexOf$default(ByteString var0, byte[] var1, int var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: indexOf");
      } else {
         if ((var3 & 2) != 0) {
            var2 = 0;
         }

         return var0.indexOf(var1, var2);
      }
   }

   @JvmOverloads
   public final int lastIndexOf(@NotNull ByteString other, int fromIndex) {
      Intrinsics.checkNotNullParameter(other, "other");
      int $i$f$commonLastIndexOf = 0;
      return this.lastIndexOf(other.internalArray$okio(), fromIndex);
   }

   // $FF: synthetic method
   public static int lastIndexOf$default(ByteString var0, ByteString var1, int var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: lastIndexOf");
      } else {
         if ((var3 & 2) != 0) {
            var2 = -SegmentedByteString.getDEFAULT__ByteString_size();
         }

         return var0.lastIndexOf(var1, var2);
      }
   }

   @JvmOverloads
   public int lastIndexOf(@NotNull byte[] other, int fromIndex) {
      Intrinsics.checkNotNullParameter(other, "other");
      ByteString $this$commonLastIndexOf$iv = this;
      byte[] other$iv = other;
      int $i$f$commonLastIndexOf = 0;
      int fromIndex$iv = -SegmentedByteString.resolveDefaultParameter(this, fromIndex);
      int limit$iv = this.getData$okio().length - other.length;
      int i$iv = Math.min(fromIndex$iv, limit$iv);

      int var10000;
      while(true) {
         if (-1 >= i$iv) {
            var10000 = -1;
            break;
         }

         if (-SegmentedByteString.arrayRangeEquals($this$commonLastIndexOf$iv.getData$okio(), i$iv, other$iv, 0, other$iv.length)) {
            var10000 = i$iv;
            break;
         }

         --i$iv;
      }

      return var10000;
   }

   // $FF: synthetic method
   public static int lastIndexOf$default(ByteString var0, byte[] var1, int var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: lastIndexOf");
      } else {
         if ((var3 & 2) != 0) {
            var2 = -SegmentedByteString.getDEFAULT__ByteString_size();
         }

         return var0.lastIndexOf(var1, var2);
      }
   }

   public boolean equals(@Nullable Object other) {
      int $i$f$commonEquals = 0;
      return other == this ? true : (other instanceof ByteString ? ((ByteString)other).size() == this.getData$okio().length && ((ByteString)other).rangeEquals(0, (byte[])this.getData$okio(), 0, this.getData$okio().length) : false);
   }

   public int hashCode() {
      int $i$f$commonHashCode = 0;
      int result$iv = this.getHashCode$okio();
      int var10000;
      if (result$iv != 0) {
         var10000 = result$iv;
      } else {
         int it$iv = Arrays.hashCode(this.getData$okio());
         int var6 = 0;
         this.setHashCode$okio(it$iv);
         var10000 = it$iv;
      }

      return var10000;
   }

   public int compareTo(@NotNull ByteString other) {
      Intrinsics.checkNotNullParameter(other, "other");
      ByteString $this$commonCompareTo$iv = this;
      ByteString other$iv = other;
      int $i$f$commonCompareTo = 0;
      int sizeA$iv = this.size();
      int sizeB$iv = other.size();
      int i$iv = 0;
      int size$iv = Math.min(sizeA$iv, sizeB$iv);

      int var10000;
      while(true) {
         if (i$iv < size$iv) {
            int byteB$iv = $this$commonCompareTo$iv.getByte(i$iv);
            int other$iv$iv = 255;
            int $i$f$and = 0;
            int byteA$iv = byteB$iv & other$iv$iv;
            other$iv$iv = other$iv.getByte(i$iv);
            $i$f$and = 255;
            int $i$f$and = 0;
            byteB$iv = other$iv$iv & $i$f$and;
            if (byteA$iv == byteB$iv) {
               ++i$iv;
               continue;
            }

            var10000 = byteA$iv < byteB$iv ? -1 : 1;
            break;
         }

         var10000 = sizeA$iv == sizeB$iv ? 0 : (sizeA$iv < sizeB$iv ? -1 : 1);
         break;
      }

      return var10000;
   }

   @NotNull
   public String toString() {
      int $i$f$commonToString = 0;
      String var10000;
      if (this.getData$okio().length == 0) {
         var10000 = "[size=0]";
      } else {
         int i$iv = -ByteString.access$codePointIndexToCharIndex(this.getData$okio(), 64);
         if (i$iv == -1) {
            if (this.getData$okio().length <= 64) {
               var10000 = "[hex=" + this.hex() + ']';
            } else {
               StringBuilder var15 = (new StringBuilder()).append("[size=").append(this.getData$okio().length).append(" hex=");
               byte beginIndex$iv$iv = 0;
               int endIndex$iv$iv = 64;
               int $i$f$commonSubstring = 0;
               int endIndex$iv$iv = -SegmentedByteString.resolveDefaultParameter(this, endIndex$iv$iv);
               if (endIndex$iv$iv > this.getData$okio().length) {
                  int var9 = 0;
                  String var13 = "endIndex > length(" + this.getData$okio().length + ')';
                  throw new IllegalArgumentException(var13.toString());
               }

               int subLen$iv$iv = endIndex$iv$iv - beginIndex$iv$iv;
               if (subLen$iv$iv < 0) {
                  int var11 = 0;
                  String var14 = "endIndex < beginIndex";
                  throw new IllegalArgumentException(var14.toString());
               }

               var10000 = var15.append((endIndex$iv$iv == this.getData$okio().length ? this : new ByteString(ArraysKt.copyOfRange(this.getData$okio(), beginIndex$iv$iv, endIndex$iv$iv))).hex()).append("…]").toString();
            }
         } else {
            String text$iv = this.utf8();
            var10000 = text$iv.substring(0, i$iv);
            Intrinsics.checkNotNullExpressionValue(var10000, "substring(...)");
            String safeText$iv = StringsKt.replace$default(StringsKt.replace$default(StringsKt.replace$default(var10000, "\\", "\\\\", false, 4, (Object)null), "\n", "\\n", false, 4, (Object)null), "\r", "\\r", false, 4, (Object)null);
            var10000 = i$iv < text$iv.length() ? "[size=" + this.getData$okio().length + " text=" + safeText$iv + "…]" : "[text=" + safeText$iv + ']';
         }
      }

      return var10000;
   }

   private final void readObject(ObjectInputStream in) throws IOException {
      int dataLength = in.readInt();
      ByteString byteString = Companion.read((InputStream)in, dataLength);
      Field field = ByteString.class.getDeclaredField("data");
      field.setAccessible(true);
      field.set(this, byteString.data);
   }

   private final void writeObject(ObjectOutputStream out) throws IOException {
      out.writeInt(this.data.length);
      out.write(this.data);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to operator function",
      replaceWith = @ReplaceWith(
   expression = "this[index]",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_getByte"
   )
   public final byte _deprecated_getByte/* $FF was: -deprecated_getByte*/(int index) {
      return this.getByte(index);
   }

   /** @deprecated */
   @Deprecated(
      message = "moved to val",
      replaceWith = @ReplaceWith(
   expression = "size",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   @JvmName(
      name = "-deprecated_size"
   )
   public final int _deprecated_size/* $FF was: -deprecated_size*/() {
      return this.size();
   }

   @JvmOverloads
   @NotNull
   public final ByteString substring(int beginIndex) {
      return substring$default(this, beginIndex, 0, 2, (Object)null);
   }

   @JvmOverloads
   @NotNull
   public final ByteString substring() {
      return substring$default(this, 0, 0, 3, (Object)null);
   }

   @JvmOverloads
   public final int indexOf(@NotNull ByteString other) {
      Intrinsics.checkNotNullParameter(other, "other");
      return indexOf$default(this, (ByteString)other, 0, 2, (Object)null);
   }

   @JvmOverloads
   public final int indexOf(@NotNull byte[] other) {
      Intrinsics.checkNotNullParameter(other, "other");
      return indexOf$default(this, (byte[])other, 0, 2, (Object)null);
   }

   @JvmOverloads
   public final int lastIndexOf(@NotNull ByteString other) {
      Intrinsics.checkNotNullParameter(other, "other");
      return lastIndexOf$default(this, (ByteString)other, 0, 2, (Object)null);
   }

   @JvmOverloads
   public final int lastIndexOf(@NotNull byte[] other) {
      Intrinsics.checkNotNullParameter(other, "other");
      return lastIndexOf$default(this, (byte[])other, 0, 2, (Object)null);
   }

   @JvmStatic
   @NotNull
   public static final ByteString of(@NotNull byte... data) {
      return Companion.of(data);
   }

   @JvmStatic
   @JvmName(
      name = "of"
   )
   @NotNull
   public static final ByteString of(@NotNull byte[] $this$of, int offset, int byteCount) {
      return Companion.of($this$of, offset, byteCount);
   }

   @JvmStatic
   @JvmName(
      name = "of"
   )
   @NotNull
   public static final ByteString of(@NotNull ByteBuffer $this$of) {
      return Companion.of($this$of);
   }

   @JvmStatic
   @NotNull
   public static final ByteString encodeUtf8(@NotNull String $this$encodeUtf8) {
      return Companion.encodeUtf8($this$encodeUtf8);
   }

   @JvmStatic
   @JvmName(
      name = "encodeString"
   )
   @NotNull
   public static final ByteString encodeString(@NotNull String $this$encodeString, @NotNull Charset charset) {
      return Companion.encodeString($this$encodeString, charset);
   }

   @JvmStatic
   @Nullable
   public static final ByteString decodeBase64(@NotNull String $this$decodeBase64) {
      return Companion.decodeBase64($this$decodeBase64);
   }

   @JvmStatic
   @NotNull
   public static final ByteString decodeHex(@NotNull String $this$decodeHex) {
      return Companion.decodeHex($this$decodeHex);
   }

   @JvmStatic
   @JvmName(
      name = "read"
   )
   @NotNull
   public static final ByteString read(@NotNull InputStream $this$read, int byteCount) throws IOException {
      return Companion.read($this$read, byteCount);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\u0010\u0005\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\f\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\b\u001a\u00020\u00072\n\u0010\t\u001a\u00020\n\"\u00020\u000bH\u0007J%\u0010\f\u001a\u00020\u0007*\u00020\n2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000eH\u0007¢\u0006\u0002\b\bJ\u0011\u0010\f\u001a\u00020\u0007*\u00020\u0010H\u0007¢\u0006\u0002\b\bJ\f\u0010\u0011\u001a\u00020\u0007*\u00020\u0012H\u0007J\u001b\u0010\u0013\u001a\u00020\u0007*\u00020\u00122\b\b\u0002\u0010\u0014\u001a\u00020\u0015H\u0007¢\u0006\u0002\b\u0016J\u000e\u0010\u0017\u001a\u0004\u0018\u00010\u0007*\u00020\u0012H\u0007J\f\u0010\u0018\u001a\u00020\u0007*\u00020\u0012H\u0007J\u0019\u0010\u0019\u001a\u00020\u0007*\u00020\u001a2\u0006\u0010\u000f\u001a\u00020\u000eH\u0007¢\u0006\u0002\b\u001bJ\u0017\u0010\u0017\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u001c\u001a\u00020\u0012H\u0007¢\u0006\u0002\b\u001dJ\u0015\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u001c\u001a\u00020\u0012H\u0007¢\u0006\u0002\b\u001eJ\u001d\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u001c\u001a\u00020\u00122\u0006\u0010\u0014\u001a\u00020\u0015H\u0007¢\u0006\u0002\b\u001fJ\u0015\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u001c\u001a\u00020\u0012H\u0007¢\u0006\u0002\b J\u0015\u0010\b\u001a\u00020\u00072\u0006\u0010!\u001a\u00020\u0010H\u0007¢\u0006\u0002\b\"J%\u0010\b\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000eH\u0007¢\u0006\u0002\b\"J\u001d\u0010\u001b\u001a\u00020\u00072\u0006\u0010$\u001a\u00020\u001a2\u0006\u0010\u000f\u001a\u00020\u000eH\u0007¢\u0006\u0002\b%R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006&"},
      d2 = {"Lokio/ByteString$Companion;", "", "<init>", "()V", "serialVersionUID", "", "EMPTY", "Lokio/ByteString;", "of", "data", "", "", "toByteString", "offset", "", "byteCount", "Ljava/nio/ByteBuffer;", "encodeUtf8", "", "encode", "charset", "Ljava/nio/charset/Charset;", "encodeString", "decodeBase64", "decodeHex", "readByteString", "Ljava/io/InputStream;", "read", "string", "-deprecated_decodeBase64", "-deprecated_decodeHex", "-deprecated_encodeString", "-deprecated_encodeUtf8", "buffer", "-deprecated_of", "array", "inputstream", "-deprecated_read", "okio"}
   )
   @SourceDebugExtension({"SMAP\nByteString.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ByteString.kt\nokio/ByteString$Companion\n+ 2 ByteString.kt\nokio/internal/-ByteString\n+ 3 ByteStringNonJs.kt\nokio/internal/-ByteStringNonJs\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,364:1\n269#2:365\n273#2,3:366\n280#2,3:369\n287#2,2:372\n25#3:374\n27#3,7:376\n1#4:375\n1#4:383\n*S KotlinDebug\n*F\n+ 1 ByteString.kt\nokio/ByteString$Companion\n*L\n234#1:365\n239#1:366,3\n251#1:369,3\n259#1:372,2\n262#1:374\n262#1:376,7\n262#1:375\n*E\n"})
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final ByteString of(@NotNull byte... data) {
         Intrinsics.checkNotNullParameter(data, "data");
         int $i$f$commonOf = 0;
         byte[] var10002 = Arrays.copyOf(data, data.length);
         Intrinsics.checkNotNullExpressionValue(var10002, "copyOf(...)");
         return new ByteString(var10002);
      }

      @JvmStatic
      @JvmName(
         name = "of"
      )
      @NotNull
      public final ByteString of(@NotNull byte[] $this$toByteString, int offset, int byteCount) {
         Intrinsics.checkNotNullParameter($this$toByteString, "<this>");
         int $i$f$commonToByteString = 0;
         int byteCount$iv = -SegmentedByteString.resolveDefaultParameter($this$toByteString, byteCount);
         -SegmentedByteString.checkOffsetAndCount((long)$this$toByteString.length, (long)offset, (long)byteCount$iv);
         return new ByteString(ArraysKt.copyOfRange($this$toByteString, offset, offset + byteCount$iv));
      }

      // $FF: synthetic method
      public static ByteString of$default(Companion var0, byte[] var1, int var2, int var3, int var4, Object var5) {
         if ((var4 & 1) != 0) {
            var2 = 0;
         }

         if ((var4 & 2) != 0) {
            var3 = -SegmentedByteString.getDEFAULT__ByteString_size();
         }

         return var0.of(var1, var2, var3);
      }

      @JvmStatic
      @JvmName(
         name = "of"
      )
      @NotNull
      public final ByteString of(@NotNull ByteBuffer $this$toByteString) {
         Intrinsics.checkNotNullParameter($this$toByteString, "<this>");
         byte[] copy = new byte[$this$toByteString.remaining()];
         $this$toByteString.get(copy);
         return new ByteString(copy);
      }

      @JvmStatic
      @NotNull
      public final ByteString encodeUtf8(@NotNull String $this$encodeUtf8) {
         Intrinsics.checkNotNullParameter($this$encodeUtf8, "<this>");
         int $i$f$commonEncodeUtf8 = 0;
         ByteString byteString$iv = new ByteString(_JvmPlatformKt.asUtf8ToByteArray($this$encodeUtf8));
         byteString$iv.setUtf8$okio($this$encodeUtf8);
         return byteString$iv;
      }

      @JvmStatic
      @JvmName(
         name = "encodeString"
      )
      @NotNull
      public final ByteString encodeString(@NotNull String $this$encode, @NotNull Charset charset) {
         Intrinsics.checkNotNullParameter($this$encode, "<this>");
         Intrinsics.checkNotNullParameter(charset, "charset");
         byte[] var10002 = $this$encode.getBytes(charset);
         Intrinsics.checkNotNullExpressionValue(var10002, "getBytes(...)");
         return new ByteString(var10002);
      }

      // $FF: synthetic method
      public static ByteString encodeString$default(Companion var0, String var1, Charset var2, int var3, Object var4) {
         if ((var3 & 1) != 0) {
            var2 = Charsets.UTF_8;
         }

         return var0.encodeString(var1, var2);
      }

      @JvmStatic
      @Nullable
      public final ByteString decodeBase64(@NotNull String $this$decodeBase64) {
         Intrinsics.checkNotNullParameter($this$decodeBase64, "<this>");
         int $i$f$commonDecodeBase64 = 0;
         byte[] decoded$iv = -Base64.decodeBase64ToArray($this$decodeBase64);
         return decoded$iv != null ? new ByteString(decoded$iv) : null;
      }

      @JvmStatic
      @NotNull
      public final ByteString decodeHex(@NotNull String $this$decodeHex) {
         Intrinsics.checkNotNullParameter($this$decodeHex, "<this>");
         String $this$commonDecodeHex$iv = $this$decodeHex;
         int $i$f$commonDecodeHex = 0;
         if ($this$decodeHex.length() % 2 != 0) {
            int var9 = 0;
            String var10 = "Unexpected hex string: " + $this$decodeHex;
            throw new IllegalArgumentException(var10.toString());
         } else {
            byte[] result$iv = new byte[$this$decodeHex.length() / 2];
            int i$iv = 0;

            for(int var6 = result$iv.length; i$iv < var6; ++i$iv) {
               int d1$iv = -ByteStringNonJs.access$decodeHexDigit($this$commonDecodeHex$iv.charAt(i$iv * 2)) << 4;
               int d2$iv = -ByteStringNonJs.access$decodeHexDigit($this$commonDecodeHex$iv.charAt(i$iv * 2 + 1));
               result$iv[i$iv] = (byte)(d1$iv + d2$iv);
            }

            return new ByteString(result$iv);
         }
      }

      @JvmStatic
      @JvmName(
         name = "read"
      )
      @NotNull
      public final ByteString read(@NotNull InputStream $this$readByteString, int byteCount) throws IOException {
         Intrinsics.checkNotNullParameter($this$readByteString, "<this>");
         if (byteCount < 0) {
            int var6 = 0;
            String var7 = "byteCount < 0: " + byteCount;
            throw new IllegalArgumentException(var7.toString());
         } else {
            byte[] result = new byte[byteCount];
            int offset = 0;

            int read;
            for(read = 0; offset < byteCount; offset += read) {
               read = $this$readByteString.read(result, offset, byteCount - offset);
               if (read == -1) {
                  throw new EOFException();
               }
            }

            return new ByteString(result);
         }
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "string.decodeBase64()",
   imports = {"okio.ByteString.Companion.decodeBase64"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_decodeBase64"
      )
      @Nullable
      public final ByteString _deprecated_decodeBase64/* $FF was: -deprecated_decodeBase64*/(@NotNull String string) {
         Intrinsics.checkNotNullParameter(string, "string");
         return this.decodeBase64(string);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "string.decodeHex()",
   imports = {"okio.ByteString.Companion.decodeHex"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_decodeHex"
      )
      @NotNull
      public final ByteString _deprecated_decodeHex/* $FF was: -deprecated_decodeHex*/(@NotNull String string) {
         Intrinsics.checkNotNullParameter(string, "string");
         return this.decodeHex(string);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "string.encode(charset)",
   imports = {"okio.ByteString.Companion.encode"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_encodeString"
      )
      @NotNull
      public final ByteString _deprecated_encodeString/* $FF was: -deprecated_encodeString*/(@NotNull String string, @NotNull Charset charset) {
         Intrinsics.checkNotNullParameter(string, "string");
         Intrinsics.checkNotNullParameter(charset, "charset");
         return this.encodeString(string, charset);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "string.encodeUtf8()",
   imports = {"okio.ByteString.Companion.encodeUtf8"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_encodeUtf8"
      )
      @NotNull
      public final ByteString _deprecated_encodeUtf8/* $FF was: -deprecated_encodeUtf8*/(@NotNull String string) {
         Intrinsics.checkNotNullParameter(string, "string");
         return this.encodeUtf8(string);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "buffer.toByteString()",
   imports = {"okio.ByteString.Companion.toByteString"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_of"
      )
      @NotNull
      public final ByteString _deprecated_of/* $FF was: -deprecated_of*/(@NotNull ByteBuffer buffer) {
         Intrinsics.checkNotNullParameter(buffer, "buffer");
         return this.of(buffer);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "array.toByteString(offset, byteCount)",
   imports = {"okio.ByteString.Companion.toByteString"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_of"
      )
      @NotNull
      public final ByteString _deprecated_of/* $FF was: -deprecated_of*/(@NotNull byte[] array, int offset, int byteCount) {
         Intrinsics.checkNotNullParameter(array, "array");
         return this.of(array, offset, byteCount);
      }

      /** @deprecated */
      @Deprecated(
         message = "moved to extension function",
         replaceWith = @ReplaceWith(
   expression = "inputstream.readByteString(byteCount)",
   imports = {"okio.ByteString.Companion.readByteString"}
),
         level = DeprecationLevel.ERROR
      )
      @JvmName(
         name = "-deprecated_read"
      )
      @NotNull
      public final ByteString _deprecated_read/* $FF was: -deprecated_read*/(@NotNull InputStream inputstream, int byteCount) {
         Intrinsics.checkNotNullParameter(inputstream, "inputstream");
         return this.read(inputstream, byteCount);
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}
