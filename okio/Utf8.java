package okio;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000>\n\u0000\n\u0002\u0010\t\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0005\n\u0000\n\u0002\u0010\f\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\n\u001a%\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u0007¢\u0006\u0002\b\u0006\u001a\u0011\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u0004H\u0080\b\u001a\u0011\u0010\u000f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\bH\u0080\b\u001a4\u0010\u0011\u001a\u00020\u0012*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00120\u0014H\u0080\bø\u0001\u0000\u001a4\u0010\u0015\u001a\u00020\u0012*\u00020\u00162\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00120\u0014H\u0080\bø\u0001\u0000\u001a4\u0010\u0019\u001a\u00020\u0012*\u00020\u00162\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00120\u0014H\u0080\bø\u0001\u0000\u001a4\u0010\u001b\u001a\u00020\u0004*\u00020\u00162\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00120\u0014H\u0080\bø\u0001\u0000\u001a4\u0010\u001d\u001a\u00020\u0004*\u00020\u00162\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00120\u0014H\u0080\bø\u0001\u0000\u001a4\u0010\u001f\u001a\u00020\u0004*\u00020\u00162\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00042\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00120\u0014H\u0080\bø\u0001\u0000\"\u000e\u0010\u0007\u001a\u00020\bX\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\nX\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u000b\u001a\u00020\u0004X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0017\u001a\u00020\u0004X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0018\u001a\u00020\u0004X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u001a\u001a\u00020\u0004X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u001c\u001a\u00020\u0004X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u001e\u001a\u00020\u0004X\u0080T¢\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006 "},
   d2 = {"utf8Size", "", "", "beginIndex", "", "endIndex", "size", "REPLACEMENT_BYTE", "", "REPLACEMENT_CHARACTER", "", "REPLACEMENT_CODE_POINT", "isIsoControl", "", "codePoint", "isUtf8Continuation", "byte", "processUtf8Bytes", "", "yield", "Lkotlin/Function1;", "processUtf8CodePoints", "", "HIGH_SURROGATE_HEADER", "LOG_SURROGATE_HEADER", "processUtf16Chars", "MASK_2BYTES", "process2Utf8Bytes", "MASK_3BYTES", "process3Utf8Bytes", "MASK_4BYTES", "process4Utf8Bytes", "okio"}
)
@JvmName(
   name = "Utf8"
)
@SourceDebugExtension({"SMAP\nUtf8.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Utf8.kt\nokio/Utf8\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,559:1\n397#1,9:563\n127#1:572\n406#1,20:574\n440#1,4:595\n127#1:599\n446#1,10:601\n127#1:611\n456#1,5:612\n127#1:617\n461#1,24:618\n500#1,4:643\n127#1:647\n506#1,2:649\n127#1:651\n510#1,10:652\n127#1:662\n520#1,5:663\n127#1:668\n525#1,5:669\n127#1:674\n530#1,28:675\n397#1,9:704\n127#1:713\n406#1,20:715\n440#1,4:736\n127#1:740\n446#1,10:742\n127#1:752\n456#1,5:753\n127#1:758\n461#1,24:759\n500#1,4:784\n127#1:788\n506#1,2:790\n127#1:792\n510#1,10:793\n127#1:803\n520#1,5:804\n127#1:809\n525#1,5:810\n127#1:815\n530#1,28:816\n127#1:844\n127#1:846\n127#1:848\n127#1:850\n127#1:852\n127#1:854\n127#1:856\n127#1:858\n127#1:860\n1#2:560\n73#3:561\n67#3:562\n73#3:573\n67#3:594\n73#3:600\n67#3:642\n73#3:648\n67#3:703\n73#3:714\n67#3:735\n73#3:741\n67#3:783\n73#3:789\n73#3:845\n73#3:847\n73#3:849\n73#3:851\n73#3:853\n73#3:855\n73#3:857\n73#3:859\n73#3:861\n*S KotlinDebug\n*F\n+ 1 Utf8.kt\nokio/Utf8\n*L\n228#1:563,9\n228#1:572\n228#1:574,20\n232#1:595,4\n232#1:599\n232#1:601,10\n232#1:611\n232#1:612,5\n232#1:617\n232#1:618,24\n236#1:643,4\n236#1:647\n236#1:649,2\n236#1:651\n236#1:652,10\n236#1:662\n236#1:663,5\n236#1:668\n236#1:669,5\n236#1:674\n236#1:675,28\n277#1:704,9\n277#1:713\n277#1:715,20\n281#1:736,4\n281#1:740\n281#1:742,10\n281#1:752\n281#1:753,5\n281#1:758\n281#1:759,24\n285#1:784,4\n285#1:788\n285#1:790,2\n285#1:792\n285#1:793,10\n285#1:803\n285#1:804,5\n285#1:809\n285#1:810,5\n285#1:815\n285#1:816,28\n405#1:844\n443#1:846\n455#1:848\n460#1:850\n503#1:852\n507#1:854\n519#1:856\n524#1:858\n529#1:860\n127#1:561\n226#1:562\n228#1:573\n230#1:594\n232#1:600\n234#1:642\n236#1:648\n275#1:703\n277#1:714\n279#1:735\n281#1:741\n283#1:783\n285#1:789\n405#1:845\n443#1:847\n455#1:849\n460#1:851\n503#1:853\n507#1:855\n519#1:857\n524#1:859\n529#1:861\n*E\n"})
public final class Utf8 {
   public static final byte REPLACEMENT_BYTE = 63;
   public static final char REPLACEMENT_CHARACTER = '�';
   public static final int REPLACEMENT_CODE_POINT = 65533;
   public static final int HIGH_SURROGATE_HEADER = 55232;
   public static final int LOG_SURROGATE_HEADER = 56320;
   public static final int MASK_2BYTES = 3968;
   public static final int MASK_3BYTES = -123008;
   public static final int MASK_4BYTES = 3678080;

