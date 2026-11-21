package net.dv8tion.jda.api.events.interaction.component;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class GenericComponentInteractionCreateEvent extends GenericInteractionCreateEvent implements ComponentInteraction {
   private final ComponentInteraction interaction;

   public GenericComponentInteractionCreateEvent(@Nonnull JDA api, long responseNumber, @Nonnull ComponentInteraction interaction) {
      super(api, responseNumber, interaction);
      this.interaction = interaction;
   }

   @Nonnull
   public ComponentInteraction getInteraction() {
      return this.interaction;
   }

   @Nonnull
   public MessageChannelUnion getChannel() {
      return this.interaction.getChannel();
   }

   @Nonnull
   public String getComponentId() {
      return this.interaction.getComponentId();
   }

   @Nonnull
   public ActionComponent getComponent() {
      return this.interaction.getComponent();
   }

   @Nonnull
   public Message getMessage() {
      return this.interaction.getMessage();
   }

   public long getMessageIdLong() {
      return this.interaction.getMessageIdLong();
   }

   @Nonnull
   public Component.Type getComponentType() {
      return this.interaction.getComponentType();
   }

   @Nonnull
   @CheckReturnValue
   public MessageEditCallbackAction deferEdit() {
      return this.interaction.deferEdit();
   }

   @Nonnull
   public InteractionHook getHook() {
      return this.interaction.getHook();
   }

   @Nonnull
   @CheckReturnValue
   public ReplyCallbackAction deferReply() {
      return this.interaction.deferReply();
   }

   @Nonnull
   @CheckReturnValue
   public ModalCallbackAction replyModal(@Nonnull Modal modal) {
      return this.interaction.replyModal(modal);
   }
}
