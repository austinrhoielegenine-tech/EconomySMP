package net.dv8tion.jda.internal.requests.restaction.pagination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.pagination.EntitlementPaginationAction;
import net.dv8tion.jda.api.requests.restaction.pagination.PaginationAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.utils.Checks;

public class EntitlementPaginationActionImpl extends PaginationActionImpl<Entitlement, EntitlementPaginationAction> implements EntitlementPaginationAction {
   protected List<String> skuIds = new ArrayList();
   protected long guildId = 0L;
   protected long userId = 0L;
   protected boolean excludeEnded;

   public EntitlementPaginationActionImpl(JDA api) {
      super(api, Route.Applications.GET_ENTITLEMENTS.compile(api.getSelfUser().getApplicationId()), 1, 100, 100);
   }

   @Nonnull
   public EnumSet<PaginationAction.PaginationOrder> getSupportedOrders() {
      return EnumSet.of(PaginationAction.PaginationOrder.BACKWARD, PaginationAction.PaginationOrder.FORWARD);
   }

   @Nonnull
   public EntitlementPaginationAction user(@Nullable UserSnowflake user) {
      if (user == null) {
         this.userId = 0L;
      } else {
         this.userId = user.getIdLong();
      }

      return this;
   }

   @Nonnull
   public EntitlementPaginationAction skuIds(@Nonnull long... skuIds) {
      this.skuIds.clear();

      for(long skuId : skuIds) {
         this.skuIds.add(Long.toUnsignedString(skuId));
      }

      return this;
   }

   @Nonnull
   public EntitlementPaginationAction skuIds(@Nonnull String... skuIds) {
      Checks.noneNull(skuIds, "skuIds");

      for(String skuId : skuIds) {
         Checks.isSnowflake(skuId, "skuId");
      }

      this.skuIds.clear();
      Collections.addAll(this.skuIds, skuIds);
      return this;
   }

   @Nonnull
   public EntitlementPaginationAction skuIds(@Nonnull Collection<String> skuIds) {
      Checks.noneNull(skuIds, "skuIds");
      this.skuIds.clear();

      for(String skuId : skuIds) {
         Checks.isSnowflake(skuId, "skuId");
         this.skuIds.add(skuId);
      }

      return this;
   }

   @Nonnull
   public EntitlementPaginationAction guild(long guildId) {
      this.guildId = guildId;
      return this;
   }

   @Nonnull
   public EntitlementPaginationAction excludeEnded(boolean excludeEnded) {
      this.excludeEnded = excludeEnded;
      return this;
   }

   protected Route.CompiledRoute finalizeRoute() {
      Route.CompiledRoute route = super.finalizeRoute();
      if (this.userId != 0L) {
         route = route.withQueryParams("user_id", Long.toUnsignedString(this.userId));
      }

      if (!this.skuIds.isEmpty()) {
         route = route.withQueryParams("sku_ids", String.join(",", this.skuIds));
      }

      if (this.guildId != 0L) {
         route = route.withQueryParams("guild_id", Long.toUnsignedString(this.guildId));
      }

      if (this.excludeEnded) {
         route = route.withQueryParams("exclude_ended", String.valueOf(true));
      }

      return route;
   }

   protected void handleSuccess(Response response, Request<List<Entitlement>> request) {
      DataArray array = response.getArray();
      List<Entitlement> entitlements = new ArrayList(array.length());
      EntityBuilder builder = this.api.getEntityBuilder();

      for(int i = 0; i < array.length(); ++i) {
         try {
            DataObject object = array.getObject(i);
            Entitlement entitlement = builder.createEntitlement(object);
            entitlements.add(entitlement);
         } catch (NullPointerException | ParsingException e) {
            LOG.warn((String)"Encountered an exception in EntitlementPaginationAction", (Throwable)e);
         }
      }

      if (!entitlements.isEmpty()) {
         if (this.useCache) {
            this.cached.addAll(entitlements);
         }

         this.last = (Entitlement)entitlements.get(entitlements.size() - 1);
         this.lastKey = ((Entitlement)this.last).getIdLong();
      }

      request.onSuccess(entitlements);
   }

   protected long getKey(Entitlement it) {
      return it.getIdLong();
   }
}
