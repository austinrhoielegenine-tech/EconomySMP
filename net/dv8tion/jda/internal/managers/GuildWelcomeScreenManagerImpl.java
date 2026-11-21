package net.dv8tion.jda.internal.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildWelcomeScreen;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.GuildWelcomeScreenManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class GuildWelcomeScreenManagerImpl extends ManagerBase<GuildWelcomeScreenManager> implements GuildWelcomeScreenManager {
   private final Guild guild;
   protected boolean enabled;
   protected String description;
   protected final List<GuildWelcomeScreen.Channel> channels = new ArrayList(5);

   public GuildWelcomeScreenManagerImpl(Guild guild) {
      super(guild.getJDA(), Route.Guilds.MODIFY_WELCOME_SCREEN.compile(guild.getId()));
      this.guild = guild;
      if (isPermissionChecksEnabled()) {
         this.checkPermissions();
      }

   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   @Nonnull
   @CheckReturnValue
   public GuildWelcomeScreenManagerImpl reset(long fields) {
      super.reset(fields);
      if ((fields & 1L) == 1L) {
         this.enabled = false;
      }

      if ((fields & 2L) == 2L) {
         this.description = null;
      }

      if ((fields & 4L) == 4L) {
         this.channels.clear();
      }

      return this;
   }

   @Nonnull
   @CheckReturnValue
   public GuildWelcomeScreenManagerImpl reset(@Nonnull long... fields) {
      super.reset(fields);
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public GuildWelcomeScreenManagerImpl reset() {
      super.reset(7L);
      return this;
   }

   @Nonnull
   public GuildWelcomeScreenManager setEnabled(boolean enabled) {
      this.enabled = enabled;
      this.set |= 1L;
      return this;
   }

   @Nonnull
   public GuildWelcomeScreenManager setDescription(@Nullable String description) {
      if (description != null) {
         Checks.notLonger(description, 140, "Description");
      }

      this.description = description;
      this.set |= 2L;
      return this;
   }

   @Nonnull
   public List<GuildWelcomeScreen.Channel> getWelcomeChannels() {
      return Collections.unmodifiableList(this.channels);
   }

   @Nonnull
   public GuildWelcomeScreenManager clearWelcomeChannels() {
      this.withLock(this.channels, List::clear);
      this.set |= 4L;
      return this;
   }

   @Nonnull
   public GuildWelcomeScreenManager setWelcomeChannels(@Nonnull Collection<? extends GuildWelcomeScreen.Channel> channels) {
      Checks.noneNull(channels, "Welcome channels");
      Checks.check(channels.size() <= 5, "Cannot have more than %d welcome channels", (int)5);
      this.withLock(this.channels, (c) -> {
         c.clear();
         c.addAll(channels);
      });
      this.set |= 4L;
      return this;
   }

   protected RequestBody finalizeData() {
      DataObject object = DataObject.empty();
      if (this.shouldUpdate(1L)) {
         object.put("enabled", this.enabled);
      }

      if (this.shouldUpdate(2L)) {
         object.put("description", this.description);
      }

      this.withLock(this.channels, (list) -> {
         if (this.shouldUpdate(4L)) {
            object.put("welcome_channels", DataArray.fromCollection(list));
         }

      });
      this.reset();
      return this.getRequestBody(object);
   }

   protected boolean checkPermissions() {
      if (!this.getGuild().getSelfMember().hasPermission(new Permission[]{Permission.MANAGE_SERVER})) {
         throw new InsufficientPermissionException(this.getGuild(), Permission.MANAGE_SERVER);
      } else {
         return super.checkPermissions();
      }
   }
}
