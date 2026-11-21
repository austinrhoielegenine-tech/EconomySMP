package net.dv8tion.jda.internal.utils;

import com.neovisionaries.ws.client.WebSocketFactory;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.utils.requestbody.BufferedRequestBody;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.Okio;
import org.slf4j.Logger;

public class IOUtil {
   private static final Logger log = JDALogger.getLog(IOUtil.class);

   public static void silentClose(AutoCloseable closeable) {
      try {
         closeable.close();
      } catch (Exception var2) {
      }

   }

   public static void silentClose(Closeable closeable) {
      try {
         closeable.close();
      } catch (IOException var2) {
      }

   }

   public static String addQuery(String base, Object... params) {
      try {
         StringBuilder builder = new StringBuilder(base);
         if ((new URI(base)).getQuery() == null) {
            builder.append('?');
         } else {
            builder.append('&');
         }

         for(int i = 0; i < params.length; i += 2) {
            builder.append(params[i]).append('=').append(URLEncoder.encode(params[i + 1].toString(), "UTF-8")).append('&');
         }

         builder.setLength(builder.length() - 1);
         return builder.toString();
      } catch (UnsupportedEncodingException | URISyntaxException e) {
         throw new IllegalArgumentException(e);
      }
   }

   public static String getHost(String uri) {
      return URI.create(uri).getHost();
   }

   public static void setServerName(WebSocketFactory factory, String url) {
      String host = getHost(url);
      if (host != null) {
         factory.setServerName(host);
      }

   }

   public static OkHttpClient.Builder newHttpClientBuilder() {
      Dispatcher dispatcher = new Dispatcher();
      dispatcher.setMaxRequestsPerHost(25);
      ConnectionPool connectionPool = new ConnectionPool(5, 10L, TimeUnit.SECONDS);
      return (new OkHttpClient.Builder()).connectionPool(connectionPool).dispatcher(dispatcher);
   }

   public static byte[] readFully(File file) throws IOException {
      Checks.notNull(file, "File");
      Checks.check(file.exists(), "Provided file does not exist!");
      InputStream is = new FileInputStream(file);

      byte[] var7;
      try {
         long length = file.length();
         if (length > 2147483647L) {
            throw new IOException("Cannot read the file into memory completely due to it being too large!");
         }

         byte[] bytes = new byte[(int)length];
         int offset = 0;

         int numRead;
         for(numRead = 0; offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {
         }

         if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
         }

         is.close();
         var7 = bytes;
      } catch (Throwable var9) {
         try {
            is.close();
         } catch (Throwable var8) {
            var9.addSuppressed(var8);
         }

         throw var9;
      }

      is.close();
      return var7;
   }

   public static byte[] readFully(InputStream stream) throws IOException {
      Checks.notNull(stream, "InputStream");
      byte[] buffer = new byte[1024];
      ByteArrayOutputStream bos = new ByteArrayOutputStream();

      byte[] var4;
      try {
         int readAmount = 0;

         while((readAmount = stream.read(buffer)) != -1) {
            bos.write(buffer, 0, readAmount);
         }

         var4 = bos.toByteArray();
      } catch (Throwable var6) {
         try {
            bos.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      bos.close();
      return var4;
   }

   public static BufferedRequestBody createRequestBody(MediaType contentType, InputStream stream) {
      return new BufferedRequestBody(Okio.source(stream), contentType);
   }

   public static short getShortBigEndian(byte[] arr, int offset) {
      return (short)((arr[offset] & 255) << 8 | arr[offset + 1] & 255);
   }

   public static short getShortLittleEndian(byte[] arr, int offset) {
      return (short)(arr[offset] & 255 | (arr[offset + 1] & 255) << 8);
   }

   public static int getIntBigEndian(byte[] arr, int offset) {
      return arr[offset + 3] & 255 | (arr[offset + 2] & 255) << 8 | (arr[offset + 1] & 255) << 16 | (arr[offset] & 255) << 24;
   }

   public static void setIntBigEndian(byte[] arr, int offset, int it) {
      arr[offset] = (byte)(it >>> 24 & 255);
      arr[offset + 1] = (byte)(it >>> 16 & 255);
      arr[offset + 2] = (byte)(it >>> 8 & 255);
      arr[offset + 3] = (byte)(it & 255);
   }

   @Nonnull
   @CheckReturnValue
   public static ByteBuffer allocateLike(@Nonnull ByteBuffer original, int length) {
      return original.isDirect() ? ByteBuffer.allocateDirect(length) : ByteBuffer.allocate(length);
   }

   @Nonnull
   @CheckReturnValue
   public static ByteBuffer reallocate(@Nonnull ByteBuffer original, int length) {
      ByteBuffer buffer = allocateLike(original, length);
      buffer.put(original);
      return buffer;
   }

   @Nonnull
   @CheckReturnValue
   public static ByteBuffer replace(@Nonnull ByteBuffer destination, @Nonnull ByteBuffer source) {
      if (destination.capacity() < source.remaining()) {
         destination = allocateLike(destination, (int)((double)1.25F * (double)source.remaining()));
      }

      destination.clear();
      destination.put(source);
      destination.flip();
      return destination;
   }

   public static InputStream getBody(Response response) throws IOException {
      String encoding = response.header("content-encoding", "");
      InputStream data = new BufferedInputStream(response.body().byteStream());
      data.mark(256);

      try {
         if (encoding.equalsIgnoreCase("gzip")) {
            return new GZIPInputStream(data);
         } else {
            return (InputStream)(encoding.equalsIgnoreCase("deflate") ? new InflaterInputStream(data, new Inflater(true)) : data);
         }
      } catch (EOFException | ZipException ex) {
         data.reset();
         log.error("Failed to read gzip content for response. Headers: {}\nContent: '{}'", response.headers(), JDALogger.getLazyString(() -> new String(readFully(data), StandardCharsets.UTF_8)), ex);
         return null;
      }
   }
}
