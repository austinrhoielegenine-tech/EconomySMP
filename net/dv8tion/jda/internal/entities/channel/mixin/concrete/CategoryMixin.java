package net.dv8tion.jda.internal.entities.channel.mixin.concrete;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPermissionContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IPositionableChannelMixin;
import net.dv8tion.jda.internal.utils.Checks;

public interface CategoryMixin<T extends CategoryMixin<T>> extends Category, IPositionableChannelMixin<T>, IPermissionContainerMixin<T> {
   @Nonnull
   default ChannelAction<Category> createCopy(@Nonnull Guild guild) {
      Checks.notNull(guild, "Guild");
      ChannelAction<Category> action = guild.createCategory(this.getName());
      if (guild.equals(this.getGuild())) {
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
