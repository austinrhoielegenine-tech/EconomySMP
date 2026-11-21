package net.dv8tion.jda.api.entities.channel.attribute;

import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.InviteAction;

public interface IInviteContainer extends GuildChannel {
   @Nonnull
   @CheckReturnValue
   InviteAction createInvite();

   @Nonnull
   @CheckReturnValue
   RestAction<List<Invite>> retrieveInvites();
}
