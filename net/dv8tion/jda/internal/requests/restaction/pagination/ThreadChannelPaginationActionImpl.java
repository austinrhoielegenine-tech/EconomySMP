package net.dv8tion.jda.internal.requests.restaction.pagination;

import gnu.trove.map.TLongObjectMap;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadChannelPaginationAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.utils.Helpers;

public class ThreadChannelPaginationActionImpl extends PaginationActionImpl<ThreadChannel, ThreadChannelPaginationAction> implements ThreadChannelPaginationAction {
   protected final IThreadContainer channel;
   protected final boolean useID;

   public ThreadChannelPaginationActionImpl(JDA api, Route.CompiledRoute route, IThreadContainer channel, boolean useID) {
      super(api, route, 2, 100, 100);
      this.channel = channel;
      this.useID = useID;
   }

   @Nonnull
   public IThreadContainerUnion getChannel() {
      return (IThreadContainerUnion)this.channel;
   }

   @Nonnull
   public EnumSet<PaginationAction.PaginationOrder> getSupportedOrders() {
      return EnumSet.of(PaginationAction.PaginationOrder.BACKWARD);
   }

   @Nonnull
   protected String getPaginationLastEvaluatedKey(long lastId, ThreadChannel last) {
      if (this.useID) {
         return Long.toUnsignedString(lastId);
      } else if (this.order == PaginationAction.PaginationOrder.FORWARD && lastId == 0L) {
         return "2015-01-01T00:00:00.000";
      } else {
         return last == null ? OffsetDateTime.now(ZoneOffset.UTC).toString() : last.getTimeArchiveInfoLastModified().toString();
      }
   }

   protected void handleSuccess(Response response, Request<List<ThreadChannel>> request) {
      DataObject obj = response.getObject();
      DataArray selfThreadMembers = obj.getArray("members");
      DataArray threads = obj.getArray("threads");
      List<ThreadChannel> list = new ArrayList(threads.length());
      EntityBuilder builder = this.api.getEntityBuilder();
      TLongObjectMap<DataObject> selfThreadMemberMap = Helpers.convertToMap((o) -> o.getUnsignedLong("id"), selfThreadMembers);

      for(int i = 0; i < threads.length(); ++i) {
         try {
            DataObject threadObj = threads.getObject(i);
            DataObject selfThreadMemberObj = selfThreadMemberMap.get(threadObj.getLong("id", 0L));
            if (selfThreadMemberObj != null) {
               threadObj.put("member", selfThreadMemberObj);
            }

            try {
               ThreadChannel thread = builder.createThreadChannel(threadObj, this.getGuild().getIdLong());
               list.add(thread);
               if (this.useCache) {
                  this.cached.add(thread);
               }

               this.last = thread;
               this.lastKey = ((ThreadChannel)this.last).getIdLong();
            } catch (Exception e) {
               if ("MISSING_CHANNEL".equals(e.getMessage())) {
                  EntityBuilder.LOG.debug((String)"Discarding thread without cached parent channel. JSON: {}", (Object)threadObj);
               } else {
                  EntityBuilder.LOG.warn((String)"Failed to create thread channel. JSON: {}", (Object)threadObj, (Object)e);
               }
            }
         } catch (NullPointerException | ParsingException e) {
            LOG.warn((String)"Encountered exception in ThreadChannelPagination", (Throwable)e);
         }
      }

      request.onSuccess(list);
   }

   protected long getKey(ThreadChannel it) {
      return it.getIdLong();
   }
}
