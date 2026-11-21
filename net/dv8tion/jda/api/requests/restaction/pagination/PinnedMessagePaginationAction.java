package net.dv8tion.jda.api.requests.restaction.pagination;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.utils.EntityString;

public interface PinnedMessagePaginationAction extends PaginationAction<PinnedMessage, PinnedMessagePaginationAction> {
   public static class PinnedMessage {
      private final OffsetDateTime pinnedAt;
      private final Message message;

      public PinnedMessage(OffsetDateTime pinnedAt, Message message) {
         this.pinnedAt = pinnedAt;
         this.message = message;
      }

      @Nonnull
      public OffsetDateTime getTimePinned() {
         return this.pinnedAt;
      }

      @Nonnull
      public Message getMessage() {
         return this.message;
      }

      public String toString() {
         return (new EntityString(this)).addMetadata("pinned_at", this.pinnedAt).addMetadata("message", this.message).toString();
      }
   }
}
