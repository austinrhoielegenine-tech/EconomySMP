package net.dv8tion.jda.internal.interactions.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.interactions.DeferrableInteractionImpl;
import net.dv8tion.jda.internal.requests.restaction.interactions.ModalCallbackActionImpl;
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyCallbackActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public class CommandInteractionImpl extends DeferrableInteractionImpl implements CommandInteraction, CommandInteractionPayloadMixin {
   private final CommandInteractionPayloadImpl payload;

   public CommandInteractionImpl(JDAImpl jda, DataObject data) {
      super(jda, data);
      this.payload = new CommandInteractionPayloadImpl(jda, data);
   }

   public CommandInteractionPayload getCommandPayload() {
      return this.payload;
   }

   @Nonnull
   public ReplyCallbackAction deferReply() {
      return new ReplyCallbackActionImpl(this.hook);
   }

   @Nonnull
   public ModalCallbackAction replyModal(@Nonnull Modal modal) {
      Checks.notNull(modal, "Modal");
      return new ModalCallbackActionImpl(this, modal);
   }
}
