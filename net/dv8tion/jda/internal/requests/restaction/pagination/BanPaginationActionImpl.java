package net.dv8tion.jda.internal.requests.restaction.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.pagination.BanPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;

public class BanPaginationActionImpl extends PaginationActionImpl<Guild.Ban, BanPaginationAction> implements BanPaginationAction {
   protected final Guild guild;

   public BanPaginationActionImpl(Guild guild) {
      super(guild.getJDA(), Route.Guilds.GET_BANS.compile(guild.getId()), 1, 1000, 1000);
      this.guild = guild;
      this.lastKey = Long.MAX_VALUE;
   }

   @Nonnull
   public BanPaginationAction order(@Nonnull PaginationAction.PaginationOrder order) {
      if (order == PaginationAction.PaginationOrder.BACKWARD && this.lastKey == 0L) {
         this.lastKey = Long.MAX_VALUE;
      } else if (order == PaginationAction.PaginationOrder.FORWARD && this.lastKey == Long.MAX_VALUE) {
         this.lastKey = 0L;
      }

      return (BanPaginationAction)super.order(order);
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   protected void handleSuccess(Response response, Request<List<Guild.Ban>> request) {
      EntityBuilder builder = this.api.getEntityBuilder();
      DataArray bannedArr = response.getArray();
      List<Guild.Ban> bans = new ArrayList(bannedArr.length());

      for(int i = 0; i < bannedArr.length(); ++i) {
         DataObject object = bannedArr.getObject(i);

         try {
            DataObject user = object.getObject("user");
            Guild.Ban ban = new Guild.Ban(builder.createUser(user), object.getString("reason", (String)null));
            bans.add(ban);
         } catch (Exception t) {
            LOG.error("Got an unexpected error while decoding ban index {} for guild {}:\nData: {}", i, this.guild.getId(), object, t);
         }
      }

      if (this.order == PaginationAction.PaginationOrder.BACKWARD) {
         Collections.reverse(bans);
      }

      if (this.useCache) {
         this.cached.addAll(bans);
      }

      if (!bans.isEmpty()) {
         this.last = (Guild.Ban)bans.get(bans.size() - 1);
         this.lastKey = ((Guild.Ban)this.last).getUser().getIdLong();
      }

      request.onSuccess(Collections.unmodifiableList(bans));
   }

   protected long getKey(Guild.Ban it) {
      return it.getUser().getIdLong();
   }
}
