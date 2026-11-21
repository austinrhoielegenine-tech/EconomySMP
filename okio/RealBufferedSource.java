package okio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.CharsKt;
import okio.internal.-Buffer;
import okio.internal.-RealBufferedSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0090\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0005\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\n\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\n\u001a\u00020\u0007H\u0016J\u0018\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u0010H\u0016J\b\u0010\u0013\u001a\u00020\tH\u0016J\u0010\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0012\u001a\u00020\u0010H\u0016J\u0010\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u0010H\u0016J\b\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016J\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0012\u001a\u00020\u0010H\u0016J\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016J'\u0010\u001b\u001a\u0004\u0018\u0001H\u001f\"\b\b\u0000\u0010\u001f*\u00020 2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u0002H\u001f0!H\u0016¢\u0006\u0002\u0010\"J\b\u0010#\u001a\u00020$H\u0016J\u0010\u0010#\u001a\u00020$2\u0006\u0010\u0012\u001a\u00020\u0010H\u0016J\u0010\u0010\u000f\u001a\u00020\u001c2\u0006\u0010\u0011\u001a\u00020$H\u0016J\u0010\u0010%\u001a\u00020\u00152\u0006\u0010\u0011\u001a\u00020$H\u0016J \u0010\u000f\u001a\u00020\u001c2\u0006\u0010\u0011\u001a\u00020$2\u0006\u0010&\u001a\u00020\u001c2\u0006\u0010\u0012\u001a\u00020\u001cH\u0016J\u0010\u0010\u000f\u001a\u00020\u001c2\u0006\u0010\u0011\u001a\u00020'H\u0016J\u0018\u0010%\u001a\u00020\u00152\u0006\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u0010H\u0016J\u0010\u0010(\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020)H\u0016J\b\u0010*\u001a\u00020+H\u0016J\u0010\u0010*\u001a\u00020+2\u0006\u0010\u0012\u001a\u00020\u0010H\u0016J\u0010\u0010,\u001a\u00020+2\u0006\u0010-\u001a\u00020.H\u0016J\u0018\u0010,\u001a\u00020+2\u0006\u0010\u0012\u001a\u00020\u00102\u0006\u0010-\u001a\u00020.H\u0016J\n\u0010/\u001a\u0004\u0018\u00010+H\u0016J\b\u00100\u001a\u00020+H\u0016J\u0010\u00100\u001a\u00020+2\u0006\u00101\u001a\u00020\u0010H\u0016J\b\u00102\u001a\u00020\u001cH\u0016J\b\u00103\u001a\u000204H\u0016J\b\u00105\u001a\u000204H\u0016J\b\u00106\u001a\u00020\u001cH\u0016J\b\u00107\u001a\u00020\u001cH\u0016J\b\u00108\u001a\u00020\u0010H\u0016J\b\u00109\u001a\u00020\u0010H\u0016J\b\u0010:\u001a\u00020\u0010H\u0016J\b\u0010;\u001a\u00020\u0010H\u0016J\u0010\u0010<\u001a\u00020\u00152\u0006\u0010\u0012\u001a\u00020\u0010H\u0016J\u0010\u0010=\u001a\u00020\u00102\u0006\u0010>\u001a\u00020\u0018H\u0016J\u0018\u0010=\u001a\u00020\u00102\u0006\u0010>\u001a\u00020\u00182\u0006\u0010?\u001a\u00020\u0010H\u0016J \u0010=\u001a\u00020\u00102\u0006\u0010>\u001a\u00020\u00182\u0006\u0010?\u001a\u00020\u00102\u0006\u0010@\u001a\u00020\u0010H\u0016J\u0010\u0010=\u001a\u00020\u00102\u0006\u0010A\u001a\u00020\u001aH\u0016J\u0018\u0010=\u001a\u00020\u00102\u0006\u0010A\u001a\u00020\u001a2\u0006\u0010?\u001a\u00020\u0010H\u0016J \u0010=\u001a\u00020\u00102\u0006\u0010A\u001a\u00020\u001a2\u0006\u0010?\u001a\u00020\u00102\u0006\u0010@\u001a\u00020\u0010H\u0016J\u0010\u0010B\u001a\u00020\u00102\u0006\u0010C\u001a\u00020\u001aH\u0016J\u0018\u0010B\u001a\u00020\u00102\u0006\u0010C\u001a\u00020\u001a2\u0006\u0010?\u001a\u00020\u0010H\u0016J\u0018\u0010D\u001a\u00020\t2\u0006\u0010&\u001a\u00020\u00102\u0006\u0010A\u001a\u00020\u001aH\u0016J(\u0010D\u001a\u00020\t2\u0006\u0010&\u001a\u00020\u00102\u0006\u0010A\u001a\u00020\u001a2\u0006\u0010E\u001a\u00020\u001c2\u0006\u0010\u0012\u001a\u00020\u001cH\u0016J\b\u0010F\u001a\u00020\u0001H\u0016J\b\u0010G\u001a\u00020HH\u0016J\b\u0010I\u001a\u00020\tH\u0016J\b\u0010J\u001a\u00020\u0015H\u0016J\b\u0010K\u001a\u00020LH\u0016J\b\u0010M\u001a\u00020+H\u0016R\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u00020\t8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u001b\u0010\n\u001a\u00020\u00078Ö\u0002X\u0096\u0004¢\u0006\f\u0012\u0004\b\u000b\u0010\f\u001a\u0004\b\r\u0010\u000e¨\u0006N"},
   d2 = {"Lokio/RealBufferedSource;", "Lokio/BufferedSource;", "source", "Lokio/Source;", "<init>", "(Lokio/Source;)V", "bufferField", "Lokio/Buffer;", "closed", "", "buffer", "getBuffer$annotations", "()V", "getBuffer", "()Lokio/Buffer;", "read", "", "sink", "byteCount", "exhausted", "require", "", "request", "readByte", "", "readByteString", "Lokio/ByteString;", "select", "", "options", "Lokio/Options;", "T", "", "Lokio/TypedOptions;", "(Lokio/TypedOptions;)Ljava/lang/Object;", "readByteArray", "", "readFully", "offset", "Ljava/nio/ByteBuffer;", "readAll", "Lokio/Sink;", "readUtf8", "", "readString", "charset", "Ljava/nio/charset/Charset;", "readUtf8Line", "readUtf8LineStrict", "limit", "readUtf8CodePoint", "readShort", "", "readShortLe", "readInt", "readIntLe", "readLong", "readLongLe", "readDecimalLong", "readHexadecimalUnsignedLong", "skip", "indexOf", "b", "fromIndex", "toIndex", "bytes", "indexOfElement", "targetBytes", "rangeEquals", "bytesOffset", "peek", "inputStream", "Ljava/io/InputStream;", "isOpen", "close", "timeout", "Lokio/Timeout;", "toString", "okio"}
)
@SourceDebugExtension({"SMAP\nRealBufferedSource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealBufferedSource.kt\nokio/RealBufferedSource\n+ 2 RealBufferedSource.kt\nokio/internal/-RealBufferedSource\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 BufferedSource.kt\nokio/internal/-BufferedSource\n+ 5 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,207:1\n63#1:213\n63#1:224\n63#1:231\n63#1:237\n63#1:239\n63#1:243\n63#1:248\n63#1:266\n63#1:270\n63#1:277\n63#1:290\n63#1:299\n63#1:300\n63#1:301\n63#1:307\n63#1:315\n63#1:328\n63#1:332\n63#1:333\n63#1:334\n63#1:335\n63#1:340\n63#1:352\n63#1:368\n63#1:378\n63#1:381\n63#1:384\n63#1:387\n63#1:390\n63#1:393\n63#1:399\n63#1:416\n63#1:436\n63#1:451\n63#1:468\n63#1:495\n39#2:208\n40#2,3:210\n43#2,7:214\n53#2:221\n54#2:223\n58#2,2:225\n62#2:227\n63#2,2:229\n65#2,3:232\n71#2,2:235\n76#2:238\n77#2:240\n81#2,2:241\n86#2:244\n88#2,2:246\n90#2,13:249\n109#2:265\n110#2:267\n114#2,2:268\n119#2,6:271\n125#2,9:278\n136#2,3:287\n139#2,6:291\n145#2:298\n149#2,5:302\n154#2,5:308\n161#2,2:313\n163#2,11:316\n177#2:327\n178#2:329\n182#2,2:330\n187#2,4:336\n191#2,6:341\n201#2:347\n202#2,3:349\n205#2,8:353\n213#2,3:362\n220#2,3:365\n223#2,7:369\n233#2,2:376\n238#2,2:379\n243#2,2:382\n248#2,2:385\n253#2,2:388\n258#2,2:391\n263#2,5:394\n268#2,11:400\n282#2,5:411\n287#2,14:417\n304#2,2:431\n306#2,2:434\n308#2,7:437\n317#2,2:444\n319#2,4:447\n323#2,11:452\n421#2,2:463\n424#2,2:466\n426#2,7:469\n442#2:476\n444#2,12:478\n459#2:490\n463#2,4:491\n467#2:496\n469#2:497\n471#2:498\n1#3:209\n1#3:222\n1#3:228\n1#3:245\n1#3:348\n1#3:433\n1#3:446\n1#3:465\n1#3:477\n26#4,3:262\n88#5:297\n88#5:361\n*S KotlinDebug\n*F\n+ 1 RealBufferedSource.kt\nokio/RealBufferedSource\n*L\n67#1:213\n68#1:224\n70#1:231\n71#1:237\n72#1:239\n73#1:243\n74#1:248\n76#1:266\n77#1:270\n79#1:277\n81#1:290\n84#1:299\n85#1:300\n89#1:301\n93#1:307\n94#1:315\n95#1:328\n96#1:332\n99#1:333\n100#1:334\n105#1:335\n108#1:340\n110#1:352\n111#1:368\n112#1:378\n113#1:381\n114#1:384\n115#1:387\n116#1:390\n117#1:393\n118#1:399\n119#1:416\n120#1:436\n125#1:451\n135#1:468\n203#1:495\n67#1:208\n67#1:210,3\n67#1:214,7\n68#1:221\n68#1:223\n69#1:225,2\n70#1:227\n70#1:229,2\n70#1:232,3\n71#1:235,2\n72#1:238\n72#1:240\n73#1:241,2\n74#1:244\n74#1:246,2\n74#1:249,13\n76#1:265\n76#1:267\n77#1:268,2\n79#1:271,6\n79#1:278,9\n81#1:287,3\n81#1:291,6\n81#1:298\n93#1:302,5\n93#1:308,5\n94#1:313,2\n94#1:316,11\n95#1:327\n95#1:329\n96#1:330,2\n108#1:336,4\n108#1:341,6\n110#1:347\n110#1:349,3\n110#1:353,8\n110#1:362,3\n111#1:365,3\n111#1:369,7\n112#1:376,2\n113#1:379,2\n114#1:382,2\n115#1:385,2\n116#1:388,2\n117#1:391,2\n118#1:394,5\n118#1:400,11\n119#1:411,5\n119#1:417,14\n120#1:431,2\n120#1:434,2\n120#1:437,7\n125#1:444,2\n125#1:447,4\n125#1:452,11\n135#1:463,2\n135#1:466,2\n135#1:469,7\n149#1:476\n149#1:478,12\n151#1:490\n203#1:491,4\n203#1:496\n204#1:497\n205#1:498\n67#1:209\n68#1:222\n70#1:228\n74#1:245\n110#1:348\n120#1:433\n125#1:446\n135#1:465\n149#1:477\n75#1:262,3\n81#1:297\n110#1:361\n*E\n"})
public final class RealBufferedSource implements BufferedSource {
   @JvmField
   @NotNull
   public final Source source;
   @JvmField
   @NotNull
   public final Buffer bufferField;
   @JvmField
   public boolean closed;

