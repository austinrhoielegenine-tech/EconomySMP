package net.dv8tion.jda.api.utils.data.etf;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.SerializableData;

public class ExTermEncoder {
   @Nonnull
   public static ByteBuffer pack(@Nullable Object data) {
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      buffer.put((byte)-125);
      ByteBuffer packed = pack(buffer, data);
      ((Buffer)packed).flip();
      return packed;
   }

   @Nonnull
   private static ByteBuffer pack(@Nonnull ByteBuffer buffer, @Nullable Object value) {
      if (value instanceof String) {
         return packBinary(buffer, (String)value);
      } else if (value instanceof Map) {
         return packMap(buffer, (Map)value);
      } else if (value instanceof SerializableData) {
         return packMap(buffer, ((SerializableData)value).toData().toMap());
      } else if (value instanceof Collection) {
         return packList(buffer, (Collection)value);
      } else if (value instanceof DataArray) {
         return packList(buffer, ((DataArray)value).toList());
      } else if (value instanceof Byte) {
         return packSmallInt(buffer, (Byte)value);
      } else if (!(value instanceof Integer) && !(value instanceof Short)) {
         if (value instanceof Long) {
            return packLong(buffer, (Long)value);
         } else if (!(value instanceof Float) && !(value instanceof Double)) {
            if (value instanceof Boolean) {
               return packAtom(buffer, String.valueOf(value));
            } else if (value == null) {
               return packAtom(buffer, "nil");
            } else if (value instanceof long[]) {
               return packArray(buffer, (long[])value);
            } else if (value instanceof int[]) {
               return packArray(buffer, (int[])value);
            } else if (value instanceof short[]) {
               return packArray(buffer, (short[])value);
            } else if (value instanceof byte[]) {
               return packArray(buffer, (byte[])value);
            } else if (value instanceof Object[]) {
               return packList(buffer, Arrays.asList(value));
            } else {
               throw new UnsupportedOperationException("Cannot pack value of type " + value.getClass().getName());
            }
         } else {
            return packFloat(buffer, ((Number)value).doubleValue());
         }
      } else {
         return packInt(buffer, ((Number)value).intValue());
      }
   }

   @Nonnull
   private static ByteBuffer realloc(@Nonnull ByteBuffer buffer, int length) {
      if (buffer.remaining() >= length) {
         return buffer;
      } else {
         ByteBuffer allocated = ByteBuffer.allocate(buffer.position() + length << 1);
         ((Buffer)buffer).flip();
         allocated.put(buffer);
         return allocated;
      }
   }

   @Nonnull
   private static ByteBuffer packMap(@Nonnull ByteBuffer buffer, @Nonnull Map<String, Object> data) {
      buffer = realloc(buffer, data.size() + 5);
      buffer.put((byte)116);
      buffer.putInt(data.size());

      for(Map.Entry<String, Object> entry : data.entrySet()) {
         buffer = packBinary(buffer, (String)entry.getKey());
         buffer = pack(buffer, entry.getValue());
      }

      return buffer;
   }

   @Nonnull
   private static ByteBuffer packList(@Nonnull ByteBuffer buffer, @Nonnull Collection<Object> data) {
      if (data.isEmpty()) {
         return packNil(buffer);
      } else {
         buffer = realloc(buffer, data.size() + 6);
         buffer.put((byte)108);
         buffer.putInt(data.size());

         for(Object element : data) {
            buffer = pack(buffer, element);
         }

         return packNil(buffer);
      }
   }

   @Nonnull
   private static ByteBuffer packBinary(@Nonnull ByteBuffer buffer, @Nonnull String value) {
      byte[] encoded = value.getBytes(StandardCharsets.UTF_8);
      buffer = realloc(buffer, encoded.length * 4 + 5);
      buffer.put((byte)109);
      buffer.putInt(encoded.length);
      buffer.put(encoded);
      return buffer;
   }

   @Nonnull
   private static ByteBuffer packSmallInt(@Nonnull ByteBuffer buffer, byte value) {
      buffer = realloc(buffer, 2);
      buffer.put((byte)97);
      buffer.put(value);
      return buffer;
   }

