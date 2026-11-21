package net.dv8tion.jda.api.entities.channel.concrete;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.channel.concrete.NewsChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface NewsChannel extends StandardGuildMessageChannel {
   @Nonnull
   @CheckReturnValue
   RestAction<Webhook.WebhookReference> follow(@Nonnull String var1);

   @Nonnull
   @CheckReturnValue
   default RestAction<Webhook.WebhookReference> follow(long targetChannelId) {
      return this.follow(Long.toUnsignedString(targetChannelId));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Webhook.WebhookReference> follow(@Nonnull TextChannel targetChannel) {
      Checks.notNull(targetChannel, "Target Channel");
      Member selfMember = targetChannel.getGuild().getSelfMember();
      Checks.checkAccess(selfMember, targetChannel);
      if (!selfMember.hasPermission(targetChannel, new Permission[]{Permission.MANAGE_WEBHOOKS})) {
         throw new InsufficientPermissionException(targetChannel, Permission.MANAGE_WEBHOOKS);
      } else {
         return this.follow(targetChannel.getId());
      }
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Message> crosspostMessageById(@Nonnull String messageId) {
      Checks.isSnowflake(messageId);
      Checks.checkAccess(this.getGuild().getSelfMember(), this);
      Checks.checkAttached(this);
      Route.CompiledRoute route = Route.Messages.CROSSPOST_MESSAGE.compile(this.getId(), messageId);
      return new RestActionImpl<Message>(this.getJDA(), route, (response, request) -> request.getJDA().getEntityBuilder().createMessageWithChannel(response.getObject(), this, false));
   }

   @Nonnull
   @CheckReturnValue
   default RestAction<Message> crosspostMessageById(long messageId) {
      return this.crosspostMessageById(Long.toUnsignedString(messageId));
   }

   @Nonnull
   @CheckReturnValue
   ChannelAction<NewsChannel> createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<NewsChannel> createCopy() {
      return this.createCopy(this.getGuild());
   }

   @Nonnull
   @CheckReturnValue
   NewsChannelManager getManager();
}
