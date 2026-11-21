package net.dv8tion.jda.internal.requests.restaction.interactions;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import net.dv8tion.jda.internal.utils.message.MessageEditBuilderMixin;
import okhttp3.RequestBody;

public class MessageEditCallbackActionImpl extends DeferrableCallbackActionImpl implements MessageEditCallbackAction, MessageEditBuilderMixin<MessageEditCallbackAction> {
   private final MessageEditBuilder builder = new MessageEditBuilder();

   public MessageEditCallbackActionImpl(InteractionHookImpl hook) {
      super(hook);
   }

   public MessageEditBuilder getBuilder() {
      return this.builder;
   }

   @Nonnull
   public MessageEditCallbackActionImpl setCheck(BooleanSupplier checks) {
      return (MessageEditCallbackActionImpl)super.setCheck(checks);
   }

   @Nonnull
   public MessageEditCallbackActionImpl timeout(long timeout, @Nonnull TimeUnit unit) {
      return (MessageEditCallbackActionImpl)super.timeout(timeout, unit);
   }

   @Nonnull
   public MessageEditCallbackActionImpl deadline(long timestamp) {
      return (MessageEditCallbackActionImpl)super.deadline(timestamp);
   }

   @Nonnull
   public MessageEditCallbackActionImpl closeResources() {
      this.builder.closeFiles();
      return this;
   }

   private boolean isEmpty() {
      return this.builder.isEmpty();
   }

   protected RequestBody finalizeData() {
      DataObject json = DataObject.empty();
      if (this.isEmpty()) {
         return this.getRequestBody(json.put("type", InteractionCallbackAction.ResponseType.DEFERRED_MESSAGE_UPDATE.getRaw()));
      } else {
         json.put("type", InteractionCallbackAction.ResponseType.MESSAGE_UPDATE.getRaw());
         MessageEditData data = this.builder.build();

         RequestBody var3;
         try {
            json.put("data", data);
            var3 = this.getMultipartBody(data.getAllDistinctFiles(), json);
         } catch (Throwable var6) {
            if (data != null) {
               try {
                  data.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (data != null) {
            data.close();
         }

         return var3;
      }
   }
}
