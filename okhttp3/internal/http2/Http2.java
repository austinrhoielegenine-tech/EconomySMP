package okhttp3.internal.http2;

import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.internal._UtilJvmKt;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0013\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J.\u0010 \u001a\u00020\u001c2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u00072\u0006\u0010$\u001a\u00020\u00072\u0006\u0010%\u001a\u00020\u00072\u0006\u0010&\u001a\u00020\u0007J&\u0010'\u001a\u00020\u001c2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u00072\u0006\u0010$\u001a\u00020\u00072\u0006\u0010(\u001a\u00020)J\u0015\u0010*\u001a\u00020\u001c2\u0006\u0010%\u001a\u00020\u0007H\u0000¢\u0006\u0002\b+J\u0016\u0010,\u001a\u00020\u001c2\u0006\u0010%\u001a\u00020\u00072\u0006\u0010&\u001a\u00020\u0007R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0007X\u0086T¢\u0006\u0002\n\u0000R\u0016\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001bX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u001dR\u0018\u0010\u001e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001c0\u001bX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u001dR\u0016\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u001c0\u001bX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u001d¨\u0006-"},
   d2 = {"Lokhttp3/internal/http2/Http2;", "", "<init>", "()V", "CONNECTION_PREFACE", "Lokio/ByteString;", "INITIAL_MAX_FRAME_SIZE", "", "TYPE_DATA", "TYPE_HEADERS", "TYPE_PRIORITY", "TYPE_RST_STREAM", "TYPE_SETTINGS", "TYPE_PUSH_PROMISE", "TYPE_PING", "TYPE_GOAWAY", "TYPE_WINDOW_UPDATE", "TYPE_CONTINUATION", "FLAG_NONE", "FLAG_ACK", "FLAG_END_STREAM", "FLAG_END_HEADERS", "FLAG_END_PUSH_PROMISE", "FLAG_PADDED", "FLAG_PRIORITY", "FLAG_COMPRESSED", "FRAME_NAMES", "", "", "[Ljava/lang/String;", "FLAGS", "BINARY", "frameLog", "inbound", "", "streamId", "length", "type", "flags", "frameLogWindowUpdate", "windowSizeIncrement", "", "formattedType", "formattedType$okhttp", "formatFlags", "okhttp"}
)
public final class Http2 {
   @NotNull
   public static final Http2 INSTANCE = new Http2();
   @JvmField
   @NotNull
   public static final ByteString CONNECTION_PREFACE;
   public static final int INITIAL_MAX_FRAME_SIZE = 16384;
   public static final int TYPE_DATA = 0;
   public static final int TYPE_HEADERS = 1;
   public static final int TYPE_PRIORITY = 2;
   public static final int TYPE_RST_STREAM = 3;
   public static final int TYPE_SETTINGS = 4;
   public static final int TYPE_PUSH_PROMISE = 5;
   public static final int TYPE_PING = 6;
   public static final int TYPE_GOAWAY = 7;
   public static final int TYPE_WINDOW_UPDATE = 8;
   public static final int TYPE_CONTINUATION = 9;
   public static final int FLAG_NONE = 0;
   public static final int FLAG_ACK = 1;
   public static final int FLAG_END_STREAM = 1;
   public static final int FLAG_END_HEADERS = 4;
   public static final int FLAG_END_PUSH_PROMISE = 4;
   public static final int FLAG_PADDED = 8;
   public static final int FLAG_PRIORITY = 32;
   public static final int FLAG_COMPRESSED = 32;
   @NotNull
   private static final String[] FRAME_NAMES;
   @NotNull
   private static final String[] FLAGS;
   @NotNull
   private static final String[] BINARY;

   private Http2() {
   }

   @NotNull
   public final String frameLog(boolean inbound, int streamId, int length, int type, int flags) {
      String formattedType = this.formattedType$okhttp(type);
      String formattedFlags = this.formatFlags(type, flags);
      String direction = inbound ? "<<" : ">>";
      Object[] var9 = new Object[]{direction, streamId, length, formattedType, formattedFlags};
      return _UtilJvmKt.format("%s 0x%08x %5d %-13s %s", var9);
   }

