package net.dv8tion.jda.api.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Widget;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.requests.RestConfig;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.WidgetImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.IOUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WidgetUtil {
   public static final String WIDGET_PNG = "https://discord.com/api/v10/guilds/%s/widget.png?style=%s";
   public static final String WIDGET_URL = "https://discord.com/api/v10/guilds/%s/widget.json";
   public static final String WIDGET_HTML = "<iframe src=\"https://discord.com/widget?id=%s&theme=%s\" width=\"%d\" height=\"%d\" allowtransparency=\"true\" frameborder=\"0\"></iframe>";

   @Nonnull
   public static String getWidgetBanner(@Nonnull Guild guild, @Nonnull BannerType type) {
      Checks.notNull(guild, "Guild");
      return getWidgetBanner(guild.getId(), type);
   }

   @Nonnull
   public static String getWidgetBanner(@Nonnull String guildId, @Nonnull BannerType type) {
      Checks.notNull(guildId, "GuildId");
      Checks.notNull(type, "BannerType");
      return String.format("https://discord.com/api/v10/guilds/%s/widget.png?style=%s", guildId, type.name().toLowerCase(Locale.ROOT));
   }

   @Nonnull
   public static String getPremadeWidgetHtml(@Nonnull Guild guild, @Nonnull WidgetTheme theme, int width, int height) {
      Checks.notNull(guild, "Guild");
      return getPremadeWidgetHtml(guild.getId(), theme, width, height);
   }

   @Nonnull
   public static String getPremadeWidgetHtml(@Nonnull String guildId, @Nonnull WidgetTheme theme, int width, int height) {
      Checks.notNull(guildId, "GuildId");
      Checks.notNull(theme, "WidgetTheme");
      Checks.notNegative(width, "Width");
      Checks.notNegative(height, "Height");
      return Helpers.format("<iframe src=\"https://discord.com/widget?id=%s&theme=%s\" width=\"%d\" height=\"%d\" allowtransparency=\"true\" frameborder=\"0\"></iframe>", guildId, theme.name().toLowerCase(Locale.ROOT), width, height);
   }

   @Nullable
   public static Widget getWidget(@Nonnull String guildId) throws RateLimitedException {
      return getWidget(MiscUtil.parseSnowflake(guildId));
   }

   @Nullable
   public static Widget getWidget(long guildId) throws RateLimitedException {
      Checks.notNull(guildId, "GuildId");
      OkHttpClient client = (new OkHttpClient.Builder()).build();
      Request request = (new Request.Builder()).url(String.format("https://discord.com/api/v10/guilds/%s/widget.json", guildId)).method("GET", (RequestBody)null).header("user-agent", RestConfig.USER_AGENT).header("accept-encoding", "gzip").build();

      try {
         Response response = client.newCall(request).execute();

         Widget retryAfter;
         label110: {
            label111: {
               WidgetImpl var8;
               try {
                  int code = response.code();
                  InputStream data = IOUtil.getBody(response);
                  switch (code) {
                     case 200:
                        try {
                           InputStream stream = data;

                           try {
                              var8 = new WidgetImpl(DataObject.fromJson(stream));
                           } catch (Throwable var15) {
                              if (data != null) {
                                 try {
                                    stream.close();
                                 } catch (Throwable var13) {
                                    var15.addSuppressed(var13);
                                 }
                              }

                              throw var15;
                           }

                           if (data != null) {
                              data.close();
                           }
                           break;
                        } catch (IOException e) {
                           throw new UncheckedIOException(e);
                        }
                     case 400:
                     case 404:
                        retryAfter = null;
                        break label111;
                     case 403:
                        retryAfter = new WidgetImpl(guildId);
                        break label110;
                     case 429:
                        try {
                           InputStream stream = data;

                           try {
                              retryAfter = DataObject.fromJson(stream).getLong("retry_after");
                           } catch (Throwable var16) {
                              if (data != null) {
                                 try {
                                    stream.close();
                                 } catch (Throwable var14) {
                                    var16.addSuppressed(var14);
                                 }
                              }

                              throw var16;
                           }

                           if (data != null) {
                              data.close();
                           }
                        } catch (Exception var17) {
                           retryAfter = 0L;
                        }

                        throw new RateLimitedException("https://discord.com/api/v10/guilds/%s/widget.json", retryAfter);
                     default:
                        throw new IllegalStateException("An unknown status was returned: " + code + " " + response.message());
                  }
               } catch (Throwable var19) {
                  if (response != null) {
                     try {
                        response.close();
                     } catch (Throwable var12) {
                        var19.addSuppressed(var12);
                     }
                  }

                  throw var19;
               }

               if (response != null) {
                  response.close();
               }

               return var8;
            }

            if (response != null) {
               response.close();
            }

            return retryAfter;
         }

         if (response != null) {
            response.close();
         }

         return retryAfter;
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public static enum BannerType {
      SHIELD,
      BANNER1,
      BANNER2,
      BANNER3,
      BANNER4;

      // $FF: synthetic method
      private static BannerType[] $values() {
         return new BannerType[]{SHIELD, BANNER1, BANNER2, BANNER3, BANNER4};
      }
   }

   public static enum WidgetTheme {
      LIGHT,
      DARK;

      // $FF: synthetic method
      private static WidgetTheme[] $values() {
         return new WidgetTheme[]{LIGHT, DARK};
      }
   }
}
