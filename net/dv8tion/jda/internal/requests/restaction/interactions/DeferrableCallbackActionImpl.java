package net.dv8tion.jda.internal.requests.restaction.interactions;

import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import net.dv8tion.jda.internal.interactions.response.InteractionCallbackResponseImpl;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

public abstract class DeferrableCallbackActionImpl extends InteractionCallbackImpl<InteractionHook> {
   protected final InteractionHookImpl hook;

   public DeferrableCallbackActionImpl(InteractionHookImpl hook) {
      super(hook.getInteraction());
      this.hook = hook;
   }

   protected void handleSuccess(Response response, Request<InteractionHook> request) {
      this.interaction.releaseHook(true);
      this.parseOptionalBody(response);
      request.onSuccess(this.hook);
   }

   private void parseOptionalBody(Response response) {
      okhttp3.Response rawResponse = response.getRawResponse();
      if (rawResponse != null) {
         ResponseBody body = rawResponse.body();
         if (body != null) {
            MediaType mediaType = body.contentType();
            if (mediaType != null && mediaType.toString().startsWith("application/json")) {
               response.optObject().flatMap((obj) -> obj.optObject("resource")).ifPresent((resource) -> this.hook.setCallbackResponse(new InteractionCallbackResponseImpl(this.hook, resource)));
            }

         }
      }
   }
}