   @JvmOverloads
   @JvmName(
      name = "size"
   )
   public static final long size(@NotNull String $this$utf8Size, int beginIndex, int endIndex) {
      Intrinsics.checkNotNullParameter($this$utf8Size, "<this>");
      if (beginIndex < 0) {
         int var13 = 0;
         String var14 = "beginIndex < 0: " + beginIndex;
         throw new IllegalArgumentException(var14.toString());
      } else if (endIndex < beginIndex) {
         int var11 = 0;
         String var12 = "endIndex < beginIndex: " + endIndex + " < " + beginIndex;
         throw new IllegalArgumentException(var12.toString());
      } else if (endIndex > $this$utf8Size.length()) {
         int var4 = 0;
         String var10 = "endIndex > string.length: " + endIndex + " > " + $this$utf8Size.length();
         throw new IllegalArgumentException(var10.toString());
      } else {
         long result = 0L;
         int i = beginIndex;

         while(i < endIndex) {
            int c = $this$utf8Size.charAt(i);
            if (c < 128) {
               ++result;
               ++i;
            } else if (c < 2048) {
               result += (long)2;
               ++i;
            } else if (c >= 55296 && c <= 57343) {
               int low = i + 1 < endIndex ? $this$utf8Size.charAt(i + 1) : 0;
               if (c <= 56319 && low >= 56320 && low <= 57343) {
                  result += (long)4;
                  i += 2;
               } else {
                  ++result;
                  ++i;
               }
            } else {
               result += (long)3;
               ++i;
            }
         }

         return result;
      }
   }

   // $FF: synthetic method
   public static long size$default(String var0, int var1, int var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = 0;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.length();
      }

