package net.dv8tion.jda.internal.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.managers.CustomEmojiManager;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.emoji.RichCustomEmojiImpl;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class CustomEmojiManagerImpl extends ManagerBase<CustomEmojiManager> implements CustomEmojiManager {
   protected final RichCustomEmojiImpl emoji;
   protected final List<String> roles = new ArrayList();
   protected String name;

   public CustomEmojiManagerImpl(RichCustomEmojiImpl emoji) {
      super(emoji.getJDA(), Route.Emojis.MODIFY_EMOJI.compile(emoji.getGuild().getId(), emoji.getId()));
      this.emoji = emoji;
      if (isPermissionChecksEnabled()) {
         this.checkPermissions();
      }

   }

   @Nonnull
   public RichCustomEmoji getEmoji() {
      return this.emoji;
   }

   @Nonnull
   @CheckReturnValue
   public CustomEmojiManagerImpl reset(long fields) {
      super.reset(fields);
      if ((fields & 2L) == 2L) {
         this.withLock(this.roles, List::clear);
      }

      if ((fields & 1L) == 1L) {
         this.name = null;
      }

      return this;
   }

   @Nonnull
   @CheckReturnValue
   public CustomEmojiManagerImpl reset(@Nonnull long... fields) {
      super.reset(fields);
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public CustomEmojiManagerImpl reset() {
      super.reset();
      this.withLock(this.roles, List::clear);
      this.name = null;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public CustomEmojiManagerImpl setName(@Nonnull String name) {
      Checks.notBlank(name, "Name");
      name = name.trim();
      Checks.inRange(name, 2, 32, "Name");
      this.name = name;
      this.set |= 1L;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public CustomEmojiManagerImpl setRoles(Set<Role> roles) {
      if (roles == null) {
         this.withLock(this.roles, List::clear);
      } else {
         Checks.notNull(roles, "Roles");
         roles.forEach((role) -> {
            Checks.notNull(role, "Roles");
            Checks.check(role.getGuild().equals(this.getGuild()), "Roles must all be from the same guild");
         });
         this.withLock(this.roles, (list) -> {
            list.clear();
            Stream var10000 = roles.stream().map(ISnowflake::getId);
            Objects.requireNonNull(list);
            var10000.forEach(list::add);
         });
      }

      this.set |= 2L;
      return this;
   }

   protected RequestBody finalizeData() {
      DataObject object = DataObject.empty();
      if (this.shouldUpdate(1L)) {
         object.put("name", this.name);
      }

      this.withLock(this.roles, (list) -> {
         if (this.shouldUpdate(2L)) {
            object.put("roles", DataArray.fromCollection(list));
         }

      });
      this.reset();
      return this.getRequestBody(object);
   }

   protected boolean checkPermissions() {
      this.emoji.checkManagePermissions();
      return super.checkPermissions();
   }
}
