package net.dv8tion.jda.api.requests.restaction.interactions;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.FluentRestAction;
import net.dv8tion.jda.internal.utils.Checks;

public interface AutoCompleteCallbackAction extends InteractionCallbackAction<Void>, FluentRestAction<Void, AutoCompleteCallbackAction> {
   @Nonnull
   OptionType getOptionType();

   @Nonnull
   @CheckReturnValue
   AutoCompleteCallbackAction addChoices(@Nonnull Collection<Command.Choice> var1);

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction addChoices(@Nonnull Command.Choice... choices) {
      Checks.noneNull(choices, "Choices");
      return this.addChoices(Arrays.asList(choices));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction addChoice(@Nonnull String name, @Nonnull String value) {
      return this.addChoices(new Command.Choice(name, value));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction addChoice(@Nonnull String name, long value) {
      return this.addChoices(new Command.Choice(name, value));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction addChoice(@Nonnull String name, double value) {
      return this.addChoices(new Command.Choice(name, value));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction addChoiceStrings(@Nonnull String... choices) {
      return this.addChoices((Collection)Arrays.stream(choices).map((it) -> new Command.Choice(it, it)).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction addChoiceStrings(@Nonnull Collection<String> choices) {
      return this.addChoices((Collection)choices.stream().map((it) -> new Command.Choice(it, it)).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction addChoiceLongs(@Nonnull long... choices) {
      return this.addChoices((Collection)Arrays.stream(choices).mapToObj((it) -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction addChoiceLongs(@Nonnull Collection<Long> choices) {
      return this.addChoices((Collection)choices.stream().map((it) -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction addChoiceDoubles(@Nonnull double... choices) {
      return this.addChoices((Collection)Arrays.stream(choices).mapToObj((it) -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction addChoiceDoubles(@Nonnull Collection<Double> choices) {
      return this.addChoices((Collection)choices.stream().map((it) -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
   }
}
