package net.dv8tion.jda.internal.entities.channel.mixin.concrete;

import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ISlowmodeChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildMessageChannelMixin;

public interface ThreadChannelMixin<T extends ThreadChannelMixin<T>> extends ThreadChannel, GuildMessageChannelMixin<T>, ISlowmodeChannelMixin<T> {
   T setAutoArchiveDuration(ThreadChannel.AutoArchiveDuration var1);

   T setLocked(boolean var1);

   T setArchived(boolean var1);

   T setInvitable(boolean var1);

   T setArchiveTimestamp(long var1);

   T setCreationTimestamp(long var1);

   T setOwnerId(long var1);

   T setMessageCount(int var1);

   T setTotalMessageCount(int var1);

   T setMemberCount(int var1);

   T setFlags(int var1);
}
