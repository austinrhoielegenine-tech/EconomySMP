package net.dv8tion.jda.internal.entities.channel.mixin.concrete;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.StandardGuildMessageChannelMixin;
import net.dv8tion.jda.internal.utils.Checks;

public interface NewsChannelMixin<T extends NewsChannelMixin<T>> extends NewsChannel, StandardGuildMessageChannelMixin<T> {
   @Nonnull
   default ChannelAction<NewsChannel> createCopy(@Nonnull Guild guild) {
      Checks.notNull(guild, "Guild");
      ChannelAction<NewsChannel> action = guild.createNewsChannel(this.getName()).setNSFW(this.isNSFW()).setTopic(this.getTopic());
      if (guild.equals(this.getGuild())) {
         Category parent = this.getParentCategory();
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
}
