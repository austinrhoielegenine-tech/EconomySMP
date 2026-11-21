package net.dv8tion.jda.internal.entities.channel.mixin;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.unions.ChannelUnion;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.internal.entities.detached.mixin.IDetachableEntityMixin;
import net.dv8tion.jda.internal.requests.RestActionImpl;

public interface ChannelMixin<T extends ChannelMixin<T>> extends Channel, ChannelUnion, IDetachableEntityMixin {
   @Nonnull
   @CheckReturnValue
   default RestAction<Void> delete() {
      this.checkCanAccess();
      Route.CompiledRoute route = Route.Channels.DELETE_CHANNEL.compile(this.getId());
      return new RestActionImpl<Void>(this.getJDA(), route);
   }

   T setName(String var1);

   void checkCanAccess();
}
