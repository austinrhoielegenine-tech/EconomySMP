package net.dv8tion.jda.internal.interactions;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationOwners;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.InteractionEntityBuilder;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.entities.detached.DetachedGuildImpl;
import net.dv8tion.jda.internal.utils.Helpers;

public class InteractionImpl implements Interaction {
   protected final long id;
   protected final long channelId;
   protected final int interactionType;
   protected final String token;
   protected final Guild guild;
   protected final Member member;
   protected final User user;
   protected final Channel channel;
   protected final DiscordLocale userLocale;
   protected final List<Entitlement> entitlements;
   protected final InteractionContextType context;
   protected final IntegrationOwners integrationOwners;
   protected final JDAImpl api;
   protected final InteractionEntityBuilder interactionEntityBuilder;
   private boolean isAck;

   public InteractionImpl(JDAImpl jda, DataObject data) {
      DataObject userObj = ((DataObject)data.optObject("member").orElse(data)).getObject("user");
      this.api = jda;
      this.interactionEntityBuilder = new InteractionEntityBuilder(jda, data.getLong("channel_id"), userObj.getUnsignedLong("id"));
      this.id = data.getUnsignedLong("id");
      this.token = data.getString("token");
      this.interactionType = data.getInt("type");
      this.guild = (Guild)data.optObject("guild").map((guildJson) -> {
         if (!guildJson.hasKey("preferred_locale")) {
            guildJson.put("preferred_locale", data.getString("guild_locale", "en-US"));
         }

         return this.interactionEntityBuilder.getOrCreateGuild(guildJson);
      }).orElse((Object)null);
      this.channelId = data.getUnsignedLong("channel_id", 0L);
      this.userLocale = DiscordLocale.from(data.getString("locale", "en-US"));
      this.context = InteractionContextType.fromKey(data.getString("context"));
      this.integrationOwners = new IntegrationOwnersImpl(data.getObject("authorizing_integration_owners"));
      DataObject channelJson = data.getObject("channel");
      ChannelType channelType = ChannelType.fromId(channelJson.getInt("type"));
      if (this.guild instanceof GuildImpl) {
         this.member = jda.getEntityBuilder().createMember((GuildImpl)this.guild, data.getObject("member"));
         jda.getEntityBuilder().updateMemberCache((MemberImpl)this.member);
         this.user = this.member.getUser();
         GuildChannel channel = this.guild.getGuildChannelById(channelJson.getUnsignedLong("id"));
         if (channel == null && channelType.isThread()) {
            channel = this.api.getEntityBuilder().createThreadChannel((GuildImpl)this.guild, channelJson, this.guild.getIdLong(), false);
         }

         if (channel == null) {
            throw new IllegalStateException("Failed to create channel instance for interaction! Channel Type: " + channelJson.getInt("type"));
         }

         this.channel = channel;
      } else if (this.guild instanceof DetachedGuildImpl) {
         this.member = this.interactionEntityBuilder.createMember(this.guild, data.getObject("member"));
         this.user = this.member.getUser();
         if (channelType.isThread()) {
            this.channel = this.interactionEntityBuilder.createThreadChannel(this.guild, channelJson);
         } else {
            this.channel = this.interactionEntityBuilder.createGuildChannel(this.guild, channelJson);
         }

         if (this.channel == null) {
            throw new IllegalStateException("Failed to create channel instance for interaction! Channel Type: " + channelJson.getInt("type"));
         }
      } else {
         this.user = jda.getEntityBuilder().createUser(userObj);
         this.member = null;
         switch (channelType) {
            case PRIVATE:
               this.channel = this.interactionEntityBuilder.createPrivateChannel(channelJson, this.user);
               break;
            case GROUP:
               this.channel = this.interactionEntityBuilder.createGroupChannel(channelJson);
               break;
            default:
               throw new IllegalArgumentException("Received interaction in unexpected channel type! Type " + channelType + " is not supported yet!");
         }
      }

      Stream var10001 = ((DataArray)data.optArray("entitlements").orElseGet(DataArray::empty)).stream(DataArray::getObject);
      EntityBuilder var10002 = jda.getEntityBuilder();
      Objects.requireNonNull(var10002);
      this.entitlements = (List)var10001.map(var10002::createEntitlement).collect(Helpers.toUnmodifiableList());
   }

   public synchronized void releaseHook(boolean success) {
   }

   public synchronized boolean ack() {
      boolean wasAck = this.isAck;
      this.isAck = true;
      return wasAck;
   }

   public synchronized boolean isAcknowledged() {
      return this.isAck;
   }

   public long getIdLong() {
      return this.id;
   }

   public int getTypeRaw() {
      return this.interactionType;
   }

   @Nonnull
   public String getToken() {
      return this.token;
   }

   @Nullable
   public Guild getGuild() {
      return this.guild;
   }

   @Nullable
   public Channel getChannel() {
      return this.channel;
   }

   public long getChannelIdLong() {
      return this.channelId;
   }

   @Nonnull
   public DiscordLocale getUserLocale() {
      return this.userLocale;
   }

   @Nonnull
   public InteractionContextType getContext() {
      return this.context;
   }

   @Nonnull
   public IntegrationOwners getIntegrationOwners() {
      return this.integrationOwners;
   }

   @Nonnull
   public User getUser() {
      return this.user;
   }

   @Nullable
   public Member getMember() {
      return this.member;
   }

   @Nonnull
   public List<Entitlement> getEntitlements() {
      return this.entitlements;
   }

   @Nonnull
   public JDAImpl getJDA() {
      return this.api;
   }

   @Nonnull
   public InteractionEntityBuilder getInteractionEntityBuilder() {
      return this.interactionEntityBuilder;
   }
}
