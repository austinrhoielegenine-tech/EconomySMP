package net.dv8tion.jda.internal.entities.channel.mixin.concrete;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.attribute.IPostContainer;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IAgeRestrictedChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPostContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ISlowmodeChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ITopicChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IWebhookContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.StandardGuildChannelMixin;
import net.dv8tion.jda.internal.utils.Checks;

public interface ForumChannelMixin<T extends ForumChannelMixin<T>> extends ForumChannel, StandardGuildChannelMixin<T>, IAgeRestrictedChannelMixin<T>, ISlowmodeChannelMixin<T>, IWebhookContainerMixin<T>, IPostContainerMixin<T>, ITopicChannelMixin<T> {
   @Nonnull
   default ChannelAction<ForumChannel> createCopy(@Nonnull Guild guild) {
      Checks.notNull(guild, "Guild");
      ChannelAction<ForumChannel> action = guild.createForumChannel(this.getName()).setNSFW(this.isNSFW()).setTopic(this.getTopic()).setSlowmode(this.getSlowmode()).setAvailableTags(this.getAvailableTags()).setDefaultLayout(this.getDefaultLayout());
      if (this.getRawSortOrder() != -1) {
         action.setDefaultSortOrder(IPostContainer.SortOrder.fromKey(this.getRawSortOrder()));
      }

      if (this.getDefaultReaction() instanceof UnicodeEmoji) {
         action.setDefaultReaction(this.getDefaultReaction());
      }

      if (guild.equals(this.getGuild())) {
         Category parent = this.getParentCategory();
         action.setDefaultReaction(this.getDefaultReaction());
         if (parent != null) {
            action.setParent(parent);
         }

         for(PermissionOverride o : this.getPermissionOverrideMap().valueCollection()) {
            if (o.isMemberOverride()) {
               action.addMemberPermissionOverride(o.getIdLong(), o.getAllowedRaw(), o.getDeniedRaw());
            } else {
               action.addRolePermissionOverride(o.getIdLong(), o.getAllowedRaw(), o.getDeniedRaw());
            }
         }
      }

      return action;
   }

   T setDefaultLayout(int var1);
}
