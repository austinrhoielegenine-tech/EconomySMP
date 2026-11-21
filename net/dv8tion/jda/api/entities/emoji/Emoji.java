package net.dv8tion.jda.api.entities.emoji;

import java.util.Formattable;
import java.util.Formatter;
import java.util.regex.Matcher;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl;
import net.dv8tion.jda.internal.entities.emoji.UnicodeEmojiImpl;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.EncodingUtil;

public interface Emoji extends SerializableData, Formattable {
   @Nonnull
   static UnicodeEmoji fromUnicode(@Nonnull String code) {
      Checks.notEmpty(code, "Unicode");
      if (code.startsWith("U+") || code.startsWith("u+")) {
         StringBuilder emoji = new StringBuilder();
         String[] codepoints = code.trim().split("\\s*[uU]\\+");

         for(String codepoint : codepoints) {
            emoji.append(codepoint.isEmpty() ? "" : EncodingUtil.decodeCodepoint("U+" + codepoint));
         }

         code = emoji.toString();
      }

      return new UnicodeEmojiImpl(code);
   }

   @Nonnull
   static CustomEmoji fromCustom(@Nonnull String name, long id, boolean animated) {
      Checks.notEmpty(name, "Name");
      return new CustomEmojiImpl(name, id, animated);
   }

   @Nonnull
   static CustomEmoji fromCustom(@Nonnull CustomEmoji emoji) {
      Checks.notNull(emoji, "Emoji");
      return fromCustom(emoji.getName(), emoji.getIdLong(), emoji.isAnimated());
   }

   @Nonnull
   static EmojiUnion fromFormatted(@Nonnull String code) {
      Checks.notEmpty(code, "Formatting Code");
      Matcher matcher = Message.MentionType.EMOJI.getPattern().matcher(code);
      return matcher.matches() ? (EmojiUnion)fromCustom(matcher.group(1), Long.parseUnsignedLong(matcher.group(2)), code.startsWith("<a")) : (EmojiUnion)fromUnicode(code);
   }

   @Nonnull
   static EmojiUnion fromData(@Nonnull DataObject emoji) {
      Checks.notNull(emoji, "Emoji Data");
      return emoji.isNull("id") ? (EmojiUnion)fromUnicode(emoji.getString("name")) : (EmojiUnion)fromCustom(emoji.getString("name"), emoji.getUnsignedLong("id"), emoji.getBoolean("animated"));
   }

   @Nonnull
   Type getType();

   @Nonnull
   String getName();

   @Nonnull
   String getAsReactionCode();

   @Nonnull
   String getFormatted();

   default void formatTo(Formatter formatter, int flags, int width, int precision) {
      boolean leftJustified = (flags & 1) == 1;
      String out = this.getFormatted();
      MiscUtil.appendTo(formatter, width, precision, leftJustified, out);
   }

   public static enum Type {
      UNICODE,
      CUSTOM;

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{UNICODE, CUSTOM};
      }
   }
}
