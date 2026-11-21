package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.WebSocketClient;

public class MessageReactionClearEmojiHandler extends SocketHandler {
   public MessageReactionClearEmojiHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getUnsignedLong("guild_id");
      if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         Guild guild = this.getJDA().getGuildById(guildId);
         if (guild == null) {
            EventCache.LOG.debug((String)"Caching MESSAGE_REACTION_REMOVE_EMOJI event for unknown guild {}", (Object)guildId);
            this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            return null;
         } else {
            long channelId = content.getUnsignedLong("channel_id");
            GuildMessageChannel channel = (GuildMessageChannel)guild.getChannelById(GuildMessageChannel.class, channelId);
            if (channel == null) {
               GuildChannel actual = guild.getGuildChannelById(channelId);
               if (actual != null) {
                  WebSocketClient.LOG.debug((String)"Dropping MESSAGE_REACTION_REMOVE_EMOJI for unexpected channel of type {}", (Object)actual.getType());
                  return null;
               } else {
                  EventCache.LOG.debug((String)"Caching MESSAGE_REACTION_REMOVE_EMOJI event for unknown channel {}", (Object)channelId);
                  this.getJDA().getEventCache().cache(EventCache.Type.CHANNEL, channelId, this.responseNumber, this.allContent, this::handle);
                  return null;
               }
            } else {
               long messageId = content.getUnsignedLong("message_id");
               DataObject emoji = content.getObject("emoji");
               EmojiUnion reactionEmoji = EntityBuilder.createEmoji(emoji);
               boolean[] self = new boolean[]{false, false};
               MessageReaction reaction = new MessageReaction(this.api, channel, reactionEmoji, channelId, messageId, self, (int[])null);
               this.getJDA().handleEvent(new MessageReactionRemoveEmojiEvent(this.getJDA(), this.responseNumber, messageId, channel, reaction));
               return null;
            }
         }
      }
   }
}
