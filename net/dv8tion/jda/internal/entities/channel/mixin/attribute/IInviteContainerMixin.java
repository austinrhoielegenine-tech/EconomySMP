package net.dv8tion.jda.internal.entities.channel.mixin.attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.attribute.IInviteContainer;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.InviteAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.channel.mixin.middleman.GuildChannelMixin;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.InviteActionImpl;

public interface IInviteContainerMixin<T extends IInviteContainerMixin<T>> extends IInviteContainer, GuildChannelMixin<T> {
   @Nonnull
   default InviteAction createInvite() {
      this.checkAttached();
      this.checkPermission(Permission.CREATE_INSTANT_INVITE);
      return new InviteActionImpl(this.getJDA(), this.getId());
   }

   @Nonnull
   default RestAction<List<Invite>> retrieveInvites() {
      this.checkAttached();
      this.checkPermission(Permission.MANAGE_CHANNEL);
      Route.CompiledRoute route = Route.Invites.GET_CHANNEL_INVITES.compile(this.getId());
      JDAImpl jda = (JDAImpl)this.getJDA();
      return new RestActionImpl<List<Invite>>(jda, route, (response, request) -> {
         EntityBuilder entityBuilder = jda.getEntityBuilder();
         DataArray array = response.getArray();
         List<Invite> invites = new ArrayList(array.length());

         for(int i = 0; i < array.length(); ++i) {
            invites.add(entityBuilder.createInvite(array.getObject(i)));
         }

         return Collections.unmodifiableList(invites);
      });
   }
}
