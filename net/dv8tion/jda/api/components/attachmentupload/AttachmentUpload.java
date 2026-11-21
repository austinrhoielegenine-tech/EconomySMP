package net.dv8tion.jda.api.components.attachmentupload;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.attribute.ICustomId;
import net.dv8tion.jda.api.components.label.LabelChildComponent;
import net.dv8tion.jda.internal.components.attachmentupload.AttachmentUploadImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface AttachmentUpload extends Component, ICustomId, LabelChildComponent {
   int MAX_UPLOADS = 10;
   int ID_MAX_LENGTH = 100;

   @Nonnull
   static Builder create(@Nonnull String customId) {
      return new Builder(customId);
   }

   @Nonnull
   static AttachmentUpload of(@Nonnull String customId) {
      return create(customId).build();
   }

   @Nonnull
   @CheckReturnValue
   AttachmentUpload withUniqueId(int var1);

   int getMinValues();

   int getMaxValues();

   boolean isRequired();

   public static class Builder {
      protected int uniqueId = -1;
      protected String customId;
      protected int minValues = 1;
      protected int maxValues = 1;
      protected boolean required = true;

      protected Builder(@Nonnull String customId) {
         this.setCustomId(customId);
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
      public Builder setMinValues(int minValues) {
         Checks.notNegative(minValues, "Min values");
         Checks.check(minValues <= 10, "Min values (%s) must be lower than %s", minValues, 10);
         this.minValues = minValues;
         return this;
      }

      @Nonnull
      public Builder setMaxValues(int maxValues) {
         Checks.positive(maxValues, "Max values");
         Checks.check(maxValues <= 10, "Max values (%s) must be lower than %s", maxValues, 10);
         this.maxValues = maxValues;
         return this;
      }

      @Nonnull
      public Builder setRequiredRange(int min, int max) {
         return this.setMinValues(min).setMaxValues(max);
      }

      @Nonnull
      public Builder setRequired(boolean required) {
         this.required = required;
         return this;
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
      public AttachmentUpload build() {
         Checks.check(this.maxValues >= this.minValues, "Max (%s) must be higher or equal to min (%s)", this.maxValues, this.minValues);
         return new AttachmentUploadImpl(this.uniqueId, this.customId, this.minValues, this.maxValues, this.required);
      }
   }
}
