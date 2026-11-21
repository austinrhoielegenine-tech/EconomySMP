package net.dv8tion.jda.internal.requests.restaction;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleColors;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class RoleActionImpl extends AuditableRestActionImpl<Role> implements RoleAction {
   protected final Guild guild;
   protected Long permissions;
   protected String name = null;
   protected RoleColors colors = null;
   protected Boolean hoisted = null;
   protected Boolean mentionable = null;
   protected Icon icon = null;
   protected String emoji = null;

   public RoleActionImpl(Guild guild) {
      super(guild.getJDA(), Route.Roles.CREATE_ROLE.compile(guild.getId()));
      this.guild = guild;
   }

   @Nonnull
   public RoleActionImpl setCheck(BooleanSupplier checks) {
      return (RoleActionImpl)super.setCheck(checks);
   }

   @Nonnull
   public RoleActionImpl timeout(long timeout, @Nonnull TimeUnit unit) {
      return (RoleActionImpl)super.timeout(timeout, unit);
   }

   @Nonnull
   public RoleActionImpl deadline(long timestamp) {
      return (RoleActionImpl)super.deadline(timestamp);
   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   @Nonnull
   @CheckReturnValue
   public RoleActionImpl setName(String name) {
      if (name != null) {
         Checks.notEmpty(name, "Name");
         Checks.notLonger(name, 100, "Name");
      }

      this.name = name;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleActionImpl setHoisted(Boolean hoisted) {
      this.hoisted = hoisted;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleActionImpl setMentionable(Boolean mentionable) {
      this.mentionable = mentionable;
      return this;
   }

   @Nonnull
   public RoleAction setGradientColors(int primaryRgb, int secondaryRgb) {
      this.colors = new RoleColors(primaryRgb, secondaryRgb, 536870911);
      return this;
   }

   @Nonnull
   public RoleAction useHolographicStyle() {
      this.colors = RoleColors.DEFAULT_HOLOGRAPHIC;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleActionImpl setColor(Integer rgb) {
      this.colors = new RoleColors(rgb == null ? 536870911 : rgb, 536870911, 536870911);
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleAction setColors(@Nullable RoleColors colors) {
      this.colors = colors;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleActionImpl setPermissions(Long permissions) {
      if (permissions != null) {
         for(Permission p : Permission.getPermissions(permissions)) {
            this.checkPermission(p);
         }
      }

      this.permissions = permissions;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleActionImpl setIcon(Icon icon) {
      this.icon = icon;
      this.emoji = null;
      return this;
   }

   @Nonnull
   @CheckReturnValue
   public RoleActionImpl setIcon(String emoji) {
      this.emoji = emoji;
      this.icon = null;
      return this;
   }

   protected RequestBody finalizeData() {
      DataObject object = DataObject.empty();
      if (this.name != null) {
         object.put("name", this.name);
      }

      if (this.colors != null && !this.colors.isDefault()) {
         object.put("colors", this.colors);
      }

      if (this.permissions != null) {
         object.put("permissions", this.permissions);
      }

      if (this.hoisted != null) {
         object.put("hoist", this.hoisted);
      }

      if (this.mentionable != null) {
         object.put("mentionable", this.mentionable);
      }

      if (this.icon != null) {
         object.put("icon", this.icon.getEncoding());
      }

      if (this.emoji != null) {
         object.put("unicode_emoji", this.emoji);
      }

      return this.getRequestBody(object);
   }

   protected void handleSuccess(Response response, Request<Role> request) {
      request.onSuccess(this.api.getEntityBuilder().createRole((GuildImpl)this.guild, response.getObject(), this.guild.getIdLong()));
   }

   private void checkPermission(Permission permission) {
      if (!this.guild.getSelfMember().hasPermission(new Permission[]{permission})) {
         throw new InsufficientPermissionException(this.guild, permission);
      }
   }
}