   @NotNull
   public final String frameLogWindowUpdate(boolean inbound, int streamId, int length, long windowSizeIncrement) {
      String formattedType = this.formattedType$okhttp(8);
      String direction = inbound ? "<<" : ">>";
      Object[] var8 = new Object[]{direction, streamId, length, formattedType, windowSizeIncrement};
      return _UtilJvmKt.format("%s 0x%08x %5d %-13s %d", var8);
   }

   @NotNull
   public final String formattedType$okhttp(int type) {
      String var10000;
      if (type < FRAME_NAMES.length) {
         var10000 = FRAME_NAMES[type];
      } else {
         Object[] var2 = new Object[]{type};
         var10000 = _UtilJvmKt.format("0x%02x", var2);
      }

      return var10000;
   }

   @NotNull
   public final String formatFlags(int type, int flags) {
      if (flags == 0) {
         return "";
      } else {
         switch (type) {
            case 2:
            case 3:
            case 7:
            case 8:
               return BINARY[flags];
            case 4:
            case 6:
               return flags == 1 ? "ACK" : BINARY[flags];
            case 5:
            default:
               String var10000;
               if (flags < FLAGS.length) {
                  var10000 = FLAGS[flags];
                  Intrinsics.checkNotNull(var10000);
               } else {
                  var10000 = BINARY[flags];
               }

               String result = var10000;
               return type == 5 && (flags & 4) != 0 ? StringsKt.replace$default(result, "HEADERS", "PUSH_PROMISE", false, 4, (Object)null) : (type == 0 && (flags & 32) != 0 ? StringsKt.replace$default(result, "PRIORITY", "COMPRESSED", false, 4, (Object)null) : result);
         }
      }
   }

   static {
      CONNECTION_PREFACE = ByteString.Companion.encodeUtf8("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n");
      String[] var0 = new String[]{"DATA", "HEADERS", "PRIORITY", "RST_STREAM", "SETTINGS", "PUSH_PROMISE", "PING", "GOAWAY", "WINDOW_UPDATE", "CONTINUATION"};
      FRAME_NAMES = var0;
      FLAGS = new String[64];
      int var8 = 0;

      String[] var1;
      for(var1 = new String[256]; var8 < 256; ++var8) {
         Object[] var3 = new Object[1];
         String var10005 = Integer.toBinaryString(var8);
         Intrinsics.checkNotNullExpressionValue(var10005, "toBinaryString(...)");
         var3[0] = var10005;
         var1[var8] = StringsKt.replace$default(_UtilJvmKt.format("%8s", var3), ' ', '0', false, 4, (Object)null);
      }

      BINARY = var1;
      FLAGS[0] = "";
      FLAGS[1] = "END_STREAM";
      int[] var10 = new int[]{1};
      int[] prefixFlags = var10;
      FLAGS[8] = "PADDED";
      int var11 = 0;

      for(int var2 = var10.length; var11 < var2; ++var11) {
         int prefixFlag = prefixFlags[var11];
         FLAGS[prefixFlag | 8] = FLAGS[prefixFlag] + "|PADDED";
      }

      FLAGS[4] = "END_HEADERS";
      FLAGS[32] = "PRIORITY";
      FLAGS[36] = "END_HEADERS|PRIORITY";
      int[] var13 = new int[]{4, 32, 36};
      int[] frameFlags = var13;
      int i = 0;

      for(int var17 = var13.length; i < var17; ++i) {
         int frameFlag = frameFlags[i];
         int var5 = 0;

         for(int var6 = prefixFlags.length; var5 < var6; ++var5) {
            int prefixFlag = prefixFlags[var5];
            FLAGS[prefixFlag | frameFlag] = FLAGS[prefixFlag] + '|' + FLAGS[frameFlag];
            FLAGS[prefixFlag | frameFlag | 8] = FLAGS[prefixFlag] + '|' + FLAGS[frameFlag] + "|PADDED";
         }
      }

      i = 0;

      for(int var18 = FLAGS.length; i < var18; ++i) {
         if (FLAGS[i] == null) {
            FLAGS[i] = BINARY[i];
         }
      }

   }
}
