package okhttp3.internal.http;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal._UtilJvmKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000+\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0003\u001a\f\u0010\f\u001a\u0004\u0018\u00010\r*\u00020\u0007\u001a\n\u0010\u000e\u001a\u00020\u0007*\u00020\r\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0080T¢\u0006\u0002\n\u0000\"\u0010\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0004\"\u0016\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\b\"\u0018\u0010\t\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\u0006X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000b¨\u0006\u000f"},
   d2 = {"MAX_DATE", "", "STANDARD_DATE_FORMAT", "okhttp3/internal/http/DateFormattingKt$STANDARD_DATE_FORMAT$1", "Lokhttp3/internal/http/DateFormattingKt$STANDARD_DATE_FORMAT$1;", "BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS", "", "", "[Ljava/lang/String;", "BROWSER_COMPATIBLE_DATE_FORMATS", "Ljava/text/DateFormat;", "[Ljava/text/DateFormat;", "toHttpDateOrNull", "Ljava/util/Date;", "toHttpDateString", "okhttp"}
)
public final class DateFormattingKt {
   public static final long MAX_DATE = 253402300799999L;
   @NotNull
   private static final <undefinedtype> STANDARD_DATE_FORMAT = new ThreadLocal<DateFormat>() {
      protected DateFormat initialValue() {
         SimpleDateFormat $this$initialValue_u24lambda_u240 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
         int var3 = 0;
         $this$initialValue_u24lambda_u240.setLenient(false);
         $this$initialValue_u24lambda_u240.setTimeZone(_UtilJvmKt.UTC);
         return (DateFormat)$this$initialValue_u24lambda_u240;
      }
   };
   @NotNull
   private static final String[] BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS;
   @NotNull
   private static final DateFormat[] BROWSER_COMPATIBLE_DATE_FORMATS;

   @Nullable
   public static final Date toHttpDateOrNull(@NotNull String $this$toHttpDateOrNull) {
      Intrinsics.checkNotNullParameter($this$toHttpDateOrNull, "<this>");
      if (((CharSequence)$this$toHttpDateOrNull).length() == 0) {
         return null;
      } else {
         ParsePosition position = new ParsePosition(0);
         Object result = null;
         Object var14 = ((DateFormat)STANDARD_DATE_FORMAT.get()).parse($this$toHttpDateOrNull, position);
         if (position.getIndex() == $this$toHttpDateOrNull.length()) {
            return var14;
         } else {
            String[] var3 = BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS;
            synchronized(var3){}

            try {
               int var4 = 0;
               int i = 0;

               for(int var6 = BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS.length; i < var6; ++i) {
                  DateFormat format = BROWSER_COMPATIBLE_DATE_FORMATS[i];
                  if (format == null) {
                     SimpleDateFormat $this$toHttpDateOrNull_u24lambda_u240_u240 = new SimpleDateFormat(BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS[i], Locale.US);
                     int var10 = 0;
                     $this$toHttpDateOrNull_u24lambda_u240_u240.setTimeZone(_UtilJvmKt.UTC);
                     format = (DateFormat)$this$toHttpDateOrNull_u24lambda_u240_u240;
                     BROWSER_COMPATIBLE_DATE_FORMATS[i] = format;
                  }

                  position.setIndex(0);
                  var14 = format.parse($this$toHttpDateOrNull, position);
                  if (position.getIndex() != 0) {
                     Date var11 = var14;
                     return var11;
                  }
               }

               Unit var16 = Unit.INSTANCE;
               return null;
            } finally {
               ;
            }
         }
      }
   }

   @NotNull
   public static final String toHttpDateString(@NotNull Date $this$toHttpDateString) {
      Intrinsics.checkNotNullParameter($this$toHttpDateString, "<this>");
      String var10000 = ((DateFormat)STANDARD_DATE_FORMAT.get()).format($this$toHttpDateString);
      Intrinsics.checkNotNullExpressionValue(var10000, "format(...)");
      return var10000;
   }

   static {
      String[] var0 = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z", "EEE MMM d yyyy HH:mm:ss z"};
      BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS = var0;
      BROWSER_COMPATIBLE_DATE_FORMATS = new DateFormat[BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS.length];
   }
}
