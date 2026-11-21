package net.dv8tion.jda.internal.handle;

import java.util.Objects;
import java.util.Optional;
import net.dv8tion.jda.api.entities.RoleColors;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorsEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateHoistedEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateIconEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateMentionableEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePositionEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.AbstractEntityBuilder;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.RoleImpl;

public class GuildRoleUpdateHandler extends SocketHandler {
   public GuildRoleUpdateHandler(JDAImpl api) {
      super(api);
   }

   protected Long handleInternally(DataObject content) {
      long guildId = content.getLong("guild_id");
      if (this.getJDA().getGuildSetupController().isLocked(guildId)) {
         return guildId;
      } else {
         DataObject rolejson = content.getObject("role");
         GuildImpl guild = (GuildImpl)this.getJDA().getGuildById(guildId);
         if (guild == null) {
            this.getJDA().getEventCache().cache(EventCache.Type.GUILD, guildId, this.responseNumber, this.allContent, this::handle);
            EventCache.LOG.debug((String)"Received a Role Update for a Guild that is not yet cached: {}", (Object)content);
            return null;
         } else {
            long roleId = rolejson.getLong("id");
            RoleImpl role = (RoleImpl)guild.getRolesView().get(roleId);
            if (role == null) {
               this.getJDA().getEventCache().cache(EventCache.Type.ROLE, roleId, this.responseNumber, this.allContent, this::handle);
               EventCache.LOG.debug((String)"Received a Role Update for Role that is not yet cached: {}", (Object)content);
               return null;
            } else {
               String name = rolejson.getString("name");
               RoleColors colors = AbstractEntityBuilder.createRoleColors(rolejson.getObject("colors"));
               int position = rolejson.getInt("position");
               long permissions = rolejson.getLong("permissions");
               boolean hoisted = rolejson.getBoolean("hoist");
               boolean mentionable = rolejson.getBoolean("mentionable");
               String iconId = rolejson.getString("icon", (String)null);
               String emoji = rolejson.getString("unicode_emoji", (String)null);
               Optional var10000 = rolejson.optObject("tags");
               Objects.requireNonNull(role);
               var10000.ifPresent(role::setTags);
               if (!Objects.equals(name, role.getName())) {
                  String oldName = role.getName();
                  role.setName(name);
                  this.getJDA().handleEvent(new RoleUpdateNameEvent(this.getJDA(), this.responseNumber, role, oldName));
               }

               if (!colors.equals(role.getColors())) {
                  RoleColors oldColors = role.getColors();
                  role.setPrimaryColor(colors.getPrimaryRaw());
                  role.setSecondaryColor(colors.getSecondaryRaw());
                  role.setTertiaryColor(colors.getTertiaryRaw());
                  this.getJDA().handleEvent(new RoleUpdateColorsEvent(this.getJDA(), this.responseNumber, role, oldColors));
                  if (oldColors.getPrimaryRaw() != colors.getPrimaryRaw()) {
                     RoleUpdateColorEvent event = new RoleUpdateColorEvent(this.getJDA(), this.responseNumber, role, oldColors.getPrimaryRaw());
                     this.getJDA().handleEvent(event);
                  }
               }

               if (position != role.getPositionRaw()) {
                  int oldPosition = role.getPosition();
                  int oldPositionRaw = role.getPositionRaw();
                  role.setRawPosition(position);
                  this.getJDA().handleEvent(new RoleUpdatePositionEvent(this.getJDA(), this.responseNumber, role, oldPosition, oldPositionRaw));
               }

               if (permissions != role.getPermissionsRaw()) {
                  long oldPermissionsRaw = role.getPermissionsRaw();
                  role.setRawPermissions(permissions);
                  this.getJDA().handleEvent(new RoleUpdatePermissionsEvent(this.getJDA(), this.responseNumber, role, oldPermissionsRaw));
               }

               if (hoisted != role.isHoisted()) {
                  boolean wasHoisted = role.isHoisted();
                  role.setHoisted(hoisted);
                  this.getJDA().handleEvent(new RoleUpdateHoistedEvent(this.getJDA(), this.responseNumber, role, wasHoisted));
               }

               if (mentionable != role.isMentionable()) {
                  boolean wasMentionable = role.isMentionable();
                  role.setMentionable(mentionable);
                  this.getJDA().handleEvent(new RoleUpdateMentionableEvent(this.getJDA(), this.responseNumber, role, wasMentionable));
               }

               RoleIcon oldIcon = role.getIcon();
               RoleIcon newIcon = iconId == null && emoji == null ? null : new RoleIcon(iconId, emoji, roleId);
               if (!Objects.equals(oldIcon, newIcon)) {
                  role.setIcon(newIcon);
                  this.getJDA().handleEvent(new RoleUpdateIconEvent(this.getJDA(), this.responseNumber, role, oldIcon));
               }

               return null;
            }
         }
      }
   }
}
