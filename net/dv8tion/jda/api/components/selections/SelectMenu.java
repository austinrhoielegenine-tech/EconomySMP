package net.dv8tion.jda.api.components.selections;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.label.LabelChildComponent;
import net.dv8tion.jda.internal.utils.Checks;

public interface SelectMenu extends ActionComponent, ActionRowChildComponent, LabelChildComponent {
   int ID_MAX_LENGTH = 100;
   int PLACEHOLDER_MAX_LENGTH = 100;
   int OPTIONS_MAX_AMOUNT = 25;

   @Nonnull
   SelectMenu withDisabled(boolean var1);

   @Nonnull
   default SelectMenu asDisabled() {
      return (SelectMenu)ActionComponent.super.asDisabled();
   }

   @Nonnull
   default SelectMenu asEnabled() {
      return (SelectMenu)ActionComponent.super.asEnabled();
   }

   @Nonnull
   SelectMenu withUniqueId(int var1);

   @Nonnull
   String getCustomId();

   @Nullable
   String getPlaceholder();

   int getMinValues();

   int getMaxValues();

   @Nullable
   Boolean isRequired();

   @Nonnull
   @CheckReturnValue
   Builder<? extends SelectMenu, ? extends Builder<?, ?>> createCopy();

   public abstract static class Builder<T extends SelectMenu, B extends Builder<T, B>> {
      protected String customId;
      protected int uniqueId = -1;
      protected String placeholder;
      protected int minValues = 1;
      protected int maxValues = 1;
      protected boolean disabled = false;
      protected Boolean required = null;

      protected Builder(@Nonnull String customId) {
         this.setCustomId(customId);
      }

      @Nonnull
      public B setCustomId(@Nonnull String customId) {
         Checks.notEmpty(customId, "Component ID");
         Checks.notLonger(customId, 100, "Component ID");
         this.customId = customId;
         return (B)this;
      }

      @Nonnull
      public B setUniqueId(int uniqueId) {
         Checks.positive(uniqueId, "Unique ID");
         this.uniqueId = uniqueId;
         return (B)this;
      }

      @Nonnull
      public B setPlaceholder(@Nullable String placeholder) {
         if (placeholder != null) {
            Checks.notEmpty(placeholder, "Placeholder");
            Checks.notLonger(placeholder, 100, "Placeholder");
         }

         this.placeholder = placeholder;
         return (B)this;
      }

      @Nonnull
      public B setMinValues(int minValues) {
         Checks.notNegative(minValues, "Min Values");
         Checks.check(minValues <= 25, "Min Values may not be greater than %d! Provided: %d", 25, minValues);
         this.minValues = minValues;
         return (B)this;
      }

      @Nonnull
      public B setMaxValues(int maxValues) {
         Checks.positive(maxValues, "Max Values");
         Checks.check(maxValues <= 25, "Max Values may not be greater than %d! Provided: %d", 25, maxValues);
         this.maxValues = maxValues;
         return (B)this;
      }

      @Nonnull
      public B setRequiredRange(int min, int max) {
         Checks.check(min <= max, "Min Values should be less than or equal to Max Values! Provided: [%d, %d]", min, max);
         return (B)this.setMinValues(min).setMaxValues(max);
      }

      @Nonnull
      public B setDisabled(boolean disabled) {
         this.disabled = disabled;
         return (B)this;
      }

      @Nonnull
      public B setRequired(@Nullable Boolean required) {
         this.required = required;
         return (B)this;
      }

      @Nonnull
      public String getCustomId() {
         return this.customId;
      }

      public int getUniqueId() {
         return this.uniqueId;
      }

      @Nullable
      public String getPlaceholder() {
         return this.placeholder;
      }

      public int getMinValues() {
         return this.minValues;
      }

      public int getMaxValues() {
         return this.maxValues;
      }

      public boolean isDisabled() {
         return this.disabled;
      }

      @Nullable
      public Boolean isRequired() {
         return this.required;
      }

      @Nonnull
      public abstract T build();
   }
}
