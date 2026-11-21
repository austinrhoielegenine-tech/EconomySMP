package net.dv8tion.jda.internal.entities.emoji;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.ApplicationEmoji;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.managers.CustomEmojiManager;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.managers.CustomEmojiManagerImpl;
import net.dv8tion.jda.internal.requests.DeferredRestAction;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl;
import net.dv8tion.jda.internal.utils.EntityString;

public class RichCustomEmojiImpl implements RichCustomEmoji, EmojiUnion {
   private final long id;
   private final JDAImpl api;
   private final Set<Role> roles;
   private GuildImpl guild;
   private boolean managed = false;
   private boolean available = true;
   private boolean animated = false;
   private String name;
   private User owner;

   public RichCustomEmojiImpl(long id, GuildImpl guild) {
      this.id = id;
      this.api = guild.getJDA();
      this.guild = guild;
      this.roles = ConcurrentHashMap.newKeySet();
   }

   @Nonnull
   public Emoji.Type getType() {
      return Emoji.Type.CUSTOM;
   }

   @Nonnull
   public String getAsReactionCode() {
      return this.name + ":" + this.id;
   }

   @Nonnull
   public DataObject toData() {
      return DataObject.empty().put("name", this.name).put("animated", this.animated).put("id", this.id);
   }

   @Nonnull
   public GuildImpl getGuild() {
      GuildImpl realGuild = (GuildImpl)this.api.getGuildById(this.guild.getIdLong());
      if (realGuild != null) {
         this.guild = realGuild;
      }

      return this.guild;
   }

   @Nonnull
   public List<Role> getRoles() {
      return Collections.unmodifiableList(new ArrayList(this.roles));
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public boolean isManaged() {
      return this.managed;
   }

   public boolean isAvailable() {
      return this.available;
   }

   public long getIdLong() {
      return this.id;
   }

   @Nonnull
   public JDAImpl getJDA() {
      return this.api;
   }

   public User getOwner() {
      return this.owner;
   }

   @Nonnull
   public CacheRestAction<User> retrieveOwner() {
      GuildImpl guild = this.getGuild();
      if (!guild.getSelfMember().hasPermission(new Permission[]{Permission.MANAGE_GUILD_EXPRESSIONS}) && !guild.getSelfMember().hasPermission(new Permission[]{Permission.CREATE_GUILD_EXPRESSIONS})) {
         throw new InsufficientPermissionException(guild, Permission.MANAGE_GUILD_EXPRESSIONS, "Cannot retrieve owner without either MANAGE_GUILD_EXPRESSIONS or CREATE_GUILD_EXPRESSIONS permissions");
      } else {
         return new DeferredRestAction(this.api, User.class, this::getOwner, () -> {
            Route.CompiledRoute route = Route.Emojis.GET_EMOJI.compile(guild.getId(), this.getId());
            return new RestActionImpl(this.api, route, (response, request) -> {
               DataObject data = response.getObject();
               if (data.isNull("user")) {
                  throw ErrorResponseException.create(ErrorResponse.MISSING_PERMISSIONS, response);
               } else {
                  DataObject user = data.getObject("user");
                  return this.owner = this.api.getEntityBuilder().createUser(user);
               }
            });
         });
      }
   }

   @Nonnull
   public CustomEmojiManager getManager() {
      return new CustomEmojiManagerImpl(this);
   }

   public boolean isAnimated() {
      return this.animated;
   }

   @Nonnull
   public AuditableRestAction<Void> delete() {
      if (this.managed) {
         throw new UnsupportedOperationException("You cannot delete a managed emoji!");
      } else {
         this.checkManagePermissions();
         Route.CompiledRoute route = Route.Emojis.DELETE_EMOJI.compile(this.getGuild().getId(), this.getId());
         return new AuditableRestActionImpl<Void>(this.getJDA(), route);
      }
   }

   public void checkManagePermissions() {
      SelfMember selfMember = this.getGuild().getSelfMember();
      if (this.owner != null) {
         if (this.owner.getIdLong() == selfMember.getIdLong()) {
            this.checkCreateOrManagePermissions();
         } else if (!selfMember.hasPermission(new Permission[]{Permission.MANAGE_GUILD_EXPRESSIONS})) {
            throw new InsufficientPermissionException(this.getGuild(), Permission.MANAGE_GUILD_EXPRESSIONS);
         }
      } else {
         this.checkCreateOrManagePermissions();
      }

   }

   private void checkCreateOrManagePermissions() {
      SelfMember selfMember = this.getGuild().getSelfMember();
      if (!selfMember.hasPermission(new Permission[]{Permission.MANAGE_GUILD_EXPRESSIONS}) && !selfMember.hasPermission(new Permission[]{Permission.CREATE_GUILD_EXPRESSIONS})) {
         throw new InsufficientPermissionException(this.guild, Permission.MANAGE_GUILD_EXPRESSIONS, "Managing a custom emoji requires either MANAGE_GUILD_EXPRESSIONS or CREATE_GUILD_EXPRESSIONS permissions");
      }
   }

   public RichCustomEmojiImpl setName(String name) {
      this.name = name;
      return this;
   }

   public RichCustomEmojiImpl setAnimated(boolean animated) {
      this.animated = animated;
      return this;
   }

   public RichCustomEmojiImpl setManaged(boolean val) {
      this.managed = val;
      return this;
   }

   public RichCustomEmojiImpl setAvailable(boolean available) {
      this.available = available;
      return this;
   }

   public RichCustomEmojiImpl setOwner(User user) {
      this.owner = user;
      return this;
   }

   public Set<Role> getRoleSet() {
      return this.roles;
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof CustomEmoji)) {
         return false;
      } else {
         CustomEmoji other = (CustomEmoji)obj;
         return this.id == other.getIdLong();
      }
   }

   public int hashCode() {
      return Long.hashCode(this.id);
   }

   public String toString() {
      return (new EntityString(this)).setName(this.name).toString();
   }

   public RichCustomEmojiImpl copy() {
      RichCustomEmojiImpl copy = (new RichCustomEmojiImpl(this.id, this.getGuild())).setOwner(this.owner).setManaged(this.managed).setAnimated(this.animated).setName(this.name);
      copy.roles.addAll(this.roles);
      return copy;
   }

   @Nonnull
   public UnicodeEmoji asUnicode() {
      throw new IllegalStateException("Cannot convert CustomEmoji to UnicodeEmoji!");
   }

   @Nonnull
   public CustomEmoji asCustom() {
      return this;
   }

   @Nonnull
   public RichCustomEmoji asRich() {
      return this;
   }

   @Nonnull
   public ApplicationEmoji asApplication() {
      throw new IllegalStateException("Cannot convert RichCustomEmoji to ApplicationEmoji!");
   }
}
