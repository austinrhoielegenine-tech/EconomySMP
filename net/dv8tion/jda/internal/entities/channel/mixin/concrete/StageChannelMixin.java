package net.dv8tion.jda.internal.entities.channel.mixin.concrete;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IAgeRestrictedChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.ISlowmodeChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IWebhookContainerMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.AudioChannelMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildMessageChannelMixin;
import net.dv8tion.jda.internal.utils.Checks;

public interface StageChannelMixin<T extends StageChannelMixin<T>> extends StageChannel, AudioChannelMixin<T>, GuildMessageChannelMixin<T>, IWebhookContainerMixin<T>, IAgeRestrictedChannelMixin<T>, ISlowmodeChannelMixin<T> {
   @Nonnull
   default ChannelAction<StageChannel> createCopy(@Nonnull Guild guild) {
      Checks.notNull(guild, "Guild");
      ChannelAction<StageChannel> action = guild.createStageChannel(this.getName()).setBitrate(this.getBitrate());
      if (this.getRegionRaw() != null) {
         action.setRegion(Region.fromKey(this.getRegionRaw()));
      }

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
