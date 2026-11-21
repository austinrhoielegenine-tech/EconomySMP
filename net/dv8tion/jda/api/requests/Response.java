package net.dv8tion.jda.api.requests;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.utils.IOFunction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.IOUtil;

public class Response implements Closeable {
   public static final int ERROR_CODE = -1;
   public static final String ERROR_MESSAGE = "ERROR";
   public static final IOFunction<BufferedReader, DataObject> JSON_SERIALIZE_OBJECT = DataObject::fromJson;
   public static final IOFunction<BufferedReader, DataArray> JSON_SERIALIZE_ARRAY = DataArray::fromJson;
   public final int code;
   public final String message;
   public final long retryAfter;
   private final InputStream body;
   private final okhttp3.Response rawResponse;
   private final Set<String> cfRays;
   private String fallbackString;
   private Object object;
   private boolean attemptedParsing;
   private Exception exception;

   public Response(@Nonnull Exception exception, @Nonnull Set<String> cfRays) {
      this((okhttp3.Response)null, -1, "ERROR", -1L, cfRays);
      this.exception = exception;
   }

   public Response(@Nullable okhttp3.Response response, int code, @Nonnull String message, long retryAfter, @Nonnull Set<String> cfRays) {
      this.attemptedParsing = false;
      this.rawResponse = response;
      this.code = code;
      this.message = message;
      this.exception = null;
      this.retryAfter = retryAfter;
      this.cfRays = cfRays;
      if (response == null) {
         this.body = null;
      } else {
         try {
            this.body = IOUtil.getBody(response);
         } catch (Exception e) {
            throw new IllegalStateException("An error occurred while parsing the response for a RestAction", e);
         }
      }

   }

   public Response(long retryAfter, @Nonnull Set<String> cfRays) {
      this((okhttp3.Response)null, 429, "TOO MANY REQUESTS", retryAfter, cfRays);
   }

   public Response(@Nonnull okhttp3.Response response, long retryAfter, @Nonnull Set<String> cfRays) {
      this(response, response.code(), response.message(), retryAfter, cfRays);
   }

   @Nonnull
   public DataArray getArray() {
      return (DataArray)this.get(DataArray.class, JSON_SERIALIZE_ARRAY);
   }

   @Nonnull
   public Optional<DataArray> optArray() {
      return this.<DataArray>parseBody(true, DataArray.class, JSON_SERIALIZE_ARRAY);
   }

   @Nonnull
   public DataObject getObject() {
      return (DataObject)this.get(DataObject.class, JSON_SERIALIZE_OBJECT);
   }

   @Nonnull
   public Optional<DataObject> optObject() {
      return this.<DataObject>parseBody(true, DataObject.class, JSON_SERIALIZE_OBJECT);
   }

   @Nonnull
   public String getString() {
      return (String)this.parseBody(String.class, this::readString).orElseGet(() -> this.fallbackString == null ? "N/A" : this.fallbackString);
   }

   @Nonnull
   public <T> T get(@Nonnull Class<T> clazz, @Nonnull IOFunction<BufferedReader, T> parser) {
      return (T)this.parseBody(clazz, parser).orElseThrow(IllegalStateException::new);
   }

   @Nullable
   public okhttp3.Response getRawResponse() {
      return this.rawResponse;
   }

   @Nonnull
   public Set<String> getCFRays() {
      return this.cfRays;
   }

   @Nullable
   public Exception getException() {
      return this.exception;
   }

   public boolean isError() {
      return this.code == -1;
   }

   public boolean isOk() {
      return this.code > 199 && this.code < 300;
   }

   public boolean isRateLimit() {
      return this.code == 429;
   }

   public String toString() {
      EntityString entityString = new EntityString(this.exception == null ? "HTTPResponse" : "HTTPException");
      if (this.exception == null) {
         entityString.addMetadata("code", this.code);
         if (this.object != null) {
            entityString.addMetadata("object", this.object.toString());
         }
      } else {
         entityString.addMetadata("exceptionMessage", this.exception.getMessage());
      }

      return entityString.toString();
   }

   public void close() {
      if (this.rawResponse != null) {
         this.rawResponse.close();
      }

   }

   private String readString(BufferedReader reader) {
      return (String)reader.lines().collect(Collectors.joining("\n"));
   }

   private <T> Optional<T> parseBody(Class<T> clazz, IOFunction<BufferedReader, T> parser) {
      return this.<T>parseBody(false, clazz, parser);
   }

   private <T> Optional<T> parseBody(boolean opt, Class<T> clazz, IOFunction<BufferedReader, T> parser) {
      if (this.attemptedParsing) {
         return this.object != null && clazz.isAssignableFrom(this.object.getClass()) ? Optional.of(clazz.cast(this.object)) : Optional.empty();
      } else {
         this.attemptedParsing = true;
         if (this.body != null && this.rawResponse != null && this.rawResponse.body().contentLength() != 0L) {
            BufferedReader reader = null;

            try {
               reader = new BufferedReader(new InputStreamReader(this.body, StandardCharsets.UTF_8));
               reader.mark(1024);
               T t = parser.apply(reader);
               this.object = t;
               RestActionImpl.LOG.trace((String)"Parsed response body for response on url {}\n{}", (Object)this.rawResponse.request().url(), (Object)this.object);
               return Optional.ofNullable(t);
            } catch (Exception var8) {
               try {
                  reader.reset();
                  this.fallbackString = this.readString(reader);
                  reader.close();
               } catch (IOException | NullPointerException var7) {
               }

               if (opt && var8 instanceof ParsingException) {
                  return Optional.empty();
               } else {
                  throw new IllegalStateException("An error occurred while parsing the response for a RestAction", var8);
               }
            }
         } else {
            return Optional.empty();
         }
      }
   }
}
