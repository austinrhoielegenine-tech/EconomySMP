package okhttp3.internal.http;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.Challenge;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.internal._UtilCommonKt;
import okhttp3.internal._UtilJvmKt;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 2, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000T\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0005\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0018\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004*\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b\u001a\u001a\u0010\t\u001a\u00020\n*\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\rH\u0002\u001a\f\u0010\u000e\u001a\u00020\u000f*\u00020\u000bH\u0002\u001a\u0014\u0010\u0010\u001a\u00020\u000f*\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002\u001a\u000e\u0010\u0013\u001a\u0004\u0018\u00010\b*\u00020\u000bH\u0002\u001a\u000e\u0010\u0014\u001a\u0004\u0018\u00010\b*\u00020\u000bH\u0002\u001a\u001a\u0010\u0015\u001a\u00020\n*\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u0006\u001a\n\u0010\u001a\u001a\u00020\u000f*\u00020\u001b\u001a\u0010\u0010\u001c\u001a\u00020\u000f2\u0006\u0010\u001d\u001a\u00020\u001bH\u0007\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"},
   d2 = {"QUOTED_STRING_DELIMITERS", "Lokio/ByteString;", "TOKEN_DELIMITERS", "parseChallenges", "", "Lokhttp3/Challenge;", "Lokhttp3/Headers;", "headerName", "", "readChallengeHeader", "", "Lokio/Buffer;", "result", "", "skipCommasAndWhitespace", "", "startsWith", "prefix", "", "readQuotedString", "readToken", "receiveHeaders", "Lokhttp3/CookieJar;", "url", "Lokhttp3/HttpUrl;", "headers", "promisesBody", "Lokhttp3/Response;", "hasBody", "response", "okhttp"}
)
@JvmName(
   name = "HttpHeaders"
)
public final class HttpHeaders {
   @NotNull
   private static final ByteString QUOTED_STRING_DELIMITERS;
   @NotNull
   private static final ByteString TOKEN_DELIMITERS;

   @NotNull
   public static final List<Challenge> parseChallenges(@NotNull Headers $this$parseChallenges, @NotNull String headerName) {
      Intrinsics.checkNotNullParameter($this$parseChallenges, "<this>");
      Intrinsics.checkNotNullParameter(headerName, "headerName");
      List result = (List)(new ArrayList());
      int h = 0;

      for(int var4 = $this$parseChallenges.size(); h < var4; ++h) {
         if (StringsKt.equals(headerName, $this$parseChallenges.name(h), true)) {
            Buffer header = (new Buffer()).writeUtf8($this$parseChallenges.value(h));

            try {
               readChallengeHeader(header, result);
            } catch (EOFException e) {
               Platform.Companion.get().log("Unable to parse challenge", 5, (Throwable)e);
            }
         }
      }

      return result;
   }

   private static final void readChallengeHeader(Buffer $this$readChallengeHeader, List<Challenge> result) throws EOFException {
      String peek = null;

      while(true) {
         if (peek == null) {
            skipCommasAndWhitespace($this$readChallengeHeader);
            peek = readToken($this$readChallengeHeader);
            if (peek == null) {
               return;
            }
         }

         String schemeName = peek;
         boolean commaPrefixed = skipCommasAndWhitespace($this$readChallengeHeader);
         peek = readToken($this$readChallengeHeader);
         if (peek == null) {
            if (!$this$readChallengeHeader.exhausted()) {
               return;
            }

            result.add(new Challenge(schemeName, MapsKt.emptyMap()));
            return;
         }

         int eqCount = _UtilCommonKt.skipAll($this$readChallengeHeader, (byte)61);
         boolean commaSuffixed = skipCommasAndWhitespace($this$readChallengeHeader);
         if (!commaPrefixed && (commaSuffixed || $this$readChallengeHeader.exhausted())) {
            Map var10004 = Collections.singletonMap((Object)null, peek + StringsKt.repeat((CharSequence)"=", eqCount));
            Intrinsics.checkNotNullExpressionValue(var10004, "singletonMap(...)");
            result.add(new Challenge(schemeName, var10004));
            peek = null;
         } else {
            Map parameters = (Map)(new LinkedHashMap());
            eqCount += _UtilCommonKt.skipAll($this$readChallengeHeader, (byte)61);

            while(true) {
               if (peek == null) {
                  peek = readToken($this$readChallengeHeader);
                  if (skipCommasAndWhitespace($this$readChallengeHeader)) {
                     break;
                  }

                  eqCount = _UtilCommonKt.skipAll($this$readChallengeHeader, (byte)61);
               }

               if (eqCount == 0) {
                  break;
               }

               if (eqCount > 1) {
                  return;
               }

               if (skipCommasAndWhitespace($this$readChallengeHeader)) {
                  return;
               }

               String var10000 = startsWith($this$readChallengeHeader, (byte)34) ? readQuotedString($this$readChallengeHeader) : readToken($this$readChallengeHeader);
               if (var10000 == null) {
                  return;
               }

               String parameterValue = var10000;
               String replaced = (String)parameters.put(peek, parameterValue);
               peek = null;
               if (replaced != null) {
                  return;
               }

               if (!skipCommasAndWhitespace($this$readChallengeHeader) && !$this$readChallengeHeader.exhausted()) {
                  return;
               }
            }

            result.add(new Challenge(schemeName, parameters));
         }
      }
   }

