package net.dv8tion.jda.api.components.buttons;

import javax.annotation.Nonnull;

public enum ButtonStyle {
   UNKNOWN(-1),
   PRIMARY(1),
   SECONDARY(2),
   SUCCESS(3),
   DANGER(4),
   LINK(5),
   PREMIUM(6);

   private final int key;

   private ButtonStyle(int key) {
      this.key = key;
   }

   public int getKey() {
      return this.key;
   }

   @Nonnull
   public static ButtonStyle fromKey(int key) {
      for(ButtonStyle style : values()) {
         if (style.key == key) {
            return style;
         }
      }

      return UNKNOWN;
   }

   // $FF: synthetic method
   private static ButtonStyle[] $values() {
      return new ButtonStyle[]{UNKNOWN, PRIMARY, SECONDARY, SUCCESS, DANGER, LINK, PREMIUM};
   }
}
