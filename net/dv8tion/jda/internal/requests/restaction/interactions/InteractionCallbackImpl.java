package net.dv8tion.jda.internal.requests.restaction.interactions;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;
import net.dv8tion.jda.internal.interactions.InteractionImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;

public abstract class InteractionCallbackImpl<T> extends RestActionImpl<T> implements InteractionCallbackAction<T> {
   protected final InteractionImpl interaction;

   public InteractionCallbackImpl(InteractionImpl interaction) {
      super(interaction.getJDA(), Route.Interactions.CALLBACK.compile(interaction.getId(), interaction.getToken()).withQueryParams("with_response", "true"));
      this.interaction = interaction;
      this.setErrorMapper(this::handleUnknownInteraction);
   }

   private Throwable handleUnknownInteraction(Response response, Request<?> request, ErrorResponseException exception) {
      if (exception.getErrorResponse() == ErrorResponse.INTERACTION_ALREADY_ACKNOWLEDGED) {
         return ErrorResponseException.create("This interaction was acknowledged by another process running for the same bot.\nTo resolve this, try stopping all current processes for the bot that could be responsible, or resetting your bot token.\nYou can reset your token at https://discord.com/developers/applications/" + this.getJDA().getSelfUser().getApplicationId() + "/bot", exception);
      } else {
         return exception.getErrorResponse() == ErrorResponse.UNKNOWN_INTERACTION ? ErrorResponseException.create("Failed to acknowledge this interaction, this can be due to 2 reasons:\n1. This interaction took longer than 3 seconds to be acknowledged, see https://jda.wiki/using-jda/troubleshooting/#the-interaction-took-longer-than-3-seconds-to-be-acknowledged\n2. This interaction could have been acknowledged by another process running for the same bot\nYou can confirm this by checking if your bot replied, or the three dots in a button disappeared without saying 'This interaction failed', or you see '[Bot] is thinking...' for more than 3 seconds.\nTo resolve this, try stopping all current processes for the bot that could be responsible, or resetting your bot token.\nYou can reset your token at https://discord.com/developers/applications/" + this.getJDA().getSelfUser().getApplicationId() + "/bot", exception) : null;
      }
   }

   @Nonnull
   public InteractionCallbackAction<T> closeResources() {
      return this;
   }

   protected final IllegalStateException tryAck() {
      return this.interaction.ack() ? new IllegalStateException("This interaction has already been acknowledged or replied to. You can only reply or acknowledge an interaction once!") : null;
   }

   public final void queue(Consumer<? super T> success, Consumer<? super Throwable> failure) {
      IllegalStateException exception = this.tryAck();
      if (exception != null) {
         if (failure != null) {
            failure.accept(exception);
         } else {
            RestAction.getDefaultFailure().accept(exception);
         }

      } else {
         super.queue(success, failure);
      }
   }

   @Nonnull
   public final CompletableFuture<T> submit(boolean shouldQueue) {
      IllegalStateException exception = this.tryAck();
      if (exception != null) {
         CompletableFuture<T> future = new CompletableFuture();
         future.completeExceptionally(exception);
         return future;
      } else {
         return super.submit(shouldQueue);
      }
   }

   protected void handleSuccess(Response response, Request<T> request) {
      this.interaction.releaseHook(true);
      super.handleSuccess(response, request);
   }

   public void handleResponse(Response response, Request<T> request) {
      if (!response.isOk()) {
         this.interaction.releaseHook(false);
      }

      super.handleResponse(response, request);
   }
}
