package net.dv8tion.jda.api.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.unions.IWebhookContainerUnion;

public interface WebhookAction extends AuditableRestAction<Webhook> {
   @Nonnull
   @CheckReturnValue
   WebhookAction setCheck(@Nullable BooleanSupplier var1);

   @Nonnull
   @CheckReturnValue
   WebhookAction timeout(long var1, @Nonnull TimeUnit var3);

   @Nonnull
   @CheckReturnValue
   WebhookAction deadline(long var1);

   @Nonnull
   IWebhookContainerUnion getChannel();

   @Nonnull
   default Guild getGuild() {
      return this.getChannel().getGuild();
   }

   @Nonnull
   @CheckReturnValue
   WebhookAction setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   WebhookAction setAvatar(@Nullable Icon var1);
}
