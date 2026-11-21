package net.dv8tion.jda.internal.requests.restaction.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.Route;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.Checks;
import okhttp3.RequestBody;

public class RoleOrderActionImpl extends OrderActionImpl<Role, RoleOrderAction> implements RoleOrderAction {
   protected final Guild guild;

   public RoleOrderActionImpl(Guild guild, boolean useAscendingOrder) {
      super(guild.getJDA(), !useAscendingOrder, Route.Guilds.MODIFY_ROLES.compile(guild.getId()));
      this.guild = guild;
      List<Role> roles = guild.getRoles();
      roles = roles.subList(0, roles.size() - 1);
      if (useAscendingOrder) {
         for(int i = roles.size() - 1; i >= 0; --i) {
            this.orderList.add((Role)roles.get(i));
         }
      } else {
         this.orderList.addAll(roles);
      }

   }

   @Nonnull
   public Guild getGuild() {
      return this.guild;
   }

   protected RequestBody finalizeData() {
      Member self = this.guild.getSelfMember();
      boolean isOwner = self.isOwner();
      if (!isOwner) {
         if (self.getRoles().isEmpty()) {
            throw new IllegalStateException("Cannot move roles above your highest role unless you are the guild owner");
         }

         if (!self.hasPermission(new Permission[]{Permission.MANAGE_ROLES})) {
            throw new InsufficientPermissionException(this.guild, Permission.MANAGE_ROLES);
         }
      }

      DataArray array = DataArray.empty();
      List<Role> ordering = new ArrayList(this.orderList);
      if (this.ascendingOrder) {
         Collections.reverse(ordering);
      }

      for(int i = 0; i < ordering.size(); ++i) {
         Role role = (Role)ordering.get(i);
         int initialPos = role.getPosition();
         if (initialPos != i && !isOwner && !self.canInteract(role)) {
            throw new IllegalStateException("Cannot change order: One of the roles could not be moved due to hierarchical power!");
         }

         array.add(DataObject.empty().put("id", role.getId()).put("position", i + 1));
      }

      return this.getRequestBody(array);
   }

   protected void validateInput(Role entity) {
      Checks.check(entity.getGuild().equals(this.guild), "Provided selected role is not from this Guild!");
      Checks.check(this.orderList.contains(entity), "Provided role is not in the list of orderable roles!");
   }
}
