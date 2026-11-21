package net.dv8tion.jda.internal.entities.sticker;

import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.GuildStickerManager;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.managers.GuildStickerManagerImpl;
import net.dv8tion.jda.internal.requests.DeferredRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.utils.EntityString;
import net.dv8tion.jda.internal.utils.Helpers;

public class GuildStickerImpl extends RichStickerImpl implements GuildSticker {
   private final long guildId;
   private final JDA jda;
   private Guild guild;
   private User owner;
   private boolean available;

   public GuildStickerImpl(long id, Sticker.StickerFormat format, String name, Set<String> tags, String description, boolean available, long guildId, JDA jda, User owner) {
      super(id, format, name, tags, description);
      this.available = available;
      this.guildId = guildId;
      this.jda = jda;
      this.guild = jda.getGuildById(guildId);
      this.owner = owner;
   }

   @Nonnull
   public GuildSticker asGuildSticker() {
      return this;
   }

   public boolean isAvailable() {
      return this.available;
   }

   public long getGuildIdLong() {
      return this.guildId;
   }

   @Nullable
   public Guild getGuild() {
      Guild realGuild = this.jda.getGuildById(this.guildId);
      if (realGuild != null) {
         this.guild = realGuild;
      }

      return this.guild;
   }

   @Nullable
   public User getOwner() {
      if (this.owner != null) {
         User realOwner = this.jda.getUserById(this.owner.getIdLong());
         if (realOwner != null) {
            this.owner = realOwner;
         }
      }

      return this.owner;
   }

   @Nonnull
   public CacheRestAction<User> retrieveOwner() {
      Guild guild = this.getGuild();
      if (guild != null) {
         checkCreateOrManagePermissions(guild, "Retrieving sticker owner requires either MANAGE_GUILD_EXPRESSIONS or CREATE_GUILD_EXPRESSIONS permissions");
      }

      return new DeferredRestAction(this.jda, User.class, this::getOwner, () -> {
         Route.CompiledRoute route = Route.Stickers.GET_GUILD_STICKER.compile(this.getGuildId(), this.getId());
         return new RestActionImpl(this.jda, route, (response, request) -> {
            DataObject json = response.getObject();
            return this.owner = (User)json.optObject("user").map((user) -> ((JDAImpl)this.jda).getEntityBuilder().createUser(json.getObject("user"))).orElseThrow(() -> ErrorResponseException.create(ErrorResponse.MISSING_PERMISSIONS, response));
         });
      });
   }

   @Nonnull
   public AuditableRestAction<Void> delete() {
      Guild guild = this.getGuild();
      if (guild != null) {
         checkManagePermissions(guild, this.owner);
      }

      Route.CompiledRoute route = Route.Stickers.DELETE_GUILD_STICKER.compile(this.getGuildId(), this.getId());
      return new AuditableRestActionImpl<Void>(this.jda, route);
   }

   private static void checkManagePermissions(@Nonnull Guild guild, @Nullable UserSnowflake owner) {
      SelfMember selfMember = guild.getSelfMember();
      if (owner != null) {
         if (owner.getIdLong() == selfMember.getIdLong()) {
            checkCreateOrManagePermissions(guild);
         } else if (!selfMember.hasPermission(new Permission[]{Permission.MANAGE_GUILD_EXPRESSIONS})) {
            throw new InsufficientPermissionException(guild, Permission.MANAGE_GUILD_EXPRESSIONS);
         }
      } else {
         checkCreateOrManagePermissions(guild);
      }

   }

   public static void checkCreateOrManagePermissions(@Nonnull Guild guild) {
      checkCreateOrManagePermissions(guild, "Managing a sticker requires either MANAGE_GUILD_EXPRESSIONS or CREATE_GUILD_EXPRESSIONS permissions");
   }

   private static void checkCreateOrManagePermissions(@Nonnull Guild guild, @Nonnull String message) {
      SelfMember selfMember = guild.getSelfMember();
      if (!selfMember.hasPermission(new Permission[]{Permission.MANAGE_GUILD_EXPRESSIONS}) && !selfMember.hasPermission(new Permission[]{Permission.CREATE_GUILD_EXPRESSIONS})) {
         throw new InsufficientPermissionException(guild, Permission.MANAGE_GUILD_EXPRESSIONS, message);
      }
   }

   @Nonnull
   public GuildStickerManager getManager() {
      return new GuildStickerManagerImpl(this.getGuild(), this.getGuildIdLong(), this);
   }

   public GuildStickerImpl setAvailable(boolean available) {
      this.available = available;
      return this;
   }

   public GuildStickerImpl copy() {
      return new GuildStickerImpl(this.id, this.format, this.name, this.tags, this.description, this.available, this.guildId, this.jda, this.owner);
   }

   public String toString() {
      return (new EntityString(this)).setName(this.name).addMetadata("guild", this.getGuildId()).toString();
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.format, this.name, this.getType(), this.tags, this.description, this.available, this.guildId});
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof GuildStickerImpl)) {
         return false;
      } else {
         GuildStickerImpl other = (GuildStickerImpl)obj;
         return this.id == other.id && this.format == other.format && this.getType() == other.getType() && this.available == other.available && this.guildId == other.guildId && Objects.equals(this.name, other.name) && Objects.equals(this.description, other.description) && Helpers.deepEqualsUnordered(this.tags, other.tags);
      }
   }
}
