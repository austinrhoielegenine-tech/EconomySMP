package net.dv8tion.jda.internal.requests.restaction.pagination;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PollVotersPaginationAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;

public class PollVotersPaginationActionImpl extends PaginationActionImpl<User, PollVotersPaginationAction> implements PollVotersPaginationAction {
   public PollVotersPaginationActionImpl(JDA jda, String channelId, String messageId, long answerId) {
      super(jda, Route.Messages.GET_POLL_ANSWER_VOTERS.compile(channelId, messageId, Long.toString(answerId)), 1, 100, 100);
      this.order = PaginationAction.PaginationOrder.FORWARD;
   }

   @Nonnull
   public EnumSet<PaginationAction.PaginationOrder> getSupportedOrders() {
      return EnumSet.of(PaginationAction.PaginationOrder.FORWARD);
   }

   protected long getKey(User it) {
      return it.getIdLong();
   }

   protected void handleSuccess(Response response, Request<List<User>> request) {
      DataArray array = response.getObject().getArray("users");
      List<User> users = new ArrayList(array.length());
      EntityBuilder builder = this.api.getEntityBuilder();

      for(int i = 0; i < array.length(); ++i) {
         try {
            DataObject object = array.getObject(i);
            users.add(builder.createUser(object));
         } catch (NullPointerException | ParsingException e) {
            LOG.warn((String)"Encountered an exception in PollVotersPaginationAction", (Throwable)e);
         }
      }

      if (!users.isEmpty()) {
         if (this.useCache) {
            this.cached.addAll(users);
         }

         this.last = (User)users.get(users.size() - 1);
         this.lastKey = ((User)this.last).getIdLong();
      }

      request.onSuccess(users);
   }
}
