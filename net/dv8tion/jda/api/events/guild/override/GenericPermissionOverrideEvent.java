package net.dv8tion.jda.api.events.guild.override;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IPermissionContainer;
import net.dv8tion.jda.api.entities.channel.unions.IPermissionContainerUnion;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

public class GenericPermissionOverrideEvent extends GenericGuildEvent {
   protected final IPermissionContainer channel;
   protected final PermissionOverride override;

   public GenericPermissionOverrideEvent(@Nonnull JDA api, long responseNumber, @Nonnull IPermissionContainer channel, @Nonnull PermissionOverride override) {
      super(api, responseNumber, channel.getGuild());
      this.channel = channel;
      this.override = override;
   }

   @Nonnull
   public ChannelType getChannelType() {
      return this.channel.getType();
   }

   @Nonnull
   public IPermissionContainerUnion getChannel() {
      return (IPermissionContainerUnion)this.channel;
   }

   @Nonnull
   public PermissionOverride getPermissionOverride() {
      return this.override;
   }

   public boolean isRoleOverride() {
      return this.override.isRoleOverride();
   }

   public boolean isMemberOverride() {
      return this.override.isMemberOverride();
   }

   @Nullable
   public IPermissionHolder getPermissionHolder() {
      return (IPermissionHolder)(this.isMemberOverride() ? this.override.getMember() : this.override.getRole());
   }

   @Nullable
   public Member getMember() {
      return this.override.getMember();
   }

   @Nullable
   public Role getRole() {
      return this.override.getRole();
   }
}
