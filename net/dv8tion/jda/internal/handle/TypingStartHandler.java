package net.dv8tion.jda.internal.handle;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;

public class TypingStartHandler extends SocketHandler {
   public TypingStartHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      GuildImpl guild = null;
      if (!content.isNull("guild_id")) {
         long guildId = content.getUnsignedLong("guild_id");
         guild = (GuildImpl)this.getJDA().getGuildById(guildId);
         if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
            return guildId;
         }

         if (guild == null) {
            return null;
         }
      }

      long channelId = content.getLong("channel_id");
      MessageChannel channel = (MessageChannel)this.getJDA().getChannelById(MessageChannel.class, channelId);
      if (channel == null) {
         return null;
      } else {
         long userId = content.getLong("user_id");
         MemberImpl member = null;
         User user;
         if (channel instanceof PrivateChannel) {
            user = ((PrivateChannel)channel).getUser();
         } else {
            user = (User)this.getJDA().getUsersView().get(userId);
         }

         if (!content.isNull("member")) {
            EntityBuilder entityBuilder = this.getJDA().getEntityBuilder();
            member = entityBuilder.createMember(guild, content.getObject("member"));
            entityBuilder.updateMemberCache(member);
            user = member.getUser();
         }

         if (user == null) {
            return null;
         } else {
            OffsetDateTime timestamp = Instant.ofEpochSecond((long)content.getInt("timestamp")).atOffset(ZoneOffset.UTC);
            this.getJDA().handleEvent(new UserTypingEvent(this.getJDA(), this.responseNumber, user, channel, timestamp, member));
            return null;
         }
      }
   }
}
