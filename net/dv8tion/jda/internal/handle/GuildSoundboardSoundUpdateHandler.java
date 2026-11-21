package net.dv8tion.jda.internal.handle;

import java.util.Objects;
import net.dv8tion.jda.api.entities.SoundboardSound;
import net.dv8tion.jda.api.events.soundboard.update.SoundboardSoundUpdateEmojiEvent;
import net.dv8tion.jda.api.events.soundboard.update.SoundboardSoundUpdateNameEvent;
import net.dv8tion.jda.api.events.soundboard.update.SoundboardSoundUpdateVolumeEvent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.utils.UnlockHook;
import net.dv8tion.jda.internal.utils.cache.SnowflakeCacheViewImpl;

public class GuildSoundboardSoundUpdateHandler extends SocketHandler {
   public GuildSoundboardSoundUpdateHandler(JDAImpl api) {
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
               long soundId = content.getLong("sound_id");
               SoundboardSound oldSoundboardSound = (SoundboardSound)soundboardSoundsView.get(soundId);
               if (oldSoundboardSound == null) {
                  this.getJDA().getEventCache().cache(EventCache.Type.SOUNDBOARD_SOUND, soundId, this.responseNumber, this.allContent, this::handle);
                  EventCache.LOG.debug((String)"Received a Guild Soundboard Sound Update for SoundboardSound that is not yet cached: {}", (Object)content);
                  return null;
               } else {
                  SoundboardSound soundboardSound = this.api.getEntityBuilder().createSoundboardSound(content);
                  UnlockHook unlockHook = soundboardSoundsView.writeLock();

                  try {
                     soundboardSoundsView.getMap().put(soundboardSound.getIdLong(), soundboardSound);
                  } catch (Throwable var14) {
                     if (unlockHook != null) {
                        try {
                           unlockHook.close();
                        } catch (Throwable var13) {
                           var14.addSuppressed(var13);
                        }
                     }

                     throw var14;
                  }

                  if (unlockHook != null) {
                     unlockHook.close();
                  }

                  if (!Objects.equals(oldSoundboardSound.getName(), soundboardSound.getName())) {
                     this.api.handleEvent(new SoundboardSoundUpdateNameEvent(this.api, this.responseNumber, soundboardSound, oldSoundboardSound.getName()));
                  }

                  if (oldSoundboardSound.getVolume() != soundboardSound.getVolume()) {
                     this.api.handleEvent(new SoundboardSoundUpdateVolumeEvent(this.api, this.responseNumber, soundboardSound, oldSoundboardSound.getVolume()));
                  }

                  if (!Objects.equals(oldSoundboardSound.getEmoji(), soundboardSound.getEmoji())) {
                     this.api.handleEvent(new SoundboardSoundUpdateEmojiEvent(this.api, this.responseNumber, soundboardSound, oldSoundboardSound.getEmoji()));
                  }

                  return null;
               }
            }
         }
      }
   }
}
