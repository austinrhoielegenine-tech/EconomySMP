package net.dv8tion.jda.api.events.guild.member;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class GuildMemberRoleAddEvent extends GenericGuildMemberEvent {
   private final List<Role> addedRoles;

   public GuildMemberRoleAddEvent(@Nonnull JDA api, long responseNumber, @Nonnull Member member, @Nonnull List<Role> addedRoles) {
      super(api, responseNumber, member);
      this.addedRoles = Collections.unmodifiableList(addedRoles);
   }

   @Nonnull
   public List<Role> getRoles() {
      return this.addedRoles;
   }
}
