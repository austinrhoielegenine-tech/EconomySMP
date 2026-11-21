package net.dv8tion.jda.internal.requests.restaction;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Entitlement;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.TestEntitlementCreateAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class TestEntitlementCreateActionImpl extends RestActionImpl<Entitlement> implements TestEntitlementCreateAction {
   private long skuId;
   private long ownerId;
   private TestEntitlementCreateAction.OwnerType type;

   public TestEntitlementCreateActionImpl(JDA api, long skuId, long ownerId, TestEntitlementCreateAction.OwnerType type) {
      super(api, Route.Applications.CREATE_TEST_ENTITLEMENT.compile(api.getSelfUser().getApplicationId()));
      this.skuId = skuId;
      this.ownerId = ownerId;
      this.type = type;
   }

   @Nonnull
   public TestEntitlementCreateAction setSkuId(long skuId) {
      this.skuId = skuId;
      return this;
   }

   @Nonnull
   public TestEntitlementCreateAction setOwnerId(long ownerId) {
      this.ownerId = ownerId;
      return this;
   }

   @Nonnull
   public TestEntitlementCreateAction setOwnerType(@Nonnull TestEntitlementCreateAction.OwnerType type) {
      Checks.notNull(type, "type");
      this.type = type;
      return this;
   }

   protected void handleSuccess(Response response, Request<Entitlement> request) {
      DataObject object = response.getObject();
      request.onSuccess(this.api.getEntityBuilder().createEntitlement(object));
   }

   protected RequestBody finalizeData() {
      DataObject object = DataObject.empty();
      object.put("sku_id", this.skuId);
      object.put("owner_id", this.ownerId);
      object.put("owner_type", this.type.getKey());
      return this.getRequestBody(object);
   }
}
