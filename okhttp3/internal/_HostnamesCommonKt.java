package okhttp3.internal;

import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.internal.idn.IdnaMappingTableInstanceKt;
import okhttp3.internal.idn.Punycode;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000$\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\f\u001a\n\u0010\u0002\u001a\u00020\u0003*\u00020\u0004\u001a\f\u0010\u0005\u001a\u00020\u0003*\u00020\u0004H\u0000\u001a\f\u0010\u0006\u001a\u00020\u0003*\u00020\u0004H\u0000\u001a\"\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0000\u001a0\u0010\r\u001a\u00020\u00032\u0006\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u000bH\u0000\u001a\u0010\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\bH\u0000\u001a\u0010\u0010\u0011\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\bH\u0000\u001a\u0010\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\bH\u0002\u001a\u0010\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\bH\u0000\u001a\u000e\u0010\u0014\u001a\u0004\u0018\u00010\u0004*\u00020\u0004H\u0000\u001a\u0012\u0010\u0015\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0016\u001a\u00020\u0004H\u0000\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017"},
   d2 = {"VERIFY_AS_IP_ADDRESS", "Lkotlin/text/Regex;", "canParseAsIpAddress", "", "", "containsInvalidLabelLengths", "containsInvalidHostnameAsciiCodes", "decodeIpv6", "", "input", "pos", "", "limit", "decodeIpv4Suffix", "address", "addressOffset", "inet6AddressToAscii", "canonicalizeInetAddress", "isMappedIpv4Address", "inet4AddressToAscii", "toCanonicalHost", "idnToAscii", "host", "okhttp"}
)
public final class _HostnamesCommonKt {
   @NotNull
   private static final Regex VERIFY_AS_IP_ADDRESS = new Regex("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)");

   public static final boolean canParseAsIpAddress(@NotNull String $this$canParseAsIpAddress) {
      Intrinsics.checkNotNullParameter($this$canParseAsIpAddress, "<this>");
      return VERIFY_AS_IP_ADDRESS.matches((CharSequence)$this$canParseAsIpAddress);
   }

   public static final boolean containsInvalidLabelLengths(@NotNull String $this$containsInvalidLabelLengths) {
      Intrinsics.checkNotNullParameter($this$containsInvalidLabelLengths, "<this>");
      int labelStart = $this$containsInvalidLabelLengths.length();
      if (!(1 <= labelStart ? labelStart < 254 : false)) {
         return true;
      } else {
         labelStart = 0;

         while(true) {
            int dot = StringsKt.indexOf$default((CharSequence)$this$containsInvalidLabelLengths, '.', labelStart, false, 4, (Object)null);
            int labelLength = dot == -1 ? $this$containsInvalidLabelLengths.length() - labelStart : dot - labelStart;
            if (!(1 <= labelLength ? labelLength < 64 : false)) {
               return true;
            }

            if (dot == -1 || dot == $this$containsInvalidLabelLengths.length() - 1) {
               return false;
            }

            labelStart = dot + 1;
         }
      }
   }

