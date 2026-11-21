package net.dv8tion.jda.internal.handle;

import java.time.OffsetDateTime;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.InviteImpl;

public class InviteCreateHandler extends SocketHandler {
   public InviteCreateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getUnsignedLong("guild_id");
      if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         Guild realGuild = this.getJDA().getGuildById(guildId);
         if (realGuild == null) {
            EventCache.LOG.debug((String)"Caching INVITE_CREATE for unknown guild with id {}", (Object)guildId);
            this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            return null;
         } else {
            long channelId = content.getUnsignedLong("channel_id");
            GuildChannel realChannel = realGuild.getGuildChannelById(channelId);
            if (realChannel == null) {
               EventCache.LOG.debug((String)"Caching INVITE_CREATE for unknown channel with id {} in guild with id {}", (Object)channelId, (Object)guildId);
               this.getJDA().getEventCache().cache(EventCache.Type.CHANNEL, channelId, this.responseNumber, this.allContent, this::handle);
               return null;
            } else {
               String code = content.getString("code");
               boolean temporary = content.getBoolean("temporary");
               boolean guest = (content.getInt("flags", 0) & 1) == 1;
               int maxAge = content.getInt("max_age", -1);
               int maxUses = content.getInt("max_uses", -1);
               OffsetDateTime creationTime = (OffsetDateTime)content.opt("created_at").map(String::valueOf).map(OffsetDateTime::parse).orElse((Object)null);
               Optional<DataObject> inviterJson = content.optObject("inviter");
               boolean expanded = maxUses != -1;
               User inviter = (User)inviterJson.map((json) -> this.getJDA().getEntityBuilder().createUser(json)).orElse((Object)null);
               InviteImpl.ChannelImpl channel = new InviteImpl.ChannelImpl(realChannel);
               InviteImpl.GuildImpl guild = new InviteImpl.GuildImpl(realGuild);
               Invite.TargetType targetType = Invite.TargetType.fromId(content.getInt("target_type", 0));
               Invite.InviteTarget target;
               switch (targetType) {
                  case STREAM:
                     DataObject targetUserObject = content.getObject("target_user");
                     target = new InviteImpl.InviteTargetImpl(targetType, (Invite.EmbeddedApplication)null, this.getJDA().getEntityBuilder().createUser(targetUserObject));
                     break;
                  case EMBEDDED_APPLICATION:
                     DataObject applicationObject = content.getObject("target_application");
                     Invite.EmbeddedApplication application = new InviteImpl.EmbeddedApplicationImpl(applicationObject.getString("icon", (String)null), applicationObject.getString("name"), applicationObject.getString("description"), applicationObject.getString("summary"), applicationObject.getLong("id"), applicationObject.getInt("max_participants", -1));
                     target = new InviteImpl.InviteTargetImpl(targetType, application, (User)null);
                     break;
                  case NONE:
                     target = null;
                     break;
                  default:
                     target = new InviteImpl.InviteTargetImpl(targetType, (Invite.EmbeddedApplication)null, (User)null);
               }

               Invite invite = new InviteImpl(this.getJDA(), code, expanded, inviter, maxAge, maxUses, temporary, guest, creationTime, 0, channel, guild, (Invite.Group)null, target, Invite.InviteType.GUILD);
               this.getJDA().handleEvent(new GuildInviteCreateEvent(this.getJDA(), this.responseNumber, invite, realChannel));
               return null;
            }
         }
      }
   }
}
