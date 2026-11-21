package net.dv8tion.jda.api.requests;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class RestConfig {
   public static final String USER_AGENT;
   public static final String DEFAULT_BASE_URL = "https://discord.com/api/v10/";
   private String userAgent;
   private String baseUrl;
   private boolean relativeRateLimit;
   private Consumer<? super okhttp3.Request.Builder> customBuilder;
   private Function<? super RestRateLimiter.RateLimitConfig, ? extends RestRateLimiter> rateLimiter;

   public RestConfig() {
      this.userAgent = USER_AGENT;
      this.baseUrl = "https://discord.com/api/v10/";
      this.relativeRateLimit = true;
      this.rateLimiter = SequentialRestRateLimiter::new;
   }

   @Nonnull
   public RestConfig setRelativeRateLimit(boolean relativeRateLimit) {
      this.relativeRateLimit = relativeRateLimit;
      return this;
   }

   @Nonnull
   public RestConfig setRateLimiterFactory(@Nonnull Function<? super RestRateLimiter.RateLimitConfig, ? extends RestRateLimiter> rateLimiter) {
      Checks.notNull(rateLimiter, "RateLimiter");
      this.rateLimiter = rateLimiter;
      return this;
   }

   @Nonnull
   public RestConfig setBaseUrl(@Nonnull String baseUrl) {
      Checks.notEmpty(baseUrl, "URL");
      Checks.check(baseUrl.length() > 4 && baseUrl.substring(0, 4).equalsIgnoreCase("http"), "URL must be HTTP");
      if (baseUrl.endsWith("/")) {
         this.baseUrl = baseUrl;
      } else {
         this.baseUrl = baseUrl + "/";
      }

      return this;
   }

   @Nonnull
   public RestConfig setUserAgentSuffix(@Nullable String suffix) {
      if (suffix != null && !Helpers.isBlank(suffix)) {
         this.userAgent = USER_AGENT + " " + suffix;
      } else {
         this.userAgent = USER_AGENT;
      }

      return this;
   }

   @Nonnull
   public RestConfig setCustomBuilder(@Nullable Consumer<? super okhttp3.Request.Builder> customBuilder) {
      this.customBuilder = customBuilder;
      return this;
   }

   @Nonnull
   public String getUserAgent() {
      return this.userAgent;
   }

   @Nonnull
   public String getBaseUrl() {
      return this.baseUrl;
   }

   @Nonnull
   public Function<? super RestRateLimiter.RateLimitConfig, ? extends RestRateLimiter> getRateLimiterFactory() {
      return this.rateLimiter;
   }

   @Nullable
   public Consumer<? super okhttp3.Request.Builder> getCustomBuilder() {
      return this.customBuilder;
   }

   public boolean isRelativeRateLimit() {
      return this.relativeRateLimit;
   }

   static {
      USER_AGENT = "DiscordBot (https://github.com/discord-jda/JDA, " + JDAInfo.VERSION + ")";
   }
}
