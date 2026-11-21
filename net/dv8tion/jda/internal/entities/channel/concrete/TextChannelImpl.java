package net.dv8tion.jda.internal.entities.channel.concrete;

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.DefaultGuildChannelUnion;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.channel.middleman.AbstractStandardGuildMessageChannelImpl;
import net.dv8tion.jda.internal.entities.channel.mixin.concrete.TextChannelMixin;
import net.dv8tion.jda.internal.managers.channel.concrete.TextChannelManagerImpl;
import net.dv8tion.jda.internal.utils.Helpers;

public class TextChannelImpl extends AbstractStandardGuildMessageChannelImpl<TextChannelImpl> implements TextChannel, DefaultGuildChannelUnion, TextChannelMixin<TextChannelImpl> {
   private int slowmode;

   public TextChannelImpl(long id, GuildImpl guild) {
      super(id, guild);
   }

   public boolean isDetached() {
      return false;
   }

   @Nonnull
   public GuildImpl getGuild() {
      return (GuildImpl)super.getGuild();
   }

   @Nonnull
   public ChannelType getType() {
      return ChannelType.TEXT;
   }

   @Nonnull
   public List<Member> getMembers() {
      return (List)this.getGuild().getMembersView().stream().filter((m) -> m.hasPermission(this, new Permission[]{Permission.VIEW_CHANNEL})).collect(Helpers.toUnmodifiableList());
   }

   public int getSlowmode() {
      return this.slowmode;
   }

   @Nonnull
   public TextChannelManager getManager() {
      return new TextChannelManagerImpl(this);
   }

   public TextChannelImpl setSlowmode(int slowmode) {
      this.slowmode = slowmode;
      return this;
   }
}
