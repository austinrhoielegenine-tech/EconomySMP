package net.dv8tion.jda.internal.entities.channel.concrete.detached;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractStandardGuildMessageChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.attribute.IInteractionPermissionMixin;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.TextChannelMixin;
import net.dv8tion.jda.internal.entities.detached.DetachedGuildImpl;
import net.dv8tion.jda.internal.interactions.ChannelInteractionPermissions;

public class DetachedTextChannelImpl extends AbstractStandardGuildMessageChannelImpl<DetachedTextChannelImpl> implements TextChannel, DefaultGuildChannelUnion, TextChannelMixin<DetachedTextChannelImpl>, IInteractionPermissionMixin<DetachedTextChannelImpl> {
   private int slowmode;
   private ChannelInteractionPermissions interactionPermissions;

   public DetachedTextChannelImpl(long id, DetachedGuildImpl guild) {
      super(id, guild);
   }

   public boolean isDetached() {
      return true;
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.TEXT;
   }

   @Nonnull
   public List<Member> getMembers() {
      throw this.detachedException();
   }

   public int getSlowmode() {
      return this.slowmode;
   }

   @Nonnull
   public TextChannelManager getManager() {
      throw this.detachedException();
   }

   @Nonnull
   public ChannelInteractionPermissions getInteractionPermissions() {
      return this.interactionPermissions;
   }

   public DetachedTextChannelImpl setSlowmode(int slowmode) {
      this.slowmode = slowmode;
      return this;
   }

   @Nonnull
   public DetachedTextChannelImpl setInteractionPermissions(@Nonnull ChannelInteractionPermissions interactionPermissions) {
      this.interactionPermissions = interactionPermissions;
      return this;
   }
}
