package net.dv8tion.jda.api.events.interaction;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class ModalInteractionEvent extends GenericInteractionCreateEvent implements ModalInteraction {
   private final ModalInteraction interaction;

   public ModalInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull ModalInteraction interaction) {
      super(api, responseNumber, interaction);
      this.interaction = interaction;
   }

   @Nonnull
   public ModalInteraction getInteraction() {
      return this.interaction;
   }

   @Nonnull
   public String getModalId() {
      return this.interaction.getModalId();
   }

   @Nonnull
   public List<ModalMapping> getValues() {
      return this.interaction.getValues();
   }

   @Nullable
   public Message getMessage() {
      return this.interaction.getMessage();
   }

   @Nonnull
   @CheckReturnValue
   public ReplyCallbackAction deferReply() {
      return this.interaction.deferReply();
   }

   @Nonnull
   public InteractionHook getHook() {
      return this.interaction.getHook();
   }

   @Nonnull
   @CheckReturnValue
   public MessageEditCallbackAction deferEdit() {
      return this.interaction.deferEdit();
   }

   @Nonnull
   public MessageChannelUnion getChannel() {
      return this.interaction.getChannel();
   }
}
