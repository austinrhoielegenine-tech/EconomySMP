package net.dv8tion.jda.internal.requests.restaction;

import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.ThreadCreateMetadata;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.message.MessageCreateBuilderMixin;
import okhttp3.RequestBody;

public class WebhookMessageCreateActionImpl<T> extends AbstractWebhookMessageActionImpl<T, WebhookMessageCreateActionImpl<T>> implements WebhookMessageCreateAction<T>, MessageCreateBuilderMixin<WebhookMessageCreateAction<T>> {
   private final MessageCreateBuilder builder = new MessageCreateBuilder();
   private final Function<DataObject, T> transformer;
   private boolean isInteraction = true;
   private boolean ephemeral;
   private String username;
   private String avatar;
   private ThreadCreateMetadata threadMetadata;

   public WebhookMessageCreateActionImpl(JDA api, Route.CompiledRoute route, Function<DataObject, T> transformer) {
      super(api, route);
      this.transformer = transformer;
   }

   public WebhookMessageCreateActionImpl<T> setInteraction(boolean isInteraction) {
      this.isInteraction = isInteraction;
      return this;
   }

   public MessageCreateBuilder getBuilder() {
      return this.builder;
   }

   @Nonnull
   public WebhookMessageCreateActionImpl<T> setEphemeral(boolean ephemeral) {
      if (!this.isInteraction && ephemeral) {
         throw new IllegalStateException("Cannot create ephemeral messages with webhooks. Use InteractionHook instead!");
      } else {
         this.ephemeral = ephemeral;
         return this;
      }
   }

   @Nonnull
   public WebhookMessageCreateAction<T> setUsername(@Nullable String name) {
      if (this.isInteraction && this.username != null) {
         throw new IllegalStateException("Cannot set username on interaction messages.");
      } else {
         if (name != null) {
            name = name.trim();
            Checks.inRange(name, 1, 80, "Name");
         }

         this.username = name;
         return this;
      }
   }

   @Nonnull
   public WebhookMessageCreateAction<T> setAvatarUrl(@Nullable String iconUrl) {
      if (this.isInteraction && iconUrl != null) {
         throw new IllegalStateException("Cannot set avatar on interaction messages.");
      } else {
         if (iconUrl != null) {
            Checks.noWhitespace(iconUrl, "Avatar URL");
            Checks.check(iconUrl.startsWith("https://") || iconUrl.startsWith("http://"), "Invalid URL format. Must start with 'https://' or 'http://'. Provided %s", (Object)iconUrl);
         }

         this.avatar = iconUrl;
         return this;
      }
   }

   @Nonnull
   public WebhookMessageCreateAction<T> createThread(@Nonnull ThreadCreateMetadata threadMetadata) {
      if (this.isInteraction) {
         throw new IllegalStateException("Cannot create a thread through an interaction hook.");
      } else {
         Checks.notNull(threadMetadata, "Thread Metadata");
         this.threadMetadata = threadMetadata;
         return this;
      }
   }

   protected RequestBody finalizeData() {
      MessageCreateData data = this.builder.build();

      RequestBody var6;
      try {
         DataObject json = data.toData();
         if (this.ephemeral) {
            json.put("flags", json.getInt("flags", 0) | Message.MessageFlag.EPHEMERAL.getValue());
         }

         if (this.username != null) {
            json.put("username", this.username);
         }

         if (this.avatar != null) {
            json.put("avatar_url", this.avatar);
         }

         if (this.threadId == null && this.threadMetadata != null) {
            json.put("thread_name", this.threadMetadata.getName());
            List<ForumTagSnowflake> tags = this.threadMetadata.getAppliedTags();
            if (!tags.isEmpty()) {
               json.put("applied_tags", tags.stream().map(ISnowflake::getId).collect(Helpers.toDataArray()));
            }
         }

         var6 = this.getMultipartBody(data.getAllDistinctFiles(), json);
      } catch (Throwable var5) {
         if (data != null) {
            try {
               data.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (data != null) {
         data.close();
      }

      return var6;
   }

   protected Route.CompiledRoute finalizeRoute() {
      Route.CompiledRoute route = super.finalizeRoute();
      if (this.threadId != null) {
         route = route.withQueryParams("thread_id", this.threadId);
      }

      return route;
   }

   protected void handleSuccess(Response response, Request<T> request) {
      T message = (T)this.transformer.apply(response.getObject());
      request.onSuccess(message);
   }
}
