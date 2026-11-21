package net.dv8tion.jda.internal.handle;

import gnu.trove.map.TLongObjectMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateNameEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateRolesEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.emoji.RichCustomEmojiImpl;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;
import org.apache.commons.collections4.CollectionUtils;

public class GuildEmojisUpdateHandler extends SocketHandler {
   public GuildEmojisUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      if (!this.getJDA().isCacheFlagSet(CacheFlag.EMOJI)) {
         return null;
      } else {
         long guildId = content.getLong("guild_id");
         if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
            return guildId;
         } else {
            GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
            if (guild == null) {
               this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
               return null;
            } else {
               DataArray array = content.getArray("emojis");
               SnowflakeCacheViewImpl<RichCustomEmoji> emojiView = guild.getEmojisView();
               UnlockHook hook = emojiView.writeLock();

               List<RichCustomEmoji> oldEmojis;
               List<RichCustomEmoji> newEmojis;
               try {
                  TLongObjectMap<RichCustomEmoji> emojiMap = emojiView.getMap();
                  oldEmojis = new ArrayList(emojiMap.valueCollection());
                  newEmojis = new ArrayList();

                  for(int i = 0; i < array.length(); ++i) {
                     DataObject current = array.getObject(i);
                     long emojiId = current.getLong("id");
                     RichCustomEmojiImpl emoji = (RichCustomEmojiImpl)emojiMap.get(emojiId);
                     RichCustomEmojiImpl oldEmoji = null;
                     if (emoji == null) {
                        emoji = new RichCustomEmojiImpl(emojiId, guild);
                        newEmojis.add(emoji);
                     } else {
                        oldEmojis.remove(emoji);
                        oldEmoji = emoji.copy();
                     }

                     emoji.setName(current.getString("name")).setAnimated(current.getBoolean("animated")).setManaged(current.getBoolean("managed"));
                     DataArray roles = current.getArray("roles");
                     Set<Role> newRoles = emoji.getRoleSet();
                     Set<Role> oldRoles = new HashSet(newRoles);

                     for(int j = 0; j < roles.length(); ++j) {
                        Role role = guild.getRoleById(roles.getString(j));
                        if (role != null) {
                           newRoles.add(role);
                           oldRoles.remove(role);
                        }
                     }

                     for(Role r : oldRoles) {
                        newRoles.remove(r);
                     }

                     emojiMap.put(emoji.getIdLong(), emoji);
                     this.handleReplace(oldEmoji, emoji);
                  }

                  for(RichCustomEmoji e : oldEmojis) {
                     emojiMap.remove(e.getIdLong());
                  }
               } catch (Throwable var23) {
                  if (hook != null) {
                     try {
                        hook.close();
                     } catch (Throwable var22) {
                        var23.addSuppressed(var22);
                     }
                  }

                  throw var23;
               }

               if (hook != null) {
                  hook.close();
               }

               for(RichCustomEmoji e : oldEmojis) {
                  this.getJDA().handleEvent(new EmojiRemovedEvent(this.getJDA(), this.responseNumber, e));
               }

               for(RichCustomEmoji e : newEmojis) {
                  this.getJDA().handleEvent(new EmojiAddedEvent(this.getJDA(), this.responseNumber, e));
               }

               return null;
            }
         }
      }
   }

   private void handleReplace(RichCustomEmoji oldEmoji, RichCustomEmoji newEmoji) {
      if (oldEmoji != null && newEmoji != null) {
         if (!Objects.equals(oldEmoji.getName(), newEmoji.getName())) {
            this.getJDA().handleEvent(new EmojiUpdateNameEvent(this.getJDA(), this.responseNumber, newEmoji, oldEmoji.getName()));
         }

         if (!CollectionUtils.isEqualCollection(oldEmoji.getRoles(), newEmoji.getRoles())) {
            this.getJDA().handleEvent(new EmojiUpdateRolesEvent(this.getJDA(), this.responseNumber, newEmoji, oldEmoji.getRoles()));
         }

      }
   }
}
