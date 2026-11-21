package net.dv8tion.jda.internal.requests.restaction.interactions;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import net.dv8tion.jda.internal.utils.message.MessageCreateBuilderMixin;
import okhttp3.RequestBody;

public class ReplyCallbackActionImpl extends DeferrableCallbackActionImpl implements ReplyCallbackAction, MessageCreateBuilderMixin<ReplyCallbackAction> {
   private final MessageCreateBuilder builder = new MessageCreateBuilder();
   private int flags;

   public ReplyCallbackActionImpl(InteractionHookImpl hook) {
      super(hook);
   }

   public MessageCreateBuilder getBuilder() {
      return this.builder;
   }

   @Nonnull
   public ReplyCallbackActionImpl closeResources() {
      this.builder.closeFiles();
      return this;
   }

   @Nonnull
   protected RequestBody finalizeData() {
      DataObject json = DataObject.empty();
      if (this.builder.isEmpty()) {
         json.put("type", InteractionCallbackAction.ResponseType.DEFERRED_CHANNEL_MESSAGE_WITH_SOURCE.getRaw());
         if (this.flags != 0) {
            json.put("data", DataObject.empty().put("flags", this.flags));
         }

         return this.getRequestBody(json);
      } else {
         json.put("type", InteractionCallbackAction.ResponseType.CHANNEL_MESSAGE_WITH_SOURCE.getRaw());
         MessageCreateData data = this.builder.build();

         RequestBody var4;
         try {
            DataObject msg = data.toData();
            msg.put("flags", msg.getInt("flags", 0) | this.flags);
            json.put("data", msg);
            var4 = this.getMultipartBody(data.getAllDistinctFiles(), json);
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

         return var4;
      }
   }

   @Nonnull
   public ReplyCallbackActionImpl setEphemeral(boolean ephemeral) {
      int flag = Message.MessageFlag.EPHEMERAL.getValue();
      if (ephemeral) {
         this.flags |= flag;
      } else {
         this.flags &= ~flag;
      }

      return this;
   }

   @Nonnull
   public ReplyCallbackAction setCheck(BooleanSupplier checks) {
      return (ReplyCallbackAction)super.setCheck(checks);
   }

   @Nonnull
   public ReplyCallbackAction timeout(long timeout, @Nonnull TimeUnit unit) {
      return (ReplyCallbackAction)super.timeout(timeout, unit);
   }

   @Nonnull
   public ReplyCallbackAction deadline(long timestamp) {
      return (ReplyCallbackAction)super.deadline(timestamp);
   }
}
