package net.dv8tion.jda.api.exceptions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.internal.utils.Checks;

public class InsufficientPermissionException extends PermissionException {
   private final long guildId;
   private final long channelId;
   private final ChannelType channelType;

   public InsufficientPermissionException(@Nonnull Guild guild, @Nonnull Permission permission) {
      this((Guild)guild, (GuildChannel)null, (Permission)permission);
   }

   public InsufficientPermissionException(@Nonnull Guild guild, @Nonnull Permission permission, @Nonnull String reason) {
      this(guild, (GuildChannel)null, permission, reason);
   }

   public InsufficientPermissionException(@Nonnull GuildChannel channel, @Nonnull Permission permission) {
      this(channel.getGuild(), channel, permission);
   }

   public InsufficientPermissionException(@Nonnull GuildChannel channel, @Nonnull Permission permission, @Nonnull String reason) {
      this(channel.getGuild(), channel, permission, reason);
   }

   private InsufficientPermissionException(@Nonnull Guild guild, @Nullable GuildChannel channel, @Nonnull Permission permission) {
      super(permission, "Cannot perform action due to a lack of Permission. Missing permission: " + permission.toString());
      this.guildId = guild.getIdLong();
      this.channelId = channel == null ? 0L : channel.getIdLong();
      this.channelType = channel == null ? ChannelType.UNKNOWN : channel.getType();
   }

   private InsufficientPermissionException(@Nonnull Guild guild, @Nullable GuildChannel channel, @Nonnull Permission permission, @Nonnull String reason) {
      super(permission, reason);
      this.guildId = guild.getIdLong();
      this.channelId = channel == null ? 0L : channel.getIdLong();
      this.channelType = channel == null ? ChannelType.UNKNOWN : channel.getType();
   }

   public long getGuildId() {
      return this.guildId;
   }

   public long getChannelId() {
      return this.channelId;
   }

   @Nonnull
   public ChannelType getChannelType() {
      return this.channelType;
   }

   @Nullable
   public Guild getGuild(@Nonnull JDA api) {
      Checks.notNull(api, "JDA");
      return api.getGuildById(this.guildId);
   }

   @Nullable
   public GuildChannel getChannel(@Nonnull JDA api) {
      Checks.notNull(api, "JDA");
      return api.getGuildChannelById(this.channelType, this.channelId);
   }
}
