package okio.internal;

import java.util.Arrays;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u000e\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u001a\n\u0010\u0006\u001a\u00020\u0002*\u00020\u0001¨\u0006\u0007"},
   d2 = {"commonToUtf8String", "", "", "beginIndex", "", "endIndex", "commonAsUtf8ToByteArray", "okio"}
)
@SourceDebugExtension({"SMAP\n-Utf8.kt\nKotlin\n*S Kotlin\n*F\n+ 1 -Utf8.kt\nokio/internal/_Utf8Kt\n+ 2 Utf8.kt\nokio/Utf8\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,60:1\n260#2,16:61\n277#2:78\n397#2,9:79\n127#2:88\n406#2,20:90\n279#2,3:110\n440#2,4:113\n127#2:117\n446#2,10:118\n127#2:128\n456#2,5:129\n127#2:134\n461#2,24:135\n283#2,3:159\n500#2,3:162\n286#2,12:165\n503#2:177\n127#2:178\n506#2,2:179\n127#2:181\n510#2,10:182\n127#2:192\n520#2,5:193\n127#2:198\n525#2,5:199\n127#2:204\n530#2,28:205\n302#2,6:233\n138#2,67:239\n67#3:77\n73#3:89\n*S KotlinDebug\n*F\n+ 1 -Utf8.kt\nokio/internal/_Utf8Kt\n*L\n34#1:61,16\n34#1:78\n34#1:79,9\n34#1:88\n34#1:90,20\n34#1:110,3\n34#1:113,4\n34#1:117\n34#1:118,10\n34#1:128\n34#1:129,5\n34#1:134\n34#1:135,24\n34#1:159,3\n34#1:162,3\n34#1:165,12\n34#1:177\n34#1:178\n34#1:179,2\n34#1:181\n34#1:182,10\n34#1:192\n34#1:193,5\n34#1:198\n34#1:199,5\n34#1:204\n34#1:205,28\n34#1:233,6\n50#1:239,67\n34#1:77\n34#1:89\n*E\n"})
public final class _Utf8Kt {
   @NotNull
   public static final String commonToUtf8String(@NotNull byte[] $this$commonToUtf8String, int beginIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$commonToUtf8String, "<this>");
      if (beginIndex >= 0 && endIndex <= $this$commonToUtf8String.length && beginIndex <= endIndex) {
         char[] chars = new char[endIndex - beginIndex];
         int length = 0;
         byte[] $this$processUtf16Chars$iv = $this$commonToUtf8String;
         int endIndex$iv = endIndex;
         int $i$f$processUtf16Chars = 0;
         int index$iv = beginIndex;

         while(index$iv < endIndex$iv) {
            byte b0$iv = $this$processUtf16Chars$iv[index$iv];
            if (b0$iv >= 0) {
               char c = (char)b0$iv;
               int var109 = 0;
               int var145 = length++;
               chars[var145] = c;
               ++index$iv;

               while(index$iv < endIndex$iv && $this$processUtf16Chars$iv[index$iv] >= 0) {
                  c = (char)$this$processUtf16Chars$iv[index$iv++];
                  var109 = 0;
                  var145 = length++;
                  chars[var145] = c;
               }

               Unit var241 = Unit.INSTANCE;
            } else {
               int other$iv$iv = 5;
               int $i$f$shr = 0;
               if (b0$iv >> other$iv$iv == -2) {
                  int $i$f$process2Utf8Bytes = 0;
                  int var239;
                  byte var243;
                  if (endIndex$iv <= index$iv + 1) {
                     char codePoint$iv = '�';
                     int var180 = 0;
                     char c = (char)codePoint$iv;
                     int var105 = 0;
                     int var141 = length++;
                     chars[var141] = c;
                     Unit var197 = Unit.INSTANCE;
                     var239 = index$iv;
                     var243 = 1;
                  } else {
                     byte b0$iv$iv = $this$processUtf16Chars$iv[index$iv];
                     byte b1$iv$iv = $this$processUtf16Chars$iv[index$iv + 1];
                     int $i$f$isUtf8Continuation = 0;
                     int other$iv$iv$iv$iv = 192;
                     int $i$f$and = 0;
                     if ((b1$iv$iv & other$iv$iv$iv$iv) != 128) {
                        char codePoint$iv = '�';
                        int var181 = 0;
                        char c = (char)codePoint$iv;
                        int var106 = 0;
                        int var142 = length++;
                        chars[var142] = c;
                        Unit var198 = Unit.INSTANCE;
                        var239 = index$iv;
                        var243 = 1;
                     } else {
                        int codePoint$iv$iv = 3968 ^ b1$iv$iv ^ b0$iv$iv << 6;
                        if (codePoint$iv$iv < 128) {
                           char codePoint$iv = '�';
                           int var182 = 0;
                           char c = (char)codePoint$iv;
                           int var107 = 0;
                           int var143 = length++;
                           chars[var143] = c;
                           Unit var199 = Unit.INSTANCE;
                           var239 = index$iv;
                        } else {
                           int var183 = 0;
                           char c = (char)codePoint$iv$iv;
                           int var108 = 0;
                           int var144 = length++;
                           chars[var144] = c;
                           Unit var200 = Unit.INSTANCE;
                           var239 = index$iv;
                        }

                        var243 = 2;
                     }
                  }

                  index$iv = var239 + var243;
                  Unit var240 = Unit.INSTANCE;
               } else {
                  other$iv$iv = 4;
                  $i$f$shr = 0;
                  if (b0$iv >> other$iv$iv == -2) {
                     int $i$f$process3Utf8Bytes = 0;
                     int var237;
                     byte var242;
                     if (endIndex$iv <= index$iv + 2) {
                        label200: {
                           char it$iv = '�';
                           int var174 = 0;
                           char c = (char)it$iv;
                           int var99 = 0;
                           int var135 = length++;
                           chars[var135] = c;
                           Unit var191 = Unit.INSTANCE;
                           var237 = index$iv;
                           if (endIndex$iv > index$iv + 1) {
                              byte byte$iv$iv$iv = $this$processUtf16Chars$iv[index$iv + 1];
                              int $i$f$isUtf8Continuation = 0;
                              int other$iv$iv$iv$iv = 192;
                              int $i$f$and = 0;
                              if ((byte$iv$iv$iv & other$iv$iv$iv$iv) == 128) {
                                 var242 = 2;
                                 break label200;
                              }
                           }

                           var242 = 1;
                        }
                     } else {
                        byte b0$iv$iv = $this$processUtf16Chars$iv[index$iv];
                        byte b1$iv$iv = $this$processUtf16Chars$iv[index$iv + 1];
                        int $i$f$isUtf8Continuation = 0;
                        int other$iv$iv$iv$iv = 192;
                        int $i$f$and = 0;
                        if ((b1$iv$iv & other$iv$iv$iv$iv) != 128) {
                           char it$iv = '�';
                           int var175 = 0;
                           char c = (char)it$iv;
                           int var100 = 0;
                           int var136 = length++;
                           chars[var136] = c;
                           Unit var192 = Unit.INSTANCE;
                           var237 = index$iv;
                           var242 = 1;
                        } else {
                           byte b2$iv$iv = $this$processUtf16Chars$iv[index$iv + 2];
                           int $i$f$isUtf8Continuation = 0;
                           $i$f$and = 192;
                           int $i$f$and = 0;
                           if ((b2$iv$iv & $i$f$and) != 128) {
                              char it$iv = '�';
                              int var176 = 0;
                              char c = (char)it$iv;
                              int var101 = 0;
                              int var137 = length++;
                              chars[var137] = c;
                              Unit var193 = Unit.INSTANCE;
                              var237 = index$iv;
                              var242 = 2;
                           } else {
                              $i$f$isUtf8Continuation = -123008 ^ b2$iv$iv ^ b1$iv$iv << 6 ^ b0$iv$iv << 12;
                              if ($i$f$isUtf8Continuation < 2048) {
                                 char codePoint$iv = '�';
                                 int var177 = 0;
                                 char c = (char)codePoint$iv;
                                 int var102 = 0;
                                 int var138 = length++;
                                 chars[var138] = c;
                                 Unit var194 = Unit.INSTANCE;
                                 var237 = index$iv;
                              } else if (55296 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 57344 : false) {
                                 char codePoint$iv = '�';
                                 int var178 = 0;
                                 char c = (char)codePoint$iv;
                                 int var103 = 0;
                                 int var139 = length++;
                                 chars[var139] = c;
                                 Unit var195 = Unit.INSTANCE;
                                 var237 = index$iv;
                              } else {
                                 int var179 = 0;
                                 char c = (char)$i$f$isUtf8Continuation;
                                 int var104 = 0;
                                 int var140 = length++;
                                 chars[var140] = c;
                                 Unit var196 = Unit.INSTANCE;
                                 var237 = index$iv;
                              }

                              var242 = 3;
                           }
                        }
                     }

                     index$iv = var237 + var242;
                     Unit var238 = Unit.INSTANCE;
                  } else {
                     other$iv$iv = 3;
                     $i$f$shr = 0;
                     if (b0$iv >> other$iv$iv != -2) {
                        char c = '�';
                        int var98 = 0;
                        int var134 = length++;
                        chars[var134] = c;
                        Integer var236 = index$iv++;
                     } else {
                        int $i$f$process4Utf8Bytes = 0;
                        int var10000;
                        byte var10001;
                        if (endIndex$iv <= index$iv + 3) {
                           label234: {
                              char it$iv = '�';
                              int var20 = 0;
                              if (it$iv != '�') {
                                 char c = (char)((it$iv >>> 10) + 'ퟀ');
                                 int var12 = 0;
                                 int var13 = length++;
                                 chars[var13] = c;
                                 c = (char)((it$iv & 1023) + '\udc00');
                                 var12 = 0;
                                 var13 = length++;
                                 chars[var13] = c;
                              } else {
                                 char c = '�';
                                 int var76 = 0;
                                 int var112 = length++;
                                 chars[var112] = c;
                              }

                              Unit var21 = Unit.INSTANCE;
                              var10000 = index$iv;
                              if (endIndex$iv > index$iv + 1) {
                                 byte byte$iv$iv$iv = $this$processUtf16Chars$iv[index$iv + 1];
                                 int $i$f$isUtf8Continuation = 0;
                                 int other$iv$iv$iv$iv = 192;
                                 int $i$f$and = 0;
                                 if ((byte$iv$iv$iv & other$iv$iv$iv$iv) == 128) {
                                    if (endIndex$iv > index$iv + 2) {
                                       byte$iv$iv$iv = $this$processUtf16Chars$iv[index$iv + 2];
                                       $i$f$isUtf8Continuation = 0;
                                       other$iv$iv$iv$iv = 192;
                                       $i$f$and = 0;
                                       if ((byte$iv$iv$iv & other$iv$iv$iv$iv) == 128) {
                                          var10001 = 3;
                                          break label234;
                                       }
                                    }

                                    var10001 = 2;
                                    break label234;
                                 }
                              }

                              var10001 = 1;
                           }
                        } else {
                           byte b0$iv$iv = $this$processUtf16Chars$iv[index$iv];
                           byte b1$iv$iv = $this$processUtf16Chars$iv[index$iv + 1];
                           int $i$f$isUtf8Continuation = 0;
                           int other$iv$iv$iv$iv = 192;
                           int $i$f$and = 0;
                           if ((b1$iv$iv & other$iv$iv$iv$iv) != 128) {
                              char it$iv = '�';
                              int var167 = 0;
                              if (it$iv != '�') {
                                 char c = (char)((it$iv >>> 10) + 'ퟀ');
                                 int var77 = 0;
                                 int var113 = length++;
                                 chars[var113] = c;
                                 c = (char)((it$iv & 1023) + '\udc00');
                                 var77 = 0;
                                 var113 = length++;
                                 chars[var113] = c;
                              } else {
                                 char c = '�';
                                 int var79 = 0;
                                 int var115 = length++;
                                 chars[var115] = c;
                              }

                              Unit var184 = Unit.INSTANCE;
                              var10000 = index$iv;
                              var10001 = 1;
                           } else {
                              byte b2$iv$iv = $this$processUtf16Chars$iv[index$iv + 2];
                              int $i$f$isUtf8Continuation = 0;
                              $i$f$and = 192;
                              int $i$f$and = 0;
                              if ((b2$iv$iv & $i$f$and) != 128) {
                                 char it$iv = '�';
                                 int var168 = 0;
                                 if (it$iv != '�') {
                                    char c = (char)((it$iv >>> 10) + 'ퟀ');
                                    int var80 = 0;
                                    int var116 = length++;
                                    chars[var116] = c;
                                    c = (char)((it$iv & 1023) + '\udc00');
                                    var80 = 0;
                                    var116 = length++;
                                    chars[var116] = c;
                                 } else {
                                    char c = '�';
                                    int var82 = 0;
                                    int var118 = length++;
                                    chars[var118] = c;
                                 }

                                 Unit var185 = Unit.INSTANCE;
                                 var10000 = index$iv;
                                 var10001 = 2;
                              } else {
                                 $i$f$isUtf8Continuation = $this$processUtf16Chars$iv[index$iv + 3];
                                 other$iv$iv$iv$iv = 0;
                                 $i$f$and = 192;
                                 int $i$f$and = 0;
                                 if (($i$f$isUtf8Continuation & $i$f$and) != 128) {
                                    char it$iv = '�';
                                    int var169 = 0;
                                    if (it$iv != '�') {
                                       char c = (char)((it$iv >>> 10) + 'ퟀ');
                                       int var83 = 0;
                                       int var119 = length++;
                                       chars[var119] = c;
                                       c = (char)((it$iv & 1023) + '\udc00');
                                       var83 = 0;
                                       var119 = length++;
                                       chars[var119] = c;
                                    } else {
                                       char c = '�';
                                       int var85 = 0;
                                       int var121 = length++;
                                       chars[var121] = c;
                                    }

                                    Unit var186 = Unit.INSTANCE;
                                    var10000 = index$iv;
                                    var10001 = 3;
                                 } else {
                                    $i$f$isUtf8Continuation = 3678080 ^ $i$f$isUtf8Continuation ^ b2$iv$iv << 6 ^ b1$iv$iv << 12 ^ b0$iv$iv << 18;
                                    if ($i$f$isUtf8Continuation > 1114111) {
                                       char it$iv = '�';
                                       int var170 = 0;
                                       if (it$iv != '�') {
                                          char c = (char)((it$iv >>> 10) + 'ퟀ');
                                          int var86 = 0;
                                          int var122 = length++;
                                          chars[var122] = c;
                                          c = (char)((it$iv & 1023) + '\udc00');
                                          var86 = 0;
                                          var122 = length++;
                                          chars[var122] = c;
                                       } else {
                                          char c = '�';
                                          int var88 = 0;
                                          int var124 = length++;
                                          chars[var124] = c;
                                       }

                                       Unit var187 = Unit.INSTANCE;
                                       var10000 = index$iv;
                                    } else if (55296 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 57344 : false) {
                                       char it$iv = '�';
                                       int var171 = 0;
                                       if (it$iv != '�') {
                                          char c = (char)((it$iv >>> 10) + 'ퟀ');
                                          int var89 = 0;
                                          int var125 = length++;
                                          chars[var125] = c;
                                          c = (char)((it$iv & 1023) + '\udc00');
                                          var89 = 0;
                                          var125 = length++;
                                          chars[var125] = c;
                                       } else {
                                          char c = '�';
                                          int var91 = 0;
                                          int var127 = length++;
                                          chars[var127] = c;
                                       }

                                       Unit var188 = Unit.INSTANCE;
                                       var10000 = index$iv;
                                    } else if ($i$f$isUtf8Continuation < 65536) {
                                       char it$iv = '�';
                                       int var172 = 0;
                                       if (it$iv != '�') {
                                          char c = (char)((it$iv >>> 10) + 'ퟀ');
                                          int var92 = 0;
                                          int var128 = length++;
                                          chars[var128] = c;
                                          c = (char)((it$iv & 1023) + '\udc00');
                                          var92 = 0;
                                          var128 = length++;
                                          chars[var128] = c;
                                       } else {
                                          char c = '�';
                                          int var94 = 0;
                                          int var130 = length++;
                                          chars[var130] = c;
                                       }

                                       Unit var189 = Unit.INSTANCE;
                                       var10000 = index$iv;
                                    } else {
                                       int var173 = 0;
                                       if ($i$f$isUtf8Continuation != 65533) {
                                          char c = (char)(($i$f$isUtf8Continuation >>> 10) + 'ퟀ');
                                          int var95 = 0;
                                          int var131 = length++;
                                          chars[var131] = c;
                                          c = (char)(($i$f$isUtf8Continuation & 1023) + '\udc00');
                                          var95 = 0;
                                          var131 = length++;
                                          chars[var131] = c;
                                       } else {
                                          char c = '�';
                                          int var97 = 0;
                                          int var133 = length++;
                                          chars[var133] = c;
                                       }

                                       Unit var190 = Unit.INSTANCE;
                                       var10000 = index$iv;
                                    }

                                    var10001 = 4;
                                 }
                              }
                           }
                        }

                        index$iv = var10000 + var10001;
                        Unit var234 = Unit.INSTANCE;
                     }
                  }
               }
            }
         }

         return StringsKt.concatToString(chars, 0, length);
      } else {
         throw new ArrayIndexOutOfBoundsException("size=" + $this$commonToUtf8String.length + " beginIndex=" + beginIndex + " endIndex=" + endIndex);
      }
   }

   // $FF: synthetic method
   public static String commonToUtf8String$default(byte[] var0, int var1, int var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = 0;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.length;
      }

      return commonToUtf8String(var0, var1, var2);
   }

   @NotNull
   public static final byte[] commonAsUtf8ToByteArray(@NotNull String $this$commonAsUtf8ToByteArray) {
      Intrinsics.checkNotNullParameter($this$commonAsUtf8ToByteArray, "<this>");
      byte[] bytes = new byte[4 * $this$commonAsUtf8ToByteArray.length()];
      int index = 0;

      for(int var3 = $this$commonAsUtf8ToByteArray.length(); index < var3; ++index) {
         char b0 = $this$commonAsUtf8ToByteArray.charAt(index);
         if (Intrinsics.compare(b0, 128) >= 0) {
            int size = 0;
            size = index;
            String $this$processUtf8Bytes$iv = $this$commonAsUtf8ToByteArray;
            int endIndex$iv = $this$commonAsUtf8ToByteArray.length();
            int $i$f$processUtf8Bytes = 0;
            int index$iv = index;

            while(index$iv < endIndex$iv) {
               char c$iv = $this$processUtf8Bytes$iv.charAt(index$iv);
               if (Intrinsics.compare(c$iv, 128) < 0) {
                  byte c = (byte)c$iv;
                  int c = 0;
                  int var51 = size++;
                  bytes[var51] = c;
                  ++index$iv;

                  while(index$iv < endIndex$iv && Intrinsics.compare($this$processUtf8Bytes$iv.charAt(index$iv), 128) < 0) {
                     c = (byte)$this$processUtf8Bytes$iv.charAt(index$iv++);
                     c = 0;
                     var51 = size++;
                     bytes[var51] = c;
                  }
               } else if (Intrinsics.compare(c$iv, 2048) < 0) {
                  byte c = (byte)(c$iv >> 6 | 192);
                  int c = 0;
                  int var49 = size++;
                  bytes[var49] = c;
                  c = (byte)(c$iv & 63 | 128);
                  c = 0;
                  var49 = size++;
                  bytes[var49] = c;
                  ++index$iv;
               } else if (!('\ud800' <= c$iv ? c$iv < '\ue000' : false)) {
                  byte c = (byte)(c$iv >> 12 | 224);
                  int var35 = 0;
                  int var46 = size++;
                  bytes[var46] = c;
                  c = (byte)(c$iv >> 6 & 63 | 128);
                  var35 = 0;
                  var46 = size++;
                  bytes[var46] = c;
                  c = (byte)(c$iv & 63 | 128);
                  var35 = 0;
                  var46 = size++;
                  bytes[var46] = c;
                  ++index$iv;
               } else {
                  if (Intrinsics.compare(c$iv, 56319) <= 0 && endIndex$iv > index$iv + 1) {
                     int codePoint$iv = $this$processUtf8Bytes$iv.charAt(index$iv + 1);
                     if (56320 <= codePoint$iv ? codePoint$iv < 57344 : false) {
                        codePoint$iv = (c$iv << 10) + $this$processUtf8Bytes$iv.charAt(index$iv + 1) + -56613888;
                        byte c = (byte)(codePoint$iv >> 18 | 240);
                        int var42 = 0;
                        int var53 = size++;
                        bytes[var53] = c;
                        c = (byte)(codePoint$iv >> 12 & 63 | 128);
                        var42 = 0;
                        var53 = size++;
                        bytes[var53] = c;
                        c = (byte)(codePoint$iv >> 6 & 63 | 128);
                        var42 = 0;
                        var53 = size++;
                        bytes[var53] = c;
                        c = (byte)(codePoint$iv & 63 | 128);
                        var42 = 0;
                        var53 = size++;
                        bytes[var53] = c;
                        index$iv += 2;
                        continue;
                     }
                  }

                  byte c = 63;
                  int var14 = 0;
                  int var15 = size++;
                  bytes[var15] = c;
                  ++index$iv;
               }
            }

            byte[] var10000 = Arrays.copyOf(bytes, size);
            Intrinsics.checkNotNullExpressionValue(var10000, "copyOf(...)");
            return var10000;
         }

         bytes[index] = (byte)b0;
      }

      byte[] var57 = Arrays.copyOf(bytes, $this$commonAsUtf8ToByteArray.length());
      Intrinsics.checkNotNullExpressionValue(var57, "copyOf(...)");
      return var57;
   }
}
