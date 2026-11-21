package net.dv8tion.jda.internal.handle;

import gnu.trove.map.TLongObjectMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.sticker.GuildStickerAddedEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerRemovedEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateAvailableEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateNameEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateTagsEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.sticker.GuildStickerImpl;
import net.dv8tion.jda.internal.utils.Helpers;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;
import org.apache.commons.collections4.CollectionUtils;

public class GuildStickersUpdateHandler extends SocketHandler {
   public GuildStickersUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      if (!this.getJDA().isCacheFlagSet(CacheFlag.STICKER)) {
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
               DataArray array = content.getArray("stickers");
               SnowflakeCacheViewImpl<GuildSticker> stickersView = guild.getStickersView();
               EntityBuilder builder = this.api.getEntityBuilder();
               UnlockHook hook = stickersView.writeLock();

               List<GuildSticker> oldStickers;
               List<GuildSticker> newStickers;
               try {
                  TLongObjectMap<GuildSticker> stickersMap = stickersView.getMap();
                  oldStickers = new ArrayList(stickersMap.valueCollection());
                  newStickers = new ArrayList();

                  for(int i = 0; i < array.length(); ++i) {
                     DataObject current = array.getObject(i);
                     long stickerId = current.getLong("id");
                     GuildStickerImpl sticker = (GuildStickerImpl)stickersMap.get(stickerId);
                     GuildStickerImpl oldSticker = null;
                     if (sticker == null) {
                        sticker = (GuildStickerImpl)builder.createRichSticker(current);
                        newStickers.add(sticker);
                     } else {
                        oldStickers.remove(sticker);
                        oldSticker = sticker.copy();
                     }

                     sticker.setName(current.getString("name"));
                     sticker.setAvailable(current.getBoolean("available"));
                     sticker.setDescription(current.getString("description", ""));
                     sticker.setTags(Helpers.setOf(current.getString("tags").split(",\\s*")));
                     stickersMap.put(sticker.getIdLong(), sticker);
                     this.handleReplace(guild, oldSticker, sticker);
                  }

                  for(GuildSticker e : oldStickers) {
                     stickersMap.remove(e.getIdLong());
                  }
               } catch (Throwable var19) {
                  if (hook != null) {
                     try {
                        hook.close();
                     } catch (Throwable var18) {
                        var19.addSuppressed(var18);
                     }
                  }

                  throw var19;
               }

               if (hook != null) {
                  hook.close();
               }

               for(GuildSticker e : oldStickers) {
                  this.getJDA().handleEvent(new GuildStickerRemovedEvent(this.getJDA(), this.responseNumber, guild, e));
               }

               for(GuildSticker e : newStickers) {
                  this.getJDA().handleEvent(new GuildStickerAddedEvent(this.getJDA(), this.responseNumber, guild, e));
               }

               return null;
            }
         }
      }
   }

   private void handleReplace(Guild guild, GuildStickerImpl oldSticker, GuildStickerImpl newSticker) {
      if (oldSticker != null && newSticker != null) {
         if (!Objects.equals(oldSticker.getName(), newSticker.getName())) {
            this.getJDA().handleEvent(new GuildStickerUpdateNameEvent(this.getJDA(), this.responseNumber, guild, newSticker, oldSticker.getName()));
         }

         if (!Objects.equals(oldSticker.getDescription(), newSticker.getDescription())) {
            this.getJDA().handleEvent(new GuildStickerUpdateDescriptionEvent(this.getJDA(), this.responseNumber, guild, newSticker, oldSticker.getDescription()));
         }

         if (oldSticker.isAvailable() != newSticker.isAvailable()) {
            this.getJDA().handleEvent(new GuildStickerUpdateAvailableEvent(this.getJDA(), this.responseNumber, guild, newSticker, oldSticker.isAvailable()));
         }

         if (!CollectionUtils.isEqualCollection(oldSticker.getTags(), newSticker.getTags())) {
            this.getJDA().handleEvent(new GuildStickerUpdateTagsEvent(this.getJDA(), this.responseNumber, guild, newSticker, oldSticker.getTags()));
         }

      }
   }
}
