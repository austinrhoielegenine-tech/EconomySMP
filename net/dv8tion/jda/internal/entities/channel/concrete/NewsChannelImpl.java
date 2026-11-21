package net.dv8tion.jda.internal.entities.channel.concrete;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.managers.channel.concrete.NewsChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractStandardGuildMessageChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.NewsChannelMixin;
import net.dv8tion.jda.internal.managers.channel.concrete.NewsChannelManagerImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;

public class NewsChannelImpl extends AbstractStandardGuildMessageChannelImpl<NewsChannelImpl> implements NewsChannel, DefaultGuildChannelUnion, NewsChannelMixin<NewsChannelImpl> {
   public NewsChannelImpl(long id, GuildImpl guild) {
      super(id, guild);
   }

   public boolean isDetached() {
      return false;
   }

   @Nonnull
   public GuildImpl getGuild() {
      return (GuildImpl)super.getGuild();
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.NEWS;
   }

   @Nonnull
   public List<Member> getMembers() {
      return (List)this.getGuild().getMembersView().stream().filter((m) -> m.hasPermission(this, new Permission[]{Permission.VIEW_CHANNEL})).collect(Helpers.toUnmodifiableList());
   }

   @Nonnull
   public RestAction<Webhook.WebhookReference> follow(@Nonnull String targetChannelId) {
      Checks.notNull(targetChannelId, "Target Channel ID");
      Route.CompiledRoute route = Route.Channels.FOLLOW_CHANNEL.compile(this.getId());
      DataObject body = DataObject.empty().put("webhook_channel_id", targetChannelId);
      return new RestActionImpl<Webhook.WebhookReference>(this.getJDA(), route, body, (response, request) -> {
         DataObject json = response.getObject();
         return new Webhook.WebhookReference(request.getJDA(), json.getUnsignedLong("webhook_id"), json.getUnsignedLong("channel_id"));
      });
   }

   @Nonnull
   public NewsChannelManager getManager() {
      return new NewsChannelManagerImpl(this);
   }
}
