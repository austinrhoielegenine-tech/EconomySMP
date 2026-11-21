package net.dv8tion.jda.api.requests.restaction.interactions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.requests.RestAction;

public interface InteractionCallbackAction<T> extends RestAction<T> {
   @Nonnull
   @CheckReturnValue
   InteractionCallbackAction<T> closeResources();

   public static enum ResponseType {
      CHANNEL_MESSAGE_WITH_SOURCE(4),
      DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE(5),
      DEFERRED_MESSAGE_UPDATE(6),
      MESSAGE_UPDATE(7),
      COMMAND_AUTOCOMPLETE_CHOICES(8),
      MODAL(9),
      UNKNOWN(-1);

      private final int raw;

      private ResponseType(int raw) {
         this.raw = raw;
      }

      public int getRaw() {
         return this.raw;
      }

      @Nonnull
      public static ResponseType fromId(int id) {
         for(ResponseType type : values()) {
            if (type.raw == id) {
               return type;
            }
         }

         return UNKNOWN;
      }

      // $FF: synthetic method
      private static ResponseType[] $values() {
         return new ResponseType[]{CHANNEL_MESSAGE_WITH_SOURCE, DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE, DEFERRED_MESSAGE_UPDATE, MESSAGE_UPDATE, COMMAND_AUTOCOMPLETE_CHOICES, MODAL, UNKNOWN};
      }
   }
}
