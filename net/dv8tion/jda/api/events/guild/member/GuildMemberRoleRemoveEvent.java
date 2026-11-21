package net.dv8tion.jda.api.events.guild.member;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class GuildMemberRoleRemoveEvent extends GenericGuildMemberEvent {
   private final List<Role> removedRoles;

   public GuildMemberRoleRemoveEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull List<Role> removedRoles) {
      super(api, responseNumber, member);
      this.removedRoles = Collections.unmodifiableList(removedRoles);
   }

   @Nonnull
   public List<Role> getRoles() {
      return this.removedRoles;
   }
}