   public static final boolean containsInvalidHostnameAsciiCodes(@NotNull String $this$containsInvalidHostnameAsciiCodes) {
      Intrinsics.checkNotNullParameter($this$containsInvalidHostnameAsciiCodes, "<this>");
      int i = 0;

      for(int var2 = $this$containsInvalidHostnameAsciiCodes.length(); i < var2; ++i) {
         char c = $this$containsInvalidHostnameAsciiCodes.charAt(i);
         if (Intrinsics.compare(c, 31) <= 0 || Intrinsics.compare(c, 127) >= 0) {
            return true;
         }

         if (StringsKt.indexOf$default((CharSequence)" #%/:?@[\\]", c, 0, false, 6, (Object)null) != -1) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public static final byte[] decodeIpv6(@NotNull String input, int pos, int limit) {
      Intrinsics.checkNotNullParameter(input, "input");
      byte[] address = new byte[16];
      int b = 0;
      int compress = -1;
      int groupOffset = -1;

      int value;
      for(int i = pos; i < limit; address[b++] = (byte)(value & 255)) {
         if (b == address.length) {
            return null;
         }

         if (i + 2 <= limit && StringsKt.startsWith$default(input, "::", i, false, 4, (Object)null)) {
            if (compress != -1) {
               return null;
            }

            i += 2;
            b += 2;
            compress = b;
            if (i == limit) {
               break;
            }
         } else if (b != 0) {
            if (!StringsKt.startsWith$default(input, ":", i, false, 4, (Object)null)) {
               if (!StringsKt.startsWith$default(input, ".", i, false, 4, (Object)null)) {
                  return null;
               }

               if (!decodeIpv4Suffix(input, groupOffset, limit, address, b - 2)) {
                  return null;
               }

               b += 2;
               break;
            }

            ++i;
         }

         value = 0;

         for(groupOffset = i; i < limit; ++i) {
            int hexDigit = _UtilCommonKt.parseHexDigit(input.charAt(i));
            if (hexDigit == -1) {
               break;
            }

            value = (value << 4) + hexDigit;
         }

         int groupLength = i - groupOffset;
         if (groupLength == 0 || groupLength > 4) {
            return null;
         }

         address[b++] = (byte)(value >>> 8 & 255);
      }

      if (b != address.length) {
         if (compress == -1) {
            return null;
         }

         ArraysKt.copyInto(address, address, address.length - (b - compress), compress, b);
         ArraysKt.fill(address, (byte)0, compress, compress + (address.length - b));
      }

      return address;
   }

   public static final boolean decodeIpv4Suffix(@NotNull String input, int pos, int limit, @NotNull byte[] address, int addressOffset) {
      Intrinsics.checkNotNullParameter(input, "input");
      Intrinsics.checkNotNullParameter(address, "address");
      int b = addressOffset;

      int value;
      for(int i = pos; i < limit; address[b++] = (byte)value) {
         if (b == address.length) {
            return false;
         }

         if (b != addressOffset) {
            if (input.charAt(i) != '.') {
               return false;
            }

            ++i;
         }

         value = 0;

         int groupOffset;
         for(groupOffset = i; i < limit; ++i) {
            char c = input.charAt(i);
            if (Intrinsics.compare(c, 48) < 0 || Intrinsics.compare(c, 57) > 0) {
               break;
            }

            if (value == 0 && groupOffset != i) {
               return false;
            }

            value = value * 10 + c - 48;
            if (value > 255) {
               return false;
            }
         }

         int groupLength = i - groupOffset;
         if (groupLength == 0) {
            return false;
         }
      }

      return b == addressOffset + 4;
   }

   @NotNull
   public static final String inet6AddressToAscii(@NotNull byte[] address) {
      Intrinsics.checkNotNullParameter(address, "address");
      int longestRunOffset = 0;
      longestRunOffset = -1;
      int longestRunLength = 0;
      int var3 = 0;

      for(int i = 0; i < address.length; i += 2) {
         int currentRunOffset;
         for(currentRunOffset = i; i < 16 && address[i] == 0 && address[i + 1] == 0; i += 2) {
         }

         int currentRunLength = i - currentRunOffset;
         if (currentRunLength > longestRunLength && currentRunLength >= 4) {
            longestRunOffset = currentRunOffset;
            longestRunLength = currentRunLength;
         }
      }

      Buffer result = new Buffer();
      int i = 0;

      while(i < address.length) {
         if (i == longestRunOffset) {
            result.writeByte(58);
            i += longestRunLength;
            if (i == 16) {
               result.writeByte(58);
            }
         } else {
            if (i > 0) {
               result.writeByte(58);
            }

            int group = _UtilCommonKt.and((byte)address[i], 255) << 8 | _UtilCommonKt.and((byte)address[i + 1], 255);
            result.writeHexadecimalUnsignedLong((long)group);
            i += 2;
         }
      }

      return result.readUtf8();
   }

   @NotNull
   public static final byte[] canonicalizeInetAddress(@NotNull byte[] address) {
      Intrinsics.checkNotNullParameter(address, "address");
      return isMappedIpv4Address(address) ? ArraysKt.sliceArray(address, RangesKt.until(12, 16)) : address;
   }

   private static final boolean isMappedIpv4Address(byte[] address) {
      if (address.length != 16) {
         return false;
      } else {
         for(int i = 0; i < 10; ++i) {
            if (address[i] != 0) {
               return false;
            }
         }

         if (address[10] != -1) {
            return false;
         } else if (address[11] != -1) {
            return false;
         } else {
            return true;
         }
      }
   }

   @NotNull
   public static final String inet4AddressToAscii(@NotNull byte[] address) {
      Intrinsics.checkNotNullParameter(address, "address");
      if (address.length != 4) {
         String var1 = "Failed requirement.";
         throw new IllegalArgumentException(var1.toString());
      } else {
         return (new Buffer()).writeDecimalLong((long)_UtilCommonKt.and((byte)address[0], 255)).writeByte(46).writeDecimalLong((long)_UtilCommonKt.and((byte)address[1], 255)).writeByte(46).writeDecimalLong((long)_UtilCommonKt.and((byte)address[2], 255)).writeByte(46).writeDecimalLong((long)_UtilCommonKt.and((byte)address[3], 255)).readUtf8();
      }
   }

   @Nullable
   public static final String toCanonicalHost(@NotNull String $this$toCanonicalHost) {
      Intrinsics.checkNotNullParameter($this$toCanonicalHost, "<this>");
      if (!StringsKt.contains$default((CharSequence)$this$toCanonicalHost, (CharSequence)":", false, 2, (Object)null)) {
         String var5 = idnToAscii($this$toCanonicalHost);
         if (var5 == null) {
            return null;
         } else {
            String result = var5;
            if (((CharSequence)result).length() == 0) {
               return null;
            } else if (containsInvalidHostnameAsciiCodes(result)) {
               return null;
            } else {
               return containsInvalidLabelLengths(result) ? null : result;
            }
         }
      } else {
         byte[] var10000 = StringsKt.startsWith$default($this$toCanonicalHost, "[", false, 2, (Object)null) && StringsKt.endsWith$default($this$toCanonicalHost, "]", false, 2, (Object)null) ? decodeIpv6($this$toCanonicalHost, 1, $this$toCanonicalHost.length() - 1) : decodeIpv6($this$toCanonicalHost, 0, $this$toCanonicalHost.length());
         if (var10000 == null) {
            return null;
         } else {
            byte[] inetAddressByteArray = var10000;
            byte[] address = canonicalizeInetAddress(inetAddressByteArray);
            if (address.length == 16) {
               return inet6AddressToAscii(address);
            } else if (address.length == 4) {
               return inet4AddressToAscii(address);
            } else {
               throw new AssertionError("Invalid IPv6 address: '" + $this$toCanonicalHost + '\'');
            }
         }
      }
   }

   @Nullable
   public static final String idnToAscii(@NotNull String host) {
      Intrinsics.checkNotNullParameter(host, "host");
      Buffer bufferA = (new Buffer()).writeUtf8(host);
      Buffer bufferB = new Buffer();

      while(!bufferA.exhausted()) {
         int codePoint = bufferA.readUtf8CodePoint();
         if (!IdnaMappingTableInstanceKt.getIDNA_MAPPING_TABLE().map(codePoint, bufferB)) {
            return null;
         }
      }

      String normalized = _NormalizeJvmKt.normalizeNfc(bufferB.readUtf8());
      bufferA.writeUtf8(normalized);
      String var10000 = Punycode.INSTANCE.decode(bufferA.readUtf8());
      if (var10000 == null) {
         return null;
      } else {
         String decoded = var10000;
         if (!Intrinsics.areEqual((Object)decoded, (Object)_NormalizeJvmKt.normalizeNfc(decoded))) {
            return null;
         } else {
            return Punycode.INSTANCE.encode(decoded);
         }
      }
   }
}
