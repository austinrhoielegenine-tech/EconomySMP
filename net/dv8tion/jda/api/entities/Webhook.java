package net.dv8tion.jda.api.entities;

import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.unions.IWebhookContainerUnion;
import net.dv8tion.jda.api.managers.WebhookManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;

public interface Webhook extends ISnowflake, WebhookClient<Message> {
   Pattern WEBHOOK_URL = Pattern.compile("https?://(?:[^\\s.]+\\.)?discord(?:app)?\\.com/api(?:/v\\d+)?/webhooks/(?<id>\\d+)/(?<token>[^\\s/]+)", 2);

   @Nonnull
   JDA getJDA();

   @Nonnull
   WebhookType getType();

   boolean isPartial();

   @Nonnull
   Guild getGuild();

   @Nonnull
   IWebhookContainerUnion getChannel();

   @Nullable
   Member getOwner();

   @Nullable
   User getOwnerAsUser();

   @Nonnull
   User getDefaultUser();

   @Nonnull
   String getName();

   @Nullable
   String getToken();

   @Nonnull
   String getUrl();

   @Nullable
   ChannelReference getSourceChannel();

   @Nullable
   GuildReference getSourceGuild();

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> delete();

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> delete(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   WebhookManager getManager();

   public static class WebhookReference implements ISnowflake {
      private final JDA api;
      private final long webhookId;
      private final long channelId;

      public WebhookReference(JDA api, long webhookId, long channelId) {
         this.api = api;
         this.webhookId = webhookId;
         this.channelId = channelId;
      }

      public long getIdLong() {
         return this.webhookId;
      }

      @Nonnull
      public String getChannelId() {
         return Long.toUnsignedString(this.channelId);
      }

      public long getChannelIdLong() {
         return this.channelId;
      }

      @Nonnull
      @CheckReturnValue
      public RestAction<Webhook> resolve() {
         Route.CompiledRoute route = Route.Webhooks.GET_WEBHOOK.compile(this.getId());
         return new RestActionImpl<Webhook>(this.api, route, (response, request) -> request.getJDA().getEntityBuilder().createWebhook(response.getObject(), true));
      }
   }

   public static class ChannelReference implements ISnowflake {
      private final long id;
      private final String name;

      public ChannelReference(long id, String name) {
         this.id = id;
         this.name = name;
      }

      public long getIdLong() {
         return this.id;
      }

      @Nonnull
      public String getName() {
         return this.name;
      }
   }

   public static class GuildReference implements ISnowflake {
      private final long id;
      private final String name;

      public GuildReference(long id, String name) {
         this.id = id;
         this.name = name;
      }

      public long getIdLong() {
         return this.id;
      }

      @Nonnull
      public String getName() {
         return this.name;
      }
   }
}