   public RealBufferedSource(@NotNull Source source) {
      Intrinsics.checkNotNullParameter(source, "source");
      super();
      this.source = source;
      this.bufferField = new Buffer();
   }

   @NotNull
   public Buffer getBuffer() {
      int $i$f$getBuffer = 0;
      return this.bufferField;
   }

   /** @deprecated */
   // $FF: synthetic method
   public static void getBuffer$annotations() {
   }

   @NotNull
   public Buffer buffer() {
      return this.bufferField;
   }

   public long read(@NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonRead = 0;
      if (byteCount < 0L) {
         int var19 = 0;
         String var20 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var20.toString());
      } else if (this.closed) {
         int var17 = 0;
         String $i$f$getBuffer = "closed";
         throw new IllegalStateException($i$f$getBuffer.toString());
      } else {
         int $i$f$getBuffer = 0;
         long var10000;
         if (this.bufferField.size() == 0L) {
            if (byteCount == 0L) {
               var10000 = 0L;
               return var10000;
            }

            int $i$f$getBuffer = 0;
            long read$iv = this.source.read(this.bufferField, 8192L);
            if (read$iv == -1L) {
               var10000 = -1L;
               return var10000;
            }
         }

         int $i$f$getBuffer = 0;
         long var15 = this.bufferField.size();
         long toRead$iv = Math.min(byteCount, var15);
         $i$f$getBuffer = 0;
         var10000 = this.bufferField.read(sink, toRead$iv);
         return var10000;
      }
   }

   public boolean exhausted() {
      int $i$f$commonExhausted = 0;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var7 = "closed";
         throw new IllegalStateException(var7.toString());
      } else {
         int $i$f$getBuffer = 0;
         boolean var10000;
         if (this.bufferField.exhausted()) {
            $i$f$getBuffer = 0;
            if (this.source.read(this.bufferField, 8192L) == -1L) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public void require(long byteCount) {
      int $i$f$commonRequire = 0;
      if (!this.request(byteCount)) {
         throw new EOFException();
      }
   }

   public boolean request(long byteCount) {
      RealBufferedSource $this$commonRequest$iv = this;
      long byteCount$iv = byteCount;
      int $i$f$commonRequest = 0;
      if (byteCount < 0L) {
         int var12 = 0;
         String var13 = "byteCount < 0: " + byteCount;
         throw new IllegalArgumentException(var13.toString());
      } else if (this.closed) {
         int $i$f$getBuffer = 0;
         String $i$f$getBuffer = "closed";
         throw new IllegalStateException($i$f$getBuffer.toString());
      } else {
         boolean var10000;
         while(true) {
            int $i$f$getBuffer = 0;
            if ($this$commonRequest$iv.bufferField.size() < byteCount$iv) {
               $i$f$getBuffer = 0;
               if ($this$commonRequest$iv.source.read($this$commonRequest$iv.bufferField, 8192L) != -1L) {
                  continue;
               }

               var10000 = false;
               break;
            }

            var10000 = true;
            break;
         }

         return var10000;
      }
   }

   public byte readByte() {
      int $i$f$commonReadByte = 0;
      this.require(1L);
      int $i$f$getBuffer = 0;
      return this.bufferField.readByte();
   }

   @NotNull
   public ByteString readByteString() {
      int $i$f$commonReadByteString = 0;
      int $i$f$getBuffer = 0;
      this.bufferField.writeAll(this.source);
      $i$f$getBuffer = 0;
      return this.bufferField.readByteString();
   }

   @NotNull
   public ByteString readByteString(long byteCount) {
      int $i$f$commonReadByteString = 0;
      this.require(byteCount);
      int $i$f$getBuffer = 0;
      return this.bufferField.readByteString(byteCount);
   }

   public int select(@NotNull Options options) {
      Intrinsics.checkNotNullParameter(options, "options");
      RealBufferedSource $this$commonSelect$iv = this;
      Options options$iv = options;
      int $i$f$commonSelect = 0;
      if (this.closed) {
         int var5 = 0;
         String this_$iv$iv = "closed";
         throw new IllegalStateException(this_$iv$iv.toString());
      } else {
         while(true) {
            int $i$f$getBuffer = 0;
            int index$iv = -Buffer.selectPrefix($this$commonSelect$iv.bufferField, options$iv, true);
            int var10000;
            switch (index$iv) {
               case -2:
                  int $i$f$getBuffer = 0;
                  if ($this$commonSelect$iv.source.read($this$commonSelect$iv.bufferField, 8192L) != -1L) {
                     break;
                  }

                  var10000 = -1;
                  return var10000;
               case -1:
                  var10000 = -1;
                  return var10000;
               default:
                  $i$f$getBuffer = options$iv.getByteStrings$okio()[index$iv].size();
                  int $i$f$getBuffer = 0;
                  $this$commonSelect$iv.bufferField.skip((long)$i$f$getBuffer);
                  var10000 = index$iv;
                  return var10000;
            }
         }
      }
   }

   @Nullable
   public <T> T select(@NotNull TypedOptions<T> options) {
      Intrinsics.checkNotNullParameter(options, "options");
      BufferedSource $this$commonSelect$iv = this;
      int $i$f$commonSelect = 0;
      int index$iv = $this$commonSelect$iv.select(options.getOptions$okio());
      return (T)(index$iv == -1 ? null : options.get(index$iv));
   }

   @NotNull
   public byte[] readByteArray() {
      int $i$f$commonReadByteArray = 0;
      int $i$f$getBuffer = 0;
      this.bufferField.writeAll(this.source);
      $i$f$getBuffer = 0;
      return this.bufferField.readByteArray();
   }

   @NotNull
   public byte[] readByteArray(long byteCount) {
      int $i$f$commonReadByteArray = 0;
      this.require(byteCount);
      int $i$f$getBuffer = 0;
      return this.bufferField.readByteArray(byteCount);
   }

   public int read(@NotNull byte[] sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      return this.read(sink, 0, sink.length);
   }

   public void readFully(@NotNull byte[] sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      RealBufferedSource $this$commonReadFully$iv = this;
      byte[] sink$iv = sink;
      int $i$f$commonReadFully = 0;

      try {
         $this$commonReadFully$iv.require((long)sink$iv.length);
      } catch (EOFException e$iv) {
         int offset$iv = 0;

         while(true) {
            int $i$f$getBuffer = 0;
            if ($this$commonReadFully$iv.bufferField.size() <= 0L) {
               throw e$iv;
            }

            int $i$f$getBuffer = 0;
            $i$f$getBuffer = 0;
            int read$iv = $this$commonReadFully$iv.bufferField.read(sink$iv, offset$iv, (int)$this$commonReadFully$iv.bufferField.size());
            if (read$iv == -1) {
               throw new AssertionError();
            }

            offset$iv += read$iv;
         }
      }

      int $i$f$getBuffer = 0;
      this.bufferField.readFully(sink);
   }

   public int read(@NotNull byte[] sink, int offset, int byteCount) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$commonRead = 0;
      -SegmentedByteString.checkOffsetAndCount((long)sink.length, (long)offset, (long)byteCount);
      int $i$f$getBuffer = 0;
      int var10000;
      if (this.bufferField.size() == 0L) {
         if (byteCount == 0) {
            var10000 = 0;
            return var10000;
         }

         int $i$f$getBuffer = 0;
         long read$iv = this.source.read(this.bufferField, 8192L);
         if (read$iv == -1L) {
            var10000 = -1;
            return var10000;
         }
      }

      int $i$f$getBuffer = 0;
      long b$iv$iv = this.bufferField.size();
      int $i$f$minOf = 0;
      int toRead$iv = (int)Math.min((long)byteCount, b$iv$iv);
      int $i$f$getBuffer = 0;
      var10000 = this.bufferField.read(sink, offset, toRead$iv);
      return var10000;
   }

   public int read(@NotNull ByteBuffer sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      int $i$f$getBuffer = 0;
      if (this.bufferField.size() == 0L) {
         int $i$f$getBuffer = 0;
         long read = this.source.read(this.bufferField, 8192L);
         if (read == -1L) {
            return -1;
         }
      }

      $i$f$getBuffer = 0;
      return this.bufferField.read(sink);
   }

   public void readFully(@NotNull Buffer sink, long byteCount) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      RealBufferedSource $this$commonReadFully$iv = this;
      long byteCount$iv = byteCount;
      int $i$f$commonReadFully = 0;

      try {
         $this$commonReadFully$iv.require(byteCount$iv);
      } catch (EOFException e$iv) {
         int $i$f$getBuffer = 0;
         sink.writeAll(this.bufferField);
         throw e$iv;
      }

      int $i$f$getBuffer = 0;
      this.bufferField.readFully(sink, byteCount);
   }

   public long readAll(@NotNull Sink sink) {
      Intrinsics.checkNotNullParameter(sink, "sink");
      RealBufferedSource $this$commonReadAll$iv = this;
      Sink sink$iv = sink;
      int $i$f$commonReadAll = 0;
      long totalBytesWritten$iv = 0L;

      while(true) {
         int $i$f$getBuffer = 0;
         if ($this$commonReadAll$iv.source.read($this$commonReadAll$iv.bufferField, 8192L) == -1L) {
            $i$f$getBuffer = 0;
            if ($this$commonReadAll$iv.bufferField.size() > 0L) {
               $i$f$getBuffer = 0;
               totalBytesWritten$iv += $this$commonReadAll$iv.bufferField.size();
               $i$f$getBuffer = 0;
               $i$f$getBuffer = 0;
               sink$iv.write($this$commonReadAll$iv.bufferField, $this$commonReadAll$iv.bufferField.size());
            }

            return totalBytesWritten$iv;
         }

         int $i$f$getBuffer = 0;
         long emitByteCount$iv = $this$commonReadAll$iv.bufferField.completeSegmentByteCount();
         if (emitByteCount$iv > 0L) {
            totalBytesWritten$iv += emitByteCount$iv;
            $i$f$getBuffer = 0;
            sink$iv.write($this$commonReadAll$iv.bufferField, emitByteCount$iv);
         }
      }
   }

   @NotNull
   public String readUtf8() {
      int $i$f$commonReadUtf8 = 0;
      int $i$f$getBuffer = 0;
      this.bufferField.writeAll(this.source);
      $i$f$getBuffer = 0;
      return this.bufferField.readUtf8();
   }

   @NotNull
   public String readUtf8(long byteCount) {
      int $i$f$commonReadUtf8 = 0;
      this.require(byteCount);
      int $i$f$getBuffer = 0;
      return this.bufferField.readUtf8(byteCount);
   }

   @NotNull
   public String readString(@NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(charset, "charset");
      int $i$f$getBuffer = 0;
      this.bufferField.writeAll(this.source);
      $i$f$getBuffer = 0;
      return this.bufferField.readString(charset);
   }

   @NotNull
   public String readString(long byteCount, @NotNull Charset charset) {
      Intrinsics.checkNotNullParameter(charset, "charset");
      this.require(byteCount);
      int $i$f$getBuffer = 0;
      return this.bufferField.readString(byteCount, charset);
   }

   @Nullable
   public String readUtf8Line() {
      int $i$f$commonReadUtf8Line = 0;
      long newline$iv = this.indexOf((byte)10);
      String var10000;
      if (newline$iv == -1L) {
         int $i$f$getBuffer = 0;
         if (this.bufferField.size() != 0L) {
            $i$f$getBuffer = 0;
            var10000 = this.readUtf8(this.bufferField.size());
         } else {
            var10000 = null;
         }
      } else {
         int $i$f$getBuffer = 0;
         var10000 = -Buffer.readUtf8Line(this.bufferField, newline$iv);
      }

      return var10000;
   }

   @NotNull
   public String readUtf8LineStrict() {
      return this.readUtf8LineStrict(Long.MAX_VALUE);
   }

   @NotNull
   public String readUtf8LineStrict(long limit) {
      int $i$f$commonReadUtf8LineStrict = 0;
      if (limit < 0L) {
         int var7 = 0;
         String var19 = "limit < 0: " + limit;
         throw new IllegalArgumentException(var19.toString());
      } else {
         label37: {
            long scanLength$iv = limit == Long.MAX_VALUE ? Long.MAX_VALUE : limit + 1L;
            long newline$iv = this.indexOf((byte)10, 0L, scanLength$iv);
            String var10000;
            if (newline$iv != -1L) {
               int $i$f$getBuffer = 0;
               var10000 = -Buffer.readUtf8Line(this.bufferField, newline$iv);
            } else {
               if (scanLength$iv >= Long.MAX_VALUE || !this.request(scanLength$iv)) {
                  break label37;
               }

               int $i$f$getBuffer = 0;
               if (this.bufferField.getByte(scanLength$iv - 1L) != 13 || !this.request(scanLength$iv + 1L)) {
                  break label37;
               }

               $i$f$getBuffer = 0;
               if (this.bufferField.getByte(scanLength$iv) != 10) {
                  break label37;
               }

               $i$f$getBuffer = 0;
               var10000 = -Buffer.readUtf8Line(this.bufferField, scanLength$iv);
            }

            return var10000;
         }

         Buffer data$iv = new Buffer();
         int $i$f$getBuffer = 0;
         Buffer var25 = this.bufferField;
         byte this_$iv$iv = 32;
         int $i$f$getBuffer = 0;
         long b$iv$iv = this.bufferField.size();
         int $i$f$minOf = 0;
         var25.copyTo(data$iv, 0L, Math.min((long)this_$iv$iv, b$iv$iv));
         StringBuilder var10002 = (new StringBuilder()).append("\\n not found: limit=");
         $i$f$getBuffer = 0;
         throw new EOFException(var10002.append(Math.min(this.bufferField.size(), limit)).append(" content=").append(data$iv.readByteString().hex()).append('…').toString());
      }
   }

   public int readUtf8CodePoint() {
      int $i$f$commonReadUtf8CodePoint = 0;
      this.require(1L);
      int $i$f$getBuffer = 0;
      int b0$iv = this.bufferField.getByte(0L);
      if ((b0$iv & 224) == 192) {
         this.require(2L);
      } else if ((b0$iv & 240) == 224) {
         this.require(3L);
      } else if ((b0$iv & 248) == 240) {
         this.require(4L);
      }

      $i$f$getBuffer = 0;
      return this.bufferField.readUtf8CodePoint();
   }

   public short readShort() {
      int $i$f$commonReadShort = 0;
      this.require(2L);
      int $i$f$getBuffer = 0;
      return this.bufferField.readShort();
   }

   public short readShortLe() {
      int $i$f$commonReadShortLe = 0;
      this.require(2L);
      int $i$f$getBuffer = 0;
      return this.bufferField.readShortLe();
   }

   public int readInt() {
      int $i$f$commonReadInt = 0;
      this.require(4L);
      int $i$f$getBuffer = 0;
      return this.bufferField.readInt();
   }

   public int readIntLe() {
      int $i$f$commonReadIntLe = 0;
      this.require(4L);
      int $i$f$getBuffer = 0;
      return this.bufferField.readIntLe();
   }

   public long readLong() {
      int $i$f$commonReadLong = 0;
      this.require(8L);
      int $i$f$getBuffer = 0;
      return this.bufferField.readLong();
   }

   public long readLongLe() {
      int $i$f$commonReadLongLe = 0;
      this.require(8L);
      int $i$f$getBuffer = 0;
      return this.bufferField.readLongLe();
   }

   public long readDecimalLong() {
      RealBufferedSource $this$commonReadDecimalLong$iv = this;
      int $i$f$commonReadDecimalLong = 0;
      this.require(1L);

      for(long pos$iv = 0L; $this$commonReadDecimalLong$iv.request(pos$iv + 1L); ++pos$iv) {
         int $i$f$getBuffer = 0;
         byte b$iv = $this$commonReadDecimalLong$iv.bufferField.getByte(pos$iv);
         if ((b$iv < 48 || b$iv > 57) && (pos$iv != 0L || b$iv != 45)) {
            if (pos$iv == 0L) {
               StringBuilder var10002 = (new StringBuilder()).append("Expected a digit or '-' but was 0x");
               String var10003 = Integer.toString(b$iv, CharsKt.checkRadix(16));
               Intrinsics.checkNotNullExpressionValue(var10003, "toString(...)");
               throw new NumberFormatException(var10002.append(var10003).toString());
            }
            break;
         }
      }

      int $i$f$getBuffer = 0;
      return $this$commonReadDecimalLong$iv.bufferField.readDecimalLong();
   }

   public long readHexadecimalUnsignedLong() {
      RealBufferedSource $this$commonReadHexadecimalUnsignedLong$iv = this;
      int $i$f$commonReadHexadecimalUnsignedLong = 0;
      this.require(1L);

      for(int pos$iv = 0; $this$commonReadHexadecimalUnsignedLong$iv.request((long)(pos$iv + 1)); ++pos$iv) {
         int $i$f$getBuffer = 0;
         byte b$iv = $this$commonReadHexadecimalUnsignedLong$iv.bufferField.getByte((long)pos$iv);
         if ((b$iv < 48 || b$iv > 57) && (b$iv < 97 || b$iv > 102) && (b$iv < 65 || b$iv > 70)) {
            if (pos$iv == 0) {
               StringBuilder var10002 = (new StringBuilder()).append("Expected leading [0-9a-fA-F] character but was 0x");
               String var10003 = Integer.toString(b$iv, CharsKt.checkRadix(16));
               Intrinsics.checkNotNullExpressionValue(var10003, "toString(...)");
               throw new NumberFormatException(var10002.append(var10003).toString());
            }
            break;
         }
      }

      int $i$f$getBuffer = 0;
      return $this$commonReadHexadecimalUnsignedLong$iv.bufferField.readHexadecimalUnsignedLong();
   }

   public void skip(long byteCount) {
      RealBufferedSource $this$commonSkip$iv = this;
      int $i$f$commonSkip = 0;
      long byteCount$iv = byteCount;
      if (this.closed) {
         int $i$f$getBuffer = 0;
         String var19 = "closed";
         throw new IllegalStateException(var19.toString());
      } else {
         while(byteCount$iv > 0L) {
            int $i$f$getBuffer = 0;
            if ($this$commonSkip$iv.bufferField.size() == 0L) {
               $i$f$getBuffer = 0;
               if ($this$commonSkip$iv.source.read($this$commonSkip$iv.bufferField, 8192L) == -1L) {
                  throw new EOFException();
               }
            }

            int $i$f$getBuffer = 0;
            long var13 = $this$commonSkip$iv.bufferField.size();
            long toSkip$iv = Math.min(byteCount$iv, var13);
            $i$f$getBuffer = 0;
            $this$commonSkip$iv.bufferField.skip(toSkip$iv);
            byteCount$iv -= toSkip$iv;
         }

      }
   }

   public long indexOf(byte b) {
      return this.indexOf(b, 0L, Long.MAX_VALUE);
   }

   public long indexOf(byte b, long fromIndex) {
      return this.indexOf(b, fromIndex, Long.MAX_VALUE);
   }

   public long indexOf(byte b, long fromIndex, long toIndex) {
      RealBufferedSource $this$commonIndexOf$iv = this;
      byte b$iv = b;
      long toIndex$iv = toIndex;
      int $i$f$commonIndexOf = 0;
      long fromIndex$iv = 0L;
      fromIndex$iv = fromIndex;
      if (this.closed) {
         int var25 = 0;
         String var26 = "closed";
         throw new IllegalStateException(var26.toString());
      } else if (!(0L <= fromIndex ? fromIndex <= toIndex : false)) {
         int var13 = 0;
         String var24 = "fromIndex=" + fromIndex + " toIndex=" + toIndex;
         throw new IllegalArgumentException(var24.toString());
      } else {
         long var10000;
         while(true) {
            if (fromIndex$iv < toIndex$iv) {
               int $i$f$getBuffer = 0;
               long result$iv = $this$commonIndexOf$iv.bufferField.indexOf(b$iv, fromIndex$iv, toIndex$iv);
               if (result$iv != -1L) {
                  var10000 = result$iv;
                  break;
               }

               int $i$f$getBuffer = 0;
               long lastBufferSize$iv = $this$commonIndexOf$iv.bufferField.size();
               if (lastBufferSize$iv < toIndex$iv) {
                  $i$f$getBuffer = 0;
                  if ($this$commonIndexOf$iv.source.read($this$commonIndexOf$iv.bufferField, 8192L) != -1L) {
                     fromIndex$iv = Math.max(fromIndex$iv, lastBufferSize$iv);
                     continue;
                  }
               }

               var10000 = -1L;
               break;
            }

            var10000 = -1L;
            break;
         }

         return var10000;
      }
   }

   public long indexOf(@NotNull ByteString bytes) {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      return this.indexOf(bytes, 0L);
   }

   public long indexOf(@NotNull ByteString bytes, long fromIndex) {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      return this.indexOf(bytes, fromIndex, Long.MAX_VALUE);
   }

   public long indexOf(@NotNull ByteString bytes, long fromIndex, long toIndex) {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      return -RealBufferedSource.commonIndexOf$default(this, bytes, 0, 0, fromIndex, toIndex, 6, (Object)null);
   }

   public long indexOfElement(@NotNull ByteString targetBytes) {
      Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
      return this.indexOfElement(targetBytes, 0L);
   }

   public long indexOfElement(@NotNull ByteString targetBytes, long fromIndex) {
      Intrinsics.checkNotNullParameter(targetBytes, "targetBytes");
      RealBufferedSource $this$commonIndexOfElement$iv = this;
      ByteString targetBytes$iv = targetBytes;
      int $i$f$commonIndexOfElement = 0;
      long fromIndex$iv = fromIndex;
      if (this.closed) {
         int var11 = 0;
         String var20 = "closed";
         throw new IllegalStateException(var20.toString());
      } else {
         long var10000;
         while(true) {
            int $i$f$getBuffer = 0;
            long result$iv = $this$commonIndexOfElement$iv.bufferField.indexOfElement(targetBytes$iv, fromIndex$iv);
            if (result$iv != -1L) {
               var10000 = result$iv;
               break;
            }

            int $i$f$getBuffer = 0;
            long lastBufferSize$iv = $this$commonIndexOfElement$iv.bufferField.size();
            $i$f$getBuffer = 0;
            if ($this$commonIndexOfElement$iv.source.read($this$commonIndexOfElement$iv.bufferField, 8192L) == -1L) {
               var10000 = -1L;
               break;
            }

            fromIndex$iv = Math.max(fromIndex$iv, lastBufferSize$iv);
         }

         return var10000;
      }
   }

   public boolean rangeEquals(long offset, @NotNull ByteString bytes) {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      return this.rangeEquals(offset, bytes, 0, bytes.size());
   }

   public boolean rangeEquals(long offset, @NotNull ByteString bytes, int bytesOffset, int byteCount) {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      int $i$f$commonRangeEquals = 0;
      if (this.closed) {
         int var13 = 0;
         String var14 = "closed";
         throw new IllegalStateException(var14.toString());
      } else {
         return byteCount < 0 ? false : (offset < 0L ? false : (bytesOffset >= 0 && bytesOffset + byteCount <= bytes.size() ? (byteCount == 0 ? true : -RealBufferedSource.commonIndexOf(this, bytes, bytesOffset, byteCount, offset, offset + 1L) != -1L) : false));
      }
   }

   @NotNull
   public BufferedSource peek() {
      int $i$f$commonPeek = 0;
      return Okio.buffer(new PeekSource(this));
   }

   @NotNull
   public InputStream inputStream() {
      return new InputStream() {
         public int read() {
            if (RealBufferedSource.this.closed) {
               throw new IOException("closed");
            } else {
               RealBufferedSource this_$iv = RealBufferedSource.this;
               int $i$f$getBuffer = 0;
               if (this_$iv.bufferField.size() == 0L) {
                  RealBufferedSource this_$iv = RealBufferedSource.this;
                  int $i$f$getBuffer = 0;
                  long count = RealBufferedSource.this.source.read(this_$iv.bufferField, 8192L);
                  if (count == -1L) {
                     return -1;
                  }
               }

               this_$iv = RealBufferedSource.this;
               $i$f$getBuffer = 0;
               byte $this$and$iv = this_$iv.bufferField.readByte();
               $i$f$getBuffer = 255;
               int $i$f$and = 0;
               return $this$and$iv & $i$f$getBuffer;
            }
         }

         public int read(byte[] data, int offset, int byteCount) {
            Intrinsics.checkNotNullParameter(data, "data");
            if (RealBufferedSource.this.closed) {
               throw new IOException("closed");
            } else {
               -SegmentedByteString.checkOffsetAndCount((long)data.length, (long)offset, (long)byteCount);
               RealBufferedSource this_$iv = RealBufferedSource.this;
               int $i$f$getBuffer = 0;
               if (this_$iv.bufferField.size() == 0L) {
                  RealBufferedSource this_$iv = RealBufferedSource.this;
                  int $i$f$getBuffer = 0;
                  long count = RealBufferedSource.this.source.read(this_$iv.bufferField, 8192L);
                  if (count == -1L) {
                     return -1;
                  }
               }

               this_$iv = RealBufferedSource.this;
               $i$f$getBuffer = 0;
               return this_$iv.bufferField.read(data, offset, byteCount);
            }
         }

         public int available() {
            if (RealBufferedSource.this.closed) {
               throw new IOException("closed");
            } else {
               RealBufferedSource this_$iv = RealBufferedSource.this;
               int $i$f$getBuffer = 0;
               long a$iv = this_$iv.bufferField.size();
               int b$iv = Integer.MAX_VALUE;
               int $i$f$minOf = 0;
               return (int)Math.min(a$iv, (long)b$iv);
            }
         }

         public void close() {
            RealBufferedSource.this.close();
         }

         public String toString() {
            return RealBufferedSource.this + ".inputStream()";
         }

         public long transferTo(OutputStream out) {
            Intrinsics.checkNotNullParameter(out, "out");
            if (RealBufferedSource.this.closed) {
               throw new IOException("closed");
            } else {
               long count = 0L;

               while(true) {
                  RealBufferedSource this_$iv = RealBufferedSource.this;
                  int $i$f$getBuffer = 0;
                  if (this_$iv.bufferField.size() == 0L) {
                     RealBufferedSource this_$iv = RealBufferedSource.this;
                     int $i$f$getBuffer = 0;
                     long read = RealBufferedSource.this.source.read(this_$iv.bufferField, 8192L);
                     if (read == -1L) {
                        return count;
                     }
                  }

                  this_$iv = RealBufferedSource.this;
                  $i$f$getBuffer = 0;
                  count += this_$iv.bufferField.size();
                  this_$iv = RealBufferedSource.this;
                  $i$f$getBuffer = 0;
                  Buffer.writeTo$default(this_$iv.bufferField, out, 0L, 2, (Object)null);
               }
            }
         }
      };
   }

   public boolean isOpen() {
      return !this.closed;
   }

   public void close() {
      int $i$f$commonClose = 0;
      if (!this.closed) {
         this.closed = true;
         this.source.close();
         int $i$f$getBuffer = 0;
         this.bufferField.clear();
      }

   }

   @NotNull
   public Timeout timeout() {
      int $i$f$commonTimeout = 0;
      return this.source.timeout();
   }

   @NotNull
   public String toString() {
      int $i$f$commonToString = 0;
      return "buffer(" + this.source + ')';
   }
}
