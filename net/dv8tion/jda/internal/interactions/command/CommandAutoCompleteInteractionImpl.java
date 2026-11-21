package net.dv8tion.jda.internal.interactions.command;

import java.util.Collection;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction;
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.interactions.InteractionImpl;
import net.dv8tion.jda.internal.requests.restaction.interactions.AutoCompleteCallbackActionImpl;

public class CommandAutoCompleteInteractionImpl extends InteractionImpl implements CommandInteractionPayloadMixin, CommandAutoCompleteInteraction {
   private final CommandInteractionPayload payload;
   private AutoCompleteQuery focused;

   public CommandAutoCompleteInteractionImpl(JDAImpl jda, DataObject data) {
      super(jda, data);
      this.payload = new CommandInteractionPayloadImpl(jda, data);
      DataArray options = data.getObject("data").getArray("options");
      this.findFocused(options);
      if (this.focused == null) {
         throw new IllegalStateException("Failed to get focused option for auto complete interaction");
      }
   }

   private void findFocused(DataArray options) {
      for(int i = 0; i < options.length(); ++i) {
         DataObject option = options.getObject(i);
         switch (OptionType.fromKey(option.getInt("type"))) {
            case SUB_COMMAND:
            case SUB_COMMAND_GROUP:
               this.findFocused(option.getArray("options"));
               break;
            default:
               if (option.getBoolean("focused")) {
                  OptionMapping opt = this.getOption(option.getString("name"));
                  this.focused = new AutoCompleteQuery(opt);
               }
         }
      }

   }

   @Nonnull
   public AutoCompleteQuery getFocusedOption() {
      return this.focused;
   }

   @Nonnull
   public MessageChannelUnion getChannel() {
      return (MessageChannelUnion)super.getChannel();
   }

   public CommandInteractionPayload getCommandPayload() {
      return this.payload;
   }

   @Nonnull
   public AutoCompleteCallbackAction replyChoices(@Nonnull Collection<Command.Choice> choices) {
      return (new AutoCompleteCallbackActionImpl(this, this.focused.getType())).addChoices(choices);
   }
}