   private static final boolean skipCommasAndWhitespace(Buffer $this$skipCommasAndWhitespace) {
      boolean commaFound = false;

      while(!$this$skipCommasAndWhitespace.exhausted()) {
         byte var2 = $this$skipCommasAndWhitespace.getByte(0L);
         if (var2 == 44) {
            $this$skipCommasAndWhitespace.readByte();
            commaFound = true;
         } else {
            if (var2 != 32 && var2 != 9) {
               break;
            }

            $this$skipCommasAndWhitespace.readByte();
         }
      }

      return commaFound;
   }

   private static final boolean startsWith(Buffer $this$startsWith, byte prefix) {
      return !$this$startsWith.exhausted() && $this$startsWith.getByte(0L) == prefix;
   }

   private static final String readQuotedString(Buffer $this$readQuotedString) throws EOFException {
      if ($this$readQuotedString.readByte() != 34) {
         String var4 = "Failed requirement.";
         throw new IllegalArgumentException(var4.toString());
      } else {
         Buffer result = new Buffer();

         while(true) {
            long i = $this$readQuotedString.indexOfElement(QUOTED_STRING_DELIMITERS);
            if (i == -1L) {
               return null;
            }

            if ($this$readQuotedString.getByte(i) == 34) {
               result.write($this$readQuotedString, i);
               $this$readQuotedString.readByte();
               return result.readUtf8();
            }

            if ($this$readQuotedString.size() == i + 1L) {
               return null;
            }

            result.write($this$readQuotedString, i);
            $this$readQuotedString.readByte();
            result.write($this$readQuotedString, 1L);
         }
      }
   }

   private static final String readToken(Buffer $this$readToken) {
      long tokenSize = $this$readToken.indexOfElement(TOKEN_DELIMITERS);
      if (tokenSize == -1L) {
         tokenSize = $this$readToken.size();
      }

      return tokenSize != 0L ? $this$readToken.readUtf8(tokenSize) : null;
   }

   public static final void receiveHeaders(@NotNull CookieJar $this$receiveHeaders, @NotNull HttpUrl url, @NotNull Headers headers) {
      Intrinsics.checkNotNullParameter($this$receiveHeaders, "<this>");
      Intrinsics.checkNotNullParameter(url, "url");
      Intrinsics.checkNotNullParameter(headers, "headers");
      if ($this$receiveHeaders != CookieJar.NO_COOKIES) {
         List cookies = Cookie.Companion.parseAll(url, headers);
         if (!cookies.isEmpty()) {
            $this$receiveHeaders.saveFromResponse(url, cookies);
         }
      }
   }

   public static final boolean promisesBody(@NotNull Response $this$promisesBody) {
      Intrinsics.checkNotNullParameter($this$promisesBody, "<this>");
      if (Intrinsics.areEqual((Object)$this$promisesBody.request().method(), (Object)"HEAD")) {
         return false;
      } else {
         int responseCode = $this$promisesBody.code();
         if ((responseCode < 100 || responseCode >= 200) && responseCode != 204 && responseCode != 304) {
            return true;
         } else {
            return _UtilJvmKt.headersContentLength($this$promisesBody) != -1L || StringsKt.equals("chunked", Response.header$default($this$promisesBody, "Transfer-Encoding", (String)null, 2, (Object)null), true);
         }
      }
   }

   /** @deprecated */
   @Deprecated(
      message = "No longer supported",
      replaceWith = @ReplaceWith(
   expression = "response.promisesBody()",
   imports = {}
),
      level = DeprecationLevel.ERROR
   )
   public static final boolean hasBody(@NotNull Response response) {
      Intrinsics.checkNotNullParameter(response, "response");
      return promisesBody(response);
   }

   static {
      QUOTED_STRING_DELIMITERS = ByteString.Companion.encodeUtf8("\"\\");
      TOKEN_DELIMITERS = ByteString.Companion.encodeUtf8("\t ,=");
   }
}
