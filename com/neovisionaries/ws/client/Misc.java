package com.neovisionaries.ws.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Misc {
   private static final SecureRandom sRandom = new SecureRandom();

   private Misc() {
   }

   public static byte[] getBytesUTF8(String string) {
      if (string == null) {
         return null;
      } else {
         try {
            return string.getBytes("UTF-8");
         } catch (UnsupportedEncodingException var2) {
            return null;
         }
      }
   }

   public static String toStringUTF8(byte[] bytes) {
      return bytes == null ? null : toStringUTF8(bytes, 0, bytes.length);
   }

   public static String toStringUTF8(byte[] bytes, int offset, int length) {
      if (bytes == null) {
         return null;
      } else {
         try {
            return new String(bytes, offset, length, "UTF-8");
         } catch (UnsupportedEncodingException var4) {
            return null;
         } catch (IndexOutOfBoundsException var5) {
            return null;
         }
      }
   }

   public static byte[] nextBytes(byte[] buffer) {
      sRandom.nextBytes(buffer);
      return buffer;
   }

   public static byte[] nextBytes(int nBytes) {
      byte[] buffer = new byte[nBytes];
      return nextBytes(buffer);
   }

   public static String toOpcodeName(int opcode) {
      switch (opcode) {
         case 0:
            return "CONTINUATION";
         case 1:
            return "TEXT";
         case 2:
            return "BINARY";
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         default:
            if (1 <= opcode && opcode <= 7) {
               return String.format("DATA(0x%X)", opcode);
            } else {
               if (8 <= opcode && opcode <= 15) {
                  return String.format("CONTROL(0x%X)", opcode);
               }

               return String.format("0x%X", opcode);
            }
         case 8:
            return "CLOSE";
         case 9:
            return "PING";
         case 10:
            return "PONG";
      }
   }

   public static String readLine(InputStream in, String charset) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      while(true) {
         int b = in.read();
         if (b == -1) {
            if (baos.size() == 0) {
               return null;
            }
            break;
         }

         if (b == 10) {
            break;
         }

         if (b != 13) {
            baos.write(b);
         } else {
            int b2 = in.read();
            if (b2 == -1) {
               baos.write(b);
               break;
            }

            if (b2 == 10) {
               break;
            }

            baos.write(b);
            baos.write(b2);
         }
      }

      return baos.toString(charset);
   }

   public static int min(int[] values) {
      int min = Integer.MAX_VALUE;

      for(int i = 0; i < values.length; ++i) {
         if (values[i] < min) {
            min = values[i];
         }
      }

      return min;
   }

   public static int max(int[] values) {
      int max = Integer.MIN_VALUE;

      for(int i = 0; i < values.length; ++i) {
         if (max < values[i]) {
            max = values[i];
         }
      }

      return max;
   }

   public static String join(Collection<?> values, String delimiter) {
      StringBuilder builder = new StringBuilder();
      join(builder, values, delimiter);
      return builder.toString();
   }

   private static void join(StringBuilder builder, Collection<?> values, String delimiter) {
      boolean first = true;

      for(Object value : values) {
         if (first) {
            first = false;
         } else {
            builder.append(delimiter);
         }

         builder.append(value.toString());
      }

   }

   public static String extractHost(URI uri) {
      String host = uri.getHost();
      if (host != null) {
         return host;
      } else {
         host = extractHostFromAuthorityPart(uri.getRawAuthority());
         return host != null ? host : extractHostFromEntireUri(uri.toString());
      }
   }

   static String extractHostFromAuthorityPart(String authority) {
      if (authority == null) {
         return null;
      } else {
         Matcher matcher = Pattern.compile("^(.*@)?([^:]+)(:\\d+)?$").matcher(authority);
         return matcher != null && matcher.matches() ? matcher.group(2) : null;
      }
   }

   static String extractHostFromEntireUri(String uri) {
      if (uri == null) {
         return null;
      } else {
         Matcher matcher = Pattern.compile("^\\w+://([^@/]*@)?([^:/]+)(:\\d+)?(/.*)?$").matcher(uri);
         return matcher != null && matcher.matches() ? matcher.group(2) : null;
      }
   }

   public static Constructor<?> getConstructor(String className, Class<?>[] parameterTypes) {
      try {
         return Class.forName(className).getConstructor(parameterTypes);
      } catch (Exception var3) {
         return null;
      }
   }

   public static Object newInstance(Constructor<?> constructor, Object... parameters) {
      if (constructor == null) {
         return null;
      } else {
         try {
            return constructor.newInstance(parameters);
         } catch (Exception var3) {
            return null;
         }
      }
   }

   public static Method getMethod(String className, String methodName, Class<?>[] parameterTypes) {
      try {
         return Class.forName(className).getMethod(methodName, parameterTypes);
      } catch (Exception var4) {
         return null;
      }
   }

   public static Object invoke(Method method, Object object, Object... parameters) {
      if (method == null) {
         return null;
      } else {
         try {
            return method.invoke(object, parameters);
         } catch (Exception var4) {
            return null;
         }
      }
   }
}
