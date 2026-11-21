package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class GuildSoundboardSoundsUpdateHandler extends SocketHandler {
   private final GuildSoundboardSoundUpdateHandler soundboardSoundUpdateHandler;

   public GuildSoundboardSoundsUpdateHandler(JDAImpl api, GuildSoundboardSoundUpdateHandler soundboardSoundUpdateHandler) {
      super(api);
      this.soundboardSoundUpdateHandler = soundboardSoundUpdateHandler;
   }

   protected Long handleInternally(DataObject content) {
      if (!this.getJDA().isCacheFlagSet(CacheFlag.SOUNDBOARD_SOUNDS)) {
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
               DataArray array = content.getArray("soundboard_sounds");

               for(int i = 0; i < array.length(); ++i) {
                  DataObject payload = array.getObject(i);
                  payload.put("guild_id", guildId);
                  this.soundboardSoundUpdateHandler.handle(this.responseNumber, DataObject.empty().put("t", "GUILD_SOUNDBOARD_SOUND_UPDATE").put("d", payload));
               }

               return null;
            }
         }
      }
   }
}
