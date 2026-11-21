package net.dv8tion.jda.internal.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.ChannelUtil;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class ThreadChannelActionImpl extends AuditableRestActionImpl<ThreadChannel> implements ThreadChannelAction {
   protected final Guild guild;
   protected final GuildChannel channel;
   protected final ChannelType type;
   protected final String parentMessageId;
   protected String name;
   protected ThreadChannel.AutoArchiveDuration autoArchiveDuration = null;
   protected Integer slowmode = null;
   protected Boolean invitable = null;

   public ThreadChannelActionImpl(GuildChannel channel, String name, ChannelType type) {
      super(channel.getJDA(), Route.Channels.CREATE_THREAD.compile(channel.getId()));
      this.guild = channel.getGuild();
      this.channel = channel;
      this.type = type;
      this.parentMessageId = null;
      this.name = name;
   }

   public ThreadChannelActionImpl(GuildChannel channel, String name, String parentMessageId) {
      super(channel.getJDA(), Route.Channels.CREATE_THREAD_FROM_MESSAGE.compile(channel.getId(), parentMessageId));
      this.guild = channel.getGuild();
      this.channel = channel;
      this.type = channel.getType() == ChannelType.TEXT ? ChannelType.GUILD_PUBLIC_THREAD : ChannelType.GUILD_NEWS_THREAD;
      this.parentMessageId = parentMessageId;
      this.name = name;
   }

   @Nonnull
   public ThreadChannelActionImpl reason(String reason) {
      return (ThreadChannelActionImpl)super.reason(reason);
   }

   @Nonnull
   public ThreadChannelActionImpl setCheck(BooleanSupplier checks) {
      return (ThreadChannelActionImpl)super.setCheck(checks);
   }

   @Nonnull
   public ThreadChannelActionImpl timeout(long timeout, @Nonnull TimeUnit unit) {
      return (ThreadChannelActionImpl)super.timeout(timeout, unit);
   }

   @Nonnull
   public ThreadChannelActionImpl deadline(long timestamp) {
      return (ThreadChannelActionImpl)super.deadline(timestamp);
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   @Nonnull
   public ChannelType getType() {
      return this.type;
   }

   @Nonnull
   @CheckReturnValue
   public ThreadChannelActionImpl setName(@Nonnull String name) {
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      this.name = name;
      return this;
   }

   @Nonnull
   public ThreadChannelAction setAutoArchiveDuration(@Nonnull ThreadChannel.AutoArchiveDuration autoArchiveDuration) {
      Checks.notNull(autoArchiveDuration, "autoArchiveDuration");
      this.autoArchiveDuration = autoArchiveDuration;
      return this;
   }

   @Nonnull
   public ThreadChannelAction setSlowmode(int slowmode) {
      Checks.checkSupportedChannelTypes(ChannelUtil.SLOWMODE_SUPPORTED, this.type, "slowmode");
      Checks.check(slowmode <= 21600 && slowmode >= 0, "Slowmode per user must be between 0 and %d (seconds)!", (int)21600);
      if (!this.getGuild().getSelfMember().hasPermission(this.channel, new Permission[]{Permission.MANAGE_THREADS})) {
         throw new InsufficientPermissionException(this.channel, Permission.MANAGE_THREADS, "You must have Permission.MANAGE_THREADS on the parent channel to set a slowmode!");
      } else {
         this.slowmode = slowmode;
         return this;
      }
   }

   @Nonnull
   public ThreadChannelAction setInvitable(boolean invitable) {
      if (this.type != ChannelType.GUILD_PRIVATE_THREAD) {
         throw new UnsupportedOperationException("Can only set invitable on private threads");
      } else {
         this.invitable = invitable;
         return this;
      }
   }

   protected RequestBody finalizeData() {
      DataObject object = DataObject.empty();
      object.put("name", this.name);
      if (this.parentMessageId == null) {
         object.put("type", this.type.getId());
      }

      if (this.autoArchiveDuration != null) {
         object.put("auto_archive_duration", this.autoArchiveDuration.getMinutes());
      }

      if (this.slowmode != null) {
         object.put("rate_limit_per_user", this.slowmode);
      }

      if (this.invitable != null) {
         object.put("invitable", this.invitable);
      }

      return this.getRequestBody(object);
   }

   protected void handleSuccess(Response response, Request<ThreadChannel> request) {
      ThreadChannel channel = this.api.getEntityBuilder().createThreadChannel(response.getObject(), this.guild.getIdLong());
      request.onSuccess(channel);
   }
}
