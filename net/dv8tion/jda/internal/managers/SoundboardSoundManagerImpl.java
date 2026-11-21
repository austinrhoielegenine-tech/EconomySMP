package net.dv8tion.jda.internal.managers;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SoundboardSoundSnowflake;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.managers.SoundboardSoundManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class SoundboardSoundManagerImpl extends ManagerBase<SoundboardSoundManager> implements SoundboardSoundManager {
   private final Guild guild;
   private String name;
   private double volume;
   private Emoji emoji;

   public SoundboardSoundManagerImpl(Guild guild, SoundboardSoundSnowflake soundboardSound) {
      super(guild.getJDA(), Route.SoundboardSounds.MODIFY_GUILD_SOUNDBOARD_SOUND.compile(guild.getId(), soundboardSound.getId()));
      this.guild = guild;
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   @Nonnull
   @CheckReturnValue
   public SoundboardSoundManagerImpl reset(long fields) {
      super.reset(fields);
      if ((fields & 1L) == 1L) {
         this.name = null;
      }

      if ((fields & 2L) == 2L) {
         this.volume = (double)1.0F;
      }

      if ((fields & 4L) == 4L) {
         this.emoji = null;
      }

      return this;
   }

   @Nonnull
   @CheckReturnValue
   public SoundboardSoundManagerImpl reset(@Nonnull long... fields) {
      super.reset(fields);
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public SoundboardSoundManagerImpl reset() {
      super.reset();
      this.name = null;
      this.volume = (double)1.0F;
      this.emoji = null;
      return this;
   }

   @Nonnull
   public SoundboardSoundManagerImpl setName(@Nonnull String name) {
      Checks.notNull(name, "name");
      Checks.check(name.length() >= 2 && name.length() <= 32, "Name must be between 2 and 32 characters");
      this.name = name;
      this.set |= 1L;
      return this;
   }

   @Nonnull
   public SoundboardSoundManagerImpl setVolume(double volume) {
      Checks.check(volume >= (double)0.0F && volume <= (double)1.0F, "Volume must be between 0 and 1");
      this.volume = volume;
      this.set |= 2L;
      return this;
   }

   @Nonnull
   public SoundboardSoundManagerImpl setEmoji(@Nullable Emoji emoji) {
      this.emoji = emoji;
      this.set |= 4L;
      return this;
   }

   protected RequestBody finalizeData() {
      DataObject object = DataObject.empty();
      if (this.shouldUpdate(1L)) {
         object.put("name", this.name);
      }

      if (this.shouldUpdate(2L)) {
         object.put("volume", this.volume);
      }

      if (this.shouldUpdate(4L)) {
         if (this.emoji instanceof CustomEmoji) {
            object.put("emoji_id", ((CustomEmoji)this.emoji).getId());
         } else if (this.emoji != null) {
            object.put("emoji_name", this.emoji.getName());
         } else {
            object.put("emoji_id", (Object)null);
            object.put("emoji_name", (Object)null);
         }
      }

      this.reset();
      return this.getRequestBody(object);
   }
}
