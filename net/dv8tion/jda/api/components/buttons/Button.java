package net.dv8tion.jda.api.components.buttons;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent;
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent;
import net.dv8tion.jda.api.entities.SkuSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.internal.components.buttons.ButtonImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface Button extends ActionComponent, ActionRowChildComponent, SectionAccessoryComponent {
   int LABEL_MAX_LENGTH = 80;
   int ID_MAX_LENGTH = 100;
   int URL_MAX_LENGTH = 512;

   @Nonnull
   String getLabel();

   @Nonnull
   ButtonStyle getStyle();

   @Nullable
   String getUrl();

   @Nullable
   SkuSnowflake getSku();

   @Nullable
   EmojiUnion getEmoji();

   @Nonnull
   @CheckReturnValue
   default Button asDisabled() {
      return (Button)ActionComponent.super.asDisabled();
   }

   @Nonnull
   @CheckReturnValue
   default Button asEnabled() {
      return (Button)ActionComponent.super.asEnabled();
   }

   @Nonnull
   @CheckReturnValue
   default Button withDisabled(boolean disabled) {
      return (new ButtonImpl(this.getCustomId(), this.getUniqueId(), this.getLabel(), this.getStyle(), this.getUrl(), this.getSku(), disabled, this.getEmoji())).checkValid();
   }

   @Nonnull
   @CheckReturnValue
   default Button withEmoji(@Nullable Emoji emoji) {
      return (new ButtonImpl(this.getCustomId(), this.getUniqueId(), this.getLabel(), this.getStyle(), this.getUrl(), this.getSku(), this.isDisabled(), emoji)).checkValid();
   }

   @Nonnull
   @CheckReturnValue
   default Button withLabel(@Nonnull String label) {
      return (new ButtonImpl(this.getCustomId(), this.getUniqueId(), label, this.getStyle(), this.getUrl(), this.getSku(), this.isDisabled(), this.getEmoji())).checkValid();
   }

   @Nonnull
   @CheckReturnValue
   default Button withCustomId(@Nonnull String customId) {
      return (new ButtonImpl(customId, this.getUniqueId(), this.getLabel(), this.getStyle(), this.getUrl(), this.getSku(), this.isDisabled(), this.getEmoji())).checkValid();
   }

   @Nonnull
   @CheckReturnValue
   default Button withUniqueId(int uniqueId) {
      Checks.positive(uniqueId, "Unique ID");
      return (new ButtonImpl(this.getCustomId(), uniqueId, this.getLabel(), this.getStyle(), this.getUrl(), this.getSku(), this.isDisabled(), this.getEmoji())).checkValid();
   }

   @Nonnull
   @CheckReturnValue
   default Button withUrl(@Nonnull String url) {
      return (new ButtonImpl(this.getCustomId(), this.getUniqueId(), this.getLabel(), this.getStyle(), url, this.getSku(), this.isDisabled(), this.getEmoji())).checkValid();
   }

   @Nonnull
   @CheckReturnValue
   default Button withSku(@Nonnull SkuSnowflake sku) {
      return (new ButtonImpl(this.getCustomId(), this.getUniqueId(), this.getLabel(), this.getStyle(), this.getUrl(), sku, this.isDisabled(), this.getEmoji())).checkValid();
   }

   @Nonnull
   @CheckReturnValue
   default Button withStyle(@Nonnull ButtonStyle style) {
      Checks.notNull(style, "Style");
      Checks.check(style != ButtonStyle.UNKNOWN, "Cannot make button with unknown style!");
      if (this.getStyle() == ButtonStyle.LINK && style != ButtonStyle.LINK) {
         throw new IllegalArgumentException("You cannot change a link button to another style!");
      } else if (this.getStyle() != ButtonStyle.LINK && style == ButtonStyle.LINK) {
         throw new IllegalArgumentException("You cannot change a styled button to a link button!");
      } else if (this.getStyle() == ButtonStyle.PREMIUM && style != ButtonStyle.PREMIUM) {
         throw new IllegalArgumentException("You cannot change a premium button to another style!");
      } else if (this.getStyle() != ButtonStyle.PREMIUM && style == ButtonStyle.PREMIUM) {
         throw new IllegalArgumentException("You cannot change a styled button to a premium button!");
      } else {
         return (new ButtonImpl(this.getCustomId(), this.getUniqueId(), this.getLabel(), style, this.getUrl(), this.getSku(), this.isDisabled(), this.getEmoji())).checkValid();
      }
   }

   @Nonnull
   static Button primary(@Nonnull String id, @Nonnull String label) {
      return (new ButtonImpl(id, label, ButtonStyle.PRIMARY, false, (Emoji)null)).checkValid();
   }

   @Nonnull
   static Button primary(@Nonnull String id, @Nonnull Emoji emoji) {
      return (new ButtonImpl(id, (String)null, ButtonStyle.PRIMARY, false, emoji)).checkValid();
   }

   @Nonnull
   static Button secondary(@Nonnull String id, @Nonnull String label) {
      return (new ButtonImpl(id, label, ButtonStyle.SECONDARY, false, (Emoji)null)).checkValid();
   }

   @Nonnull
   static Button secondary(@Nonnull String id, @Nonnull Emoji emoji) {
      return (new ButtonImpl(id, (String)null, ButtonStyle.SECONDARY, false, emoji)).checkValid();
   }

   @Nonnull
   static Button success(@Nonnull String id, @Nonnull String label) {
      return (new ButtonImpl(id, label, ButtonStyle.SUCCESS, false, (Emoji)null)).checkValid();
   }

   @Nonnull
   static Button success(@Nonnull String id, @Nonnull Emoji emoji) {
      return (new ButtonImpl(id, (String)null, ButtonStyle.SUCCESS, false, emoji)).checkValid();
   }

   @Nonnull
   static Button danger(@Nonnull String id, @Nonnull String label) {
      return (new ButtonImpl(id, label, ButtonStyle.DANGER, false, (Emoji)null)).checkValid();
   }

   @Nonnull
   static Button danger(@Nonnull String id, @Nonnull Emoji emoji) {
      return (new ButtonImpl(id, (String)null, ButtonStyle.DANGER, false, emoji)).checkValid();
   }

   @Nonnull
   static Button link(@Nonnull String url, @Nonnull String label) {
      return (new ButtonImpl((String)null, label, ButtonStyle.LINK, url, (SkuSnowflake)null, false, (Emoji)null)).checkValid();
   }

   @Nonnull
   static Button link(@Nonnull String url, @Nonnull Emoji emoji) {
      return (new ButtonImpl((String)null, (String)null, ButtonStyle.LINK, url, (SkuSnowflake)null, false, emoji)).checkValid();
   }

   @Nonnull
   static Button premium(@Nonnull SkuSnowflake sku) {
      return (new ButtonImpl((String)null, (String)null, ButtonStyle.PREMIUM, (String)null, sku, false, (Emoji)null)).checkValid();
   }

   @Nonnull
   static Button of(@Nonnull ButtonStyle style, @Nonnull String idOrUrl, @Nonnull String label) {
      Checks.check(style != ButtonStyle.PREMIUM, "Premium buttons don't support labels");
      return (Button)(style == ButtonStyle.LINK ? link(idOrUrl, label) : (new ButtonImpl(idOrUrl, label, style, false, (Emoji)null)).checkValid());
   }

   @Nonnull
   static Button of(@Nonnull ButtonStyle style, @Nonnull String idOrUrl, @Nonnull Emoji emoji) {
      Checks.check(style != ButtonStyle.PREMIUM, "Premium buttons don't support emojis");
      return (Button)(style == ButtonStyle.LINK ? link(idOrUrl, emoji) : (new ButtonImpl(idOrUrl, (String)null, style, false, emoji)).checkValid());
   }

   @Nonnull
   static Button of(@Nonnull ButtonStyle style, @Nonnull String idOrUrlOrSku, @Nullable String label, @Nullable Emoji emoji) {
      Checks.notNull(style, "ButtonStyle");
      switch (style) {
         case LINK:
            return (new ButtonImpl((String)null, label, style, idOrUrlOrSku, (SkuSnowflake)null, false, emoji)).checkValid();
         case PREMIUM:
            return (new ButtonImpl((String)null, label, style, (String)null, SkuSnowflake.fromId(idOrUrlOrSku), false, emoji)).checkValid();
         default:
            return (new ButtonImpl(idOrUrlOrSku, label, style, (String)null, (SkuSnowflake)null, false, emoji)).checkValid();
      }
   }
}
