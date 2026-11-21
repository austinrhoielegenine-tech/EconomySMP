package net.dv8tion.jda.internal.requests.restaction.pagination;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PinnedMessagePaginationAction;
import net.dv8tion.jda.api.utils.TimeUtil;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.ReceivedMessage;

public class PinnedMessagePaginationActionImpl extends PaginationActionImpl<PinnedMessagePaginationAction.PinnedMessage, PinnedMessagePaginationAction> implements PinnedMessagePaginationAction {
   protected final MessageChannel channel;

   public PinnedMessagePaginationActionImpl(MessageChannel channel) {
      super(channel.getJDA(), Route.Messages.GET_MESSAGE_PINS.compile(channel.getId()), 1, 50, 50);
      this.channel = channel;
   }

   @Nonnull
   public EnumSet<PaginationAction.PaginationOrder> getSupportedOrders() {
      return EnumSet.of(PaginationAction.PaginationOrder.BACKWARD);
   }

   protected long getKey(PinnedMessagePaginationAction.PinnedMessage it) {
      OffsetDateTime timestamp = it.getTimePinned();
      long epochMillis = timestamp.toInstant().toEpochMilli();
      return TimeUtil.getDiscordTimestamp(epochMillis);
   }

   @Nonnull
   protected String getPaginationLastEvaluatedKey(long lastId, PinnedMessagePaginationAction.PinnedMessage last) {
      return last == null ? OffsetDateTime.now(ZoneOffset.UTC).toString() : last.getTimePinned().toString();
   }

   protected void handleSuccess(Response response, Request<List<PinnedMessagePaginationAction.PinnedMessage>> request) {
      DataObject object = response.getObject();
      DataArray items = object.getArray("items");
      EntityBuilder entityBuilder = this.api.getEntityBuilder();
      List<PinnedMessagePaginationAction.PinnedMessage> messages = new ArrayList(items.length());

      for(int i = 0; i < items.length(); ++i) {
         try {
            DataObject item = items.getObject(i);
            ReceivedMessage message = entityBuilder.createMessageWithChannel(item.getObject("message"), this.channel, false);
            OffsetDateTime pinnedAt = item.getOffsetDateTime("pinned_at");
            PinnedMessagePaginationAction.PinnedMessage pinnedMessage = new PinnedMessagePaginationAction.PinnedMessage(pinnedAt, message);
            messages.add(pinnedMessage);
            this.last = pinnedMessage;
            this.lastKey = this.getKey(this.last);
            if (this.useCache) {
               this.cached.add(pinnedMessage);
            }
         } catch (Exception e) {
            EntityBuilder.LOG.error((String)"Failed to parse pinned message", (Throwable)e);
         }
      }

      request.onSuccess(messages);
   }
}
