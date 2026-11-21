package net.dv8tion.jda.internal.handle;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;
import net.dv8tion.jda.internal.entities.channel.concrete.PrivateChannelImpl;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class MessageReactionHandler extends SocketHandler {
   private final boolean add;

   public MessageReactionHandler(JDAImpl api, boolean add) {
      super(api);
      this.add = add;
   }

   protected Long handleInternally(DataObject content) {
      if (!content.isNull("guild_id")) {
         long guildId = content.getLong("guild_id");
         if (this.api.getGuildSetupController().isLocked(guildId)) {
            return guildId;
         }
      }

      DataObject emoji = content.getObject("emoji");
      long userId = content.getLong("user_id");
      long messageId = content.getLong("message_id");
      long channelId = content.getLong("channel_id");
      Long emojiId = emoji.isNull("id") ? null : emoji.getLong("id");
      String emojiName = emoji.getString("name", (String)null);
      if (emojiId == null && emojiName == null) {
         WebSocketClient.LOG.debug((String)"Received a reaction {} with no name nor id. json: {}", (Object)(this.add ? "add" : "remove"), (Object)content);
         return null;
      } else {
         long guildId = content.getUnsignedLong("guild_id", 0L);
         Guild guild = this.api.getGuildById(guildId);
         MemberImpl member = null;
         if (guild != null) {
            member = (MemberImpl)guild.getMemberById(userId);
            Optional<DataObject> memberJson = content.optObject("member");
            if (memberJson.isPresent()) {
               DataObject json = (DataObject)memberJson.get();
               if (member != null && member.hasTimeJoined()) {
                  Stream var10000 = json.getArray("roles").stream(DataArray::getUnsignedLong);
                  Objects.requireNonNull(guild);
                  List<Role> roles = (List)var10000.map(guild::getRoleById).filter(Objects::nonNull).collect(Collectors.toList());
                  this.api.getEntityBuilder().updateMember((GuildImpl)guild, member, json, roles);
               } else {
                  member = this.api.getEntityBuilder().createMember((GuildImpl)guild, json);
               }

               this.api.getEntityBuilder().updateMemberCache(member);
            }

            if (member == null && this.add && guild.isLoaded()) {
               WebSocketClient.LOG.debug((String)"Dropping reaction event for unknown member {}", (Object)content);
               return null;
            }
         }

         User user = this.api.getUserById(userId);
         if (user == null && member != null) {
            user = member.getUser();
         }

         if (user == null && this.add && guild != null) {
            this.api.getEventCache().cache(EventCache.Type.USER, userId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug("Received a reaction for a user that JDA does not currently have cached. UserID: {} ChannelId: {} MessageId: {}", userId, channelId, messageId);
            return null;
         } else {
            MessageChannel channel = (MessageChannel)this.api.getChannelById(MessageChannel.class, channelId);
            if (channel == null) {
               if (guild != null) {
                  GuildChannel actual = guild.getGuildChannelById(channelId);
                  if (actual != null) {
                     WebSocketClient.LOG.debug((String)"Dropping MESSAGE_REACTION event for unexpected channel of type {}", (Object)actual.getType());
                     return null;
                  }
               }

               if (guildId != 0L) {
                  this.api.getEventCache().cache(EventCache.Type.CHANNEL, channelId, this.responseNumber, this.allContent, this::handle);
                  EventCache.LOG.debug("Received a reaction for a channel that JDA does not currently have cached");
                  return null;
               }

               channel = this.getJDA().getEntityBuilder().createPrivateChannel(DataObject.empty().put("id", channelId));
            }

            EmojiUnion rEmoji = EntityBuilder.createEmoji(emoji);
            boolean[] self = new boolean[]{false, false};
            MessageReaction reaction = new MessageReaction(this.api, channel, rEmoji, channelId, messageId, self, (int[])null);
            if (channel.getType() == ChannelType.PRIVATE) {
               this.api.usedPrivateChannel(reaction.getChannel().getIdLong());
               PrivateChannelImpl priv = (PrivateChannelImpl)channel;
               if (priv.getUser() == null && user != null) {
                  priv.setUser(user);
               }
            }

            if (this.add) {
               this.api.handleEvent(new MessageReactionAddEvent(this.api, this.responseNumber, user, member, reaction, userId, content.getUnsignedLong("message_author_id", 0L)));
            } else {
               this.api.handleEvent(new MessageReactionRemoveEvent(this.api, this.responseNumber, user, member, reaction, userId));
            }

            return null;
         }
      }
   }
}
