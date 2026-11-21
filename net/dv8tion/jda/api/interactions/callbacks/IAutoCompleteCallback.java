package net.dv8tion.jda.api.interactions.callbacks;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.restaction.interactions.AutoCompleteCallbackAction;
import net.dv8tion.jda.internal.utils.Checks;

public interface IAutoCompleteCallback extends Interaction {
   @Nonnull
   @CheckReturnValue
   AutoCompleteCallbackAction replyChoices(@Nonnull Collection<Command.Choice> var1);

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction replyChoices(@Nonnull Command.Choice... choices) {
      Checks.noneNull(choices, "Choice");
      return this.replyChoices(Arrays.asList(choices));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction replyChoice(@Nonnull String name, @Nonnull String value) {
      return this.replyChoices(new Command.Choice(name, value));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction replyChoice(@Nonnull String name, long value) {
      return this.replyChoices(new Command.Choice(name, value));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction replyChoice(@Nonnull String name, double value) {
      return this.replyChoices(new Command.Choice(name, value));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction replyChoiceStrings(@Nonnull String... choices) {
      return this.replyChoices((Collection)Arrays.stream(choices).map((it) -> new Command.Choice(it, it)).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction replyChoiceStrings(@Nonnull Collection<String> choices) {
      return this.replyChoices((Collection)choices.stream().map((it) -> new Command.Choice(it, it)).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction replyChoiceLongs(@Nonnull long... choices) {
      return this.replyChoices((Collection)Arrays.stream(choices).mapToObj((it) -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction replyChoiceLongs(@Nonnull Collection<Long> choices) {
      return this.replyChoices((Collection)choices.stream().map((it) -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction replyChoiceDoubles(@Nonnull double... choices) {
      return this.replyChoices((Collection)Arrays.stream(choices).mapToObj((it) -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
   }

   @Nonnull
   @CheckReturnValue
   default AutoCompleteCallbackAction replyChoiceDoubles(@Nonnull Collection<Double> choices) {
      return this.replyChoices((Collection)choices.stream().map((it) -> new Command.Choice(String.valueOf(it), it)).collect(Collectors.toList()));
   }
}
