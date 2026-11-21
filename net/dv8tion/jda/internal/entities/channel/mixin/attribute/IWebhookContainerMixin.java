package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.unions.IWebhookContainerUnion;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.WebhookActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface IWebhookContainerMixin<T extends IWebhookContainerMixin<T>> extends IWebhookContainer, IWebhookContainerUnion, GuildChannelMixin<T> {
   @Nonnull
   default RestAction<List<Webhook>> retrieveWebhooks() {
      this.checkAttached();
      this.checkPermission(Permission.MANAGE_WEBHOOKS);
      Route.CompiledRoute route = Route.Channels.GET_WEBHOOKS.compile(this.getId());
      JDAImpl jda = (JDAImpl)this.getJDA();
      return new RestActionImpl<List<Webhook>>(jda, route, (response, request) -> {
         DataArray array = response.getArray();
         List<Webhook> webhooks = new ArrayList(array.length());
         EntityBuilder builder = jda.getEntityBuilder();

         for(int i = 0; i < array.length(); ++i) {
            try {
               webhooks.add(builder.createWebhook(array.getObject(i)));
            } catch (NullPointerException | UncheckedIOException e) {
               JDAImpl.LOG.error((String)"Error while creating websocket from json", (Throwable)e);
            }
         }

         return Collections.unmodifiableList(webhooks);
      });
   }

   @Nonnull
   default WebhookAction createWebhook(@Nonnull String name) {
      Checks.notBlank(name, "Webhook name");
      name = name.trim();
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      this.checkAttached();
      this.checkPermission(Permission.MANAGE_WEBHOOKS);
      return new WebhookActionImpl(this.getJDA(), this, name);
   }

   @Nonnull
   default AuditableRestAction<Void> deleteWebhookById(@Nonnull String id) {
      Checks.isSnowflake(id, "Webhook ID");
      this.checkAttached();
      this.checkPermission(Permission.MANAGE_WEBHOOKS);
      Route.CompiledRoute route = Route.Webhooks.DELETE_WEBHOOK.compile(id);
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }
}
