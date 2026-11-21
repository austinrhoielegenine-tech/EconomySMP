package net.dv8tion.jda.internal.components.buttons;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponentUnion;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.section.SectionAccessoryComponentUnion;
import net.dv8tion.jda.api.entities.SkuSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.components.AbstractComponentImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EntityString;

public class ButtonImpl extends AbstractComponentImpl implements Button, ActionRowChildComponentUnion, SectionAccessoryComponentUnion {
   private final String customId;
   private final int uniqueId;
   private final String label;
   private final ButtonStyle style;
   private final String url;
   private final SkuSnowflake sku;
   private final boolean disabled;
   private final EmojiUnion emoji;

   public ButtonImpl(DataObject data) {
      this(data.getString("custom_id", (String)null), data.getInt("id", -1), data.getString("label", ""), ButtonStyle.fromKey(data.getInt("style")), data.getString("url", (String)null), data.hasKey("sku_id") ? SkuSnowflake.fromId(data.getLong("sku_id")) : null, data.getBoolean("disabled"), (Emoji)data.optObject("emoji").map(EntityBuilder::createEmoji).orElse((Object)null));
   }

   public ButtonImpl(String customId, String label, ButtonStyle style, boolean disabled, Emoji emoji) {
      this(customId, label, style, (String)null, (SkuSnowflake)null, disabled, emoji);
   }

   public ButtonImpl(String customId, String label, ButtonStyle style, String url, SkuSnowflake sku, boolean disabled, Emoji emoji) {
      this(customId, -1, label, style, url, sku, disabled, emoji);
   }

   public ButtonImpl(String customId, int uniqueId, String label, ButtonStyle style, String url, SkuSnowflake sku, boolean disabled, Emoji emoji) {
      this.customId = customId;
      this.uniqueId = uniqueId;
      this.label = label == null ? "" : label;
      this.style = style;
      this.url = url;
      this.sku = sku;
      this.disabled = disabled;
      this.emoji = (EmojiUnion)emoji;
   }

   public ButtonImpl checkValid() {
      Checks.notNull(this.style, "Style");
      Checks.notLonger(this.label, 80, "Label");
      switch (this.style) {
         case PRIMARY:
         case SECONDARY:
         case SUCCESS:
         case DANGER:
            Checks.check(this.url == null, "Cannot set an URL on action buttons");
            Checks.check(this.sku == null, "Cannot set an SKU on action buttons");
            Checks.check(this.emoji != null || !this.label.isEmpty(), "Action buttons must have either an emoji or label");
            Checks.notEmpty(this.customId, "Id");
            Checks.notLonger(this.customId, 100, "Id");
            break;
         case LINK:
            Checks.check(this.customId == null, "Cannot set an ID on link buttons");
            Checks.check(this.url != null, "You must set an URL on link buttons");
            Checks.check(this.sku == null, "Cannot set an SKU on link buttons");
            Checks.check(this.emoji != null || !this.label.isEmpty(), "Link buttons must have either an emoji or label");
            Checks.notEmpty(this.url, "URL");
            Checks.notLonger(this.url, 512, "URL");
            break;
         case PREMIUM:
            Checks.check(this.customId == null, "Cannot set an ID on premium buttons");
            Checks.check(this.url == null, "Cannot set an URL on premium buttons");
            Checks.check(this.emoji == null, "Cannot set an emoji on premium buttons");
            Checks.check(this.label.isEmpty(), "Cannot set a label on premium buttons");
            Checks.notNull(this.sku, "SKU");
            break;
         case UNKNOWN:
            throw new IllegalArgumentException("Cannot make button with unknown style!");
      }

      return this;
   }

   @Nonnull
   public Component.Type getType() {
      return Component.Type.BUTTON;
   }

   @Nonnull
   public ButtonImpl withUniqueId(int uniqueId) {
      return (ButtonImpl)Button.super.withUniqueId(uniqueId);
   }

   @Nullable
   public String getCustomId() {
      return this.customId;
   }

   public int getUniqueId() {
      return this.uniqueId;
   }

   @Nonnull
   public String getLabel() {
      return this.label;
   }

   @Nonnull
   public ButtonStyle getStyle() {
      return this.style;
   }

   @Nullable
   public String getUrl() {
      return this.url;
   }

   @Nullable
   public SkuSnowflake getSku() {
      return this.sku;
   }

   @Nullable
   public EmojiUnion getEmoji() {
      return this.emoji;
   }

   public boolean isDisabled() {
      return this.disabled;
   }

   @Nonnull
   public DataObject toData() {
      DataObject json = DataObject.empty();
      json.put("type", 2);
      if (!this.label.isEmpty()) {
         json.put("label", this.label);
      }

      json.put("style", this.style.getKey());
      json.put("disabled", this.disabled);
      if (this.emoji != null) {
         json.put("emoji", this.emoji);
      }

      if (this.url != null) {
         json.put("url", this.url);
      } else if (this.customId != null) {
         json.put("custom_id", this.customId);
      } else {
         json.put("sku_id", this.sku.getId());
      }

      if (this.uniqueId >= 0) {
         json.put("id", this.uniqueId);
      }

      return json;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.customId, this.label, this.style, this.url, this.sku, this.disabled, this.emoji});
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ButtonImpl)) {
         return false;
      } else {
         ButtonImpl other = (ButtonImpl)obj;
         return Objects.equals(other.customId, this.customId) && Objects.equals(other.label, this.label) && Objects.equals(other.url, this.url) && Objects.equals(other.sku, this.sku) && Objects.equals(other.emoji, this.emoji) && other.disabled == this.disabled && other.style == this.style;
      }
   }

   public String toString() {
      EntityString entityString = (new EntityString(this)).setName(this.label).addMetadata("id", this.uniqueId);
      if (this.customId != null) {
         entityString.addMetadata("custom id", this.customId);
      }

      if (this.url != null) {
         entityString.addMetadata("url", this.url);
      }

      if (this.sku != null) {
         entityString.addMetadata("sku", this.sku.getId());
      }

      return entityString.toString();
   }
}
