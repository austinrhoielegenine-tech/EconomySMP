package net.dv8tion.jda.api.interactions.components;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.ICustomIdInteraction;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

public interface ComponentInteraction extends IReplyCallback, IMessageEditCallback, IModalCallback, ICustomIdInteraction {
   @Nonnull
   default String getCustomId() {
      return this.getComponentId();
   }

   @Nonnull
   String getComponentId();

   default int getUniqueId() {
      return this.getComponent().getUniqueId();
   }

   @Nonnull
   ActionComponent getComponent();

   @Nonnull
   Message getMessage();

   long getMessageIdLong();

   @Nonnull
   default String getMessageId() {
      return Long.toUnsignedString(this.getMessageIdLong());
   }

   @Nonnull
   Component.Type getComponentType();

   @Nonnull
   MessageChannelUnion getChannel();

   @Nonnull
   default GuildMessageChannelUnion getGuildChannel() {
      return (GuildMessageChannelUnion)IReplyCallback.super.getGuildChannel();
   }
}
