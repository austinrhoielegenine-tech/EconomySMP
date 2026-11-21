package net.dv8tion.jda.api.entities.channel.concrete;

import java.util.EnumSet;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.StageInstance;
import net.dv8tion.jda.api.entities.channel.attribute.IAgeRestrictedChannel;
import net.dv8tion.jda.api.entities.channel.attribute.ISlowmodeChannel;
import net.dv8tion.jda.api.entities.channel.attribute.IWebhookContainer;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.channel.concrete.StageChannelManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.api.requests.restaction.StageInstanceAction;
import net.dv8tion.jda.internal.requests.restaction.StageInstanceActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

public interface StageChannel extends StandardGuildChannel, GuildMessageChannel, AudioChannel, IWebhookContainer, IAgeRestrictedChannel, ISlowmodeChannel {
   int MAX_USERLIMIT = 10000;

   @Nullable
   StageInstance getStageInstance();

   @Nonnull
   @CheckReturnValue
   default StageInstanceAction createStageInstance(@Nonnull String topic) {
      Checks.checkAttached(this);
      EnumSet<Permission> permissions = this.getGuild().getSelfMember().getPermissions(this);

      for(Permission perm : EnumSet.of(Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_MOVE_OTHERS)) {
         if (!permissions.contains(perm)) {
            throw new InsufficientPermissionException(this, perm, "You must be a stage moderator to create a stage instance! Missing Permission: " + perm);
         }
      }

      return (new StageInstanceActionImpl(this)).setTopic(topic);
   }

   default boolean isModerator(@Nonnull Member member) {
      Checks.notNull(member, "Member");
      return member.hasPermission(this, new Permission[]{Permission.MANAGE_CHANNEL, Permission.VOICE_MUTE_OTHERS, Permission.VOICE_MOVE_OTHERS});
   }

   @Nonnull
   @CheckReturnValue
   ChannelAction<StageChannel> createCopy(@Nonnull Guild var1);

   @Nonnull
   @CheckReturnValue
   default ChannelAction<StageChannel> createCopy() {
      return this.createCopy(this.getGuild());
   }

   @Nonnull
   @CheckReturnValue
   StageChannelManager getManager();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> requestToSpeak();

   @Nonnull
   @CheckReturnValue
   RestAction<Void> cancelRequestToSpeak();
}
