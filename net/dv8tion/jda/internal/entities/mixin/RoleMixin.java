package net.dv8tion.jda.internal.entities.mixin;

import java.time.OffsetDateTime;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.RoleIcon;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.detached.mixin.IDetachableEntityMixin;
import net.dv8tion.jda.internal.utils.Checks;

public interface RoleMixin<T extends RoleMixin<T>> extends Role, IDetachableEntityMixin {
   @Nonnull
   default RoleAction createCopy(@Nonnull Guild guild) {
      Checks.notNull(guild, "Guild");
      return guild.createRole().setColors(this.getColors()).setHoisted(this.isHoisted()).setMentionable(this.isMentionable()).setName(this.getName()).setPermissions(this.getPermissionsRaw()).setIcon(this.getIcon() == null ? null : this.getIcon().getEmoji());
   }

   default int compareTo(@Nonnull Role r) {
      if (this == r) {
         return 0;
      } else if (this.getGuild().getIdLong() != r.getGuild().getIdLong()) {
         throw new IllegalArgumentException("Cannot compare roles that aren't from the same guild!");
      } else if (this.getPositionRaw() != r.getPositionRaw()) {
         return this.getPositionRaw() - r.getPositionRaw();
      } else {
         OffsetDateTime thisTime = this.getTimeCreated();
         OffsetDateTime rTime = r.getTimeCreated();
         return rTime.compareTo(thisTime);
      }
   }

   T setName(String var1);

   T setPrimaryColor(int var1);

   T setSecondaryColor(int var1);

   T setTertiaryColor(int var1);

   T setManaged(boolean var1);

   T setHoisted(boolean var1);

   T setMentionable(boolean var1);

   T setRawPermissions(long var1);

   T setRawPosition(int var1);

   T setTags(DataObject var1);

   T setIcon(RoleIcon var1);
}
