package net.dv8tion.jda.api.components.checkboxgroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.attribute.ICustomId;
import net.dv8tion.jda.api.components.label.LabelChildComponent;
import net.dv8tion.jda.internal.components.checkboxgroup.CheckboxGroupImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import org.jetbrains.annotations.Unmodifiable;

public interface CheckboxGroup extends ICustomId, LabelChildComponent {
   int CUSTOM_ID_MAX_LENGTH = 100;
   int OPTIONS_MAX_AMOUNT = 10;

   @Nonnull
   CheckboxGroup withUniqueId(int var1);

   @Nonnull
   String getCustomId();

   @Nonnull
   @Unmodifiable List<CheckboxGroupOption> getOptions();

   int getMinValues();

   int getMaxValues();

   boolean isRequired();

   @Nonnull
   static Builder create(@Nonnull String customId) {
      return new Builder(customId);
   }

   @Nonnull
   default Builder createCopy() {
      return create(this.getCustomId()).setUniqueId(this.getUniqueId()).setMinValues(this.getMinValues()).setMaxValues(this.getMaxValues()).addOptions((Collection)this.getOptions()).setRequired(this.isRequired());
   }

   public static class Builder {
      protected int uniqueId = -1;
      protected String customId;
      protected final List<CheckboxGroupOption> options = new ArrayList();
      protected int minValues = -1;
      protected int maxValues = -1;
      protected boolean required = true;

      protected Builder(@Nonnull String customId) {
         this.customId = customId;
      }

      public int getUniqueId() {
         return this.uniqueId;
      }

      @Nonnull
      public String getCustomId() {
         return this.customId;
      }

      public int getMinValues() {
         return this.minValues;
      }

      public int getMaxValues() {
         return this.maxValues;
      }

      public boolean isRequired() {
         return this.required;
      }

      @Nonnull
      public Builder setUniqueId(int uniqueId) {
         Checks.positive(uniqueId, "Unique ID");
         this.uniqueId = uniqueId;
         return this;
      }

      @Nonnull
      public Builder setCustomId(@Nonnull String customId) {
         Checks.notBlank(customId, "Custom ID");
         Checks.notLonger(customId, 100, "Custom ID");
         this.customId = customId;
         return this;
      }

      @Nonnull
      public Builder addOptions(@Nonnull CheckboxGroupOption... options) {
         Checks.noneNull(options, "Options");
         return this.addOptions((Collection)Arrays.asList(options));
      }

      @Nonnull
      public Builder addOptions(@Nonnull Collection<? extends CheckboxGroupOption> options) {
         Checks.noneNull(options, "Options");
         this.options.addAll(options);
         return this;
      }

      @Nonnull
      public Builder addOption(@Nonnull String label, @Nonnull String value) {
         return this.addOptions(CheckboxGroupOption.of(label, value));
      }

      @Nonnull
      public Builder addOption(@Nonnull String label, @Nonnull String value, @Nullable String description) {
         return this.addOptions(CheckboxGroupOption.of(label, value, description));
      }

      @Nonnull
      public Builder addOption(@Nonnull String label, @Nonnull String value, @Nullable String description, boolean isDefault) {
         return this.addOptions(CheckboxGroupOption.of(label, value, description, isDefault));
      }

      @Nonnull
      public List<CheckboxGroupOption> getOptions() {
         return this.options;
      }

      @Nonnull
      public Builder setMinValues(int minValues) {
         Checks.notNegative(minValues, "Min values");
         Checks.check(minValues <= 10, "Min values cannot be greater than 10");
         this.minValues = minValues;
         return this;
      }

      @Nonnull
      public Builder setMaxValues(int maxValues) {
         Checks.positive(maxValues, "Max values");
         Checks.check(maxValues <= 10, "Max values cannot be greater than 10");
         this.maxValues = maxValues;
         return this;
      }

      @Nonnull
      public Builder setRequiredRange(int minValues, int maxValues) {
         Checks.check(minValues <= maxValues, "Min values (%d) cannot be greater than max values (%d)", minValues, maxValues);
         return this.setMinValues(minValues).setMaxValues(maxValues);
      }

      @Nonnull
      public Builder setRequired(boolean required) {
         this.required = required;
         return this;
      }

      @Nonnull
      public Builder setSelectedValues(@Nonnull Collection<String> values) {
         Checks.noneNull(values, "Values");
         ListIterator<CheckboxGroupOption> iterator = this.options.listIterator();

         while(iterator.hasNext()) {
            CheckboxGroupOption option = (CheckboxGroupOption)iterator.next();
            boolean shouldBeDefault = values.contains(option.getValue());
            iterator.set(option.withDefault(shouldBeDefault));
         }

         return this;
      }

      @Nonnull
      public Builder setSelectedValues(@Nonnull String... values) {
         Checks.noneNull(values, "Values");
         return this.setSelectedValues(Arrays.asList(values));
      }

      @Nonnull
      public Builder setSelectedOptions(@Nonnull CheckboxGroupOption... options) {
         Checks.noneNull(options, "Options");
         return this.setSelectedValues((Collection)Arrays.stream(options).map(CheckboxGroupOption::getValue).collect(Collectors.toList()));
      }

      @Nonnull
      public Builder setSelectedOptions(@Nonnull Collection<CheckboxGroupOption> options) {
         Checks.noneNull(options, "Options");
         return this.setSelectedValues((Collection)options.stream().map(CheckboxGroupOption::getValue).collect(Collectors.toList()));
      }

      @Nonnull
      public CheckboxGroup build() {
         if (this.options.isEmpty()) {
            throw new IllegalStateException("Cannot build a checkbox group with no options");
         } else if (this.options.size() > 10) {
            throw new IllegalStateException("Cannot build a checkbox group with more than 10 options");
         } else if (this.minValues != -1 && this.maxValues != -1 && this.minValues > this.maxValues) {
            throw new IllegalStateException(Helpers.format("Min values (%d) cannot be greater than max values (%d)", this.minValues, this.maxValues));
         } else if (this.minValues == 0 && this.required) {
            throw new IllegalStateException("A checkbox group cannot have min values set to 0 and be required at the same time");
         } else {
            int min = Math.min(this.minValues, this.options.size());
            int max = Math.min(this.maxValues, this.options.size());
            return new CheckboxGroupImpl(this.uniqueId, this.customId, this.options, min, max, this.required);
         }
      }
   }
}
