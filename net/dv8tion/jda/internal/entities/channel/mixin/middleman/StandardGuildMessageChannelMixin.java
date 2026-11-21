package net.dv8tion.jda.internal.entities.channel.mixin.middleman;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IAgeRestrictedChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IThreadContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ITopicChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IWebhookContainerMixin;

public interface StandardGuildMessageChannelMixin<T extends StandardGuildMessageChannelMixin<T>> extends StandardGuildMessageChannel, StandardGuildChannelMixin<T>, GuildMessageChannelMixin<T>, IThreadContainerMixin<T>, IAgeRestrictedChannelMixin<T>, IWebhookContainerMixin<T>, ITopicChannelMixin<T> {
   default boolean canTalk(@Nonnull Member member) {
      if (!this.getGuild().equals(member.getGuild())) {
         throw new IllegalArgumentException("Provided Member is not from the Guild that this channel is part of.");
      } else {
         return member.hasPermission(this, new Permission[]{Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND});
      }
   }
}
