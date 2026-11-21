package net.dv8tion.jda.api.components;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;

public interface Component {
   @Nonnull
   Type getType();

   int getUniqueId();

   @Nonnull
   @CheckReturnValue
   Component withUniqueId(int var1);

   default boolean isMessageCompatible() {
      return this.getType().isMessageCompatible();
   }

   default boolean isModalCompatible() {
      return this.getType().isModalCompatible();
   }

   public static enum Type {
      UNKNOWN(-1, false, false),
      ACTION_ROW(1, true, false),
      BUTTON(2, true, false),
      STRING_SELECT(3, true, true),
      TEXT_INPUT(4, false, true),
      USER_SELECT(5, true, true),
      ROLE_SELECT(6, true, true),
      MENTIONABLE_SELECT(7, true, true),
      CHANNEL_SELECT(8, true, true),
      SECTION(9, true, false),
      TEXT_DISPLAY(10, true, true),
      THUMBNAIL(11, true, false),
      MEDIA_GALLERY(12, true, false),
      FILE_DISPLAY(13, true, false),
      SEPARATOR(14, true, false),
      CONTAINER(17, true, false),
      LABEL(18, false, true),
      FILE_UPLOAD(19, false, true),
      RADIO_GROUP(21, false, true),
      CHECKBOX_GROUP(22, false, true),
      CHECKBOX(23, false, true);

      private final int key;
      private final boolean messageCompatible;
      private final boolean modalCompatible;

      private Type(int key, boolean messageCompatible, boolean modalCompatible) {
         this.key = key;
         this.messageCompatible = messageCompatible;
         this.modalCompatible = modalCompatible;
      }

      public int getKey() {
         return this.key;
      }

      public boolean isMessageCompatible() {
         return this.messageCompatible;
      }

      public boolean isModalCompatible() {
         return this.modalCompatible;
      }

      public boolean isEntitySelectMenu() {
         return this == MENTIONABLE_SELECT || this == CHANNEL_SELECT || this == USER_SELECT || this == ROLE_SELECT;
      }

      @Nonnull
      public static Type fromKey(int type) {
         for(Type t : values()) {
            if (t.key == type) {
               return t;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{UNKNOWN, ACTION_ROW, BUTTON, STRING_SELECT, TEXT_INPUT, USER_SELECT, ROLE_SELECT, MENTIONABLE_SELECT, CHANNEL_SELECT, SECTION, TEXT_DISPLAY, THUMBNAIL, MEDIA_GALLERY, FILE_DISPLAY, SEPARATOR, CONTAINER, LABEL, FILE_UPLOAD, RADIO_GROUP, CHECKBOX_GROUP, CHECKBOX};
      }
   }
}
