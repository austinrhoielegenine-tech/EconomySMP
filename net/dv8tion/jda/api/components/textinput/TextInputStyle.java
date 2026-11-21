package net.dv8tion.jda.api.components.textinput;

import javax.annotation.Nonnull;

public enum TextInputStyle {
   UNKNOWN(-1),
   SHORT(1),
   PARAGRAPH(2);

   private final int key;

   private TextInputStyle(int type) {
      this.key = type;
   }

   public int getRaw() {
      return this.key;
   }

   @Nonnull
   public static TextInputStyle fromKey(int key) {
      for(TextInputStyle style : values()) {
         if (style.key == key) {
            return style;
         }
      }

      return UNKNOWN;
   }

   // $FF: synthetic method
   private static TextInputStyle[] $values() {
      return new TextInputStyle[]{UNKNOWN, SHORT, PARAGRAPH};
   }
}