   @Nonnull
   private static ByteBuffer packInt(@Nonnull ByteBuffer buffer, int value) {
      if (countBytes((long)value) <= 1 && value >= 0) {
         return packSmallInt(buffer, (byte)value);
      } else {
         buffer = realloc(buffer, 5);
         buffer.put((byte)98);
         buffer.putInt(value);
         return buffer;
      }
   }

   @Nonnull
   private static ByteBuffer packLong(@Nonnull ByteBuffer buffer, long value) {
      byte bytes = countBytes(value);
      if (bytes <= 1) {
         return packSmallInt(buffer, (byte)((int)value));
      } else if (bytes <= 4 && value >= 0L) {
         buffer = realloc(buffer, 5);
         buffer.put((byte)98);
         buffer.putInt((int)value);
         return buffer;
      } else {
         buffer = realloc(buffer, 3 + bytes);
         buffer.put((byte)110);
         buffer.put(bytes);
         buffer.put((byte)0);

         while(value > 0L) {
            buffer.put((byte)((int)value));
            value >>>= 8;
         }

         return buffer;
      }
   }

   @Nonnull
   private static ByteBuffer packFloat(@Nonnull ByteBuffer buffer, double value) {
      buffer = realloc(buffer, 9);
      buffer.put((byte)70);
      buffer.putDouble(value);
      return buffer;
   }

   @Nonnull
   private static ByteBuffer packAtom(@Nonnull ByteBuffer buffer, String value) {
      byte[] array = value.getBytes(StandardCharsets.ISO_8859_1);
      buffer = realloc(buffer, array.length + 3);
      buffer.put((byte)100);
      buffer.putShort((short)array.length);
      buffer.put(array);
      return buffer;
   }

   @Nonnull
   private static ByteBuffer packArray(@Nonnull ByteBuffer buffer, @Nonnull long[] array) {
      if (array.length == 0) {
         return packNil(buffer);
      } else {
         buffer = realloc(buffer, array.length * 8 + 6);
         buffer.put((byte)108);
         buffer.putInt(array.length);

         for(long it : array) {
            buffer = packLong(buffer, it);
         }

         return packNil(buffer);
      }
   }

   @Nonnull
   private static ByteBuffer packArray(@Nonnull ByteBuffer buffer, @Nonnull int[] array) {
      if (array.length == 0) {
         return packNil(buffer);
      } else {
         buffer = realloc(buffer, array.length * 4 + 6);
         buffer.put((byte)108);
         buffer.putInt(array.length);

         for(int it : array) {
            buffer = packInt(buffer, it);
         }

         return packNil(buffer);
      }
   }

   @Nonnull
   private static ByteBuffer packArray(@Nonnull ByteBuffer buffer, @Nonnull short[] array) {
      if (array.length == 0) {
         return packNil(buffer);
      } else {
         buffer = realloc(buffer, array.length * 2 + 6);
         buffer.put((byte)108);
         buffer.putInt(array.length);

         for(short it : array) {
            buffer = packInt(buffer, it);
         }

         return packNil(buffer);
      }
   }

   @Nonnull
   private static ByteBuffer packArray(@Nonnull ByteBuffer buffer, @Nonnull byte[] array) {
      if (array.length == 0) {
         return packNil(buffer);
      } else {
         buffer = realloc(buffer, array.length + 6);
         buffer.put((byte)108);
         buffer.putInt(array.length);

         for(byte it : array) {
            buffer = packSmallInt(buffer, it);
         }

         return packNil(buffer);
      }
   }

   @Nonnull
   private static ByteBuffer packNil(@Nonnull ByteBuffer buffer) {
      buffer = realloc(buffer, 1);
      buffer.put((byte)106);
      return buffer;
   }

   private static byte countBytes(long value) {
      int leadingZeros = Long.numberOfLeadingZeros(value);
      return (byte)((int)Math.ceil((double)(64 - leadingZeros) / (double)8.0F));
   }
}
