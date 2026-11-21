package net.dv8tion.jda.api.managers.channel;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.managers.Manager;

public interface ChannelManager<T extends GuildChannel, M extends ChannelManager<T, M>> extends Manager<M> {
   long NAME = 1L;
   long PARENT = 2L;
   long TOPIC = 4L;
   long POSITION = 8L;
   long NSFW = 16L;
   long USERLIMIT = 32L;
   long BITRATE = 64L;
   long PERMISSION = 128L;
   long SLOWMODE = 256L;
   long TYPE = 512L;
   long REGION = 1024L;
   long AUTO_ARCHIVE_DURATION = 2048L;
   long ARCHIVED = 4096L;
   long LOCKED = 8192L;
   long INVITEABLE = 16384L;
   long AVAILABLE_TAGS = 32768L;
   long APPLIED_TAGS = 65536L;
   long PINNED = 131072L;
   long REQUIRE_TAG = 262144L;
   long DEFAULT_REACTION = 524288L;
   long DEFAULT_LAYOUT = 1048576L;
   long DEFAULT_SORT_ORDER = 2097152L;
   long HIDE_MEDIA_DOWNLOAD_OPTIONS = 4194304L;
   long DEFAULT_THREAD_SLOWMODE = 8388608L;

   @Nonnull
   @CheckReturnValue
   M reset(long var1);

   @Nonnull
   @CheckReturnValue
   M reset(@Nonnull long... var1);

   @Nonnull
   T getChannel();

   @Nonnull
   default Guild getGuild() {
      return this.getChannel().getGuild();
   }

   @Nonnull
   @CheckReturnValue
   M setName(@Nonnull String var1);
}
