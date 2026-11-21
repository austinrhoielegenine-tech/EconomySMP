package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.GuildVoiceStateImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;

public class GuildMemberRemoveHandler extends SocketHandler {
   public GuildMemberRemoveHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long id = content.getLong("guild_id");
      boolean setup = this.getJDA().getGuildSetupController().onRemoveMember(id, content);
      if (setup) {
         return null;
      } else {
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildsView().get(id);
         if (guild == null) {
            return null;
         } else {
            long userId = content.getObject("user").getUnsignedLong("id");
            if (userId == this.getJDA().getSelfUser().getIdLong()) {
               return null;
            } else {
               UnlockHook hook;
               try {
                  User user = this.api.getEntityBuilder().createUser(content.getObject("user"));
                  GuildVoiceStateImpl voiceState = (GuildVoiceStateImpl)guild.getVoiceStateView().getElementById(userId);
                  if (voiceState != null && voiceState.inAudioChannel()) {
                     AudioChannel channel = voiceState.getChannel();
                     voiceState.updateConnectedChannel((AudioChannel)null);
                     this.getJDA().handleEvent(new GuildVoiceUpdateEvent(this.getJDA(), this.responseNumber, voiceState.getMember(), channel));
                  }

                  MemberImpl member = (MemberImpl)guild.getMembersView().remove(userId);
                  SnowflakeCacheViewImpl<User> userView = this.getJDA().getUsersView();
                  hook = userView.writeLock();

                  try {
                     if (user.getMutualGuilds().isEmpty()) {
                        userView.remove(userId);
                        this.getJDA().getEventCache().clear(EventCache.Type.USER, userId);
                     }
                  } catch (Throwable var20) {
                     if (hook != null) {
                        try {
                           hook.close();
                        } catch (Throwable var19) {
                           var20.addSuppressed(var19);
                        }
                     }

                     throw var20;
                  }

                  if (hook != null) {
                     hook.close();
                  }

                  this.getJDA().handleEvent(new GuildMemberRemoveEvent(this.getJDA(), this.responseNumber, guild, user, member));
                  hook = null;
               } finally {
                  guild.onMemberRemove(userId);
               }

               return hook;
            }
         }
      }
   }
}
