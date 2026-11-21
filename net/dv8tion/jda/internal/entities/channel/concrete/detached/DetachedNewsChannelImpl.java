package net.dv8tion.jda.internal.entities.channel.concrete.detached;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.managers.channel.concrete.NewsChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractStandardGuildMessageChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IInteractionPermissionMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.NewsChannelMixin;
import net.dv8tion.jda.internal.entities.detached.DetachedGuildImpl;
import net.dv8tion.jda.internal.interactions.ChannelInteractionPermissions;

public class DetachedNewsChannelImpl extends AbstractStandardGuildMessageChannelImpl<DetachedNewsChannelImpl> implements NewsChannel, DefaultGuildChannelUnion, NewsChannelMixin<DetachedNewsChannelImpl>, IInteractionPermissionMixin<DetachedNewsChannelImpl> {
   private ChannelInteractionPermissions interactionPermissions;

   public DetachedNewsChannelImpl(long id, DetachedGuildImpl guild) {
      super(id, guild);
   }

   public boolean isDetached() {
      return true;
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.NEWS;
   }

   @Nonnull
   public List<Member> getMembers() {
      throw this.detachedException();
   }

   @Nonnull
   public RestAction<Webhook.WebhookReference> follow(@Nonnull String targetChannelId) {
      throw this.detachedException();
   }

   @Nonnull
   public NewsChannelManager getManager() {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelInteractionPermissions getInteractionPermissions() {
      return this.interactionPermissions;
   }

   @Nonnull
   public DetachedNewsChannelImpl setInteractionPermissions(@Nonnull ChannelInteractionPermissions interactionPermissions) {
      this.interactionPermissions = interactionPermissions;
      return this;
   }
}
