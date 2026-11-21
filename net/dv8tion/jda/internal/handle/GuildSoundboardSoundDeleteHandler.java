package net.dv8tion.jda.internal.handle;

import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.events.soundboard.SoundboardSoundDeleteEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;

public class GuildSoundboardSoundDeleteHandler extends SocketHandler {
   public GuildSoundboardSoundDeleteHandler(JDAImpl api) {
      super(api);
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
               SnowflakeCacheViewImpl<SoundboardSound> soundboardSoundsView = guild.getSoundboardSoundsView();
               UnlockHook unlockHook = soundboardSoundsView.writeLock();

               SoundboardSound removedSound;
               try {
                  removedSound = (SoundboardSound)soundboardSoundsView.getMap().remove(content.getLong("sound_id"));
               } catch (Throwable var11) {
                  if (unlockHook != null) {
                     try {
                        unlockHook.close();
                     } catch (Throwable var10) {
                        var11.addSuppressed(var10);
                     }
                  }

                  throw var11;
               }

               if (unlockHook != null) {
                  unlockHook.close();
               }

               if (removedSound != null) {
                  this.api.handleEvent(new SoundboardSoundDeleteEvent(this.api, this.responseNumber, removedSound));
               }

               return null;
            }
         }
      }
   }
}
