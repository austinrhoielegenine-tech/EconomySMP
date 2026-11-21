package net.dv8tion.jda.api.entities.channel.attribute;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import org.jetbrains.annotations.Unmodifiable;

public interface IWebhookContainer extends GuildChannel {
   @Nonnull
   @CheckReturnValue
   RestAction<@Unmodifiable List<Webhook>> retrieveWebhooks();

   @Nonnull
   @CheckReturnValue
   WebhookAction createWebhook(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   AuditableRestAction<Void> deleteWebhookById(@Nonnull String var1);
}
