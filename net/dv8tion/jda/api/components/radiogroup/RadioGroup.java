package net.dv8tion.jda.api.components.radiogroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.attribute.ICustomId;
import net.dv8tion.jda.api.components.label.LabelChildComponent;
import net.dv8tion.jda.internal.components.radiogroup.RadioGroupImpl;
import net.dv8tion.jda.internal.utils.Checks;
import org.jetbrains.annotations.Unmodifiable;

public interface RadioGroup extends ICustomId, LabelChildComponent {
   int CUSTOM_ID_MAX_LENGTH = 100;
   int OPTIONS_MAX_AMOUNT = 10;

   @Nonnull
   RadioGroup withUniqueId(int var1);

   @Nonnull
   String getCustomId();

   @Nonnull
   @Unmodifiable List<RadioGroupOption> getOptions();

   boolean isRequired();

   @Nonnull
   static Builder create(@Nonnull String customId) {
      return new Builder(customId);
   }

   @Nonnull
   default Builder createCopy() {
      return create(this.getCustomId()).setUniqueId(this.getUniqueId()).addOptions((Collection)this.getOptions()).setRequired(this.isRequired());
   }

   public static class Builder {
      protected int uniqueId = -1;
      protected String customId;
      protected final List<RadioGroupOption> options = new ArrayList();
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
      public Builder addOptions(@Nonnull RadioGroupOption... options) {
         Checks.noneNull(options, "Options");
         return this.addOptions((Collection)Arrays.asList(options));
      }

      @Nonnull
      public Builder addOptions(@Nonnull Collection<? extends RadioGroupOption> options) {
         Checks.noneNull(options, "Options");
         this.options.addAll(options);
         return this;
      }

      @Nonnull
      public Builder addOption(@Nonnull String label, @Nonnull String value) {
         return this.addOptions(RadioGroupOption.of(label, value));
      }

      @Nonnull
      public Builder addOption(@Nonnull String label, @Nonnull String value, @Nullable String description) {
         return this.addOptions(RadioGroupOption.of(label, value, description));
      }

      @Nonnull
      public Builder addOption(@Nonnull String label, @Nonnull String value, @Nullable String description, boolean isDefault) {
         return this.addOptions(RadioGroupOption.of(label, value, description, isDefault));
      }

      @Nonnull
      public List<RadioGroupOption> getOptions() {
         return this.options;
      }

      @Nonnull
      public Builder setRequired(boolean required) {
         this.required = required;
         return this;
      }

      @Nonnull
      public Builder setSelectedValue(@Nullable String value) {
         ListIterator<RadioGroupOption> iterator = this.options.listIterator();

         while(iterator.hasNext()) {
            RadioGroupOption option = (RadioGroupOption)iterator.next();
            boolean shouldBeDefault = option.getValue().equals(value);
            iterator.set(option.withDefault(shouldBeDefault));
         }

         return this;
      }

      @Nonnull
      public Builder setSelectedOption(@Nullable RadioGroupOption option) {
         return option == null ? this.setSelectedValue((String)null) : this.setSelectedValue(option.getValue());
      }

      @Nonnull
      public RadioGroup build() {
         if (this.options.size() < 2) {
            throw new IllegalStateException("Cannot build a radio group with less than 2 options");
         } else if (this.options.size() > 10) {
            throw new IllegalStateException("Cannot build a radio group with more than 10 options");
         } else {
            this.checkUniqueDefault();
            return new RadioGroupImpl(this.uniqueId, this.customId, this.options, this.required);
         }
      }

      private void checkUniqueDefault() {
         List<String> selectedValues = new ArrayList();

         for(RadioGroupOption option : this.options) {
            if (option.isDefault()) {
               selectedValues.add(option.getValue());
            }
         }

         if (selectedValues.size() > 1) {
            throw new IllegalStateException("Cannot build a radio group with more than one option selected by default: " + selectedValues);
         }
      }
   }
}
