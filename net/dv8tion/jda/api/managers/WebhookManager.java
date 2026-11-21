package net.dv8tion.jda.api.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.IWebhookContainerUnion;

public interface WebhookManager extends Manager<WebhookManager> {
   long NAME = 1L;
   long CHANNEL = 2L;
   long AVATAR = 4L;

   @Nonnull
   @CheckReturnValue
   WebhookManager reset(long var1);

   @Nonnull
   @CheckReturnValue
   WebhookManager reset(@Nonnull long... var1);

   @Nonnull
   Webhook getWebhook();

   @Nonnull
   default IWebhookContainerUnion getChannel() {
      return this.getWebhook().getChannel();
   }

   @Nonnull
   default Guild getGuild() {
      return this.getWebhook().getGuild();
   }

   @Nonnull
   @CheckReturnValue
   WebhookManager setName(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   WebhookManager setAvatar(@Nullable Icon var1);

   @Nonnull
   @CheckReturnValue
   WebhookManager setChannel(@Nonnull TextChannel var1);
}
