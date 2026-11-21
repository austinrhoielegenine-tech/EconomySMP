package net.dv8tion.jda.internal.requests.restaction.interactions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.callbacks.IAutoCompleteCallback;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.InteractionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class AutoCompleteCallbackActionImpl extends InteractionCallbackImpl<Void> implements AutoCompleteCallbackAction {
   private final OptionType type;
   private final List<Command.Choice> choices = new ArrayList(26);

   public AutoCompleteCallbackActionImpl(IAutoCompleteCallback interaction, OptionType type) {
      super((InteractionImpl)interaction);
      this.type = type;
   }

   @Nonnull
   public OptionType getOptionType() {
      return this.type;
   }

   @Nonnull
   public AutoCompleteCallbackAction addChoices(@Nonnull Collection<Command.Choice> choices) {
      Checks.noneNull(choices, "Choices");
      Checks.check(choices.size() + this.choices.size() <= 25, "Can only reply with up to %d choices. Limit your suggestions!", (int)25);

      for(Command.Choice choice : choices) {
         Checks.inRange(choice.getName(), 1, 100, "Choice name");
         switch (this.type) {
            case INTEGER:
               Checks.check(choice.getType() == OptionType.INTEGER, "Choice of type %s cannot be converted to INTEGER", (Object)choice.getType());
               long valueLong = choice.getAsLong();
               Checks.check((double)valueLong <= 9.007199254740991E15, "Choice value cannot be larger than %f Provided: %d", 9.007199254740991E15, valueLong);
               Checks.check((double)valueLong >= -9.007199254740991E15, "Choice value cannot be smaller than %f. Provided: %d", -9.007199254740991E15, valueLong);
               break;
            case NUMBER:
               Checks.check(choice.getType() == OptionType.NUMBER || choice.getType() == OptionType.INTEGER, "Choice of type %s cannot be converted to NUMBER", (Object)choice.getType());
               double valueDouble = choice.getAsDouble();
               Checks.check(valueDouble <= 9.007199254740991E15, "Choice value cannot be larger than %f Provided: %f", 9.007199254740991E15, valueDouble);
               Checks.check(valueDouble >= -9.007199254740991E15, "Choice value cannot be smaller than %f. Provided: %f", -9.007199254740991E15, valueDouble);
               break;
            case STRING:
               String valueString = choice.getAsString();
               Checks.inRange(valueString, 1, 100, "Choice value");
         }
      }

      this.choices.addAll(choices);
      return this;
   }

   protected RequestBody finalizeData() {
      DataObject data = DataObject.empty();
      DataArray array = DataArray.empty();
      this.choices.forEach((choice) -> array.add(choice.toData(this.type)));
      data.put("choices", array);
      return this.getRequestBody(DataObject.empty().put("type", InteractionCallbackAction.ResponseType.COMMAND_AUTOCOMPLETE_CHOICES.getRaw()).put("data", data));
   }

   @Nonnull
   public AutoCompleteCallbackAction setCheck(BooleanSupplier checks) {
      return (AutoCompleteCallbackAction)super.setCheck(checks);
   }

   @Nonnull
   public AutoCompleteCallbackAction deadline(long timestamp) {
      return (AutoCompleteCallbackAction)super.deadline(timestamp);
   }
}
