package net.dv8tion.jda.api.utils.data.etf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.InflaterOutputStream;
import javax.annotation.Nonnull;

public class ExTermDecoder {
   @Nonnull
   public static Object unpack(@Nonnull ByteBuffer buffer) {
      if (buffer.get() != -125) {
         throw new IllegalArgumentException("Failed header check");
      } else {
         return unpack0(buffer);
      }
   }

   @Nonnull
   public static Map<String, Object> unpackMap(@Nonnull ByteBuffer buffer) {
      byte tag = buffer.get(1);
      if (tag != 116) {
         throw new IllegalArgumentException("Cannot unpack map from tag " + tag);
      } else {
         return (Map)unpack(buffer);
      }
   }

   @Nonnull
   public static List<Object> unpackList(@Nonnull ByteBuffer buffer) {
      byte tag = buffer.get(1);
      if (tag != 108) {
         throw new IllegalArgumentException("Cannot unpack list from tag " + tag);
      } else {
         return (List)unpack(buffer);
      }
   }

   private static Object unpack0(@Nonnull ByteBuffer buffer) {
      int tag = buffer.get();
      switch (tag) {
         case 70:
            return unpackFloat(buffer);
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 93:
         case 94:
         case 95:
         case 96:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 111:
         case 112:
         case 113:
         case 114:
         case 117:
         default:
            throw new IllegalArgumentException("Unknown tag " + tag);
         case 80:
            return unpackCompressed(buffer);
         case 97:
            return unpackSmallInt(buffer);
         case 98:
            return unpackInt(buffer);
         case 99:
            return unpackOldFloat(buffer);
         case 100:
            return unpackAtom(buffer, StandardCharsets.ISO_8859_1);
         case 106:
            return Collections.emptyList();
         case 107:
            return unpackString(buffer);
         case 108:
            return unpackList0(buffer);
         case 109:
            return unpackBinary(buffer);
         case 110:
            return unpackSmallBigint(buffer);
         case 115:
            return unpackSmallAtom(buffer, StandardCharsets.ISO_8859_1);
         case 116:
            return unpackMap0(buffer);
         case 118:
            return unpackAtom(buffer, StandardCharsets.UTF_8);
         case 119:
            return unpackSmallAtom(buffer, StandardCharsets.UTF_8);
      }
   }

   private static Object unpackCompressed(@Nonnull ByteBuffer buffer) {
      int size = buffer.getInt();
      ByteArrayOutputStream decompressed = new ByteArrayOutputStream(size);

      try {
         InflaterOutputStream inflater = new InflaterOutputStream(decompressed);

         try {
            inflater.write(buffer.array(), buffer.position(), buffer.remaining());
         } catch (Throwable var7) {
            try {
               inflater.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }

            throw var7;
         }

         inflater.close();
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }

      buffer = ByteBuffer.wrap(decompressed.toByteArray());
      return unpack0(buffer);
   }

   private static double unpackOldFloat(@Nonnull ByteBuffer buffer) {
      String bytes = getString(buffer, StandardCharsets.ISO_8859_1, 31);
      return Double.parseDouble(bytes);
   }

   private static double unpackFloat(@Nonnull ByteBuffer buffer) {
      return buffer.getDouble();
   }

   private static long unpackSmallBigint(@Nonnull ByteBuffer buffer) {
      int arity = Byte.toUnsignedInt(buffer.get());
      int sign = Byte.toUnsignedInt(buffer.get());
      long sum = 0L;

      for(long offset = 0L; arity-- > 0; offset += 8L) {
         sum += Byte.toUnsignedLong(buffer.get()) << (int)offset;
      }

      return sign == 0 ? sum : -sum;
   }

   private static int unpackSmallInt(@Nonnull ByteBuffer buffer) {
      return Byte.toUnsignedInt(buffer.get());
   }

   private static int unpackInt(@Nonnull ByteBuffer buffer) {
      return buffer.getInt();
   }

   private static List<Object> unpackString(@Nonnull ByteBuffer buffer) {
      int length = Short.toUnsignedInt(buffer.getShort());
      List<Object> bytes = new ArrayList(length);

      while(length-- > 0) {
         bytes.add(buffer.get());
      }

      return bytes;
   }

   private static String unpackBinary(@Nonnull ByteBuffer buffer) {
      int length = buffer.getInt();
      return getString(buffer, StandardCharsets.UTF_8, length);
   }

   private static Object unpackSmallAtom(@Nonnull ByteBuffer buffer, @Nonnull Charset charset) {
      int length = Byte.toUnsignedInt(buffer.get());
      return unpackAtom(buffer, charset, length);
   }

   private static Object unpackAtom(@Nonnull ByteBuffer buffer, @Nonnull Charset charset) {
      int length = Short.toUnsignedInt(buffer.getShort());
      return unpackAtom(buffer, charset, length);
   }

   private static Object unpackAtom(@Nonnull ByteBuffer buffer, @Nonnull Charset charset, int length) {
      switch (getString(buffer, charset, length)) {
         case "true":
            return true;
         case "false":
            return false;
         case "nil":
            return null;
         default:
            return value.intern();
      }
   }

   private static String getString(@Nonnull ByteBuffer buffer, @Nonnull Charset charset, int length) {
      byte[] array = new byte[length];
      buffer.get(array);
      return new String(array, charset);
   }

   private static List<Object> unpackList0(@Nonnull ByteBuffer buffer) {
      int length = buffer.getInt();
      List<Object> list = new ArrayList(length);

      while(length-- > 0) {
         list.add(unpack0(buffer));
      }

      Object tail = unpack0(buffer);
      if (tail != Collections.emptyList()) {
         throw new IllegalArgumentException("Unexpected tail " + tail);
      } else {
         return list;
      }
   }

   private static Map<String, Object> unpackMap0(@Nonnull ByteBuffer buffer) {
      Map<String, Object> map = new HashMap();
      int arity = buffer.getInt();

      while(arity-- > 0) {
         Object rawKey = unpack0(buffer);
         String key = String.valueOf(rawKey);
         Object value = unpack0(buffer);
         map.put(key, value);
      }

      return map;
   }
}
