package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import javax.annotation.Nonnull;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;
import net.dv8tion.jda.internal.interactions.ChannelInteractionPermissions;

public interface IInteractionPermissionMixin<T extends IInteractionPermissionMixin<T>> extends GuildChannelMixin<T> {
   @Nonnull
   ChannelInteractionPermissions getInteractionPermissions();

   @Nonnull
   T setInteractionPermissions(@Nonnull ChannelInteractionPermissions var1);
}
