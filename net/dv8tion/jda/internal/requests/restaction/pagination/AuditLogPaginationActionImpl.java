package net.dv8tion.jda.internal.requests.restaction.pagination;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class AuditLogPaginationActionImpl extends PaginationActionImpl<AuditLogEntry, AuditLogPaginationAction> implements AuditLogPaginationAction {
   protected final Guild guild;
   protected ActionType type = null;
   protected String userId = null;

   public AuditLogPaginationActionImpl(Guild guild) {
      super(guild.getJDA(), Route.Guilds.GET_AUDIT_LOGS.compile(guild.getId()), 1, 100, 100);
      if (!guild.getSelfMember().hasPermission(new Permission[]{Permission.VIEW_AUDIT_LOGS})) {
         throw new InsufficientPermissionException(guild, Permission.VIEW_AUDIT_LOGS);
      } else {
         this.guild = guild;
         super.order(PaginationAction.PaginationOrder.BACKWARD);
      }
   }

   @Nonnull
   public AuditLogPaginationActionImpl type(ActionType type) {
      this.type = type;
      return this;
   }

   @Nonnull
   public AuditLogPaginationActionImpl user(UserSnowflake user) {
      this.userId = user == null ? null : user.getId();
      return this;
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   @Nonnull
   public EnumSet<PaginationAction.PaginationOrder> getSupportedOrders() {
      return EnumSet.of(PaginationAction.PaginationOrder.BACKWARD, PaginationAction.PaginationOrder.FORWARD);
   }

   protected Route.CompiledRoute finalizeRoute() {
      Route.CompiledRoute route = super.finalizeRoute();
      if (this.type != null) {
         route = route.withQueryParams("action_type", String.valueOf(this.type.getKey()));
      }

      if (this.userId != null) {
         route = route.withQueryParams("user_id", this.userId);
      }

      return route;
   }

   protected void handleSuccess(Response response, Request<List<AuditLogEntry>> request) {
      DataObject obj = response.getObject();
      DataArray users = obj.getArray("users");
      DataArray webhooks = obj.getArray("webhooks");
      DataArray entries = obj.getArray("audit_log_entries");
      List<AuditLogEntry> list = new ArrayList(entries.length());
      EntityBuilder builder = this.api.getEntityBuilder();
      TLongObjectMap<DataObject> userMap = new TLongObjectHashMap<DataObject>();

      for(int i = 0; i < users.length(); ++i) {
         DataObject user = users.getObject(i);
         userMap.put(user.getLong("id"), user);
      }

      TLongObjectMap<DataObject> webhookMap = new TLongObjectHashMap<DataObject>();

      for(int i = 0; i < webhooks.length(); ++i) {
         DataObject webhook = webhooks.getObject(i);
         webhookMap.put(webhook.getLong("id"), webhook);
      }

      for(int i = 0; i < entries.length(); ++i) {
         try {
            DataObject entry = entries.getObject(i);
            DataObject user = userMap.get(entry.getLong("user_id", 0L));
            DataObject webhook = webhookMap.get(entry.getLong("target_id", 0L));
            AuditLogEntry result = builder.createAuditLogEntry((GuildImpl)this.guild, entry, user, webhook);
            list.add(result);
         } catch (NullPointerException | ParsingException e) {
            LOG.warn((String)"Encountered exception in AuditLogPagination", (Throwable)e);
         }
      }

      if (!list.isEmpty()) {
         if (this.useCache) {
            this.cached.addAll(list);
         }

         this.last = (AuditLogEntry)list.get(list.size() - 1);
         this.lastKey = ((AuditLogEntry)this.last).getIdLong();
      }

      request.onSuccess(list);
   }

   protected long getKey(AuditLogEntry it) {
      return it.getIdLong();
   }
}
