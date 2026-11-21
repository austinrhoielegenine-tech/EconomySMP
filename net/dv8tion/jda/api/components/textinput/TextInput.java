package net.dv8tion.jda.api.components.textinput;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.attribute.ICustomId;
import net.dv8tion.jda.api.components.label.LabelChildComponent;
import net.dv8tion.jda.internal.components.textinput.TextInputImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface TextInput extends ICustomId, LabelChildComponent {
   int MAX_VALUE_LENGTH = 4000;
   int MAX_ID_LENGTH = 100;
   int MAX_PLACEHOLDER_LENGTH = 100;

   @Nonnull
   TextInputStyle getStyle();

   @Nonnull
   String getCustomId();

   int getMinLength();

   int getMaxLength();

   boolean isRequired();

   @Nullable
   String getValue();

   @Nullable
   String getPlaceHolder();

   @Nonnull
   TextInput withUniqueId(int var1);

   @Nonnull
   default Component.Type getType() {
      return Component.Type.TEXT_INPUT;
   }

   @Nonnull
   static Builder create(@Nonnull String id, @Nonnull TextInputStyle style) {
      return new Builder(id, style);
   }

   @Nonnull
   static TextInput of(@Nonnull String id, @Nonnull TextInputStyle style) {
      return create(id, style).build();
   }

   public static class Builder {
      private String customId;
      private int uniqueId = -1;
      private String value;
      private String placeholder;
      private int minLength = -1;
      private int maxLength = -1;
      private TextInputStyle style;
      private boolean required = true;

      protected Builder(String customId, TextInputStyle style) {
         this.setCustomId(customId);
         this.setStyle(style);
      }

      @Nonnull
      public Builder setCustomId(@Nonnull String customId) {
         Checks.notBlank(customId, "Custom ID");
         Checks.notLonger(customId, 100, "Custom ID");
         this.customId = customId;
         return this;
      }

      @Nonnull
      public Builder setUniqueId(int uniqueId) {
         Checks.positive(uniqueId, "Unique ID");
         this.uniqueId = uniqueId;
         return this;
      }

      @Nonnull
      public Builder setStyle(@Nonnull TextInputStyle style) {
         Checks.notNull(style, "Style");
         Checks.check(style != TextInputStyle.UNKNOWN, "TextInputStyle cannot be UNKNOWN!");
         this.style = style;
         return this;
      }

      @Nonnull
      public Builder setRequired(boolean required) {
         this.required = required;
         return this;
      }

      @Nonnull
      public Builder setMinLength(int minLength) {
         if (minLength != -1) {
            Checks.notNegative(minLength, "Minimum length");
            Checks.check(minLength <= 4000, "Minimum length cannot be longer than %d characters!", (int)4000);
         }

         this.minLength = minLength;
         return this;
      }

      @Nonnull
      public Builder setMaxLength(int maxLength) {
         if (maxLength != -1) {
            Checks.check(maxLength >= 1, "Maximum length cannot be smaller than 1 character!");
            Checks.check(maxLength <= 4000, "Maximum length cannot be longer than %d characters!", (int)4000);
         }

         this.maxLength = maxLength;
         return this;
      }

      @Nonnull
      public Builder setRequiredRange(int min, int max) {
         if (min != -1 && max != -1 && min > max) {
            throw new IllegalArgumentException("minimum cannot be greater than maximum!");
         } else {
            this.setMinLength(min);
            this.setMaxLength(max);
            return this;
         }
      }

      @Nonnull
      public Builder setValue(@Nullable String value) {
         if (value != null) {
            Checks.notLonger(value, 4000, "Value");
            Checks.notBlank(value, "Value");
         }

         this.value = value;
         return this;
      }

      @Nonnull
      public Builder setPlaceholder(@Nullable String placeholder) {
         if (placeholder != null) {
            Checks.notLonger(placeholder, 100, "Placeholder");
            Checks.notBlank(placeholder, "Placeholder");
         }

         this.placeholder = placeholder;
         return this;
      }

      public int getMinLength() {
         return this.minLength;
      }

      public int getMaxLength() {
         return this.maxLength;
      }

      @Nonnull
      public String getCustomId() {
         return this.customId;
      }

      public int getUniqueId() {
         return this.uniqueId;
      }

      @Nonnull
      public TextInputStyle getStyle() {
         return this.style;
      }

      @Nullable
      public String getPlaceholder() {
         return this.placeholder;
      }

      @Nullable
      public String getValue() {
         return this.value;
      }

      public boolean isRequired() {
         return this.required;
      }

      @Nonnull
      public TextInput build() {
         if (this.maxLength < this.minLength && this.maxLength != -1) {
            throw new IllegalStateException("maxLength cannot be smaller than minLength!");
         } else {
            return new TextInputImpl(this.customId, this.uniqueId, this.style, this.minLength, this.maxLength, this.required, this.value, this.placeholder);
         }
      }
   }
}
