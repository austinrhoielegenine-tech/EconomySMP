package net.dv8tion.jda.api.events.interaction;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.IntegrationOwners;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionContextType;

public class GenericInteractionCreateEvent extends Event implements Interaction {
   private final Interaction interaction;

   public GenericInteractionCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull Interaction interaction) {
      super(api, responseNumber);
      this.interaction = interaction;
   }

   @Nonnull
   public Interaction getInteraction() {
      return this.interaction;
   }

   @Nonnull
   public String getToken() {
      return this.interaction.getToken();
   }

   public int getTypeRaw() {
      return this.interaction.getTypeRaw();
   }

   @Nullable
   public Guild getGuild() {
      return this.interaction.getGuild();
   }

   @Nullable
   public Channel getChannel() {
      return this.interaction.getChannel();
   }

   public long getChannelIdLong() {
      return this.interaction.getChannelIdLong();
   }

   @Nonnull
   public DiscordLocale getUserLocale() {
      return this.interaction.getUserLocale();
   }

   @Nonnull
   public DiscordLocale getGuildLocale() {
      return this.interaction.getGuildLocale();
   }

   @Nonnull
   public InteractionContextType getContext() {
      return this.interaction.getContext();
   }

   @Nonnull
   public IntegrationOwners getIntegrationOwners() {
      return this.interaction.getIntegrationOwners();
   }

   @Nullable
   public Member getMember() {
      return this.interaction.getMember();
   }

   @Nonnull
   public User getUser() {
      return this.interaction.getUser();
   }

   @Nonnull
   public List<Entitlement> getEntitlements() {
      return this.interaction.getEntitlements();
   }

   public long getIdLong() {
      return this.interaction.getIdLong();
   }

   public boolean isAcknowledged() {
      return this.interaction.isAcknowledged();
   }
}
