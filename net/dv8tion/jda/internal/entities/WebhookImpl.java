package net.dv8tion.jda.internal.entities;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.WebhookClient;
import net.dv8tion.jda.api.entities.WebhookType;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.unions.IWebhookContainerUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.WebhookManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageDeleteAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageRetrieveAction;
import net.dv8tion.jda.internal.managers.WebhookManagerImpl;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageCreateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.WebhookMessageEditActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class WebhookImpl extends AbstractWebhookClient<Message> implements Webhook {
   private final IWebhookContainer channel;
   private final WebhookType type;
   private Member owner;
   private User user;
   private User ownerUser;
   private Webhook.ChannelReference sourceChannel;
   private Webhook.GuildReference sourceGuild;

   public WebhookImpl(IWebhookContainer channel, long id, WebhookType type) {
      this(channel, channel.getJDA(), id, type);
   }

   public WebhookImpl(IWebhookContainer channel, JDA api, long id, WebhookType type) {
      super(id, (String)null, api);
      this.channel = channel;
      this.type = type;
   }

   @Nonnull
   public WebhookType getType() {
      return this.type;
   }

   public boolean isPartial() {
      return this.channel == null;
   }

   @Nonnull
   public JDA getJDA() {
      return this.api;
   }

   @Nonnull
   public Guild getGuild() {
      if (this.channel == null) {
         throw new IllegalStateException("Cannot provide guild for this Webhook instance because it does not belong to this shard");
      } else {
         return this.getChannel().getGuild();
      }
   }

   @Nonnull
   public IWebhookContainerUnion getChannel() {
      if (this.channel == null) {
         throw new IllegalStateException("Cannot provide channel for this Webhook instance because it does not belong to this shard");
      } else {
         return (IWebhookContainerUnion)this.channel;
      }
   }

   public Member getOwner() {
      return this.owner == null && this.channel != null && this.ownerUser != null ? this.getGuild().getMember(this.ownerUser) : this.owner;
   }

   public User getOwnerAsUser() {
      return this.ownerUser;
   }

   @Nonnull
   public User getDefaultUser() {
      return this.user;
   }

   @Nonnull
   public String getName() {
      return this.user.getName();
   }

   @Nonnull
   public String getUrl() {
      return "https://discord.com/api/v10/webhooks/" + this.getId() + (this.getToken() == null ? "" : "/" + this.getToken());
   }

   public Webhook.ChannelReference getSourceChannel() {
      return this.sourceChannel;
   }

   public Webhook.GuildReference getSourceGuild() {
      return this.sourceGuild;
   }

   @Nonnull
   public AuditableRestAction<Void> delete() {
      if (this.token != null) {
         return this.delete(this.token);
      } else if (!this.getGuild().getSelfMember().hasPermission(this.getChannel(), new Permission[]{Permission.MANAGE_WEBHOOKS})) {
         throw new InsufficientPermissionException(this.getChannel(), Permission.MANAGE_WEBHOOKS);
      } else {
         Route.CompiledRoute route = Route.Webhooks.DELETE_WEBHOOK.compile(this.getId());
         return new AuditableRestActionImpl<Void>(this.getJDA(), route);
      }
   }

   @Nonnull
   public AuditableRestAction<Void> delete(@Nonnull String token) {
      Checks.notNull(token, "Token");
      Route.CompiledRoute route = Route.Webhooks.DELETE_TOKEN_WEBHOOK.compile(this.getId(), token);
      return new AuditableRestActionImpl<Void>(this.getJDA(), route);
   }

   @Nonnull
   public WebhookManager getManager() {
      return new WebhookManagerImpl(this);
   }

   public WebhookMessageCreateActionImpl<Message> sendRequest() {
      this.checkToken();
      AbstractWebhookClient<Message> client = (AbstractWebhookClient)WebhookClient.createClient(this.api, this.getId(), this.token);
      return client.sendRequest();
   }

   public WebhookMessageEditActionImpl<Message> editRequest(String messageId) {
      this.checkToken();
      AbstractWebhookClient<Message> client = (AbstractWebhookClient)WebhookClient.createClient(this.api, this.getId(), this.token);
      return client.editRequest(messageId);
   }

   @Nonnull
   public WebhookMessageDeleteAction deleteMessageById(@Nonnull String messageId) {
      this.checkToken();
      return WebhookClient.createClient(this.api, this.getId(), this.token).deleteMessageById(messageId);
   }

   @Nonnull
   public WebhookMessageRetrieveAction retrieveMessageById(@Nonnull String messageId) {
      this.checkToken();
      return WebhookClient.createClient(this.api, this.getId(), this.token).retrieveMessageById(messageId);
   }

   private void checkToken() {
      if (this.token == null) {
         throw new UnsupportedOperationException("Cannot execute webhook without a token!");
      }
   }

   public WebhookImpl setOwner(Member member, User user) {
      this.owner = member;
      this.ownerUser = user;
      return this;
   }

   public WebhookImpl setToken(String token) {
      this.token = token;
      return this;
   }

   public WebhookImpl setUser(User user) {
      this.user = user;
      return this;
   }

   public WebhookImpl setSourceGuild(Webhook.GuildReference reference) {
      this.sourceGuild = reference;
      return this;
   }

   public WebhookImpl setSourceChannel(Webhook.ChannelReference reference) {
      this.sourceChannel = reference;
      return this;
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof WebhookImpl)) {
         return false;
      } else {
         WebhookImpl impl = (WebhookImpl)obj;
         return impl.id == this.id;
      }
   }

   public String toString() {
      return (new EntityString(this)).setName(this.getName()).toString();
   }
}