      return size(var0, var1, var2);
   }

   public static final boolean isIsoControl(int codePoint) {
      int $i$f$isIsoControl = 0;
      return (0 <= codePoint ? codePoint < 32 : false) || (127 <= codePoint ? codePoint < 160 : false);
   }

   public static final boolean isUtf8Continuation(byte var0) {
      int $i$f$isUtf8Continuation = 0;
      int other$iv = 192;
      int $i$f$and = 0;
      return (var0 & other$iv) == 128;
   }

   public static final void processUtf8Bytes(@NotNull String $this$processUtf8Bytes, int beginIndex, int endIndex, @NotNull Function1<? super Byte, Unit> yield) {
      Intrinsics.checkNotNullParameter($this$processUtf8Bytes, "<this>");
      Intrinsics.checkNotNullParameter(yield, "yield");
      int $i$f$processUtf8Bytes = 0;
      int index = beginIndex;

      while(index < endIndex) {
         char c = $this$processUtf8Bytes.charAt(index);
         if (Intrinsics.compare(c, 128) < 0) {
            yield.invoke((byte)c);
            ++index;

            while(index < endIndex && Intrinsics.compare($this$processUtf8Bytes.charAt(index), 128) < 0) {
               yield.invoke((byte)$this$processUtf8Bytes.charAt(index++));
            }
         } else if (Intrinsics.compare(c, 2048) < 0) {
            yield.invoke((byte)(c >> 6 | 192));
            yield.invoke((byte)(c & 63 | 128));
            ++index;
         } else if (!('\ud800' <= c ? c < '\ue000' : false)) {
            yield.invoke((byte)(c >> 12 | 224));
            yield.invoke((byte)(c >> 6 & 63 | 128));
            yield.invoke((byte)(c & 63 | 128));
            ++index;
         } else {
            if (Intrinsics.compare(c, 56319) <= 0 && endIndex > index + 1) {
               int codePoint = $this$processUtf8Bytes.charAt(index + 1);
               if (56320 <= codePoint ? codePoint < 57344 : false) {
                  codePoint = (c << 10) + $this$processUtf8Bytes.charAt(index + 1) + -56613888;
                  yield.invoke((byte)(codePoint >> 18 | 240));
                  yield.invoke((byte)(codePoint >> 12 & 63 | 128));
                  yield.invoke((byte)(codePoint >> 6 & 63 | 128));
                  yield.invoke((byte)(codePoint & 63 | 128));
                  index += 2;
                  continue;
               }
            }

            yield.invoke((byte)63);
            ++index;
         }
      }

   }

   public static final void processUtf8CodePoints(@NotNull byte[] $this$processUtf8CodePoints, int beginIndex, int endIndex, @NotNull Function1<? super Integer, Unit> yield) {
      Intrinsics.checkNotNullParameter($this$processUtf8CodePoints, "<this>");
      Intrinsics.checkNotNullParameter(yield, "yield");
      int $i$f$processUtf8CodePoints = 0;
      int index = beginIndex;

      while(index < endIndex) {
         byte b0 = $this$processUtf8CodePoints[index];
         if (b0 >= 0) {
            yield.invoke(b0);
            ++index;

            while(index < endIndex && $this$processUtf8CodePoints[index] >= 0) {
               yield.invoke($this$processUtf8CodePoints[index++]);
            }
         } else {
            int other$iv = 5;
            int $i$f$shr = 0;
            if (b0 >> other$iv == -2) {
               int $i$f$process2Utf8Bytes = 0;
               int var112;
               byte var114;
               if (endIndex <= index + 1) {
                  char it = '�';
                  int var57 = 0;
                  yield.invoke(it);
                  Unit var107 = Unit.INSTANCE;
                  var112 = index;
                  var114 = 1;
               } else {
                  byte b0$iv = $this$processUtf8CodePoints[index];
                  byte b1$iv = $this$processUtf8CodePoints[index + 1];
                  int $i$f$isUtf8Continuation = 0;
                  int other$iv$iv$iv = 192;
                  int $i$f$and = 0;
                  if ((b1$iv & other$iv$iv$iv) != 128) {
                     char it = '�';
                     int var58 = 0;
                     yield.invoke(it);
                     Unit var108 = Unit.INSTANCE;
                     var112 = index;
                     var114 = 1;
                  } else {
                     int codePoint$iv = 3968 ^ b1$iv ^ b0$iv << 6;
                     if (codePoint$iv < 128) {
                        char it = '�';
                        int var59 = 0;
                        yield.invoke(it);
                        Unit var109 = Unit.INSTANCE;
                        var112 = index;
                     } else {
                        int var60 = 0;
                        yield.invoke(codePoint$iv);
                        Unit var110 = Unit.INSTANCE;
                        var112 = index;
                     }

                     var114 = 2;
                  }
               }

               index = var112 + var114;
            } else {
               other$iv = 4;
               $i$f$shr = 0;
               if (b0 >> other$iv == -2) {
                  int $i$f$process3Utf8Bytes = 0;
                  int var111;
                  byte var113;
                  if (endIndex <= index + 2) {
                     label150: {
                        char it = '�';
                        int var51 = 0;
                        yield.invoke(it);
                        Unit var101 = Unit.INSTANCE;
                        var111 = index;
                        if (endIndex > index + 1) {
                           byte byte$iv$iv = $this$processUtf8CodePoints[index + 1];
                           int $i$f$isUtf8Continuation = 0;
                           int other$iv$iv$iv = 192;
                           int $i$f$and = 0;
                           if ((byte$iv$iv & other$iv$iv$iv) == 128) {
                              var113 = 2;
                              break label150;
                           }
                        }

                        var113 = 1;
                     }
                  } else {
                     byte b0$iv = $this$processUtf8CodePoints[index];
                     byte b1$iv = $this$processUtf8CodePoints[index + 1];
                     int $i$f$isUtf8Continuation = 0;
                     int other$iv$iv$iv = 192;
                     int $i$f$and = 0;
                     if ((b1$iv & other$iv$iv$iv) != 128) {
                        char it = '�';
                        int var52 = 0;
                        yield.invoke(it);
                        Unit var102 = Unit.INSTANCE;
                        var111 = index;
                        var113 = 1;
                     } else {
                        byte b2$iv = $this$processUtf8CodePoints[index + 2];
                        int $i$f$isUtf8Continuation = 0;
                        $i$f$and = 192;
                        int $i$f$and = 0;
                        if ((b2$iv & $i$f$and) != 128) {
                           char it = '�';
                           int var53 = 0;
                           yield.invoke(it);
                           Unit var103 = Unit.INSTANCE;
                           var111 = index;
                           var113 = 2;
                        } else {
                           $i$f$isUtf8Continuation = -123008 ^ b2$iv ^ b1$iv << 6 ^ b0$iv << 12;
                           if ($i$f$isUtf8Continuation < 2048) {
                              char it = '�';
                              int var54 = 0;
                              yield.invoke(it);
                              Unit var104 = Unit.INSTANCE;
                              var111 = index;
                           } else if (55296 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 57344 : false) {
                              char it = '�';
                              int var55 = 0;
                              yield.invoke(it);
                              Unit var105 = Unit.INSTANCE;
                              var111 = index;
                           } else {
                              int var56 = 0;
                              yield.invoke($i$f$isUtf8Continuation);
                              Unit var106 = Unit.INSTANCE;
                              var111 = index;
                           }

                           var113 = 3;
                        }
                     }
                  }

                  index = var111 + var113;
               } else {
                  other$iv = 3;
                  $i$f$shr = 0;
                  if (b0 >> other$iv != -2) {
                     yield.invoke(65533);
                     ++index;
                  } else {
                     int $i$f$process4Utf8Bytes = 0;
                     int var10000;
                     byte var10001;
                     if (endIndex <= index + 3) {
                        label162: {
                           char it = '�';
                           int var12 = 0;
                           yield.invoke(it);
                           Unit var23 = Unit.INSTANCE;
                           var10000 = index;
                           if (endIndex > index + 1) {
                              byte byte$iv$iv = $this$processUtf8CodePoints[index + 1];
                              int $i$f$isUtf8Continuation = 0;
                              int other$iv$iv$iv = 192;
                              int $i$f$and = 0;
                              if ((byte$iv$iv & other$iv$iv$iv) == 128) {
                                 if (endIndex > index + 2) {
                                    byte$iv$iv = $this$processUtf8CodePoints[index + 2];
                                    $i$f$isUtf8Continuation = 0;
                                    other$iv$iv$iv = 192;
                                    $i$f$and = 0;
                                    if ((byte$iv$iv & other$iv$iv$iv) == 128) {
                                       var10001 = 3;
                                       break label162;
                                    }
                                 }

                                 var10001 = 2;
                                 break label162;
                              }
                           }

                           var10001 = 1;
                        }
                     } else {
                        byte b0$iv = $this$processUtf8CodePoints[index];
                        byte b1$iv = $this$processUtf8CodePoints[index + 1];
                        int $i$f$isUtf8Continuation = 0;
                        int other$iv$iv$iv = 192;
                        int $i$f$and = 0;
                        if ((b1$iv & other$iv$iv$iv) != 128) {
                           char it = '�';
                           int var44 = 0;
                           yield.invoke(it);
                           Unit var94 = Unit.INSTANCE;
                           var10000 = index;
                           var10001 = 1;
                        } else {
                           byte b2$iv = $this$processUtf8CodePoints[index + 2];
                           int $i$f$isUtf8Continuation = 0;
                           $i$f$and = 192;
                           int $i$f$and = 0;
                           if ((b2$iv & $i$f$and) != 128) {
                              char it = '�';
                              int var45 = 0;
                              yield.invoke(it);
                              Unit var95 = Unit.INSTANCE;
                              var10000 = index;
                              var10001 = 2;
                           } else {
                              $i$f$isUtf8Continuation = $this$processUtf8CodePoints[index + 3];
                              other$iv$iv$iv = 0;
                              $i$f$and = 192;
                              int $i$f$and = 0;
                              if (($i$f$isUtf8Continuation & $i$f$and) != 128) {
                                 char it = '�';
                                 int var46 = 0;
                                 yield.invoke(it);
                                 Unit var96 = Unit.INSTANCE;
                                 var10000 = index;
                                 var10001 = 3;
                              } else {
                                 $i$f$isUtf8Continuation = 3678080 ^ $i$f$isUtf8Continuation ^ b2$iv << 6 ^ b1$iv << 12 ^ b0$iv << 18;
                                 if ($i$f$isUtf8Continuation > 1114111) {
                                    char it = '�';
                                    int var47 = 0;
                                    yield.invoke(it);
                                    Unit var97 = Unit.INSTANCE;
                                    var10000 = index;
                                 } else if (55296 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 57344 : false) {
                                    char it = '�';
                                    int var48 = 0;
                                    yield.invoke(it);
                                    Unit var98 = Unit.INSTANCE;
                                    var10000 = index;
                                 } else if ($i$f$isUtf8Continuation < 65536) {
                                    char it = '�';
                                    int var49 = 0;
                                    yield.invoke(it);
                                    Unit var99 = Unit.INSTANCE;
                                    var10000 = index;
                                 } else {
                                    int var50 = 0;
                                    yield.invoke($i$f$isUtf8Continuation);
                                    Unit var100 = Unit.INSTANCE;
                                    var10000 = index;
                                 }

                                 var10001 = 4;
                              }
                           }
                        }
                     }

                     index = var10000 + var10001;
                  }
               }
            }
         }
      }

   }

   public static final void processUtf16Chars(@NotNull byte[] $this$processUtf16Chars, int beginIndex, int endIndex, @NotNull Function1<? super Character, Unit> yield) {
      Intrinsics.checkNotNullParameter($this$processUtf16Chars, "<this>");
      Intrinsics.checkNotNullParameter(yield, "yield");
      int $i$f$processUtf16Chars = 0;
      int index = beginIndex;

      while(index < endIndex) {
         byte b0 = $this$processUtf16Chars[index];
         if (b0 >= 0) {
            yield.invoke((char)b0);
            ++index;

            while(index < endIndex && $this$processUtf16Chars[index] >= 0) {
               yield.invoke((char)$this$processUtf16Chars[index++]);
            }
         } else {
            int other$iv = 5;
            int $i$f$shr = 0;
            if (b0 >> other$iv == -2) {
               int $i$f$process2Utf8Bytes = 0;
               int var112;
               byte var114;
               if (endIndex <= index + 1) {
                  char codePoint = '�';
                  int var57 = 0;
                  yield.invoke((char)codePoint);
                  Unit var107 = Unit.INSTANCE;
                  var112 = index;
                  var114 = 1;
               } else {
                  byte b0$iv = $this$processUtf16Chars[index];
                  byte b1$iv = $this$processUtf16Chars[index + 1];
                  int $i$f$isUtf8Continuation = 0;
                  int other$iv$iv$iv = 192;
                  int $i$f$and = 0;
                  if ((b1$iv & other$iv$iv$iv) != 128) {
                     char codePoint = '�';
                     int var58 = 0;
                     yield.invoke((char)codePoint);
                     Unit var108 = Unit.INSTANCE;
                     var112 = index;
                     var114 = 1;
                  } else {
                     int codePoint$iv = 3968 ^ b1$iv ^ b0$iv << 6;
                     if (codePoint$iv < 128) {
                        char codePoint = '�';
                        int var59 = 0;
                        yield.invoke((char)codePoint);
                        Unit var109 = Unit.INSTANCE;
                        var112 = index;
                     } else {
                        int var60 = 0;
                        yield.invoke((char)codePoint$iv);
                        Unit var110 = Unit.INSTANCE;
                        var112 = index;
                     }

                     var114 = 2;
                  }
               }

               index = var112 + var114;
            } else {
               other$iv = 4;
               $i$f$shr = 0;
               if (b0 >> other$iv == -2) {
                  int $i$f$process3Utf8Bytes = 0;
                  int var111;
                  byte var113;
                  if (endIndex <= index + 2) {
                     label189: {
                        char it = '�';
                        int var51 = 0;
                        yield.invoke((char)it);
                        Unit var101 = Unit.INSTANCE;
                        var111 = index;
                        if (endIndex > index + 1) {
                           byte byte$iv$iv = $this$processUtf16Chars[index + 1];
                           int $i$f$isUtf8Continuation = 0;
                           int other$iv$iv$iv = 192;
                           int $i$f$and = 0;
                           if ((byte$iv$iv & other$iv$iv$iv) == 128) {
                              var113 = 2;
                              break label189;
                           }
                        }

                        var113 = 1;
                     }
                  } else {
                     byte b0$iv = $this$processUtf16Chars[index];
                     byte b1$iv = $this$processUtf16Chars[index + 1];
                     int $i$f$isUtf8Continuation = 0;
                     int other$iv$iv$iv = 192;
                     int $i$f$and = 0;
                     if ((b1$iv & other$iv$iv$iv) != 128) {
                        char it = '�';
                        int var52 = 0;
                        yield.invoke((char)it);
                        Unit var102 = Unit.INSTANCE;
                        var111 = index;
                        var113 = 1;
                     } else {
                        byte b2$iv = $this$processUtf16Chars[index + 2];
                        int $i$f$isUtf8Continuation = 0;
                        $i$f$and = 192;
                        int $i$f$and = 0;
                        if ((b2$iv & $i$f$and) != 128) {
                           char it = '�';
                           int var53 = 0;
                           yield.invoke((char)it);
                           Unit var103 = Unit.INSTANCE;
                           var111 = index;
                           var113 = 2;
                        } else {
                           $i$f$isUtf8Continuation = -123008 ^ b2$iv ^ b1$iv << 6 ^ b0$iv << 12;
                           if ($i$f$isUtf8Continuation < 2048) {
                              char codePoint = '�';
                              int var54 = 0;
                              yield.invoke((char)codePoint);
                              Unit var104 = Unit.INSTANCE;
                              var111 = index;
                           } else if (55296 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 57344 : false) {
                              char codePoint = '�';
                              int var55 = 0;
                              yield.invoke((char)codePoint);
                              Unit var105 = Unit.INSTANCE;
                              var111 = index;
                           } else {
                              int var56 = 0;
                              yield.invoke((char)$i$f$isUtf8Continuation);
                              Unit var106 = Unit.INSTANCE;
                              var111 = index;
                           }

                           var113 = 3;
                        }
                     }
                  }

                  index = var111 + var113;
               } else {
                  other$iv = 3;
                  $i$f$shr = 0;
                  if (b0 >> other$iv != -2) {
                     yield.invoke('�');
                     ++index;
                  } else {
                     int $i$f$process4Utf8Bytes = 0;
                     int var10000;
                     byte var10001;
                     if (endIndex <= index + 3) {
                        label222: {
                           char it = '�';
                           int var12 = 0;
                           if (it != '�') {
                              yield.invoke((char)((it >>> 10) + 'ퟀ'));
                              yield.invoke((char)((it & 1023) + '\udc00'));
                           } else {
                              yield.invoke('�');
                           }

                           Unit var23 = Unit.INSTANCE;
                           var10000 = index;
                           if (endIndex > index + 1) {
                              byte byte$iv$iv = $this$processUtf16Chars[index + 1];
                              int $i$f$isUtf8Continuation = 0;
                              int other$iv$iv$iv = 192;
                              int $i$f$and = 0;
                              if ((byte$iv$iv & other$iv$iv$iv) == 128) {
                                 if (endIndex > index + 2) {
                                    byte$iv$iv = $this$processUtf16Chars[index + 2];
                                    $i$f$isUtf8Continuation = 0;
                                    other$iv$iv$iv = 192;
                                    $i$f$and = 0;
                                    if ((byte$iv$iv & other$iv$iv$iv) == 128) {
                                       var10001 = 3;
                                       break label222;
                                    }
                                 }

                                 var10001 = 2;
                                 break label222;
                              }
                           }

                           var10001 = 1;
                        }
                     } else {
                        byte b0$iv = $this$processUtf16Chars[index];
                        byte b1$iv = $this$processUtf16Chars[index + 1];
                        int $i$f$isUtf8Continuation = 0;
                        int other$iv$iv$iv = 192;
                        int $i$f$and = 0;
                        if ((b1$iv & other$iv$iv$iv) != 128) {
                           char it = '�';
                           int var44 = 0;
                           if (it != '�') {
                              yield.invoke((char)((it >>> 10) + 'ퟀ'));
                              yield.invoke((char)((it & 1023) + '\udc00'));
                           } else {
                              yield.invoke('�');
                           }

                           Unit var94 = Unit.INSTANCE;
                           var10000 = index;
                           var10001 = 1;
                        } else {
                           byte b2$iv = $this$processUtf16Chars[index + 2];
                           int $i$f$isUtf8Continuation = 0;
                           $i$f$and = 192;
                           int $i$f$and = 0;
                           if ((b2$iv & $i$f$and) != 128) {
                              char it = '�';
                              int var45 = 0;
                              if (it != '�') {
                                 yield.invoke((char)((it >>> 10) + 'ퟀ'));
                                 yield.invoke((char)((it & 1023) + '\udc00'));
                              } else {
                                 yield.invoke('�');
                              }

                              Unit var95 = Unit.INSTANCE;
                              var10000 = index;
                              var10001 = 2;
                           } else {
                              $i$f$isUtf8Continuation = $this$processUtf16Chars[index + 3];
                              other$iv$iv$iv = 0;
                              $i$f$and = 192;
                              int $i$f$and = 0;
                              if (($i$f$isUtf8Continuation & $i$f$and) != 128) {
                                 char it = '�';
                                 int var46 = 0;
                                 if (it != '�') {
                                    yield.invoke((char)((it >>> 10) + 'ퟀ'));
                                    yield.invoke((char)((it & 1023) + '\udc00'));
                                 } else {
                                    yield.invoke('�');
                                 }

                                 Unit var96 = Unit.INSTANCE;
                                 var10000 = index;
                                 var10001 = 3;
                              } else {
                                 $i$f$isUtf8Continuation = 3678080 ^ $i$f$isUtf8Continuation ^ b2$iv << 6 ^ b1$iv << 12 ^ b0$iv << 18;
                                 if ($i$f$isUtf8Continuation > 1114111) {
                                    char it = '�';
                                    int var47 = 0;
                                    if (it != '�') {
                                       yield.invoke((char)((it >>> 10) + 'ퟀ'));
                                       yield.invoke((char)((it & 1023) + '\udc00'));
                                    } else {
                                       yield.invoke('�');
                                    }

                                    Unit var97 = Unit.INSTANCE;
                                    var10000 = index;
                                 } else if (55296 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 57344 : false) {
                                    char it = '�';
                                    int var48 = 0;
                                    if (it != '�') {
                                       yield.invoke((char)((it >>> 10) + 'ퟀ'));
                                       yield.invoke((char)((it & 1023) + '\udc00'));
                                    } else {
                                       yield.invoke('�');
                                    }

                                    Unit var98 = Unit.INSTANCE;
                                    var10000 = index;
                                 } else if ($i$f$isUtf8Continuation < 65536) {
                                    char it = '�';
                                    int var49 = 0;
                                    if (it != '�') {
                                       yield.invoke((char)((it >>> 10) + 'ퟀ'));
                                       yield.invoke((char)((it & 1023) + '\udc00'));
                                    } else {
                                       yield.invoke('�');
                                    }

                                    Unit var99 = Unit.INSTANCE;
                                    var10000 = index;
                                 } else {
                                    int var50 = 0;
                                    if ($i$f$isUtf8Continuation != 65533) {
                                       yield.invoke((char)(($i$f$isUtf8Continuation >>> 10) + 'ퟀ'));
                                       yield.invoke((char)(($i$f$isUtf8Continuation & 1023) + '\udc00'));
                                    } else {
                                       yield.invoke('�');
                                    }

                                    Unit var100 = Unit.INSTANCE;
                                    var10000 = index;
                                 }

                                 var10001 = 4;
                              }
                           }
                        }
                     }

                     index = var10000 + var10001;
                  }
               }
            }
         }
      }

   }

   public static final int process2Utf8Bytes(@NotNull byte[] $this$process2Utf8Bytes, int beginIndex, int endIndex, @NotNull Function1<? super Integer, Unit> yield) {
      Intrinsics.checkNotNullParameter($this$process2Utf8Bytes, "<this>");
      Intrinsics.checkNotNullParameter(yield, "yield");
      int $i$f$process2Utf8Bytes = 0;
      if (endIndex <= beginIndex + 1) {
         yield.invoke(65533);
         return 1;
      } else {
         byte b0 = $this$process2Utf8Bytes[beginIndex];
         byte b1 = $this$process2Utf8Bytes[beginIndex + 1];
         int $i$f$isUtf8Continuation = 0;
         int other$iv$iv = 192;
         int $i$f$and = 0;
         if ((b1 & other$iv$iv) != 128) {
            yield.invoke(65533);
            return 1;
         } else {
            int codePoint = 3968 ^ b1 ^ b0 << 6;
            if (codePoint < 128) {
               yield.invoke(65533);
            } else {
               yield.invoke(codePoint);
            }

            return 2;
         }
      }
   }

   public static final int process3Utf8Bytes(@NotNull byte[] $this$process3Utf8Bytes, int beginIndex, int endIndex, @NotNull Function1<? super Integer, Unit> yield) {
      Intrinsics.checkNotNullParameter($this$process3Utf8Bytes, "<this>");
      Intrinsics.checkNotNullParameter(yield, "yield");
      int $i$f$process3Utf8Bytes = 0;
      if (endIndex <= beginIndex + 2) {
         yield.invoke(65533);
         if (endIndex > beginIndex + 1) {
            byte byte$iv = $this$process3Utf8Bytes[beginIndex + 1];
            int $i$f$isUtf8Continuation = 0;
            int other$iv$iv = 192;
            int $i$f$and = 0;
            if ((byte$iv & other$iv$iv) == 128) {
               return 2;
            }
         }

         return 1;
      } else {
         byte b0 = $this$process3Utf8Bytes[beginIndex];
         byte b1 = $this$process3Utf8Bytes[beginIndex + 1];
         int $i$f$isUtf8Continuation = 0;
         int other$iv$iv = 192;
         int $i$f$and = 0;
         if ((b1 & other$iv$iv) != 128) {
            yield.invoke(65533);
            return 1;
         } else {
            byte b2 = $this$process3Utf8Bytes[beginIndex + 2];
            int $i$f$isUtf8Continuation = 0;
            $i$f$and = 192;
            int $i$f$and = 0;
            if ((b2 & $i$f$and) != 128) {
               yield.invoke(65533);
               return 2;
            } else {
               $i$f$isUtf8Continuation = -123008 ^ b2 ^ b1 << 6 ^ b0 << 12;
               if ($i$f$isUtf8Continuation < 2048) {
                  yield.invoke(65533);
               } else if (55296 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 57344 : false) {
                  yield.invoke(65533);
               } else {
                  yield.invoke($i$f$isUtf8Continuation);
               }

               return 3;
            }
         }
      }
   }

   public static final int process4Utf8Bytes(@NotNull byte[] $this$process4Utf8Bytes, int beginIndex, int endIndex, @NotNull Function1<? super Integer, Unit> yield) {
      Intrinsics.checkNotNullParameter($this$process4Utf8Bytes, "<this>");
      Intrinsics.checkNotNullParameter(yield, "yield");
      int $i$f$process4Utf8Bytes = 0;
      if (endIndex <= beginIndex + 3) {
         yield.invoke(65533);
         if (endIndex > beginIndex + 1) {
            byte byte$iv = $this$process4Utf8Bytes[beginIndex + 1];
            int $i$f$isUtf8Continuation = 0;
            int other$iv$iv = 192;
            int $i$f$and = 0;
            if ((byte$iv & other$iv$iv) == 128) {
               if (endIndex > beginIndex + 2) {
                  byte$iv = $this$process4Utf8Bytes[beginIndex + 2];
                  $i$f$isUtf8Continuation = 0;
                  other$iv$iv = 192;
                  $i$f$and = 0;
                  if ((byte$iv & other$iv$iv) == 128) {
                     return 3;
                  }
               }

               return 2;
            }
         }

         return 1;
      } else {
         byte b0 = $this$process4Utf8Bytes[beginIndex];
         byte b1 = $this$process4Utf8Bytes[beginIndex + 1];
         int $i$f$isUtf8Continuation = 0;
         int other$iv$iv = 192;
         int $i$f$and = 0;
         if ((b1 & other$iv$iv) != 128) {
            yield.invoke(65533);
            return 1;
         } else {
            byte b2 = $this$process4Utf8Bytes[beginIndex + 2];
            int $i$f$isUtf8Continuation = 0;
            $i$f$and = 192;
            int $i$f$and = 0;
            if ((b2 & $i$f$and) != 128) {
               yield.invoke(65533);
               return 2;
            } else {
               $i$f$isUtf8Continuation = $this$process4Utf8Bytes[beginIndex + 3];
               other$iv$iv = 0;
               $i$f$and = 192;
               int $i$f$and = 0;
               if (($i$f$isUtf8Continuation & $i$f$and) != 128) {
                  yield.invoke(65533);
                  return 3;
               } else {
                  $i$f$isUtf8Continuation = 3678080 ^ $i$f$isUtf8Continuation ^ b2 << 6 ^ b1 << 12 ^ b0 << 18;
                  if ($i$f$isUtf8Continuation > 1114111) {
                     yield.invoke(65533);
                  } else if (55296 <= $i$f$isUtf8Continuation ? $i$f$isUtf8Continuation < 57344 : false) {
                     yield.invoke(65533);
                  } else if ($i$f$isUtf8Continuation < 65536) {
                     yield.invoke(65533);
                  } else {
                     yield.invoke($i$f$isUtf8Continuation);
                  }

                  return 4;
               }
            }
         }
      }
   }

   @JvmOverloads
   @JvmName(
      name = "size"
   )
   public static final long size(@NotNull String $this$utf8Size, int beginIndex) {
      Intrinsics.checkNotNullParameter($this$utf8Size, "<this>");
      return size$default($this$utf8Size, beginIndex, 0, 2, (Object)null);
   }

   @JvmOverloads
   @JvmName(
      name = "size"
   )
   public static final long size(@NotNull String $this$utf8Size) {
      Intrinsics.checkNotNullParameter($this$utf8Size, "<this>");
      return size$default($this$utf8Size, 0, 0, 3, (Object)null);
   }
}
