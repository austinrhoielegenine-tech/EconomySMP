package net.dv8tion.jda.api.components.selections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.internal.components.selections.StringSelectMenuImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface StringSelectMenu extends SelectMenu {
   @Nonnull
   default StringSelectMenu asDisabled() {
      return (StringSelectMenu)SelectMenu.super.asDisabled();
   }

   @Nonnull
   default StringSelectMenu asEnabled() {
      return (StringSelectMenu)SelectMenu.super.asEnabled();
   }

   @Nonnull
   default StringSelectMenu withDisabled(boolean disabled) {
      return ((Builder)this.createCopy().setDisabled(disabled)).build();
   }

   @Nonnull
   StringSelectMenu withUniqueId(int var1);

   @Nonnull
   List<SelectOption> getOptions();

   @Nonnull
   @CheckReturnValue
   default Builder createCopy() {
      Builder builder = create(this.getCustomId());
      builder.setRequiredRange(this.getMinValues(), this.getMaxValues());
      builder.setPlaceholder(this.getPlaceholder());
      builder.addOptions((Collection)this.getOptions());
      builder.setDisabled(this.isDisabled());
      builder.setRequired(this.isRequired());
      if (this.getUniqueId() > 0) {
         builder.setUniqueId(this.getUniqueId());
      }

      return builder;
   }

   @Nonnull
   @CheckReturnValue
   static Builder create(@Nonnull String customId) {
      return new Builder(customId);
   }

   public static class Builder extends SelectMenu.Builder<StringSelectMenu, Builder> {
      private final List<SelectOption> options = new ArrayList();

      protected Builder(@Nonnull String customId) {
         super(customId);
      }

      @Nonnull
      public Builder addOptions(@Nonnull SelectOption... options) {
         Checks.noneNull(options, "Options");
         Checks.check(this.options.size() + options.length <= 25, "Cannot have more than %d options for a select menu!", (int)25);
         Collections.addAll(this.options, options);
         return this;
      }

      @Nonnull
      public Builder addOptions(@Nonnull Collection<? extends SelectOption> options) {
         Checks.noneNull(options, "Options");
         Checks.check(this.options.size() + options.size() <= 25, "Cannot have more than %d options for a select menu!", (int)25);
         this.options.addAll(options);
         return this;
      }

      @Nonnull
      public Builder addOption(@Nonnull String label, @Nonnull String value) {
         return this.addOptions(new SelectOption(label, value));
      }

      @Nonnull
      public Builder addOption(@Nonnull String label, @Nonnull String value, @Nonnull Emoji emoji) {
         return this.addOption(label, value, (String)null, emoji);
      }

      @Nonnull
      public Builder addOption(@Nonnull String label, @Nonnull String value, @Nonnull String description) {
         return this.addOption(label, value, description, (Emoji)null);
      }

      @Nonnull
      public Builder addOption(@Nonnull String label, @Nonnull String value, @Nullable String description, @Nullable Emoji emoji) {
         return this.addOptions(new SelectOption(label, value, description, false, emoji));
      }

      @Nonnull
      public List<SelectOption> getOptions() {
         return this.options;
      }

      @Nonnull
      public Builder setDefaultValues(@Nonnull Collection<String> values) {
         Checks.noneNull(values, "Values");
         Set<String> set = new HashSet(values);
         ListIterator<SelectOption> it = this.getOptions().listIterator();

         while(it.hasNext()) {
            SelectOption option = (SelectOption)it.next();
            it.set(option.withDefault(set.contains(option.getValue())));
         }

         return this;
      }

      @Nonnull
      public Builder setDefaultValues(@Nonnull String... values) {
         Checks.noneNull(values, "Values");
         return this.setDefaultValues(Arrays.asList(values));
      }

      @Nonnull
      public Builder setDefaultOptions(@Nonnull Collection<? extends SelectOption> values) {
         Checks.noneNull(values, "Values");
         return this.setDefaultValues((Collection)values.stream().map(SelectOption::getValue).collect(Collectors.toSet()));
      }

      @Nonnull
      public Builder setDefaultOptions(@Nonnull SelectOption... values) {
         Checks.noneNull(values, "Values");
         return this.setDefaultOptions(Arrays.asList(values));
      }

      @Nonnull
      public StringSelectMenu build() {
         Checks.check(this.minValues <= this.maxValues, "Min values cannot be greater than max values!");
         Checks.check(!this.options.isEmpty(), "Cannot build a select menu without options. Add at least one option!");
         Checks.check(this.options.size() <= 25, "Cannot build a select menu with more than %d options.", (int)25);
         if (this.required != null && this.required) {
            Checks.check(this.minValues > 0, "A select menu cannot have min values set to 0 and be required at the same time");
         }

         int min = Math.min(this.minValues, this.options.size());
         int max = Math.min(this.maxValues, this.options.size());
         return new StringSelectMenuImpl(this.customId, this.uniqueId, this.placeholder, min, max, this.disabled, this.options, this.required);
      }
   }
}
