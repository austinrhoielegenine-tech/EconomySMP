package net.dv8tion.jda.internal.entities.channel.mixin.concrete;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ISlowmodeChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.StandardGuildMessageChannelMixin;
import net.dv8tion.jda.internal.utils.Checks;

public interface TextChannelMixin<T extends TextChannelMixin<T>> extends TextChannel, StandardGuildMessageChannelMixin<T>, ISlowmodeChannelMixin<T> {
   @Nonnull
   default ChannelAction<TextChannel> createCopy(@Nonnull Guild guild) {
      Checks.notNull(guild, "Guild");
      ChannelAction<TextChannel> action = guild.createTextChannel(this.getName()).setNSFW(this.isNSFW()).setTopic(this.getTopic()).setSlowmode(this.getSlowmode());
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
