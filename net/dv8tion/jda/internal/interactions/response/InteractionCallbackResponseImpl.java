package net.dv8tion.jda.internal.interactions.response;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.response.InteractionCallbackResponse;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;

public class InteractionCallbackResponseImpl implements InteractionCallbackResponse {
   private final InteractionCallbackAction.ResponseType type;
   private final Message message;

   public InteractionCallbackResponseImpl(InteractionHookImpl hook, DataObject resource) {
      this.type = InteractionCallbackAction.ResponseType.fromId(resource.getInt("type", -1));
      Optional var10001 = resource.optObject("message");
      Objects.requireNonNull(hook);
      this.message = (Message)var10001.map(hook::buildMessage).orElse((Object)null);
   }

   @Nullable
   public Message getMessage() {
      return this.message;
   }

   @Nonnull
   public InteractionCallbackAction.ResponseType getResponseType() {
      return this.type;
   }
}
