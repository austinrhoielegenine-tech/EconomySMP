package net.dv8tion.jda.internal.interactions.command;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.context.ContextInteraction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;

public abstract class ContextInteractionImpl<T> extends CommandInteractionImpl implements ContextInteraction<T>, CommandInteractionPayloadMixin {
   private final T target;
   private final CommandInteractionPayloadImpl payload;

   public ContextInteractionImpl(JDAImpl jda, DataObject data) {
      super(jda, data);
      this.payload = new CommandInteractionPayloadImpl(jda, data);
      this.target = (T)this.parse(data, data.getObject("data").getObject("resolved"));
   }

   protected abstract T parse(DataObject var1, DataObject var2);

   public CommandInteractionPayload getCommandPayload() {
      return this.payload;
   }

   @Nonnull
   public T getTarget() {
      return this.target;
   }
}
