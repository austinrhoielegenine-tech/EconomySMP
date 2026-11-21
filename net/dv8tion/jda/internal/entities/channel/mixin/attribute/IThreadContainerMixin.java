package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.unions.IThreadContainerUnion;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ThreadChannelPaginationAction;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;
import net.dv8tion.jda.internal.requests.restaction.ThreadChannelActionImpl;
import net.dv8tion.jda.internal.requests.restaction.pagination.ThreadChannelPaginationActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface IThreadContainerMixin<T extends IThreadContainerMixin<T>> extends IThreadContainer, IThreadContainerUnion, GuildChannelMixin<T> {
   @Nonnull
   default ThreadChannelAction createThreadChannel(@Nonnull String name, boolean isPrivate) {
      Checks.notNull(name, "Name");
      name = name.trim();
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      this.checkAttached();
      Checks.checkAccess(this.getGuild().getSelfMember(), this);
      if (isPrivate) {
         this.checkPermission(Permission.CREATE_PRIVATE_THREADS);
      } else {
         this.checkPermission(Permission.CREATE_PUBLIC_THREADS);
      }

      ChannelType threadType = isPrivate ? ChannelType.GUILD_PRIVATE_THREAD : (this.getType() == ChannelType.TEXT ? ChannelType.GUILD_PUBLIC_THREAD : ChannelType.GUILD_NEWS_THREAD);
      return new ThreadChannelActionImpl(this, name, threadType);
   }

   @Nonnull
   default ThreadChannelAction createThreadChannel(@Nonnull String name, long messageId) {
      Checks.notNull(name, "Name");
      name = name.trim();
      Checks.notEmpty(name, "Name");
      Checks.notLonger(name, 100, "Name");
      this.checkAttached();
      Checks.checkAccess(this.getGuild().getSelfMember(), this);
      this.checkPermission(Permission.CREATE_PUBLIC_THREADS);
      return new ThreadChannelActionImpl(this, name, Long.toUnsignedString(messageId));
   }

   @Nonnull
   default ThreadChannelPaginationAction retrieveArchivedPublicThreadChannels() {
      this.checkAttached();
      Checks.checkAccess(this.getGuild().getSelfMember(), this);
      this.checkPermission(Permission.MESSAGE_HISTORY);
      Route.CompiledRoute route = Route.Channels.LIST_PUBLIC_ARCHIVED_THREADS.compile(this.getId());
      return new ThreadChannelPaginationActionImpl(this.getJDA(), route, this, false);
   }

   @Nonnull
   default ThreadChannelPaginationAction retrieveArchivedPrivateThreadChannels() {
      this.checkAttached();
      Checks.checkAccess(this.getGuild().getSelfMember(), this);
      this.checkPermission(Permission.MESSAGE_HISTORY);
      this.checkPermission(Permission.MANAGE_THREADS);
      Route.CompiledRoute route = Route.Channels.LIST_PRIVATE_ARCHIVED_THREADS.compile(this.getId());
      return new ThreadChannelPaginationActionImpl(this.getJDA(), route, this, false);
   }

   @Nonnull
   default ThreadChannelPaginationAction retrieveArchivedPrivateJoinedThreadChannels() {
      this.checkAttached();
      Checks.checkAccess(this.getGuild().getSelfMember(), this);
      this.checkPermission(Permission.MESSAGE_HISTORY);
      Route.CompiledRoute route = Route.Channels.LIST_JOINED_PRIVATE_ARCHIVED_THREADS.compile(this.getId());
      return new ThreadChannelPaginationActionImpl(this.getJDA(), route, this, true);
   }

   T setDefaultThreadSlowmode(int var1);
}
