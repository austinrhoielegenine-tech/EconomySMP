package net.dv8tion.jda.api.events.interaction.command;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class GenericCommandInteractionEvent extends GenericInteractionCreateEvent implements CommandInteraction {
   public GenericCommandInteractionEvent(@Nonnull JDA api, long responseNumber, @Nonnull CommandInteraction interaction) {
      super(api, responseNumber, interaction);
   }

   @Nonnull
   public CommandInteraction getInteraction() {
      return (CommandInteraction)super.getInteraction();
   }

   @Nonnull
   public Command.Type getCommandType() {
      return this.getInteraction().getCommandType();
   }

   @Nonnull
   public String getName() {
      return this.getInteraction().getName();
   }

   @Nullable
   public String getSubcommandName() {
      return this.getInteraction().getSubcommandName();
   }

   @Nullable
   public String getSubcommandGroup() {
      return this.getInteraction().getSubcommandGroup();
   }

   public long getCommandIdLong() {
      return this.getInteraction().getCommandIdLong();
   }

   public boolean isGuildCommand() {
      return this.getInteraction().isGuildCommand();
   }

   @Nonnull
   public List<OptionMapping> getOptions() {
      return this.getInteraction().getOptions();
   }

   @Nonnull
   public InteractionHook getHook() {
      return this.getInteraction().getHook();
   }

   @Nonnull
   @CheckReturnValue
   public ReplyCallbackAction deferReply() {
      return this.getInteraction().deferReply();
   }

   @Nonnull
   @CheckReturnValue
   public ModalCallbackAction replyModal(@Nonnull Modal modal) {
      return this.getInteraction().replyModal(modal);
   }
}
