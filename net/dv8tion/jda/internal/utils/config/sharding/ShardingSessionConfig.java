package net.dv8tion.jda.internal.utils.config.sharding;

import com.neovisionaries.ws.client.WebSocketFactory;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import net.dv8tion.jda.api.utils.SessionController;
import net.dv8tion.jda.internal.utils.IOUtil;
import net.dv8tion.jda.internal.utils.config.SessionConfig;
import net.dv8tion.jda.internal.utils.config.flags.ConfigFlag;
import net.dv8tion.jda.internal.utils.config.flags.ShardingConfigFlag;
import okhttp3.OkHttpClient;

public class ShardingSessionConfig extends SessionConfig {
   private final OkHttpClient.Builder builder;
   private final EnumSet<ShardingConfigFlag> shardingFlags;

   public ShardingSessionConfig(@Nullable SessionController sessionController, @Nullable VoiceDispatchInterceptor interceptor, @Nullable OkHttpClient httpClient, @Nullable OkHttpClient.Builder httpClientBuilder, @Nullable WebSocketFactory webSocketFactory, EnumSet<ConfigFlag> flags, EnumSet<ShardingConfigFlag> shardingFlags, int maxReconnectDelay, int largeThreshold) {
      super(sessionController, httpClient, webSocketFactory, interceptor, flags, maxReconnectDelay, largeThreshold);
      if (httpClient == null) {
         this.builder = httpClientBuilder == null ? IOUtil.newHttpClientBuilder() : httpClientBuilder;
      } else {
         this.builder = null;
      }

      this.shardingFlags = shardingFlags;
   }

   public SessionConfig toSessionConfig(OkHttpClient client) {
      return new SessionConfig(this.getSessionController(), client, this.getWebSocketFactory(), this.getVoiceDispatchInterceptor(), this.getFlags(), this.getMaxReconnectDelay(), this.getLargeThreshold());
   }

   public EnumSet<ShardingConfigFlag> getShardingFlags() {
      return this.shardingFlags;
   }

   @Nullable
   public OkHttpClient.Builder getHttpBuilder() {
      return this.builder;
   }

   @Nonnull
   public static ShardingSessionConfig getDefault() {
      return new ShardingSessionConfig((SessionController)null, (VoiceDispatchInterceptor)null, new OkHttpClient(), (OkHttpClient.Builder)null, (WebSocketFactory)null, ConfigFlag.getDefault(), ShardingConfigFlag.getDefault(), 900, 250);
   }
}
