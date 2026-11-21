package net.dv8tion.jda.internal.requests.restaction.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ScheduledEventMembersPaginationAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class ScheduledEventMembersPaginationActionImpl extends PaginationActionImpl<Member, ScheduledEventMembersPaginationAction> implements ScheduledEventMembersPaginationAction {
   protected final Guild guild;

   public ScheduledEventMembersPaginationActionImpl(ScheduledEvent event) {
      super(event.getGuild().getJDA(), Route.Guilds.GET_SCHEDULED_EVENT_USERS.compile(event.getGuild().getId(), event.getId()).withQueryParams("with_member", "true"), 1, 100, 100);
      this.guild = event.getGuild();
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   protected void handleSuccess(Response response, Request<List<Member>> request) {
      DataArray array = response.getArray();
      List<Member> members = new ArrayList(array.length());
      EntityBuilder builder = this.api.getEntityBuilder();

      for(int i = 0; i < array.length(); ++i) {
         try {
            DataObject object = array.getObject(i);
            if (!object.isNull("member")) {
               DataObject userObject = object.getObject("user");
               DataObject memberObject = object.getObject("member");
               Member member = builder.createMember((GuildImpl)this.guild, memberObject.put("user", userObject));
               members.add(member);
            }
         } catch (NullPointerException | ParsingException e) {
            LOG.warn((String)"Encountered an exception in ScheduledEventPagination", (Throwable)e);
         }
      }

      if (this.order == PaginationAction.PaginationOrder.BACKWARD) {
         Collections.reverse(members);
      }

      if (this.useCache) {
         this.cached.addAll(members);
      }

      if (!members.isEmpty()) {
         this.last = (Member)members.get(members.size() - 1);
         this.lastKey = ((Member)this.last).getIdLong();
      }

      request.onSuccess(members);
   }

   protected long getKey(Member it) {
      return it.getIdLong();
   }
}
