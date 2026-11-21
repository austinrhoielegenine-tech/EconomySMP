package net.dv8tion.jda.internal.requests.restaction;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumPost;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.ForumPostAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.utils.ChannelUtil;
import net.dv8tion.jda.internal.utils.Checks;
import net.dv8tion.jda.internal.utils.message.MessageCreateBuilderMixin;
import okhttp3.RequestBody;

public class ForumPostActionImpl extends RestActionImpl<ForumPost> implements ForumPostAction, MessageCreateBuilderMixin<ForumPostAction> {
   private final MessageCreateBuilder builder;
   private final IPostContainer channel;
   private final TLongSet appliedTags = new TLongHashSet();
   private String name;
   private ThreadChannel.AutoArchiveDuration autoArchiveDuration;
   protected Integer slowmode = null;

   public ForumPostActionImpl(IPostContainer channel, String name, MessageCreateBuilder builder) {
      super(channel.getJDA(), Route.Channels.CREATE_THREAD.compile(channel.getId()));
      this.builder = builder;
      this.channel = channel;
      this.setName(name);
   }

   @Nonnull
   public ForumPostAction setCheck(BooleanSupplier checks) {
      return (ForumPostAction)super.setCheck(checks);
   }

   @Nonnull
   public ForumPostAction addCheck(@Nonnull BooleanSupplier checks) {
      return (ForumPostAction)super.addCheck(checks);
   }

   @Nonnull
   public ForumPostAction deadline(long timestamp) {
      return (ForumPostAction)super.deadline(timestamp);
   }

   @Nonnull
   public Guild getGuild() {
      return this.channel.getGuild();
   }

   @Nonnull
   public IPostContainer getChannel() {
      return this.channel;
   }

   @Nonnull
   public ForumPostAction setTags(@Nonnull Collection<? extends ForumTagSnowflake> tags) {
      Checks.noneNull(tags, "Tags");
      Checks.check(tags.size() <= 5, "Provided more than %d tags.", (int)5);
      Checks.check(!this.channel.isTagRequired() || !tags.isEmpty(), "This forum requires at least one tag per post! See ForumChannel#isRequireTag()");
      this.appliedTags.clear();
      tags.forEach((t) -> this.appliedTags.add(t.getIdLong()));
      return this;
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.GUILD_PUBLIC_THREAD;
   }

   @Nonnull
   public ForumPostAction setName(@Nonnull String name) {
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      this.name = name.trim();
      return this;
   }

   @Nonnull
   public ForumPostAction setAutoArchiveDuration(@Nonnull ThreadChannel.AutoArchiveDuration autoArchiveDuration) {
      Checks.notNull(autoArchiveDuration, "AutoArchiveDuration");
      this.autoArchiveDuration = autoArchiveDuration;
      return this;
   }

   @Nonnull
   public ForumPostAction setSlowmode(int slowmode) {
      Checks.checkSupportedChannelTypes(ChannelUtil.SLOWMODE_SUPPORTED, this.getType(), "slowmode");
      Checks.check(slowmode <= 21600 && slowmode >= 0, "Slowmode per user must be between 0 and %d (seconds)!", (int)21600);
      if (!this.getGuild().getSelfMember().hasPermission(this.channel, new Permission[]{Permission.MANAGE_THREADS})) {
         throw new InsufficientPermissionException(this.channel, Permission.MANAGE_THREADS, "You must have Permission.MANAGE_THREADS on the parent forum channel to set a slowmode!");
      } else {
         this.slowmode = slowmode;
         return this;
      }
   }

   public MessageCreateBuilder getBuilder() {
      return this.builder;
   }

   protected RequestBody finalizeData() {
      MessageCreateData message = this.builder.build();

      RequestBody var3;
      try {
         DataObject json = DataObject.empty();
         json.put("message", message);
         json.put("name", this.name);
         if (this.autoArchiveDuration != null) {
            json.put("auto_archive_duration", this.autoArchiveDuration.getMinutes());
         }

         if (this.slowmode != null) {
            json.put("rate_limit_per_user", this.slowmode);
         }

         if (!this.appliedTags.isEmpty()) {
            json.put("applied_tags", this.appliedTags.toArray());
         } else if (this.getChannel().isTagRequired()) {
            throw new IllegalStateException("Cannot create posts without a tag in this forum. Apply at least one tag!");
         }

         var3 = this.getMultipartBody(message.getAllDistinctFiles(), json);
      } catch (Throwable var5) {
         if (message != null) {
            try {
               message.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (message != null) {
         message.close();
      }

      return var3;
   }

   protected void handleSuccess(Response response, Request<ForumPost> request) {
      DataObject json = response.getObject();
      EntityBuilder entityBuilder = this.api.getEntityBuilder();
      ThreadChannel thread = entityBuilder.createThreadChannel(json, this.getGuild().getIdLong());
      Message message = entityBuilder.createMessageWithChannel(json.getObject("message"), thread, false);
      request.onSuccess(new ForumPost(message, thread));
   }
}
