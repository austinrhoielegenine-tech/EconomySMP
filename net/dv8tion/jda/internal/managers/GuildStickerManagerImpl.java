package net.dv8tion.jda.internal.managers;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.managers.GuildStickerManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.sticker.GuildStickerImpl;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class GuildStickerManagerImpl extends ManagerBase<GuildStickerManager> implements GuildStickerManager {
   private final Guild guild;
   private final long guildId;
   private String name;
   private String description;
   private String tags;

   public GuildStickerManagerImpl(Guild guild, long guildId, StickerSnowflake sticker) {
      super(guild.getJDA(), Route.Stickers.MODIFY_GUILD_STICKER.compile(Long.toUnsignedString(guildId), sticker.getId()));
      this.guild = guild;
      this.guildId = guildId;
      if (isPermissionChecksEnabled()) {
         this.checkPermissions();
      }

   }

   @Nullable
   public Guild getGuild() {
      return this.guild;
   }

   public long getGuildIdLong() {
      return this.guildId;
   }

   @Nonnull
   public GuildStickerManagerImpl reset(long fields) {
      super.reset(fields);
      if ((fields & 1L) == 1L) {
         this.name = null;
      }

      if ((fields & 2L) == 2L) {
         this.description = null;
      }

      if ((fields & 4L) == 4L) {
         this.tags = null;
      }

      return this;
   }

   @Nonnull
   public GuildStickerManagerImpl reset(@Nonnull long... fields) {
      super.reset(fields);
      return this;
   }

   @Nonnull
   public GuildStickerManagerImpl reset() {
      super.reset();
      this.name = null;
      this.description = null;
      this.tags = null;
      return this;
   }

   @Nonnull
   public GuildStickerManager setName(@Nonnull String name) {
      Checks.inRange(name, 2, 30, "Name");
      this.name = name;
      this.set |= 1L;
      return this;
   }

   @Nonnull
   public GuildStickerManager setDescription(@Nonnull String description) {
      Checks.inRange(description, 2, 100, "Description");
      this.description = description;
      this.set |= 2L;
      return this;
   }

   @Nonnull
   public GuildStickerManager setTags(@Nonnull Collection<String> tags) {
      Checks.notEmpty(tags, "Tags");

      for(String tag : tags) {
         Checks.notEmpty(tag, "Tags");
      }

      String csv = String.join(",", tags);
      Checks.notLonger(csv, 200, "List of tags");
      this.tags = csv;
      this.set |= 4L;
      return this;
   }

   protected RequestBody finalizeData() {
      DataObject object = DataObject.empty();
      if (this.shouldUpdate(1L)) {
         object.put("name", this.name);
      }

      if (this.shouldUpdate(2L)) {
         object.put("description", this.description);
      }

      if (this.shouldUpdate(4L)) {
         object.put("tags", this.tags);
      }

      this.reset();
      return this.getRequestBody(object);
   }

   protected boolean checkPermissions() {
      if (this.guild != null) {
         GuildStickerImpl.checkCreateOrManagePermissions(this.guild);
      }

      return super.checkPermissions();
   }
}
